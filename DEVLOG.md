# DevLog: JinieBox 작업 이력

## 개요

이 DevLog는 JinieBox 프로젝트의 작업 이력을 기록합니다.

**작업 기록 형식**: 각 작업은 `YYYY-MM-DD HH:MM` 형식의 일자로 기록됩니다.

---

## 주요 변경사항

### 2026-01-18 - 자동 수집 활성화 상태 관리 개선 및 Eclipse 개발 환경 설정

#### 버그 수정

- **자동 수집 활성화 상태와 실제 동작 불일치 문제 해결**
  - 문제: UI에서 '장보고 FTP 파일 자동 수집 활성화' 체크박스가 disabled 상태인데도 자동 수집이 계속 실행됨
  - 원인: `FtpFileProcessorScheduler`에 enabled 상태 관리 로직이 없어서 DB 값과 관계없이 무조건 실행됨
  - 해결:
    - `FtpFileProcessorScheduler`에 `enabled` 변수 추가
    - `loadEnabledFromDatabase()` 메서드 추가: DB의 `jbg_info.auto_collect_enabled` 값을 확인하여 활성화된 사용자가 있는지 체크
    - TimerTask의 `run()` 메서드에서 매 주기마다 DB 상태를 확인하고, `enabled = true`인 경우에만 FTP 파일 처리 실행
    - `setEnabled(boolean enabled)`, `isEnabled()` 메서드 추가
    - `StartMonitors.init()`에서 서버 시작 시 DB에서 초기 enabled 상태 로드
    - `JbgServlet.toggleAutoCollect()`에서 체크박스 상태 변경 시 스케줄러의 enabled 상태도 업데이트

- **Eclipse Tomcat 환경에서 javax.mail ClassNotFoundException 해결**
  - 오류: `java.lang.ClassNotFoundException: javax.mail.internet.AddressException`
  - 원인: Eclipse에서 Tomcat 실행 시 Gradle 의존성이 자동으로 `WEB-INF/lib`에 포함되지 않음
  - 해결: `javax.mail-api-1.6.2.jar`와 `javax.mail-1.6.2.jar` 파일을 `src/main/webapp/WEB-INF/lib` 디렉토리에 직접 추가

- **Eclipse root 프로젝트 Jakarta Servlet API 오류 해결**
  - 오류: `The default superclass, "jakarta.servlet.http.HttpServlet", according to the project's Dynamic Web Module facet version (5.0), was not found on the Java Build Path.`
  - 원인: Dynamic Web Module facet 버전과 `web.xml` 버전 불일치, Jakarta Servlet API JAR 누락
  - 해결:
    - `jakarta.servlet-api-5.0.0.jar`를 `WEB-INF/lib`에 추가
    - Dynamic Web Module facet 버전을 5.0으로 변경
    - `.classpath`에 Jakarta Servlet API JAR 명시적 추가

#### 새로운 기능

- **Eclipse root 프로젝트 생성**
  - Eclipse workspace에 root 경로(`/`) 리다이렉트용 웹 애플리케이션 프로젝트 생성
  - 프로젝트 위치: `D:\DEV\eclipse\eclipse-workspace\root`
  - 기능: root 경로 접근 시 `/jbs`로 자동 리다이렉트
  - Context Path: `/` (ROOT)로 설정
  - Jakarta EE 5.0 (Tomcat 11 호환) 설정

#### 변경사항

- **FtpFileProcessorScheduler 자동 수집 상태 관리 개선**
  - DB의 `jbg_info.auto_collect_enabled` 값을 주기적으로 확인하여 활성화된 사용자가 있는 경우에만 실행
  - `auto_collect_enabled = 1`인 사용자가 하나라도 있으면 스케줄러가 활성화됨
  - 매 주기마다 DB 상태를 확인하여 실시간으로 활성화/비활성화 상태 반영
  - 로그에 활성화된 사용자 수와 상태 변경 정보 기록

- **StartMonitors 초기화 개선**
  - 서버 시작 시 DB에서 자동 수집 활성화 상태를 로드하여 스케줄러 초기화
  - 초기화 시 활성화/비활성화 상태를 로그에 기록

- **JbgServlet 자동 수집 설정 저장 개선**
  - UI에서 체크박스 상태 변경 시 DB에 저장한 후, 스케줄러의 enabled 상태도 즉시 업데이트
  - DB에서 최신 상태를 다시 확인하여 다른 사용자가 변경한 경우도 반영

#### 수정된 파일 목록

- `src/main/java/com/omnibuscode/logic/jbg/ftp/FtpFileProcessorScheduler.java`: enabled 상태 관리 로직 추가
  - `enabled` 변수 추가
  - `loadEnabledFromDatabase()` 메서드 추가
  - `setEnabled(boolean enabled)`, `isEnabled()` 메서드 추가
  - TimerTask의 `run()` 메서드에서 enabled 체크 로직 추가
- `src/main/java/com/omnibuscode/base/StartMonitors.java`: DB에서 초기 enabled 상태 로드
- `src/main/java/com/omnibuscode/ctrl/JbgServlet.java`: `toggleAutoCollect()`에서 `setEnabled()` 호출 추가
- `src/main/webapp/WEB-INF/lib/javax.mail-api-1.6.2.jar`: Eclipse Tomcat 환경용 추가
- `src/main/webapp/WEB-INF/lib/javax.mail-1.6.2.jar`: Eclipse Tomcat 환경용 추가
- `D:\DEV\eclipse\eclipse-workspace\root/`: root 리다이렉트 프로젝트 생성
  - `.project`, `.classpath`, `.settings/` 파일
  - `src/main/webapp/WEB-INF/web.xml`
  - `src/main/webapp/index.jsp` (리다이렉트 로직)
  - `src/main/webapp/WEB-INF/lib/jakarta.servlet-api-5.0.0.jar`

#### 테스트

- **기능 테스트**
  - UI에서 체크박스 비활성화 시 자동 수집이 중지되는지 확인
  - UI에서 체크박스 활성화 시 자동 수집이 시작되는지 확인
  - DB의 `auto_collect_enabled` 값이 올바르게 저장/조회되는지 확인
  - 여러 사용자 중 하나라도 활성화되어 있으면 스케줄러가 실행되는지 확인

- **Eclipse 환경 테스트**
  - Eclipse에서 Tomcat 실행 시 javax.mail 관련 오류 없이 정상 실행되는지 확인
  - root 프로젝트가 정상적으로 리다이렉트되는지 확인

#### 배포 정보

- **버전**: 1.2.4 (유지)
- **배포 방법**: `gradle deploy` 또는 `deploy.bat` 더블 클릭
- **배포 전 확인사항**:
  - `FtpFileProcessorScheduler`가 DB의 `auto_collect_enabled` 값을 올바르게 확인하는지 확인
  - UI에서 체크박스 상태 변경 시 자동 수집이 올바르게 활성화/비활성화되는지 확인

---

### 2026-01-12 - FTP 파일 처리 스케줄러 개선 및 개발 환경 설정 개선

#### 새로운 기능

- **FtpFileProcessorScheduler 싱글톤 패턴 적용**
  - `FtpFileProcessorScheduler` 클래스를 싱글톤 패턴으로 리팩토링
  - `getInstance()` 메서드를 통한 인스턴스 접근
  - 서버 시작 시 자동으로 초기화되도록 `StartMonitors`에 통합

- **Gradle Deploy 배치 파일 추가**
  - 프로젝트 루트에 `deploy.bat` 파일 생성
  - 더블 클릭으로 Gradle deploy 작업 실행 가능
  - 오류 발생 시 오류 코드 표시 및 일시 정지
  - 성공 시에도 결과 확인을 위한 일시 정지

#### 변경사항

- **StartMonitors 초기화 개선**
  - `StartMonitors.init()`에서 `FtpFileProcessorScheduler` 자동 초기화
  - 초기화 실패 시 에러 로그 기록

- **JbgServlet 자동 수집 설정 저장 개선**
  - UI에서 '장보고 FTP 파일 자동 수집 활성화' 체크박스 상태 변경 시 사용자별 설정(`jbg_info.auto_collect_enabled`)을 저장하도록 개선

- **Eclipse Gradle 설정 개선**
  - Red Hat Java 확장 프로그램의 Gradle 초기화 스크립트 문제 해결
  - `.vscode/settings.json` 파일 생성:
    - Red Hat Java 확장 프로그램의 Gradle 관련 기능 비활성화
    - Gradle 초기화 스크립트 경로 제거
    - 자동 빌드 및 설정 업데이트 비활성화
  - `.settings/org.eclipse.buildship.core.prefs` 수정:
    - `arguments` 라인에서 init 스크립트 인수 제거
    - `override.workspace.settings=false`로 변경하여 워크스페이스 설정 우선 적용
  - `.settings/org.eclipse.jdt.ls.core.prefs` 파일 생성:
    - Red Hat Java Language Server의 Gradle 관련 기능 비활성화
  - 빈 `init.gradle` 및 `protobuf/init.gradle` 파일 생성으로 오류 방지

#### 버그 수정

- **Eclipse에서 Gradle deploy 실행 시 오류 해결**
  - 오류: `The specified initialization script '...\init.gradle' does not exist`
  - 원인: Red Hat Java 확장 프로그램이 존재하지 않는 init 스크립트 경로를 지정
  - 해결:
    - Eclipse Buildship 설정에서 init 스크립트 인수 제거
    - Red Hat Java 확장 프로그램의 Gradle 관련 기능 비활성화
    - 빈 init 스크립트 파일 생성으로 오류 방지

#### 개선사항

- **개발 환경 설정 개선**
  - Cursor/VSCode와 Eclipse에서 모두 Gradle 빌드가 정상 작동하도록 설정
  - Red Hat Java 확장 프로그램의 간섭 최소화
  - 프로젝트별 설정 파일로 개발 환경 독립성 확보

- **배포 프로세스 개선**
  - `deploy.bat` 파일로 배포 작업 간소화
  - 더블 클릭으로 배포 실행 가능
  - 배포 결과 확인을 위한 일시 정지 기능

#### 수정된 파일 목록

- `src/main/java/com/omnibuscode/logic/jbg/ftp/FtpFileProcessorScheduler.java`: 싱글톤 패턴 적용
- `src/main/java/com/omnibuscode/base/StartMonitors.java`: FtpFileProcessorScheduler 자동 초기화
- `deploy.bat`: Gradle deploy 배치 파일 생성
- `.vscode/settings.json`: Red Hat Java 확장 프로그램 비활성화 설정
- `.vscode/extensions.json`: Red Hat Java 확장 프로그램 비원하는 확장 프로그램으로 표시
- `.settings/org.eclipse.buildship.core.prefs`: Eclipse Buildship 설정에서 init 스크립트 제거
- `.settings/org.eclipse.jdt.ls.core.prefs`: Red Hat Java Language Server 설정 추가

#### 테스트

- **빌드 테스트**
  - `gradle deploy`: Gradle deploy 작업 실행 성공
  - Eclipse에서 Gradle deploy 실행 성공 (init 스크립트 오류 해결)
  - `deploy.bat` 더블 클릭 실행 성공

- **기능 테스트**
  - `StartMonitors` 초기화 시 `FtpFileProcessorScheduler` 자동 시작 확인

#### 배포 정보

- **버전**: 1.2.3 (유지)
- **배포 방법**: `gradle deploy` 또는 `deploy.bat` 더블 클릭
- **배포 전 확인사항**:
  - `FtpFileProcessorScheduler`가 서버 시작 시 자동 초기화되는지 확인

---

### 2026-01-06 00:00 - 버전 1.2.3 배포 준비 및 빌드/배포 시스템 개선

#### 새로운 기능

- **FTP 파일 처리 실패 시 복호화된 JSON 파일 저장 기능**
  - FTP 업로드 파일 처리 실패 시, 복호화된 내용을 Pretty Print 형식의 JSON 파일로 저장
  - 저장 위치: `failed` 디렉토리
  - 파일명 형식: 원본 파일명에서 `.encrypted.failed.log` 제거 후 `.json` 확장자 추가
  - JSON 파싱 실패 시 원본 텍스트를 그대로 저장하여 디버깅 용이성 향상

- **Gradle 배포 자동화 태스크 (`deploy`)**
  - WAR 파일 생성 후 자동 배포 프로세스 구현
  - 배포 단계:
    1. WAR 파일 압축 해제하여 Tomcat webapps 디렉토리에 배포
    2. `user_files` 심볼릭 링크 생성 (Windows junction)
    3. `gradle/deploy/WEB-INF` 디렉토리 내용을 배포된 웹앱에 복사
    4. 불필요한 디렉토리 삭제 (`.vscode`, `dev`, `bak`, `WEB-INF/classes/webapp`, `WEB-INF/classes/jiniebox`)
  - 환경 변수 또는 `gradle.properties`를 통한 경로 설정 지원
  - 설정 가능한 경로:
    - `tomcat.path`: Apache Tomcat 설치 경로
    - `user.files.path`: user_files 소스 경로
    - `deploy.webinf.path`: 배포용 WEB-INF 디렉토리 경로

- **운영 서버용 FTP 전용 로그 파일 설정**
  - `com.omnibuscode.ftp` 패키지 로그를 `JINIEBOX_FTP.log`에 별도 기록
  - `com.omnibuscode.logic.jbg.ftp` 패키지 로그도 FTP 전용 로그 파일에 기록
  - 개발 환경과 운영 환경의 로그 설정 분리

#### 변경사항

- **의존성 추가**
  - `com.sun.mail:javax.mail:1.6.2`: JavaMail API 구현체 추가 (기존 `javax.mail-api`는 인터페이스만 제공)
  - Jackson JSON 라이브러리 추가:
    - `com.fasterxml.jackson.core:jackson-core:2.16.1`
    - `com.fasterxml.jackson.core:jackson-databind:2.16.1`
    - `com.fasterxml.jackson.core:jackson-annotations:2.16.1`

- **Gradle 빌드 설정 개선**
  - `sourceSets` 설정 변경:
    - `src/main/java/res` 디렉토리를 소스에서 제외하고 리소스로만 사용
    - `log4j2.xml`을 소스에서 제외하고 리소스로만 사용
    - 리소스 디렉토리에 `src/main/java` 추가하여 `log4j2.xml`과 `res/**` 포함
  - WAR 태스크 개선:
    - `duplicatesStrategy = DuplicatesStrategy.EXCLUDE` 설정으로 중복 JAR 파일 처리
    - `src/main/java/res`의 파일들을 `WEB-INF/classes/res/`에 포함하도록 명시적 설정

- **로그 설정 분리**
  - 개발 환경용: `src/main/java/log4j2.xml` (기존 설정 유지)
  - 운영 환경용: `gradle/deploy/WEB-INF/classes/log4j2.xml` (FTP 전용 로그 파일 설정 추가)

#### 버그 수정

- **JavaMail ClassNotFoundException 해결**
  - `javax.mail.internet.AddressException` 클래스를 찾을 수 없는 오류 해결
  - 원인: `javax.mail-api`는 인터페이스만 제공하고 구현체가 없음
  - 해결: `com.sun.mail:javax.mail` 구현체 의존성 추가

- **JsonNode 타입 누락 오류 해결**
  - `JangbogoOrderImporter.importOrders(JsonNode, String)` 메서드에서 `JsonNode` 타입을 찾을 수 없는 오류 해결
  - 해결: Jackson JSON 라이브러리 의존성 추가

- **JangbogoDataParserTest 컴파일 오류 해결**
  - `decryptor cannot be resolved` 오류 해결
  - 해결: 테스트 메서드 내에서 `FtpFileDecryptor` 인스턴스 생성 및 초기화

- **store_jbg.jsp 페이지 접속 실패 해결**
  - `JbgServlet.java` 94번 라인에서 모든 명령어를 무조건 차단하던 문제 해결
  - 원인: `connectToMall`과 `fetchOrders`만 차단해야 하는데 모든 명령어를 차단
  - 해결: 조건부로 특정 명령어만 차단하도록 수정 (`getJangbogoPublicKey` 등 다른 명령어는 정상 동작)

- **WAR 빌드 시 중복 파일 오류 해결**
  - `Entry WEB-INF/lib/commons-fileupload-1.5.jar is a duplicate` 오류 해결
  - 해결: WAR 태스크에 `duplicatesStrategy = DuplicatesStrategy.EXCLUDE` 설정 추가

- **JINIEBOX.PROPERTIES 파일 누락 문제 해결**
  - WAR 파일에 `JINIEBOX.PROPERTIES` 파일이 포함되지 않던 문제 해결
  - 해결: `sourceSets` 설정 변경 및 WAR 태스크에 명시적 복사 설정 추가

- **소스 디렉토리 중첩 오류 해결**
  - `Cannot nest 'jiniebox/src/main/java/res' inside 'jiniebox/src/main/java'` 오류 해결
  - 해결: `sourceSets`에서 `res/**`를 소스 디렉토리에서 명시적으로 제외

- **FTP 파일 처리 실패 시 리소스 누수 방지**
  - JSON 파싱 실패 시 `FileWriter` 리소스가 제대로 닫히지 않을 수 있던 문제 해결
  - 해결: `try-with-resources`를 사용하여 예외 발생 시에도 파일 리소스가 자동으로 해제되도록 개선
  - 중첩된 `try-with-resources` 블록으로 JSON 파싱 실패 후 원본 텍스트 저장 시에도 리소스 안전성 보장

#### 개선사항

- **예외 처리 개선**
  - JSON 파싱 실패 후 원본 텍스트 저장 시 `IOException`을 명시적으로 처리
  - 파일 I/O 오류를 안전하게 처리하도록 개선

- **AI 패키지 관련 컴파일 오류 해결**
  - `com.omnibuscode.ai` 패키지가 없어 발생하는 컴파일 오류 해결
  - 영향받은 파일:
    - `UserSession.java`: `ChatRoom` 타입을 `Object`로 변경
    - `ChatbotServlet.java`: AI 관련 코드 주석 처리 및 경고 메시지로 대체
    - `CreateOnboardingLink.java`: `ProcessFunction` 구현 주석 처리

- **배포 프로세스 개선**
  - 수동 배포 스크립트를 Gradle 태스크로 통합
  - 배포 단계를 명확하게 구분하고 로그 출력으로 진행 상황 확인 가능
  - 환경별 경로 설정을 `gradle.properties`로 관리 가능

#### 수정된 파일 목록

- `build.gradle`: 의존성 추가, sourceSets 설정, WAR 태스크 개선, deploy 태스크 추가
- `src/main/java/com/omnibuscode/logic/jbg/ftp/FtpFileMonitor.java`: 복호화된 JSON 저장 기능 추가, 리소스 누수 방지 개선
- `src/main/java/com/omnibuscode/ctrl/JbgServlet.java`: 조건부 명령어 차단 로직 수정
- `src/main/java/com/omnibuscode/base/UserSession.java`: ChatRoom 타입을 Object로 변경
- `src/main/java/com/omnibuscode/ctrl/ChatbotServlet.java`: AI 관련 코드 주석 처리
- `src/main/java/com/omnibuscode/logic/chatgpt/CreateOnboardingLink.java`: ProcessFunction 구현 주석 처리
- `src/test/java/com/omnibuscode/logic/jbg/ftp/JangbogoDataParserTest.java`: decryptor 변수 선언 및 초기화
- `src/main/java/log4j2.xml`: 개발 환경용 설정 (기존 유지)
- `gradle/deploy/WEB-INF/classes/log4j2.xml`: 운영 환경용 설정 (FTP 전용 로그 추가)
- `CHANGELOG.md`: 버전 1.2.2 변경사항 기록

#### 테스트

- **빌드 테스트**
  - `gradle build`: 컴파일 및 WAR 파일 생성 성공
  - `gradle war`: WAR 파일 생성 및 중복 파일 처리 확인
  - `gradle deploy`: 배포 태스크 실행 및 배포 디렉토리 구조 확인

- **기능 테스트**
  - FTP 파일 처리 실패 시 복호화된 JSON 파일 저장 기능 확인 필요
  - 운영 서버 배포 후 FTP 로그 파일 분리 확인 필요

#### 배포 정보

- **버전**: 1.2.2
- **배포 방법**: `gradle deploy` 태스크 실행
- **배포 전 확인사항**:
  - `gradle.properties` 파일에 배포 경로 설정 확인
  - 또는 환경 변수로 경로 설정 확인
- **배포 후 확인사항**:
  - `webapps/jbs/WEB-INF/classes/res/JINIEBOX.PROPERTIES` 파일 존재 확인
  - `webapps/jbs/user_files` 심볼릭 링크 생성 확인
  - `webapps/jbs/WEB-INF/classes/log4j2.xml` 파일이 운영용 설정으로 교체되었는지 확인

---

## 테스트

### 단위 테스트

### 통합 테스트

### 빌드 테스트

---

## 체크리스트

---

## 관련 이슈

---

## 참고 문서

---

## 배포 정보

---

## 통계

---

## 리뷰 요청사항

---

## 향후 계획

---

## 기타 참고사항

### Breaking Changes

### Migration Guide

### Known Issues

---

## 작업 기록 형식 가이드

새로운 작업을 추가할 때는 다음 형식을 사용하세요:

```markdown
### YYYY-MM-DD - 작업 제목

#### 새로운 기능
- 기능 설명

#### 변경사항
- 변경 내용

#### 버그 수정
- 수정 내용

#### 개선사항
- 개선 내용
```

**참고**: 
- 날짜 형식: `YYYY-MM-DD HH:MM` (예: 2026-01-15 14:30)
- 작업은 날짜순으로 정렬 (최신 작업이 위에)

