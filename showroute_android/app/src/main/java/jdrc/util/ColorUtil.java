package jdrc.util;

import android.graphics.Color;

import java.util.Random;

import xyz.showroute.showroute.Util;

/**
 * Created by jdrc8 on 12/03/2018.
 */

public class ColorUtil {

    static int newSeed = 1;

    public static int randomColor(){
        return randomColor(newSeed);
    }

    public static int randomColor(int seed){
        Random random = new Random(seed);
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        newSeed = random.nextInt();
        Util.log("" + Color.rgb(r,g,b));
        return Color.rgb(r,g,b);
    }

}
