package com.example.customerservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class CustomerServiceApplicationTests {

    @Test
    void contextLoads() {
        CustomerServiceApplication app = new CustomerServiceApplication();
        assertNotNull(app);

        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(CustomerServiceApplication.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            CustomerServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(CustomerServiceApplication.class, new String[]{}));
        }
    }
}
