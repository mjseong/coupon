package com.assignment.coupon;

import com.assignment.coupon.domain.entity.Coupon;
import com.assignment.coupon.repository.CouponRepository;
import com.assignment.coupon.service.impl.CustomUserDetailService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConponIntegrationTests {


    Logger logger = LoggerFactory.getLogger(ConponIntegrationTests.class);

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CouponRepository couponRepository;
    @Autowired
    CustomUserDetailService customUserDetailService;
    @Autowired
    ObjectMapper mapper;

    private static SecureRandom random;
    private static String accessToken;
    private static String adminAccessToken;
    private static String dumyCouponCode;
    private static String testUserName;


    @BeforeAll
    public void init(){
        this.random = new SecureRandom();
        try{
            //testUser생성
            this.testUserName = "smj";
            customUserDetailService.createUser("smj","123456", false).getUsername();
            //adminUser생성
            customUserDetailService.createUser("admin","123456", true).getUsername();
        }catch (Exception e){
            logger.debug(e.getMessage());
        }

        this.dumyCouponCode = couponRepository.save(Coupon.newCoupon("ubot_corp",null)).getCouponCode();

    }

    //signup
    @Test
    @Order(1)
    void signupUserTest() throws Exception{
        String testUser = "testUser-"+this.random.nextInt(10000);
        Map<String, Object> map = new HashMap<>();
        map.put("userName",testUser);
        map.put("password","123456");
        String jsonBody = this.mapper.writeValueAsString(map);

        MvcResult signUpResult = this.mockMvc.perform(post("/signup")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonBody))
            .andExpect(status().isOk())
            .andReturn();
    }

    //signin user
    @Test
    @Order(2)
    void signinUserTest() throws Exception {

        MvcResult signinResult = this.mockMvc.perform(post("/signin")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("userName",this.testUserName)
                    .param("password","123456"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = signinResult.getResponse().getContentAsString();
        Assertions.assertNotNull(json);

        Map<String,Object> resultMap = this.mapper.readValue(json, new TypeReference<Map>(){});
        this.accessToken = (String) resultMap.get("access_token");

    }

    //signin admin
    @Test
    @Order(3)
    void signinAdminTest() throws Exception {

        MvcResult signinResult = this.mockMvc.perform(post("/signin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("userName","admin")
                .param("password","123456"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = signinResult.getResponse().getContentAsString();
        Assertions.assertNotNull(json);

        Map<String,Object> resultMap = this.mapper.readValue(json, new TypeReference<Map>(){});
        this.adminAccessToken = (String) resultMap.get("access_token");

    }

    @Test
    @Order(4)
    void tokenPassTest() throws Exception {

        this.mockMvc.perform(get("/authtoken-test")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @Order(5)
    void badParamTest() throws Exception {

        this.mockMvc.perform(get("/authtoken-test")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("userName","<script>a=b</script>")
                .param("couponCode","adada-acaaab-aaa-daaa1213-aaad; select * from dual")
                .param("createDate", "1999-102-111"))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

    //coupon create; post /api/coupons

    /**
     * 쿠폰 발급 n개
     * coupon create: post /api/coupons
     * input param
     * @throws Exception
     */

    @Transactional
    @Test
    @Order(6)
    void createCouponTest() throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("count", 10);

        String jsonBody = this.mapper.writeValueAsString(map);

        MvcResult couponResult = this.mockMvc.perform(post("/api/coupons")
                        .header("Authorization", "bearer "+this.adminAccessToken)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String json = couponResult.getResponse().getContentAsString();
        Assertions.assertNotNull(json);

        Map<String,Object> resultMap = this.mapper.readValue(json, new TypeReference<Map>(){});
        Assertions.assertEquals(10, resultMap.get("couponCount"));

    }

    /**
     * 사용자에게 단일 쿠폰 지급;
     * input param: couponCode, userId
     * coupon assign by issuer ; put /api/coupons/{couponCode}/users/{userId}/assign
     */

    @Test
    @Order(7)
    void assignCouponTest() throws Exception {

        String apiUrl = "/api/coupons/"+this.dumyCouponCode+"/users/"+this.testUserName+"/assign";
        this.mockMvc.perform(put(apiUrl)
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print());
    }


    /**
     * 사용자에게 지급된 쿠폰 조회;
     * access_token사용;
     * input param: couponId
     * coupon by userId : get /api/coupons/user
     */
//    @Disabled
    @Test
    @Order(9)
    void findAssignedCouponTest() throws Exception {

        MvcResult couponResult = this.mockMvc.perform(get("/api/coupons/user")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }


    /**
     * 지급된 쿠폰 사용;
     * access_token사용 role_admin 필요
     * input param: userId, couponId
     * coupon use ; put /api/coupons/{couponCode}/users/{username}/use
     */
//    @Disabled
    @Test
    @Order(100)
    void useCouponTest() throws Exception {
        String apiUrl = "/api/coupons/"+this.dumyCouponCode+"/users/"+this.testUserName+"/use";
        MvcResult couponResult = this.mockMvc.perform(put(apiUrl)
                .header("Authorization", "bearer "+this.adminAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * 지급된 쿠폰 취소;
     * access_token사용 role_admin 필요
     * input param: userId, couponId
     * coupon cancel : put /api/coupons/{couponCode}/users/{username}/cancel
     */
//    @Disabled
    @Test
    @Order(101)
    void cancelCouponTest() throws Exception {

        String apiUrl = "/api/coupons/"+this.dumyCouponCode+"/users/"+this.testUserName+"/cancel";
        MvcResult couponResult = this.mockMvc.perform(put(apiUrl)
                .header("Authorization", "bearer "+this.adminAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * 당일만료된 전체쿠폰 목록
     * input param : date
     * used coupon-list : get  /api/coupons/expired-coupon
     */
//    @Disabled
    @Test
    @Order(200)
    void findTodayExpireCuoponTest() throws Exception {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                                .withLocale(Locale.KOREA)
                                                                .withZone(ZoneId.systemDefault());

        String datePlusOne = dateTimeFormatter.format(Instant.now().truncatedTo(ChronoUnit.DAYS));

        MvcResult couponResult = this.mockMvc.perform(get("/api/coupons/expired-coupon")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("searchDate", datePlusOne)
                .param("page", "1") // page 0~n
                .param("size", "100")) // size
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = couponResult.getResponse().getContentAsString();
        logger.info(json);
    }

    /**
     * 사용자에게 대량 쿠폰 지급;
     * coupon bulk-assign by issuer; put /api/coupons/bulk-assign
     */
//    @Test
//    @Order(300)
//    void createCuoponTest() throws Exception {
//        Map<String, Object> map = new HashMap();
//        map.put("count", 10);
//
//        String jsonBody = this.mapper.writeValueAsString(map);
//
//        MvcResult couponResult = this.mockMvc.perform(post("/api/coupon")
//                .header("Authorization", "bearer "+this.accessToken)
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(jsonBody))
//                .andExpect(status().isOk())
//                .andReturn();
//    }

}
