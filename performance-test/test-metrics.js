import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";

// 커스텀 메트릭 정의
const successRate = new Rate("success_rate");
const responseTime = new Trend("response_time");
const requestCount = new Counter("request_count");

export let options = {
  vus: 1,
  duration: "5s",
};

export default function() {
  const res = http.get("https://httpbin.org/get");
  const success = res.status === 200;
  
  successRate.add(success);
  responseTime.add(res.timings.duration);
  requestCount.add(1);
  
  check(res, {
    "status is 200": (r) => r.status === 200,
    "response time < 1000ms": (r) => r.timings.duration < 1000,
  });
  
  sleep(1);
}
