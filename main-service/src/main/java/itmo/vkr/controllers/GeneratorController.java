package itmo.vkr.controllers;


import itmo.vkr.services.GeneratorService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/generator")
public class GeneratorController {

    final GeneratorService generatorService;

    public GeneratorController(GeneratorService generatorService) {
        this.generatorService = generatorService;
    }


    @GetMapping("generated-project")
    public ResponseEntity<Resource> generateProject() throws IOException {

        Resource resource = generatorService.downloadGeneratedProject().get();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"dirCompressed.zip\"")
                .body(resource);
    }

    @PostMapping("xsd")
    public ResponseEntity<String> uploadXSDFile(@RequestParam("file") MultipartFile file){
        return generatorService.uploadXSDFile(file) ? ResponseEntity.ok("uploaded") : new ResponseEntity<>("some troubles", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

