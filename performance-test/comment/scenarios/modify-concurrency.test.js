import http from 'k6/http';
import {check, sleep} from 'k6';

/**
 * 동일 댓글 하나를 다수가 동시에 수정하는 경합 시나리오 (동시성 가시화)
 */

// ====================
// 공통 설정
// ====================
const BASE_URL     = __ENV.BASE_URL     || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO     || 'load'; // 동시성 확인 목적이므로 기본 load
const PROJECT_ID   = __ENV.PROJECT_ID   || 1;
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || 'paste-access-token';
const DEBUG        = (__ENV.DEBUG || '0') === '1';

export let options = {
    scenarios: {
        load:    {
            executor: 'ramping-vus',
            startVUs: 20,
            exec: 'load',
            stages: [
                { duration: '2m', target: 200 },
                { duration: '3m', target: 200 },
                { duration: '1m', target: 0 }
            ]
        },
        stress:  {
            executor: 'ramping-vus',
            startVUs: 100,
            exec: 'stress',
            stages: [
                { duration: '2m', target: 400 },
                { duration: '3m', target: 800 },
                { duration: '1m', target: 0 }
            ]
        },
    },
    thresholds: {
        // 경합으로 락 대기 등의 영향이 있으므로 가장 여유로운 기준
        'http_req_failed{scenario:load,endpoint:modify}':    ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:modify}':  ['p(95)<1300'],

        'http_req_failed{scenario:stress,endpoint:modify}':  ['rate<0.05'],
        'http_req_duration{scenario:stress,endpoint:modify}':['p(99)<3000'],
    },
};
// 실행 시 원하는 하나만 남겨도 됨
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

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
// setup / teardown
// ====================
export function setup() {
    const url = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments?nocache=${Math.random()}`;
    const res = http.post(url, JSON.stringify({ content: `경합용 댓글 seed ${Math.random()}` }), headers({ endpoint: 'prepare-upload' }));
    if (DEBUG) console.log('SETUP UPLOAD', res.status, res.body?.slice?.(0, 160));
    check(res, { 'setup upload 201': (r) => r.status === 201 });

    const j  = safeJson(res.body);
    const id = j?.data?.commentId ?? j?.data?.id ?? j?.data?.comment?.id ?? null;
    check({ id }, { 'setup got commentId': (v) => v.id != null });
    return { id };
}

export function teardown(data) {
    const id = data?.id;
    if (!id) return;
    const url = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments/${id}?nocache=${Math.random()}`;
    const res = http.del(url, null, headers({ endpoint: 'cleanup-delete' }));
    if (DEBUG) console.log('TEARDOWN DELETE', res.status, res.body?.slice?.(0, 160));
    check(res, { 'teardown delete 200': (r) => r.status === 200 });
}

// ====================
// 동시 수정 (모든 VU가 같은 id에 PUT)
// ====================
function execModify(data, sleepTime = 0) {
    const id = data?.id;
    if (!id) return;
    const url = `${BASE_URL}/api/v1/projects/${PROJECT_ID}/comments/${id}?nocache=${Math.random()}`;
    const res = http.put(url, JSON.stringify({ content: `경합 수정 ${Math.random()}` }), headers({ endpoint: 'modify' }));
    if (DEBUG) console.log('MODIFY', res.status, res.body?.slice?.(0, 160));
    check(res, { 'modify 200': (r) => r.status === 200 });

    if (sleepTime > 0) sleep(sleepTime);
}

export function load(data)    { execModify(data, 0.3); }
export function stress(data)  { execModify(data); }

// ============ 실행 예시 ============
// (기본: load 동시성)
// k6 run performance-test/comment/scenarios/modify-concurrency.test.js \
//   -e SCENARIO=load -e BASE_URL=http://localhost:8080 -e PROJECT_ID=1 \
//   -e ACCESS_TOKEN="발급토큰"
//
// (강경합: stress)
// k6 run performance-test/comment/scenarios/modify-concurrency.test.js \
//   -e SCENARIO=stress -e BASE_URL=http://localhost:8080 -e PROJECT_ID=1 \
//   -e ACCESS_TOKEN="발급토큰"
