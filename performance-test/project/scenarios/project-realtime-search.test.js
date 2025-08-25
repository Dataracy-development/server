
import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * 실시간 검색 시나리오
 */

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
        capacity:{ executor: 'ramping-arrival-rate',
                   startRate: 80, timeUnit: '1s', preAllocatedVUs: 300, maxVUs: 3000, exec: 'capacity',
                   stages: [{ target: 400, duration: '2m' }, { target: 800, duration: '2m' }, { target: 0, duration: '2m' }] },
    },
    thresholds: {
        'http_req_failed{scenario:smoke,endpoint:search-rt}':   ['rate<0.01'],
        'http_req_duration{scenario:smoke,endpoint:search-rt}': ['p(95)<1000'],

        'http_req_failed{scenario:load,endpoint:search-rt}':    ['rate<0.02'],
        'http_req_duration{scenario:load,endpoint:search-rt}':  ['p(95)<1200'],

        'http_req_failed{scenario:stress,endpoint:search-rt}':  ['rate<0.05'],
        'http_req_duration{scenario:stress,endpoint:search-rt}':['p(99)<1800'],

        'http_req_failed{scenario:soak,endpoint:search-rt}':    ['rate<0.02'],
        'http_req_duration{scenario:soak,endpoint:search-rt}':  ['avg<1300'],

        'http_req_failed{scenario:spike,endpoint:search-rt}':   ['rate<0.05'],
        'http_req_duration{scenario:spike,endpoint:search-rt}': ['p(99)<2200'],

        'http_req_failed{scenario:capacity,endpoint:search-rt}':['rate<0.05'],
        'http_req_duration{scenario:capacity,endpoint:search-rt}':['p(95)<2500'],
    },
};
for (const s of Object.keys(options.scenarios)) if (s !== RUN_SCENARIO) delete options.scenarios[s];

function headers(tags={}) {
  return {
    headers: {
      'Accept': 'application/json',
      'Authorization': `Bearer ${ACCESS_TOKEN}`,
    },
    tags,
  };
}

function execOnce(wait=0) {
  const url = `${BASE_URL}/api/v1/projects/search/real-time?q=test&nocache=${Math.random()}`;
  const res = http.get(url, headers({ endpoint: 'search-rt' }));
  check(res, { '실시간 검색 200': (r) => r.status === 200 });
  if (wait > 0) sleep(wait);
}

export function smoke()    { execOnce(1); }
export function load()     { execOnce(0.5); }
export function stress()   { execOnce(); }
export function soak()     { execOnce(1); }
export function spike()    { execOnce(); }
export function capacity() { execOnce(); }
