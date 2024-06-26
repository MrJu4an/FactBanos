package ctec.app_fac_banos;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.printservice.PrintService;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.github.anastaciocintra.escpos.barcode.BarCode;
import com.github.anastaciocintra.escpos.barcode.PDF417;
import com.github.anastaciocintra.escpos.barcode.QRCode;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.github.anastaciocintra.output.TcpIpOutputStream;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.Gedetsuptip;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;


public class FacBanosActivity extends AppCompatActivity {

    EditText edTCant,edTCedula,edTNombres,edTEmail, edTCelular;
    TextView txtVCaja,txtVuser,txtVValor,txtVIva,txtVTotal,txtVFecha;
    Button btnGrabar;
    CheckBox chkBoxImprimir;
    Spinner spnConcept;

    int banImp=0;
    Date date;
    SimpleDateFormat dateFormat;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    private PrinterDevice printerDevice;
    private Format format;


    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_banos);

        ArrayList<String> listaConcept = new ArrayList<>();
        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        edTCedula = findViewById(R.id.edTCedula);
        edTNombres = findViewById(R.id.edTNombres);
        edTEmail = findViewById(R.id.edTEmail);
        edTCelular = findViewById(R.id.edTCelular);
        edTCant = findViewById(R.id.edTCant);
        txtVCaja = findViewById(R.id.txtVUsuario);
        txtVuser = findViewById(R.id.txtVuser);
        spnConcept = findViewById(R.id.spnConcept);
        txtVValor = findViewById(R.id.txtVValor);
        txtVIva = findViewById(R.id.txtVIva);
        txtVTotal = findViewById(R.id.txtVTotal);
        txtVFecha = findViewById(R.id.txtVFecha);
        btnGrabar = findViewById(R.id.btnGrabar);
        chkBoxImprimir = findViewById(R.id.chkBoxImprimir);


        txtVuser.setText(Global.g_User.getUsuario());
        txtVCaja.setText(readPreference("Caja"));
        chkBoxImprimir.setChecked(true);
        chkBoxImprimir.setEnabled(false);
        edTCedula.setEnabled(true);
        edTNombres.setEnabled(true);
        edTCedula.setText("");
        edTNombres.setText("");
        edTEmail.setText("");
        edTCelular.setText("");
        edTCant.setText("1");

        listaConcept = new ArrayList<String>();

        for (Gedetsuptip detalle : Global.detalles) {
            listaConcept.add(detalle.getCodDetalle());
        }

        Global.g_ValorIva = Integer.parseInt(Global.detalles[0].getValor());
        Global.g_ValorConcep = Integer.parseInt(Global.detalles[0].getDescripcion());
        Global.g_NomConcepto = Global.detalles[0].getCodDetalle();

        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(this,android.R.layout.simple_spinner_item,listaConcept);
        spnConcept.setAdapter(adaptador);


        obtenerFecha();

        edTCant.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                try {
                    if (edTCant.length()>=1 && !edTCant.getText().equals("0")) {
                        calcularValores();
                        /*
                        txtVValor.setText( NumberFormat.getInstance().format(Global.g_ValorConcep * Double.parseDouble(edTCant.getText().toString())));
                        txtVIva.setText( NumberFormat.getInstance().format(Global.g_ValorIva * Double.parseDouble(edTCant.getText().toString())));
                        txtVTotal.setText(NumberFormat.getInstance().format((Global.g_ValorConcep * Double.parseDouble(edTCant.getText().toString()))+(Global.g_ValorIva * Double.parseDouble(edTCant.getText().toString()))));*/
                    }
                    else
                    {
                        txtVValor.setText( "0");
                        txtVIva.setText( "0" );
                        txtVTotal.setText("0");
                    }
                }
                catch(Exception e){
                    mensaje.MensajeAdvertencia(FacBanosActivity.this,"Advertencia", e.getMessage());
                    txtVValor.setText( "0");
                    txtVIva.setText( "0" );
                    txtVTotal.setText("0");
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });
/*
        chkBoxImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkBoxImprimir.isChecked()){
                    edTCedula.setEnabled(true);
                    edTNombres.setEnabled(true);
                }
                else{
                    edTCedula.setEnabled(false);
                    edTNombres.setEnabled(false);
                    edTCedula.setText("");
                    edTNombres.setText("");
                }
            }
        });*/

        spnConcept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Global.g_ValorIva = Integer.parseInt(Global.detalles[position].getValor());
                    Global.g_ValorConcep = Integer.parseInt(Global.detalles[position ].getDescripcion());
                    Global.g_NomConcepto = Global.detalles[position].getCodDetalle();
                if (edTCant.length()>0 && !edTCant.getText().equals("0") && !edTCant.getText().equals("")) {
                    calcularValores();
                    /*txtVValor.setText( NumberFormat.getInstance().format(Global.g_ValorConcep * Double.parseDouble(edTCant.getText().toString())));
                    txtVIva.setText( NumberFormat.getInstance().format(Global.g_ValorIva * Double.parseDouble(edTCant.getText().toString())));
                    txtVTotal.setText(NumberFormat.getInstance().format((Global.g_ValorConcep * Double.parseDouble(edTCant.getText().toString()))+(Global.g_ValorIva * Double.parseDouble(edTCant.getText().toString()))));*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarFactura();
            }
        });


    }

    protected void onResume(){
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void obtenerFecha(){
        /*DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                final int mesActual = month + 1;

                String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);

                txtVFecha.setText("Fecha:" + diaFormateado + '/' + mesFormateado + '/' + year);

            }
        },anio, mes, dia);

        recogerFecha.show();*/

        dateFormat = new SimpleDateFormat("dd/MM/yyyy",  new Locale("es","CO"));
        date = new Date();

        txtVFecha.setText("Fecha:" + dateFormat.getDateInstance().format(date));

    }

    private Date convertDate(String date,String Format){
        Date dateResult = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(Format);
            String dateInString = date;
            dateResult = formatter.parse(dateInString);

        } catch (ParseException ex) {

            sweetAlertDialog.dismiss();
            mensaje.MensajeError(FacBanosActivity.this, "Advertencia", "convertDate: " +ex.getMessage());
        }
        return  dateResult;
    }

    private void grabarFactura(){
        try{
            String imp = "",cedula ="",nombre="",email="",celular="";
            if (edTCant.getText().toString().equals("")){
                mensaje.MensajeAdvertencia(FacBanosActivity.this,"ADVERTENCIA","Debe ingresar la cantidad de Usos!!!");
                return;
            }

            if ( Integer.parseInt(edTCant.getText().toString()) == 0 ) {
                mensaje.MensajeAdvertencia(FacBanosActivity.this,"ADVERTENCIA","Debe ingresar la cantidad de Usos!!!");
                return;
            }

//            if (chkBoxImprimir.isChecked() && (edTCedula.getText().equals("") || edTCedula.getText().equals(""))){
//                mensaje.MensajeAdvertencia(FacBanosActivity.this,"ADVERTENCIA","Debe ingresar la cedula y el nombre del cliente!!!");
//                return;
//            }

            dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
            date = new Date();

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String URL = Global.g_DirecApi + "/api/FactBanos/PostGrabarFact";

            //Datos impresora
            String host = Global.g_HostImp;
            Integer port = Integer.parseInt(Global.g_portImp);

            sweetAlertDialog = mensaje.progreso(FacBanosActivity.this,"Grabando Tiquetes");
            sweetAlertDialog.show();

            if (chkBoxImprimir.isChecked())
                imp= "1";
            else
                imp = "0";

            if (edTCedula.getText().toString().equals("")){
                cedula= "222222222222";
                edTCedula.setText("222222222222");
            }
            else{
                cedula= edTCedula.getText().toString();
            }

            if (edTNombres.getText().toString().equals("")){
                nombre= "CONSUMIDOR FINAL";
                edTNombres.setText("CONSUMIDOR FINAL");
            }
            else {
                nombre= edTNombres.getText().toString();
            }

            if (edTEmail.getText().toString().equals("")){
                email= "cliente@sincorreo.com";
                edTEmail.setText("cliente@sincorreo.com");
            }
            else {
                email = edTEmail.getText().toString();
            }

            if (edTCelular.getText().toString().equals("")){
                celular = "0";
                edTCelular.setText("0");
            } else {
                celular = edTCelular.getText().toString();
            }


            JSONObject jsonBody = new JSONObject();

            jsonBody.put("RBRESFAC",Global.g_Resolucion.getFRNUMRES() );
            jsonBody.put("RBCAJA",Global.g_Caja);
            jsonBody.put("RBCODUSU",Global.g_Usuario);
            jsonBody.put("RBNOMUSU",Global.g_User.getUsuario());
            jsonBody.put("RBFECHA",dateFormat.format(date));
            jsonBody.put("RBNUMMAQ",Global.g_Serial);
            jsonBody.put("RBNOMUBI",Global.g_NomUbica);
            jsonBody.put("RBCONCEPTO",Global.g_NomConcepto);
            jsonBody.put("RBCANTUSOS",edTCant.getText().toString().replace(".",""));

            String vlr = txtVValor.getText().toString().replace(".","");
            float edtValor = Float.parseFloat(vlr.replace(",","."));
            jsonBody.put("RBVALOR", String.valueOf(edtValor));
            String vIva = txtVIva.getText().toString().replace(".","");
            float edtIva = Float.parseFloat(vIva.replace(",","."));
            jsonBody.put("RBIVA",String.valueOf( edtIva));
            jsonBody.put("RBTOTFAC",txtVTotal.getText().toString().replace(".",""));
            jsonBody.put("RBCEDCLIENTE",cedula);
            jsonBody.put("RBNOMCLIENTE",nombre);
            jsonBody.put("RBCORCLIENTE",email);
            jsonBody.put("RBCELCLIENTE",celular);
            jsonBody.put("RBIMPFACT",imp );
            jsonBody.put("RBPREFACT",Global.g_Resolucion.getFRPRERES());
            jsonBody.put("RBSESION",Global.g_User.getSesion());

            final String requestBody = jsonBody.toString();

            //Por problemas de impresión, verificamos si los datos de la impresora no sean nulos
            //Y si no lo son, continuamos con el proceso normal
            if (host.equals("")) {
                mensaje.MensajeAdvertencia(FacBanosActivity.this, "Advertencia", "Dato host de la impresora vacio, no se puede imprimir");
                return;
            }
            if (String.valueOf(port).equals("")) {
                mensaje.MensajeAdvertencia(FacBanosActivity.this, "Advertencia", "Datos puerto de la impresora vacio, no se puede imprimir");
                return;
            }

            StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                String result[] = new Gson().fromJson(response, String.class).split(";");
                                sweetAlertDialog.dismiss();
                                if (result[0].equals("OK")){
                                    Global.g_Resolucion.setFRNUMFAC(Double.parseDouble(result[1].toString()));
                                    Global.g_NumFacFin = Double.parseDouble(result[1].toString());
                                    if (chkBoxImprimir.isChecked()) {
                                        if (edTCedula.getText().toString().equals("222222222222")){
                                            if (Global.g_Dispositivo.equals("P")){
                                                imprimirFacKR5(host, port);
                                            } else if (Global.g_Dispositivo.equals("K")){
                                                imprimirFac();
                                            }
                                        } else {
                                            limpiar();
                                        }
                                    }
                                    else {
                                        limpiar();
                                    }
                                     mensaje.MensajeExitoso(FacBanosActivity.this, "Exitoso" , "factura grabada Exitosamente!!!");
                                    //limpiar();
                                }
                                else {
                                    mensaje.MensajeAdvertencia(FacBanosActivity.this, "Advertencia" ,
                                            "Inconvenientes en la Transacción:\n"+ result[1]);
                                }
                            } catch (Exception e) {
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(FacBanosActivity.this, "2.Advertencia" , e.getMessage());
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
            postRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(FacBanosActivity.this).add(postRequest);

        }
        catch(Exception ex){
            sweetAlertDialog.dismiss();
            mensaje.MensajeError(FacBanosActivity.this, "Advertencia", ex.getMessage());
        }
    }

    /*
    Jorge Sabogal
    11/Nov/2021
    Se crea funcion para calcular el iva al seleccionar el concepto.
    */
    public void calcularValores(){
        try{

            double subtotal = Global.g_ValorConcep / 1.19;
            double vSub = subtotal * Integer.parseInt(edTCant.getText().toString());
            int valSubtotal = (int) Math.round(vSub) ;
            //double vIva = valSubtotal *0.19;
            int ivaPrueba = Global.g_ValorIva;
            double vIva = (Global.g_ValorConcep * Integer.parseInt(edTCant.getText().toString())) - valSubtotal;
            int Iva = (int) Math.round(vIva);

            int vTotal = (valSubtotal + Iva);

            txtVValor.setText(String.valueOf(valSubtotal));
            txtVIva.setText( String.valueOf(Iva));
            txtVTotal.setText(String.valueOf(vTotal));

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //Sebastian Rondón - 21 Dic 2022
    //Impresión dispositivo DIG-KR5
    public void imprimirFacKR5(String host, Integer port){
        if (host.equals("")) {
            mensaje.MensajeAdvertencia(FacBanosActivity.this, "Advertencia", "Dato host de la impresora vacio, no se puede imprimir");
            return;
        }
        if (String.valueOf(port).equals("")) {
            mensaje.MensajeAdvertencia(FacBanosActivity.this, "Advertencia", "Datos puerto de la impresora vacio, no se puede imprimir");
            return;
        }

        int valor = Integer.parseInt(txtVValor.getText().toString());
        int iva = Integer.parseInt(txtVIva.getText().toString());
        int total = Integer.parseInt(txtVTotal.getText().toString());

        String cufe = crearCUFE();
        QRCode qrCode = new QRCode();
        qrCode.setSize(7);
        qrCode.setJustification(EscPosConst.Justification.Center);
        String ivaText = "IVA 19%";

        //handler.post(myRunnable);
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

                    //int hora;

                    //String[] parts = fecha.split(":");
                    //if (parts[0].length()==1)
                        //fecha = "0"+fecha;
                    //hora = Integer.parseInt(fecha.substring(0, 2));

                    //if(hora>12)
                        //fecha =  "" + String.format("%2s",(hora-12)) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                    //else if(hora==12)
                        //fecha =  "" + (hora) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 5) + " pm" ;
                    //else
                    String fechaGeneracion="";
                    String horaGeneracion="";
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
                    horaGeneracion = dateFormat.getTimeInstance().format(date);
                    //hora =  dateFormat.getTimeInstance().format(date).substring(0, 5);
                    fechaGeneracion = String.format("%s",dateFormat.getDateInstance().format(date) + " " + horaGeneracion);

                    String fechaExpedicion="";
                    String horaExpedicion="";
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, 10);
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
                    horaExpedicion = dateFormat.format(calendar.getTime());
                    fechaExpedicion = String.format("%s", horaExpedicion);
                    String concepto = "";
                    concepto = String.format("%s",Global.g_NomConcepto);
                    /*if(concepto.length() > 17)
                        concepto = concepto.substring(1, 17);
                    else
                        concepto = String.format("%-17s", concepto);
*/
                    if (concepto.equals("USO BAÑO"))
                        concepto = "USO BANO";

                    DecimalFormat formato = new DecimalFormat("######");

                    //Impresion Factura
                    if (Global.g_NomEmp.toUpperCase().contains("PEREIRA")){
                        //escPos.writeLF(title, Global.g_NomEmp)
                        escPos.writeLF(subtitle, Global.g_NomEmp)
                                .writeLF(center, "Nit." + Global.g_Nit)
                                .writeLF(center, "Agente Retenedor de IVA")
                                .writeLF(center, "----------------------------------")
                                //.writeLF(subtitle, "COMPROBANTE DE RECAUDO")
                                .writeLF(center, "FACTURA ELECTRONICA DE VENTA")
                                .writeLF(center, Global.g_Resolucion.getFRPRERES() + " - " + formato.format(Global.g_Resolucion.getFRNUMFAC()))
                                .writeLF(resolucion, "Res. DIAN " + Global.g_Resolucion.getFRNUMRES())
                                .writeLF(resolucion, "RANGO DEL "+ NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMINI())
                                        +" AL " +  NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFIN()))
                                .writeLF(resolucion, "Mayo 20 de 2024  -  Vigencia de 24 Meses")
                                .writeLF(subtitle, "REGIMEN COMUN")
                                .feed(1)
                                .writeLF("FECHA DE GENERACION: " + fechaGeneracion)
                                .writeLF("FECHA DE EXPEDICION: " + fechaExpedicion)
                                //.writeLF("FACT. VENTA POS No: " + Global.g_Resolucion.getFRPRERES() + " - " + NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFAC()))
                                .writeLF("FORMA DE PAGO: CONTADO - EFECTIVO")
                                //.writeLF("COMPROBANTE DE RECAUDO No: " + Global.g_Resolucion.getFRPRERES() + "" + NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFAC()))
                                .writeLF("Ubicacion: BANOS " + Global.g_NomUbica.substring(6) )
                                .writeLF("Usuario: " + Global.g_User.getUsuario())
                                .feed(1)
                                .writeLF("CUFE: " + cufe)
                                //.write(bold, "Caja: " + Global.g_Caja)
                                //.writeLF(" Codigo Usuario: " + Global.g_Usuario)
                                .feed(2)
                                .write(qrCode, "https://catalogo-vpfe-hab.dian.gov.co/document/searchqr?documentkey=" + cufe)
                                .feed(2)
                                //.writeLF(subtitle, "CLIENTE")
                                //.feed(1)
                                .writeLF("Cedula Cliente: " + edTCedula.getText().toString())
                                .writeLF("Nombre Cliente: " + edTNombres.getText().toString())
                                .writeLF("Correo Cliente: " + edTEmail.getText().toString())
                                .writeLF("Celular Cliente: " + edTCelular.getText().toString())
                                .feed(1)
                                .writeLF("   CONCEPTO        |CT|   VALOR C/U |   TOTAL")
                                .writeLF(center,"----------------------------------------------")
                                .write(String.format(" %-1s", concepto))
                                .write(String.format(" %11s", edTCant.getText()))
                                .write(String.format(" %11s", NumberFormat.getInstance().format(Global.g_ValorConcep)))
                                .writeLF(String.format(" %9s", txtVTotal.getText()))
                                .feed(1)
                                .writeLF("TOTAL ITEMS: " + edTCant.getText())
                                .feed(1)
                                .writeLF(String.format("SUBTOTAL       %28s", valor))
                                .writeLF(String.format("%-20s          %18s", ivaText,iva))
                                .writeLF(String.format("TOTAL          %28s", total))
                                //.feed(1)
                                .writeLF(center,"----------------------------------------------")
                                .writeLF(resolucion, "Fabricante de Software:")
                                .writeLF(resolucion, "Consultores Tecnologicos S.A.S")
                                .writeLF(resolucion, "Nit 809.007.347-7")
                                .writeLF(resolucion, "Software FETickets")
                                .feed(3)
                                .cut(EscPos.CutMode.FULL);
                        //escPos.feed(1);

                    } else{

                        String ubicacion = "BANO " + Global.g_Caja.trim().charAt(1);
                        //escPos.writeLF(title, Global.g_NomEmp)
                        escPos.writeLF(subtitle, Global.g_NomEmp)
                                .writeLF(center, "Nit." + Global.g_Nit)
                                .writeLF(center, Global.g_DirEmp)
                                .writeLF(center, "Agente Retenedor de IVA")
                                .writeLF(center, "----------------------------------")
                                .writeLF(resolucion, "Res. DIAN " + Global.g_Resolucion.getFRNUMRES())
                                .writeLF(resolucion, "RANGO DEL "+ NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMINI())
                                        +" AL " +  NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFIN()))
                                .writeLF(resolucion, "Fecha resol. " + Global.g_Resolucion.getFRFECEMI())
                                .writeLF(subtitle, "REGIMEN COMUN")
                                .feed(1)
                                .writeLF(fechaGeneracion)
                                .writeLF("FACT. VENTA POS No: " + Global.g_Resolucion.getFRPRERES() + "" + NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFAC()))
                                .writeLF("Ubicacion: " + ubicacion )
                                .write(bold, "Maquina: " + Global.g_Caja)
                                .writeLF(" Serial: " + Global.g_Serial)
                                //.writeLF("Usuario: " + Global.g_User.getUsuario())
                                .feed(1)
                                //.writeLF(subtitle, "CLIENTE")
                                //.feed(1)
                                .writeLF("Cedula Cliente: " + edTCedula.getText().toString())
                                .writeLF("Nombre Cliente: " + edTNombres.getText().toString())
                                .feed(1)
                                .writeLF("   CONCEPTO        |CT|   VALOR C/U |   TOTAL")
                                .writeLF(center,"----------------------------------------------")
                                .write(String.format(" %-1s", concepto))
                                .write(String.format(" %11s", edTCant.getText()))
                                .write(String.format(" %11s", NumberFormat.getInstance().format(Global.g_ValorConcep)))
                                .writeLF(String.format(" %9s", txtVTotal.getText()))
                                .feed(1)
                                .writeLF(String.format("SUBTOTAL       %28s", txtVValor.getText()))
                                .writeLF(String.format("IVA            %28s", txtVIva.getText()))
                                .writeLF(String.format("TOTAL          %28s", txtVTotal.getText()))
                                //.feed(1)
                                .writeLF(center,"----------------------------------------------")
                                .writeLF(resolucion, "Fabricante de Software:")
                                .writeLF(resolucion, "Consultores Tecnológicos S.A.S")
                                .writeLF(resolucion, "Nit 809.007.347-7")
                                .writeLF(resolucion, "Software FETickets")
                                .feed(2)
                                .cut(EscPos.CutMode.FULL);
                        //escPos.feed(1);
                    }

                    banImp =1;
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /*Inicio Funciones para la impresionLibro*/
    public void imprimirFac() {
        String cufe = crearCUFE();
        Bitmap QR = generarCodigoQR("https://catalogo-vpfe-hab.dian.gov.co/document/searchqr?documentkey=" + cufe);
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

            int valor = Integer.parseInt(txtVValor.getText().toString());
            int iva = Integer.parseInt(txtVIva.getText().toString());
            int total = Integer.parseInt(txtVTotal.getText().toString());

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
                                int hora;
                                PrinterDeviceSpec printerDeviceSpec = (PrinterDeviceSpec) POSTerminal.getInstance(
                                        FacBanosActivity.this).getDeviceSpec("cloudpos.device.printer");


                                format = new Format();
                                format.setParameter("align", "center");
                                format.setParameter("bold", "true");
                                format.setParameter("size", "medium");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                //printerDevice.printText(format, "SISTEMA POS" );
                                Bitmap bitmap = BitmapFactory.decodeResource(FacBanosActivity.this.getResources(),
                                        R.drawable.logoterminalpereira);
                                format.clear();
                                format.setParameter("align", "center");
                                //printerDevice.printBitmap(format,bitmap);
                                printerDevice.printText(format, Global.g_NomEmp );
                                printerDevice.printText(format, "Nit." + Global.g_Nit );
                                printerDevice.printText(format, "Agente Retenedor de IVA");
                                printerDevice.printText(format, "--------------------------------" );

                                printerDevice.printText("\n");
                                format.clear();

                                format.setParameter("align", "center");
                                format.setParameter("bold", "true");
                                format.setParameter("size", "medium");
                                DecimalFormat formato = new DecimalFormat("######");
                                printerDevice.printText(format, "FACTURA ELECTRONICA DE VENTA");
                                printerDevice.printText(format, Global.g_Resolucion.getFRPRERES() + " - " + formato.format(Global.g_Resolucion.getFRNUMFAC()));
                                //printerDevice.printText(format, "COMPROBANTE DE RECAUDO");

                                format.setParameter("align", "center");
                                format.setParameter("size", "small");
                                printerDevice.printText(format, "Res. DIAN " + Global.g_Resolucion.getFRNUMRES());
                                printerDevice.printText(format, "RANGO DEL "+ NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMINI())
                                                                    +" AL " +  NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFIN()));
                                printerDevice.printText(format, "Mayo 20 de 2024  -  Vigencia de 24 Meses");
                                printerDevice.printText(format,"REGIMEN COMUN");

                                //cadena = String.format("COMPROBANTE DE RECAUDO");
                                //printerDevice.printText(format, cadena);

                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                format.clear();

                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");

                                //cadena = String.format("No:%.24s",Global.g_Resolucion.getFRPRERES() + "" + NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFAC()));
                                //cadena = String.format("No:%.24s", NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFAC()));
                                //printerDevice.printText(format, cadena);

                                //Verifica que exita papel
                                if (printerDevice.queryStatus() == 0)
                                    banImp = 1;

                                dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
                                cadena = dateFormat.getTimeInstance().format(date);
                                String[] parts = cadena.split(":");
                                if (parts[0].length()==1)
                                    cadena = "0"+cadena;
                                hora = Integer.parseInt(cadena.substring(0, 2));

                                if(hora>12)
                                    cadena =  "" + String.format("%2s",(hora-12)) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 8) + " pm" ;
                                else if(hora==12)
                                    cadena =  "" + (hora) +":"+ dateFormat.getTimeInstance().format(date).substring(3, 8) + " pm" ;
                                else
                                    cadena =  dateFormat.getTimeInstance().format(date).substring(0, 7) + " am" ;

                                cadena = String.format("FECHA DE GENERACION: \n %24s",dateFormat.getDateInstance().format(date) + " " + cadena);
                                printerDevice.printText(format, cadena );

                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.SECOND, 10);
                                dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
                                cadena = dateFormat.getTimeInstance().format(calendar.getTime());
                                parts = cadena.split(":");
                                if (parts[0].length()==1)
                                    cadena = "0"+cadena;
                                hora = Integer.parseInt(cadena.substring(0, 2));

                                if(hora>12)
                                    cadena =  "" + String.format("%2s",(hora-12)) +":"+ dateFormat.getTimeInstance().format(calendar.getTime()).substring(3, 8) + " pm" ;
                                else if(hora==12)
                                    cadena =  "" + (hora) +":"+ dateFormat.getTimeInstance().format(calendar.getTime()).substring(3, 8) + " pm" ;
                                else
                                    cadena =  dateFormat.getTimeInstance().format(calendar.getTime()).substring(0, 7) + " am" ;

                                cadena = String.format("FECHA DE EXPEDICION: \n %24s",dateFormat.getDateInstance().format(calendar.getTime()) + " " + cadena);
                                printerDevice.printText(format, cadena );

                                //cadena = String.format("FACT. VENTA No:%.24s",Global.g_Resolucion.getFRPRERES() + " - " + NumberFormat.getInstance().format(Global.g_Resolucion.getFRNUMFAC()));
                                //printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("FORMA PAGO: CONTADO - EFECTIVO");
                                printerDevice.printText(format, cadena+" \n");

                                cadena = String.format("Ubicacion:%.24s",Global.g_NomUbica );
                                printerDevice.printText(format, cadena+" \n" );

                                //cadena = String.format("Caja:%.6s Cod Usu:%.16s",Global.g_Caja,Global.g_Usuario);
                                //printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("Caja:%.6s",Global.g_Caja);
                                printerDevice.printText(format, cadena+" \n" );

                                cadena = String.format("Usuario:%.26s",Global.g_User.getUsuario());
                                printerDevice.printText(format, cadena+" \n\n" );

                                cadena = String.format("CUFE:%50s", cufe);
                                printerDevice.printText(format, cadena);

                                printerDevice.printBitmap(format, QR);

                                if (!edTCedula.getText().toString().equals("")) {
                                    format.setParameter("align", "center");
                                    format.setParameter("bold", "true");
                                    format.setParameter("size", "medium");
                                    printerDevice.printText(format, "CLIENTE" );
                                    printerDevice.printText("\n");
                                    printerDevice.printText("\n");

                                    format.setParameter("align", "left");
                                    format.setParameter("size", "medium");

                                    cadena = String.format("Cedula:%.11s", edTCedula.getText().toString());
                                    printerDevice.printText(format, cadena + " \n");
                                    cadena = String.format("Nombre:%.30s", edTNombres.getText().toString());
                                    printerDevice.printText(format, cadena + " \n");
                                    cadena = String.format("Correo:%.30s", edTEmail.getText().toString());
                                    printerDevice.printText(format, cadena + " \n");
                                    cadena = String.format("Celular:%.30s", edTCelular.getText().toString());
                                    printerDevice.printText(format, cadena + " \n\n");
                                }

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "small");

                                printerDevice.printText(format, "     CONCEPTO    |Ct| VALOR C/U |  TOTAL  \n" );
                                printerDevice.printText(format, "------------------------------------------\n" );

                                cadena = String.format("%s",Global.g_NomConcepto);

                                if (cadena.length() >17)
                                    cadena = cadena.substring(1,17);
                                else
                                    cadena = String.format("%-17s", cadena);

                                cadena = cadena + String.format(" %2s",edTCant.getText());

                                cadena = cadena + String.format(" %11s",NumberFormat.getInstance().format(Global.g_ValorConcep));

                                cadena = cadena + String.format(" %8s",txtVTotal.getText());

                                printerDevice.printText(format, cadena+" \n\n" );

                                cadena = String.format("TOTAL ITEMS: %2s", edTCant.getText());
                                printerDevice.printText(format, cadena+" \n\n");

                                format.clear();
                                format.setParameter("align", "left");
                                format.setParameter("size", "medium");
                                printerDevice.printText("\n");

                                cadena = String.format("SUBTOTAL             %11s",valor);
                                printerDevice.printText(format, cadena );

                                String ivaText = "IVA 19%";
                                cadena = String.format("%-20s %11s",ivaText, iva);
                                printerDevice.printText(format, cadena );

                                cadena = String.format("TOTAL                %11s",total);
                                printerDevice.printText(format, cadena );

                                printerDevice.printText(format, "\n" );

                                printerDevice.printText(format, "--------------------------------\n" );
                                format.clear();
                                format.setParameter("align", "center");
                                format.setParameter("size", "extra-small");
                                printerDevice.printText(format, "Fabricante de Software:");
                                printerDevice.printText(format, "Consultores Tecnologicos S.A.S \n");
                                printerDevice.printText(format, "Nit 809.007.347-7 \n");
                                printerDevice.printText(format, "Software FETickets \n" );

                                //Verifica que exita papel
                                if (printerDevice.queryStatus() == 0)
                                    banImp = 1;


                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
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

        if(banImp==0){
            sweetAlertDialog.dismiss();
            //mensaje.MensajeExitoso(FacBanosActivity.this, "IMPRESION", "Impresion Exitosa");
            limpiar ();
        }else if (banImp==1)
        {
            try {

                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(FacBanosActivity.this, "Impresora sin papel", "Desea reimprimir la Ult?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            banImp=0;
                            imprimirFac();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(FacBanosActivity.this, "Advertencia", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(FacBanosActivity.this, "Advertencia", e.getMessage());

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
                    mensaje.MensajeAdvertencia(FacBanosActivity.this, "7.Advertencia", error.toString());
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(FacBanosActivity.this, "8.Advertencia", "Servidor No Responde");
                    //startCounter = false;
                    //counter.onFinish();
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(FacBanosActivity.this, "Advertencia", msj.toString());
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

    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = getSharedPreferences("FacBan", MODE_PRIVATE);

            valor = prefs.getString(key, "");
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(FacBanosActivity.this,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }

    private String crearCUFE()
    {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";

        // Objeto Random para generar números aleatorios
        Random random = new Random();

        // Crear un StringBuilder para construir el string alfanumérico
        StringBuilder sb = new StringBuilder();

        // Generar el string alfanumérico
        for (int i = 0; i < 90; i++) {
            // Generar un índice aleatorio entre 0 y el tamaño de la cadena de caracteres
            int index = random.nextInt(characters.length());
            // Obtener el carácter correspondiente al índice aleatorio y agregarlo al StringBuilder
            sb.append(characters.charAt(index));
        }

        // Convertir el StringBuilder a String y mostrar el resultado
        String alphanumeric = sb.toString();

        return alphanumeric;
    }

    private Bitmap generarCodigoQR(String texto) {
        try {
            // Configurar los parámetros para generar el código QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, 300, 300);

            // Convertir la matriz de bits en un bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void limpiar (){

        edTCant.setText("1");
        txtVValor.setText("0");
        txtVIva.setText( "0" );
        txtVTotal.setText("0");
        chkBoxImprimir.setChecked(true);
        chkBoxImprimir.setEnabled(false);
        edTCedula.setEnabled(true);
        edTNombres.setEnabled(true);
        edTCedula.setText("");
        edTNombres.setText("");
        edTEmail.setText("");
        edTCelular.setText("");
        calcularValores();
    }
}
