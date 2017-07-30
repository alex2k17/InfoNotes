package es.whoisalex.infonotes.Activitys;

import android.Manifest;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.whoisalex.infonotes.BD.DAO;
import es.whoisalex.infonotes.Interfaces.updateGPS;
import es.whoisalex.infonotes.POJOS.Notas;
import es.whoisalex.infonotes.R;
import es.whoisalex.infonotes.Utils.GPS;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, updateGPS {

    private GoogleMap mMap;
    private GPS gps;

    private Circle circle;
    private LatLng latLng;
    private SupportMapFragment mapFragment;
    List<Notas> lista;
    List<Marker> marcadoresOut;
    List<Marker> marcadoresIn;

    private String provincia;
    private DAO dao;
    private Handler handler;

    private static final String TAG = MapsActivity.class.getSimpleName();
    public static final int TASK_FINISH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        requestPermission();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }catch (SecurityException e){
            e.printStackTrace();
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (lista!=null){
            printNotas(lista);
            drawMarks(lista);
            initPos();
        }
    }

    @Override
    public void onBackPressed() {}

    public void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                List<String> grantedPermissions = new ArrayList<String>();
                for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
                    if (!grantedPermissions.contains(response.getPermissionName())) {
                        grantedPermissions.add(response.getPermissionName());
                    }
                }
                initGPS();
                Log.d("Permissions:", " Granted.");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    public void initGPS(){
        gps = new GPS(this);
        gps.getLocation();
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }
        gps.delegate = this;
        gps.onLocationChanged(gps.getLocation());

        new Thread() {
            @Override
            public void run() {
                try {
                    cargarNotas();
                    handler.sendEmptyMessage(TASK_FINISH);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what) {
                    case TASK_FINISH:
                        refreshMap();
                        break;
                }
            }
        };
    }

    public void refreshMap(){mapFragment.getMapAsync(this);}

    @Override
    public void update() {
        if (circle!=null)circle.remove();
        if (mMap!=null)initPos();
    }

    public void initPos(){
        latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(250)
                .strokeWidth(0f)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#300084d3")));
        putNotesInCircle();
    }

    public void putNotesInCircle(){
        float[] distance = new float[2];
        if (marcadoresIn!=null || !marcadoresIn.isEmpty()) {
            for (int i = 0; i < marcadoresIn.size(); i++) {
                Location.distanceBetween(marcadoresIn.get(i).getPosition().latitude, marcadoresIn.get(i).getPosition().longitude,
                        circle.getCenter().latitude, circle.getCenter().longitude, distance);
                if (distance[0] > circle.getRadius()) {
                    marcadoresIn.get(i).setVisible(false);
                } else {
                    marcadoresIn.get(i).setVisible(true);
                }
            }
        }
    }

    //Metodo para obtener la provincia en la que se encuentra el usuario
    public String getProvincia() {
        provincia = null;
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
                addresses = gcd.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
            while (addresses.size() == 0) {
                addresses = gcd.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            if (addresses.size() > 0) {
                provincia = addresses.get(0).getLocality();
            }
        }
        return provincia;
    }

    public void cargarNotas() throws SQLException {
        dao = new DAO(this).open();
        lista=dao.getNotasByProvincia(getProvincia());
        dao.close();
        Log.e(TAG, lista.toString());
    }

    public void printNotas(List<Notas> lista){
        for (int i=0;i<lista.size();i++){
            Log.d(TAG, lista.get(i).toString());
        }
    }

    public void drawMarks(List<Notas> lista){
        marcadoresOut = new ArrayList<>();
        marcadoresIn = new ArrayList<>();
        for(int i=0;i<lista.size();i++){
            latLng = new LatLng(lista.get(i).getCoordY(), lista.get(i).getCoordX());
            if (lista.get(i).getCategoria().equalsIgnoreCase("Peligros") || lista.get(i).getCategoria().equalsIgnoreCase("Clima")) {
                marcadoresOut.add(mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(lista.get(i).getNombre())));
            }else{
                marcadoresIn.add(mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .visible(false)
                        .title(lista.get(i).getNombre())));
            }
        }
    }
}
