package hr.algebra.photoapp_designpatterns_galic.unit_tests;

import hr.algebra.photoapp_designpatterns_galic.model.*;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import hr.algebra.photoapp_designpatterns_galic.service.AuditLoggerService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuditLoggerService auditLoggerService;
    @InjectMocks
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setPackageType(PackageType.PRO);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@example.com");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void shouldRegisterLocalUser() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        userService.registerLocalUser("new@example.com", "pass123", PackageType.FREE, AuthProvider.LOCAL);

        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("new@example.com") &&
                        user.getPassword().equals("encoded") &&
                        user.getPackageType() == PackageType.FREE &&
                        user.getAuthProvider() == AuthProvider.LOCAL
        ));
        verify(auditLoggerService).logAction(any(), eq(ActionType.REGISTER), contains("new@example.com"));
    }

    @Test
    void shouldThrowIfEmailAlreadyExists() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerLocalUser("user@example.com", "pass123", PackageType.FREE, AuthProvider.LOCAL));
    }

    @Test
    void shouldReturnTrueIfPackageSelectionNeeded() {
        testUser.setPackageType(null);
        assertTrue(userService.needsPackageSelection(testUser));
    }

    @Test
    void shouldReturnFalseIfPackageSelected() {
        testUser.setPackageType(PackageType.PRO);
        assertFalse(userService.needsPackageSelection(testUser));
    }

    @Test
    void shouldSetCurrentUserPackage() {
        when(userRepository.save(any())).thenReturn(testUser);
        userService.setCurrentUserPackage(PackageType.GOLD);
        verify(userRepository).save(testUser);
        verify(auditLoggerService).logAction(eq(testUser), eq(ActionType.SELECT_PACKAGE), contains("GOLD"));
    }

    @Test
    void shouldUpdateUser() {
        User admin = new User();
        admin.setEmail("user@example.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(userRepository.save(any())).thenReturn(admin);

        userService.updateUser(2L, "REGISTERED", "FREE");

        verify(userRepository).save(argThat(u ->
                u.getRole() == Role.REGISTERED && u.getPackageType() == PackageType.FREE
        ));
        verify(auditLoggerService).logAction(eq(testUser), eq(ActionType.EDIT), contains("role: REGISTERED"));
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(999L, "REGISTERED", "FREE"));
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(5L);
        verify(userRepository).deleteById(5L);
        verify(auditLoggerService).logAction(eq(testUser), eq(ActionType.DELETE), contains("ID: 5"));
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAllUsers();
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> found = userService.findById(1L);
        assertTrue(found.isPresent());
        assertEquals("user@example.com", found.get().getEmail());
    }
}