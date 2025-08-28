
import http from 'k6/http';
import { check, sleep } from 'k6';

/** Generated user-domain perf test (k6) */
const BASE_URL     = __ENV.BASE_URL     || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO     || 'smoke';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN || 'paste-access-token';

export let options = {
    scenarios: {
        smoke:   { executor: 'constant-vus', vus: 5, duration: '30s', exec: 'smoke' },
        load:    { executor: 'ramping-vus', startVUs: 10, exec: 'load',
                   stages: [{ duration: '2m', target: 120 }, { duration: '6m', target: 120 }, { duration: '2m', target: 0 }] },
        stress:  { executor: 'ramping-vus', startVUs: 100, exec: 'stress',
                   stages: [{ duration: '3m', target: 300 }, { duration: '4m', target: 600 }, { duration: '4m', target: 1200 }, { duration: '3m', target: 0 }] },
        soak:    { executor: 'constant-vus', vus: 200, duration: '1h', exec: 'soak' },
        spike:   { executor: 'ramping-vus', startVUs: 40, exec: 'spike',
                   stages: [{ duration: '15s', target: 1500 }, { duration: '2m', target: 1500 }, { duration: '1m', target: 0 }] },
        capacity:{ executor: 'ramping-arrival-rate', startRate: 80, timeUnit: '1s', preAllocatedVUs: 300, maxVUs: 3000, exec: 'capacity',
                   stages: [{ target: 400, duration: '2m' }, { target: 800, duration: '2m' }, { target: 0, duration: '2m' }] },
    },

    thresholds: {
        'http_req_failed{scenario:smoke,endpoint:user-detail}':   ['rate<0.01'],
        'http_req_duration{scenario:smoke,endpoint:user-detail}': ['p(95)<500'],

        'http_req_failed{scenario:load,endpoint:user-detail}':    ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:user-detail}':  ['p(95)<600'],

        'http_req_failed{scenario:stress,endpoint:user-detail}':  ['rate<0.05'],
        'http_req_duration{scenario:stress,endpoint:user-detail}':['p(99)<1200'],

        'http_req_failed{scenario:soak,endpoint:user-detail}':    ['rate<0.02'],
        'http_req_duration{scenario:soak,endpoint:user-detail}':  ['avg<800'],

        'http_req_failed{scenario:spike,endpoint:user-detail}':   ['rate<0.05'],
        'http_req_duration{scenario:spike,endpoint:user-detail}': ['p(99)<1500'],

        'http_req_failed{scenario:capacity,endpoint:user-detail}':['rate<0.05'],
        'http_req_duration{scenario:capacity,endpoint:user-detail}':['p(95)<1800'],
    },};

function headers(tags = {}) {
    const h = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    };
    if (ACCESS_TOKEN) h['Authorization'] = `Bearer ${ACCESS_TOKEN}`;
    return { headers: h, tags };
}
function safeJson(t) { try { return JSON.parse(t); } catch { return null; } }
function jitter(ms) { return Math.max(0, ms + Math.floor((Math.random() - 0.5) * ms)); }
function randStr(n){ const a='abcdefghijklmnopqrstuvwxyz0123456789'; let s=''; for(let i=0;i<n;i++) s+=a[Math.floor(Math.random()*a.length)]; return s; }

const USER_ID = parseInt(__ENV.USER_ID || '1', 10);

function execOnce(wait=0){
    const res = http.get(`${BASE_URL}/api/v1/users/${USER_ID}?nocache=${Math.random()}`, headers({ endpoint: 'user-detail' }));
    check(res, { 'detail 200': (r)=> r.status===200 });
    if (wait>0) sleep(wait);
}

for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

export function smoke()    { execOnce(1); }
export function load()     { execOnce(0.5); }
export function stress()   { execOnce(); }
export function soak()     { execOnce(1); }
export function spike()    { execOnce(); }
export function capacity() { execOnce(); }


// 실행 예시:
// k6 run performance-test/user/scenarios/user-read-detail.test.js -e SCENARIO=load -e USER_ID=42
