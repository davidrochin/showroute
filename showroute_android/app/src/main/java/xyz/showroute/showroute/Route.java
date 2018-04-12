package xyz.showroute.showroute;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Xml;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import jdrc.util.*;

public class Route {

    public String name;
    public int cityId;
    public int stateId;
    public int countryId;
    public LatLng[] coordinates;

    public int color;

    public Route(String name, int cityId, int stateId, int countryId, LatLng[] coordinates, int color){
        this.name = name;
        this.cityId = cityId;
        this.stateId = stateId;
        this.countryId = countryId;
        this.coordinates = coordinates;
        this.color = color;
    }

    public Route(){
        this("Ruta vacía", 0, 0, 0, new LatLng[0], ColorUtil.randomColor());
    }

    public GoogleMap drawOnMap(GoogleMap map){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(7).color(color);

        //Agregar cada coordenada a la PolyLine
        for (LatLng pos : this.coordinates) {
            polylineOptions.add(pos);
        }

        //Unir el final con el inicio para que la ruta sea cíclica
        //polylineOptions.add(this.coordinates[0]);
        
        map.addPolyline(polylineOptions);
        return map;
    }

    public LatLngBounds getBounds(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for( LatLng x : coordinates ){
            builder.include(x);
        }
        return builder.build();
    }

    public String toString(){
        return name;
    }

}
