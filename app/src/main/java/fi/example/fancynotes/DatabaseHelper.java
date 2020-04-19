package fi.example.fancynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.Date;

/**
 * The DatabaseHelper class is used to store the SQLite database and edit it.
 *
 * Via the DatabaseHelper class each note can be created, updated and deleted according to needed attributes.
 * This class is used all over the project and it is used to edit and manage one table named notes_table.
 *
 * @author Hanna Tuominen
 * @author Maija Visala
 * @version 3.0
 * @since 2020-03-09
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    //Table name
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

    /**
     * The constructor of the DatabaseHelper class that requires current context to update information in the database.
     * User super to make the table
     * @param context the current context of the app.
     */
    public DatabaseHelper(Context context) {
        super(context,TABLE_NAME,null,1 );
    }

    /**
     * Lifecycle method on create to create a new table with the wanted columns.
     * @param db the SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
            String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL1 + " TEXT, " + COL2 + " TEXT,"
                    + COL3 + " TEXT," + COL4 + " TEXT,"
                    + COL5 + " TEXT," + COL6 + " TEXT,"
                    + COL7 + " TEXT," + COL8 + " DATETIME)";
            db.execSQL(createTable);
    }

    /**
     * Lifecycle method.
     * @param db database
     * @param i i
     * @param i1 i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    /**
     * Method is used to remove all of the data in the database.
     * @param context the current context.
     * @return a toast message to inform the user that all data has been deleted from the database.
     */
    public Toast removeAllData(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        Toast toast = Toast.makeText(context, "All data has been deleted", Toast.LENGTH_LONG);
        return toast;
    }

    /**
     * Method is used to add new notes to the database with all of the needed info and return true or false if the adding was successful.
     * @param orderId the orderid of the new note
     * @param title the tittle of the new note
     * @param note the text of the new note
     * @param background the background color of the new note
     * @param imageUri the uri of the new note image
     * @param audioUri the audio uri of the new note
     * @param tags the tags of the new note
     * @param timedDate the timed note of the new note
     * @return true or false if the adding was successful
     */
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
        } else {
            contentValues.put(COL8, "null");
        }
        // -1 if not inserted correctly
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Method is used to update the order id of a note whenever needed.
     * @param ID the old id of the note
     * @param newOrderId the new id of the note
     * @return true when the new order has been successfully set
     */
    public boolean updateOrderId (Integer ID, Integer newOrderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, newOrderId);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(ID) } );

        return true;
    }

    /**
     * Method is used to return all of the data in the database to be displayed.
     * @return Cursor with all of the notes in the data. The data is ordered by OrderID.
     */
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL1 + " DESC";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    /**
     * Method is used to get the latest note in the current order to set a new order id for new note upon creation.
     * @return the Cursor info of the last note created.
     */
    public Cursor getLatestInOrder() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME + " WHERE " + COL1 + " = (SELECT MAX(" + COL1 +") from " + TABLE_NAME + ") order by " + COL1 + " ASC LIMIT 1" , null );
        return res;
    }

    /**
     * Method used to delete a note based on id from the database.
     * @param id the id of the note that is wanted to be deleted
     * @return if the deletion was succesfull return positive number if not return negative
     */
    public Integer deleteNote (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    /**
     * Method used to update the SQLite database note.
     * @param id the id of the note
     * @param title the tittle of the note
     * @param note the text of the note
     * @param tags the tags of the note
     * @return
     */
    public boolean updateNote (Integer id, String title, String note, String tags) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, title);
        contentValues.put(COL3, note);
        contentValues.put(COL7, tags);
        //update based on id
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

}
