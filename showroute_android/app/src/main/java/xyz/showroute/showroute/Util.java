package xyz.showroute.showroute;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

public class Util {

    public static String varDump(Object o){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Log.i(o.getClass().getName(), gson.toJson(o));
        return gson.toJson(o);
    }

    public static void log(String text){
        Log.d("Quick log", text);
    }
}
