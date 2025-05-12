package hr.algebra.photoapp_designpatterns_galic.controller.rest;

import hr.algebra.photoapp_designpatterns_galic.service.ConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UploadTestController {
    private final ConsumptionService consumptionService;

    @Autowired
    public UploadTestController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    //http://localhost:8081/test/upload?size=6
    @RequestMapping(value = "/test/upload", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> testUpload(@RequestParam("size") double uploadSizeMb) {
        try {
            consumptionService.recordUpload(uploadSizeMb);
            return ResponseEntity.ok("Upload recorded: " + uploadSizeMb + " MB");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }
}