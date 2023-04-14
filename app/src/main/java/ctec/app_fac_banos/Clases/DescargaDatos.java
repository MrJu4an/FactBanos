package ctec.app_fac_banos.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.FacBanosActivity;
import ctec.app_fac_banos.MainActivity;

public class DescargaDatos extends AppCompatActivity {

    SQLiteDatabase db;
    ArrayList<Ruta>rutas;
    ArrayList<Servicio>servicios;
    ArrayList<DocGenerales>docGenerales;
    ArrayList<TipoDevolucion>tipoDev;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje = new Mensaje();
    private  RequestQueue requestQueue;
    private Context context;

    public DescargaDatos(Context context) {
        this.context = context;
        sweetAlertDialog = new SweetAlertDialog(context);
        //mensaje = new Mensaje();
        db = Global.conn.getWritableDatabase();
    }

    public void  DescargaResolucion(final String Caja)  {


        try {
            requestQueue = Volley.newRequestQueue(context);

            String URL = Global.g_DirecApi + "/api/FactBanos/PostResolucionPereira";

            sweetAlertDialog.dismiss();
            sweetAlertDialog = mensaje.progreso(context,"DESCARGANDO 1 de 3 (DATOS RESOLUCION)");
            sweetAlertDialog.show();


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("caja",Caja);
            jsonBody.put("item","BAÑOS");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        sweetAlertDialog.dismiss();
                        Resolucion resolucion = new Gson().fromJson(response, Resolucion.class);

                        Global.g_Resolucion=resolucion;
                        if (Global.g_Resolucion.getFRNUMINI() !=0) {
                            Global.g_NumFacIni = Global.g_Resolucion.getFRNUMFAC();
                            DescargaDescripCaja(Caja);
                        }
                        else{
                            mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                                    "No se descargo la resolucion \n Contacte al Administrador!!!");
                        }


                    } catch (Exception e) {
                        sweetAlertDialog.dismiss();
                        e.printStackTrace();
                        mensaje.MensajeAdvertencia(context, "3.Advertencia" , e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;

                    Log.e("VOLLEY", error.toString());
                    Log.e("VOLLEy", error.getStackTrace().toString());
                    sweetAlertDialog.dismiss();
                    if (networkResponse != null && networkResponse.statusCode == 400) {
                        error.printStackTrace();
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(context, "7.Advertencia", error.toString());
                        //startCounter = false;
                        //counter.onFinish();
                        return;
                    }
                    String msj = error.getMessage();
                    try {
                        mensaje.MensajeAdvertencia(context, "3.ADVERTENCIA", error.toString());

                    } catch (Exception e) {
                        mensaje.MensajeAdvertencia(context, "3.ADVERTENCIA"  , e.getMessage());
                    }
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody()  {
                    try {
                        return requestBody.getBytes();
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + Global.g_Token);
                    return headers;
                }

               // @Override
                //protected Response<String> parseNetworkResponse(NetworkResponse response) {
                 //   String parsed = null;
                  //  try {

                  //      parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                  //  } catch (UnsupportedEncodingException e) {
                  //      sweetAlertDialog.dismiss();
                  //      mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                  //              e.getMessage());
                  //  }

                  //  return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
               // }
            };
            stringRequest.setRetryPolicy(
                    new DefaultRetryPolicy(4000,
                            1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            );
            //Volley.newRequestQueue(context).add(stringRequest);
            //requestQueue.add(stringRequest);
            //RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                    e.getMessage());
            e.printStackTrace();
        }

    }

    public void  DescargaDescripCaja(final String Caja)  {


        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = Global.g_DirecApi + "/api/FactBanos/PostDescargaPereira";

            sweetAlertDialog.dismiss();
            sweetAlertDialog = mensaje.progreso(context,"DESCARGANDO 2 de 3 (DATOS CAJA)");
            sweetAlertDialog.show();


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("caja",Caja);
            jsonBody.put("item","DescripCajaBanos");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        sweetAlertDialog.dismiss();
                        Gedetsuptip detalle[] = new Gson().fromJson(response, Gedetsuptip[].class);

                        writePreference("NomUbica",detalle[0].getDescripcion());
                        Global.g_NomUbica = detalle[0].getDescripcion();
                        DescargaParametro("NIT");
                        DescargaParametro("NOMEMPRESA");
                        DescargaConcepBanos(Caja);


                    } catch (Exception e) {
                        sweetAlertDialog.dismiss();
                        e.printStackTrace();
                        mensaje.MensajeAdvertencia(context, "3.Advertencia" , e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    sweetAlertDialog.dismiss();
                    try {
                        mensaje.MensajeAdvertencia(context, "3.ADVERTENCIA", error.toString());

                    } catch (Exception e) {
                        mensaje.MensajeAdvertencia(context, "3.ADVERTENCIA"  , e.getMessage());
                    }
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody()  {
                    try {
                        return requestBody.getBytes();
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + Global.g_Token);
                    return headers;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String parsed = null;
                    try {

                        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                                e.getMessage());
                    }

                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                    e.getMessage());
            e.printStackTrace();
        }

    }

    public void  DescargaParametro(final String item)  {


        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = Global.g_DirecApi + "/api/FactBanos/PostParametro";

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("item",item);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {

                        String detalle = new Gson().fromJson(response, String.class);
                        writePreference(item,detalle);
                        if (item.equals("NIT"))
                            Global.g_Nit = detalle;
                        else if (item.equals("NOMEMPRESA"))
                            Global.g_NomEmp = detalle;

                    } catch (Exception e) {
                        sweetAlertDialog.dismiss();
                        e.printStackTrace();
                        mensaje.MensajeAdvertencia(context, "3.Advertencia" , e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    sweetAlertDialog.dismiss();
                    try {
                        mensaje.MensajeAdvertencia(context, "3.ADVERTENCIA", error.toString());

                    } catch (Exception e) {
                        mensaje.MensajeAdvertencia(context, "3.ADVERTENCIA"  , e.getMessage());
                    }
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody()  {
                    try {
                        return requestBody.getBytes();
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + Global.g_Token);
                    return headers;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String parsed = null;
                    try {

                        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                                e.getMessage());
                    }

                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                    e.getMessage());
            e.printStackTrace();
        }

    }

    public void  DescargaConcepBanos(final String Caja)  {


        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = Global.g_DirecApi + "/api/FactBanos/PostDescargaPereira";

            sweetAlertDialog.dismiss();
            sweetAlertDialog = mensaje.progreso(context,"DESCARGANDO 3 de 3 (DATOS CONCEPTOS)");
            sweetAlertDialog.show();


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("caja",Caja);
            jsonBody.put("item","DescripConcepBanos");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        sweetAlertDialog.dismiss();
                        Global.detalles = new Gson().fromJson(response, Gedetsuptip[].class);

                        /*writePreference("NomConcep",detalle.getCodDetalle());
                        Global.g_NomConcepto = detalle.getCodDetalle();
                        writePreference("ValConcep",detalle.getDescripcion());
                        Global.g_ValorConcep = Double.parseDouble(detalle.getDescripcion());
                        writePreference("ValorIva",detalle.getValor());
                        Global.g_ValorIva = Double.parseDouble(detalle.getValor());*/
                        cargarDatos();

                    } catch (Exception e) {
                        sweetAlertDialog.dismiss();
                        e.printStackTrace();
                        mensaje.MensajeAdvertencia(context, "3.Advertencia" , e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    sweetAlertDialog.dismiss();
                    try {
                        mensaje.MensajeAdvertencia(context, "3.ADVERTENCIA", error.toString());

                    } catch (Exception e) {
                        mensaje.MensajeAdvertencia(context, "3.ADVERTENCIA"  , e.getMessage());
                    }
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody()  {
                    try {
                        return requestBody.getBytes();
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + Global.g_Token);
                    return headers;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String parsed = null;
                    try {

                        parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                                e.getMessage());
                    }

                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(context, "3.Advertencia" ,
                    e.getMessage());
            e.printStackTrace();
        }

    }

    public void cargarDatos(){

        try {

            int bandescarga = 1;

            if ( Global.g_Nit.equals("") || Global.g_Nit.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.g_NomEmp.equals("") || Global.g_NomEmp.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.g_Resolucion.equals("") || Global.g_Resolucion.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.g_NomUbica.equals("") || Global.g_NomUbica.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.detalles == null) {
                bandescarga = 0;
            }

            /*if ( Global.g_ValorConcep.equals("") || Global.g_ValorConcep.toString().equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.g_ValorIva.equals("") || Global.g_ValorIva.toString().equals("-1")) {
                bandescarga = 0;
            }

           if ( Global.g_NomConcepto.equals("") || Global.g_NomConcepto.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.g_NumFacIni.equals("") || Global.g_NumFacIni.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.g_NumFacFin.equals("") || Global.g_NumFacFin.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.g_NumFacAct.equals("") || Global.g_NumFacAct.equals("-1")) {
                bandescarga = 0;
            }*/


            if ( bandescarga == 0 ) {
                mensaje.MensajeAdvertencia(context,"Advertencia","No se completo correctamente la descargar, Por Favor Verifique!!!");
            }

            mensaje.MensajeExitoso(context, "Exitoso" , "Descarga realizada correctamente");

        }
        catch(Exception ex){
            mensaje.MensajeAdvertencia(context,"Advertencia",ex.getMessage());
        }
    }

    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = getSharedPreferences("FacBan", context.MODE_PRIVATE);

            valor = prefs.getString(key, "-1");
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(this.context,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }

    public boolean writePreference(String key, String value){
        try {
            SharedPreferences prefs = context.getSharedPreferences("FacBan",  context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value.toString());
            editor.commit();
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(context,"Advertencia",ex.getMessage() );
            return false;
        }
        return true;
    }

    
}
