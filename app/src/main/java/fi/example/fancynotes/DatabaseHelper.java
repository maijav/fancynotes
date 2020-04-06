package fi.example.fancynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "notes_table";
    //Column 0
    private static final String COL0 = "ID";
    //Column 1
    private static final String COL1 = "orderId";
    //Column 2
    private static final String COL2 = "title";
    //Column 3
    private static final String COL3 = "note";
    //Column 4
    private static final String COL4 = "background";
    //Column 5
    private static final String COL5 = "imgUri";
    //Column 6
    private static final String COL6 = "audioUri";
    //Column 7
    private static final String COL7 = "tags";
    //Column 8
    private static final String COL8 = "time";

    public DatabaseHelper(Context context) {
        super(context,TABLE_NAME,null,1 );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL1 + " TEXT, " + COL2 + " TEXT,"
                    + COL3 + " TEXT," + COL4 + " TEXT,"
                    + COL5 + " TEXT," + COL6 + " TEXT,"
                    + COL7 + " TEXT," + COL8 + " DATETIME)";
            db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addData(int orderId, String title, String note, String background, String imageUri, String audioUri, String tags, Date timedDate) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, orderId);
        contentValues.put(COL2, title);
        contentValues.put(COL3, note);
        contentValues.put(COL4, background);
        contentValues.put(COL5, imageUri);
        contentValues.put(COL6, audioUri);
        contentValues.put(COL7, tags);
        if(timedDate != null) {
            contentValues.put(COL8, Util.parseDateToString(timedDate));
            Log.d("DATEE", " DATABASEHELPER FORMAT DATE " +  Util.parseDateToString(timedDate));
        } else {
            contentValues.put(COL8, "null");
        }

        Log.d(TAG, "addData: Adding" + orderId + " to " + TABLE_NAME);
        Log.d(TAG, "addData: Adding" + title + " to " + TABLE_NAME);
        Log.d(TAG, "addData: Adding" + note + " to " + TABLE_NAME);
        Log.d(TAG, "addData: Adding" + background + " to " + TABLE_NAME);
        // -1 if not inserted correctly
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean updateOrderId (Integer ID, Integer newOrderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, newOrderId);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(ID) } );

        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL1 + " DESC";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getDataById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ TABLE_NAME +" where id="+id+"", null );
        return res;
    }

    public Cursor getLatestInOrder() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME + " WHERE " + COL1 + " = (SELECT MAX(" + COL1 +") from " + TABLE_NAME + ") order by " + COL1 + " ASC LIMIT 1" , null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }
    public Integer deleteNote (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllDataInStringArrayList() {
        ArrayList<String> listData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            listData.add(res.getString(res.getColumnIndex(COL2)));
            res.moveToNext();
        }
        return listData;
    }

    public boolean updateNote (Integer id, String title, String note, String tags) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, title);
        contentValues.put(COL3, note);
        contentValues.put(COL7, tags);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

}
