package itmo.vkr.editors;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class JaxBSourseEditor implements SourceEditor {

    @Override
    public void edit() {
        String dirName = "E:\\ITMO\\ВКР\\Code\\GenServ\\DeserializationModule\\target\\generated-sources\\jaxb\\generated";
        File generatedSrcDir = new File(dirName);
        handleDir(generatedSrcDir);
    }


    private void handleDir(File dir) {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(Path.of(dir.getAbsolutePath()))) {
            StringBuilder builder = new StringBuilder("package generated;\n" +
                    "\n" +
                    "import java.util.ArrayList;\n" +
                    "import java.util.List;\n" +
                    "import org.eclipse.xtend.lib.annotations.EqualsHashCode;\n" +
                    "import org.eclipse.xtend.lib.annotations.ToString;\n" +
                    "import javax.persistence.JoinColumn;\n" +
                    "import generator.annotations.Entity;\n" +
                    "import org.eclipse.xtend.lib.annotations.Accessors;\n\n\n");
            String savedFileName = "E:\\ITMO\\ВКР\\Code\\GenServ\\GenerationModule\\src\\main\\java\\generated\\Model.xtend";
            for (Path srcFile : files) {
                System.out.println(srcFile.toString());
                if (!srcFile.toString().contains("ObjectFactory.java")
                && !srcFile.toString().contains("package-info.java")) {
                    System.out.println(srcFile.getFileName());
                    builder.append(handleFile(srcFile.toString()));
                }
            }
            saveFile(savedFileName, builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleFile(String fileName) throws IOException {
        String contents = readUsingBufferedReader(fileName);

        String annotation = "@Entity\n";

        StringBuilder builder = new StringBuilder(contents);


        while (builder.indexOf("@XmlElement(required = true)") != -1)
            builder.delete(builder.indexOf("@XmlElement(required = true)"),
                    builder.indexOf("@XmlElement(required = true)") + "@XmlElement(required = true)".length());


        builder.delete(builder.indexOf("//"), builder.indexOf("public class"));
        builder.delete(builder.indexOf("/**"), builder.length());
        builder.insert(builder.length(), "}\n");


        builder.insert(builder.indexOf("public class"), annotation);
        builder.append("\n\n\n");


        String result = builder.toString();
        System.out.println("===================================================");
        System.out.println(result);

        return result;
    }

    @Override
     public void saveFile(String fileName, String content){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName.replace(".java", ".xtend"))))
        {
            bw.write(content);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }


}
