# 시작 프롬프트

> 아래 내용을 AI 도구에 그대로 붙여넣어 시작한다.

---

```
## 프로젝트 개요

스터디 그룹을 생성하고 멤버를 모집·관리하며, 함께 일정과 할일을 공유하는
스터디 그룹 관리 서비스다. 팀원이 각자 도메인을 나눠 개발하는 팀 프로젝트다.

## 기술 스택

- Backend: Java 21, Spring Boot 3, Spring Security, Spring Data JPA, H2 DB
- Frontend: React 18, Vite, TypeScript, React Query, React Router
- 개발 환경: GitHub Codespaces
- 인증 방식: JWT (Access Token 30분 + Refresh Token 7일)

## 전체 도메인 구조

```
com.studygroup
├── domain/
│   ├── auth/        인증/회원관리 — 회원가입, 로그인, 마이페이지
│   ├── group/       스터디 그룹  — 그룹 CRUD, 검색
│   ├── membership/  멤버십       — 가입신청, 승인·거절, 멤버 관리
│   ├── board/       게시판       — 게시글, 댓글, 좋아요
│   ├── schedule/    일정/출석    — 캘린더, 일정 등록, 출석 체크
│   └── todo/        할일         — 공동 할일, 개인 체크리스트, 진행률
└── global/          공통 인프라  — 이미 구현 완료
```

## 이미 구현된 공통 인프라 (global/)

아래 파일들은 이미 구현되어 있다. 새로 만들지 말 것.

- `global/response/ApiResponse.java` — 공통 응답 래퍼
- `global/response/ErrorCode.java` — 에러 코드 enum
- `global/exception/CustomException.java` — 커스텀 예외
- `global/exception/GlobalExceptionHandler.java` — 전역 예외 처리
- `global/entity/BaseEntity.java` — createdAt, updatedAt 자동 관리
- `global/jwt/JwtProvider.java` — JWT 생성·검증
- `global/security/JwtAuthenticationFilter.java` — JWT 인증 필터
- `global/security/CustomUserDetailsService.java` — auth 담당자가 구현 예정
- `global/config/SecurityConfig.java` — Spring Security, CORS 설정

## 코드 규칙

### 공통
- 브랜치: dev에서 작업, 검수 완료 후 main으로 머지. main 직접 push 금지
- 도메인 간 의존: 다른 도메인의 Entity를 직접 참조하지 않고 ID로 참조

### Backend
- 모든 Entity는 BaseEntity 상속
- Lombok @Builder + @NoArgsConstructor(access = PROTECTED) 패턴 사용
- 읽기 메서드: @Transactional(readOnly = true) / 쓰기 메서드: @Transactional
- 비즈니스 예외: throw new CustomException(ErrorCode.XXX)
- DTO 네이밍: [기능]Request / [기능]Response
- 새 에러 코드 필요 시 ErrorCode.java에 추가

### Frontend
- TypeScript 사용 (JS 파일 생성 금지)
- 컴포넌트에서 axios/fetch 직접 호출 금지 → api/[도메인].api.ts 함수 + React Query 사용
- Access Token: sessionStorage 또는 메모리 저장 (localStorage 금지)
- Refresh Token: HttpOnly 쿠키로만 처리

## API 응답 형식

모든 API 응답은 아래 형식을 따른다.

성공: { "success": true, "data": { ... } }
실패: { "success": false, "error": { "code": "...", "message": "..." } }

## 보안 규칙

- 민감 정보(JWT Secret 등) 코드 하드코딩 금지
- 비밀번호: BCryptPasswordEncoder 해싱 필수, 평문 저장 금지
- dangerouslySetInnerHTML 사용 금지
- 권한 검증은 프론트가 아닌 백엔드에서 수행
- 다른 사용자의 리소스 접근 시 서비스 레이어에서 소유자 검증 필수

## 참고 문서 (docs/ 폴더)

- docs/global-infrastructure.md — 공통 인프라 사용법 상세
- docs/auth-guide.md — auth 도메인 구현 예시 (다른 도메인 구현 시 참고 가능)
```
