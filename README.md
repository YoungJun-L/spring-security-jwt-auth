# Spring Security Authentication Service

## 📌 프로젝트 소개

![image](https://github.com/user-attachments/assets/c3441cbe-1ec0-46a6-8f28-f85d9ad86f06)

Kotlin과 Spring Security 기반의 사용자 인증 및 권한 부여 시스템입니다. JWT 기반 인증과 이메일 인증 기능을 제공합니다.

이 인증 모듈은 독립 실행형(Runnable) 모듈로 설계되었으며, 다른 메인 모듈에 쉽게 통합할 수 있도록 구성되었습니다. 이를 통해 메인 서비스에서 별도의 인증 기능 개발 없이도 간편하게 인증 기능을 활용할 수
있습니다.

React, Antd 기반의 Admin 기능도 제공합니다.

## 🚀 주요 기능

- 회원가입 및 로그인, 로그아웃
- 이메일 인증 코드 전송
- JWT 액세스/리프레시 토큰 발급 및 검증
- 비밀번호 변경 기능
- 쿠키를 통한 USER_ID 모듈 간 전달

## ⚙️ 기술 스택

언어: Kotlin 2.0.21, Java 21

프레임워크: Spring Boot 3.3.6

데이터베이스: MySQL8, H2

테스트: Kotest 5.9.0, Asci

기타: JJWT 0.12.6

## 🔧 설치 및 실행 방법

### 1️⃣ 프로젝트 설정 및 실행

```
git clone https://github.com/YoungJun-L/spring-security-jwt-auth.git
cd spring-security-jwt-auth
./gradlew bootRun
```

### 2️⃣ 환경 변수 설정

- JWT 설정  
  `ACCESS_SECRET_KEY`, `REFRESH_SECRET_KEY`

- 메일 설정  
  `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_PORT`

- DB 설정  
  `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`

## 🛠️ 프로젝트 구조

### 1. Auth: 인증 모듈

```
├── security          # Spring Security 관련
│   ├── config        # FilterChain, PasswordEncoder 등
│   ├── filter        # 커스텀 인증 필터(Json 기반 로그인, 로그아웃, JWT 인증, 유저 정보 쿠키 교환)
│   ├── handler       # 인증 후처리 핸들러
│   ├── provider      # 인증 처리
│   └── token         # Authenticaion 객체 관련
│
├── api               # api (컨트롤러 계층)
├── application       # 비즈니스 로직 (서비스 계층)
├── domain            # 도메인 계층 (엔티티 및 도메인 로직)
├── infra             # 인프라 계층 (DB 및 메일 서비스)
└── support           # 공통 예외, Response Dto
```

### 2. Core: 코어 모듈 (예시용)

쿠키로 전달받은 USER_ID 처리

- AnyUserArgumentResolver.kt: 미인증 유저 USER_ID=0, 인증 유저 USER_ID>0
- UserArgumentResolver.kt : 인증 유저

### 3. Async: 비동기 관련 설정 모듈

## 💻 사용 예시

### 1. 회원 가입

```
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "yourPassword",
  "verificationCode": "123456"
}
```

### 2. 로그인

```
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "yourPassword"
}
```

### 3. JWT 인증

```
Authorization: Bearer <your-access-token>
```
