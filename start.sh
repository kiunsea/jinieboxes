#!/usr/bin/env bash
# ============================================================
#  Jinieboxes 서비스 시작 (임베디드 Tomcat standalone) — Linux/macOS
#
#  사용 예:
#    ./start.sh                          기본 포트 8585 으로 시작
#    ./start.sh --port=9090              포트 변경
#    ./start.sh --config=conf/JINIEBOX.PROPERTIES
#    ./start.sh --data-dir=./mydata --port=9090
#    ./start.sh --help                   전체 옵션
# ============================================================
set -e

# 사용자가 --port 를 안 줬을 때 적용할 프로젝트 기본 포트
DEFAULT_PORT=8282

# 스크립트 위치로 이동 (어디서 실행해도 동작)
cd "$(dirname "$0")"

# 1) Java 확인
if ! command -v java >/dev/null 2>&1; then
    echo "[ERROR] Java 가 PATH 에 없습니다. JDK 17 이상을 설치하고 PATH 에 추가해 주세요." >&2
    exit 1
fi

# 2) standalone jar 위치 확인 (없으면 빌드)
JAR=$(ls build/libs/*-standalone.jar 2>/dev/null | head -n 1 || true)
if [ -z "$JAR" ]; then
    echo "[INFO] standalone jar 가 없어 빌드를 시작합니다 (최초 1회, 수 분 소요)..."
    ./gradlew standaloneJar --console=plain
    JAR=$(ls build/libs/*-standalone.jar 2>/dev/null | head -n 1 || true)
fi
if [ -z "$JAR" ]; then
    echo "[ERROR] standalone jar 를 찾을 수 없습니다 (build/libs/)." >&2
    exit 1
fi

# 3) 사용자가 --port 를 안 줬으면 DEFAULT_PORT 추가
PORT_ARG=""
case " $* " in
    *" --port="*) ;;
    *) PORT_ARG="--port=${DEFAULT_PORT}" ;;
esac

# 4) 실행
cat <<EOF

============================================================
  JAR  : ${JAR}
  ARGS : ${PORT_ARG} $*
============================================================

EOF

exec java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 \
    -jar "${JAR}" ${PORT_ARG} "$@"
