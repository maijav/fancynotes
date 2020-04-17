package fi.example.fancynotes;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyService extends Service {
    Thread t;
    long id;
    String title;
    String  text;
    DatabaseHelper mDatabaseHelper;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent i, int flags, int startId){
        Log.d("jepajee", "jepajee");
        mDatabaseHelper = new DatabaseHelper(this);
        t = new Thread(() -> {
            doThis();
        });

        t.start();
        return START_STICKY;
    }

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
            Log.d("jepajee", result);
            return result;
        }finally {
            if(in != null){
                in.close();
            }
        }
    }
}
