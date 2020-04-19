package fi.example.fancynotes;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The static Util class is used to reduce code repetition on code that is used in multiple places.
 *
 * @author  Hanna Tuominen
 * @version 3.0
 * @since   2020-03-09
 */

public class Util {
    // simple date format to keep dates same everywhere
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    //database helper for getting ids and data
    static DatabaseHelper mDatabaseHelper;

    /**
     * Used to parse a date into a string.
     * @param date the date attribute that needs to be parsed
     * @return a string that has the date parsed
     */
    public static String parseDateToString(Date date) {
        String dateS = dateFormat.format(date);
        return dateS;
    }

    /**
     * Used to create a new calendar attribute from a date string.
     * @param dateS the date string that should be changed into a calendar attribute
     * @return created calendar attribute
     */
    public static Calendar parseStringToCalendar(String dateS) {
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

    /**
     * Used to change the orderId of notes upon creation of new notes.
     * The latest id is gotten via database helper and a new id is created based on that.
     *
     * @param context the current context of the app
     * @return the new order int for the note
     */
    public static int getNewOrderId(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        Cursor data = mDatabaseHelper.getLatestInOrder();

        int orderIdFromData = 0;
        while(data.moveToNext()){
            orderIdFromData = data.getInt(1);
        }
        int orderId = orderIdFromData;

        if(orderId == 0) {
            return 1;
        } else {
            orderId++;
            return orderId;
        }
    }

}
