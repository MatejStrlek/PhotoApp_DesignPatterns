package hr.algebra.photoapp_designpatterns_galic.ui_tests;

import hr.algebra.photoapp_designpatterns_galic.controller.PhotoController;
import hr.algebra.photoapp_designpatterns_galic.decorator.PhotoUploadComponent;
import hr.algebra.photoapp_designpatterns_galic.service.PhotoShowService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = PhotoController.class)
class UploadControllerUITest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private PhotoUploadComponent photoUploadComponent;
    @MockBean
    private PhotoShowService photoShowService;

    @Test
    @WithMockUser(username = "testuser", roles = "REGISTERED")
    void shouldReturnUploadViewWithForm() throws Exception {
        mockMvc.perform(get("/photos/upload"))
                .andExpect(status().isOk())
                .andExpect(view().name("photo-upload"))
                .andExpect(model().attributeExists("photoUploadDTO"));
    }
}