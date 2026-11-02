package com.beedigital.beestore;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class BeeStoreApplicationTest {

    @Test
    void main_delegatesToSpringApplicationRun() {
        // SpringApplication.run is mocked: no context, no database, no web server
        try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
            String[] args = {"--spring.profiles.active=test"};

            BeeStoreApplication.main(args);

            spring.verify(() -> SpringApplication.run(BeeStoreApplication.class, args));
        }
    }

    @Test
    void constructor_canBeInstantiated() {
        assertNotNull(new BeeStoreApplication());
    }
}