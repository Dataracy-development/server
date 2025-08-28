
import http from 'k6/http';
import {check} from 'k6';

/**
 * 실패 로그인 남용 / 레이트리밋 (필수 보안)
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO || 'spike';
const TARGET_EMAIL = __ENV.TARGET_EMAIL || 'victim@example.com';

export let options = {
  scenarios: {
    smoke: { executor: 'constant-vus', vus: 5, duration: '20s', exec: 'smoke' },
    spike: { executor: 'constant-arrival-rate', rate: parseInt(__ENV.RATE || '600',10), timeUnit: '1s',
             duration: `${__ENV.DUR_S || '30'}s`, preAllocatedVUs: 2000, maxVUs: 4000, exec: 'spike' },
  },
  thresholds: {
    'http_req_failed{scenario:spike}': ['rate<0.35'],
    'http_req_duration{scenario:spike}': ['p(99)<3000'],
    'http_req_failed{scenario:smoke}': ['rate<0.05'],
    'http_req_duration{scenario:smoke}': ['p(95)<800'],
  },
};
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

export function smoke(){}
export function spike(){
  const body = JSON.stringify({ email: TARGET_EMAIL, password: 'wrong-'+Math.random() });
  const res = http.post(`${BASE_URL}/api/v1/auth/login?nocache=${Math.random()}`, body, {
    headers: { 'Content-Type':'application/json' },
  });
  check(res, { '400/401/429/423 허용': r => [400,401,429,423].includes(r.status) });
}

// 예시
// k6 run performance-test/auth/scenarios/login-abuse.test.js -e SCENARIO=spike -e BASE_URL=http://localhost:8080 -e TARGET_EMAIL=foo@bar.com -e RATE=800 -e DUR_S=20
