package itmo.vkr.editors;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaSourceEditor  implements SourceEditor{

    @Override
    public void edit() {
        String dirName = "E:\\ITMO\\ВКР\\Code\\GenServ\\main-service\\src\\main\\resources\\GeneratedService\\src\\main\\java\\generated";
        File generatedSrcDir = new File(dirName);
        handleDir(generatedSrcDir);
    }

    private void handleDir(File dir) {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(Path.of(dir.getAbsolutePath()))) {
            for (Path srcFile : files) {
                System.out.println(srcFile.toString());
                if (!srcFile.toString().contains("Application") && !srcFile.toString().contains("controllers")
                && !srcFile.toString().contains("entity") && !srcFile.toString().contains("repositories")
                && !srcFile.toString().contains("services")) {
                    System.out.println(srcFile.getFileName());
                    handleFile(srcFile.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleFile(String fileName) throws IOException {
        String contents = readUsingBufferedReader(fileName);

        StringBuilder builder = new StringBuilder(contents);

        builder.insert(builder.indexOf("@Id") + "@Id".length() + 2,
                " @GeneratedValue(strategy = GenerationType.IDENTITY)\n");


        if (builder.indexOf("@OneToMany(mappedBy =") != -1) {
            builder.delete(builder.indexOf("@JsonIgnore") - 4, builder.indexOf("@JsonIgnore") - 3);
            builder.insert(builder.indexOf("@JsonIgnore") - 4,
                    ", cascade = CascadeType.ALL");
        }

        String result = builder.toString();
        System.out.println("===================================================");
        System.out.println(result);

        saveFile(fileName, result);
    }



}
