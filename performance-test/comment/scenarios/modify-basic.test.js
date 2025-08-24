import http from 'k6/http';
import {check, sleep} from 'k6';

/**
 * 매 루프 생성 → 수정(측정) → 삭제(정리)
 */

// ====================
// 공통 설정
// ====================
const BASE_URL     = __ENV.BASE_URL     || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO     || 'smoke';
const PROJECT_ID   = __ENV.PROJECT_ID   || 1;
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || 'paste-access-token';
const DEBUG        = (__ENV.DEBUG || '0') === '1';

// ====================
// k6 options
// ====================
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
                { duration: '2m', target: 100 },
                { duration: '6m', target: 100 },
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
            startVUs: 40,
            exec: 'spike',
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
        // 수정(UPDATE)만 평가
        'http_req_failed{scenario:smoke,endpoint:modify}':   ['rate<0.01'],
        'http_req_duration{scenario:smoke,endpoint:modify}': ['p(95)<700'],

        'http_req_failed{scenario:load,endpoint:modify}':    ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:modify}':  ['p(95)<1000'],

        'http_req_failed{scenario:stress,endpoint:modify}':  ['rate<0.05'],
        'http_req_duration{scenario:stress,endpoint:modify}':['p(99)<2000'],

        'http_req_failed{scenario:soak,endpoint:modify}':    ['rate<0.02'],
        'http_req_duration{scenario:soak,endpoint:modify}':  ['avg<1200'],

        'http_req_failed{scenario:spike,endpoint:modify}':   ['rate<0.05'],
        'http_req_duration{scenario:spike,endpoint:modify}': ['p(99)<2800'],

        'http_req_failed{scenario:capacity,endpoint:modify}':['rate<0.05'],
        'http_req_duration{scenario:capacity,endpoint:modify}':['p(95)<3000'],
    },
};

// 실행할 시나리오만 남기기
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

// ====================
// 유틸
// ====================
function headers(tags = {}) {
    return {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${ACCESS_TOKEN}`,
            'Cache-Control': 'no-store, no-cache, must-revalidate',
            'Pragma': 'no-cache',
            'Expires': '0',
        },
        tags,
    };
}
function safeJson(t) { try { return JSON.parse(t); } catch { return null; } }

// ====================
// 생성(준비) → 수정(측정) → 정리(삭제)
// ====================
function scenarioExec(sleepTime = 0) {
    // 1) 업로드 (prepare)
    const postUrl = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments?nocache=${Math.random()}`;
    const postRes = http.post(postUrl, JSON.stringify({ content: `수정대상 댓글 ${Math.random()}` }), headers({ endpoint: 'prepare-upload' }));
    if (DEBUG) console.log('UPLOAD', postRes.status, postRes.body?.slice?.(0, 160));
    const ok201 = check(postRes, { 'upload 201': (r) => r.status === 201 });
    if (!ok201) return;

    const j   = safeJson(postRes.body);
    const id  = j?.data?.commentId ?? j?.data?.id ?? j?.data?.comment?.id ?? null;
    check({ id }, { 'got commentId': (v) => v.id != null });
    if (id == null) return;

    // 2) 수정 (measure)
    const putUrl = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments/${id}?nocache=${Math.random()}`;
    const putRes = http.put(putUrl, JSON.stringify({ content: `수정된 내용 ${Math.random()}` }), headers({ endpoint: 'modify' }));
    if (DEBUG) console.log('MODIFY', putRes.status, putRes.body?.slice?.(0, 160));
    check(putRes, { 'modify 200': (r) => r.status === 200 });

    // 3) 정리 (cleanup)
    const delUrl = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments/${id}?nocache=${Math.random()}`;
    const delRes = http.del(delUrl, null, headers({ endpoint: 'cleanup-delete' }));
    if (DEBUG) console.log('DELETE', delRes.status, delRes.body?.slice?.(0, 160));
    check(delRes, { 'cleanup delete 200': (r) => r.status === 200 });

    if (sleepTime > 0) sleep(sleepTime);
}

export function smoke()    { scenarioExec(1); }
export function load()     { scenarioExec(0.5); }
export function stress()   { scenarioExec(); }
export function soak()     { scenarioExec(1); }
export function spike()    { scenarioExec(); }
export function capacity() { scenarioExec(); }

// ============ 실행 예시 ============
// k6 run performance-test/comment/scenarios/modify-basic.test.js \
//   -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e PROJECT_ID=1 \
//   -e ACCESS_TOKEN="발급토큰"
