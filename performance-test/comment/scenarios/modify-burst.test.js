// performance-test/comment/scenarios/modify-burst.test.js
import http from 'k6/http';
import {check, sleep} from 'k6';

/**
 * 한 번 만든 댓글에 연속 다회 수정(예: 3~5회) 시나리오
 */

// ==================== 공통 설정 ====================
const BASE_URL            = __ENV.BASE_URL            || 'http://localhost:8080';
const RUN_SCENARIO        = __ENV.SCENARIO            || 'smoke';
const PROJECT_ID          = __ENV.PROJECT_ID          || 1;
const ACCESS_TOKEN        = __ENV.ACCESS_TOKEN        || 'paste-access-token';

const MODIFY_TIMES        = parseInt(__ENV.MODIFY_TIMES || '3', 10);          // 연속 수정 횟수
const PAYLOAD_SIZE        = parseInt(__ENV.PAYLOAD_SIZE || '64', 10);         // 원하는 본문 크기(기본 64B로 안전하게)
const MAX_CONTENT_LEN     = parseInt(__ENV.MAX_CONTENT_LEN || '220', 10);     // 서버 유효성 한계 추정치(접두어 포함 총 길이) - 필요시 조정
const MODIFY_INTERVAL_MS  = parseInt(__ENV.MODIFY_INTERVAL_MS || '100', 10);  // 수정 간 간격
const POST_WAIT_MS        = parseInt(__ENV.POST_WAIT_MS || '120', 10);        // 업로드 후 첫 수정 전 대기
const STRICT_200          = (__ENV.STRICT_200 || '0') === '1';                // true면 200만 성공
const RETRIES             = parseInt(__ENV.RETRIES || '2', 10);               // 재시도 횟수
const RETRY_BASE_MS       = parseInt(__ENV.RETRY_BASE_MS || '40', 10);        // 재시도 기본 대기(ms)
const DEBUG               = (__ENV.DEBUG || '0') === '1';

// ==================== k6 options ====================
export let options = {
    scenarios: {
        smoke:   {
            executor: 'constant-vus',
            vus: 5,
            duration: '30s',
            exec: 'smoke'
        },
        load:    {
            executor: 'ramping-vus',
            startVUs: 10,
            exec: 'load',
            stages: [
                { duration: '2m', target: 120 },
                { duration: '6m', target: 120 },
                { duration: '2m', target: 0 }
            ]
        },
        stress:  {
            executor: 'ramping-vus',
            startVUs: 100,
            exec: 'stress',
            stages: [
                { duration: '3m', target: 300 },
                { duration: '4m', target: 600 },
                { duration: '4m', target: 1200 },
                { duration: '3m', target: 0 }
            ]
        },
        soak:    {
            executor: 'constant-vus',
            vus: 200,
            duration: '1h',
            exec: 'soak'
        },
        spike:   {
            executor: 'ramping-vus',
            startVUs: 40, exec: 'spike',
            stages: [
                { duration: '15s', target: 1500 },
                { duration: '2m', target: 1500 },
                { duration: '1m', target: 0 }
            ]
        },
        capacity:{
            executor: 'ramping-arrival-rate',
            startRate: 80,
            timeUnit: '1s',
            preAllocatedVUs: 300,
            maxVUs: 3000,
            exec: 'capacity',
            stages: [
                { target: 400, duration: '2m' },
                { target: 800, duration: '2m' },
                { target: 0, duration: '2m' }
            ]
        },
    },
    thresholds: {
        'http_req_failed{scenario:smoke,endpoint:modify}':    ['rate<0.01'],
        'http_req_duration{scenario:smoke,endpoint:modify}':  ['p(95)<900'],

        'http_req_failed{scenario:load,endpoint:modify}':     ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:modify}':   ['p(95)<1100'],

        'http_req_failed{scenario:stress,endpoint:modify}':   ['rate<0.05'],
        'http_req_duration{scenario:stress,endpoint:modify}': ['p(99)<2200'],

        'http_req_failed{scenario:soak,endpoint:modify}':     ['rate<0.02'],
        'http_req_duration{scenario:soak,endpoint:modify}':   ['avg<1400'],

        'http_req_failed{scenario:spike,endpoint:modify}':    ['rate<0.05'],
        'http_req_duration{scenario:spike,endpoint:modify}':  ['p(99)<3000'],

        'http_req_failed{scenario:capacity,endpoint:modify}': ['rate<0.05'],
        'http_req_duration{scenario:capacity,endpoint:modify}':['p(95)<3200'],
    },
};

// 실행할 시나리오만 남기기
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

// ==================== 유틸 ====================
function headers(tags = {}) {
    return {
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'Authorization': `Bearer ${ACCESS_TOKEN}`,
            'Cache-Control': 'no-store, no-cache, must-revalidate',
            'Pragma': 'no-cache',
            'Expires': '0',
        },
        tags,
    };
}
function safeJson(t) { try { return JSON.parse(t); } catch { return null; } }
function jitter(ms) { return Math.max(0, ms + Math.floor((Math.random() - 0.5) * ms)); }

// content를 안전 길이로 생성(접두어 포함 총 길이 <= MAX_CONTENT_LEN)
function buildContent(prefix, wantBytes) {
    // 유니코드/이모지 고려 없이 단순 반복: 서버가 영문/한글 혼합 검증이면 여유를 두기 위해 0.9 스케일
    const base = 'x'.repeat(Math.max(1, Math.floor(wantBytes * 0.9)));
    let content = `${prefix} ${Date.now()} ${Math.random()} ${base}`;
    if (content.length > MAX_CONTENT_LEN) {
        content = content.slice(0, MAX_CONTENT_LEN);
    }
    return content;
}

function isRetryable(status) {
    return status === 409 || status === 429 || status === 500 || status === 502 || status === 503 || status === 504;
}

function putWithRetry(url, body, tagObj) {
    let attempt = 0;
    while (true) {
        const res = http.put(url, body, headers(tagObj));
        if (DEBUG) console.log(`PUT attempt#${attempt+1}`, res.status, (res.body || '').slice(0, 180));

        const ok = STRICT_200 ? (res.status === 200) : (res.status >= 200 && res.status < 300);
        if (ok) return res;

        if (attempt >= RETRIES || !isRetryable(res.status)) return res;

        const waitMs = RETRY_BASE_MS * Math.pow(2, attempt);
        sleep(waitMs / 1000);
        attempt++;
    }
}

// ==================== 시나리오 엔트리포인트 ====================
function scenarioExec(sleepTime = 0) {
    // 1) 업로드
    const postUrl = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments?nocache=${Math.random()}`;
    const postRes = http.post(postUrl, JSON.stringify({ content: buildContent('burst 대상', 32) }), headers({ endpoint: 'prepare-upload' }));
    if (DEBUG) console.log('UPLOAD', postRes.status, (postRes.body || '').slice(0, 180));
    const ok201 = check(postRes, { 'upload 201': (r) => r.status === 201 });
    if (!ok201) return;

    const j  = safeJson(postRes.body);
    const id = j?.data?.commentId ?? j?.data?.id ?? j?.data?.comment?.id ?? null;
    check({ id }, { 'got commentId': (v) => v.id != null });
    if (id == null) return;

    // 업로드 직후 약간 대기(트랜잭션/인덱스 반영)
    if (POST_WAIT_MS > 0) sleep(POST_WAIT_MS / 1000);

    // 2) 연속 수정
    const putUrl = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments/${id}?nocache=${Math.random()}`;
    for (let i = 0; i < MODIFY_TIMES; i++) {
        const content = buildContent(`연속수정 #${i + 1}`, PAYLOAD_SIZE);
        const res = putWithRetry(putUrl, JSON.stringify({ content }), { endpoint: 'modify' });

        const ok = STRICT_200 ? (res.status === 200) : (res.status >= 200 && res.status < 300);
        check(res, { [`modify ${i + 1}/${MODIFY_TIMES} ` + (STRICT_200 ? '200' : '2xx')]: () => ok });

        const wait = jitter(MODIFY_INTERVAL_MS);
        if (wait > 0) sleep(wait / 1000);
    }

    // 3) 정리
    const delUrl = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments/${id}?nocache=${Math.random()}`;
    const delRes = http.del(delUrl, null, headers({ endpoint: 'cleanup-delete' }));
    if (DEBUG) console.log('DELETE', delRes.status, (delRes.body || '').slice(0, 180));
    check(delRes, { 'cleanup delete 200': (r) => r.status === 200 });

    if (sleepTime > 0) sleep(sleepTime);
}

export function smoke()    { scenarioExec(1);   }
export function load()     { scenarioExec(0.5); }
export function stress()   { scenarioExec();    }
export function soak()     { scenarioExec(1);   }
export function spike()    { scenarioExec();    }
export function capacity() { scenarioExec();    }

// ============ 실행 예시 ============
// 안전 기본: 3회 수정, 64B, 100ms 간격, 2xx 허용
// k6 run performance-test/comment/scenarios/modify-burst.test.js \
//   -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e PROJECT_ID=1 \
//   -e ACCESS_TOKEN="발급토큰"
//
// 타이트: 4회, 1KB, 50ms 간격, 200만 허용, 재시도 2, 디버그 ON, 길이캡 220자
// k6 run performance-test/comment/scenarios/modify-burst.test.js \
//   -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e PROJECT_ID=1 \
//   -e ACCESS_TOKEN="발급토큰" -e MODIFY_TIMES=4 -e PAYLOAD_SIZE=1024 \
//   -e MODIFY_INTERVAL_MS=50 -e STRICT_200=1 -e RETRIES=2 -e DEBUG=1 -e MAX_CONTENT_LEN=220
