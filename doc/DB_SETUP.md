# 데이터베이스 설정 가이드

지니박스는 MariaDB(또는 MySQL 5.7+)을 필요로 합니다. 본 문서는 처음부터 DB를 셋업하는 절차를 정리합니다.

## 1. 데이터베이스/사용자 생성

```sql
-- root 또는 관리자 계정으로 접속 후 실행
CREATE DATABASE jiniebox CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'jiniebox'@'localhost' IDENTIFIED BY 'CHANGE_ME_STRONG_PASSWORD';
GRANT ALL PRIVILEGES ON jiniebox.* TO 'jiniebox'@'localhost';
FLUSH PRIVILEGES;
```

## 2. 스키마 적용

리포에 포함된 [db/schema.sql](../db/schema.sql) (운영 DB 의 mysqldump 결과, MariaDB 11.x) 을
다음 명령으로 적용합니다:

```bash
mysql -u jiniebox -p jiniebox < db/schema.sql
```

스키마는 데이터 없이 테이블 구조 + 루틴/트리거/이벤트만 포함합니다.
운영 환경에서 새로 덤프하려면:

```bash
mysqldump --no-data --routines --triggers --events -u <user> -p jiniebox > db/schema.sql
```

## 3. JINIEBOX.PROPERTIES 설정

`src/main/java/res/JINIEBOX.PROPERTIES` (또는 standalone 모드의 `./conf/JINIEBOX.PROPERTIES`) 에 다음 키를 채웁니다:

```properties
LOCALDB_NAME = jiniebox
LOCALDB_URL  = jdbc:mysql://127.0.0.1:3306/jiniebox?characterEncoding=UTF-8&autoReconnect=true
LOCALDB_USER = jiniebox
LOCALDB_PASS = CHANGE_ME_STRONG_PASSWORD
```

## 4. 연결 검증

서버 부팅 시 DB 설정이 누락되어 있으면 다음과 같은 명확한 에러 메시지가 출력됩니다:

```
DB 설정이 누락되었습니다. JINIEBOX.PROPERTIES 의 LOCALDB_URL/LOCALDB_USER/LOCALDB_PASS 를 확인하세요.
(현재: URL=MISSING, USER=MISSING, PASS=MISSING)
```

## 참고

- JDBC 드라이버는 `com.mysql:mysql-connector-j:8.4.0` (Maven Central) 사용. 드라이버 클래스: `com.mysql.cj.jdbc.Driver`
- DB 자체가 없어도 서버는 부팅됩니다 (외부 연동 graceful degradation 적용). 다만 사용자 인증/박스 등 핵심 기능 사용 시 위 에러가 발생합니다.
