package ctec.app_fac_banos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;

public class ConfigActivity extends Activity {

    Mensaje mensaje;
    SweetAlertDialog sweetAlertDialog;
    EditText edTDirecApi,edTCaja,edTNomUbica,edTHost,edTPort;
    Button btnGrabar,btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();

        edTDirecApi = findViewById(R.id.edTDirecApi);
        edTCaja = findViewById(R.id.edTCaja);
        edTNomUbica = findViewById(R.id.edTNomUbica);
        edTHost = findViewById(R.id.edTHost);
        edTPort = findViewById(R.id.edTPort);
        btnGrabar = findViewById(R.id.btnGrabar);
        btnSalir =  findViewById(R.id.btnSalir);

        cargarDatos();

        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(ConfigActivity.this,"Informacion","Desea grabar los datos de configuracion?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        grabarDatos();
                        sweetAlertDialog.dismiss();
                    }
                });
                sweetAlertDialog.show();
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(ConfigActivity.this,"Informacion","Desea salir de la pantalla de configuracion?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        cargarDatos ();
                        finish();
                        sweetAlertDialog.dismiss();
                    }
                });
                sweetAlertDialog.show();
            }
        });

    }



    private  void cargarDatos(){

        try {

            Global.g_DirecApi = readPreference("DirecApi");
            if (!Global.g_DirecApi.equals("") && !Global.g_DirecApi.equals("-1")) {
                edTDirecApi.setText(Global.g_DirecApi);
            }

            Global.g_Caja = readPreference("Caja");
            if (!Global.g_Caja.equals("") && !Global.g_Caja.equals("-1")) {
                edTCaja.setText(Global.g_Caja);
            }

            /*Global.g_NomUbica = readPreference("NomUbica");
            if (!Global.g_NomUbica.toString().equals("") && !Global.g_NomUbica.toString().equals("-1")) {
                edTNomUbica.setText(Global.g_NomUbica);
            }*/

            Global.g_HostImp = readPreference("Host");
            if (!Global.g_HostImp.equals("") && !Global.g_HostImp.equals("-1")){
                edTHost.setText(Global.g_HostImp);
            }

            Global.g_portImp = readPreference("Port");
            if (!Global.g_portImp.equals("") && !Global.g_portImp.equals("-1")) {
                edTPort.setText(Global.g_portImp);
            }
        }
        catch(Exception ex){
            mensaje.MensajeAdvertencia(ConfigActivity.this,"Advertencia",ex.getMessage());
        }

    }

    private void grabarDatos (){
        try {
                if ( edTDirecApi.length() == 0 ) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Dato Direccion Vacio ");
                    return;
                }

                if ( edTCaja.length() == 0 ) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Dato Caja Vacio ");
                    return;
                }

                /*if ( edTNomUbica.length() == 0 ) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Dato Nombre Vacio ");
                    return;
                }*/

                if (edTHost.length() == 0) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Dato Host Impresora Vacio ");
                    return;
                }

                if (edTPort.length() == 0) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Dato Puerto Impresora Vacio ");
                    return;
                }

                if (!writePreference("DirecApi", edTDirecApi.getText().toString())) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Inconvenientes al grabar dato DirecApi");
                    return;
                }

                if (!writePreference("Caja", edTCaja.getText().toString())) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Inconvenientes al grabar dato Caja");
                    return;
                }

                if (!writePreference("Host", edTHost.getText().toString())) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Inconvenientes al grabar el host de la impresora");
                    return;
                }

                if (!writePreference("Port", edTPort.getText().toString())) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Inconvenientes al grabar el puerto de la impresora");
                    return;
                }

                /*if (!writePreference("NomUbica", edTNomUbica.getText().toString())) {
                    mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", "Inconvenientes al grabar dato Nombre");
                    return;
                }*/

                mensaje.MensajeExitoso(ConfigActivity.this,"MENSAJE","Datos Grabados Exitosamente");
                goHome();
                //esperar(2);
                //Intent intent = new Intent((getApplicationContext()),MainActivity.class);
                //startActivity(intent);
                //finish();

        }
        catch (Exception ex) {
            mensaje.MensajeExitoso(ConfigActivity.this, "Advertencia", ex.getMessage());
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
            mensaje.MensajeAdvertencia(ConfigActivity.this,"Advertencia",ex.getMessage() );
            return false;
        }
        return true;
    }

    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = getSharedPreferences("FacBan", MODE_PRIVATE);

            valor = prefs.getString(key, "");
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(ConfigActivity.this,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }

    @Override
    public void onBackPressed() {

    }

    /**
     * Pausa la ejecución durante X segundos.
     * @param segundos El número de segundos que se quiere esperar.
     */
    public static void esperar(int segundos){
        try {
            Thread.sleep(segundos * 1000);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void goHome(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent((getApplicationContext()),MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }

}
