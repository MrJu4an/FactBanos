package ctec.app_fac_banos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.DocGenerales;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;

public class DocumentosGen extends AppCompatActivity {

    EditText edTIdComp,edTIdCliente,edTNombres,edTPriApe,edTSegApe,edTIdent2,edTCantidad,edTObser,edTDocSoporte,edTValDoc,edTNumDoc;
    TextView txtVNomComp,txtVNumDoc,txtVIdCliente,txtVNombres2,txtVPriApe,txtVSegApe2,txtVIdentificador,txtVCantidad,txtVObserv,txtVDocSoporte,txtVValor;
    Button btnAnulaDoc,btnGrabaDoc,btnSalir,btnBuscarComp;
    RadioButton rdoBtnAnular,rdoBtnGrabar;

    int requiereId2 = 0;

    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentos_gen);

        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        txtVIdCliente = findViewById(R.id.txtVIdCliente);
        txtVNombres2 = findViewById(R.id.txtVNombres2);
        txtVPriApe = findViewById(R.id.txtVlblValor);
        txtVSegApe2 = findViewById(R.id.txtVSegApe2);
        txtVIdentificador = findViewById(R.id.txtVIdentificador);
        txtVCantidad = findViewById(R.id.txtVCantidad);
        txtVObserv = findViewById(R.id.txtVObserv);
        txtVDocSoporte = findViewById(R.id.txtVDocSoporte);
        txtVValor = findViewById(R.id.txtVValor);

        edTIdComp = findViewById(R.id.edTIdComp);
        edTIdCliente = findViewById(R.id.edTIdCliente);
        edTNombres = findViewById(R.id.edTNombres);
        edTPriApe = findViewById(R.id.edTPriApe);
        edTSegApe = findViewById(R.id.edTSegApe);
        edTIdent2 = findViewById(R.id.edTIdent2);
        edTCantidad = findViewById(R.id.edTCantidad);
        edTObser = findViewById(R.id.edTObser);
        edTDocSoporte = findViewById(R.id.edTDocSoporte);
        edTValDoc = findViewById(R.id.edTValDoc);
        edTNumDoc = findViewById(R.id.edTNumDoc);
        txtVNomComp = findViewById(R.id.txtVNomComp);
        txtVNumDoc = findViewById(R.id.txtVNumDoc);
        btnAnulaDoc = findViewById(R.id.btnAnulaDoc);
        btnGrabaDoc = findViewById(R.id.btnGrabaDoc);
        btnSalir = findViewById(R.id.btnSalir);
        btnBuscarComp = findViewById(R.id.btnBuscarComp);
        rdoBtnAnular = findViewById(R.id.rdoBtnAnular);
        rdoBtnGrabar = findViewById(R.id.rdoBtnGrabar);

        rdoBtnGrabar.setChecked(true);
        actcontroles(1);

        edTIdCliente.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                try{
                    if(!hasFocus) {
                        if (edTIdCliente.length() == 0)
                            return;

                        buscarCliente();
                    }
                }
                catch(Exception ex){
                    mensaje.MensajeAdvertencia(DocumentosGen.this,"Advertecia","FocusCliente:"+ex.getMessage());
                }
            }
        });

        edTIdComp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                try{
                    if(!hasFocus) {
                        if (edTIdComp.length() == 0)
                            return;

                        buscarComprobante();
                    }
                }
                catch(Exception ex){
                    mensaje.MensajeAdvertencia(DocumentosGen.this,"Advertecia","FocusComprobante:"+ex.getMessage());
                }
            }
        });

        edTIdent2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try{
                    if(!hasFocus) {
                        if (requiereId2 == 0)
                            return;

                        if( edTIdent2.getText().toString().trim().length() == 0 )
                            mensaje.MensajeAdvertencia(DocumentosGen.this,"Advertecia","EL comprobante requeire 2° Identificador ");
                    }
                }
                catch(Exception ex){
                    mensaje.MensajeAdvertencia(DocumentosGen.this,"Advertecia","FocusComprobante:"+ex.getMessage());
                }
            }
        });

        edTValDoc.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                //edTValDoc.setText("" + NumberFormat.getInstance().format(Integer.parseInt(edTValDoc.getText().toString())));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

        });
        /*
        edTValDoc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                try {
                    if(!hasFocus){
                        if (edTValDoc.length() ==0)
                            return;

                        edTValDoc.setText("" + NumberFormat.getInstance().format(Integer.parseInt(edTValDoc.getText().toString())));
                    }
                }catch (Exception e){

                }
            }
        });*/

    }

    protected void onResume(){
        super.onResume();
        if (Global.g_PosBuscar >=0){
            switch (Global.g_OpcBuscar){
                case 5 :
                    DocGenerales docGenerales = new DocGenerales();
                    docGenerales = Global.g_docGenerales.get(Global.g_PosBuscar);
                    edTIdComp.setText(docGenerales.getIdComprobante());
                    txtVNomComp.setText(docGenerales.getNombreComprobante());
                    if ( docGenerales.getRequiereId2() == false )
                        requiereId2 = 0;
                    else
                        requiereId2 = 1;
                    edTCantidad.setText("1");
                    Global.g_PosBuscar=-1;
                    break;
                default:
            }
        }
    }

    private void buscarCliente(){
        try {


            RequestQueue requestQueue = Volley.newRequestQueue(this);


            String URL = Global.g_DirecApi + "/api/Clientes/GetCliente";

            sweetAlertDialog = mensaje.progreso(DocumentosGen.this,"Consultando Clientes");
            sweetAlertDialog.show();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("IdCliente", edTIdCliente.getText());

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject result = null;
                    try {
                        result = new JSONObject(response);

                        sweetAlertDialog.dismiss();
                        if (result.getJSONObject("respuesta").get("codigorespuesta").equals("GC-00")){
                            edTNombres.setText(result.get("nombres").toString());
                            edTPriApe.setText(result.get("apellidoUno").toString());
                            edTSegApe.setText(result.get("apellidoDos").toString());
                        }

                        Log.i("VOLLEY", response);
                    } catch (JSONException e) {
                        sweetAlertDialog.dismiss();
                        e.printStackTrace();
                        mensaje.MensajeAdvertencia(DocumentosGen.this, "2.Advertencia" , e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    try {
                        sweetAlertDialog.dismiss();
                        if (error.networkResponse == null){
                            mensaje.MensajeAdvertencia(DocumentosGen.this, "2.ADVERTENCIA", error.toString());
                        }
                        else {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            mensaje.MensajeAdvertencia(DocumentosGen.this, "2.ADVERTENCIA", responseBody);
                        }
                    } catch (UnsupportedEncodingException errorr) {
                        mensaje.MensajeAdvertencia(DocumentosGen.this, "2.ADVERTENCIA"  , errorr.getMessage());
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
                        mensaje.MensajeAdvertencia(DocumentosGen.this, "2.Advertencia" ,
                                e.getMessage());
                    }

                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(DocumentosGen.this, "2.Advertencia" ,
                    e.getMessage());
            e.printStackTrace();
        }
    }

    public void buscarComprobante( ){
        try {
            String consulta;
            SQLiteDatabase db = Global.conn.getReadableDatabase();


            if (edTIdComp.length() == 0)
                return;

            consulta = "SELECT nombreComprobante,requiereId2 FROM DOCGEN WHERE intIdAgencia =  "  + Global.g_Caja +
                    " AND idComprobante = '" + edTIdComp.getText().toString().trim() +"'";

            Cursor cursor = db.rawQuery(consulta ,null);

            cursor.moveToFirst();

            if (cursor.getCount() > 0 ){
                txtVNomComp.setText(cursor.getString(0));
                requiereId2 = cursor.getInt(1);
                edTCantidad.setText("1");
            }
            else
            {
                mensaje.MensajeAdvertencia(this,"Advertecia","No se encontro Comprobante");
                txtVNomComp.setText("");
                requiereId2 =0;
            }

        }
        catch(Exception e){
            mensaje.MensajeAdvertencia(this,"Advertecia","buscarComprobante:"+e.getMessage());
        }
    }

    public void buscarComprobantes(View view){
        try {
            String consulta;
            DocGenerales docGenerales;
            SQLiteDatabase db = Global.conn.getReadableDatabase();
            Global.g_docGenerales = new ArrayList<DocGenerales>();

            consulta = "SELECT idComprobante,nombreComprobante,requiereId2 FROM DOCGEN WHERE intIdAgencia =  "  + Global.g_Caja;

            Cursor cursor = db.rawQuery(consulta ,null);

            cursor.moveToFirst();

            if (cursor.getCount() == 0 ){
                mensaje.MensajeAdvertencia(this,"Advertecia","No existen comprobantes contables para esta agencia");
                return;
            }

            for (int cont =0;cont < cursor.getCount();cont++) {
                docGenerales = new DocGenerales();
                docGenerales.setIdComprobante(cursor.getString(0));
                docGenerales.setNombreComprobante(cursor.getString(1));
                //docGenerales.setRequiereId2(cursor.getInt(1));
                Global.g_docGenerales.add(docGenerales);
                cursor.moveToNext();
            }

            Global.g_OpcBuscar = 5;
            startActivity(new Intent(DocumentosGen.this, BuscarActivity.class));

        }
        catch(Exception e){
            mensaje.MensajeAdvertencia(this,"Advertecia","buscarServicio:"+e.getMessage());
        }
    }

    public void grabarDoc (View view){
        try{

            if ( edTIdComp.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el Codigo del Comprobante");
                return;
            }
            if (edTIdCliente.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el Codigo del cliente");
                return;
            }
            if (edTNombres.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el Nombre del cliente");
                return;
            }
            if (edTPriApe.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el 1° Apellido del cliente");
                return;
            }

            if (edTIdent2.getText().toString().trim().equals("") && requiereId2==1){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el Codigo del cliente");
                return;
            }
            if (edTCantidad.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar la Cantidad");
                return;
            }
            if (edTObser.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar la Observación");
                return;
            }
            if (edTDocSoporte.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el Documento de Soporte");
                return;
            }
            if (edTValDoc.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el valor del Documento");
                return;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String URL = Global.g_DirecApi + "/api/DocumentosG/IngresarDocumentoGeneral";

            sweetAlertDialog = mensaje.progreso(DocumentosGen.this,"Grabando Documento");
            sweetAlertDialog.show();

            JSONObject token = new JSONObject();
            token.put("Username",Global.g_Usuario);
            token.put("Password",Global.g_PassWord);
            token.put("Serial",Global.g_Serial);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("SVM", token);
            jsonBody.put("IdComprobante",edTIdComp.getText().toString().trim());
            jsonBody.put("IdCliente",edTIdCliente.getText().toString().trim());
            jsonBody.put("NombreCliente",edTNombres.getText().toString().trim());
            jsonBody.put("Apellido1",edTPriApe.getText().toString().trim());
            jsonBody.put("Apellido2",edTSegApe.getText().toString().trim());
            jsonBody.put("IdIdentificadorDos",edTIdent2.getText().toString().trim());
            jsonBody.put("Cantidad",Integer.parseInt(edTCantidad.getText().toString().trim()));
            jsonBody.put("Valor",Double.parseDouble(edTValDoc.getText().toString().trim().replace(",","")));
            jsonBody.put("DocumentoSoporte",edTDocSoporte.getText().toString().trim());
            jsonBody.put("Observaciones",edTObser.getText().toString().trim());

            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject result = null;
                            try {
                                result = new JSONObject(response);
                                sweetAlertDialog.dismiss();
                                if (result.getJSONObject("respuesta").get("codigorespuesta").equals("ID-00")){
                                    mensaje.MensajeExitoso(DocumentosGen.this,"Exitoso","Documento:" + result.getString("numeroDocumento") + "\n" +result.getJSONObject("respuesta").get("mensaje").toString());
                                    limpiar();
                                }
                                else {
                                    mensaje.MensajeAdvertencia(DocumentosGen.this, result.getJSONObject("respuesta").get("codigorespuesta").toString() + " Advertencia",
                                            result.getJSONObject("respuesta").get("mensaje").toString());
                                }
                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(DocumentosGen.this, "2.Advertencia" , e.getMessage());
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
            Volley.newRequestQueue(DocumentosGen.this).add(postRequest);


        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(DocumentosGen.this, "Advertencia", ex.getMessage());
        }
    }

    public void anularDoc (View view){
        try{

            if ( edTIdComp.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el Codigo del Comprobante");
                return;
            }
            if (edTNumDoc.getText().toString().trim().equals("")){
                mensaje.MensajeAdvertencia(this,"Advertecia","Debe llenar el Numero del Documento");
                return;
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String URL = Global.g_DirecApi + "/api/DocumentosG/AnularDocumentoGeneral";

            sweetAlertDialog = mensaje.progreso(DocumentosGen.this,"Grabando Documento");
            sweetAlertDialog.show();

            JSONObject token = new JSONObject();
            token.put("Username",Global.g_Usuario);
            token.put("Password",Global.g_PassWord);
            token.put("Serial",Global.g_Serial);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("SVM", token);
            jsonBody.put("IdComprobante",edTIdComp.getText().toString().trim());
            jsonBody.put("NumeroDocumento",edTNumDoc.getText().toString().trim());

            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject result = null;
                            try {
                                result = new JSONObject(response);
                                sweetAlertDialog.dismiss();
                                if (result.getString("codigorespuesta").equals("ID-00")){
                                    mensaje.MensajeExitoso(DocumentosGen.this,"Exitoso",result.getString("mensaje"));
                                    limpiar();
                                }
                                else {
                                    mensaje.MensajeAdvertencia(DocumentosGen.this, result.getString("codigorespuesta") + " Advertencia",
                                            result.getString("mensaje"));
                                }
                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(DocumentosGen.this, "2.Advertencia" , e.getMessage());
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
            Volley.newRequestQueue(DocumentosGen.this).add(postRequest);


        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(DocumentosGen.this, "Advertencia", ex.getMessage());
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
                    mensaje.MensajeAdvertencia(DocumentosGen.this, "7.Advertencia", error.toString());
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(DocumentosGen.this, "8.Advertencia", "Servidor No Responde");
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(DocumentosGen.this, "Advertencia", msj.toString());
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

    private void limpiar(){

        edTIdComp.setText("");
        edTIdCliente.setText("");
        edTNombres.setText("");
        edTPriApe.setText("");
        edTSegApe.setText("");
        edTIdent2.setText("");
        edTCantidad.setText("");
        edTObser.setText("");
        edTDocSoporte.setText("");
        edTValDoc.setText("");
        txtVNomComp.setText("");
        edTNumDoc.setText("");
        requiereId2=0;


    }

    public void onClick(View view) {

        boolean marcado;

        switch (view.getId()) {

            case R.id.rdoBtnAnular:
                marcado = ((RadioButton) view).isChecked();
                if (marcado) {
                    actcontroles(0);
                }
                break;

            case R.id.rdoBtnGrabar:
                marcado = ((RadioButton) view).isChecked();
                if (marcado) {
                    actcontroles(1);
                }
                break;

            case R.id.btnSalir:
                finish();
                break;

        }

    }

    private void actcontroles (int estado){

        if (estado == 0) {

            edTIdCliente.setVisibility(View.INVISIBLE);
            edTNombres.setVisibility(View.INVISIBLE);
            edTPriApe.setVisibility(View.INVISIBLE);
            edTSegApe.setVisibility(View.INVISIBLE);
            edTIdent2.setVisibility(View.INVISIBLE);
            edTCantidad.setVisibility(View.INVISIBLE);
            edTObser.setVisibility(View.INVISIBLE);
            edTDocSoporte.setVisibility(View.INVISIBLE);
            edTValDoc.setVisibility(View.INVISIBLE);
            edTNumDoc.setVisibility(View.INVISIBLE);
            txtVIdCliente.setVisibility(View.INVISIBLE);
            txtVNombres2.setVisibility(View.INVISIBLE);
            txtVPriApe.setVisibility(View.INVISIBLE);
            txtVIdentificador.setVisibility(View.INVISIBLE);
            txtVCantidad.setVisibility(View.INVISIBLE);
            txtVObserv.setVisibility(View.INVISIBLE);
            txtVDocSoporte.setVisibility(View.INVISIBLE);
            txtVValor.setVisibility(View.INVISIBLE);
            txtVSegApe2.setVisibility(View.INVISIBLE);
            btnGrabaDoc.setVisibility(View.INVISIBLE);

            txtVNumDoc.setVisibility(View.VISIBLE);
            edTNumDoc.setVisibility(View.VISIBLE);
            btnAnulaDoc.setVisibility(View.VISIBLE);
            edTIdComp.setVisibility(View.VISIBLE);
            txtVNomComp.setVisibility(View.VISIBLE);
            btnBuscarComp.setVisibility(View.VISIBLE);

        }
        else
        {
            edTIdComp.setVisibility(View.VISIBLE);
            edTIdCliente.setVisibility(View.VISIBLE);
            edTNombres.setVisibility(View.VISIBLE);
            edTPriApe.setVisibility(View.VISIBLE);
            edTSegApe.setVisibility(View.VISIBLE);
            edTIdent2.setVisibility(View.VISIBLE);
            edTCantidad.setVisibility(View.VISIBLE);
            edTObser.setVisibility(View.VISIBLE);
            edTDocSoporte.setVisibility(View.VISIBLE);
            edTValDoc.setVisibility(View.VISIBLE);
            edTNumDoc.setVisibility(View.VISIBLE);
            txtVNomComp.setVisibility(View.VISIBLE);
            txtVIdCliente.setVisibility(View.VISIBLE);
            txtVNombres2.setVisibility(View.VISIBLE);
            txtVPriApe.setVisibility(View.VISIBLE);
            txtVSegApe2.setVisibility(View.VISIBLE);
            txtVIdentificador.setVisibility(View.VISIBLE);
            txtVCantidad.setVisibility(View.VISIBLE);
            txtVObserv.setVisibility(View.VISIBLE);
            txtVDocSoporte.setVisibility(View.VISIBLE);
            txtVValor.setVisibility(View.VISIBLE);
            btnGrabaDoc.setVisibility(View.VISIBLE);
            btnBuscarComp.setVisibility(View.VISIBLE);

            txtVNumDoc.setVisibility(View.INVISIBLE);
            edTNumDoc.setVisibility(View.INVISIBLE);
            btnAnulaDoc.setVisibility(View.INVISIBLE);
        }

    }
}
