package ctec.app_fac_banos;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.printer.PrinterDeviceSpec;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;
import ctec.app_fac_banos.Clases.Ruta;
import ctec.app_fac_banos.Clases.Tiquete;

public class ReImpTiqActivity extends AppCompatActivity {

    private static final String CERO = "0";
    EditText edTFlibro,edTHora,edTCedula,edTCodRuta,edTTiquete;
    TextView txtVNomRuta;
    Button btnBuscarTiq,btnObtFecha, btnBuscarRut;
    Date date;
    SimpleDateFormat dateFormat;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    Integer banHor,banImp;
    private PrinterDevice printerDevice;
    private Format format;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimp_tiq);

        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        edTFlibro = findViewById(R.id.edTFlibro);
        edTHora = findViewById(R.id.edTHora);
        edTCedula = findViewById(R.id.edTCedula);
        edTTiquete = findViewById(R.id.edTCedula);
        edTCodRuta = findViewById(R.id.edTCodRuta);
        txtVNomRuta = findViewById(R.id.txtVNomRuta);
        btnBuscarTiq = findViewById(R.id.btnBuscarTiq);
        btnObtFecha = findViewById(R.id.btnObtFecha);
        btnBuscarRut = findViewById(R.id.btnBuscarRut);

        banHor = 0;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date = new Date();
        edTFlibro.setText(dateFormat.format(date));

        btnObtFecha.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha();
            }
        }));

        btnBuscarRut.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    edTCodRuta.setText("");
                    txtVNomRuta.setText("");
                    buscarRutas();
                }
                catch (Exception e){
                    mensaje.MensajeAdvertencia(ReImpTiqActivity.this,"ADVERTENCIA",e.getMessage());
                    return;
                }
            }
        }));

        edTFlibro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                try {
                    if(!hasFocus){
                        Calendar cal = Calendar.getInstance();
                        int day = Integer.parseInt(edTFlibro.getText().toString().substring(0, 2));
                        int mon = Integer.parseInt(edTFlibro.getText().toString().substring(3, 5));
                        int year = Integer.parseInt(edTFlibro.getText().toString().substring(6, 10));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        edTFlibro.setText(String.format("%02d%02d%02d", day, mon, year));

                    }
                }catch (Exception e){
                    String cadena;
                    cadena = "Fecha del libro incorrecta";
                    mensaje.MensajeAdvertencia(ReImpTiqActivity.this, " Advertencia", cadena );

                }
            }
        });

        edTFlibro.addTextChangedListener(new TextWatcher() {

            String current = "";
            String ddmmyyyy = "DDMMYYYY";

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*This method is called to notify you that, within s, the count characters beginning at start are about to be replaced by new text with length after. It is an error to attempt to make changes to s from this callback.*/
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    if (!s.toString().equals(current)) {
                        int cursorPosition = edTFlibro.getSelectionStart();
                        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;

                        if (clean.length() < 8) {
                            clean = clean + ddmmyyyy.substring(clean.length());
                        }

                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4), clean.substring(4, 8));


                        //sel = sel < 0 ? 0 : sel;
                        current = clean;
                        edTFlibro.setText(current);
                        edTFlibro.setSelection(cursorPosition);//sel < current.length() ? sel : current.length());
                    }
                }
                catch(Exception e){
                    mensaje.MensajeAdvertencia(ReImpTiqActivity.this, " Advertencia",
                            e.getMessage() );
                }

            }

        });

        edTHora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                try {
                    if(!hasFocus){
                        if (edTHora.length() ==0)
                            return;

                        String hh,mm;
                        int hora = Integer.parseInt(edTHora.getText().toString().substring(0, 2));
                        int min = Integer.parseInt(edTHora.getText().toString().substring(3, 5));

                        hora = hora < 0 ? 0 : hora > 23 ? 23 : hora;
                        min = (min < 00) ? 00 : (min > 59) ? 59 : min;

                        if(hora<10)
                            hh = "0" + hora;
                        else
                            hh = "" + hora;

                        if(min<10)
                            mm = "0" + min;
                        else
                            mm = "" + min;

                        edTHora.setText(hh + ":" + mm);
                    }
                }catch (Exception e){
                    String cadena;
                    cadena = "Hora del libro incorrecta";
                    mensaje.MensajeAdvertencia(ReImpTiqActivity.this, " Advertencia", cadena );
                    edTHora.setText("");
                }
            }
        });

        edTHora.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                try {
                    if (edTHora.length()>4) {
                        edTHora.setSelection(5);
                    }
                }
                catch(Exception e){

                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    String current = edTHora.getText().toString();

                    current = current.replaceAll("[^\\d.]|\\.", "");
                    if ( banHor == 0 ) {
                        if (current.length() == 4) {
                            banHor = 1;
                            edTHora.setText(current.substring(0, 2) + ":" + current.substring(2, 4));
                        }
                    }
                    if (current.length() < 4)
                        banHor = 0;
                }
                catch(Exception e){

                }

            }

        });

    }

    protected void onResume() {
        super.onResume();
        if (Global.g_PosBuscar >=0) {
            switch (Global.g_OpcBuscar) {
                case 4:
                    ActualizaReimpTiquete();
                    break;
                case 3:
                    Ruta ruta = new Ruta();
                    ruta = Global.g_rutas.get(Global.g_PosBuscar);
                    edTCodRuta.setText("" + ruta.getidRuta());
                    txtVNomRuta.setText(ruta.getnombreRuta());
                    break;
                default:

            }
        }
        else if(Global.g_PosBuscar == -2){
            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "Advertencia", "Se ha cumplido el numero" +
                    " maximo de reimpresiones");
        }

    }
    
    public void BuscarTiquete(View view){
        try {

            if(edTHora.length()>0 && edTHora.length()<5){
                mensaje.MensajeAdvertencia(ReImpTiqActivity.this,"ADVERTENCIA","Debe ingresar una hora correcta");
                return;
            }


            if (edTFlibro.getText().toString().equals("") ){
                mensaje.MensajeAdvertencia(ReImpTiqActivity.this,"ADVERTENCIA","Debe ingresar la Fecha");
                return;
            }

            if (edTCedula.getText().toString().equals("") && edTCodRuta.getText().toString().equals("") &&
                edTHora.getText().toString().equals("") && edTTiquete.getText().toString().equals("") ){
                mensaje.MensajeAdvertencia(ReImpTiqActivity.this,"ADVERTENCIA","Ademas de la Fecha " +
                        "debe ingresar la Cedula o la Hora o la Ruta o el Numero de Ti q uete!!!");
                return;
            }

            if (edTCodRuta.getText().toString().equals("") ){
                    edTCodRuta.setText("0");
            }

            if (edTTiquete.getText().toString().equals("") ){
                edTTiquete.setText("0");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);


            String URL = Global.g_DirecApi + "/api/Tiquetes/ReimprimirTiquete";

            sweetAlertDialog = mensaje.progreso(ReImpTiqActivity.this,"Consultando Tiquetes");
            sweetAlertDialog.show();


            JSONObject token = new JSONObject();
            token.put("Username",Global.g_Usuario);
            token.put("Password",Global.g_PassWord);
            token.put("Serial",Global.g_Serial);


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("SVM", token);
            jsonBody.put("FechadeViaje", edTFlibro.getText().toString().substring(6,10) + "-" +
                    edTFlibro.getText().toString().substring(3,5) + "-" +
                    edTFlibro.getText().toString().substring(0,2));
            jsonBody.put("IdCliente", edTCedula.getText());
            jsonBody.put("HoraSalida", edTHora.getText());
            jsonBody.put("RutaViaje", edTCodRuta.getText());
            jsonBody.put("NumeroTiquete", edTTiquete.getText());

            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject result = null;
                    try {
                        result = new JSONObject(response);

                        sweetAlertDialog.dismiss();
                        if (result.getJSONObject("respuesta").get("codigorespuesta").equals("RT-00")){
                            Tiquete[] lista = new Gson().fromJson(result.get("tiquetes").toString(), Tiquete[].class);
                            Global.g_tiquetes = new ArrayList<Tiquete>(Arrays.asList(lista));

                            if ( Global.g_tiquetes.size() > 0) {
                                Global.g_OpcBuscar = 4;
                                startActivity(new Intent(ReImpTiqActivity.this, BuscarActivity.class));
                            }
                            else{
                                Global.g_PosBuscar = -1;
                                Global.g_OpcBuscar = -1;
                                mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "Advertencia",
                                        "No se encontraron tiq uetes para Reimprimir!!!");
                            }

                        }
                        else {
                            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, result.getJSONObject("respuesta").get("codigorespuesta").toString() + " Advertencia",
                                    result.getJSONObject("respuesta").get("mensaje").toString());
                        }


                        Log.i("VOLLEY", response);
                    } catch (JSONException e) {
                        sweetAlertDialog.dismiss();
                        e.printStackTrace();
                        mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.Advertencia" , e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    try {
                        sweetAlertDialog.dismiss();
                        if (error.networkResponse == null){
                            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.ADVERTENCIA", error.toString());
                        }
                        else {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.ADVERTENCIA", responseBody);
                        }
                    } catch (UnsupportedEncodingException errorr) {
                        mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.ADVERTENCIA"  , errorr.getMessage());
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
                        mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.Advertencia" ,
                                e.getMessage());
                    }

                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            postRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(ReImpTiqActivity.this).add(postRequest);
        } catch (JSONException e) {
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.Advertencia" ,
                    e.getMessage());
            e.printStackTrace();
        }
    }

    public void ActualizaReimpTiquete(){
        try {


            RequestQueue requestQueue = Volley.newRequestQueue(this);


            String URL = Global.g_DirecApi + "/api/Tiquetes/ActualizarReimpresiones";

            sweetAlertDialog = mensaje.progreso(ReImpTiqActivity.this,"Actualizando Reimpresion");
            sweetAlertDialog.show();

            Tiquete tiquete = Global.g_tiquetes.get(Global.g_PosBuscar);

            JSONObject token = new JSONObject();
            token.put("Username",Global.g_Usuario);
            token.put("Password",Global.g_PassWord);
            token.put("Serial",Global.g_Serial);


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("SVM", token);
            jsonBody.put("AgenciaDespacho", Global.g_Caja);
            jsonBody.put("IdLibro", tiquete.getIdLibro());
            jsonBody.put("NumTiquete",tiquete.getIdTiquete());
            jsonBody.put("NumImpresiones", tiquete.getNumImpresiones());

            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject result = null;
                    try {
                        result = new JSONObject(response);

                        sweetAlertDialog.dismiss();
                        if (result.get("codigorespuesta").equals("AR-00")){
                            imprimirTiquetes();
                        }
                        else {
                            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, result.get("codigorespuesta").toString() + " Advertencia",
                                    result.get("mensaje").toString());
                        }


                        Log.i("VOLLEY", response);
                    } catch (JSONException e) {
                        sweetAlertDialog.dismiss();
                        e.printStackTrace();
                        mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.Advertencia" , e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    try {
                        sweetAlertDialog.dismiss();
                        if (error.networkResponse == null){
                            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.ADVERTENCIA", error.toString());
                        }
                        else {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.ADVERTENCIA", responseBody);
                        }
                    } catch (UnsupportedEncodingException errorr) {
                        mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.ADVERTENCIA"  , errorr.getMessage());
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
                        mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.Advertencia" ,
                                e.getMessage());
                    }

                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            postRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(ReImpTiqActivity.this).add(postRequest);
        } catch (JSONException e) {
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "2.Advertencia" ,
                    e.getMessage());
            e.printStackTrace();
        }
    }

    private void buscarRutas(){
        try {
            String consulta;
            Ruta ruta;
            SQLiteDatabase db = Global.conn.getReadableDatabase();
            Global.g_rutas = new ArrayList<Ruta>();

            consulta = "SELECT idRuta,nombreRuta FROM RUTAS" ;


            Cursor cursor = db.rawQuery(consulta ,null);

            cursor.moveToFirst();

            for (int cont =0;cont < cursor.getCount();cont++) {
                ruta = new Ruta();
                ruta.setidRuta(Integer.parseInt(cursor.getString(0)));
                ruta.setnombreRuta(cursor.getString(1));
                Global.g_rutas.add(ruta);
                cursor.moveToNext();
            }

            cursor.moveToFirst();

            if (cursor.getCount() == 1 ){
                txtVNomRuta.setText(cursor.getString(1));
                return;
            }
            if (cursor.getCount() == 0 )
            {
                mensaje.MensajeAdvertencia(this,"Advertecia","No se encontro ruta");
                txtVNomRuta.setText("");
                return;
            }

            Global.g_OpcBuscar = 3;
            startActivity(new Intent(ReImpTiqActivity.this, BuscarActivity.class));

        }
        catch(Exception e){
            mensaje.MensajeAdvertencia(this,"Advertecia","buscarServicio:"+e.getMessage());
        }
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                final int mesActual = month + 1;

                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);

                edTFlibro.setText(diaFormateado + '/' + mesFormateado + '/' + year);


            }
        },anio, mes, dia);

        recogerFecha.show();

    }

    private void limpiar(){

        banHor = 0;
        edTHora.setText("");
        edTCedula.setText("");
        edTCodRuta.setText("");
        txtVNomRuta.setText("");

        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        date = new Date();
        edTFlibro.setText(dateFormat.format(date));

        Global.g_PosBuscar = -1;
        Global.g_OpcBuscar = -1;
    }

    /*Inicio Funciones para la impresion*/
    private void imprimirTiquetes() {

        try {

            banImp =0;
            if (printerDevice==null) {
                printerDevice = (PrinterDevice) POSTerminal.getInstance(getApplicationContext()).getDevice(
                        "cloudpos.device.printer");
            }
            handler.post(myRunnable);
            printerDevice.open();
            handler.post(myRunnable);
            format = new Format();
            try {
                if (printerDevice.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                    handler.post(myRunnable);
                    banImp = 1;
                    //mensaje.MensajeAdvertencia(EntradaActivity.this,"Info","Impresora si papel");
                    closePrinter();
                    verificarImp();
                } else if (printerDevice.queryStatus() == 1) {
                    handler.post(myRunnable);
                    final Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                String cadena="";
                                PrinterDeviceSpec printerDeviceSpec = (PrinterDeviceSpec) POSTerminal.getInstance(
                                        ReImpTiqActivity.this).getDeviceSpec("cloudpos.device.printer");

                                Tiquete tiquete = Global.g_tiquetes.get(Global.g_PosBuscar);

                                format = new Format();
                                format.setParameter("align", "center");
                                format.setParameter("bold", "true");
                                format.setParameter("size", "medium");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");

                                Bitmap bitmap = BitmapFactory.decodeResource(ReImpTiqActivity.this.getResources(),
                                        R.drawable.logo_terminal);
                                format.clear();
                                format.setParameter("align", "center");
                                printerDevice.printBitmap(format,bitmap);
                                printerDevice.printText(format, "890.905.680-2" );
                                printerDevice.printText(format, "Transversal 78 No 65-376" );
                                printerDevice.printText(format, "Tel. 4801580 Fax 4413085" );
                                printerDevice.printText(format, "MEDELLIN" );
                                printerDevice.printText(format, "info@coonorte.com.co" );
                                printerDevice.printText(format, "--------------------------------" );

                                printerDevice.printText("\n");
                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");

                                //Verifica que exita papel
                                if (printerDevice.queryStatus() == 0)
                                    banImp = 1;

                                cadena= String.format("BUS:  %4s   VALOR: %7s",tiquete.getBus(),tiquete.getValor());
                                printerDevice.printText(format, cadena+" \n" );

                                int hora = Integer.parseInt(tiquete.getHora().substring(0, 2));

                                if(hora>12)
                                    cadena =  "" + String.format("%2s",(hora-12)) +":"+ tiquete.getHora().substring(3, 5) + " pm" ;
                                else if(hora==12)
                                    cadena =  "" + String.format("%2s",(hora)) +":"+ tiquete.getHora().substring(3, 5) + " pm" ;
                                else
                                    cadena =  edTHora.getText().toString() + " am" ;


                                printerDevice.printText(format, "FECHA:"+ tiquete.getFechaSalidaLibro() + " HORA:" + cadena + "\n");
                                cadena = String.format("PUESTO: %2d      AGENCIA: %.4s",tiquete.getNumPuesto(),tiquete.getIdAgenciaLibro() );
                                printerDevice.printText(format, cadena+" \n" );
                                printerDevice.printText(format, "--------------------------------\n" );


                                cadena = String.format("ORIGEN: %.21s",tiquete.getOrigen());
                                printerDevice.printText(format, cadena+" \n" );
                                cadena = String.format("DESTINO:%.21s",tiquete.getDetino());
                                printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("CEDULA: %s",tiquete.getIdPasajero().trim());
                                printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("PASAJERO:%.21s",tiquete.getNombrePasajero().trim() );

                                printerDevice.printText(format, cadena+" \n" );
                                printerDevice.printText(format, "--------------------------------\n" );

                                cadena = String.format("Tiquete:%s Libro:%d",String.format("%-10s",tiquete.getIdTiquete().toString()),tiquete.getIdLibro());
                                printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("Vende:%.7s F.Venta:%s",tiquete.getCajaVende(),tiquete.getFechaVenta());
                                printerDevice.printText(format, cadena+" \n" );
                                cadena = String.format("Agencia que vende: %s ",tiquete.getAgenciaVenta().toString());
                                printerDevice.printText(format, cadena+" \n" );

                                format.clear();
                                format.setParameter("align", "right");
                                format.setParameter("size", "medium");
                                cadena = String.format("%.32s",Global.g_NomUbica);
                                printerDevice.printText(format, cadena+" \n" );

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                cadena = String.format("imp:%s",Global.g_CajaNomb);
                                printerDevice.printText(format, cadena+" \n" );
                                cadena = String.format("Obs.:%s",tiquete.getObservaciones());
                                printerDevice.printText(format, cadena+" \n" );

                                format.clear();
                                format.setParameter("align", "center");
                                format.setParameter("size", "medium");
                                cadena = String.format("COPIA");

                                printerDevice.printText(format, cadena+" \n" );
                                printerDevice.printText(format, "--------------------------------\n" );

                                String parts[];
                                dateFormat = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss a", Locale.getDefault());
                                cadena = dateFormat.getTimeInstance().format(date);
                                parts = cadena.split(":");
                                if (parts[0].length()==1)
                                    cadena = "0"+cadena;
                                hora = Integer.parseInt(cadena.substring(0, 2));

                                if(hora>12)
                                    cadena =  "" + String.format("%2s",(hora-12)) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                                else if(hora==12)
                                    cadena =  "" + (hora) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                                else
                                    cadena =  dateFormat.getTimeInstance().format(date).substring(0, 5) + " am" ;

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "small");
                                cadena = String.format("Fecha/hora de imp:%s",dateFormat.getDateInstance().format(date) + " " + cadena);
                                printerDevice.printText(format, cadena );

                                bitmap = Global.encode( tiquete.getCodigoBarras(), 400, 90);
                                printerDevice.printBitmap(bitmap);

                                format.clear();
                                format.setParameter("align", "center");
                                format.setParameter("size", "small");
                                printerDevice.printText(format,  tiquete.getCodigoBarras() + "\n" );
                                printerDevice.printText(format, "PRESENTE EL TIQUETE CUANDO LE SEA\n" );
                                printerDevice.printText(format, "SOLICITADO. VALIDO PARA LA FECHA\n" );
                                printerDevice.printText(format, "Y HORA INDICADAS.VER CONTRATO EN LA\n" );
                                printerDevice.printText(format, "PAGINA www.coonorte.com\n" );

                                format.clear();
                                format.setParameter("align", "center");
                                format.setParameter("size", "extra-small");
                                printerDevice.printText(format, "FacBanos. Consultores Tecnologicos S.A.S\n" );


                                //Verifica que exita papel
                                if (printerDevice.queryStatus() == 0)
                                    banImp = 1;


                               /* format.clear();
                                format.setParameter("align", "center");
                                format.setParameter("size", "large");
                                format.setParameter("HRI-location", "up-down");
                                printerDevice.printBarcode(format, printerDevice.BARCODE_CODE128, "010070250000584062019");*/

                                //Verifica que exita papel
                                if (printerDevice.queryStatus() == 0)
                                    banImp = 1;


                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                //printerDevice.printText("\n");
                                printerDevice.cutPaper();
                                //closePrinter();
                                

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        verificarImp();

                                    }
                                });
                            } catch (DeviceException e) {
                                // TODO Auto-generated catch block
                                sweetAlertDialog.dismiss();
                                e.printStackTrace();
                                try {
                                    if (printerDevice.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                                        handler.post(myRunnable);
                                    } else if (printerDevice.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                                        handler.post(myRunnable);
                                    } else {
                                        handler.post(myRunnable);
                                    }
                                } catch (DeviceException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                    handler.post(myRunnable);
                                }
                            } finally {
                                closePrinter();
                            }


                        }
                    });
                    thread.start();
                }
            } catch (DeviceException de) {
                sweetAlertDialog.dismiss();
                handler.post(myRunnable);
                de.printStackTrace();

            }
        } catch (DeviceException de) {
            sweetAlertDialog.dismiss();
            de.printStackTrace();
            handler.post(myRunnable);

        }
    }

    private void verificarImp() {

        if(banImp==0)
        {
           limpiar();
               

        }else if (banImp==1)
        {

            try {

                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(ReImpTiqActivity.this, "Impresora sin papel", "Desea reimprimir el Tiq?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            imprimirTiquetes();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "Advertencia", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            limpiar();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "Advertencia", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(ReImpTiqActivity.this, "Advertencia", e.getMessage());
                sweetAlertDialog.dismiss();
            }
        }

    }


    private void closePrinter() {
        try {
            printerDevice.close();
            handler.post(myRunnable);
        } catch (DeviceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            handler.post(myRunnable);
        }
    }

    private Handler handler = new Handler();

    private Runnable myRunnable = new Runnable() {
        public void run() {


        }
    };
    /*Fin Funciones para la impresion*/
}
