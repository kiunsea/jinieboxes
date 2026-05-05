# 변경 이력 (CHANGELOG)

프로젝트의 모든 주요 변경 사항은 이 파일에 기록됩니다.
이 프로젝트는 [Semantic Versioning (유의적 버전 관리)](https://semver.org/lang/ko/) 규칙을 따릅니다.

## 1.2.5 (2026-01-18)

### Fixed

- **자동 수집 활성화 상태와 실제 동작 불일치 문제 해결**: UI에서 체크박스가 비활성화 상태인데도 자동 수집이 계속 실행되던 문제를 해결했습니다. `FtpFileProcessorScheduler`가 DB의 `jbg_info.auto_collect_enabled` 값을 주기적으로 확인하여 활성화된 사용자가 있는 경우에만 실행하도록 개선했습니다.
- **Eclipse Tomcat 환경에서 javax.mail ClassNotFoundException 해결**: Eclipse에서 Tomcat 실행 시 `javax.mail-api-1.6.2.jar`와 `javax.mail-1.6.2.jar` 파일을 `WEB-INF/lib`에 추가하여 오류를 해결했습니다.

### Added

- **Eclipse root 프로젝트 생성**: Eclipse workspace에 root 경로(`/`) 리다이렉트용 웹 애플리케이션 프로젝트를 추가했습니다. root 경로 접근 시 `/jbs`로 자동 리다이렉트됩니다.

### Changed

- **FtpFileProcessorScheduler 자동 수집 상태 관리 개선**: DB의 `jbg_info.auto_collect_enabled` 값을 주기적으로 확인하여 실시간으로 활성화/비활성화 상태를 반영하도록 개선했습니다. 매 주기마다 DB 상태를 확인하고, 활성화된 사용자가 있는 경우에만 FTP 파일 처리를 실행합니다.

---

## 1.2.4 (2026-01-12)

### Added

- **FtpFileProcessorScheduler 싱글톤 패턴 적용**: `FtpFileProcessorScheduler` 클래스를 싱글톤 패턴으로 리팩토링하여 서버 시작 시 자동으로 초기화되도록 개선했습니다.
- **Gradle Deploy 배치 파일**: 프로젝트 루트에 `deploy.bat` 파일을 추가하여 더블 클릭으로 배포 작업을 실행할 수 있도록 개선했습니다.

### Changed

- **StartMonitors 초기화 개선**: 서버 시작 시 `FtpFileProcessorScheduler`가 자동으로 초기화되도록 개선했습니다.
- **JbgServlet 자동 수집 설정 저장 개선**: UI에서 자동 수집 활성화 체크박스 상태 변경 시 사용자별 설정(`jbg_info.auto_collect_enabled`)을 저장하도록 개선했습니다.
- **Eclipse Gradle 설정 개선**: Red Hat Java 확장 프로그램의 Gradle 초기화 스크립트 문제를 해결하기 위해 프로젝트별 설정 파일을 추가했습니다.

### Fixed

- **Eclipse에서 Gradle deploy 실행 시 오류 해결**: Red Hat Java 확장 프로그램이 존재하지 않는 init 스크립트 경로를 지정하여 발생하던 오류를 해결했습니다. Eclipse Buildship 설정에서 init 스크립트 인수를 제거하고 관련 기능을 비활성화했습니다.

---
## 1.2.3 (2026-01-06)

### Added

- **Gradle 배포 자동화 태스크**: WAR 파일 생성 후 Tomcat에 자동 배포하는 `deploy` 태스크를 추가했습니다. 심볼릭 링크 생성, WEB-INF 디렉토리 복사, 불필요한 디렉토리 삭제를 자동화했습니다.

### Changed

- **운영 서버용 FTP 전용 로그 파일 설정**: `com.omnibuscode.ftp` 및 `com.omnibuscode.logic.jbg.ftp` 패키지의 로그를 `JINIEBOX_FTP.log`에 별도로 기록하도록 설정했습니다.

### Fixed

- **JavaMail ClassNotFoundException 해결**: `javax.mail-api`는 인터페이스만 제공하므로 `com.sun.mail:javax.mail` 구현체 의존성을 추가했습니다.
- **JsonNode 타입 누락 오류 해결**: Jackson JSON 라이브러리 의존성을 추가하여 `JsonNode` 타입을 사용할 수 있도록 했습니다.
- **WAR 빌드 개선**: 중복 JAR 파일 처리 전략 설정 및 `JINIEBOX.PROPERTIES` 파일이 `WEB-INF/classes/res/`에 포함되도록 빌드 설정을 개선했습니다.

---

## 1.2.2 (2025-12-22)

### Fixed

- **FTP 파일 처리 실패 시 리소스 누수 방지**: JSON 파싱 실패 시 FileWriter 리소스가 제대로 닫히지 않을 수 있던 문제를 해결했습니다. try-with-resources를 사용하여 예외 발생 시에도 파일 리소스가 자동으로 해제되도록 개선했습니다.
- **예외 처리 개선**: JSON 파싱 실패 후 원본 텍스트 저장 시 IOException을 명시적으로 처리하여 파일 I/O 오류를 안전하게 처리하도록 개선했습니다.

---

## 1.2.1 (2025-11-14)

### Fixed

- FTP 자동 수집 파이프라인이 jangbogo에서 전달된 파일을 사용자별 `auto_collect_enabled` 상태에 맞춰 안전하게 처리하도록 업데이트했으며, jiniebox 내장 장보고에서 생성되는 FTP 업로드 JSON 포맷과 `JangbogoDataParser` 파서 요구사항을 일치시켰습니다.
- 장보고 기본 내보내기 경로가 존재하지 않을 때 Public Key 필드가 초기화되어 보이던 문제를 해결했습니다.

### Changed

- FTP 서버 패시브 포트(50000-50100) 및 외부 IP 설정을 명시할 수 있는 구조를 도입해, 포트포워딩 환경에서도 안전하게 접속 가능합니다.
- 자동 수집 즉시 실행 시 FTP 업로드용 임시 파일을 업로드 직후 삭제해 디스크 사용량을 줄였습니다.

---

## 1.2.0 (2025-11-12)

### 변경 사항 (Changed)

-   Public Key 생성 버튼을 누르는 즉시 키 쌍이 DB 및 세션에 저장되도록 변경하고, UI에서 실시간으로 갱신된 키를 보여주도록 개선했습니다.
-   FTP 계정 생성/갱신 플로우를 조정하여 사용자별 중복 주문 검사를 강화하고, 계정 정보가 즉시 FTP 서버에 반영되도록 동적 업데이트 로직을 안정화했습니다.
-   RSA 하이브리드 암복호화 임계값을 256바이트로 통일하고, 암호화/복호화 오류 로그를 보강했습니다.

### 추가 사항 (Added)

-   키 미설정 상태에서 표시되는 안내 문구, 버튼 위치 조정, 필수값 검증 등 장보고 연동 UI 개선 사항을 다수 도입했습니다.
-   빌드/배포 준비를 위한 Gretty 기반 설정 및 문서 템플릿을 정비했습니다.

### 수정 사항 (Fixed)

-   Public Key가 다른 계정으로 덮어씌워지거나 누락되는 문제를 해결했습니다.
-   FTP 정보 저장 시 `saveToJiniebox` 옵션이 꺼져 있어도 불필요하게 업로드가 발생하던 문제를 제거했습니다.
-   `mallname` 필드가 누락되어 `jbg_order.mall_name`이 `null`로 저장되던 문제를 수정했습니다.

### 제거 사항 (Removed)

-   (해당 사항 없음)

## 1.1.0 (2025-03-01)

### 변경 사항 (Changed)

-   **버전 관리 시스템을 Subversion (SVN)에서 Git으로 마이그레이션했습니다.**
    -   기존 SVN 저장소의 전체 이력을 보존하여 Git으로 이전했습니다.
    -   개발 워크플로우가 Git 기반 브랜칭 전략(예: Git Flow 또는 GitHub Flow)으로 변경됩니다.
    -   협업 및 코드 리뷰는 이제 GitHub Pull Request를 통해 진행됩니다.

### 추가 사항 (Added)

-   (여기에 새로운 기능이 있다면 추가)

### 수정 사항 (Fixed)

-   (여기에 해결된 버그가 있다면 추가)

### 제거 사항 (Removed)

-   (여기에 제거된 기능이나 파일이 있다면 추가)

## 1.0.0

-   백업 경로 : https://dev.omnibuscode.com:8443/svn/doing/trunk/boxes/jiniebox
