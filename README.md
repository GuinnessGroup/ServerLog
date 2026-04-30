<h1 align="center">ServerLog</h1>

<p align="center">Minecraft Paper 서버를 위한 상세 활동 로그 플러그인</p>
<p align="center">블록, 채팅, 명령어, 아이템, 플레이어 행동을 자동으로 분류·기록합니다.<br>
파일 로그와 MariaDB 데이터베이스를 동시에 또는 단독으로 사용할 수 있습니다.</p>

---

## 지원 환경

| 항목 | 버전 |
|---|---|
| Minecraft (Paper) | 26.1.2 |
| Java | 25 |
| MariaDB (선택) | 11.x 이상 권장 |

---

## 무엇을 기록하나요?

모든 로그에는 날짜·시간이 포함됩니다.

| 분류 | 기록 내용 |
|---|---|
| **블록** | 설치, 파괴 — 닉네임, 블록 종류, 위치 좌표 |
| **채팅** | 닉네임, 채팅 내용 |
| **명령어** | 닉네임, 명령어 전문, 맵 이름, 위치 좌표 |
| **아이템** | 버리기·줍기·스폰 에그 사용 — 닉네임, 아이템 종류, 수량, 위치 |
| **양동이** | 물·용암 붓기/채우기, 생물 포획 — 닉네임, 양동이 종류, 위치 |
| **플레이어** | 접속·퇴장·추방·사망·텔레포트·게임모드 변경 |
| **서버 지표** | 설정 주기(기본 5분)마다 맵별 로드된 청크 수, 엔티티 수, 접속 인원 |

---

## 설치 방법

1. `./gradlew build` 로 jar 빌드 (`build/libs/ServerLog-*.jar`)
2. jar 파일을 서버의 `plugins/` 폴더에 복사
3. 서버 재시작
4. `plugins/ServerLog/config.yml` 설정 후 필요 시 `/reload confirm` 또는 서버 재시작

---

## 빌드

```bash
./gradlew build      # 플러그인 jar 빌드 (build/libs/)
./gradlew runServer  # 개발용 내장 Paper 서버 실행
./gradlew clean      # 빌드 산출물 삭제
```

---

## 설정 (config.yml)

```yaml
lang: en   # 언어: en (영어) 또는 ko (한국어)

# 출력 대상 (파일, 데이터베이스 중 하나 또는 둘 다)
output:
  - file
  # - database   # 주석 해제 시 MariaDB 저장 활성화

# MariaDB 연결 설정 (output에 database 추가 시 필요)
database:
  host: localhost
  port: 3306
  name: serverlog
  username: root
  password: ""
  table-prefix: sl_   # 테이블 이름 접두사
  pool-size: 5        # 커넥션 풀 크기

serverInfo:
  interval: 5   # 서버 지표 기록 주기 (분)
```

---

## 로그 파일 구조

파일 로그는 플러그인 폴더(`plugins/ServerLog/`) 아래에 이벤트 종류별로 날짜 파일로 저장됩니다.

```
plugins/ServerLog/Logs/
├── Block/
│   ├── Block Break/  → 2026-04-30.txt
│   └── Block Place/  → 2026-04-30.txt
├── Chat/             → 2026-04-30.txt
├── Command/          → 2026-04-30.txt
├── Player/
│   ├── Join/         → 2026-04-30.txt
│   ├── Quit/         → ...
│   └── ...
└── ...
```

---

## 데이터베이스 스키마

DB 로그 활성화 시 아래 두 테이블이 자동 생성됩니다.

### `{prefix}events` — 플레이어·블록·아이템 이벤트

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT | 자동 증가 PK |
| event_type | VARCHAR(32) | 이벤트 종류 (e.g. `BLOCK_PLACE`, `CHAT`) |
| player_uuid | CHAR(36) | 플레이어 UUID |
| player_name | VARCHAR(16) | 플레이어 닉네임 |
| world | VARCHAR(64) | 맵 이름 |
| x, y, z | INT | 발생 위치 |
| timestamp | DATETIME(3) | 발생 시각 |
| data | JSON | 이벤트별 추가 정보 |

### `{prefix}server_metrics` — 서버 주기 지표

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | BIGINT | 자동 증가 PK |
| metric_type | VARCHAR(32) | `CHUNK_LOAD`, `ENTITY_COUNT`, `PLAYER_COUNT` |
| world | VARCHAR(64) | 맵 이름 |
| count | INT | 수량 |
| timestamp | DATETIME(3) | 측정 시각 |

> **웹 뷰어 활용 팁**: `player_uuid`로 조회 후 브라우저에서 Mojang UUID API를 호출해 플레이어명·스킨을 불러오면, 서버 DB에 불필요한 캐시 없이 최신 정보를 표시할 수 있습니다.

---

## 작성자

- **Liminaire**
