// performance-test/like/scenarios/like-distributed-load.test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

// ==================== 공통 설정 ====================
const BASE_URL     = __ENV.BASE_URL     || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO     || 'load';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || 'paste-access-token';

const TARGET_TYPE  = (__ENV.TARGET_TYPE || 'PROJECT').toUpperCase();
const ID_START     = parseInt(__ENV.TARGET_ID_START || '1', 10);
const ID_END       = parseInt(__ENV.TARGET_ID_END   || '10000', 10);
const ACTION       = (__ENV.ACTION || 'LIKE').toUpperCase(); // LIKE | UNLIKE | TOGGLE

const REQUESTS_PER_ITER = parseInt(__ENV.REQUESTS_PER_ITER || '5', 10);
const RETRIES      = parseInt(__ENV.RETRIES || '2', 10);
const RETRY_BASE_MS= parseInt(__ENV.RETRY_BASE_MS || '40', 10);
const STRICT_200   = (__ENV.STRICT_200 || '0') === '1';
const DEBUG        = (__ENV.DEBUG || '0') === '1';

// ==================== k6 options ====================
export let options = {
    scenarios: {
        load: {
            executor: 'ramping-vus', startVUs: 10, exec: 'load',
            stages: [{ duration: '2m', target: 300 }, { duration: '6m', target: 300 }, { duration: '1m', target: 0 }]
        },
        stress:{
            executor: 'ramping-vus', startVUs: 50, exec: 'stress',
            stages: [{ duration: '2m', target: 600 }, { duration: '3m', target: 1200 }, { duration: '3m', target: 2000 }, { duration: '1m', target: 0 }]
        },
        capacity:{
            executor: 'ramping-arrival-rate', startRate: 100, timeUnit: '1s',
            preAllocatedVUs: 400, maxVUs: 4000, exec: 'capacity',
            stages: [{ target: 800, duration: '2m' }, { target: 1600, duration: '2m' }, { target: 0, duration: '2m' }]
        },
    },
    thresholds: {
        'http_req_failed{endpoint:like-distributed}': ['rate<0.03'],
        'http_req_duration{endpoint:like-distributed}': ['p(95)<900','p(99)<1800'],
    },
};
// 시나리오만 남기기
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

// ==================== 유틸 ====================
function headers(tags = {}) {
    const h = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
    if (ACCESS_TOKEN) h['Authorization'] = `Bearer ${ACCESS_TOKEN}`;
    return { headers: h, tags };
}
function isRetryable(st){ return st===409||st===429||st===500||st===502||st===503||st===504; }
function postWithRetry(url, body, tag){
    let a=0;
    while(true){
        const r=http.post(url, body, headers(tag));
        const ok = STRICT_200 ? r.status===200 : (r.status>=200 && r.status<300);
        if (ok || a>=RETRIES || !isRetryable(r.status)) return r;
        sleep((RETRY_BASE_MS*Math.pow(2,a))/1000);
        a++;
    }
}
let cursor = ID_START;
function nextId(){
    if(cursor>ID_END) cursor=ID_START;
    return cursor++;
}

// ==================== 시나리오 ====================
function execOnce(){
    const url = `${BASE_URL}/api/v1/likes?nocache=${Math.random()}`;
    for(let i=0;i<REQUESTS_PER_ITER;i++){
        const body = JSON.stringify({ targetType: TARGET_TYPE, targetId: nextId(), action: ACTION });
        const res = postWithRetry(url, body, { endpoint: 'like-distributed' });
        check(res, { '2xx': (r)=> r.status>=200 && r.status<300 });
    }
}
export function load(){ execOnce(); }
export function stress(){ execOnce(); }
export function capacity(){ execOnce(); }

// ===== 실행 예시 =====
// k6 run performance-test/like/scenarios/like-distributed-load.test.js \
//  -e SCENARIO=capacity -e BASE_URL=http://localhost:8080 -e ACCESS_TOKEN=... \
//  -e TARGET_TYPE=PROJECT -e TARGET_ID_START=1 -e TARGET_ID_END=50000 -e ACTION=LIKE
