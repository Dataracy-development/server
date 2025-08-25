
import http from 'k6/http';
import { check, sleep } from 'k6';

/** Generated user-domain perf test (k6) */
const BASE_URL     = __ENV.BASE_URL     || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO     || 'race';
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
        'http_req_failed{scenario:smoke,endpoint:user-signup-race}':   ['rate<0.01'],
        'http_req_duration{scenario:smoke,endpoint:user-signup-race}': ['p(95)<900'],

        'http_req_failed{scenario:load,endpoint:user-signup-race}':    ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:user-signup-race}':  ['p(95)<1100'],

        'http_req_failed{scenario:stress,endpoint:user-signup-race}':  ['rate<0.15'],
        'http_req_duration{scenario:stress,endpoint:user-signup-race}':['p(99)<2200'],

        'http_req_failed{scenario:soak,endpoint:user-signup-race}':    ['rate<0.02'],
        'http_req_duration{scenario:soak,endpoint:user-signup-race}':  ['avg<1300'],

        'http_req_failed{scenario:spike,endpoint:user-signup-race}':   ['rate<0.2'],
        'http_req_duration{scenario:spike,endpoint:user-signup-race}': ['p(99)<2600'],

        'http_req_failed{scenario:capacity,endpoint:user-signup-race}':['rate<0.05'],
        'http_req_duration{scenario:capacity,endpoint:user-signup-race}':['p(95)<3000'],
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

const EMAIL = __ENV.EMAIL || `race_${Date.now()}_${randStr(5)}@example.com`;
const ATTEMPTS = parseInt(__ENV.ATTEMPTS || '5', 10);
const PASSWORD_LEN = parseInt(__ENV.PASSWORD_LEN || '12', 10);

export let options2 = options; // just to avoid unused var lint

function execOnce(wait=0){
    for (let i=0;i<ATTEMPTS;i++){
        const body = JSON.stringify({ email: EMAIL, password: randStr(PASSWORD_LEN), nickname: `race_${randStr(4)}` });
        const res = http.post(`${BASE_URL}/api/v1/users/signup?nocache=${Math.random()}`, body, headers({ endpoint: 'user-signup-race' }));
        // 201/200 or 409 (중복) 허용
        check(res, { '2xx or 409': (r)=> (r.status>=200 && r.status<300) || r.status===409 });
    }
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
// k6 run performance-test/user/scenarios/user-signup-race.test.js -e SCENARIO=smoke -e EMAIL="dup@example.com"
