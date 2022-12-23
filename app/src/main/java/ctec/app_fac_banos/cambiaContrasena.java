package ctec.app_fac_banos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;
import ctec.app_fac_banos.Clases.RespLogin;
import ctec.app_fac_banos.Clases.Usuario;
import ctec.app_fac_banos.Clases.Utilities;

public class cambiaContrasena extends AppCompatActivity {

    Button btnAcceso;
    TextView txtVFecha,txtVAgencia,txtVVersion;
    EditText edTUsuario,edTPassWord, edTNewPassword, edtNewPassWord2;
    Mensaje mensaje;
    SweetAlertDialog sweetAlertDialog;
    SimpleDateFormat dateFormat;
    Date date,fecIniD,fecFinD;
    String IP;
    Integer banOpe=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambia_contrasena);
        PackageInfo info= new PackageInfo();

        Global.conn= new ConexionSQliteHelper(this,"db_Coonorte",null,4);
        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();

        btnAcceso = findViewById(R.id.btnAcceso);
        edTUsuario =  findViewById(R.id.edTUsuario);
        edTPassWord =  findViewById(R.id.edTPassWord);
        edTNewPassword = findViewById(R.id.edTNewPassWord);
        txtVFecha =  findViewById(R.id.txtVFecha);
        txtVAgencia =  findViewById(R.id.txtVlbluser);
        txtVVersion =  findViewById(R.id.txtVVersion);
        edtNewPassWord2 = findViewById(R.id.edTNewPassWord2);

        try {
            info = getPackageManager( ).getPackageInfo( getPackageName( ), 0 );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        txtVVersion.setText("V."+info.versionName);

        Global.g_Serial = Build.SERIAL;
        dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
        date = new Date();

        txtVFecha.setText("Fecha:         " + dateFormat.format(date));
        txtVAgencia.setText("Caja: " + Global.g_Caja + " Nombre Ubicación: " + Global.g_NomUbica);

        IP = Global.g_DirecApi;

        btnAcceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean conectado = Utilities.isOnline(cambiaContrasena.this);
                if (edTUsuario.length() == 0){
                    mensaje.MensajeAdvertencia(cambiaContrasena.this,"Acceso Sistema","Debe Ingresar Usuario" +
                            " para realizar el cambio de contraseña");
                    return;
                }

                if (edTPassWord.length()==0){
                    mensaje.MensajeAdvertencia(cambiaContrasena.this,"Acceso Sistema","Debe Ingresar Contraseña " +
                            " para realizar el cambio de contraseña");
                    return;
                }

                if (edTNewPassword.length()==0){
                    mensaje.MensajeAdvertencia(cambiaContrasena.this,"Acceso Sistema","Debe Ingresar la nueva Contraseña " +
                            " para realizar el cambio de contraseña");
                    return;
                }
                if (edtNewPassWord2.length()==0){
                    mensaje.MensajeAdvertencia(cambiaContrasena.this,"Acceso Sistema","Debe Ingresar la Contraseña de confirmación" +
                            " para realizar el cambio de contraseña");
                    return;
                }


                String tx1 = edTNewPassword.getText().toString();
                String txt2 = edtNewPassWord2.getText().toString();

                if(tx1.equals(txt2)){
                    if (conectado) {
                        banOpe = 1;
                        iniciarSesion();
                    }
                    else{
                        mensaje.MensajeAdvertencia(cambiaContrasena.this, "Alerta", "Movil sin conexion");
                        return;
                    }
                }else {
                    mensaje.MensajeAdvertencia(cambiaContrasena.this,"Acceso Sistema","Las contraseña nueva y confirmación deben ser iguales");
                    return;
                }

            }
        });

    }

    public void iniciarSesion(){
        try{

            String URL = IP + "/api/FactBanos/postActualizaContrasena";

            sweetAlertDialog = mensaje.progreso(cambiaContrasena.this,"Cargando Parametros");
            sweetAlertDialog.show();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("usuario",edTUsuario.getText());
            jsonBody.put("contrasena",edTPassWord.getText());
            jsonBody.put("contrasenaNueva",edTNewPassword.getText());
            jsonBody.put("sesion",readPreference("sesion"));

            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                sweetAlertDialog.dismiss();
                                RespLogin login = new Gson().fromJson(response,RespLogin.class);
                                if (login.isEstado()){
                                    if(login.getMensaje() != ""){
                                        mensaje.MensajeAdvertencia(cambiaContrasena.this, "Advertencia",
                                                login.getMensaje());
                                    }
                                    Usuario usr = login.getData();
                                    if (usr.getSesion()>0){
                                        Global.g_User = usr;
                                        writePreference("sesion",usr.getSesion().toString());
                                        startActivity(new Intent(cambiaContrasena.this, MenuActivity.class));
                                        edTUsuario.setText("");
                                        edTPassWord.setText("");
                                    }
                                }else {

                                    mensaje.MensajeAdvertencia(cambiaContrasena.this, "Advertencia",
                                            login.getMensaje());
                                }


                                Usuario user = new Gson().fromJson(response,Usuario.class);

                                if (user.getSesion()>0){
                                    Global.g_User = user;
                                    writePreference("sesion",user.getSesion().toString());
                                    startActivity(new Intent(cambiaContrasena.this, MenuActivity.class));
                                    edTUsuario.setText("");
                                    edTPassWord.setText("");
                                }
                                else {
                                    mensaje.MensajeAdvertencia(cambiaContrasena.this, "Advertencia",
                                            "El usuario ingresado no se pudo validar!!!");
                                }
                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(cambiaContrasena.this, "2.Advertencia" , e.getMessage());
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
            Volley.newRequestQueue(cambiaContrasena.this).add(postRequest);

        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(cambiaContrasena.this, "Advertencia", ex.getMessage());
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
            mensaje.MensajeAdvertencia(cambiaContrasena.this,"Advertencia",ex.getMessage() );
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
            mensaje.MensajeAdvertencia(cambiaContrasena.this,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
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
                    mensaje.MensajeAdvertencia(cambiaContrasena.this, "7.Advertencia", error.toString());
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(cambiaContrasena.this, "8.Advertencia", "Servidor No Responde");
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(cambiaContrasena.this, "Advertencia", msj.toString());
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

}