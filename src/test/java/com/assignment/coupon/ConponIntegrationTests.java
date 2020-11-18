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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    String accessToken;

    @BeforeEach
    void init(){
        this.mapper = new ObjectMapper();
    }

    //signup
    @Test
    @Order(1)
    void signupTest() throws Exception{

        Map<String, Object> map = new HashMap<>();
        map.put("userId","smj");
        map.put("password","123456");
        String jsonBody = this.mapper.writeValueAsString(map);

        MvcResult signUpResult = this.mockMvc.perform(post("/signup")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonBody))
            .andExpect(status().isOk())
            .andReturn();
    }

    //signin
    @Test
    @Order(2)
    void signinTest() throws Exception {

        MvcResult signinResult = this.mockMvc.perform(post("/signin")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("userId","smj")
                    .param("password","123456"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = signinResult.getResponse().getContentAsString();
        Assertions.assertNull(json);

        Map<String,Object> resultMap = this.mapper.readValue(json, new TypeReference<Map>(){});
        this.accessToken = (String) resultMap.get("access_token");

    }

    //coupon generator; post
    @Disabled
    @Test
    @Order(3)
    void createCuoponTest() throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("count", 10);

        String jsonBody = this.mapper.writeValueAsString(map);

        MvcResult couponResult = this.mockMvc.perform(post("/api/coupon")
                        .header("Authorization", "bearer "+this.accessToken)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andReturn();
    }

    //coupon assign ; put /api/coupon/{couponId}/assign

    //coupon use ; put /api/coupon/{couponId}/use

    //coupon cancel ; delete /api/coupon/{couponId}/cancel

    //coupon list by userId ; get

    //used coupon list ; get
}
