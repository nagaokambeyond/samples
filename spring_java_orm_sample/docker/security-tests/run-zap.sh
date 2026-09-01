#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
RESULTS_DIR="${SCRIPT_DIR}/results"
APP_LOG_FILE="${RESULTS_DIR}/app.log"
COMPOSE_FILE="${SCRIPT_DIR}/compose.yaml"
ZAP_AUTOMATION_TEMPLATE="${SCRIPT_DIR}/zap-api.yaml"
ZAP_AUTOMATION_RUN_FILE="${RESULTS_DIR}/zap-api.run.yaml"

PERSISTENCE_PROFILE="${PERSISTENCE_PROFILE:-${APP_PROFILE:-doma}}"
APP_PORT="${APP_PORT:-18080}"
APP_HOST="${APP_HOST:-127.0.0.1}"
APP_USERNAME="${APP_USERNAME:-admin}"
APP_PASSWORD="${APP_PASSWORD:-password}"
APP_START_TIMEOUT_SECONDS="${APP_START_TIMEOUT_SECONDS:-90}"
ZAP_MAX_SCAN_DURATION_MINS="${ZAP_MAX_SCAN_DURATION_MINS:-30}"

LOCAL_BASE_URL="${LOCAL_BASE_URL:-http://${APP_HOST}:${APP_PORT}}"
ZAP_TARGET_URL="${ZAP_TARGET_URL:-http://host.docker.internal:${APP_PORT}}"
ZAP_OPENAPI_URL="${ZAP_OPENAPI_URL:-${ZAP_TARGET_URL}/v3/api-docs}"
ZAP_AUTH_HEADER="${ZAP_AUTH_HEADER:-Authorization}"
ZAP_AUTH_HEADER_SITE="${ZAP_AUTH_HEADER_SITE:-host.docker.internal}"

APP_PID=""

json_escape() {
  printf '%s' "$1" | sed 's/\\/\\\\/g; s/"/\\"/g'
}

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command is not available: $1" >&2
    exit 1
  fi
}

cleanup() {
  if [[ -n "${APP_PID}" ]] && kill -0 "${APP_PID}" >/dev/null 2>&1; then
    kill "${APP_PID}" >/dev/null 2>&1 || true
    wait "${APP_PID}" >/dev/null 2>&1 || true
  fi
}

wait_for_app() {
  local waited_seconds=0
  until curl --fail --silent --show-error "${LOCAL_BASE_URL}/v3/api-docs" >/dev/null; do
    if [[ -n "${APP_PID}" ]] && ! kill -0 "${APP_PID}" >/dev/null 2>&1; then
      echo "Spring Boot application exited before it became ready. See ${APP_LOG_FILE}." >&2
      tail -n 80 "${APP_LOG_FILE}" >&2 || true
      exit 1
    fi
    if (( waited_seconds >= APP_START_TIMEOUT_SECONDS )); then
      echo "Timed out waiting for ${LOCAL_BASE_URL}/v3/api-docs. See ${APP_LOG_FILE}." >&2
      tail -n 80 "${APP_LOG_FILE}" >&2 || true
      exit 1
    fi
    sleep 1
    waited_seconds=$((waited_seconds + 1))
  done
}

ensure_target_port_is_available() {
  if curl --fail --silent "${LOCAL_BASE_URL}/v3/api-docs" >/dev/null; then
    echo "Refusing to scan: ${LOCAL_BASE_URL}/v3/api-docs is already responding before this script started the app." >&2
    echo "Stop the existing process or choose another APP_PORT." >&2
    exit 1
  fi

  if command -v lsof >/dev/null 2>&1 && lsof -nP -iTCP:"${APP_PORT}" -sTCP:LISTEN >/dev/null 2>&1; then
    echo "Refusing to start: port ${APP_PORT} is already in use." >&2
    echo "Stop the existing process or choose another APP_PORT." >&2
    exit 1
  fi
}

extract_access_token() {
  sed -n 's/.*"accessToken"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p'
}

normalize_sarif_report() {
  local sarif_json="${RESULTS_DIR}/zap-api-sarif.json"
  local sarif="${RESULTS_DIR}/zap-api.sarif"

  if [[ -s "${sarif_json}" ]]; then
    cp "${sarif_json}" "${sarif}"
  elif [[ -s "${sarif}" ]]; then
    return 0
  else
    echo "ZAP SARIF report was not generated at ${sarif_json}." >&2
    return 1
  fi
}

require_command curl
require_command docker
require_command java

case "${ZAP_MAX_SCAN_DURATION_MINS}" in
  ''|*[!0-9]*)
    echo "ZAP_MAX_SCAN_DURATION_MINS must be an integer: ${ZAP_MAX_SCAN_DURATION_MINS}" >&2
    exit 1
    ;;
esac

mkdir -p "${RESULTS_DIR}"
sed "s/maxScanDurationInMins: 30/maxScanDurationInMins: ${ZAP_MAX_SCAN_DURATION_MINS}/" \
  "${ZAP_AUTOMATION_TEMPLATE}" > "${ZAP_AUTOMATION_RUN_FILE}"
trap cleanup EXIT INT TERM

cd "${REPO_ROOT}"
./gradlew bootJar

ensure_target_port_is_available

: > "${APP_LOG_FILE}"
java -jar "${REPO_ROOT}/build/libs/demo-0.0.1-SNAPSHOT.jar" \
  --spring.profiles.active="${PERSISTENCE_PROFILE},dast" \
  --server.address=0.0.0.0 \
  --server.port="${APP_PORT}" \
  > "${APP_LOG_FILE}" 2>&1 &
APP_PID=$!

wait_for_app

login_username="$(json_escape "${APP_USERNAME}")"
login_password="$(json_escape "${APP_PASSWORD}")"
login_response="$(
  curl --fail --silent --show-error \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"${login_username}\",\"password\":\"${login_password}\"}" \
    "${LOCAL_BASE_URL}/api/auth/login"
)"
access_token="$(printf '%s' "${login_response}" | extract_access_token)"

if [[ -z "${access_token}" ]]; then
  echo "Failed to extract accessToken from login response." >&2
  exit 1
fi

curl --fail --silent --show-error \
  -X POST \
  -H "${ZAP_AUTH_HEADER}: Bearer ${access_token}" \
  "${LOCAL_BASE_URL}/api/auth/login-rate-limit/reset" \
  -o /dev/null

export ZAP_AUTH_HEADER
export ZAP_AUTH_HEADER_VALUE="Bearer ${access_token}"
export ZAP_AUTH_HEADER_SITE
export ZAP_TARGET_URL
export ZAP_OPENAPI_URL
export ZAP_MAX_SCAN_DURATION_MINS

set +e
docker compose -f "${COMPOSE_FILE}" run --rm zap \
  zap.sh -cmd -autorun /zap/wrk/results/zap-api.run.yaml
zap_status=$?
set -e

normalize_sarif_report || true

exit "${zap_status}"
