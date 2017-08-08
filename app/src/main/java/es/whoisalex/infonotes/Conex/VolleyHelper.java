package es.whoisalex.infonotes.Conex;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.whoisalex.infonotes.Activitys.MapsActivity;
import es.whoisalex.infonotes.POJOS.Notas;

/**
 * Created by Alex on 08/08/2017.
 */

public class VolleyHelper {

    private Notas[] notas;
    private Context mContext;
    private static final String TAG = MapsActivity.class.getSimpleName();

    public VolleyHelper(Context context){
        this.mContext=context;
    }

    public void sendData(String url, JSONObject objecto){
        VolleySingleton.getInstance(mContext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        objecto,
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

    public Notas[] getDataArray(String url){
        VolleySingleton.getInstance(mContext).addToRequestQueue(new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuestaArray(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Volley: " + error.getMessage());
                    }
                }
        ));
        return notas;
    }

    public void procesarRespuestaArray(JSONObject response){
        try {
            Gson gson = new Gson();
            String estado = response.getString("estado");

            switch (estado){
                case "1":
                    JSONArray mensaje = response.getJSONArray("notas");
                    notas = gson.fromJson(mensaje.toString(), Notas[].class);
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Log.d(TAG, "ERROR; "+mensaje2);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
