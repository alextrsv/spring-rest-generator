package itmo.vkr;

import itmo.vkr.editors.JavaSourceEditor;
import itmo.vkr.editors.JaxBSourseEditor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Collector collector = new Collector();

        collector.cleanAll();
//        подать на вход xsd
        collector.writeXsd();


//        //генерируем jaxB
        MavenExecutor.execute("mvn.cmd compile -pl DeserializationModule -am\n");


        while (!collector.isXtendGenerated()){
            try {
                System.out.println("NO");
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("jaxB generation ended UP!");
//        //редачим jaxB файлы
        new JaxBSourseEditor().edit();
//        //перемещаем отредаченные в исходники для генерации xtend
//        collector.collectJaxBSources();


//        //генерация Java исходников
        System.out.println("xtend generation started!");

        MavenExecutor.execute("mvn.cmd compile -pl GenerationModule -am"); // генерируем исходники

        while (!collector.isJavaGenerated()){
            try {
                System.out.println("NO");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("xtend generation ended UP!");

        collector.collectJavaSources();

        new JavaSourceEditor().edit();

        Zipper.main(null);
        System.out.println("DONE!");
    }
}
