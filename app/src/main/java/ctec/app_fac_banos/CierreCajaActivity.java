package ctec.app_fac_banos;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.TcpIpOutputStream;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.DetalleCaja;
import ctec.app_fac_banos.Clases.FactBanos;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;
import ctec.app_fac_banos.ListView.ListVAdapDetCaja;
import ctec.app_fac_banos.ListView.ListVAdapDetCierre;

public class CierreCajaActivity extends AppCompatActivity {

    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    Date date;
    SimpleDateFormat dateFormat;
    TextView txtVNomBano,txtVNomUsuario,txtVTotalCaja,txtVTotalImp,txtVTotalUsos,txtVTotalFact, txtVsubtotal, txtVTotalIva;
    ListView listViewCierre;
    FactBanos[] factBanos;
    ListVAdapDetCierre adapDetCierre;

    Button btnSalir,btnCerrarCaja;
    private PrinterDevice printerDevice;
    private Format format;
    private Handler handler = new Handler();
    int banImp;
    Integer TotalCaja=0,TotalImp=0,TotalUsos=0,TotalFact=0, subTotal = 0, totalIva = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre_caja);

        txtVNomBano =  findViewById(R.id.txtVNomBano);
        txtVNomUsuario = findViewById(R.id.txtVNomUsuario);
        txtVTotalCaja = findViewById(R.id.txtVTotalCaja);
        txtVTotalImp = findViewById(R.id.txtVTotalImp);
        txtVTotalUsos = findViewById(R.id.txtVTotUsos);
        txtVTotalFact = findViewById(R.id.txtVTotalFact);
        listViewCierre = findViewById(R.id.listViewCierre);

        txtVTotalIva = findViewById(R.id.txtVTotalIva);
        txtVsubtotal = findViewById(R.id.txtVSubTotalCaja);

        txtVNomUsuario.setText(Global.g_User.getUsuario());
        txtVNomBano.setText(Global.g_NomUbica);




        btnSalir = findViewById(R.id.btnSalir);
        btnCerrarCaja = findViewById(R.id.btnCerrarCaja);

        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();

        consultaDetalleCaja();
        
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSalir:
                finish();
                break;
            case R.id.btnCerrarCaja:
                //Datos impresora
                String host = Global.g_HostImp;
                Integer port = Integer.parseInt(Global.g_portImp);
                imprimirCierreKR5(host, port);
                //imprimirCierre();
                break;
        }

    }

    @Override
    public void onBackPressed() {

    }

    public void consultaDetalleCaja(){
        try{

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String URL = Global.g_DirecApi + "/api/FactBanos/PostDetCierre";

            sweetAlertDialog = mensaje.progreso(CierreCajaActivity.this,"Consultando cierre de caja");
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

                                    for (FactBanos fact: factBanos){
                                        TotalCaja = TotalCaja + fact.getRBTOTFAC();
                                        TotalImp = TotalImp + Integer.parseInt(fact.getRBIMPFACT());
                                        TotalUsos = TotalUsos + Integer.parseInt(fact.getRBCANTUSOS().toString());
                                        TotalFact = TotalFact + Integer.parseInt(fact.getRBNUMFAC().toString());
                                        subTotal = subTotal + Integer.parseInt(fact.getRBVALOR().toString());
                                        totalIva = totalIva + Integer.parseInt(fact.getRBIVA().toString());
                                    }

                                    ArrayList<FactBanos> ArrfactBanos = new ArrayList<FactBanos>(Arrays.asList(factBanos));

                                    adapDetCierre = new ListVAdapDetCierre(CierreCajaActivity.this,ArrfactBanos);
                                    listViewCierre.setAdapter(adapDetCierre);

                                    txtVTotalCaja.setText(NumberFormat.getInstance().format(TotalCaja));
                                    txtVTotalImp.setText(TotalImp.toString());
                                    txtVTotalUsos.setText(TotalUsos.toString());
                                    txtVTotalFact.setText(TotalFact.toString());
                                    txtVsubtotal.setText(NumberFormat.getInstance().format(subTotal));
                                    txtVTotalIva.setText(NumberFormat.getInstance().format(totalIva));

                                    sweetAlertDialog.dismiss();
                                }
                                else {
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeExitoso(CierreCajaActivity.this, "Mensaje",
                                            "No se encontraron datos!!!");
                                }
                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(CierreCajaActivity.this, "2.Advertencia" , e.getMessage());
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
            Volley.newRequestQueue(CierreCajaActivity.this).add(postRequest);

        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(CierreCajaActivity.this, "Advertencia", ex.getMessage());
        }
    }

    public void cerrarCaja(){
        try{
            String URL = Global.g_DirecApi +"/api/FactBanos/PostCierre";

            sweetAlertDialog = mensaje.progreso(CierreCajaActivity.this,"Cerrando Caja");
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

                            try {
                                String result[] = new Gson().fromJson(response, String.class).split(";");
                                sweetAlertDialog.dismiss();
                                if (result[0].equals("OK")){
                                    writePreference("sesion","0");
                                    sweetAlertDialog = mensaje.MensajeConfirmacionExitosoConUnBoton(CierreCajaActivity.this, "Exitoso",
                                            "Cierre realizado correctamente!!!");

                                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            finish();
                                        }
                                    });
                                    sweetAlertDialog.show();
                                }
                                else {
                                    mensaje.MensajeAdvertencia(CierreCajaActivity.this, "Advertencia",
                                            result[1]);
                                }
                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(CierreCajaActivity.this, "2.Advertencia" , e.getMessage());
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
            Volley.newRequestQueue(CierreCajaActivity.this).add(postRequest);

        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(CierreCajaActivity.this, "Advertencia", ex.getMessage());
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
                    mensaje.MensajeAdvertencia(CierreCajaActivity.this, "7.Advertencia", error.toString());
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(CierreCajaActivity.this, "8.Advertencia", "Servidor No Responde");
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(CierreCajaActivity.this, "Advertencia", msj.toString());
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

    private void imprimirCierreKR5(String host, Integer port){
        if (host.equals("")) {
            mensaje.MensajeAdvertencia(CierreCajaActivity.this, "Advertencia", "Dato host de la impresora vacio, no se puede imprimir");
            return;
        }
        if (String.valueOf(port).equals("")) {
            mensaje.MensajeAdvertencia(CierreCajaActivity.this, "Advertencia", "Datos puerto de la impresora vacio, no se puede imprimir");
            return;
        }
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try(TcpIpOutputStream stream = new TcpIpOutputStream(host, port)){
                    EscPos escPos = new EscPos(stream);

                    //Declaramos los estilos
                    Style title = new Style()
                            .setFontSize(Style.FontSize._2, Style.FontSize._2)
                            .setJustification(EscPosConst.Justification.Center);
                    Style subtitle = new Style(escPos.getStyle())
                            .setBold(true)
                            .setUnderline(Style.Underline.OneDotThick)
                            .setJustification(EscPosConst.Justification.Center);
                    Style bold = new Style(escPos.getStyle())
                            .setBold(true);
                    Style center = new Style()
                            .setJustification(EscPosConst.Justification.Center);
                    Style resolucion = new Style()
                            .setFontSize(Style.FontSize._1, Style.FontSize._1)
                            .setJustification(EscPosConst.Justification.Center);

                    //Declaramos variables
                    String fecha="";
                    int hora;
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
                    date = new Date();
                    fecha = dateFormat.getTimeInstance().format(date);
                    String[] parts = fecha.split(":");
                    if (parts[0].length()==1)
                        fecha = "0"+fecha;
                    hora = Integer.parseInt(fecha.substring(0, 2));

                    if(hora>12)
                        fecha =  "" + String.format("%2s",(hora-12)) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                    else if(hora==12)
                        fecha =  "" + (hora) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                    else
                        fecha =  dateFormat.getTimeInstance().format(date).substring(0, 5) + " am" ;

                    fecha = String.format("%s",dateFormat.getDateInstance().format(date) + " " + fecha);

                    //Impresion Cierre
                    escPos.writeLF(title, Global.g_NomEmp)
                            .writeLF(center, "Nit." + Global.g_Nit)
                            .writeLF(center, "----------------------------------")
                            .feed(1)
                            .writeLF(fecha)
                            .writeLF("Ubicacion: BANOS " + Global.g_NomUbica.substring(6) )
                            .write(bold, "Caja: " + Global.g_Caja)
                            .writeLF(" Codigo Usuario: " + Global.g_Usuario)
                            .writeLF("Usuario: " + Global.g_User.getUsuario())
                            .feed(2)
                            .writeLF("  CONCEPTO    |USOS|   IMP |   FACTs |   Total")
                            .writeLF(center,"----------------------------------------------");
                            //Imprimimos la grilla
                            int cont,total=0;
                            if (factBanos != null){
                                for (cont=0;cont < factBanos.length;cont++){
                                    String concepto = "";
                                    concepto = factBanos[cont].getRBCONCEPTO();
                                    if (concepto.equals("USO BAÑO"))
                                        concepto = "USO BANO ";

                                    escPos.write(String.format("%7s ", concepto))
                                            .write(String.format("%8s ", factBanos[cont].getRBCANTUSOS().toString()))
                                            .write(String.format("%8s ", factBanos[cont].getRBIMPFACT().toString()))
                                            .write(String.format("%7s ", factBanos[cont].getRBNUMFAC().toString()))
                                            .writeLF(String.format("%11s ", NumberFormat.getInstance().format(factBanos[cont].getRBTOTFAC()).toString()));
                                }
                            }
                    escPos.feed(2)
                            .writeLF(String.format("TOTAL FACTURAS          %20s", txtVTotalFact.getText().toString()))
                            .writeLF(String.format("TOTAL USOS              %20s", txtVTotalUsos.getText().toString()))
                            .writeLF(String.format("TOTAL IMPRESIONES       %20s", txtVTotalImp.getText().toString()))
                            .writeLF(String.format("SUBTOTAL VENTAS         %20s", NumberFormat.getInstance().format(subTotal)))
                            .writeLF(String.format("IVA VENTAS              %20s", NumberFormat.getInstance().format(totalIva)))
                            .writeLF(String.format("TOTAL VENTAS            %20s", NumberFormat.getInstance().format(TotalCaja)))
                            .feed(4)
                            .writeLF(center,"----------------------------------------------")
                            .writeLF("FIRMA TAQUILLERO")
                            .feed(1)
                            .writeLF(resolucion, "Desarrollado Consultores Tecnologicos S.A.S")
                            .feed(3)
                            .cut(EscPos.CutMode.FULL);
                    escPos.feed(1);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        cerrarCaja();
    }
    private void imprimirCierre() {

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
                                        CierreCajaActivity.this).getDeviceSpec("cloudpos.device.printer");


                                format = new Format();
                                format.setParameter("align", "center");
                                format.setParameter("bold", "true");
                                format.setParameter("size", "medium");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");

                                Bitmap bitmap = BitmapFactory.decodeResource(CierreCajaActivity.this.getResources(),
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

                                dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
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

                                //dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
                                //date = new Date();
                                //cadena = dateFormat.getTimeInstance().format(date);
                                //String[] parts = cadena.split(":");
                                //if (parts[0].length()==1)
                                //    cadena = "0"+cadena;
                                //hora = Integer.parseInt(cadena.substring(0, 2));

                                //if(hora>12)
                                //    cadena =  "" + String.format("%2s",(hora-12)) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                                //else if(hora==12)
                                //    cadena =  "" + (hora) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                                //else
                                //    cadena =  dateFormat.getTimeInstance().format(date).substring(0, 5) + " am" ;

                                //cadena = String.format("%s",dateFormat.getDateInstance().format(date) + " " + cadena);
                                //printerDevice.printText(format, cadena );

                                cadena = String.format("Ubicacion:%.24s",Global.g_NomUbica );
                                printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("Caja:%.6s Cod Usu:%.16s",Global.g_Caja,Global.g_Usuario);
                                printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("Usuario:%.26s",Global.g_User.getUsuario());
                                printerDevice.printText(format, cadena+" \n\n" );


                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "small");

                                printerDevice.printText(format, "  CONCEPTO  |USOS| IMP | FACTs |   Total  \n" );
                                printerDevice.printText(format, "------------------------------------------\n" );

                                int cont,total=0;

                                if (factBanos != null){

                                for (cont=0;cont < factBanos.length;cont++){

                                    cadena = String.format("%-12s ",factBanos[cont].getRBCONCEPTO());

                                    cadena = cadena + String.format("%4s ",factBanos[cont].getRBCANTUSOS().toString());

                                    cadena = cadena+String.format("%5s ",factBanos[cont].getRBIMPFACT().toString());

                                    cadena = cadena+String.format("%7s ",factBanos[cont].getRBNUMFAC().toString());

                                    cadena = cadena + String.format("%10s",NumberFormat.getInstance().format(factBanos[cont].getRBTOTFAC()).toString());

                                    printerDevice.printText(format, cadena );

                                }

                                }

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText(format, "\n" );

                                cadena = String.format("TOTAL FACTURAS       %11s",txtVTotalFact.getText().toString());
                                printerDevice.printText(format, cadena );

                                cadena = String.format("TOTAL USOS           %11s",txtVTotalUsos.getText().toString());
                                printerDevice.printText(format, cadena );

                                cadena = String.format("TOTAL IMPRESIONES    %11s",txtVTotalImp.getText().toString());
                                printerDevice.printText(format, cadena );

                                cadena = String.format("SUBTOTAL VENTAS         %8s",NumberFormat.getInstance().format(subTotal));
                                printerDevice.printText(format, cadena );

                                cadena = String.format("IVA VENTAS         %13s",NumberFormat.getInstance().format(totalIva));
                                printerDevice.printText(format, cadena );

                                cadena = String.format("TOTAL VENTAS         %11s",NumberFormat.getInstance().format(TotalCaja));
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
            cerrarCaja();
        }else if (banImp==1)
        {

            try {

                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(CierreCajaActivity.this, "Impresora sin papel", "Desea reimprimir la Ult?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            banImp=0;
                            Object view = null;
                            imprimirCierre();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(CierreCajaActivity.this, "Advertencia", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(CierreCajaActivity.this, "Advertencia", e.getMessage());
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

    public boolean writePreference(String key, String value){
        try {
            SharedPreferences prefs = getSharedPreferences("FacBan", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(CierreCajaActivity.this,"Advertencia",ex.getMessage() );
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
            mensaje.MensajeAdvertencia(CierreCajaActivity.this,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }
}
