# API Description
--------------------------------------------------------------
## Auth API
--------------------------------------------------------------
##### - Require parameter *표시 
| NO | API NAME | HTTP<br>method|API PATH | API PARAM | DESC | 
|---:|----------------------:|---:|----------------------:|------------------------:|--------------------:| 
|1|가입| POST| /signup|username*<br>passwrod*<br> adminRole|adminRole=true일때<br> adminRole 부여
|2|로그인| POST| /signin|username*<br> password*<br>| accessToken(JWT) 발급

## Coupon API
--------------------------------------------------------------
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

