package com.example.careverificationservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class CareVerificationServiceApplicationTests {

    @Test
    void contextLoads() {
        CareVerificationServiceApplication app = new CareVerificationServiceApplication();
        assertNotNull(app);

        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(CareVerificationServiceApplication.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            CareVerificationServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(CareVerificationServiceApplication.class, new String[]{}));
        }
    }
}
