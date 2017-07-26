package es.whoisalex.infonotes.Activitys;

import android.Manifest;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.whoisalex.infonotes.Interfaces.updateGPS;
import es.whoisalex.infonotes.R;
import es.whoisalex.infonotes.Utils.GPS;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, updateGPS {

    private GoogleMap mMap;
    private GPS gps;

    private String provincia;

    private Circle circle;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        requestPermission();
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }catch (SecurityException e){
            e.printStackTrace();
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        initGPS();
        // Add a marker in Sydney and move the camera
        //mMap.addMarker(new MarkerOptions().position(algeciras).title("Marquita"));
    }

    @Override
    public void onBackPressed() {
    }

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
                try {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    initGPS();
                }catch (SecurityException e){
                    e.printStackTrace();
                }
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
    }


    @Override
    public void update() {
        if (circle!=null)circle.remove();
        latLng = new LatLng(gps.getLatitude(), gps.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(250)
                .strokeWidth(0f)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#300084d3")));
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
                provincia = addresses.get(0).getSubAdminArea();
            }
        }
        return provincia;
    }
}
