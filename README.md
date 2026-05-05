# Jinieboxes

`jinieboxes` 는 [지니박스](https://jiniebox.com) 의 공개용 클론 오픈소스 프로젝트입니다.
운영 서비스는 별도(jiniebox.com)로 유지되며, 본 리포는 동일한 비즈니스 로직을
누구나 빌드/실행/학습할 수 있도록 공개한 형태입니다.

- 🚫 어떠한 시크릿/API 키도 포함되지 않습니다 (모든 외부 연동은 설정이 없으면 자동 비활성화됩니다)
- 📦 외부 Tomcat 에 WAR 로 배포하거나, 임베디드 Tomcat 으로 단독 실행할 수 있습니다
- 🌏 다국어 지원(한/영/일) 은 점진적으로 도입 예정입니다

## 라이선스

[AGPL-3.0](LICENSE)

> ⚠️ **장보고(Jangbogo) 연동 로직** 에는 별도의 특허가 적용됩니다. 코드 자체는 AGPL 로
> 자유롭게 사용/수정/재배포할 수 있으나, 해당 로직을 상업적으로 활용하려면 별도의
> 특허 라이선스가 필요할 수 있습니다. 기본 설정은 비활성(`JANGBOGO_ENABLED=false`)이며,
> 명시적으로 `true` 로 변경한 경우에만 동작합니다.

## 요구 사항

- Java 17 이상 (LTS 권장: JDK 17 또는 21)
- MariaDB 또는 MySQL 5.7+ (사용자 인증/박스 기능 사용 시)

## 빌드 & 실행

### 1) Standalone (임베디드 Tomcat) — 가장 간단

```bash
./gradlew standaloneJar
java -jar build/libs/jinieboxes-*-standalone.jar --port=8080
```

옵션:

| 옵션 | 기본값 | 설명 |
| --- | --- | --- |
| `--port=8080` | `8080` | HTTP 포트 |
| `--context=/jbs` | `/jbs` | Context path |
| `--config=path/to/JINIEBOX.PROPERTIES` | (내장) | 외부 설정 파일 경로 |
| `--data-dir=./data` | `./data` | 데이터/로그/Tomcat 작업 디렉토리 |

개발 모드 (자동 재컴파일 X, 빠른 반복):

```bash
./gradlew runStandalone -Pport=8080
```

### 2) 외부 Tomcat 에 WAR 배포

```bash
./gradlew war
# build/libs/jinieboxes-*.war 를 Tomcat 11 의 webapps/ 에 복사
```

`gradle deploy` 태스크로 자동 배포도 가능합니다 (`gradle.properties.example` 참고).

## 설정

기본 동작은 시크릿 없이도 부팅되도록 설계되어 있습니다. 외부 연동을 사용하려면
`JINIEBOX.PROPERTIES` 에 해당 키를 채우세요.

### DB 설정

[doc/DB_SETUP.md](doc/DB_SETUP.md) 참고. 핵심:

```properties
LOCALDB_URL  = jdbc:mysql://127.0.0.1:3306/jiniebox?characterEncoding=UTF-8
LOCALDB_USER = jiniebox
LOCALDB_PASS = <강력한 암호>
```

### 외부 연동 (선택)

설정되지 않은 항목은 자동으로 비활성화되고 503 응답 또는 no-op 으로 처리됩니다.

| 기능 | 필요한 키 | 동작 |
| --- | --- | --- |
| Google OAuth | `GOOGLE_CLIENT_ID/SECRET/REDIRECT_URI` | OAuth 로그인 / Drive 연동 |
| Kakao 로그인 | `KAKAO_REST_API_KEY/CLIENT_SECRET/REDIRECT_URI` | Kakao 로그인 |
| Naver 로그인 | `NAVER_CLIENT_ID/SECRET/REDIRECT_URI` | Naver 로그인 / Clova |
| Firebase FCM | `FCM_REFRESH_TOKEN_FILE` (서비스 계정 JSON) | 푸시 알림 |
| 장보고 | `JANGBOGO_ENABLED=true` (특허) | 쇼핑몰 자동 수집 |

## 디렉토리 구조

```
src/
├── main/java/
│   ├── com/jiniebox/standalone/   # 임베디드 Tomcat 기반 standalone Main
│   ├── com/omnibuscode/base/      # 부팅, 환경, 안전 래퍼 (SafeProps, IntegrationGate)
│   ├── com/omnibuscode/auth/      # 사용자 인증
│   ├── com/omnibuscode/ctrl/      # 서블릿 (HTTP 진입점)
│   ├── com/omnibuscode/dao/       # 데이터 접근 객체
│   ├── com/omnibuscode/logic/     # 비즈니스 로직
│   └── com/omnibuscode/util/      # 유틸리티
└── main/webapp/                    # JSP, 정적 리소스, web.xml
gradle/deploy/                      # 운영 배포용 WEB-INF 템플릿
doc/                                # 설계 문서, 가이드
```

## 기여

이슈/PR 환영합니다. 변경 시 다음을 확인해 주세요.

- `./gradlew compileJava` 통과
- 시크릿이 들어 있지 않은지 (`.gitignore` 의 패턴 확인)
- 외부 연동 추가 시 [IntegrationGate](src/main/java/com/omnibuscode/base/IntegrationGate.java) 패턴을 따르기 (설정 누락 시 비활성)
