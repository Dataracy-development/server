// performance-test/like/scenarios/like-toggle-hotspot.test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

// ==================== 공통 설정 ====================
const BASE_URL     = __ENV.BASE_URL     || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO     || 'smoke';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || 'paste-access-token';

const TARGET_TYPE  = (__ENV.TARGET_TYPE || 'PROJECT').toUpperCase(); // PROJECT | COMMENT
const TARGET_ID    = parseInt(__ENV.TARGET_ID || '1', 10);
const TOGGLES      = parseInt(__ENV.TOGGLES || '5', 10);    // 1 VU가 연속으로 토글할 횟수
const POST_WAIT_MS = parseInt(__ENV.POST_WAIT_MS || '0', 10);
const RETRIES      = parseInt(__ENV.RETRIES || '2', 10);
const RETRY_BASE_MS= parseInt(__ENV.RETRY_BASE_MS || '60', 10);
const STRICT_200   = (__ENV.STRICT_200 || '0') === '1';
const DEBUG        = (__ENV.DEBUG || '0') === '1';

// 감사/검증(선택): 최종 카운트 조회용(스테이징/로컬 전용 관리 API)
const AUDIT_URL    = __ENV.AUDIT_URL || ''; // 예: http://localhost:8080/admin/likes/audit?targetType=PROJECT&targetId=1

// ==================== k6 options ====================
export let options = {
    scenarios: {
        smoke: { executor: 'constant-vus', vus: 5, duration: '30s', exec: 'smoke' },
        load:  {
            executor: 'ramping-vus', startVUs: 10, exec: 'load',
            stages: [{ duration: '2m', target: 200 }, { duration: '5m', target: 200 }, { duration: '1m', target: 0 }]
        },
        stress:{
            executor: 'ramping-vus', startVUs: 50, exec: 'stress',
            stages: [{ duration: '2m', target: 400 }, { duration: '3m', target: 800 }, { duration: '3m', target: 1500 }, { duration: '1m', target: 0 }]
        },
        soak:  { executor: 'constant-vus', vus: 200, duration: '1h', exec: 'soak' },
        spike: {
            executor: 'ramping-vus', startVUs: 30, exec: 'spike',
            stages: [{ duration: '15s', target: 1500 }, { duration: '2m', target: 1500 }, { duration: '40s', target: 0 }]
        },
        capacity: {
            executor: 'ramping-arrival-rate', startRate: 80, timeUnit: '1s',
            preAllocatedVUs: 400, maxVUs: 4000, exec: 'capacity',
            stages: [{ target: 500, duration: '2m' }, { target: 1000, duration: '2m' }, { target: 0, duration: '2m' }]
        },
    },
    thresholds: {
        'http_req_failed{endpoint:like-toggle,scenario:smoke}':   ['rate<0.01'],
        'http_req_duration{endpoint:like-toggle,scenario:smoke}': ['p(95)<700'],

        'http_req_failed{endpoint:like-toggle,scenario:load}':    ['rate<0.02'],
        'http_req_duration{endpoint:like-toggle,scenario:load}':  ['p(95)<900'],

        'http_req_failed{endpoint:like-toggle,scenario:stress}':  ['rate<0.05'],
        'http_req_duration{endpoint:like-toggle,scenario:stress}':['p(99)<1800'],

        'http_req_failed{endpoint:like-toggle,scenario:soak}':    ['rate<0.02'],
        'http_req_duration{endpoint:like-toggle,scenario:soak}':  ['avg<1100'],

        'http_req_failed{endpoint:like-toggle,scenario:spike}':   ['rate<0.05'],
        'http_req_duration{endpoint:like-toggle,scenario:spike}': ['p(99)<2500'],

        'http_req_failed{endpoint:like-toggle,scenario:capacity}': ['rate<0.05'],
        'http_req_duration{endpoint:like-toggle,scenario:capacity}':['p(95)<3000'],
    },
};
// 실행할 시나리오만 남기기
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

// ==================== 유틸 ====================
function headers(tags = {}) {
    const h = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Cache-Control': 'no-store, no-cache, must-revalidate',
    };
    if (ACCESS_TOKEN) h['Authorization'] = `Bearer ${ACCESS_TOKEN}`;
    return { headers: h, tags };
}
function safeJson(t){ try{ return JSON.parse(t);}catch{ return null; } }
function isRetryable(st){ return st===409||st===429||st===500||st===502||st===503||st===504; }
function postWithRetry(url, body, tag){
    let a=0;
    while(true){
        const r=http.post(url, body, headers(tag));
        if(DEBUG) console.log('POST#'+(a+1), r.status, (r.body||'').slice(0,160));
        const ok = STRICT_200 ? r.status===200 : (r.status>=200 && r.status<300);
        if(ok) return r;
        if(a>=RETRIES || !isRetryable(r.status)) return r;
        sleep((RETRY_BASE_MS*Math.pow(2,a))/1000);
        a++;
    }
}

// ==================== 시나리오 ====================
function execOnce(wait=0){
    const url = `${BASE_URL}/api/v1/likes?nocache=${Math.random()}`;
    for(let i=0;i<TOGGLES;i++){
        const body = JSON.stringify({ targetType: TARGET_TYPE, targetId: TARGET_ID, action: 'TOGGLE' });
        const res = postWithRetry(url, body, { endpoint: 'like-toggle' });
        check(res, { [`toggle ${i+1}/${TOGGLES} 2xx`]: (r)=> r.status>=200 && r.status<300 });
        if(POST_WAIT_MS>0) sleep(POST_WAIT_MS/1000);
    }

    // 감사 API로 최종 상태 확인(선택)
    if (AUDIT_URL) {
        const audit = http.get(`${AUDIT_URL}${AUDIT_URL.includes('?')?'&':'?'}nocache=${Math.random()}`, headers({ endpoint: 'like-audit' }));
        if (DEBUG) console.log('AUDIT', audit.status, (audit.body||'').slice(0,160));
        check(audit, { 'audit 200': (r)=> r.status===200 });
    }
    if(wait>0) sleep(wait);
}
export function smoke(){ execOnce(1); }
export function load(){ execOnce(0.5); }
export function stress(){ execOnce(); }
export function soak(){ execOnce(1); }
export function spike(){ execOnce(); }
export function capacity(){ execOnce(); }

// ===== 실행 예시 =====
// k6 run performance-test/like/scenarios/like-toggle-hotspot.test.js \
//  -e SCENARIO=spike -e BASE_URL=http://localhost:8080 -e ACCESS_TOKEN=... \
//  -e TARGET_TYPE=PROJECT -e TARGET_ID=1 -e TOGGLES=6 -e DEBUG=1
