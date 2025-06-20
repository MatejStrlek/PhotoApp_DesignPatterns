package hr.algebra.photoapp_designpatterns_galic.integration_tests;

import hr.algebra.photoapp_designpatterns_galic.controller.PackageController;
import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.service.PackageService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PackageController.class)
class PackageControllerUITest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private PackageService packageService;

    @Test
    @WithMockUser(username = "testuser", roles = "REGISTERED")
    void shouldProcessPackageSelection() throws Exception {
        mockMvc.perform(post("/select-package")
                        .param("packageType", "FREE")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/photos"));

        verify(userService).setCurrentUserPackage(PackageType.FREE);
    }
}