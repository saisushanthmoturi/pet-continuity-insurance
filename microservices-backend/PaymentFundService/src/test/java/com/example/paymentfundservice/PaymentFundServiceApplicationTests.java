package com.example.paymentfundservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class PaymentFundServiceApplicationTests {

    @Test
    void contextLoads() {
        PaymentFundServiceApplication app = new PaymentFundServiceApplication();
        assertNotNull(app);

        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(PaymentFundServiceApplication.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            PaymentFundServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(PaymentFundServiceApplication.class, new String[]{}));
        }
    }
}
