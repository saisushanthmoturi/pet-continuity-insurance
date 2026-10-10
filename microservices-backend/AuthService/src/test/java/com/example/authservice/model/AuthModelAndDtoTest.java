package com.example.authservice.model;

import com.example.authservice.config.DatabaseConfig;
import com.example.authservice.dto.*;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AuthModelAndDtoTest {

    @Test
    void testUserModel() {
        LocalDateTime now = LocalDateTime.now();
        User u = new User(1L, "johndoe", "john@example.com", "hashedpw", "CUSTOMER", "ACTIVE", now, now, now);
        assertEquals(1L, u.getId());
        assertEquals(1L, u.getUserId());
        assertEquals("johndoe", u.getUsername());
        assertEquals("johndoe", u.getFullName());
        assertEquals("john@example.com", u.getEmail());
        assertEquals("hashedpw", u.getPasswordHash());
        assertEquals("CUSTOMER", u.getRole());
        assertEquals("ACTIVE", u.getStatus());
        assertEquals(now, u.getCreatedAt());
        assertEquals(now, u.getUpdatedAt());
        assertEquals(now, u.getLastLoginAt());

        u.setId(2L);
        assertEquals(2L, u.getId());
        u.setUserId(3L);
        assertEquals(3L, u.getUserId());
        u.setUsername("janedoe");
        assertEquals("janedoe", u.getUsername());
        u.setFullName("Jane Doe");
        assertEquals("Jane Doe", u.getFullName());
        u.setEmail("jane@example.com");
        assertEquals("jane@example.com", u.getEmail());
        u.setPasswordHash("newhash");
        assertEquals("newhash", u.getPasswordHash());
        u.setRole("ADMIN");
        assertEquals("ADMIN", u.getRole());
        u.setStatus("INACTIVE");
        assertEquals("INACTIVE", u.getStatus());
        LocalDateTime later = now.plusDays(1);
        u.setCreatedAt(later);
        assertEquals(later, u.getCreatedAt());
        u.setUpdatedAt(later);
        assertEquals(later, u.getUpdatedAt());
        u.setLastLoginAt(later);
        assertEquals(later, u.getLastLoginAt());

        User def = new User();
        assertNull(def.getId());
        assertEquals("ACTIVE", def.getStatus());

        User nullArgs = new User(null, null, null, null, null, null, null, null, null);
        assertEquals("ACTIVE", nullArgs.getStatus());
        assertNotNull(nullArgs.getCreatedAt());
        assertNotNull(nullArgs.getUpdatedAt());

        // Factory createNew with fullName
        User createdWithName = User.createNew("user@example.com", "hash", "Alice Smith", "ADMIN");
        assertEquals("alice_smith", createdWithName.getUsername());
        assertEquals("user@example.com", createdWithName.getEmail());
        assertEquals("ADMIN", createdWithName.getRole());

        // Factory createNew with blank/null fullName
        User createdNoName = User.createNew("bob@example.com", "hash", "  ", "CUSTOMER");
        assertEquals("bob", createdNoName.getUsername());

        User createdNullName = User.createNew("carol@example.com", "hash", null, "CUSTOMER");
        assertEquals("carol", createdNullName.getUsername());
    }

    @Test
    void testRoleEnum() {
        Role[] roles = Role.values();
        assertEquals(5, roles.length);
        assertEquals(Role.CUSTOMER, Role.valueOf("CUSTOMER"));
        assertEquals(Role.UNDERWRITER, Role.valueOf("UNDERWRITER"));
        assertEquals(Role.CLAIMS_OFFICER, Role.valueOf("CLAIMS_OFFICER"));
        assertEquals(Role.CARETAKER, Role.valueOf("CARETAKER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }

    @Test
    void testDtos() {
        RegisterRequest reg = new RegisterRequest("test@example.com", "pwd", "Full Name", "CUSTOMER");
        assertEquals("test@example.com", reg.email());
        assertEquals("pwd", reg.password());
        assertEquals("Full Name", reg.fullName());
        assertEquals("CUSTOMER", reg.role());

        LoginRequest login = new LoginRequest("test@example.com", "pwd");
        assertEquals("test@example.com", login.email());
        assertEquals("pwd", login.password());

        AuthResponse auth = new AuthResponse("token123", 5L, "test@example.com", "Full Name", "CUSTOMER");
        assertEquals("token123", auth.token());
        assertEquals(5L, auth.userId());
        assertEquals("test@example.com", auth.email());
        assertEquals("Full Name", auth.fullName());
        assertEquals("CUSTOMER", auth.role());

        LocalDateTime now = LocalDateTime.now();
        UserDto dto = new UserDto(10L, "user@example.com", "User One", "ADMIN", now);
        assertEquals(10L, dto.id());
        assertEquals("user@example.com", dto.email());
        assertEquals("User One", dto.fullName());
        assertEquals("ADMIN", dto.role());
        assertEquals(now, dto.createdAt());

        UserUpdateRequest upd = new UserUpdateRequest("New Name", "CARETAKER");
        assertEquals("New Name", upd.fullName());
        assertEquals("CARETAKER", upd.role());
    }

    @Test
    void testDatabaseConfig() {
        DatabaseConfig config = new DatabaseConfig();
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        ConnectionFactoryInitializer initializer = config.initializer(connectionFactory);
        assertNotNull(initializer);
    }
}
