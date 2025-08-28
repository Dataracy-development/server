
import http from 'k6/http';
import {check, sleep} from 'k6';
import {SharedArray} from 'k6/data';

/**
 * 로그인 → 보호 API → 로그아웃 → 보호 API 401 (필수 E2E)
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const AUTH_MODE = (__ENV.AUTH_MODE || 'dev').toLowerCase();
const DEFAULT_EMAIL = __ENV.EMAIL || 'test@email.com';
const DEFAULT_PASSWORD = __ENV.PASSWORD || 'test_password';
const RUN_SCENARIO = __ENV.SCENARIO || 'smoke';

const USERS = new SharedArray('users', () => {
  if (__ENV.USERS_FILE) {
    const raw = open(__ENV.USERS_FILE).trim();
    return raw.split('\n').map(l => {
      const [email, password] = l.split(',').map(s => s.trim());
      return { email, password };
    }).filter(u => u.email && u.password);
  }
  return [{ email: DEFAULT_EMAIL, password: DEFAULT_PASSWORD }];
});
function credForVu() { return USERS[(__VU - 1) % USERS.length]; }

export let options = {
  scenarios: {
    smoke: { executor: 'constant-vus', vus: 5, duration: '30s', exec: 'smoke' },
    load:  { executor: 'ramping-vus', startVUs: 10, exec: 'load',
             stages: [{ duration: '2m', target: 120 }, { duration: '6m', target: 120 }, { duration: '2m', target: 0 }] },
  },
  thresholds: {
    'http_req_failed{scenario:smoke}': ['rate<0.01'],
    'http_req_duration{scenario:smoke}': ['p(95)<700'],
    'http_req_failed{scenario:load}': ['rate<0.02'],
    'http_req_duration{scenario:load}': ['p(95)<900'],
  },
};
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

function safeJson(t){ try{return JSON.parse(t)}catch{return null} }

function login() {
  const { email, password } = credForVu();
  const url = AUTH_MODE === 'dev' ? `${BASE_URL}/api/v1/auth/dev/login` : `${BASE_URL}/api/v1/auth/login`;
  return http.post(`${url}?nocache=${Math.random()}`, JSON.stringify({ email, password }), { headers: { 'Content-Type':'application/json' } });
}

function me(at){
  return http.get(`${BASE_URL}/api/v1/users/me?nocache=${Math.random()}`, { headers: at ? { 'Authorization': `Bearer ${at}` } : {} });
}

function logout(at){
  return http.post(`${BASE_URL}/api/v1/auth/logout?nocache=${Math.random()}`, null, { headers: at ? { 'Authorization': `Bearer ${at}` } : {} });
}

function scenarioExec(wait=0){
  const r = login();
  const j = safeJson(r.body);
  check(r, { 'login 200': res => res.status===200 });

  const at = AUTH_MODE==='dev' ? j?.data?.accessToken : (j?.data?.accessToken || null);
  const me1 = me(at);
  check(me1, { 'me 200': res => res.status===200 });

  const lo = logout(at);
  check(lo, { 'logout 200': res => res.status===200 });

  const me2 = me(at);
  check(me2, { 'me after logout 401/403': res => [401,403].includes(res.status) });

  if (wait>0) sleep(wait);
}

export function smoke(){ scenarioExec(1); }
export function load(){ scenarioExec(0.5); }

// 예시
// k6 run performance-test/auth/scenarios/login-chain.test.js -e SCENARIO=smoke -e BASE_URL=http://localhost:8080 -e AUTH_MODE=dev
