package jdrc.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jdrc8 on 19/04/2018.
 */

public class LatLngOp {

    public static LatLng add(LatLng a, LatLng b){
        return new LatLng(a.latitude + b.latitude, a.longitude + b.longitude);
    }

    public static LatLng sub(LatLng a, LatLng b){
        LatLng r = new LatLng(a.latitude - b.latitude, a.longitude - b.longitude);
        return r;
    }

    public static LatLng div(LatLng a, double divisor){
        return new LatLng(a.latitude / divisor, a.longitude / divisor);
    }

    public static LatLng mul(LatLng a, double multiplier){
        return new LatLng(a.latitude * multiplier, a.longitude * multiplier);
    }

    public static double getLength(LatLng l){
        return Math.sqrt(Math.pow(l.latitude, 2) + Math.pow(l.longitude, 2));
    }

    public static double getDistance(LatLng a, LatLng b){
        return getLength(sub(b, a));
    }

    public static double getDistanceInMeters(LatLng a, LatLng b){
        Location locA = new Location("locA"); locA.setLatitude(a.latitude); locA.setLongitude(a.longitude);
        Location locB = new Location("locB"); locB.setLatitude(b.latitude); locB.setLongitude(b.longitude);
        return locA.distanceTo(locB);
    }

    public static LatLng pointFromDirection(LatLng origin, LatLng direction, double longitud){

        return null;
    }

}
