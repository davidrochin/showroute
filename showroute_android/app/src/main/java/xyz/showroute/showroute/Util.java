package xyz.showroute.showroute;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Util {

    public static String varDump(Object o){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Log.i(o.getClass().getName(), gson.toJson(o));
        return gson.toJson(o);
    }

    public static void toast(Context c, String text){
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }

    public static void log(String text){
        Log.d("Quick log", text);
    }
}
