package com.example.petservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class PetServiceApplicationTests {

    @Test
    void contextLoads() {
        PetServiceApplication app = new PetServiceApplication();
        assertNotNull(app);

        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(PetServiceApplication.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));
            PetServiceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(PetServiceApplication.class, new String[]{}));
        }
    }
}
