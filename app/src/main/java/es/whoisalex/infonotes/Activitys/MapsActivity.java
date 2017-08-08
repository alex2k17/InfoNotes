package es.whoisalex.infonotes.Activitys;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.scalified.fab.ActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.whoisalex.infonotes.BD.DAO;
import es.whoisalex.infonotes.Conex.VolleySingleton;
import es.whoisalex.infonotes.POJOS.Notas;
import es.whoisalex.infonotes.R;
import es.whoisalex.infonotes.Utils.Constantes;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnLocationUpdatedListener {

    private GoogleMap mMap;
    private ActionButton button, addbuton;
    private ToggleButton tipo;
    private EditText nombre, descripcion;
    private Spinner categoria;
    private ProgressDialog progress;
    private Context mContext;

    private boolean updateMap = false;
    private int color;
    private Circle circle;
    private LatLng latLng;
    private SupportMapFragment mapFragment;
    List<Notas> lista;
    Marker poss;
    List<Marker> marcadoresOut;
    List<Marker> marcadoresIn;
    Location loc;

    private String provincia, nombreText, descripcionText, categoriaText;
    private DAO dao;
    private Handler handler;
    private LocationParams.Builder params;

    private static final String TAG = MapsActivity.class.getSimpleName();
    public static final int TASK_FINISH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mContext = this;
        color = Color.parseColor("#300084d3");
        button = (ActionButton) findViewById(R.id.GPSLOC);
        tipo = (ToggleButton) findViewById(R.id.toggleType);
        addbuton = (ActionButton) findViewById(R.id.addNote);
        SmartLocation.with(this).location().config(putConfig()).start(this);
        initGPS();
        requestPermission();
        addbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGPS();
            }
        });
        tipo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    color = Color.parseColor("#40DF7401");
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    updateMap = true;
                    refreshUpdate();
                } else {
                    color = Color.parseColor("#300084d3");
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    updateMap = true;
                    refreshUpdate();
                }
            }
        });
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
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (lista != null) {
            //printNotas(lista);
            drawMarks(lista);
            if (!updateMap) initGPS();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateMap = false;
        SmartLocation.with(this).location().config(putConfig()).stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmartLocation.with(this).location().config(putConfig()).start(this);
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
                loadData();
                Log.d("Permissions:", " Granted.");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    public void loadData() {
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

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TASK_FINISH:
                        refreshMap();
                        break;
                }
            }
        };
    }

    public void refreshUpdate() {
        drawCircle();
    }

    public void refreshMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
    }

    public void initGPS() {
        loc = SmartLocation.with(this).location().getLastLocation();
        //Log.e(TAG, loc.toString())
        if (circle != null) circle.remove();
        if (mMap != null) {
            drawPosition(loc);
            latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            circle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(250)
                    .strokeWidth(0f)
                    .strokeColor(Color.BLUE)
                    .fillColor(color));
            putNotesInCircle();
        }
    }

    public void drawPosition(Location location) {
        if (poss != null) poss.remove();
        LatLng currentPosition = new LatLng(location.getLatitude(),
                location.getLongitude());
        poss = mMap.addMarker(new MarkerOptions().position(currentPosition)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Posicion"));
    }

    public void putNotesInCircle() {
        float[] distance = new float[2];
        if (marcadoresIn != null || !marcadoresIn.isEmpty()) {
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
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            while (addresses.size() == 0) {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
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
        lista = dao.getNotasByProvincia(getProvincia());
        dao.close();
        Log.d(TAG, lista.toString());
    }

    public void printNotas(List<Notas> lista) {
        for (int i = 0; i < lista.size(); i++) {
            Log.d(TAG, lista.get(i).toString());
        }
    }

    public LocationParams putConfig() {
        params = new LocationParams.Builder();
        params.setDistance(0);
        params.setInterval(900);
        params.setAccuracy(LocationAccuracy.HIGH);
        return params.build();
    }

    public void drawMarks(List<Notas> lista) {
        marcadoresOut = new ArrayList<>();
        marcadoresIn = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            latLng = new LatLng(lista.get(i).getCoordY(), lista.get(i).getCoordX());
            if (lista.get(i).getCategoria().equalsIgnoreCase("Peligros") || lista.get(i).getCategoria().equalsIgnoreCase("Clima")) {
                marcadoresOut.add(mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(lista.get(i).getNombre())));
            } else {
                marcadoresIn.add(mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .visible(false)
                        .title(lista.get(i).getNombre())));
            }
        }
    }

    public void drawCircle() {
        if (circle != null) circle.remove();
        latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(250)
                .strokeWidth(0f)
                .strokeColor(Color.BLUE)
                .fillColor(color));
    }

    public void customDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_dialog, null, false);
        builder.setView(contentView);
        builder.setTitle("Añadir Nota");
        builder.setIcon(R.drawable.gps);
        builder.setPositiveButton("Aceptar", null);
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        categoria = (Spinner) contentView.findViewById(R.id.spinnerCategoria);
        nombre = (EditText) contentView.findViewById(R.id.nombre);
        descripcion = (EditText) contentView.findViewById(R.id.descripcion);
        loadSpinner(categoria);
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        categoriaText = categoria.getSelectedItem().toString();
                        nombreText = nombre.getText().toString();
                        descripcionText = descripcion.getText().toString();
                        if (comprobarTexts(nombreText, descripcionText, categoriaText)) {
                            Log.d(TAG, "Comprobacion apta.");
                            dialog.dismiss();
                            processData(nombreText, descripcionText, categoriaText);
                        } else {
                            Log.d(TAG, "Comprobacion no apta.");
                            Toast.makeText(getApplicationContext(), "Comprobacion no apta.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alert.show();
        centerButtonDialog(alert);
    }

    public void loadSpinner(Spinner categoria) {
        final String[] datos = new String[]{"Selecciona la categoria", "Comida", "Clima", "Peligros", "Random", "Test"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, datos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adaptador);
    }

    public void centerButtonDialog(AlertDialog alert) {
        Button positiveButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    @Override
    public void onLocationUpdated(Location location) {
        loc = location;
        //Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "OnLocationUpdated: " + loc.toString());
        if (circle != null) circle.remove();
        if (mMap != null) {
            drawPosition(loc);
            drawCircle();
            putNotesInCircle();
        }
    }

    public boolean comprobarTexts(String nombretxt, String descripciontxt, String categoriatext) {
        if (!nombretxt.equalsIgnoreCase("") &&
                !nombretxt.equalsIgnoreCase(" ") &&
                !descripciontxt.equalsIgnoreCase(" ") &&
                !descripciontxt.equalsIgnoreCase("") &&
                !categoriatext.equalsIgnoreCase("Selecciona la categoria"))
            return true;
        else
            return false;
    }

    public void processData(final String nombretxt, final String descripciontxt, final String categoriatxt) {

        progress = ProgressDialog.show(this, "Cargando",
                "Espere porfavor...", true);

        new Thread() {
            @Override
            public void run() {
                try {
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    Notas nota = new Notas();
                    nota.setNombre(nombretxt);
                    nota.setDescripcion(descripciontxt);
                    nota.setCategoria(categoriatxt);
                    nota.setLocalidad(getProvincia());
                    nota.setDate(timeStamp);
                    nota.setCoordY(loc.getLatitude());
                    nota.setCoordX(loc.getLongitude());
                    dao = new DAO(mContext).open();
                    long id = dao.createNota(nota);
                    dao.close();
                    if (id >= 0) {
                        nota.setId(id);
                    }
                    lista.add(nota);
                    uploadNota(nota);
                    handler.sendEmptyMessage(TASK_FINISH);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TASK_FINISH:
                        progress.dismiss();
                        refreshMap();
                        break;
                }
            }
        };
    }

    public void uploadNota(Notas nota) {
        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        String coordY = String.valueOf(nota.getCoordY());
        String coordX = String.valueOf(nota.getCoordX());
        map.put("nombre", nota.getNombre());
        map.put("localidad", nota.getLocalidad());
        map.put("categoria", nota.getCategoria());
        map.put("descripcion", nota.getDescripcion());
        map.put("coordY", coordY);
        map.put("coordX", coordX);

        JSONObject jobject = new JSONObject(map);

        // Depurando objeto Json...
        Log.d(TAG, jobject.toString());

        VolleySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.ADD_NOTA,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuestaAdd(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    private void procesarRespuestaAdd(JSONObject response) {
        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    Log.d(TAG, "Añadido con exito al servidor.");
                    break;
                case "2":
                    Log.d(TAG, "ERROR; "+mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
