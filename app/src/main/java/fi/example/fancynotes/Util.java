package fi.example.fancynotes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static Date parseDateFormat(String dateS) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date();
        try {
            date = dateFormat.parse(dateS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
