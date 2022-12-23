package ctec.app_fac_banos;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyLog;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.ConexionSQliteHelper;
import ctec.app_fac_banos.Clases.Constantes;
import ctec.app_fac_banos.Clases.DescargaDatos;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;
import ctec.app_fac_banos.Clases.Permisos;
import ctec.app_fac_banos.Clases.RespLogin;
import ctec.app_fac_banos.Clases.Update;
import ctec.app_fac_banos.Clases.UpdateApp;
import ctec.app_fac_banos.Clases.Usuario;
import ctec.app_fac_banos.Clases.Utilities;

public class MainActivity extends AppCompatActivity {


    Button btnAcceso;
    TextView txtVFecha,txtVAgencia,txtVVersion;
    EditText edTUsuario,edTPassWord;
    Mensaje mensaje;
    SweetAlertDialog sweetAlertDialog;
    SimpleDateFormat dateFormat;
    Date date,fecIniD,fecFinD;
    String IP;
    UpdateApp updateApp;
    Integer banOpe=0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        PackageInfo info= new PackageInfo();

        Global.conn= new ConexionSQliteHelper(this,"db_Coonorte",null,4);
        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        updateApp = new UpdateApp(this,this);

        btnAcceso = findViewById(R.id.btnAcceso);
        edTUsuario =  findViewById(R.id.edTUsuario);
        edTPassWord =  findViewById(R.id.edTPassWord);
        txtVFecha =  findViewById(R.id.txtVFecha);
        txtVAgencia =  findViewById(R.id.txtVlbluser);
        txtVVersion =  findViewById(R.id.txtVVersion);

        try {
            info = getPackageManager( ).getPackageInfo( getPackageName( ), 0 );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        txtVVersion.setText("V."+info.versionName);


        Global.g_Serial = Build.SERIAL;

        Integer  VersionServer = 0;
        try{
            VersionServer =  updateApp.obtenerVersionAppServidor();
        }catch (Exception e){
            e.printStackTrace();
        }

        Integer VersionActual = updateApp.obtenerVersionAppActual();

/*
        if (Global.g_DirecApi.equals("-1")) {
            mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia", "Debe configurar primero la movil!!!");
            return;
        }
*/
        cargarDatos();
        /*
            if (VersionServer > VersionActual) {
                Update();
            }else{
                cargarDatos();
            }
        */


        btnAcceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean conectado = Utilities.isOnline(MainActivity.this);
                if (edTUsuario.length() == 0){
                    mensaje.MensajeAdvertencia(MainActivity.this,"Acceso Sistema","Debe Ingresar Usuario" +
                            " para Ingresar al Sistema");
                    return;
                }

                if (edTPassWord.length()==0){
                    mensaje.MensajeAdvertencia(MainActivity.this,"Acceso Sistema","Debe Ingresar  Contraseña " +
                            " para Ingresar al Sistema");
                    return;
                }

                if (conectado) {
                    banOpe = 1;
                    Global.g_Usuario = edTUsuario.getText().toString();
                    Global.g_PassWord = edTPassWord.getText().toString();
                    if (cargarDatos())
                        iniciarSesion();
                }
                else{
                    mensaje.MensajeAdvertencia(MainActivity.this, "Alerta", "Movil sin conexion");
                    return;
                }
            }
        });
    }

    //Metodo para mostrar el menu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    //metodo para asignar las fuciones correspondientes al menu
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.accesoConf) {
            if (edTPassWord.length()==0){
                mensaje.MensajeAdvertencia(MainActivity.this,"Acceso Configuracion","Debe Ingresar Usuario y Contraseña " +
                        " para Ingresar a la Configuracion");
            }
            else {
                if(edTPassWord.getText().toString().equals("0423")){
                    startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                    edTUsuario.setText("");
                    edTPassWord.setText("");
                }
                else {
                    mensaje.MensajeAdvertencia(MainActivity.this, "Acceso Configuracion", "Usuario y Contraseña Invalidos");
                }
            }
        }

        if (id == R.id.descargaDatos) {
            DescargaDatos descargaDatos = new DescargaDatos(MainActivity.this);
            descargaDatos.DescargaResolucion(Global.g_Caja);
        }

        if (id == R.id.actulizarApk) {
            try {
                /*Update();*/
                        /*
                        Update update;
                        update = new Update( MainActivity.this );
                        update.actualizarAplicacion();*/

            } catch (Exception e) {
                e.printStackTrace();
                mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia", e.getMessage());
            }
            /*
            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(MainActivity.this, "Advertencia",
                    "Desea Actualizar el Software?");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();

                    try {
                        Update();

                        Update update;
                        update = new Update( MainActivity.this );
                        update.actualizarAplicacion();

                    } catch (Exception e) {
                        e.printStackTrace();
                        mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia", e.getMessage());
                    }

                }
            });
            sweetAlertDialog.show();

             */
        }

        return super.onOptionsItemSelected(item);

    }


    protected void onResume(){
        super.onResume();
        //cargarDatos();
    }

    public  Boolean cargarDatos(){

        try {

            Global.g_DirecApi = readPreference("DirecApi");
            if (Global.g_DirecApi.equals("") || Global.g_DirecApi.equals("-1")) {
                mensaje.MensajeAdvertencia(MainActivity.this,"Advertencia","Dato DirecApi vacio ");
                return false;
            }

            Global.g_Caja = readPreference("Caja");
            if (Global.g_Caja.equals("") || Global.g_Caja.equals("-1")) {
                mensaje.MensajeAdvertencia(MainActivity.this,"Advertencia","Dato Caja vacio ");
                return false;
            }

            int bandescarga = 1;

            Global.g_Nit = readPreference("NIT");
            if ( Global.g_Nit.equals("") || Global.g_Nit.equals("-1")) {
                bandescarga = 0;
            }

            Global.g_NomEmp = readPreference("NOMEMPRESA");
            if ( Global.g_NomEmp.equals("") || Global.g_NomEmp.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.g_Resolucion == null ) {
                bandescarga = 0;
            }

            Global.g_NomUbica = readPreference("NomUbica");
            if ( Global.g_NomUbica.equals("") || Global.g_NomUbica.equals("-1")) {
                bandescarga = 0;
            }

            if ( Global.detalles == null) {
                bandescarga = 0;
            }

            Global.g_HostImp = readPreference("Host");
            if (Global.g_HostImp.equals("") || Global.g_HostImp.equals("-1")) {
                mensaje.MensajeAdvertencia(MainActivity.this,"Advertencia","Dato Host de impresora vacio ");
                return false;
            }

            Global.g_portImp = readPreference("Port");
            if (Global.g_portImp.equals("") || Global.g_portImp.equals("-1")) {
                mensaje.MensajeAdvertencia(MainActivity.this,"Advertencia","Dato Puerto de impresora vacio ");
                return false;
            }

            /*Global.g_ValorConcep = Double.parseDouble(readPreference("ValConcep"));
            if ( Global.g_ValorConcep.equals("") || Global.g_ValorConcep.toString().equals("-1")) {
                bandescarga = 0;
            }

            Global.g_ValorIva = Double.parseDouble(readPreference("ValorIva"));
            if ( Global.g_ValorIva.equals("") || Global.g_ValorIva.toString().equals("-1")) {
                bandescarga = 0;
            }

            Global.g_NomConcepto = readPreference("NomConcep");
            if ( Global.g_NomConcepto.equals("") || Global.g_NomConcepto.equals("-1")) {
                bandescarga = 0;
            }

            Global.g_NumFacIni = Double.parseDouble(readPreference("NumFacIni"));
            if ( Global.g_NumFacIni.equals("") || Global.g_NumFacIni.equals("-1")) {
                bandescarga = 0;
            }

            Global.g_NumFacFin =  Double.parseDouble(readPreference("NumFacFin"));
            if ( Global.g_NumFacFin.equals("") || Global.g_NumFacFin.equals("-1")) {
                bandescarga = 0;
            }

            Global.g_NumFacAct =  Double.parseDouble(readPreference("NumFacAct"));
            if ( Global.g_NumFacAct.equals("") || Global.g_NumFacAct.equals("-1")) {
                bandescarga = 0;
            }*/


            dateFormat = new SimpleDateFormat("dd/MMM/yyyy",
                    new Locale("es","CO"));
            date = new Date();

            txtVFecha.setText("Fecha:         " + dateFormat.format(date));
            txtVAgencia.setText("Caja: " + Global.g_Caja + " Nombre Ubicación: " + Global.g_NomUbica);

            IP = Global.g_DirecApi;

            if ( bandescarga == 0 ) {
                DescargaDatos descargaDatos = new DescargaDatos(MainActivity.this);
                descargaDatos.DescargaResolucion(Global.g_Caja);
                return false;
            }

            return true;

        }
        catch(Exception ex){
            mensaje.MensajeAdvertencia(MainActivity.this,"Advertencia",ex.getMessage());
            return false;
        }
    }


    public void iniciarSesion(){
        try{

            String URL = IP + "/api/FactBanos/postNewLogin";

            sweetAlertDialog = mensaje.progreso(MainActivity.this,"Cargando Usuario");
            sweetAlertDialog.show();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("usuario",Global.g_Usuario);
            jsonBody.put("contraseña",Global.g_PassWord);
            jsonBody.put("puntoVenta",Global.g_Caja);

            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                sweetAlertDialog.dismiss();
                                RespLogin login = new Gson().fromJson(response,RespLogin.class);
                                if (login.isEstado()){
                                    if(login.getMensaje().toString().length() > 0){
                                        mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia",
                                                login.getMensaje());
                                    }
                                    Usuario usr = login.getData();
                                    if (usr.getSesion()>0){
                                        Global.g_User = usr;
                                        writePreference("sesion",usr.getSesion().toString());
                                        startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                        edTUsuario.setText("");
                                        edTPassWord.setText("");
                                    }else{
                                        mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia",
                                                "El usuario ingresado no se pudo validar!!!");
                                    }
                                }else {
                                    if(login.getMensaje().contains("Tiene que cambiar la contraseña para poder ingresar al sistema")){
                                        mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia",
                                                login.getMensaje());
                                         Intent intent = new Intent(MainActivity.this, cambiaContrasena.class);
                                         startActivity(intent);

                                    }
                                    mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia",
                                            "El usuario ingresado no se pudo validar!!!");
                                }


                                //Usuario user = new Gson().fromJson(response,Usuario.class);

                                //if (user.getSesion()>0){
                                //    Global.g_User = user;
                                //    writePreference("sesion",user.getSesion().toString());
                                //    startActivity(new Intent(MainActivity.this, MenuActivity.class));
                                //    edTUsuario.setText("");
                                //    edTPassWord.setText("");
                                //}
                                //else {
                                //    mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia",
                                //            "El usuario ingresado no se pudo validar!!!");
                                //}

                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(MainActivity.this, "2.Advertencia" , e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    },errorListener
            ) {
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


            };
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            postRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(MainActivity.this).add(postRequest);

        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(MainActivity.this, "Advertencia", ex.getMessage());
        }
    }

    public boolean writePreference(String key, String value){
        try {
            SharedPreferences prefs = getSharedPreferences("FacBan", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(MainActivity.this,"Advertencia",ex.getMessage() );
            return false;
        }
        return true;
    }

    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = getSharedPreferences("FacBan", MODE_PRIVATE);

            valor = prefs.getString(key, "-1");
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(MainActivity.this,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            try {
                sweetAlertDialog.dismiss();
                //counter.cancel();
                //startCounter = false;
                if (networkResponse != null && networkResponse.statusCode == 400) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(MainActivity.this, "7.Advertencia", error.toString());
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(MainActivity.this, "8.Advertencia", "Servidor No Responde");
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia", msj.toString());
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                }
            }
            catch(WindowManager.BadTokenException e){
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    };

    public long getDiferencia(Date fechaInicial, Date fechaFinal){

        long diferencia =  fechaFinal.getTime() -  fechaInicial.getTime();

        Log.i("MainActivity", "fechaInicial : " + fechaInicial);
        Log.i("MainActivity", "fechaFinal : " + fechaFinal);

        long segsMilli = 1000;
        long minsMilli = segsMilli * 60;
        long horasMilli = minsMilli * 60;
        long diasMilli = horasMilli * 24;

        long diasTranscurridos = diferencia / diasMilli;
        diferencia = diferencia % diasMilli;

        long horasTranscurridos = diferencia / horasMilli;
        diferencia = diferencia % horasMilli;

        long minutosTranscurridos = diferencia / minsMilli;
        diferencia = diferencia % minsMilli;

        long segsTranscurridos = diferencia / segsMilli;

        return  (diasTranscurridos);

    }

    public void Update(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(this, "Alerta", "Actualización Disponible \n ¿Actualizar Aplicación?");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    updateApp.actualizarAplicacion();
                }
            });
            sweetAlertDialog.show();
        }else{
            Permisos.requestPermission(this, Constantes.PERMISSION_ALL,Constantes.PERMISSIONS);
        }
    }

}
