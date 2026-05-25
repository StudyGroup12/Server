# StudyGroup

스터디 그룹을 생성하고 멤버를 모집·관리하며, 함께 일정과 할일을 공유하는 **스터디 그룹 관리 서비스**입니다.

## 주요 기능

| 기능 | 설명 |
|------|------|
| 인증 / 회원관리 | 회원가입, JWT 로그인, 마이페이지 |
| 스터디 그룹 | 그룹 생성·조회·수정·삭제, 키워드 검색 |
| 멤버십 | 가입 신청, 승인·거절, 멤버 관리 |
| 게시판 | 그룹 내 게시글·댓글·좋아요 |
| 일정 / 출석 | 캘린더, 일정 등록, 출석 체크 |
| 할일 (Todo) | 그룹 공동 할일, 개인 체크리스트, 진행률 |

## 기술 스택

| 분류 | 기술 |
|------|------|
| Backend | Java 17, Spring Boot 3, Spring Security, Spring Data JPA |
| Frontend | React 18, Vite, TypeScript, React Query, React Router |
| Database | MySQL / MariaDB |
| Auth | JWT (Access Token + Refresh Token) |

## 시작하기

### 요구사항

- Java 17 이상
- Node.js 18 이상
- MySQL 8 이상

### 환경변수 설정

`backend/src/main/resources/application-local.yml` 파일을 직접 생성한다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/studygroup
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD

jwt:
  secret: YOUR_JWT_SECRET_KEY
```

### 실행

```bash
# Backend (포트 8080)
cd backend
./gradlew bootRun

# Frontend (포트 5173)
cd frontend
npm install
npm run dev
```

브라우저에서 `http://localhost:5173` 접속.

## 프로젝트 구조

```
StudyGroup/
├── backend/    # Spring Boot
└── frontend/   # React + Vite
```

백엔드는 도메인별 패키지(`auth`, `group`, `membership`, `board`, `schedule`, `todo`)로 분리되어 있으며, 팀원 각자가 담당 도메인을 독립적으로 개발한다.
