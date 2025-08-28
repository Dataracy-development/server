// performance-test/like/scenarios/like-idempotent-race.test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

// ==================== 공통 설정 ====================
const BASE_URL     = __ENV.BASE_URL     || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO     || 'smoke';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || 'paste-access-token';

const TARGET_TYPE  = (__ENV.TARGET_TYPE || 'PROJECT').toUpperCase();
const TARGET_ID    = parseInt(__ENV.TARGET_ID || '1', 10);
const ACTION       = (__ENV.ACTION || 'LIKE').toUpperCase(); // LIKE 고정 권장
const BURST        = parseInt(__ENV.BURST || '5', 10);       // 동일 요청을 몇 번 연타할지
const USE_IDEMPOTENCY = (__ENV.USE_IDEMPOTENCY || '1') === '1'; // 헤더로 멱등키 보낼지
const RETRIES      = parseInt(__ENV.RETRIES || '1', 10);
const DEBUG        = (__ENV.DEBUG || '0') === '1';

// ==================== k6 options ====================
export let options = {
    scenarios: {
        smoke: { executor: 'constant-vus', vus: 1, duration: '20s', exec: 'smoke' },
        race:  { executor: 'ramping-vus', startVUs: 5, exec: 'race',
            stages: [{ duration: '20s', target: 50 }, { duration: '40s', target: 100 }, { duration: '20s', target: 0 }] },
    },
    thresholds: {
        'http_req_failed{endpoint:like-race}': ['rate<0.02'],
        'http_req_duration{endpoint:like-race}': ['p(95)<900'],
    },
};
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

// ==================== 유틸 ====================
function headers(tags = {}, idemKey = ''){
    const h = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
    if (ACCESS_TOKEN) h['Authorization'] = `Bearer ${ACCESS_TOKEN}`;
    if (USE_IDEMPOTENCY && idemKey) h['Idempotency-Key'] = idemKey;
    return { headers: h, tags };
}
function post(url, body, tag, key){ return http.post(url, body, headers(tag, key)); }

// ==================== 시나리오 ====================
function execOnce(){
    const url = `${BASE_URL}/api/v1/likes?nocache=${Math.random()}`;
    const body = JSON.stringify({ targetType: TARGET_TYPE, targetId: TARGET_ID, action: ACTION });
    const idemKey = `${TARGET_TYPE}:${TARGET_ID}:${Math.floor(Date.now()/60000)}`; // 분단위 멱등키 예시

    // 동일 요청 연타
    for (let i=0;i<BURST;i++){
        const res = post(url, body, { endpoint: 'like-race' }, idemKey);
        if (DEBUG) console.log(`REQ#${i+1}`, res.status, (res.body||'').slice(0,140));
        check(res, { '2xx/409/429 허용': (r)=> (r.status>=200&&r.status<300) || r.status===409 || r.status===429 });
        // 409/429가 일부 나오더라도 최종 상태는 한 번만 변경되는 게 목표
    }
}
export function smoke(){ execOnce(); }
export function race(){ execOnce(); }

// ===== 실행 예시 =====
// k6 run performance-test/like/scenarios/like-idempotent-race.test.js \
//  -e SCENARIO=race -e BASE_URL=http://localhost:8080 -e ACCESS_TOKEN=... \
//  -e TARGET_TYPE=PROJECT -e TARGET_ID=1 -e ACTION=LIKE -e BURST=8 -e USE_IDEMPOTENCY=1 -e DEBUG=1
