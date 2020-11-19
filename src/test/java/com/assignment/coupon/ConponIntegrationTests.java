package com.assignment.coupon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConponIntegrationTests {

    @Autowired
    MockMvc mockMvc;
    ObjectMapper mapper;
    static String accessToken;
    static String adminAccessToken;

    @BeforeEach
    void init(){
        this.mapper = new ObjectMapper();
    }

    //signup
    @Test
    @Order(1)
    void signupUserTest() throws Exception{

        Map<String, Object> map = new HashMap<>();
        map.put("userName","smj");
        map.put("password","123456");
        String jsonBody = this.mapper.writeValueAsString(map);

        MvcResult signUpResult = this.mockMvc.perform(post("/signup")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonBody))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    @Order(2)
    void signupAdminTest() throws Exception{

        Map<String, Object> map = new HashMap<>();
        map.put("userName","admin");
        map.put("password","123456");
        map.put("adminRole","true");
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
    @Order(3)
    void signinUserTest() throws Exception {

        MvcResult signinResult = this.mockMvc.perform(post("/signin")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("userName","smj")
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
    @Order(4)
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
    @Order(5)
    void tokenPassTest() throws Exception {

        MvcResult couponResult = this.mockMvc.perform(get("/authtoken-test")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    //coupon create; post /api/coupons

    /**
     * 쿠폰 발급 n개
     * coupon create: post /api/coupons
     * input param
     * @throws Exception
     */

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
                .andReturn();
    }

    /**
     * 사용자에게 단일 쿠폰 지급;
     * input param: couponCode, userId
     * coupon assign by issuer ; put /api/coupons/{couponCode}/users/{userId}/assign
     */

    @Disabled
    @Test
    @Order(6)
    void assignCouponTest() throws Exception {

        MvcResult couponResult = this.mockMvc.perform(put("/api/coupons/{couponCode}/users/{userId}/assign")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }


    /**
     * 사용자에게 대량 쿠폰 지급;
     * coupon bulk-assign by issuer; put /api/coupons/bulk-assign
     */
//    @Test
//    @Order(3)
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

    /**
     * 지급된 쿠폰 사용;
     * access_token사용;
     * role_user : check token_sub == userId
     * role_admin : ignore token_sub != userId
     * input param: userId, couponId
     * coupon use ; put /api/coupons/{couponCode}/use
     */
    @Disabled
    @Test
    @Order(5)
    void useCouponTest() throws Exception {

        MvcResult couponResult = this.mockMvc.perform(post("/api/coupons/{couponCode}/use")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * 지급된 쿠폰중 취소;
     * access_token사용;
     * role_user : check token_sub == userId
     * role_admin : ignore token_sub != userId
     * input param: userId, couponId
     * coupon cancel : put /api/coupons/{couponCode}/cancel
     */
    @Disabled
    @Test
    @Order(6)
    void cancelCouponTest() throws Exception {

        MvcResult couponResult = this.mockMvc.perform(put("/api/coupons/{couponCode}/cancel")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * 사용자에게 지급된 쿠폰 조회;
     * access_token사용;
     * input param: couponId
     * coupon by userId : get /api/coupons
     */
    @Disabled
    @Test
    @Order(7)
    void findAssignedCouponTest() throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("count", 10);

        String jsonBody = this.mapper.writeValueAsString(map);

        MvcResult couponResult = this.mockMvc.perform(post("/api/coupons/{couponCode}")
                .header("Authorization", "bearer "+this.accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn();
    }


    /**
     * 당일만료된 전체쿠폰 목록
     * input param : date
     * used coupon-list : get  /api/coupons/expired-coupon
     */
    @Disabled
    @Test
    @Order(8)
    void createCuoponTest() throws Exception {

        MvcResult couponResult = this.mockMvc.perform(post("/api/coupons/expired-coupon")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("",""))
                .andExpect(status().isOk())
                .andReturn();
    }

}
