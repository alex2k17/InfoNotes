
package es.whoisalex.infonotes.ScreenSplash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import es.whoisalex.infonotes.Activitys.MapsActivity;
import es.whoisalex.infonotes.BD.DAO;
import es.whoisalex.infonotes.Conex.VolleySingleton;
import es.whoisalex.infonotes.Interfaces.updateGPS;
import es.whoisalex.infonotes.POJOS.Notas;
import es.whoisalex.infonotes.R;
import es.whoisalex.infonotes.Utils.Constantes;
import es.whoisalex.infonotes.Utils.GPS;

/**
 * Created by Alex on 20/07/2017.
 */

public class ScreenSplash extends Activity implements updateGPS {

    private static final long TIME = 1700;

    private static final String TAG = MapsActivity.class.getSimpleName();

    Notas[] notas;
    String provincia;
    private GPS gps;
    private Gson gson;
    private DAO dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_screensplash);
        gson = new Gson();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermission();
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
                initGPS();
                Log.d("Permissions:", " Granted.");
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    @Override
    public void onBackPressed() {}

    public void initGPS(){
        gps = new GPS(this);
        gps.getLocation();
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }
        gps.delegate = this;
        gps.onLocationChanged(gps.getLocation());
        cargarNotas();
    }

    public String getProvincia() {
        provincia = null;
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
            while (addresses.size() == 0) {
                addresses = gcd.getFromLocation(gps.getLatitude(), gps.getLongitude(), 1);
                Log.e(TAG, "Stuck");
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

    public void cargarNotas(){
        String newURL = Constantes.GET_NOTAS_BY_LOCALIDAD + "?localidad="+getProvincia();

        VolleySingleton.getInstance(this).addToRequestQueue(new JsonObjectRequest(
                Request.Method.GET,
                newURL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuesta(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage());
                    }
                }
        ));
    }

    public void procesarRespuesta(JSONObject response){
        try {
            String estado = response.getString("estado");

            switch (estado){
                case "1":
                    JSONArray mensaje = response.getJSONArray("notas");
                    notas = gson.fromJson(mensaje.toString(), Notas[].class);
                    insertDataBd(notas);
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Toast.makeText(
                            this,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertDataBd(Notas[] notas) throws SQLException{
        long id;
        dao = new DAO(this).open();
        dao.dropBD();
        for (int i=0;i<notas.length;i++){
            id =dao.createNota(notas[i]);
            if (id >= 0) {
                notas[i].setId(id);
            }
        }
        dao.close();
        changeActivity();
    }

    public void changeActivity(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, TIME);
    }

    @Override
    public void update() {
    }
}