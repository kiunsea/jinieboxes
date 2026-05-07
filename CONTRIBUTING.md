# Contributing to Jinieboxes

`jinieboxes` 에 기여해 주셔서 감사합니다. 본 문서는 이슈/PR 등록 시 참고하실 가이드입니다.

## 개발 환경

- JDK 17 이상 (LTS 권장: 17 또는 21)
- MariaDB 10.6+ 또는 MySQL 5.7+
- Gradle 은 wrapper 가 포함되어 있으므로 별도 설치 불필요 (`./gradlew`)

## 빌드 & 실행

```bash
# 1) standalone fat jar 생성
./gradlew standaloneJar

# 2) 실행
./start.sh                  # Linux/macOS (기본 포트 8282)
start.bat                   # Windows
```

상세 옵션은 [README.md](README.md), DB 설정은 [doc/DB_SETUP.md](doc/DB_SETUP.md) 참고.

## 변경 시 체크리스트

- [ ] `./gradlew compileJava` 통과
- [ ] 시크릿(`*.PROPERTIES` 의 실제 값, API 키, 토큰, private key) 이 들어가지 않았는지 `.gitignore` 확인
- [ ] 새로운 외부 연동 추가 시 [IntegrationGate](src/main/java/com/omnibuscode/base/IntegrationGate.java) 패턴 따르기 — 설정 누락 시 자동 비활성
- [ ] 기존 동작에 영향 가는 변경이면 [DEVLOG.md](DEVLOG.md) 와 [CHANGELOG.md](CHANGELOG.md) 갱신

## 커밋 메시지 가이드

자유 형식이지만 다음 prefix 사용을 권장합니다.

| Prefix | 의미 |
| --- | --- |
| `feat:` | 새 기능 |
| `fix:` | 버그 수정 |
| `refactor:` | 동작 변경 없는 코드 정리 |
| `docs:` | 문서만 변경 |
| `chore:` | 빌드/툴/설정 |
| `test:` | 테스트만 변경 |

## PR 절차

1. fork → 브랜치 생성 (`feat/something` 또는 `fix/issue-123`)
2. 작은 단위로 commit
3. 본 리포 main 브랜치 대상으로 PR 생성
4. CI(GitHub Actions) 통과 확인
5. 리뷰 후 squash merge

## 라이선스 동의

본 프로젝트는 [AGPL-3.0](LICENSE) 입니다. PR 을 제출하시면 동일 라이선스로 기여하시는 것에 동의하시는 것으로 간주됩니다.

장보고(Jangbogo) 연동 로직에는 별도의 특허가 적용됩니다 (기본 비활성). 해당 로직과 직접 관련된 기여는 사전에 이슈로 논의해 주세요.

## 보안 이슈 신고

공개 이슈가 아닌 비공개 채널로 신고해 주세요: kiunsea@gmail.com
