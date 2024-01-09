# reactive_user

개발 환경 및 참고사항
- Java version 11
- JPA
- Postgresql
- Reactive Programming, non-blocking 처리 활용 (flux, mono)
- JWT 인증
- GET, POST, PUT, DELETE 구현

---
- 개발 환경구성
  - 
  - non-blocking 환경을 사용하기 위해 Spring MVC 대신 Spring Webflux 를 사용했습니다.
  - Database는 Postgresql을 사용했으나, Reactive환경에서는 JPA대신 R2DBMS를 사용한다 하여 JPA 대신 사용 하였습니다.
- 구현한 API
  - 
  - 회원가입 (POST)
  - 로그인 (POST)
  - 리스트조회 (GET)
  - 단일조회(GET)
  - 회원수정(PUT)
  - 회원탈퇴(DELETE)
- JWT
  - 
  - JWT를 IUWT 방식으로 구현 하였습니다.
    - refreshToken 대신 uuid를 accessToken안에 심고, cookie로 해당 uuid를 전달하는 방식입니다.
    - 클라이언트에서 요청이 올 경우 accessToken안의 uuid과 cookie로 전달받은 uuid가 동일한지 검증합니다.
    - refreshToken은 Stateless 하지만 REDIS와 같은 Cache에 저장을 해야하는데, 이러면 Stateless가 깨진다고 생각했습니다.
    - refreshToken을 저장하지 않고 accessToken에 넣는다면 IUWT와 동일한 방식이라 생각 됩니다.
    - 논문에 따르면 이중으로 데이터가 있기 때문에 안전하여 accessToken의 유효기간을 길게 가져가도 좋다고 적혀있습니다.