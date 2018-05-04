package xyz.showroute.showroute;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;

import jdrc.util.*;

public class Route {

    public String name;

    public int estimatedArrivalTime = 0;

    public int cityId;
    public int stateId;
    public int countryId;

    public LatLng[] coordinates;

    public int color;

    public Route(String name, int cityId, int stateId, int countryId, LatLng[] coordinates, int color, int estimatedArrivalTime){
        this.name = name;
        this.cityId = cityId;
        this.stateId = stateId;
        this.countryId = countryId;
        this.coordinates = coordinates;
        this.color = color;
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Route(String name, int cityId, int stateId, int countryId, LatLng[] coordinates, int color){
        this(name, cityId, stateId, countryId, coordinates, color, -1);
    }

    public Route(){
        this("Ruta vacía", 0, 0, 0, new LatLng[0], ColorUtil.randomColor());
    }

    public GoogleMap drawOnMap(GoogleMap map){

        //Si esta ruta está vacía, saltarse todo
        if(coordinates.length == 0){ return map; }

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(7).color(color);
        //polylineOptions.startCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_drop_up_black_24dp), 1600));
        //polylineOptions.endCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_drop_up_black_24dp), 1600));

        //Agregar cada coordenada a la PolyLine
        for (LatLng pos : this.coordinates) {
            polylineOptions.add(pos);
        }

        //Unir el final con el inicio para que la ruta sea cíclica
        //polylineOptions.add(this.coordinates[0]);

        //Crear la Polyline con todas las opciones definidas
        map.addPolyline(polylineOptions);

        //Dibujar las flechas sobre toda la Polyline
        MarkerOptions arrowMarkerOptions = new MarkerOptions()
                .position(coordinates[0])
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_drop_up_black_24dp))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_small))
                .anchor(0.5f, 0.5f);

        Util.log("Esta ruta tiene " + getSegments().length + " segmentos.");
        Util.log("La longitud de esta ruta es de " + getLength() + " ó " + getLengthInMeters() + " metros.");

        int i = 0; LatLng point; double separation = 0.005; Segment resultantSegment = new Segment();
        while ((point = getPoint(i, separation, resultantSegment)) != null){
            i++;

            Location currentLoc = new Location("currentLoc"); currentLoc.setLatitude(point.latitude); currentLoc.setLongitude(point.longitude);
            Location nextLoc = new Location("nextLoc"); nextLoc.setLatitude(resultantSegment.end.latitude); nextLoc.setLongitude(resultantSegment.end.longitude);
            arrowMarkerOptions.rotation(currentLoc.bearingTo(nextLoc));

            //Dibujar el marcador
            map.addMarker(arrowMarkerOptions.position(point));
        }
        Util.log("Esta ruta va a tener " + i + " flechitas.");

        return map;
    }

    public LatLngBounds getBounds(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for( LatLng x : coordinates ){
            builder.include(x);
        }
        return builder.build();
    }

    public Segment[] getSegments(){
        ArrayList<Segment> segments = new ArrayList<>();
        for (int i = 0; i < coordinates.length - 1; i++){
            segments.add(new Segment(coordinates[i], coordinates[i+1]));
        }
        Segment[] array = new Segment[0];
        return segments.toArray(array);
    }

    public double getLength(){
        double length = 0f;
        double lengthInMeters = 0f;
        Segment[] segments = getSegments();

        for (Segment s : segments){
            length += LatLngOp.getDistance(s.start, s.end);
            lengthInMeters += LatLngOp.getDistanceInMeters(s.start, s.end);
            //Util.log("Longitud de " + s + " es " + LatLngOp.getDistance(s.start, s.end));
        }

        return length;
    }

    public double getLengthInMeters(){
        double lengthInMeters = 0f;
        Segment[] segments = getSegments();

        for (Segment s : segments){
            lengthInMeters += LatLngOp.getDistanceInMeters(s.start, s.end);
        }

        return lengthInMeters;
    }

    public LatLng getPoint(int multiplier, double separation, Segment resultantSegment){

        if(multiplier <= 0){
            resultantSegment.end = getSegments()[0].end;
            return coordinates[0];
        }

        //Si se pasa del limite, regresar null
        if(separation * multiplier > getLength()){
            //Util.log("Separation: " + separation + " x Multiplier: " + multiplier + " = " + (separation * multiplier) + "... getLength() = " + getLength());
            return null;
        } else {
            double distToTravel = separation * multiplier;
            LatLng current = coordinates[0];
            Segment[] segments = getSegments();

            for(Segment s : segments){

                //Si la longitud del segmento es menor a lo que falta por viajar
                if(s.getLength() < distToTravel){
                    distToTravel -= s.getLength();
                    current = s.end;
                }

                //Si la longitud del segmento es mayor a lo que falta por viajar
                else if(s.getLength() > distToTravel){
                    resultantSegment.start = s.start; resultantSegment.end = s.end;
                    return s.getTravel(distToTravel);
                }
            }

            return current;
        }
    }

    public double getDistanceFrom(LatLng from){
        Segment[] segments = getSegments();
        double distance = PolyUtil.distanceToLine(from, segments[0].start, segments[0].end);
        for(Segment segment : segments){
            double thisDistance = PolyUtil.distanceToLine(from, segment.start, segment.end);
            if(thisDistance < distance){
                distance = thisDistance;
            }
        }
        return distance;
    }

    public static Route getNearestToPoint(Route[] routes, LatLng point){
        Route nearest = routes[0];
        for (Route r : routes){
            if(r.getDistanceFrom(point) < nearest.getDistanceFrom(point)){
                nearest = r;
            }
        }
        return nearest;
    }

    public static Route calculateBestRoute(Route[] routes, LatLng from, LatLng to){
        Route bestRoute = routes[0];
        double bestDistance = routes[0].getDistanceFrom(from) + routes[0].getDistanceFrom(to);

        for(Route route : routes){
            double distance = route.getDistanceFrom(from) + route.getDistanceFrom(to);
            //Util.log(route.name + ", " + distance);
            if(distance < bestDistance){
                bestRoute = route;
                bestDistance = distance;
            }
        }

        return bestRoute;
    }

    public String toString(){
        return name;
    }

}
