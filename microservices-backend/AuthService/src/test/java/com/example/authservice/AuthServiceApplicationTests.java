package com.example.authservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
        AuthServiceApplication app = new AuthServiceApplication();
        assertNotNull(app);

        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(AuthServiceApplication.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            AuthServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(AuthServiceApplication.class, new String[]{}));
        }
    }
}
