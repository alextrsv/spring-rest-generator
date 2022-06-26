package itmo.vkr;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Collector {

    private final static String xsdFromPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\main-service\\src\\main\\resources\\model.xsd";
    private final static String xsdTargetPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\DeserializationModule\\src\\main\\resources\\model.xsd";
    private final String javaGeneratedPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\GenerationModule\\target\\generated-sources\\xtend\\generated";
    private final String javaFinalPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\main-service\\src\\main\\resources\\GeneratedService\\src\\main\\java\\generated";
    private final String xtendGeneratedPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\DeserializationModule\\target\\generated-sources\\jaxb\\generated";
    private final String xtendFinalPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\GenerationModule\\src\\main\\java\\generated";
    private final String mainClassPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\main-service\\src\\main\\resources\\GeneratedApplication.java";
    private final String pomPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\main-service\\src\\main\\resources\\pom.xml";



    public boolean isJavaGenerated() {
        return isFileExist(javaGeneratedPath);
    }

    public boolean isXtendGenerated() {
        return isFileExist(xtendGeneratedPath);
    }

    private boolean isFileExist(String path) {
        return new File(path).exists();
    }


    public void collectJavaSources(){
        moveFiles(javaGeneratedPath, javaFinalPath);
        addMainClass();
    }


    private void addMainClass(){
        copyFiles(mainClassPath, javaFinalPath + "\\GeneratedApplication.java" );
    }

    public void writeXsd(){
        copyFiles(xsdFromPath, xsdTargetPath);
    }

    private void copyFiles(String fromPath, String toPath){
        try {
            if (isFileExist(toPath)) {
                System.out.println( new File(toPath).delete());
            }
            if (isFileExist(fromPath))
                Files.copy(Path.of(fromPath), Path.of(toPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveFiles(String fromPath, String toPath){
        try {
            if (isFileExist(toPath)) {
                if (Files.isDirectory(Path.of(toPath)))
                    System.out.println(FileSystemUtils.deleteRecursively(new File(toPath)));
                else
                    System.out.println( new File(toPath).delete());
            }
            if (isFileExist(fromPath))
                Files.move(Path.of(fromPath), Path.of(toPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void cleanAll() {
        cleanFinalDir();
        cleanJaxBTarget();
        cleanXtendTarget();
    }

    private void cleanXtendTarget() {
        String targetPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\GenerationModule\\target";
        removeDir(targetPath);
    }

    private void cleanJaxBTarget() {
        String targetPath = "E:\\ITMO\\ВКР\\Code\\GenServ\\DeserializationModule\\target";
        removeDir(targetPath);
    }

    private void removeDir(String path) {
        if (isFileExist(path)) {
//            FileSystemUtils.deleteRecursively(new File(path));
            try {
                FileUtils.deleteDirectory(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cleanFinalDir() {
        removeDir(javaFinalPath);
    }
}
