# Apache FTP Server 관리 클래스 문서 인덱스

**패키지:** `com.omnibuscode.ftp`  
**버전:** 1.0  
**최종 업데이트:** 2025-11-06

---

## 📂 문서 목록

### 1. [README.md](./README.md) - 시작 가이드
**대상:** 처음 사용하는 개발자  
**내용:**
- 패키지 개요
- 클래스별 기본 사용법
- 전체 통합 예제
- 의존성 및 설치 방법

**이 문서를 읽어야 하는 경우:**
- Apache FTP Server 관리 클래스를 처음 접하는 경우
- 각 클래스의 역할과 기본 사용법을 알고 싶은 경우
- 빠르게 시작하고 싶은 경우

---

### 2. [QUICK_REFERENCE.md](./QUICK_REFERENCE.md) - 빠른 참조 가이드
**대상:** 이미 사용 경험이 있는 개발자  
**내용:**
- 자주 사용하는 코드 패턴
- 메서드 요약 테이블
- 일반적인 워크플로우
- 문제 해결 팁
- 디버깅 방법

**이 문서를 읽어야 하는 경우:**
- 특정 기능을 빠르게 찾고 싶은 경우
- 코드 스니펫이 필요한 경우
- 일반적인 오류 해결 방법을 찾는 경우
- 메서드 시그니처를 빠르게 확인하고 싶은 경우

---

### 3. [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - 상세 API 문서
**대상:** 심화 사용자, 라이브러리 개발자  
**내용:**
- 모든 클래스의 상세 설명
- 모든 메서드의 완전한 시그니처
- 파라미터 설명
- 반환값 및 예외 처리
- 사용 예제 및 코드 샘플
- 보안 고려사항

**이 문서를 읽어야 하는 경우:**
- 특정 메서드의 모든 파라미터를 알고 싶은 경우
- 예외 처리 방법을 정확히 알고 싶은 경우
- 고급 기능을 사용하고 싶은 경우
- 라이브러리를 확장하거나 커스터마이징하고 싶은 경우

---

## 🎯 목적별 문서 선택 가이드

### "처음 시작합니다"
👉 **[README.md](./README.md)** → **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)**

### "빠르게 코드가 필요합니다"
👉 **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)**

### "정확한 메서드 사양이 필요합니다"
👉 **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)**

### "문제가 발생했습니다"
👉 **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)** (자주 발생하는 오류 섹션)

### "모든 기능을 알고 싶습니다"
👉 **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)** → **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)**

---

## 📋 클래스별 문서 위치

### FtpServerManager (서버 시작/정지)

| 문서 | 섹션 | 내용 |
|------|------|------|
| README.md | "1. FtpServerManager" | 기본 사용법 |
| QUICK_REFERENCE.md | "FtpServerManager - 서버 제어" | 서버 제어 패턴 |
| API_DOCUMENTATION.md | "1. FtpServerManager" | 완전한 API 문서 |

**주요 메서드:**
- `startServer()` - 서버 시작
- `stopServer()` - 서버 정지
- `restartServer()` - 서버 재시작
- `isRunning()` - 실행 상태 확인
- `printStatus()` - 상태 정보 출력
- `printConnectedUsers()` - 연결 통계 출력

---

### FtpUserManager (사용자 계정 관리)

| 문서 | 섹션 | 내용 |
|------|------|------|
| README.md | "2. FtpUserManager" | 기본 사용법 |
| QUICK_REFERENCE.md | "FtpUserManager - 계정 관리" | 빠른 코드 예제 |
| API_DOCUMENTATION.md | "2. FtpUserManager" | 완전한 API 문서 |

**주요 메서드:**
- `createUser(String, String, String, String)` - 단일 사용자 생성
- `createUser(String, AccountInfo)` - 단일 사용자 생성 (객체)
- `createUsers(String, Map)` - 여러 사용자 일괄 생성
- `deleteUser(String, String)` - 단일 사용자 삭제
- `deleteUsers(String, String[])` - 여러 사용자 일괄 삭제

---

### FtpConfigManager (설정 파일 관리)

| 문서 | 섹션 | 내용 |
|------|------|------|
| README.md | "3. FtpConfigManager" | 기본 사용법 |
| QUICK_REFERENCE.md | "FtpConfigManager - 설정 관리" | 설정 변경 패턴 |
| API_DOCUMENTATION.md | "3. FtpConfigManager" | 완전한 API 문서 |

**주요 메서드:**
- `loadConfig()` - 설정 파일 로드
- `saveConfig()` - 설정 파일 저장
- `setServerPort(int)` - 포트 설정
- `setMaxLogins(int)` - 최대 로그인 수 설정
- `setAnonymousLoginEnabled(boolean)` - 익명 로그인 설정
- `printCurrentConfig()` - 설정 정보 출력

---

## 🔄 학습 경로

### 초급 (1-2일)

1. **[README.md](./README.md)** 전체 읽기
2. **간단한 서버 시작** 예제 실행
   ```java
   FtpServerManager serverManager = new FtpServerManager(ftpServerPath, 2121);
   serverManager.startServer();
   ```
3. **단일 사용자 생성** 실습
   ```java
   FtpUserManager.createUser(path, "user", "pass", homeDir);
   ```

### 중급 (3-5일)

1. **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)** 주요 섹션 읽기
2. **설정 파일 수정** 실습
3. **프로그래밍 방식 서버 제어** 실습
4. **여러 사용자 일괄 생성** 실습

### 고급 (1주 이상)

1. **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)** 전체 읽기
2. **예외 처리** 구현
3. **커스텀 워크플로우** 작성
4. **라이브러리 확장** 고려

---

## 📊 문서 통계

| 문서 | 페이지 수* | 섹션 수 | 코드 예제 수 | 난이도 |
|------|-----------|---------|--------------|--------|
| README.md | ~15 | 8 | 12 | ⭐ 초급 |
| QUICK_REFERENCE.md | ~10 | 12 | 25+ | ⭐⭐ 중급 |
| API_DOCUMENTATION.md | ~30 | 45+ | 35+ | ⭐⭐⭐ 고급 |
| INDEX.md | ~5 | 8 | 3 | ⭐ 초급 |

*예상 인쇄 페이지 수

---

## 🔍 키워드 검색 가이드

### 계정 관련
- **계정 생성** → README.md, QUICK_REFERENCE.md "FtpUserManager"
- **계정 삭제** → README.md, QUICK_REFERENCE.md "FtpUserManager"
- **비밀번호 변경** → API_DOCUMENTATION.md "createUser"
- **권한 설정** → API_DOCUMENTATION.md "FtpUserManager"

### 설정 관련
- **포트 변경** → QUICK_REFERENCE.md "설정 관리"
- **익명 로그인** → API_DOCUMENTATION.md "setAnonymousLoginEnabled"
- **속도 제한** → API_DOCUMENTATION.md "setMaxUploadRate"

### 서버 제어
- **서버 시작** → README.md "FtpServerManager"
- **서버 정지** → QUICK_REFERENCE.md "서버 제어"
- **재시작** → API_DOCUMENTATION.md "restartServer"

### 문제 해결
- **오류 해결** → QUICK_REFERENCE.md "자주 발생하는 오류"
- **디버깅** → QUICK_REFERENCE.md "디버깅"
- **예외 처리** → API_DOCUMENTATION.md "예외 처리"

---

## 💡 추천 읽기 순서

### 시나리오 1: "빠르게 서버를 실행하고 싶어요"
1. README.md - 클래스 구성 섹션 (5분)
2. QUICK_REFERENCE.md - 빠른 시작 (3분)
3. 코드 실행 및 테스트 (10분)

**총 소요 시간:** 20분

---

### 시나리오 2: "프로덕션 환경에 배포할 거예요"
1. README.md - 전체 읽기 (30분)
2. API_DOCUMENTATION.md - 보안 고려사항 (15분)
3. API_DOCUMENTATION.md - 예외 처리 (15분)
4. QUICK_REFERENCE.md - 자주 발생하는 오류 (10분)
5. 코드 작성 및 테스트 (2시간)

**총 소요 시간:** 3시간 10분

---

### 시나리오 3: "라이브러리를 확장하고 싶어요"
1. README.md - 전체 읽기 (30분)
2. API_DOCUMENTATION.md - 전체 읽기 (2시간)
3. QUICK_REFERENCE.md - 전체 읽기 (30분)
4. 소스 코드 분석 (4시간)
5. 확장 기능 개발 (개발 규모에 따라 다름)

**총 소요 시간:** 7시간+ (개발 시간 제외)

---

## 🆘 도움이 필요한 경우

### 1. 문서에서 답을 찾지 못한 경우
- 프로젝트 이슈 트래커에 질문 등록
- 관련 문서 섹션 링크 포함

### 2. 버그를 발견한 경우
- 재현 가능한 최소 코드 예제 작성
- 오류 메시지 전체 포함
- 환경 정보 (OS, Java 버전 등) 포함

### 3. 기능 요청
- 사용 사례 설명
- 예상되는 API 인터페이스 제안

---

## 📌 문서 버전 정보

| 버전 | 날짜 | 변경 사항 |
|------|------|-----------|
| 1.0 | 2025-11-06 | 초기 문서 작성 |

---

## 📖 외부 리소스

- [Apache FTP Server 공식 문서](https://mina.apache.org/ftpserver-project/)
- [Apache FTP Server API JavaDoc](https://mina.apache.org/ftpserver-project/apidocs/)
- [FTP 프로토콜 RFC 959](https://tools.ietf.org/html/rfc959)

---

**다음 단계:**
- 처음 시작한다면 → [README.md](./README.md)
- 코드가 필요하다면 → [QUICK_REFERENCE.md](./QUICK_REFERENCE.md)
- 심화 학습을 원한다면 → [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

