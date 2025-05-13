package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.model.AuthProvider;
import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.model.Role;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String email, String password, PackageType packageType, AuthProvider authProvider) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, Role.REGISTERED, packageType);
        user.setAuthProvider(authProvider);
        userRepository.save(user);
    }

    public boolean needsPackageSelection(User user) {
        return user.getPackageType() == null;
    }

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
    }
}