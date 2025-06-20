package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.aop.TrackPerformance;
import hr.algebra.photoapp_designpatterns_galic.model.*;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLoggerService auditLoggerService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditLoggerService auditLoggerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLoggerService = auditLoggerService;
    }

    public void registerLocalUser(String email, String password, PackageType packageType, AuthProvider authProvider) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, Role.REGISTERED, packageType);
        user.setAuthProvider(authProvider);

        userRepository.save(user);
        logAction(
                user,
                ActionType.REGISTER,
                "Local user registered with email: " + email
        );
    }

    public boolean needsPackageSelection(User user) {
        return user.getPackageType() == null;
    }

    @TrackPerformance
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = extractEmailFromAuthentication(auth);

        if (email == null) {
            throw new UsernameNotFoundException("Email could not be extracted from authenticated principal.");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private String extractEmailFromAuthentication(Authentication auth) {
        if (auth instanceof OAuth2AuthenticationToken token) {
            Object principal = token.getPrincipal();
            if (principal instanceof OAuth2User user) {
                return user.getAttribute("email");
            }
        }

        return auth.getName();
    }

    public void setCurrentUserPackage(PackageType packageType) {
        User currentUser = getCurrentUser();
        currentUser.setPackageType(packageType);

        userRepository.save(currentUser);
        logAction(
                currentUser,
                ActionType.SELECT_PACKAGE,
                "User selected package: " + packageType
        );
    }

    public void updateUser(Long id, String role, String packageType) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(Role.valueOf(role));
        user.setPackageType(PackageType.valueOf(packageType));

        userRepository.save(user);
        logAction(
                getCurrentUser(),
                ActionType.EDIT,
                "User updated with role: " + role + " and package: " + packageType
        );
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        logAction(
                getCurrentUser(),
                ActionType.DELETE,
                "User deleted with ID: " + id
        );
    }

    private void logAction(User user, ActionType actionType, String message) {
        auditLoggerService.logAction(user, actionType, message);
    }
}