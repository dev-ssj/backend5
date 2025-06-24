package com.example.backendproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")//요거추가 -> application-test.properties에 test용 db설정.
@SpringBootTest
class BackendProjectApplicationTests {

    @Test
    void contextLoads() {
    }

}
