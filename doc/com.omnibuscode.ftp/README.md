# Apache FTP Server 관리 클래스

`com.omnibuscode.ftp` 패키지는 Apache FTP Server를 관리하기 위한 클래스들을 제공합니다.

## 📚 문서

- **[API 문서](./API_DOCUMENTATION.md)** - 모든 클래스와 메서드의 상세한 API 문서
- **[빠른 참조 가이드](./QUICK_REFERENCE.md)** - 자주 사용하는 패턴과 코드 스니펫
- **[문서 인덱스](./INDEX.md)** - 문서 찾기 가이드
- **[사용 가이드](./README.md)** - 현재 문서 (기본 사용법 및 예제)

---

## 📁 패키지 구조

```
com.omnibuscode.ftp/
└─ apache/                          # Apache FTP Server 관련 클래스
   ├─ FtpServerManager.java         # FTP 서버 시작/정지
   ├─ FtpUserManager.java           # 사용자 계정 생성/삭제/관리
   └─ FtpConfigManager.java         # ftpd-typical.xml 설정 관리
```

---

## 클래스 구성

### 1. FtpServerManager
**패키지:** `com.omnibuscode.ftp.apache.FtpServerManager`

Apache FTP Server를 프로그래밍 방식으로 시작/정지하는 클래스입니다.

**주요 기능:**
- FTP 서버 시작/정지/재시작
- 서버 실행 상태 확인
- 연결된 사용자 통계 확인 (Java Reflection 사용)

**사용 예시:**
```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
int port = 2121;

FtpServerManager serverManager = new FtpServerManager(ftpServerPath, port);

// 서버 시작
serverManager.startServer();

// 서버 상태 확인
serverManager.printStatus();

// 연결된 사용자 통계
serverManager.printConnectedUsers();

// 서버 정지
serverManager.stopServer();

// 서버 재시작
serverManager.restartServer();
```

---

### 2. FtpUserManager
**패키지:** `com.omnibuscode.ftp.apache.FtpUserManager`

사용자 계정을 생성, 삭제, 관리하는 클래스입니다.

**주요 기능:**
- 단일 FTP 계정 생성/업데이트
- 여러 FTP 계정을 한번에 생성/업데이트
- 단일 FTP 계정 삭제
- 여러 FTP 계정을 한번에 삭제
- 기존 계정의 비밀번호 변경
- 등록된 모든 계정 정보를 테이블 형태로 출력

**내부 클래스:**
- `AccountInfo` - 계정 정보(userId, password, homeDirectory)를 담는 DTO

**사용 예시 1: 단일 사용자 생성**
```java
import com.omnibuscode.ftp.apache.FtpUserManager;
import com.omnibuscode.ftp.apache.FtpUserManager.AccountInfo;

String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";

// 방법 1: 파라미터로 직접 전달
boolean result = FtpUserManager.createUser(
    ftpServerPath,
    "newuser",
    "password123",
    ftpServerPath + "/res/home/newuser"
);

// 방법 2: AccountInfo 객체 사용
AccountInfo account = new AccountInfo(
    "newuser",
    "password123",
    ftpServerPath + "/res/home/newuser"
);
boolean result2 = FtpUserManager.createUser(ftpServerPath, account);
```

**사용 예시 2: 여러 사용자 일괄 생성**
```java
import com.omnibuscode.ftp.apache.FtpUserManager;
import com.omnibuscode.ftp.apache.FtpUserManager.AccountInfo;
import java.util.LinkedHashMap;
import java.util.Map;

String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";

// 계정 정보 맵 생성
Map<String, AccountInfo> accounts = new LinkedHashMap<>();

accounts.put("jinie", new AccountInfo(
    "jinie",
    "password123",
    ftpServerPath + "/res/home/jinie"
));

accounts.put("admin", new AccountInfo(
    "admin",
    "admin123",
    ftpServerPath + "/res/home/admin"
));

// 여러 계정 일괄 생성/업데이트
FtpUserManager.createUsers(ftpServerPath, accounts);
```

**사용 예시 3: 단일 사용자 삭제**
```java
import com.omnibuscode.ftp.apache.FtpUserManager;

String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";

// 단일 사용자 삭제
boolean result = FtpUserManager.deleteUser(ftpServerPath, "testuser");
System.out.println("삭제 결과: " + (result ? "성공" : "실패"));
```

**사용 예시 4: 여러 사용자 일괄 삭제**
```java
import com.omnibuscode.ftp.apache.FtpUserManager;

String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";

// 삭제할 사용자 ID 배열
String[] usersToDelete = {"user1", "user2", "user3"};

// 여러 사용자 일괄 삭제
FtpUserManager.deleteUsers(ftpServerPath, usersToDelete);
```

---

### 3. FtpConfigManager
`ftpd-typical.xml` 설정 파일을 수정하는 클래스입니다.

**주요 기능:**
- FTP 서버 포트 번호 설정
- 최대 로그인 수 설정
- 익명 로그인 허용/비허용 설정
- 최대 업로드/다운로드 속도 제한 설정
- 유휴 시간 제한 설정
- 현재 설정 정보 출력

**사용 예시:**
```java
String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";

FtpConfigManager configManager = new FtpConfigManager(ftpServerPath);

// 설정 파일 로드
configManager.loadConfig();

// 설정 변경
configManager.setServerPort(2121);
configManager.setMaxLogins(20);
configManager.setAnonymousLoginEnabled(false);
configManager.setIdleTime(600);
configManager.setMaxUploadRate(0);  // 0 = 무제한
configManager.setMaxDownloadRate(0);

// 변경 사항 저장
configManager.saveConfig();

// 현재 설정 출력
configManager.printCurrentConfig();
```

---

## 전체 통합 예제

각 클래스를 독립적으로 사용하여 FTP 서버를 설정하고 실행하는 예제입니다.

```java
import com.omnibuscode.ftp.apache.FtpServerManager;
import com.omnibuscode.ftp.apache.FtpUserManager;
import com.omnibuscode.ftp.apache.FtpUserManager.AccountInfo;
import com.omnibuscode.ftp.apache.FtpConfigManager;
import java.util.LinkedHashMap;
import java.util.Map;

public class FtpServerSetup {
    public static void main(String[] args) {
        try {
            String ftpServerPath = "D:/DEV/ftp/apache-ftpserver-1.1.4";
            int port = 2121;
            
            // 1단계: 설정 파일 수정
            System.out.println("=== 설정 파일 수정 ===");
            FtpConfigManager configManager = new FtpConfigManager(ftpServerPath);
            configManager.loadConfig();
            configManager.setServerPort(port);
            configManager.setMaxLogins(20);
            configManager.setAnonymousLoginEnabled(false);
            configManager.setIdleTime(600);
            configManager.saveConfig();
            configManager.printCurrentConfig();
            
            // 2단계: 사용자 계정 생성
            System.out.println("\n=== 사용자 계정 생성 ===");
            Map<String, AccountInfo> accounts = new LinkedHashMap<>();
            accounts.put("jinie", new AccountInfo(
                "jinie", 
                "bobopa", 
                ftpServerPath + "/res/home/jinie"
            ));
            accounts.put("admin", new AccountInfo(
                "admin", 
                "admin123", 
                ftpServerPath + "/res/home/admin"
            ));
            FtpUserManager.createUsers(ftpServerPath, accounts);
            
            // 3단계: FTP 서버 시작
            System.out.println("\n=== FTP 서버 시작 ===");
            FtpServerManager serverManager = new FtpServerManager(ftpServerPath, port);
            serverManager.startServer();
            serverManager.printStatus();
            
            // 서버를 계속 실행하려면...
            System.out.println("\nFTP 서버가 실행 중입니다. 종료하려면 Enter를 누르세요...");
            System.in.read();
            
            // 4단계: 서버 정지
            System.out.println("\n=== FTP 서버 정지 ===");
            serverManager.stopServer();
            serverManager.printStatus();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## 의존성

이 클래스들은 다음 의존성이 필요합니다:

```gradle
implementation 'org.apache.ftpserver:ftpserver-core:1.2.0'
```

---

## 주의사항

1. **보안**: 운영 환경에서는 비밀번호를 해시화하여 저장하는 것을 권장합니다.
2. **포트**: 1024 이하의 포트는 관리자 권한이 필요할 수 있습니다.
3. **방화벽**: FTP 포트가 방화벽에서 열려있는지 확인하세요.
4. **경로**: FTP 서버 설치 경로를 올바르게 지정해야 합니다.
5. **파일 권한**: users.properties와 ftpd-typical.xml 파일에 쓰기 권한이 있어야 합니다.

---

## 파일 경로

- **users.properties**: `{ftpServerPath}/res/conf/users.properties`
- **ftpd-typical.xml**: `{ftpServerPath}/res/conf/ftpd-typical.xml`
- **사용자 홈 디렉토리**: `{ftpServerPath}/res/home/{username}`

---

## 테스트 방법

각 클래스의 `main()` 메서드를 실행하여 개별적으로 테스트할 수 있습니다:

1. **FtpUserManager**: 계정 생성/삭제 테스트
2. **FtpConfigManager**: 설정 파일 수정 테스트
3. **FtpServerManager**: 서버 시작/정지 테스트

또는 위의 "전체 통합 예제"를 실행하여 모든 클래스를 함께 테스트할 수 있습니다.

FTP 클라이언트(FileZilla, WinSCP 등)를 사용하여 실제 접속을 테스트할 수 있습니다.

---

## 문제 해결

### Q: `UnsupportedOperationException` 발생
A: `getAuthorities().add()` 대신 `setAuthorities()`를 사용하세요.

### Q: 포트가 이미 사용 중
A: 다른 프로그램이 해당 포트를 사용 중일 수 있습니다. 포트를 변경하거나 해당 프로그램을 종료하세요.

### Q: 설정 파일을 찾을 수 없음
A: `ftpServerPath`가 올바른지 확인하고, 해당 경로에 Apache FTP Server가 설치되어 있는지 확인하세요.

### Q: 파일 업로드/다운로드 권한 오류
A: 사용자에게 `WritePermission` 권한이 부여되어 있는지 확인하세요.

