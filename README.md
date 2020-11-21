#API Description
##Auth API
| NO | API NAME | HTTP<br>METHOD|API PATH | API PARAM | DESC | 
|---:|----------------------:|---:|----------------------:|------------------------:|--------------------:| 
|1|가입| POST| /signup|username(필수)<br>passwrod(필수)<br> adminRole(옵션)|adminRole=true일때<br> adminRole 부여
|2|로그인| POST| /signin|username(필수)<br> password(필수)<br>| accessToken(JWT) 발급

##Coupon API
##### - Http Header Authorization Bearer Token 사용되는 API NAME에 (*)표시
##### - JWT Claim SCOPE(coupon:write,read)와 (coupon_admin:write,read)에 따라 API의 접근 제한이 있음.
| NO | API NAME | HTTP<br>METHOD|API PATH | API PARAM | DESC |
|---:|----------:|---:|:----------------------|------------------------:|--------------------:| 
|1|쿠폰발급(*)|POST|/api/coupons| count(필수)| coupon_admin:write 필요|
|2|쿠폰지급(*)|PUT|/api/coupons/{couponCode}/users/{userName}/assign| count(필수)| coupon:write 필요
|3|사용자에게 지급된 쿠폰 조회(*)|GET|/api/coupons/user| | coupon:write<br> token에서 사용자ID 사용|
|4|사용자에게 지급된 쿠폰 조회(*)|GET|/api/coupons/{couponCode}|couponCode(필수)| coupon:write 필요|
|5|지급된 쿠폰사용(*)|PUT|/api/coupons/{couponCode}/users/{userName}/use| couponCode(필수)<br> userName(필수)| coupon_admin:write 필요|
|6|사용된 쿠폰취소(*)|PUT|/api/coupons/{couponCode}/users/{userName}/cancel| couponCode(필수)<br> userName(필수)| coupon_admin:write 필요|
|7|당일 만료된 쿠폰 목록 조회(*)|GET|/api/coupons/expired-coupon| searchDate(필수)<br> page(옵션)<br>size(옵션)| coupon:write 필요|

