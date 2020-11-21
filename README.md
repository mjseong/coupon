# Coupon Service application


## Environment
+ Spring Boot 2
+ Java 11
+ MySQL 5.7 or H2
+ JUnit5 

## Development Strategy
* 개발환경 분리


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

