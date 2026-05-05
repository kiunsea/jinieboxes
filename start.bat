@echo off
chcp 65001 >nul
REM ============================================================
REM  Jinieboxes 서비스 시작 (임베디드 Tomcat standalone)
REM
REM  사용 예:
REM    start.bat                          기본 포트 8080 으로 시작
REM    start.bat --port=9090              포트 변경
REM    start.bat --config=conf\JINIEBOX.PROPERTIES
REM    start.bat --data-dir=.\mydata --port=9090
REM    start.bat --help                   전체 옵션
REM ============================================================
setlocal enabledelayedexpansion

cd /d "%~dp0"

REM 1) Java 확인
where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Java 가 PATH 에 없습니다. JDK 17 이상을 설치하고 PATH 에 추가해 주세요.
    exit /b 1
)

REM 2) standalone jar 위치 확인 (없으면 빌드)
set JAR=
for %%F in (build\libs\*-standalone.jar) do set JAR=%%F

if not defined JAR (
    echo [INFO] standalone jar 가 없어 빌드를 시작합니다 ^(최초 1회, 수 분 소요^)...
    call gradlew.bat standaloneJar --console=plain
    if errorlevel 1 (
        echo [ERROR] 빌드 실패
        exit /b 1
    )
    for %%F in (build\libs\*-standalone.jar) do set JAR=%%F
)

if not defined JAR (
    echo [ERROR] standalone jar 를 찾을 수 없습니다 ^(build\libs\^).
    exit /b 1
)

REM 3) 실행
echo.
echo ============================================================
echo   JAR  : !JAR!
echo   ARGS : %*
echo ============================================================
echo.

java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -jar "!JAR!" %*

endlocal
