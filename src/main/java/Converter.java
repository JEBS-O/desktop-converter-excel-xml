import java.io.*;

public class Converter {
    private static Trial trial = loadTrial();
    public static void main(String[] args) {
        Graphic.openApplication();
    }

    public void write(File xmlFile, String xml) {
        try(FileWriter fileWriter = new FileWriter(xmlFile, false)) {
            fileWriter.write(xml);
            fileWriter.flush();

            Graphic.cleanErrorLabel();
            Graphic.setErrorLabel("Готово!");
        } catch(IOException e) {
            Graphic.setErrorLabel("Не удалось записать в файл");
            e.printStackTrace();
            Logs.print(e.getMessage());
        }
    }

    public static boolean checkTrial() {
        return trial.getNumOfLaunches() < 5;
    }

    public static void incrementLaunch() {
        trial.setNumOfLaunches(trial.getNumOfLaunches()+1);
        writeTrial();
    }

    private static Trial loadTrial() {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("Trial.txt")))) {
            return (Trial) ois.readObject();
        } catch(FileNotFoundException e) {
            Logs.print(e.getMessage());
            e.printStackTrace();
            return null;
        } catch(IOException e) {
            Logs.print(e.getMessage());
            e.printStackTrace();
            return null;
        } catch(ClassNotFoundException e) {
            Logs.print(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void writeTrial() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("Trial.txt")))) {
            oos.writeObject(trial);
            oos.flush();
        } catch(FileNotFoundException e) {
            Logs.print(e.getMessage());
            e.printStackTrace();
        } catch(IOException e) {
            Logs.print(e.getMessage());
            e.printStackTrace();
        }
    }
}
