package xyz.showroute.showroute;

import com.google.android.gms.maps.model.LatLng;

import jdrc.util.LatLngOp;

/**
 * Created by jdrc8 on 19/04/2018.
 */

public class Segment {

    public LatLng start;
    public LatLng end;

    public Segment(){
        this(new LatLng(0,0), new LatLng(0,0));
    }

    public Segment(LatLng start, LatLng end){
        this.start = start;
        this.end = end;
    }

    public LatLng getTravel(double distance){
        LatLng direction = getDirection();
        return LatLngOp.add(start, LatLngOp.mul(direction, distance));
    }

    public LatLng getDirection(){
        double num = LatLngOp.getDistance(start, end);
        LatLng rawDir = LatLngOp.sub(end, start);
        LatLng normDir = LatLngOp.div(rawDir, num);
        return normDir;
    }

    public double getLength(){
        return LatLngOp.getDistance(start, end);
    }

    @Override
    public String toString() {
        return "Start: " + start + ", End: " + end;
    }
}
