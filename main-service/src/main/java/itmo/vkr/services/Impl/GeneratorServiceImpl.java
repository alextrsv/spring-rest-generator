package itmo.vkr.services.Impl;

import itmo.vkr.Collector;
import itmo.vkr.MavenExecutor;
import itmo.vkr.services.GeneratorService;
import itmo.vkr.Zipper;
import itmo.vkr.editors.JavaSourceEditor;
import itmo.vkr.editors.JaxBSourseEditor;
import lombok.SneakyThrows;
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
public class GeneratorServiceImpl implements GeneratorService {

    final Collector collector;
    final JaxBSourseEditor jaxBSourseEditor;

    public GeneratorServiceImpl(Collector collector, JaxBSourseEditor jaxBSourseEditor) {
        this.collector = collector;
        this.jaxBSourseEditor = jaxBSourseEditor;
    }

    private void runDeserializationWork(){
        MavenExecutor.execute("mvn.cmd compile -pl DeserializationModule -am\n");

        while (!collector.isXtendGenerated()){
            try {
                System.out.println("NO");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("jaxB generation ended UP!");
        jaxBSourseEditor.edit();
    }

    private void runGenerationWork(){
        MavenExecutor.execute("mvn.cmd compile -pl GenerationModule -am"); // генерируем исходники

        while (!collector.isJavaGenerated()){
            try {
                System.out.println("NO");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("xtend generation ended UP!");
    }
    @SneakyThrows
    private void startGeneration() {
        collector.cleanAll();
//        подать на вход xsd
        collector.writeXsd();
//        генерируем jaxB
        runDeserializationWork();
//        генерация Java исходников
        runGenerationWork();

        collector.collectJavaSources();

        new JavaSourceEditor().edit();

        Zipper.zipSourceToArchive("E:\\ITMO\\ВКР\\Code\\GenServ\\main-service\\src\\main\\resources\\GeneratedService",
                "SpringCrudService.zip");

        System.out.println("DONE!");
    }

    @Override
    public Optional<Resource> downloadGeneratedProject() {
        startGeneration();
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
