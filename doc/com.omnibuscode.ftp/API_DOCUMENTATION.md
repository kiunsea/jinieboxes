# Apache FTP Server 관리 클래스 API 문서

**패키지:** `com.omnibuscode.ftp`  
**버전:** 1.0  
**Apache FTP Server 버전:** 1.2.0

---

## 목차

1. [FtpCreateUser](#1-ftpcreateuser)
2. [FtpConfigManager](#2-ftpconfigmanager)
3. [FtpServerManager](#3-ftpservermanager)
4. [FtpManager](#4-ftpmanager)

---

## 1. FtpCreateUser

Apache FTP Server의 사용자 계정을 생성하고 관리하는 클래스입니다.

### 클래스 정보

```java
public class FtpCreateUser
```

### 내부 클래스

#### AccountInfo

FTP 계정 정보를 담는 데이터 클래스입니다.

```java
public static class AccountInfo
```

**필드:**
- `public String userId` - 사용자 ID
- `public String password` - 비밀번호
- `public String homeDirectory` - 홈 디렉토리 경로

**생성자:**
```java
public AccountInfo(String userId, String password, String homeDirectory)
```

**파라미터:**
- `userId` - 사용자 ID
- `password` - 비밀번호
- `homeDirectory` - 홈 디렉토리 경로

---

### 메서드

#### createUser (단일 사용자 - AccountInfo)

```java
public static boolean createUser(String ftpServerPath, AccountInfo accountInfo) throws Exception
```

AccountInfo 객체를 사용하여 단일 사용자 계정을 생성/업데이트합니다.

**파라미터:**
- `ftpServerPath` - FTP 서버 설치 경로
- `accountInfo` - 계정 정보 객체

**반환값:**
- `boolean` - 성공 시 `true`, 실패 시 `false`

**예외:**
- `Exception` - 계정 생성/업데이트 중 오류 발생

**예제:**
```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
AccountInfo account = new AccountInfo(
    "testuser",
    "password123",
    ftpServerPath + "/res/home/testuser"
);
boolean result = FtpCreateUser.createUser(ftpServerPath, account);
```

---

#### createUser (단일 사용자 - 파라미터)

```java
public static boolean createUser(String ftpServerPath, String userId, String password, String homeDirectory) throws Exception
```

파라미터로 직접 전달하여 단일 사용자 계정을 생성/업데이트합니다.

**파라미터:**
- `ftpServerPath` - FTP 서버 설치 경로
- `userId` - 사용자 ID
- `password` - 비밀번호
- `homeDirectory` - 홈 디렉토리 경로

**반환값:**
- `boolean` - 성공 시 `true`, 실패 시 `false`

**예외:**
- `Exception` - 계정 생성/업데이트 중 오류 발생

**동작:**
- 기존 사용자가 존재하면 비밀번호만 업데이트
- 새 사용자면 계정 생성 및 홈 디렉토리 자동 생성
- WritePermission 권한 자동 부여

**예제:**
```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
boolean result = FtpCreateUser.createUser(
    ftpServerPath,
    "testuser",
    "password123",
    ftpServerPath + "/res/home/testuser"
);
```

---

#### createUsers (여러 사용자)

```java
public static void createUsers(String ftpServerPath, Map<String, AccountInfo> accounts) throws Exception
```

여러 계정을 한번에 생성/업데이트하고 결과를 테이블로 출력합니다.

**파라미터:**
- `ftpServerPath` - FTP 서버 설치 경로
- `accounts` - 계정 정보 맵 (키: 사용자 ID, 값: AccountInfo)

**예외:**
- `Exception` - 계정 생성/업데이트 중 오류 발생

**동작:**
- 각 계정을 순차적으로 처리
- 진행 상황 실시간 출력
- 처리 완료 후 등록된 모든 계정 정보를 테이블 형태로 출력

**예제:**
```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";

Map<String, AccountInfo> accounts = new LinkedHashMap<>();
accounts.put("user1", new AccountInfo("user1", "pass1", ftpServerPath + "/res/home/user1"));
accounts.put("user2", new AccountInfo("user2", "pass2", ftpServerPath + "/res/home/user2"));

FtpCreateUser.createUsers(ftpServerPath, accounts);
```

---

## 2. FtpConfigManager

Apache FTP Server의 `ftpd-typical.xml` 설정 파일을 관리하는 클래스입니다.

### 클래스 정보

```java
public class FtpConfigManager
```

### 생성자

```java
public FtpConfigManager(String ftpServerPath)
```

**파라미터:**
- `ftpServerPath` - FTP 서버 설치 경로

**예제:**
```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
FtpConfigManager configManager = new FtpConfigManager(ftpServerPath);
```

---

### 메서드

#### loadConfig

```java
public void loadConfig() throws Exception
```

설정 파일을 메모리에 로드합니다.

**예외:**
- `Exception` - 파일이 존재하지 않거나 읽기 오류 발생

**예제:**
```java
configManager.loadConfig();
```

---

#### saveConfig

```java
public void saveConfig() throws Exception
```

변경된 설정을 파일에 저장합니다.

**예외:**
- `Exception` - 파일 쓰기 오류 발생

**예제:**
```java
configManager.saveConfig();
```

---

#### setServerPort

```java
public void setServerPort(int port) throws Exception
```

FTP 서버 포트 번호를 설정합니다.

**파라미터:**
- `port` - 포트 번호 (예: 21, 2121)

**예외:**
- `Exception` - 설정 노드를 찾을 수 없음

**예제:**
```java
configManager.setServerPort(2121);
```

---

#### setMaxLogins

```java
public void setMaxLogins(int maxLogins) throws Exception
```

최대 동시 로그인 수를 설정합니다.

**파라미터:**
- `maxLogins` - 최대 로그인 수 (예: 10, 20)

**예외:**
- `Exception` - 설정 노드를 찾을 수 없음

**예제:**
```java
configManager.setMaxLogins(20);
```

---

#### setAnonymousLoginEnabled

```java
public void setAnonymousLoginEnabled(boolean allow) throws Exception
```

익명 로그인 허용 여부를 설정합니다.

**파라미터:**
- `allow` - `true`: 허용, `false`: 비허용

**예외:**
- `Exception` - 설정 노드를 찾을 수 없음

**예제:**
```java
configManager.setAnonymousLoginEnabled(false);
```

---

#### setMaxUploadRate

```java
public void setMaxUploadRate(int bytesPerSec) throws Exception
```

최대 업로드 속도를 설정합니다.

**파라미터:**
- `bytesPerSec` - 초당 바이트 수 (0 = 무제한)

**예외:**
- `Exception` - 설정 노드를 찾을 수 없음

**예제:**
```java
configManager.setMaxUploadRate(0); // 무제한
// 또는
configManager.setMaxUploadRate(1048576); // 1MB/s
```

---

#### setMaxDownloadRate

```java
public void setMaxDownloadRate(int bytesPerSec) throws Exception
```

최대 다운로드 속도를 설정합니다.

**파라미터:**
- `bytesPerSec` - 초당 바이트 수 (0 = 무제한)

**예외:**
- `Exception` - 설정 노드를 찾을 수 없음

**예제:**
```java
configManager.setMaxDownloadRate(0); // 무제한
```

---

#### setIdleTime

```java
public void setIdleTime(int seconds) throws Exception
```

유휴 시간 제한을 설정합니다.

**파라미터:**
- `seconds` - 유휴 시간 (초 단위)

**예외:**
- `Exception` - 설정 노드를 찾을 수 없음

**예제:**
```java
configManager.setIdleTime(600); // 10분
```

---

#### printCurrentConfig

```java
public void printCurrentConfig() throws Exception
```

현재 설정 정보를 콘솔에 출력합니다.

**예외:**
- `Exception` - 설정 읽기 오류 발생

**출력 예시:**
```
====================================================================================================
FTP 서버 설정 정보
====================================================================================================
포트 번호: 2121
최대 로그인 수: 20
익명 로그인: false
유휴 시간 제한: 600초
최대 업로드 속도: 무제한
최대 다운로드 속도: 무제한
====================================================================================================
```

---

## 3. FtpServerManager

Apache FTP Server를 프로그래밍 방식으로 시작/정지하는 관리 클래스입니다.

### 클래스 정보

```java
public class FtpServerManager
```

### 생성자

```java
public FtpServerManager(String ftpServerPath, int port)
```

**파라미터:**
- `ftpServerPath` - FTP 서버 설치 경로
- `port` - FTP 서버 포트 번호

**예제:**
```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
int port = 2121;
FtpServerManager serverManager = new FtpServerManager(ftpServerPath, port);
```

---

### 메서드

#### startServer

```java
public void startServer() throws Exception
```

FTP 서버를 시작합니다.

**예외:**
- `Exception` - 서버 시작 중 오류 발생

**동작:**
- 이미 실행 중이면 메시지 출력 후 종료
- users.properties 파일을 로드하여 사용자 관리자 설정
- 지정된 포트에서 리스너 생성 및 시작

**예제:**
```java
serverManager.startServer();
```

---

#### stopServer

```java
public void stopServer()
```

FTP 서버를 정지합니다.

**동작:**
- 실행 중이 아니면 메시지 출력 후 종료
- 서버를 graceful하게 정지

**예제:**
```java
serverManager.stopServer();
```

---

#### restartServer

```java
public void restartServer() throws Exception
```

FTP 서버를 재시작합니다.

**예외:**
- `Exception` - 서버 재시작 중 오류 발생

**동작:**
- 서버 정지 → 2초 대기 → 서버 시작

**예제:**
```java
serverManager.restartServer();
```

---

#### isRunning

```java
public boolean isRunning()
```

FTP 서버 실행 상태를 확인합니다.

**반환값:**
- `boolean` - `true`: 실행 중, `false`: 정지 상태

**예제:**
```java
if (serverManager.isRunning()) {
    System.out.println("서버가 실행 중입니다.");
}
```

---

#### printStatus

```java
public void printStatus()
```

FTP 서버 상태 정보를 출력합니다.

**출력 예시:**
```
====================================================================================================
FTP 서버 상태
====================================================================================================
서버 상태: 실행 중
서버 경로: D:/DEV/ftp/apache-ftpserver-1.1.4
포트 번호: 2121
====================================================================================================
```

---

#### printConnectedUsers

```java
public void printConnectedUsers()
```

현재 연결된 사용자 통계를 출력합니다.

**동작:**
- 서버가 실행 중이 아니면 메시지 출력 후 종료
- 리플렉션을 사용하여 API 버전 호환성 확보
- 통계 기능을 지원하지 않으면 안내 메시지 출력

**출력 예시:**
```
=== FTP 서버 통계 ===
현재 연결된 사용자 수: 2
총 로그인 수: 15
총 업로드 파일 수: 45
총 다운로드 파일 수: 120
```

---

## 4. FtpManager

위의 세 클래스를 통합하여 관리하는 메인 클래스입니다.

### 클래스 정보

```java
public class FtpManager
```

### 생성자

```java
public FtpManager(String ftpServerPath)
```

**파라미터:**
- `ftpServerPath` - FTP 서버 설치 경로

**예제:**
```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
FtpManager ftpManager = new FtpManager(ftpServerPath);
```

---

### 메서드

#### getConfigManager

```java
public FtpConfigManager getConfigManager()
```

설정 관리자 인스턴스를 반환합니다. (싱글톤 패턴)

**반환값:**
- `FtpConfigManager` - 설정 관리자 인스턴스

**예제:**
```java
FtpConfigManager configManager = ftpManager.getConfigManager();
configManager.loadConfig();
```

---

#### getServerManager

```java
public FtpServerManager getServerManager(int port)
```

서버 관리자 인스턴스를 반환합니다. (싱글톤 패턴)

**파라미터:**
- `port` - FTP 서버 포트 번호

**반환값:**
- `FtpServerManager` - 서버 관리자 인스턴스

**예제:**
```java
FtpServerManager serverManager = ftpManager.getServerManager(2121);
serverManager.startServer();
```

---

#### initializeServer

```java
public void initializeServer(int port, Map<String, AccountInfo> accounts) throws Exception
```

FTP 서버 초기 설정을 수행합니다. (설정 파일 수정 + 계정 생성)

**파라미터:**
- `port` - FTP 서버 포트 번호
- `accounts` - 생성할 계정 정보 맵

**예외:**
- `Exception` - 초기화 중 오류 발생

**동작:**
1. 설정 파일 로드 및 수정 (포트, 최대 로그인 수 등)
2. 설정 파일 저장
3. 사용자 계정 생성
4. 결과 출력

**예제:**
```java
Map<String, AccountInfo> accounts = new LinkedHashMap<>();
accounts.put("admin", new AccountInfo("admin", "admin123", 
    ftpServerPath + "/res/home/admin"));

ftpManager.initializeServer(2121, accounts);
```

---

#### startServer

```java
public void startServer(int port) throws Exception
```

FTP 서버를 시작합니다.

**파라미터:**
- `port` - FTP 서버 포트 번호

**예외:**
- `Exception` - 서버 시작 중 오류 발생

**예제:**
```java
ftpManager.startServer(2121);
```

---

#### stopServer

```java
public void stopServer()
```

FTP 서버를 정지합니다.

**예제:**
```java
ftpManager.stopServer();
```

---

#### restartServer

```java
public void restartServer() throws Exception
```

FTP 서버를 재시작합니다.

**예외:**
- `Exception` - 서버 재시작 중 오류 발생

**예제:**
```java
ftpManager.restartServer();
```

---

#### printServerStatus

```java
public void printServerStatus()
```

서버 상태 및 연결 사용자 통계를 출력합니다.

**예제:**
```java
ftpManager.printServerStatus();
```

---

#### showInteractiveMenu

```java
public void showInteractiveMenu()
```

대화형 메뉴를 표시하고 사용자 입력을 처리합니다.

**메뉴 옵션:**
1. 서버 시작
2. 서버 정지
3. 서버 재시작
4. 서버 상태 확인
5. 설정 파일 보기
6. 설정 파일 수정
7. 사용자 계정 추가
0. 종료

**특징:**
- 무한 루프로 사용자 입력 대기
- 예외 발생 시 오류 메시지 출력 후 계속 실행
- 종료 시 실행 중인 서버 자동 정지

**예제:**
```java
ftpManager.showInteractiveMenu();
```

---

## 사용 워크플로우

### 1. 전체 초기화 및 서버 시작

```java
// 1. FtpManager 생성
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
FtpManager ftpManager = new FtpManager(ftpServerPath);

// 2. 계정 정보 준비
Map<String, AccountInfo> accounts = new LinkedHashMap<>();
accounts.put("admin", new AccountInfo(
    "admin", 
    "admin123", 
    ftpServerPath + "/res/home/admin"
));
accounts.put("user1", new AccountInfo(
    "user1", 
    "user123", 
    ftpServerPath + "/res/home/user1"
));

// 3. 서버 초기화 (설정 + 계정)
ftpManager.initializeServer(2121, accounts);

// 4. 서버 시작
ftpManager.startServer(2121);

// 5. 서버 상태 확인
ftpManager.printServerStatus();
```

---

### 2. 개별 클래스 사용

```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";

// 설정 관리
FtpConfigManager configManager = new FtpConfigManager(ftpServerPath);
configManager.loadConfig();
configManager.setServerPort(2121);
configManager.setMaxLogins(20);
configManager.saveConfig();

// 계정 생성
FtpCreateUser.createUser(ftpServerPath, "testuser", "pass123", 
    ftpServerPath + "/res/home/testuser");

// 서버 시작
FtpServerManager serverManager = new FtpServerManager(ftpServerPath, 2121);
serverManager.startServer();

// ... 작업 수행 ...

// 서버 정지
serverManager.stopServer();
```

---

### 3. 대화형 모드 사용

```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
FtpManager ftpManager = new FtpManager(ftpServerPath);

// 대화형 메뉴 시작 (사용자가 메뉴를 통해 모든 작업 수행)
ftpManager.showInteractiveMenu();
```

---

## 예외 처리

모든 주요 메서드는 `Exception`을 던질 수 있으므로 try-catch 블록으로 감싸야 합니다.

```java
try {
    FtpManager ftpManager = new FtpManager(ftpServerPath);
    ftpManager.startServer(2121);
} catch (Exception e) {
    System.err.println("오류 발생: " + e.getMessage());
    e.printStackTrace();
}
```

---

## 보안 고려사항

1. **비밀번호 평문 저장**: 현재 구현은 평문 비밀번호를 저장합니다. 운영 환경에서는 해시화를 권장합니다.

2. **포트 번호**: 1024 이하의 포트는 관리자 권한이 필요할 수 있습니다.

3. **방화벽**: FTP 포트가 방화벽에서 열려있는지 확인하세요.

4. **파일 권한**: `users.properties`와 `ftpd-typical.xml` 파일에 쓰기 권한이 있어야 합니다.

---

## 의존성

```gradle
implementation 'org.apache.ftpserver:ftpserver-core:1.2.0'
```

---

## 라이센스

이 코드는 Apache FTP Server 라이브러리를 기반으로 하며, Apache License 2.0을 따릅니다.

---

## 문의 및 지원

문제가 발생하거나 질문이 있으시면 프로젝트 리포지토리의 이슈 트래커를 이용해주세요.

