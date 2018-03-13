package xyz.showroute.showroute;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jdrc8 on 06/03/2018.
 */

public class KmlParser {

    public static Route[] getRoutes(InputStream in) throws XmlPullParserException, IOException {

        ArrayList<Route> routes = new ArrayList<>();

        //Obtener el PullParser y configurarlo
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(in, null);

        //Obtener el primer evento
        int event = parser.getEventType();

        //Ejecutarse continuamente mientras no se alcance el evento final
        while (event != XmlPullParser.END_DOCUMENT){
            if(event == XmlPullParser.START_TAG){
                String tagName = parser.getName();

                //Si se encuentra un Placemark
                if(tagName.equals("Placemark")){
                    Util.log("Se encontró un placemark");
                    Route route = new Route();

                    //Confeccionar la ruta
                    while (!(event == XmlPullParser.END_TAG && parser.getName().equals("Placemark"))){
                        if(event == XmlPullParser.START_TAG){
                            if(parser.getName().equals("name")){
                                route.name = parser.nextText();
                                Util.log(route.name);
                            } else if(parser.getName().equals("coordinates")){

                                //Obtener el texto de las coordenadas y separarlo en el ArrayList
                                ArrayList<LatLng> coords = new ArrayList<>();
                                String rawText = parser.nextText().replace("\n", "").replace("\t", "");
                                String[] separatedText = rawText.split(" ");
                                for (String current : separatedText) {
                                    double lat = Double.parseDouble(current.split(",")[1]);
                                    double lng = Double.parseDouble(current.split(",")[0]);
                                    coords.add(new LatLng(lat, lng));
                                }

                                //Ya que se separó, agregar las coordenadas a la ruta
                                route.coordinates = Arrays.copyOf(coords.toArray(), coords.size(), LatLng[].class);
                            }
                        }
                        event = parser.next();
                    }

                    //Añadir la ruta confeccionada al arraylist de rutas
                    routes.add(route);
                }
            }

            //Obtener el siguiente evento a procesar
            event = parser.next();
        }

        //Si se encontraron rutas, regresarlas
        if(routes.size() > 0){
            return Arrays.copyOf(routes.toArray(), routes.size(), Route[].class);
        } else {
            Log.e(KmlParser.class.getName(), "No se encontraron rutas en el InputStream");
            return new Route[0];
        }
    }

}
