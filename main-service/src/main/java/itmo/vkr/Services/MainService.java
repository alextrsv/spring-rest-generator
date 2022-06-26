package itmo.vkr.Services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface MainService {
    Optional<Resource> downloadGeneratedProject();

    boolean uploadXSDFile(MultipartFile file);
}
