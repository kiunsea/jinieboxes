#!/usr/bin/env bash
# ============================================================
#  Jinieboxes 서비스 종료 — Linux/macOS
#    실행 중인 com.jiniebox.standalone.JinieboxStandalone JVM 을 찾아
#    1) graceful 종료 시도 (SIGTERM → shutdown hook 실행)
#    2) 5초 후에도 살아있으면 SIGKILL
# ============================================================
set -u

if ! command -v jps >/dev/null 2>&1; then
    echo "[ERROR] jps 가 PATH 에 없습니다. JDK 17 이상이 필요합니다." >&2
    exit 1
fi

PIDS=$(jps -l 2>/dev/null | awk '/com\.jiniebox\.standalone\.JinieboxStandalone/ {print $1}')

if [ -z "${PIDS}" ]; then
    echo "[INFO] 실행 중인 standalone 프로세스를 찾지 못했습니다."
    exit 0
fi

for PID in ${PIDS}; do
    echo "[INFO] PID ${PID} graceful 종료 시도 (SIGTERM)..."
    kill "${PID}" 2>/dev/null || true
    for i in 1 2 3 4 5; do
        sleep 1
        kill -0 "${PID}" 2>/dev/null || break
    done
    if kill -0 "${PID}" 2>/dev/null; then
        echo "[WARN] graceful 종료 실패 - 강제 종료 (SIGKILL)"
        kill -9 "${PID}" 2>/dev/null || true
    fi
    echo "[INFO] PID ${PID} 종료 완료"
done
