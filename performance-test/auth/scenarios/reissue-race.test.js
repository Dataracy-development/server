
import http from 'k6/http';
import {check} from 'k6';

/**
 * 동시 재발급 레이스 (필수 정합성)
 */

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const RUN_SCENARIO = __ENV.SCENARIO || 'race';
const REFRESH_TOKEN = __ENV.REFRESH_TOKEN || 'dummy-refresh-token';

export let options = {
  scenarios: {
    race: { executor: 'ramping-vus', startVUs: 50, exec: 'race',
            stages: [{ duration: '30s', target: 500 }, { duration: '30s', target: 0 }] },
  },
  thresholds: {
    'http_req_failed{scenario:race}': ['rate<0.2'],
    'http_req_duration{scenario:race}': ['p(99)<2500'],
  },
};
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

export function race(){
  const res = http.post(`${BASE_URL}/api/v1/auth/token/re-issue?nocache=${Math.random()}`, null, {
    headers: { 'Content-Type':'application/json' },
    cookies: { refreshToken: REFRESH_TOKEN },
  });
  check(res, { '2xx/401/409/429 허용': r => (r.status>=200&&r.status<300) || [401,409,429].includes(r.status) });
}

// 예시
// k6 run performance-test/auth/scenarios/reissue-race.test.js -e SCENARIO=race -e BASE_URL=http://localhost:8080 -e REFRESH_TOKEN=...
