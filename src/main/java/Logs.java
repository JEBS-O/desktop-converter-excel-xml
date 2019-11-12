import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logs {
    private static File logsFile = new File("logs.txt");

    public static void print(String log) {
        try(FileWriter writer = new FileWriter(logsFile, true)) {
            writer.write("[" + DateCreator.getDateForLogs() + "] " + log + "\r\n");
            writer.flush();
        } catch(IOException e) {
            System.err.println("logs.txt file not found");
            e.printStackTrace();
        }
    }
}
