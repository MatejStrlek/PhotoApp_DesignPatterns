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

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public void setCurrentUserPackage(PackageType packageType) {
        User currentUser = getCurrentUser();
        currentUser.setPackageType(packageType);
        userRepository.save(currentUser);
    }

}