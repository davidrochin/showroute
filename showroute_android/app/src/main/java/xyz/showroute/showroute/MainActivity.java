package xyz.showroute.showroute;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MapView mapView;
    GoogleMap gMap;
    Route[] routes;
    FusedLocationProviderClient locationClient;

    View routesLayoutGroup;
    View directionsLayoutGroup;

    Spinner routesSpinner;

    //Direcciones para restringir el mapa
    LatLngBounds mapBounds;
    LatLng mapNorthEast = new LatLng(25.843149, -108.9374687);
    LatLng mapSouthWest = new LatLng(25.7352309, -109.0893818);

    //Direcciones elegidas por el usuario
    LatLng fromLocation;
    LatLng toLocation;

    //Puntos de prueba
    LatLng rochinLocation = new LatLng(25.7952263, -108.9976896);
    LatLng itlmLocation = new LatLng(25.7986727, -108.9747927);
    LatLng ferLocation = new LatLng(25.8060528, -109.003748);

    //Valores de padding
    int routesPadding = 128;
    int directionsPadding = 265;

    //Anuncios
    AdView adBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Inicializar los Ads de Google
        MobileAds.initialize(this, "ca-app-pub-8880885435323383~8876300246");
        adBottom = findViewById(R.id.ad_bottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        adBottom.loadAd(adRequest);

        //Buscar views que se quieren referenciar
        routesSpinner = findViewById(R.id.spinner_routes);

        //Obtener las rutas del archivo KML
        try {
            routes = KmlParser.getRoutes(this.getAssets().open("bus_los_mochis.kml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Calcular el limite del mapa
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        latLngBuilder.include(mapNorthEast).include(mapSouthWest);
        mapBounds = latLngBuilder.build();

        //Revisar si se tiene permisos para la ubicacion y si no hay, pedirlo
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        //Configurar el mapa
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                //Activar la opción que muestra la ubicación del usuario (si hay permisos para hacerlo)
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                try{
                    if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                        googleMap.setMyLocationEnabled(true);
                    }
                } catch (SecurityException e){
                    e.printStackTrace();
                }

                //Ponerle padding para evitar que se tapen los botones del mapa por otras views
                googleMap.setPadding(0, routesPadding, 0, 0);

                //Limitar el mapa y hacer que no se pueda rotar
                googleMap.setLatLngBoundsForCameraTarget(mapBounds);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);

                //Guardar el mapa para uso posterior
                gMap = googleMap;

            }
        });

        //Popular Spinner con las rutas
        routesSpinner.setAdapter(new RoutesAdapter(this, routes));

        //Establecer que pasa al hacer clic en un elemento del spinner
        routesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Route route = (Route) adapterView.getItemAtPosition(i);

                //Limpiar las otras rutas y dibujar la seleccionada
                if(gMap != null){
                    gMap.clear();
                    route.drawOnMap(gMap);

                    //Encajar la camara en la ruta
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(route.getBounds(), 20);
                    gMap.animateCamera(cu);

                    //Mostrar el tiempo estimado de pasada
                    if(route.estimatedArrivalTime != -1){
                        ((TextView)findViewById(R.id.text_estimated_time)).setText("Tiempo estimado de pasada: " + route.estimatedArrivalTime + " minutos");
                    } else {
                        ((TextView)findViewById(R.id.text_estimated_time)).setText("Tiempo estimado de pasada desconocido");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ArrayList<Route> routes = ((RoutesAdapter)adapterView.getAdapter()).routes;
            }
        });

        //Establecer que pasa al usar las pestañas inferiores
        routesLayoutGroup = findViewById(R.id.layout_group_routes);
        directionsLayoutGroup = findViewById(R.id.layout_group_directions);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout_sections);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //toast.makeText(MainActivity.this, "" + tab.getPosition(), toast.LENGTH_SHORT).show();
                switch (tab.getPosition()){
                    //Rutas
                    case 0:
                        gMap.clear();
                        ((Route)routesSpinner.getSelectedItem()).drawOnMap(gMap);
                        routesLayoutGroup.setVisibility(View.VISIBLE);
                        directionsLayoutGroup.setVisibility(View.GONE);

                        //Calcular el padding y aplicarlo
                        routesLayoutGroup.post(new Runnable() {
                            @Override
                            public void run() {
                                routesPadding = routesLayoutGroup.getHeight() - getPixelsFromDPs(MainActivity.this, 10);
                                gMap.setPadding(0, routesPadding, 0, 0);
                                //Util.toast(MainActivity.this, routesPadding + "");
                            }
                        });

                        break;
                    //Como llegar
                    case 1:
                        gMap.clear();
                        routesLayoutGroup.setVisibility(View.GONE);
                        directionsLayoutGroup.setVisibility(View.VISIBLE);

                        //Calcular el padding y aplicarlo
                        directionsLayoutGroup.post(new Runnable() {
                            @Override
                            public void run() {
                                directionsPadding = directionsLayoutGroup.getHeight() - getPixelsFromDPs(MainActivity.this, 10);
                                gMap.setPadding(0, directionsPadding, 0, 0);
                                //Util.toast(MainActivity.this, directionsPadding + "");
                            }
                        });

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        routesLayoutGroup.setVisibility(View.VISIBLE);
        directionsLayoutGroup.setVisibility(View.GONE);

        //Calcular el padding de la seccion default y aplicarlo
        routesLayoutGroup.post(new Runnable() {
            @Override
            public void run() {
                routesPadding = routesLayoutGroup.getHeight() - getPixelsFromDPs(MainActivity.this, 10);
                gMap.setPadding(0, routesPadding, 0, 0);
            }
        });

        //Establecer el comportamiento de la seccion de direcciones
        Button selectDestinationButton = (Button)findViewById(R.id.button_select_destination);
        selectDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(MainActivity.this), DESTINATION_PICKER_REQUEST);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Button selectOriginButton = (Button)findViewById(R.id.button_select_origin);
        selectOriginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(MainActivity.this), ORIGIN_PICKER_REQUEST);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //Pruebas
        //Util.toast(this, "" + routes[0].getDistanceFrom(new LatLng(25.7952263, -108.9976896)));
        //Util.toast(this, "" + routes[1].getDistanceFrom(new LatLng(25.7952263, -108.9976896)));
        //Util.toast(this, "Ruta mas cercana: " + Route.getNearestToPoint(routes, itlmLocation).name);
        //Util.toast(this, "Mejor ruta: " + Route.calculateBestRoute(routes, rochinLocation, ferLocation));

    }

    final int ORIGIN_PICKER_REQUEST = 2;
    final int DESTINATION_PICKER_REQUEST = 1;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case ORIGIN_PICKER_REQUEST:
            case DESTINATION_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    Place place = PlacePicker.getPlace(this, data);
                    Util.log(place.getAddress().toString() + "");
                    if(requestCode == ORIGIN_PICKER_REQUEST){
                        fromLocation = place.getLatLng();
                    } else {
                        toLocation = place.getLatLng();
                    }

                    //Si ya se tienen todos los lugares, calcular y seleccionar la mejor ruta
                    if(fromLocation != null && toLocation != null){
                        Route bestRoute = Route.calculateBestRoute(routes, fromLocation, toLocation);
                        gMap.clear();
                        bestRoute.drawOnMap(gMap);

                        //Encajar la camara en la ruta
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bestRoute.getBounds(), 20);
                        gMap.animateCamera(cu);

                        //Seleccionarla en el spinner de la otra sección
                        List<Route> routeList = ((RoutesAdapter)routesSpinner.getAdapter()).routes;
                        for(int i = 0; i < routeList.size(); i++){
                            if(routeList.get(i) == bestRoute){
                                routesSpinner.setSelection(i);
                            }
                        }

                    } else {
                        Toast.makeText(this, "Falta especificar de donde a donde.", Toast.LENGTH_LONG);
                    }

                }
                break;
            default:
                break;
        }
    }

    SupportMapFragment mapFragment;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.toast(this, "Por favor reinicie la aplicación para que aparezca tu ubicación en el mapa");
                    if(gMap != null){
                        try{
                            gMap.setMyLocationEnabled(true);
                        } catch (SecurityException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    Util.toast(this, "Has denegado los permisos de ubicación");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static int getPixelsFromDPs(Activity activity, int dps){
        Resources r = activity.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }

}
