package ctec.app_fac_banos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;
import ctec.app_fac_banos.Clases.TipoDevolucion;

public class DevolverTiqActivity extends AppCompatActivity {

    EditText edTCodAgencia,edTNumLibro,edTNumTiquete,edTCodTran,edTValorDer;
    Button btnGrabar,btnCancelar;
    Spinner spMovTrans;
    RadioButton rdoBtnAnular,rdoBtnDevolver;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    TipoDevolucion movito;
    ArrayList<String> listaMovitos;
    ArrayList<TipoDevolucion> movitosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devolver_tiq);

        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();

        edTCodAgencia = findViewById(R.id.edTCodAgencia);
        edTNumLibro = findViewById(R.id.edTNumLibro);
        edTNumTiquete = findViewById(R.id.edTNumTiquete);
        edTCodTran = findViewById(R.id.edTCodTran);
        edTValorDer = findViewById(R.id.edTValorDev);
        btnGrabar = findViewById(R.id.btnGrabar);
        btnCancelar = findViewById(R.id.btnCancelar);
        rdoBtnAnular = findViewById(R.id.rdoBtnAnular);
        rdoBtnDevolver = findViewById(R.id.rdoBtnGrabar);
        spMovTrans = findViewById(R.id.spMovTrans);

        rdoBtnDevolver.setChecked(true);
        edTCodAgencia.setText(Global.g_Caja);
        edTCodAgencia.setEnabled(false);
        buscarMotivo();

        spMovTrans.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0) {
                    edTCodTran.setText(movitosList.get(position - 1).getCodigo());
                }
                else
                    edTCodTran.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void onClick(View view) {
        boolean marcado = ((RadioButton) view).isChecked();

        switch (view.getId()) {

            case R.id.rdoBtnAnular:
                if (marcado) {
                    edTValorDer.setText("0");
                    edTValorDer.setEnabled(false);
                    btnGrabar.setText("Anular");

                }
                break;

            case R.id.rdoBtnGrabar:
                if (marcado) {
                    edTValorDer.setEnabled(true);
                    btnGrabar.setText("Devolver");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void buscarMotivo(){
        try {
            String consulta;
            SQLiteDatabase db = Global.conn.getReadableDatabase();

            movito = null;
            movitosList = new ArrayList<TipoDevolucion>();


            if (rdoBtnAnular.isChecked()) {}
            else{}

            consulta = "SELECT codigo,descripcion FROM TIPODEV ";

            Cursor cursor = db.rawQuery(consulta ,null);

            cursor.moveToFirst();

            if (cursor.getCount() == 0 ){
                mensaje.MensajeAdvertencia(this,"Advertecia","No existen datos para el tipo de transaccion seleccionado");
                return;
            }

            while (cursor.moveToNext()) {
                movito = new TipoDevolucion();
                movito.setCodigo(cursor.getString(0));
                movito.setDescripcion(cursor.getString(1));
                movitosList.add(movito);
            }

            listaMovitos = new ArrayList<String>();
            listaMovitos.add("Seleccione...");

            for (int cont=0;cont < movitosList.size();cont++){
                listaMovitos.add(movitosList.get(cont).getDescripcion() );
            }


            ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this,android.R.layout.simple_spinner_item,listaMovitos);
            spMovTrans.setAdapter(adaptador);

        }
        catch(Exception e){
            mensaje.MensajeAdvertencia(this,"Advertecia","buscarServicio:"+e.getMessage());
        }
    }

    public void grabarTransaccion(View view){
        try{
            String URL,cadena;

            if ( edTCodAgencia.length() == 0 || edTNumLibro.length() == 0 || edTNumTiquete.length() == 0 ||
                    edTCodTran.length() == 0 || edTValorDer.length() == 0 ) {
                mensaje.MensajeAdvertencia(DevolverTiqActivity.this,"ADVERTENCIA","Faltan datos por llenar");
                return;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            if (rdoBtnAnular.isChecked()) {
                URL = Global.g_DirecApi + "/api/Tiquetes/AnularTiquete";
                cadena = "Anulando Tiquete";
            }
            else {
                URL = Global.g_DirecApi + "/api/Tiquetes/DevolverTiquete";
                cadena = "Devolviendo Tiquete";
            }

            sweetAlertDialog = mensaje.progreso(DevolverTiqActivity.this,cadena);
            sweetAlertDialog.show();



            JSONObject token = new JSONObject();
            token.put("Username",Global.g_Usuario);
            token.put("Password",Global.g_PassWord);
            token.put("Serial",Global.g_Serial);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("SVM", token);
            jsonBody.put("AgenciaVenta", edTCodAgencia.getText().toString());
            jsonBody.put("LibroVenta",edTNumLibro.getText().toString() );
            jsonBody.put("NroTiquete", edTNumTiquete.getText().toString());
            if (rdoBtnDevolver.isChecked())
                jsonBody.put("ValorADevolver", edTValorDer.getText().toString());

            jsonBody.put("CodigoMotivoDevolucion", edTCodTran.getText().toString());

            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject result = null;
                            try {
                                result = new JSONObject(response);

                                if (result.get("codigorespuesta").equals("AT-00") ||
                                    result.get("codigorespuesta").equals("DT-00") ){
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeExitoso(DevolverTiqActivity.this, result.get("codigorespuesta").toString() + " Advertencia",
                                            result.get("mensaje").toString());

                                    //edTCodAgencia.setText("");
                                    edTNumLibro.setText("");
                                    edTNumTiquete.setText("");
                                    edTCodTran.setText("");
                                    edTValorDer.setText("");
                                    spMovTrans.setSelection(0);

                                }
                                else {
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(DevolverTiqActivity.this, result.get("codigorespuesta").toString() + " Advertencia",
                                            result.get("mensaje").toString());
                                }
                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(DevolverTiqActivity.this, "2.Advertencia" , e.getMessage());
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
            Volley.newRequestQueue(DevolverTiqActivity.this).add(postRequest);

        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(DevolverTiqActivity.this, "Advertencia", ex.getMessage());
        }
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
                    mensaje.MensajeAdvertencia(DevolverTiqActivity.this, "7.Advertencia", error.toString());
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(DevolverTiqActivity.this, "8.Advertencia", "Servidor No Responde");
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(DevolverTiqActivity.this, "Advertencia", msj.toString());
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
