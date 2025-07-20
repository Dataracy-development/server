import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { BASE_URL, commonHeaders } from '../common/config.js';

export let options = {
    scenarios: {
        before_n_plus_1: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 100,
            stages: [
                { target: 50, duration: '30s' },
                { target: 100, duration: '1m' },
                { target: 150, duration: '1m' },
                { target: 0, duration: '20s' }
            ],
        },
    },
    thresholds: {
        'http_req_duration{scenario:before_n_plus_1}': [
            'avg<100',        // 기대 성능 상향
            'p(90)<150',
            'p(95)<250',
            'p(99)<400',
        ],
        'http_req_failed{scenario:before_n_plus_1}': ['rate<0.01'], // 실패율 1% 이하
        'checks': ['rate>0.99']
    }
};

export default function () {
    group('🔥 인기 프로젝트 목록 조회', () => {
        const size = Math.floor(Math.random() * 5 + 10); // size=10~14 랜덤
        const res = http.get(`${BASE_URL}/api/v1/projects/search/popular?size=${size}`, {
            headers: commonHeaders,
        });

        check(res, {
            '✅ Status is 200': (r) => r.status === 200,
            '⏱️ Response time < 500ms': (r) => r.timings.duration < 500,
            '🧪 Returned project list': (r) => r.json('data.length') > 0,
        });

        sleep(Math.random() * 2); // think time
    });
}
