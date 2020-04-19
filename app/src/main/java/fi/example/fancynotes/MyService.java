package fi.example.fancynotes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service for fetching notes that are added via web app.
 * Service fetches a json array from web app backend
 *
 * @author  Hanna Tuominen
 * @author  Maija Visala
 * @version 3.0
 * @since   2020-03-09
 */
public class MyService extends Service {
    Thread t;
    long id;
    String title;
    String  text;
    DatabaseHelper mDatabaseHelper;

    /**
     * Mandatory method for service.
     * @param intent mandatory parameter for method.
     * @return null.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * When service starts, create new thread that performs wanted tasks.
     * @param i mandatory parameter for method.
     * @param flags mandatory parameter for method.
     * @param startId mandatory parameter for method.
     * @return
     */
    public int onStartCommand(Intent i, int flags, int startId){
        mDatabaseHelper = new DatabaseHelper(this);
        t = new Thread(() -> {
            doThis();
        });

        t.start();
        return START_STICKY;
    }

    /**
     * Main task of the service. Get data from web app backend and convert it to json objects.
     * Add new note data to SQLite.
     */
    public void doThis(){
        String value;
        JSONObject jsonObject;
        try {
            value = downloadUrl();
            JSONArray jsonArray = new JSONArray(value);
            for(int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
                id = jsonObject.getLong("id");
                title = jsonObject.getString("title");
                text = jsonObject.getString("text");

                mDatabaseHelper.addData(Util.getNewOrderId(this), title, text,"note_placeholder2",null,"No Audio",null,null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        Intent i = new Intent("donaldduck");
        i.putExtra("id", id);
        i.putExtra("title", title);
        i.putExtra("text", text);
        manager.sendBroadcast(i);
    }

    /**
     * Open connection to web app backend and fetch json string that contains
     * data of the notes added via web app.
     * @return string fetched from the web app backend.
     * @throws IOException
     */
    private String downloadUrl() throws IOException {
        InputStream in = null;
        try{
            URL url = new URL("https://fancynotes.herokuapp.com/getAll");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();

            int myChar;
            String result = "";
            while ((myChar = in.read()) != -1){
                result += (char) myChar;
            }
            return result;
        }finally {
            if(in != null){
                in.close();
            }
        }
    }
}
