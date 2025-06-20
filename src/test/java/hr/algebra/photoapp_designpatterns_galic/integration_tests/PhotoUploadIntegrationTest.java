package hr.algebra.photoapp_designpatterns_galic.integration_tests;

import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.model.Role;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PhotoUploadIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "testuser", roles = "REGISTERED")
    void shouldRenderUploadPage() throws Exception {
        User userWithPackage = new User();
        userWithPackage.setEmail("testuser");
        userWithPackage.setRole(Role.REGISTERED);
        userWithPackage.setPackageType(PackageType.FREE);

        when(userRepository.findByEmail("testuser")).thenReturn(Optional.of(userWithPackage));

        mockMvc.perform(get("/photos/upload"))
                .andExpect(status().isOk())
                .andExpect(view().name("photo-upload"))
                .andExpect(model().attributeExists("photoUploadDTO"));
    }

    @Test
    @WithMockUser(username = "userWitoutPackageSelected", roles = "REGISTERED")
    void shouldRedirectToSelectPackageIfNoPackageSelected() throws Exception {
        Mockito.reset(userRepository);

        User userWithoutPackage = new User();
        userWithoutPackage.setEmail("userWitoutPackageSelected");
        userWithoutPackage.setRole(Role.REGISTERED);
        userWithoutPackage.setPackageType(null);

        when(userRepository.findByEmail("userWitoutPackageSelected"))
                .thenReturn(Optional.of(userWithoutPackage));

        mockMvc.perform(get("/photos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/select-package"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "REGISTERED")
    void shouldRenderChoosePackagePage() throws Exception {
        mockMvc.perform(get("/select-package"))
                .andExpect(status().isOk())
                .andExpect(view().name("select-package"))
                .andExpect(model().attributeExists("packageTypes"));
    }
}