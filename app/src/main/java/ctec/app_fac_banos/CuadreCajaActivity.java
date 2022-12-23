package ctec.app_fac_banos;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.printer.PrinterDeviceSpec;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.FactBanos;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;
import ctec.app_fac_banos.ListView.ListVAdapDetCaja;

public class CuadreCajaActivity extends AppCompatActivity {

    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    Date date;
    SimpleDateFormat dateFormat;
    FactBanos[] factBanos;
    ListView listVDetalle;
    Button btnImpCuadre;
    TextView txtVValorTotal,txtVNomBano,txtVNomUsuario;
    private ListVAdapDetCaja adapDetCaja;
    private PrinterDevice printerDevice;
    private Format format;
    private Handler handler = new Handler();
    int banImp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuadre_caja);

        listVDetalle = findViewById(R.id.listVDetalle);
        txtVValorTotal = findViewById(R.id.txtVotalCaja);
        txtVNomBano =  findViewById(R.id.txtVNomBano);
        txtVNomUsuario = findViewById(R.id.txtVNomUsuario);
        btnImpCuadre = findViewById(R.id.btnImpCuadre);


        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        Global.g_Total=0.0;
        txtVNomUsuario.setText(Global.g_User.getUsuario());
        txtVNomBano.setText(Global.g_NomUbica);
        consultaDetalleCaja();

        btnImpCuadre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imprimirCuadre();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void consultaDetalleCaja(){
        try{

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String URL = Global.g_DirecApi + "/api/FactBanos/PostDetCaja";

            sweetAlertDialog = mensaje.progreso(CuadreCajaActivity.this,"Consultando cuadre caja");
            sweetAlertDialog.show();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("usuario",Global.g_Usuario);
            jsonBody.put("sesion",Global.g_User.getSesion());
            jsonBody.put("caja",Global.g_Caja);
            jsonBody.put("puntoVenta",readPreference("Caja"));
            final String requestBody = jsonBody.toString();

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject result = null;
                            Integer total=0;
                            try {

                                factBanos = new Gson().fromJson(response, FactBanos[].class);
                                if (factBanos != null){

                                    ArrayList<FactBanos> ArrfactBanos = new ArrayList<FactBanos>(Arrays.asList(factBanos));

                                    for (int cont=0;cont < factBanos.length;cont++){
                                        total = total + factBanos[cont].getRBTOTFAC();
                                    }

                                    txtVValorTotal.setText(NumberFormat.getInstance().format(total));

                                    adapDetCaja = new ListVAdapDetCaja(CuadreCajaActivity.this,ArrfactBanos);
                                    listVDetalle.setAdapter(adapDetCaja);
                                    sweetAlertDialog.dismiss();
                                    btnImpCuadre.setEnabled(true);
                                }
                                else {
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeExitoso(CuadreCajaActivity.this, "Mensaje",
                                            "Aun no hay usos para Mostrar!!!");
                                }
                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(CuadreCajaActivity.this, "2.Advertencia" , e.getMessage());
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
            Volley.newRequestQueue(CuadreCajaActivity.this).add(postRequest);

        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(CuadreCajaActivity.this, "Advertencia", ex.getMessage());
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
                    mensaje.MensajeAdvertencia(CuadreCajaActivity.this, "7.Advertencia", error.toString());
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(CuadreCajaActivity.this, "8.Advertencia", "Servidor No Responde");
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(CuadreCajaActivity.this, "Advertencia", msj.toString());
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


    /*Inicio Funciones para la impresionLibro*/
    private void imprimirCuadre() {

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
                    verificarImpCuadre();
                } else if (printerDevice.queryStatus() == 1) {
                    handler.post(myRunnable);
                    final Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                String cadena="";
                                int hora;
                                PrinterDeviceSpec printerDeviceSpec = (PrinterDeviceSpec) POSTerminal.getInstance(
                                        CuadreCajaActivity.this).getDeviceSpec("cloudpos.device.printer");


                                format = new Format();
                                format.setParameter("align", "center");
                                format.setParameter("bold", "true");
                                format.setParameter("size", "medium");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");

                                Bitmap bitmap = BitmapFactory.decodeResource(CuadreCajaActivity.this.getResources(),
                                        R.drawable.logoterminalpereira);
                                format.clear();
                                printerDevice.printBitmap(format,bitmap);
                                format.setParameter("align", "center");
                                printerDevice.printText(format, Global.g_NomEmp );
                                printerDevice.printText(format, "Nit." + Global.g_Nit );
                                printerDevice.printText(format, "--------------------------------" );

                                printerDevice.printText("\n");
                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");

                                //Verifica que exita papel
                                if (printerDevice.queryStatus() == 0)
                                    banImp = 1;

                                dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
                                date = new Date();
                                cadena = dateFormat.getTimeInstance().format(date);
                                String[] parts = cadena.split(":");
                                if (parts[0].length()==1)
                                    cadena = "0"+cadena;
                                hora = Integer.parseInt(cadena.substring(0, 2));

                                if(hora>12)
                                    cadena =  "" + String.format("%2s",(hora-12)) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                                else if(hora==12)
                                    cadena =  "" + (hora) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                                else
                                    cadena =  dateFormat.getTimeInstance().format(date).substring(0, 5) + " am" ;

                                cadena = String.format("%s",dateFormat.getDateInstance().format(date) + " " + cadena);
                                printerDevice.printText(format, cadena );

                                cadena = String.format("Ubicacion:%.24s",Global.g_NomUbica );
                                printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("Caja:%.6s Cod Usu:%.16s",Global.g_Caja,Global.g_Usuario);
                                printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("Usuario:%.26s",Global.g_User.getUsuario());
                                printerDevice.printText(format, cadena+" \n\n" );

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "small");

                                printerDevice.printText(format, "Num Fact | MM/dd/yyyy HH:mm:ss |Cnt| Total\n" );
                                printerDevice.printText(format, "------------------------------------------\n" );

                                int cont,total=0;
                                for (cont=0;cont < factBanos.length;cont++){

                                    cadena = String.format("%9s ",factBanos[cont].getRBPREFACT().toString() + "-" + factBanos[cont].getRBNUMFAC().toString());

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());

                                    cadena = cadena + String.format(" %20s ",dateFormat.format(convertDate(factBanos[cont].getRBFECHA(),"MM/dd/yyyy HH:mm:ss")));

                                    cadena = cadena+String.format("%3s ",factBanos[cont].getRBCANTUSOS().toString());


                                    cadena = cadena + String.format("%6s",NumberFormat.getInstance().format(factBanos[cont].getRBTOTFAC()));

                                    total= total + factBanos[cont].getRBTOTFAC();
                                    printerDevice.printText(format, cadena );

                                }

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, "\n" );

                                cadena = String.format("TOTAL USOS           %11s",cont);
                                printerDevice.printText(format, cadena );

                                cadena = String.format("TOTAL VENTAS         %11s",NumberFormat.getInstance().format(total));
                                printerDevice.printText(format, cadena );

                                printerDevice.printText(format, "\n\n" );



                                printerDevice.printText(format, "--------------------------------\n" );
                                printerDevice.printText(format, "FIRMA TAQUILLERO\n\n" );

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


                                        verificarImpCuadre();

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

    private void verificarImpCuadre() {

        if(banImp==0){
            mensaje.MensajeExitoso(CuadreCajaActivity.this, "IMPRESION", "Impresion Exitosa");
        }else if (banImp==1)
        {

            try {

                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(CuadreCajaActivity.this, "Impresora sin papel", "Desea reimprimir la Ult?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            banImp=0;
                            imprimirCuadre();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(CuadreCajaActivity.this, "Advertencia", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(CuadreCajaActivity.this, "Advertencia", e.getMessage());
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


    private Runnable myRunnable = new Runnable() {
        public void run() {


        }
    };
    /*Fin Funciones para la impresion*/

    private Date convertDate(String date,String Format){
        Date dateResult = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(Format);
            String dateInString = date;
            dateResult = formatter.parse(dateInString);

        } catch (ParseException ex) {

        }
        return  dateResult;
    }
    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = getSharedPreferences("FacBan", MODE_PRIVATE);

            valor = prefs.getString(key, "-1");
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(CuadreCajaActivity.this,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }

}
