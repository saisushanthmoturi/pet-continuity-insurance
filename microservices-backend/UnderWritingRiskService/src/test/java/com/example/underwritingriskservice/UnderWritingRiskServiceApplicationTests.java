package com.example.underwritingriskservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class UnderWritingRiskServiceApplicationTests {

    @Test
    void contextLoads() {
        UnderWritingRiskServiceApplication app = new UnderWritingRiskServiceApplication();
        assertNotNull(app);

        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(UnderWritingRiskServiceApplication.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            UnderWritingRiskServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(UnderWritingRiskServiceApplication.class, new String[]{}));
        }
    }
}
