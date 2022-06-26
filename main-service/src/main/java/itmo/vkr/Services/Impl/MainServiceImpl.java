package itmo.vkr.Services.Impl;

import itmo.vkr.Services.MainService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class MainServiceImpl implements MainService {

    @Override
    public Optional<Resource> downloadGeneratedProject() {
        String filePath = "E:\\ITMO\\ВКР\\Code\\GenServ\\dirCompressed.zip";
        try {
            return Optional.of(new UrlResource(Path.of(filePath).toUri()));
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

    public boolean uploadXSDFile(MultipartFile file) {

        String path = "E:\\ITMO\\ВКР\\Code\\GenServ\\main-service\\src\\main\\resources\\model.xsd";

        return writeFileToDisk(file, path);

    }


    /**
     * Запись файла на диск
     * @param file файл
     * @param path путь для записи
     */
    private boolean writeFileToDisk(MultipartFile file, String path) {
        try {
            Files.copy(file.getInputStream(), Path.of(path), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
