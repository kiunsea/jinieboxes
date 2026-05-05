@echo off
REM Jiniebox Gradle Deploy 배치 파일
REM 이 파일을 더블 클릭하면 Gradle deploy 작업이 실행됩니다.

echo ========================================
echo Jiniebox Gradle Deploy 시작
echo ========================================
echo.

REM 현재 스크립트가 있는 디렉토리로 이동
cd /d "%~dp0"

REM Gradle Wrapper를 사용하여 deploy 작업 실행
call gradlew.bat deploy

REM 오류 발생 시 일시 정지
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================
    echo 배포 실패! 오류 코드: %ERRORLEVEL%
    echo ========================================
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ========================================
echo 배포 완료!
echo ========================================
echo.
pause
