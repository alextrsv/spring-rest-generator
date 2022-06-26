package itmo.vkr.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface GeneratorService {
    Optional<Resource> downloadGeneratedProject();

    boolean uploadXSDFile(MultipartFile file);
}
