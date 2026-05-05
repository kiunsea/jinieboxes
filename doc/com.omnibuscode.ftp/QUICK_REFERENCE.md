# Apache FTP Server 관리 클래스 빠른 참조 가이드

**패키지:** `com.omnibuscode.ftp`

---

## 📦 클래스 개요

| 클래스 | 주요 역할 | 핵심 메서드 |
|--------|-----------|-------------|
| **FtpServerManager** | 서버 시작/정지 | `startServer()`, `stopServer()` |
| **FtpUserManager** | 사용자 계정 관리 | `createUser()`, `createUsers()`, `deleteUser()`, `deleteUsers()` |
| **FtpConfigManager** | 설정 파일 관리 | `setServerPort()`, `setMaxLogins()` |

---

## 🚀 빠른 시작

### 1. 단일 사용자 생성

```java
FtpUserManager.createUser(
    "D:/DEV/ftp/apache-ftpserver-1.1.4",
    "username",
    "password",
    "D:/DEV/ftp/apache-ftpserver-1.1.4/res/home/username"
);
```

### 2. 서버 설정 변경

```java
FtpConfigManager config = new FtpConfigManager("D:/DEV/ftp/apache-ftpserver-1.1.4");
config.loadConfig();
config.setServerPort(2121);
config.saveConfig();
```

### 3. 서버 시작

```java
FtpServerManager server = new FtpServerManager("D:/DEV/ftp/apache-ftpserver-1.1.4", 2121);
server.startServer();
```

---

## 📋 FtpUserManager - 계정 관리

### 단일 사용자 생성

**방법 1: 파라미터로 전달**
```java
boolean success = FtpUserManager.createUser(ftpServerPath, "user", "pass", homeDir);
```

**방법 2: AccountInfo 객체 사용**
```java
AccountInfo account = new AccountInfo("user", "pass", homeDir);
boolean success = FtpUserManager.createUser(ftpServerPath, account);
```

### 여러 사용자 일괄 생성

```java
Map<String, AccountInfo> accounts = new LinkedHashMap<>();
accounts.put("user1", new AccountInfo("user1", "pass1", homeDir1));
accounts.put("user2", new AccountInfo("user2", "pass2", homeDir2));

FtpUserManager.createUsers(ftpServerPath, accounts);
```

### 단일 사용자 삭제

```java
boolean success = FtpUserManager.deleteUser(ftpServerPath, "username");
```

### 여러 사용자 일괄 삭제

```java
String[] usersToDelete = {"user1", "user2", "user3"};
FtpUserManager.deleteUsers(ftpServerPath, usersToDelete);
```

---

## ⚙️ FtpConfigManager - 설정 관리

### 필수 패턴

```java
FtpConfigManager config = new FtpConfigManager(ftpServerPath);
config.loadConfig();                    // 1. 로드
// 설정 변경
config.saveConfig();                    // 2. 저장
```

### 주요 설정

| 메서드 | 설명 | 예제 |
|--------|------|------|
| `setServerPort(int)` | 포트 번호 | `setServerPort(2121)` |
| `setMaxLogins(int)` | 최대 로그인 수 | `setMaxLogins(20)` |
| `setAnonymousLoginEnabled(boolean)` | 익명 로그인 | `setAnonymousLoginEnabled(false)` |
| `setIdleTime(int)` | 유휴 시간(초) | `setIdleTime(600)` |
| `setMaxUploadRate(int)` | 업로드 속도(B/s) | `setMaxUploadRate(0)` 무제한 |
| `setMaxDownloadRate(int)` | 다운로드 속도(B/s) | `setMaxDownloadRate(0)` 무제한 |
| `printCurrentConfig()` | 설정 출력 | `printCurrentConfig()` |

---

## 🖥️ FtpServerManager - 서버 제어

### 서버 생명주기

```java
FtpServerManager server = new FtpServerManager(ftpServerPath, port);

// 시작
server.startServer();

// 상태 확인
if (server.isRunning()) {
    server.printStatus();
    server.printConnectedUsers();
}

// 재시작
server.restartServer();

// 정지
server.stopServer();
```

### 주요 메서드

| 메서드 | 반환 타입 | 설명 |
|--------|----------|------|
| `startServer()` | `void` | 서버 시작 (예외 발생 가능) |
| `stopServer()` | `void` | 서버 정지 |
| `restartServer()` | `void` | 서버 재시작 (2초 대기) |
| `isRunning()` | `boolean` | 실행 상태 확인 |
| `printStatus()` | `void` | 상태 정보 출력 |
| `printConnectedUsers()` | `void` | 연결 통계 출력 |

---

## 🔄 일반적인 워크플로우

### 시나리오 1: 최초 설정 및 시작

```java
String path = "D:/DEV/ftp/apache-ftpserver-1.1.4";

// 1. 설정
FtpConfigManager config = new FtpConfigManager(path);
config.loadConfig();
config.setServerPort(2121);
config.setMaxLogins(20);
config.saveConfig();

// 2. 계정 생성
FtpUserManager.createUser(path, "admin", "admin123", path + "/res/home/admin");

// 3. 서버 시작
FtpServerManager server = new FtpServerManager(path, 2121);
server.startServer();
```

### 시나리오 2: 계정만 추가

```java
String path = "D:/DEV/ftp/apache-ftpserver-1.1.4";
FtpUserManager.createUser(path, "newuser", "pass123", path + "/res/home/newuser");
```

### 시나리오 3: 계정 삭제

```java
String path = "D:/DEV/ftp/apache-ftpserver-1.1.4";
FtpUserManager.deleteUser(path, "olduser");
```

### 시나리오 4: 포트 변경

```java
// 서버가 실행 중이면 먼저 정지
server.stopServer();

// 설정 변경
config.loadConfig();
config.setServerPort(2222);
config.saveConfig();

// 새 포트로 재시작
FtpServerManager server = new FtpServerManager(path, 2222);
server.startServer();
```

---

## ⚠️ 자주 발생하는 오류

### 1. 포트가 이미 사용 중

**증상:** `java.net.BindException: Address already in use`

**해결:**
```java
// 다른 포트 사용
server.setServerPort(2122);

// 또는 기존 프로세스 확인 및 종료
// Windows: netstat -ano | findstr :2121
// Linux: lsof -i :2121
```

### 2. 파일을 찾을 수 없음

**증상:** `파일을 찾을 수 없습니다: .../users.properties`

**해결:**
```java
// FTP 서버 경로가 올바른지 확인
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4"; // 수정
```

### 3. 권한 오류

**증상:** `WritePermission` 관련 오류

**해결:**
```java
// 계정 생성 시 자동으로 WritePermission 부여됨
// 기존 계정이면 createUser()로 재생성
FtpUserManager.createUser(path, userId, password, homeDir);
```

---

## 💡 유용한 팁

### 1. 기본 포트 대신 2121 사용

```java
// 21 포트는 관리자 권한 필요
// 2121은 일반 사용자도 사용 가능
config.setServerPort(2121);
```

### 2. 무제한 속도 설정

```java
config.setMaxUploadRate(0);   // 0 = 무제한
config.setMaxDownloadRate(0);
```

### 3. 안전한 종료

```java
// finally 블록에서 서버 정지
try {
    server.startServer();
    // 작업 수행
} finally {
    if (server.isRunning()) {
        server.stopServer();
    }
}
```

### 4. 계정 일괄 생성 시 순서 보장

```java
// LinkedHashMap 사용으로 삽입 순서 유지
Map<String, AccountInfo> accounts = new LinkedHashMap<>();
```

---

## 📊 메서드 체이닝 패턴

```java
// 설정을 연속으로 변경
FtpConfigManager config = new FtpConfigManager(ftpServerPath);
config.loadConfig();
config.setServerPort(2121);
config.setMaxLogins(20);
config.setAnonymousLoginEnabled(false);
config.setIdleTime(600);
config.saveConfig();
```

---

## 🔍 디버깅

### 서버 상태 확인

```java
server.printStatus();
server.printConnectedUsers();
```

### 설정 확인

```java
config.loadConfig();
config.printCurrentConfig();
```

### 계정 목록 확인

```java
// createUsers() 호출 시 자동으로 테이블 출력됨
FtpUserManager.createUsers(ftpServerPath, accounts);
```

---

## 📞 테스트 접속

### FileZilla 클라이언트

```
호스트: localhost (또는 서버 IP)
포트: 2121
사용자명: admin
비밀번호: admin123
```

### 명령줄 FTP 클라이언트

```bash
ftp localhost 2121
# Username: admin
# Password: admin123
```

---

## 🔗 관련 문서

- [상세 API 문서](./API_DOCUMENTATION.md)
- [사용 가이드](./README.md)
- [Apache FTP Server 공식 문서](https://mina.apache.org/ftpserver-project/)

---

## 체크리스트

초기 설정 시 확인사항:

- [ ] FTP 서버 설치 경로가 올바른가?
- [ ] users.properties 파일이 존재하는가?
- [ ] ftpd-typical.xml 파일이 존재하는가?
- [ ] 방화벽에서 FTP 포트가 열려있는가?
- [ ] 홈 디렉토리 경로가 올바른가?
- [ ] 관리자 계정을 생성했는가?

---

이 가이드는 빠른 참조를 위한 요약본입니다. 더 자세한 내용은 [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)를 참조하세요.

