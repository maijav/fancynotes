package fi.example.fancynotes;

import android.util.Log;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    public static String parseDateToString(Date date) {
        String dateS = dateFormat.format(date);
        Log.d("DATEE", " PARSE DATE TO STRING DATES " + dateS);
        return dateS;
    }

    public static Calendar parseStringToCalendar(String dateS) {
        Log.d("DATEE", " PARSE STRING TO CALENDAR " + dateS);
        Calendar date = Calendar.getInstance();
        Date d = new Date();
        if (dateS != null) {
            try {
                d = dateFormat.parse(dateS);
                date.setTime(d);
                return date;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
