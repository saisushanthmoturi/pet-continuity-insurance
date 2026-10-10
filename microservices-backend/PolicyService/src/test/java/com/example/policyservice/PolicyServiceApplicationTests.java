package com.example.policyservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class PolicyServiceApplicationTests {

    @Test
    void contextLoads() {
        PolicyServiceApplication app = new PolicyServiceApplication();
        assertNotNull(app);

        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(PolicyServiceApplication.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            PolicyServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(PolicyServiceApplication.class, new String[]{}));
        }
    }
}
