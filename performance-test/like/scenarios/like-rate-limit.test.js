// performance-test/like/scenarios/like-rate-limit.test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

// ==================== 공통 설정 ====================
const BASE_URL     = __ENV.BASE_URL     || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO     || 'spike';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || 'paste-access-token';

const TARGET_TYPE  = (__ENV.TARGET_TYPE || 'PROJECT').toUpperCase();
const TARGET_ID    = parseInt(__ENV.TARGET_ID || '1', 10);
const ACTION       = (__ENV.ACTION || 'TOGGLE').toUpperCase();

const REQ_PER_SEC  = parseInt(__ENV.REQ_PER_SEC || '200', 10); // 1 VU가 초당 몇 번 보낼지(짧은 테스트)
const DURATION_SEC = parseInt(__ENV.DURATION_SEC || '20', 10);
const DEBUG        = (__ENV.DEBUG || '0') === '1';

// ==================== k6 options ====================
export let options = {
    scenarios: {
        spike: {
            executor: 'constant-arrival-rate',
            rate: REQ_PER_SEC,
            timeUnit: '1s',
            duration: `${DURATION_SEC}s`,
            preAllocatedVUs: Math.min(REQ_PER_SEC*2, 2000),
            maxVUs: Math.min(REQ_PER_SEC*4, 4000),
            exec: 'spike',
        },
    },
    thresholds: {
        'http_req_failed{endpoint:like-rl}': ['rate<0.2'], // 레이트리밋으로 일부 실패(429)는 허용
        // 성능은 제한 상황이므로 완화
        'http_req_duration{endpoint:like-rl}': ['p(95)<2500'],
        // 429 비중이 일정 이상 나오는지 (옵션): 'checks{metric:rl-429}': ['rate>0.2']
    },
};

// ==================== 유틸 ====================
function headers(tags = {}) {
    const h = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
    if (ACCESS_TOKEN) h['Authorization'] = `Bearer ${ACCESS_TOKEN}`;
    return { headers: h, tags };
}

// ==================== 시나리오 ====================
export function spike(){
    const url = `${BASE_URL}/api/v1/likes?nocache=${Math.random()}`;
    const body = JSON.stringify({ targetType: TARGET_TYPE, targetId: TARGET_ID, action: ACTION });
    const res = http.post(url, body, headers({ endpoint: 'like-rl' }));
    if (DEBUG) console.log('RL', res.status, res.headers['Retry-After'] || '');
    check(res, {
        '2xx or 429': (r)=> (r.status>=200 && r.status<300) || r.status===429,
    });
}

// ===== 실행 예시 =====
// k6 run performance-test/like/scenarios/like-rate-limit.test.js \
//  -e SCENARIO=spike -e BASE_URL=http://localhost:8080 -e ACCESS_TOKEN=... \
//  -e TARGET_TYPE=PROJECT -e TARGET_ID=1 -e ACTION=TOGGLE -e REQ_PER_SEC=400 -e DURATION_SEC=30 -e DEBUG=1
