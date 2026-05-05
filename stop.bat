@echo off
REM ============================================================
REM  Jinieboxes 서비스 종료
REM    실행 중인 com.jiniebox.standalone.JinieboxStandalone JVM 을 찾아
REM    1) graceful 종료 시도 (shutdown hook → tomcat.stop/destroy 실행)
REM    2) 5초 후에도 살아있으면 강제 종료
REM ============================================================
setlocal enabledelayedexpansion

REM jps 가능 여부 확인 (JDK 필요)
where jps >nul 2>nul
if errorlevel 1 (
    echo [ERROR] jps 가 PATH 에 없습니다. JDK 17 이상이 필요합니다.
    exit /b 1
)

set FOUND=0
for /f "tokens=1" %%P in ('jps -l 2^>nul ^| findstr /C:"com.jiniebox.standalone.JinieboxStandalone"') do (
    set FOUND=1
    echo [INFO] PID %%P graceful 종료 시도...
    taskkill /PID %%P >nul 2>&1
    timeout /t 5 /nobreak >nul

    REM 여전히 살아있으면 강제 종료
    jps -l 2>nul | findstr /B /C:"%%P " >nul 2>&1
    if !errorlevel! EQU 0 (
        echo [WARN] graceful 종료 실패 - 강제 종료 ^(/F^)
        taskkill /F /PID %%P >nul 2>&1
    )
    echo [INFO] PID %%P 종료 완료
)

if "!FOUND!"=="0" (
    echo [INFO] 실행 중인 standalone 프로세스를 찾지 못했습니다.
)

endlocal
