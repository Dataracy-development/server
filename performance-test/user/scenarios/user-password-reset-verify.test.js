
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
        'http_req_failed{scenario:smoke,endpoint:user-password-reset-verify}':   ['rate<0.01'],
        'http_req_duration{scenario:smoke,endpoint:user-password-reset-verify}': ['p(95)<800'],

        'http_req_failed{scenario:load,endpoint:user-password-reset-verify}':    ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:user-password-reset-verify}':  ['p(95)<1000'],

        'http_req_failed{scenario:stress,endpoint:user-password-reset-verify}':  ['rate<0.05'],
        'http_req_duration{scenario:stress,endpoint:user-password-reset-verify}':['p(99)<2200'],

        'http_req_failed{scenario:soak,endpoint:user-password-reset-verify}':    ['rate<0.02'],
        'http_req_duration{scenario:soak,endpoint:user-password-reset-verify}':  ['avg<1400'],

        'http_req_failed{scenario:spike,endpoint:user-password-reset-verify}':   ['rate<0.05'],
        'http_req_duration{scenario:spike,endpoint:user-password-reset-verify}': ['p(99)<2600'],

        'http_req_failed{scenario:capacity,endpoint:user-password-reset-verify}':['rate<0.05'],
        'http_req_duration{scenario:capacity,endpoint:user-password-reset-verify}':['p(95)<3000'],
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

const EMAIL = __ENV.EMAIL || 'someone@example.com';
const CODE  = __ENV.CODE  || '000000';

function execOnce(wait=0){
    const body = JSON.stringify({ email: EMAIL, code: CODE });
    const res = http.post(`${BASE_URL}/api/v1/users/password/reset/verify?nocache=${Math.random()}`, body, headers({ endpoint: 'user-password-reset-verify' }));
    // 200 또는 검증 실패 400/401 허용 여부는 필요 시 조정
    check(res, { '200/400 허용': (r)=> r.status===200 || r.status===400 || r.status===401 });
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
// k6 run performance-test/user/scenarios/user-password-reset-verify.test.js -e SCENARIO=smoke -e EMAIL=alice@example.com -e CODE=123456
