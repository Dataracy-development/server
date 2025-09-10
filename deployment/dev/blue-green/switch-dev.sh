#!/bin/bash
set -Eeuo pipefail
log(){ echo "[$(date -u +%FT%TZ)] $*"; }
fail(){ log "[ERROR] $*"; exit 1; }

exec 9>/tmp/dataracy.deploy.lock
flock -n 9 || fail "다른 배포가 진행 중이다."
trap 'fail "스크립트 오류(line:$LINENO)"' ERR

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
DOCKER_DIR="$SCRIPT_DIR/../docker"
NGINX_DIR="$SCRIPT_DIR/../nginx"
STATE_DIR="/home/ubuntu/color-config"
CUR_FILE="$STATE_DIR/current_color_dev"
ALWAYS_RELOAD=false
NGINX_COMPOSE="$DOCKER_DIR/docker-compose-nginx-dev.yml"
NGINX_SVC_NAME="nginx-proxy-dev"
KIBANA_UPSTREAM="${KIBANA_UPSTREAM:-kibana-dev:5601}"

mkdir -p "$STATE_DIR"; [[ -f "$CUR_FILE" ]] || echo "blue" > "$CUR_FILE"
CUR="$(tr -d '[:space:]' < "$CUR_FILE")"; [[ "$CUR" =~ ^(blue|green)$ ]] || fail "current_color_dev 값 오류:$CUR"
NEXT=$([[ "$CUR" == "blue" ]] && echo green || echo blue)
NEXT_COMPOSE="$DOCKER_DIR/docker-compose-${NEXT}-dev.yml"
BACKEND_NAME="backend-${NEXT}"

log "== DEV DEPLOY: $CUR -> $NEXT =="

docker compose -f "$NEXT_COMPOSE" up -d --pull always

for i in {1..20}; do
  s="$(docker inspect --format='{{json .State.Health.Status}}' "$BACKEND_NAME" 2>/dev/null || echo null)"
  [[ "$s" == "\"healthy\"" ]] && { log "healthy"; break; } || { log "  [$i/20] $s"; sleep 5; }
done
[[ "$s" == "\"healthy\"" ]] || fail "$BACKEND_NAME healthy 실패"

UPSTREAM_DEST="$NGINX_DIR/upstream-blue-green-dev.conf"
TMP="$NGINX_DIR/upstream-blue-green-dev.conf.tmp"
cat > "$TMP" <<'EOF'
upstream backend { server REPLACE_BACKEND_NAME:8080; }
upstream kibana_dev { server REPLACE_KIBANA_UPSTREAM; }

server {
  listen 80;
  server_name dev.api.dataracy.co.kr;

  proxy_set_header Host $host;
  proxy_set_header X-Real-IP $remote_addr;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_set_header X-Forwarded-Proto $scheme;
  proxy_http_version 1.1;
  proxy_set_header Upgrade $http_upgrade;
  proxy_set_header Connection "upgrade";
  proxy_set_header Cookie $http_cookie;

  location /api { proxy_pass http://backend; proxy_request_buffering off; proxy_send_timeout 300s; proxy_read_timeout 300s; }
  location /swagger-ui/    { proxy_pass http://backend/swagger-ui/;    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always; add_header Pragma "no-cache" always; expires -1; etag off; }
  location /v3/api-docs    { proxy_pass http://backend/v3/api-docs;    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always; add_header Pragma "no-cache" always; expires -1; etag off; }
  location /swagger-config { proxy_pass http://backend/swagger-config; add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always; add_header Pragma "no-cache" always; expires -1; etag off; }
  location /webjars/       { proxy_pass http://backend/webjars/;       add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always; add_header Pragma "no-cache" always; expires -1; etag off; }
  location /kibana/ {
    proxy_pass http://kibana_dev/kibana/;
    proxy_read_timeout 600s; proxy_send_timeout 600s;
    proxy_set_header Upgrade $http_upgrade; proxy_set_header Connection "upgrade";
    proxy_set_header X-Forwarded-Host $host; proxy_set_header X-Forwarded-Proto $scheme; proxy_set_header X-Forwarded-Prefix /kibana;
  }
  location /actuator/health { proxy_pass http://backend/actuator/health; }
  location / { proxy_pass http://backend; }
}

server {
  listen 443 ssl http2;
  server_name dev.api.dataracy.co.kr;

  ssl_certificate     /etc/nginx/ssl/cloudflare/origin.crt;
  ssl_certificate_key /etc/nginx/ssl/cloudflare/origin.key;
  ssl_protocols TLSv1.2 TLSv1.3; ssl_prefer_server_ciphers on;

  client_max_body_size 50m; client_body_timeout 120s;

  proxy_set_header Host $host;
  proxy_set_header X-Real-IP $remote_addr;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_set_header X-Forwarded-Proto $scheme;
  proxy_http_version 1.1;
  proxy_set_header Upgrade $http_upgrade; proxy_set_header Connection "upgrade";
  proxy_set_header Cookie $http_cookie;

  location /api { proxy_pass http://backend; proxy_request_buffering off; proxy_send_timeout 300s; proxy_read_timeout 300s; }
  location /swagger-ui/    { proxy_pass http://backend/swagger-ui/;    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always; add_header Pragma "no-cache" always; expires -1; etag off; }
  location /v3/api-docs    { proxy_pass http://backend/v3/api-docs;    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always; add_header Pragma "no-cache" always; expires -1; etag off; }
  location /swagger-config { proxy_pass http://backend/swagger-config; add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always; add_header Pragma "no-cache" always; expires -1; etag off; }
  location /webjars/       { proxy_pass http://backend/webjars/;       add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0" always; add_header Pragma "no-cache" always; expires -1; etag off; }
  location /kibana/ {
    proxy_pass http://kibana_dev/kibana/;
    proxy_read_timeout 600s; proxy_send_timeout 600s;
    proxy_set_header Upgrade $http_upgrade; proxy_set_header Connection "upgrade";
    proxy_set_header X-Forwarded-Host $host; proxy_set_header X-Forwarded-Proto $scheme; proxy_set_header X-Forwarded-Prefix /kibana;
  }
  location /actuator/health { proxy_pass http://backend/actuator/health; }
  location / { proxy_pass http://backend; }
}
EOF
sed -i "s|REPLACE_BACKEND_NAME|$BACKEND_NAME|g" "$TMP"
sed -i "s|REPLACE_KIBANA_UPSTREAM|$KIBANA_UPSTREAM|g" "$TMP"
mv -f "$TMP" "$UPSTREAM_DEST"

if [ "$ALWAYS_RELOAD" = true ]; then
  docker ps --format '{{.Names}}' | grep -q "^${NGINX_SVC_NAME}$" || fail "NGINX 컨테이너가 없음"
  docker exec "$NGINX_SVC_NAME" nginx -t
  docker exec "$NGINX_SVC_NAME" nginx -s reload
else
  docker compose -f "$NGINX_COMPOSE" down -v
  docker compose -f "$NGINX_COMPOSE" up -d --build
  sleep 2
fi

PREV="backend-${CUR}"
docker stop "$PREV" || true
docker rm -f "$PREV" || true

echo "$NEXT" > "$CUR_FILE"
log "[DONE] DEV 활성: $NEXT"
