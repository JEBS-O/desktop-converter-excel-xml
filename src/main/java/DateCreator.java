import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCreator {
    public static String getDateForXmlFile() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
    }
    public static String getDateForLogs() {
        return new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date());
    }
}
