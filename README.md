# Coupon Service Application
RestAPI 기반의 쿠폰 서비스 웹어플리케이션 입니다.
+ 지원기능
   + 쿠폰생성
   + 사용자에게 쿠폰지급
   + 쿠폰검색/조회
   + 쿠폰사용/취소
   + 일자 별 만료된 쿠폰 조회
   + 만료예정(3일전)인 쿠폰 안내 메시지 발송(log.info로 출력으로 대체)
   + 사용자 가입과 로그인(JWT AccessToken 발급),API별 접근제한 기능 제공
## Environment
+ Spring Boot 2 & Spring Oauth2ResourceServer & Jwt(jsonwebtoken)
+ Java 11
+ MySQL 5.7 or H2
+ JUnit5 

## Development Strategy
* 인증 및 AccessToken 사용
    * jwk를 통해 jwt sign검증 하는 방식은 사용하지 않았고 HS256키를 서버 config에 저장하여 발급받은 jwt를 sign하여 jwt위변조를 검증하였습니다.
    * 동일한 대칭키를 사용한다면, scale out시에 동일한 key를 사용해야 하기 때문에 key rotation에 어려움이 있습니다.
    * oauth2 인증 후 api게이트웨이에서 accessToken검증 방법을 사용하지 않기에 현재 구현물은 대칭키로 발급받은 jwt로 사용자 인증을 합니다.
    * jwt claim의 scope를 기록하여 일반 사용자와 관리자 사용자 검증할 수 있게 하였고, 이 토큰을 통해 coupon resource서버가 (발급,조회,사용,취소)접근 권한을
    제어합니다. 
* Blocking API 기반으로 개발
   * SpringBoot-web을 기반으로 개발되어 Blocking Api로 동작하며, Blocking JDBC driver를 이용했기 때문에 완전한 비동기 api동작은 하지 않습니다.<br>
    비동기 api서버 기반으로 개발하려면 Spring webflux 기반으로 개발해야하기 때문에 단기간 개발에 익숙한 web으로 개발하였습니다.
   * 스케줄링 및 쿠폰 만료 알림 기능은 비동기 기능을 활성화 하여 기본 API에 영향 없도록 하였습니다.<br>
     scale out시 고려할 사항은 스케줄링을 off하고 별도로 스케줄링을 실행해야 합니다. 해당 동작은 서비스 api와 batch job을 분리해서 실행되어야만, 동시성 문제가 발생하지 않습니다.
   * 다른 방법은 MessageQueue(Kafka또는RabbitMQ)가 broker가 되고 Blocking API서버를 worker로 동작하도록 하여 Broker가 제공해주는 데이터만 분산처리하는 방법을 고려할 수 있습니다.   
* 쿠폰코드 및 관리방안
   * 쿠폰 번호는 UUID를 사용하여 유일성을 보장하였습니다. scale out시에도 uuid를 이용한 쿠폰코드는 충돌 가능성이 적습니다.
   * Java UUID secureRadom 이슈(blocking, hang)는 현재 bugfix되어 성능에 문제가 없는것으로 조사하였습니다. 
   * 쿠폰은 만료기간을 설정할 수 있게 하였으며, 만료일 지정 없을시 기본 발급일 기준 7일이후로 설정 하였습니다.
   * 매일 00시 00분에 스케줄러가 실행하여 만료일 기준으로 쿠폰을 만료시기며, 추가 기능으로 만료일 3일 이전에 지급받은 사용자에게 알림을 주도록 구현하였습니다.
* EventSourcing과 CQRS 고민    
    * 현재 구현체는 조회 결과는 Info schema(coupon_info)에 상태변경이 일어나는 과정에 조회될 수 있습니다.
    따라서 event-sourcing과 CQRS를 구현하기 위한 기반으로 이벤트기반 쿠폰이력 저장기능을 추가하였습니다.   
    * 쿠폰코드 마다 (발급, 지급, 사용, 취소, 만료)이벤트와 (발급상태, 지급상태, 사용상태, 만료상태)상태정보를 저장할 수 있는 Log schema(coupon_hist)를 구현하였습니다.
    아직 과제 구현상 DB클러스링 및 master, slave 환경을 구축하지 못해 CQRS구현은 하지 못했습니다. 

## Build & Run
* 실행 환경
    * Application 실행에 영향받지 않게 local와 dev를 분리하였습니다.
    * local은 H2 DB로 동작하게 하였으며, dev환경은 MySQL을 사용하도록 세팅하였습니다.
    * 각 RDBMS에서 영향받지 않도록 ID 생성은 Hibernate Sequence전략을 사용하였습니다.
    * local은 H2, dev는 Mysql으로 동작합니다.
     
1. MySQL 컨테이너 세팅
   ```
   docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=1234 --name mysql mysql/mysql-server:5.7.24-1.1.8

2. Build & Run
   ```
   $ ./gradlew bootRun -DSpring.profile.active=local 또는 dev
   or
   $ ./gradlew build 또는 bootJar
   $ cd /git/coupon/build/libs/ java -jar coupon.jar -DSpring.profile.active=local 또는 dev 

## DB setting
```
  docker exec -it mysql bash
  $ mysql -u root -p
  password: 1234
   
  mysql> CREATE DATABASE coupon;
  mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '1234';
  mysql> FLUSH PRIVILEGES;
```

# API Description
Rest API는 인증과 쿠폰으로 나눠 구현되어 있으며, 각표에 설명 및 필수 인자값 표시를 하였음.
## Auth API

##### - Require parameter *표시 
##### - accessToken 만료일 30분 설정됨
| NO | API NAME | HTTP<br>method|API PATH | API PARAM | DESC | 
|---:|----------------------:|---:|----------------------:|------------------------:|--------------------:| 
|1|가입| POST| /signup|username*<br>passwrod*<br> adminRole|adminRole=true일때<br> adminRole 부여
|2|로그인| POST| /signin|username*<br> password*<br>| ResponseBody<br>accessToken(JWT)발급

## Coupon API

##### - Require parameter *표시
##### - Http Header Authorization Bearer Token 사용되는 API NAME에 (*)표시
##### - JWT Claim SCOPE(coupon:write,read)와 (coupon_admin:write,read)에 따라 API의 접근 제한이 있음.
| NO | API NAME | HTTP<br>METHOD|API PATH | API PARAM | DESC |
|---:|------------:|---:|:----------------------|------------------------:|--------------------:| 
|1|쿠폰발급(*)|POST|/api/coupons| count*| coupon_admin:write|
|2|쿠폰지급(*)|PUT|/api/coupons/{couponCode}/users/{userName}/assign| couponCode*<br> userName*| coupon:write
|3|사용자에게<br>지급된 쿠폰조회(*)|GET|/api/coupons/user| | coupon:write<br>JWT에서 사용자ID 사용|
|4|사용자에게<br>지급된 쿠폰조회(*)|GET|/api/coupons/{couponCode}|couponCode*| coupon:write|
|5|지급된<br>쿠폰사용(*)|PUT|/api/coupons/{couponCode}/users/{userName}/use| couponCode*<br> userName*| coupon_admin:write|
|6|사용된<br>쿠폰취소(*)|PUT|/api/coupons/{couponCode}/users/{userName}/cancel| couponCode*<br> userName*| coupon_admin:write|
|7|당일 만료된<br>쿠폰목록조회(*)|GET|/api/coupons/expired-coupon| searchDate*<br> page<br>size| coupon:write<br>searchDate<br>ex)2020-11-20|

