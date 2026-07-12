package com.khpi.wanderua.app;

import com.khpi.wanderua.app.WanderUaApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = WanderUaApplication.class,
        properties = "spring.profiles.active=test"
)
class WanderUaApplicationTests {

    @Test
    void contextLoads() {
    }

}
