package itmo.vkr.editors;

import java.io.*;

public interface SourceEditor {

    void edit();

    default void saveFile(String fileName, String content){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName)))
        {
            bw.write(content);
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }

    default String readUsingBufferedReader(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader(fileName));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        reader.close();
        return stringBuilder.toString();
    }

}
