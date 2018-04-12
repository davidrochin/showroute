package xyz.showroute.showroute;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import jdrc.util.*;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MapView mapView;
    GoogleMap gMap;
    Route[] routes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Obtener las rutas del archivo KML
        try {
            routes = KmlParser.getRoutes(this.getAssets().open("bus_los_mochis.kml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Configurar el mapa
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //Poner un marcador en Los Mochis e ir a el
                LatLng lm = new LatLng(25.799648, -108.974224);
                googleMap.addMarker(new MarkerOptions().position(lm).title("Marcador de prueba"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(lm));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lm, 12));

                //int routeColor = getApplicationContext().getResources().getColor(R.color.colorActiveRoute);

                //Dibujar las rutas obtenidas del KML
                for (Route route : routes) {
                    //int routeColor = ColorUtil.randomColor();
                    route.drawOnMap(googleMap);
                }

                //Guardar el mapa para uso posterior
                gMap = googleMap;
            }
        });

        //Buscar el spinner y popularlo con las rutas
        Spinner routesSpinner = findViewById(R.id.spinner_routes);
        routesSpinner.setAdapter(new RoutesAdapter(this, routes));

        //Establecer que pasa al hacer clic en un elemento del spinner
        routesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Route route = (Route) adapterView.getItemAtPosition(i);

                //Limpiar las otras rutas y dibujar la seleccionada
                gMap.clear();
                route.drawOnMap(gMap);

                //Encajar la camara en la ruta
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(route.getBounds(), 20);
                gMap.animateCamera(cu);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ArrayList<Route> routes = ((RoutesAdapter)adapterView.getAdapter()).routes;
            }
        });

        //Establecer que pasa al hacer clic en el botón para mostrar todas las rutas
        ((Button)findViewById(R.id.button_all_routes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Limpiar las rutas existentes
                gMap.clear();

                //Dibujar todas las rutas
                for (Route route : routes) {
                    //int routeColor = ColorUtil.randomColor();
                    route.drawOnMap(gMap);
                }
            }
        });
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
}
