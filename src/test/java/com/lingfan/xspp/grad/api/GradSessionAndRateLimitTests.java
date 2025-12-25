package com.lingfan.xspp.grad.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "grad.recommend.rate-limit-per-session-per-minute=3",
        "grad.recommend.rate-limit-per-ip-per-minute=3",
        "grad.recommend.rate-limit-retry-after-seconds=33"
})
public class GradSessionAndRateLimitTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void sessionInit_returnsUserAndSessionIds() throws Exception {
        MvcResult res = mockMvc.perform(post("/api/grad/session/init")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = mapper.readTree(res.getResponse().getContentAsString());
        Assertions.assertEquals("ok", body.get("status").asText());
        JsonNode data = body.get("data");
        Assertions.assertNotNull(data);
        Assertions.assertTrue(data.get("userId").asLong() > 0);
        Assertions.assertTrue(data.get("sessionId").asText().length() > 10);
    }

    @Test
    void rateLimit_perSession_kicksInAfterThreshold_withRetryAfter() throws Exception {
        // init session
        MvcResult res = mockMvc.perform(post("/api/grad/session/init").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode data = mapper.readTree(res.getResponse().getContentAsString()).get("data");
        String sessionId = data.get("sessionId").asText();

        // 3 allowed requests to a protected endpoint, 4th should be 429 (threshold=3)
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/grad/dicts/directions")
                            .header("X-Session-Id", sessionId))
                    .andExpect(status().isOk());
        }
        MvcResult limited = mockMvc.perform(get("/api/grad/dicts/directions")
                        .header("X-Session-Id", sessionId))
                .andExpect(status().isTooManyRequests())
                .andReturn();
        String retryAfter = limited.getResponse().getHeader("Retry-After");
        Assertions.assertEquals("33", retryAfter);
    }

    @Test
    void rateLimit_perIp_kicksInWithoutSession_withRetryAfter() throws Exception {
        // 3 allowed requests without session header, 4th should be 429, and has Retry-After
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/grad/dicts/directions"))
                    .andExpect(status().isOk());
        }
        MvcResult limited = mockMvc.perform(get("/api/grad/dicts/directions"))
                .andExpect(status().isTooManyRequests())
                .andReturn();
        String retryAfter = limited.getResponse().getHeader("Retry-After");
        Assertions.assertEquals("33", retryAfter);
    }
}
