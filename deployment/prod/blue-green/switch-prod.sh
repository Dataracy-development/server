#!/bin/bash
set -Eeuo pipefail
log(){ echo "[$(date -u +%FT%TZ)] $*"; }
fail(){ log "[ERROR] $*"; exit 1; }

exec 9>/tmp/dataracy.deploy.lock
flock -n 9 || fail "다른 배포 중"
trap 'fail "중단(line:$LINENO)"' ERR

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
DOCKER_DIR="$SCRIPT_DIR/../docker"
NGINX_DIR="$SCRIPT_DIR/../nginx"
STATE_DIR="/home/ubuntu/color-config"
CUR_FILE="$STATE_DIR/current_color_prod"

mkdir -p "$STATE_DIR"; [[ -f "$CUR_FILE" ]] || echo "blue" > "$CUR_FILE"
CUR="$(tr -d '[:space:]' < "$CUR_FILE")"; [[ "$CUR" =~ ^(blue|green)$ ]] || fail "색상 오류:$CUR"
NEXT=$([[ "$CUR" == "blue" ]] && echo green || echo blue)
NEXT_COMPOSE="$DOCKER_DIR/docker-compose-${NEXT}-prod.yml"
BACKEND="backend-prod-${NEXT}"

log "[DEPLOY] PROD $CUR -> $NEXT"
docker compose -f "$NEXT_COMPOSE" up -d --pull always
for i in {1..20}; do
  s="$(docker inspect --format='{{json .State.Health.Status}}' "$BACKEND" 2>/dev/null || echo null)"
  [[ "$s" == "\"healthy\"" ]] && { log "healthy"; break; } || { log "  [$i/20] $s"; sleep 5; }
done
[[ "$s" == "\"healthy\"" ]] || fail "$BACKEND healthy 실패"

UPSTREAM="$NGINX_DIR/upstream-blue-green-prod.conf"
TMP="$UPSTREAM.tmp"
cat > "$TMP" <<'EOF'
upstream backend_prod { server REPLACE_BACKEND:8080; }
server { listen 80; server_name api.dataracy.co.kr; return 301 https://$host$request_uri; }
server {
  listen 443 ssl http2;
  server_name api.dataracy.co.kr;
  ssl_certificate /etc/nginx/ssl/cloudflare/origin.crt;
  ssl_certificate_key /etc/nginx/ssl/cloudflare/origin.key;
  ssl_protocols TLSv1.2 TLSv1.3; ssl_prefer_server_ciphers on;
  add_header Strict-Transport-Security "max-age=31536000" always;
  client_max_body_size 50m; client_body_timeout 120s;

  proxy_set_header Host $host;
  proxy_set_header X-Real-IP $remote_addr;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_set_header X-Forwarded-Proto $scheme;
  proxy_http_version 1.1;
  proxy_set_header Upgrade $http_upgrade;
  proxy_set_header Connection "upgrade";

  location /api { proxy_pass http://backend_prod; proxy_request_buffering off; proxy_send_timeout 300s; proxy_read_timeout 300s; }
  location /actuator/health { proxy_pass http://backend_prod/actuator/health; }
  location / { proxy_pass http://backend_prod; }
}
EOF
sed -i "s|REPLACE_BACKEND|$BACKEND|g" "$TMP"; mv -f "$TMP" "$UPSTREAM"

docker compose -f "$DOCKER_DIR/docker-compose-nginx-prod.yml" down -v
docker compose -f "$DOCKER_DIR/docker-compose-nginx-prod.yml" up -d --build

PREV="backend-prod-$CUR"; docker rm -f "$PREV" || true
echo "$NEXT" > "$CUR_FILE"
log "[DONE] PROD 활성: $NEXT"
