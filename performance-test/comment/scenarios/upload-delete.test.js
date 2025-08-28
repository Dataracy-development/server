import http from 'k6/http';
import {check, sleep} from 'k6';

/**
 * 댓글 작성 -> 삭제 시나리오로 DB에 적재되지 않도록 한다.
 */

// ====================
// 공통 설정
// ====================
const BASE_URL      = __ENV.BASE_URL      || 'http://localhost:8080';
const RUN_SCENARIO  = __ENV.SCENARIO      || 'smoke';

const PROJECT_ID    = __ENV.PROJECT_ID    || 1;
const ACCESS_TOKEN  = __ENV.ACCESS_TOKEN  || 'paste-access-token';
const DEBUG         = (__ENV.DEBUG || '0') === '1';

// ====================
// k6 options
// ====================
export let options = {
    scenarios: {
        smoke: {
            executor: 'constant-vus',
            vus: 5,
            duration: '30s',
            exec: 'smoke'
        },
        load: {
            executor: 'ramping-vus',
            startVUs: 10,
            exec: 'load',
            stages: [
                { duration: '2m', target: 200 },
                { duration: '6m', target: 200 },
                { duration: '2m', target: 0   },
            ]
        },
        stress: {
            executor: 'ramping-vus',
            startVUs: 100,
            exec: 'stress',
            stages: [
                { duration: '3m', target: 500  },
                { duration: '4m', target: 1000 },
                { duration: '4m', target: 2000 },
                { duration: '3m', target: 0    },
            ]
        },
        soak: {
            executor: 'constant-vus',
            vus: 300,
            duration: '1h',
            exec: 'soak'
        },
        spike: {
            executor: 'ramping-vus',
            startVUs: 50,
            exec: 'spike',
            stages: [
                { duration: '15s', target: 2000 },
                { duration: '2m',  target: 2000 },
                { duration: '1m',  target: 0    },
            ]
        },
        capacity: {
            executor: 'ramping-arrival-rate',
            startRate: 100, timeUnit: '1s',
            preAllocatedVUs: 500, maxVUs: 5000, exec: 'capacity',
            stages: [
                { target: 500,  duration: '2m' },
                { target: 1000, duration: '2m' },
                { target: 2000, duration: '2m' },
                { target: 3000, duration: '2m' },
                { target: 0,    duration: '2m' },
            ]
        },
    },
    thresholds: {
        // 업로드(INSERT) 임계치 - endpoint:upload 만 평가
        'http_req_failed{scenario:smoke,endpoint:upload}':    ['rate<0.01'],
        'http_req_duration{scenario:smoke,endpoint:upload}':  ['p(95)<800'],

        'http_req_failed{scenario:load,endpoint:upload}':     ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:upload}':   ['p(95)<1200'],

        'http_req_failed{scenario:stress,endpoint:upload}':   ['rate<0.05'],
        'http_req_duration{scenario:stress,endpoint:upload}': ['p(99)<2500'],

        'http_req_failed{scenario:soak,endpoint:upload}':     ['rate<0.02'],
        'http_req_duration{scenario:soak,endpoint:upload}':   ['avg<1500'],

        'http_req_failed{scenario:spike,endpoint:upload}':    ['rate<0.05'],
        'http_req_duration{scenario:spike,endpoint:upload}':  ['p(99)<3000'],

        'http_req_failed{scenario:capacity,endpoint:upload}': ['rate<0.05'],
        'http_req_duration{scenario:capacity,endpoint:upload}':['p(95)<3000'],

        // 삭제 임계치 - endpoint:delete 만 평가 (삭제는 더 타이트)
        'http_req_failed{scenario:smoke,endpoint:delete}':    ['rate<0.01'],
        'http_req_duration{scenario:smoke,endpoint:delete}':  ['p(95)<500'],

        'http_req_failed{scenario:load,endpoint:delete}':     ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:delete}':   ['p(95)<800'],

        'http_req_failed{scenario:stress,endpoint:delete}':   ['rate<0.05'],
        'http_req_duration{scenario:stress,endpoint:delete}': ['p(99)<1500'],

        'http_req_failed{scenario:soak,endpoint:delete}':     ['rate<0.02'],
        'http_req_duration{scenario:soak,endpoint:delete}':   ['avg<900'],

        'http_req_failed{scenario:spike,endpoint:delete}':    ['rate<0.05'],
        'http_req_duration{scenario:spike,endpoint:delete}':  ['p(99)<2000'],

        'http_req_failed{scenario:capacity,endpoint:delete}': ['rate<0.05'],
        'http_req_duration{scenario:capacity,endpoint:delete}':['p(95)<2500'],
    },
};

// 실행할 시나리오만 남기기
for (const s of Object.keys(options.scenarios)) {
    if (s !== RUN_SCENARIO) delete options.scenarios[s];
}

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
// 시나리오 엔트리포인트 (업로드 → 삭제 한 루프)
// ====================
function scenarioExec(sleepTime = 0) {
    // 1) 업로드 (측정: endpoint=upload)
    const postUrl = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments?nocache=${Math.random()}`;
    const body    = JSON.stringify({ content: `부하테스트 댓글 ${Math.random()}` });

    const postRes = http.post(postUrl, body, headers({ endpoint: 'upload' }));
    if (DEBUG) console.log('UPLOAD', postRes.status, postRes.body?.slice?.(0, 200));
    const ok201 = check(postRes, { 'upload 201': (r) => r.status === 201 });

    // 생성된 ID 추출 (유연 매핑)
    let commentId = null;
    if (ok201) {
        const j = safeJson(postRes.body);
        commentId = j?.data?.commentId ?? j?.data?.id ?? j?.data?.comment?.id ?? null;
        check({ commentId }, { 'got commentId': (v) => v.commentId != null });
    }

    // 2) 삭제 (측정: endpoint=delete)
    if (commentId != null) {
        const delUrl = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments/${commentId}?nocache=${Math.random()}`;
        const delRes = http.del(delUrl, null, headers({ endpoint: 'delete' }));
        if (DEBUG) console.log('DELETE', delRes.status, delRes.body?.slice?.(0, 200));
        check(delRes, { 'delete 200': (r) => r.status === 200 });
    }

    if (sleepTime > 0) sleep(sleepTime);
}

export function smoke()    { scenarioExec(1);   }
export function load()     { scenarioExec(0.5); }
export function stress()   { scenarioExec();    }
export function soak()     { scenarioExec(1);   }
export function spike()    { scenarioExec();    }
export function capacity() { scenarioExec();    }

// ============ 실행 예시 ============
// k6 run performance-test/comment/scenarios/upload-delete.test.js \
//   -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e PROJECT_ID=1 \
//   -e ACCESS_TOKEN="발급토큰"
