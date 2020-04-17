package fi.example.fancynotes;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    static DatabaseHelper mDatabaseHelper;
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

    public static int getNewOrderId(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        Cursor data = mDatabaseHelper.getLatestInOrder();

        int orderIdFromData = 0;
        while(data.moveToNext()){
            orderIdFromData = data.getInt(1);
        }
        int orderId = orderIdFromData;

        if(orderId == 0) {
            Log.d("ORDERIDD", orderId + " UTIL 1");
            return 1;
        } else {
            orderId++;
            Log.d("ORDERIDD", orderId + " UTIL");
            return orderId;
        }
    }

}
