package ctec.app_fac_banos;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import java.text.NumberFormat;
import java.util.ArrayList;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.DocGenerales;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.LibroViaje;
import ctec.app_fac_banos.Clases.Mensaje;
import ctec.app_fac_banos.Clases.Ruta;
import ctec.app_fac_banos.Clases.Servicio;
import ctec.app_fac_banos.Clases.Tiquete;
import ctec.app_fac_banos.ListView.DatoListView;
import ctec.app_fac_banos.ListView.ListViewAdapter;

public class BuscarActivity extends AppCompatActivity {

    String Dato;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    private ListView listVDatos;
    private EditText edTBuscar;
    ArrayList<DatoListView> arraylist = new ArrayList<DatoListView>();
    ListViewAdapter adapter;
    private Handler handler = new Handler();

    //ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        listVDatos = findViewById(R.id.listVDatos);
        edTBuscar = findViewById(R.id.edTBuscar);
        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        //setRepeatingVolleyTask();
        Global.g_PosBuscar=-1;
        try{
            if(Global.g_OpcBuscar == 1)
            {
                for(int cont=0;cont < Global.g_libros.size();cont ++){

                    LibroViaje libro = Global.g_libros.get(cont);

                    if (libro.getStrDescripcionRuta().length()> 20)
                        Dato = libro.getStrDescripcionRuta().substring(0,20);
                    else
                        Dato = libro.getStrDescripcionRuta();

                    DatoListView dLV = new DatoListView("MOVIL:" + libro.getStrMovil().substring(0,4) + "- RUTA:" + Dato +
                            " - FECHA:"+ libro.getDtmFechaSalida().substring(0,10) + " - HORA:" + libro.getStrHoraSalida().substring(0,5) +
                            " - DISP:" + libro.getIntPuestosDisponibles() + " DE :"+ libro.getIntPuestos()+ " - LIBRO:"+ libro.getIntIdLibro());
                    arraylist.add(dLV);
                }
            }

            if(Global.g_OpcBuscar == 2)
            {
                for(int cont=0;cont < Global.g_servicios.size();cont ++){

                    Servicio servicio = Global.g_servicios.get(cont);

                    if (servicio.getNomServicio().length()> 20)
                        Dato = servicio.getNomServicio().substring(0,20);
                    else
                        Dato = servicio.getNomServicio();

                    DatoListView dLV = new DatoListView("Nombre:" + Dato +
                            " - Tarifa:"+ NumberFormat.getInstance().format(servicio.getTarifa()) +
                            " - Codigo:" + servicio.getIdServicio());
                    arraylist.add(dLV);
                }
            }

            if(Global.g_OpcBuscar == 3)
            {
                for(int cont=0;cont < Global.g_rutas.size();cont ++){

                    Ruta ruta = Global.g_rutas.get(cont);

                    if (ruta.getnombreRuta().length()> 40)
                        Dato = ruta.getnombreRuta().substring(0,40);
                    else
                        Dato = ruta.getnombreRuta();

                    DatoListView dLV = new DatoListView("Nombre:" + Dato + " - Codigo:" + ruta.getidRuta());
                    arraylist.add(dLV);
                }
            }

            if(Global.g_OpcBuscar == 4)
            {
                for(int cont=0;cont < Global.g_tiquetes.size();cont ++){

                    Tiquete tiquete = Global.g_tiquetes.get(cont);

                    if (tiquete.getNombrePasajero().length()> 20)
                        Dato = tiquete.getNombrePasajero().substring(0,20);
                    else
                        Dato = tiquete.getNombrePasajero();

                    DatoListView dLV = new DatoListView("Pasajero:" + tiquete.getIdPasajero() + " - " + Dato +
                            " \n- Fecha:"+ tiquete.getFechaSalidaLibro().substring(0,10) + " - Hora:" + tiquete.getHora().substring(0,5) +
                            " \n- Ori:" + tiquete.getOrigen() + " - Des:" + tiquete.getDetino() + " " +
                            " \n-Numero de Reimpresiones :"+tiquete.getNumImpresiones() +
                            " \n-Libro:"+tiquete.getIdLibro() + " - Tiquete:" +tiquete.getIdTiquete() );
                    arraylist.add(dLV);
                }
            }

            if(Global.g_OpcBuscar == 5)
            {
                for(int cont=0;cont < Global.g_docGenerales.size();cont ++){

                    DocGenerales docGenerales = Global.g_docGenerales.get(cont);

                    if (docGenerales.getNombreComprobante().length()> 20)
                        Dato = docGenerales.getNombreComprobante().substring(0,20);
                    else
                        Dato = docGenerales.getNombreComprobante();

                    DatoListView dLV = new DatoListView("Cod:" + docGenerales.getIdComprobante() +
                            " - Nombre:" + Dato  );
                    arraylist.add(dLV);
                }
            }

            //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Datos);
            adapter = new ListViewAdapter(this, arraylist);
            listVDatos.setAdapter(adapter);

        }catch(Exception e)
        {
            String informacion = e.getMessage();
        }


        edTBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = edTBuscar.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });

        listVDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {

                int intDato=0;
                String[] strDato;
                if(Global.g_OpcBuscar == 1)
                {

                    strDato = adapter.getItem(pos).getDato().split("LIBRO:");
                    intDato = Integer.parseInt(strDato[1]);
                    for(int cont=0;cont < Global.g_libros.size();cont ++){

                        LibroViaje libro = Global.g_libros.get(cont);

                        if (libro.getIntIdLibro()==intDato) {
                            Global.g_PosBuscar = cont;
                            break;
                        }
                    }
                    finish();
                }

                if(Global.g_OpcBuscar == 2)
                {
                    strDato = adapter.getItem(pos).getDato().split("Codigo:");
                    intDato = Integer.parseInt(strDato[1]);
                    for(int cont=0;cont < Global.g_servicios.size();cont ++){

                        Servicio servicio = Global.g_servicios.get(cont);

                        if (servicio.getIdServicio().equals(String.valueOf(intDato))) {
                            Global.g_PosBuscar = cont;
                            break;
                        }
                    }
                    finish();
                }

                if(Global.g_OpcBuscar == 3)
                {
                    strDato = adapter.getItem(pos).getDato().split("Codigo:");
                    intDato = Integer.parseInt(strDato[1]);
                    for(int cont=0;cont < Global.g_rutas.size();cont ++){

                        Ruta ruta = Global.g_rutas.get(cont);

                        if (ruta.getidRuta() == intDato) {
                            Global.g_PosBuscar = cont;
                            break;
                        }
                    }
                    finish();
                }

                if(Global.g_OpcBuscar == 4)
                {
                    sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(BuscarActivity.this, "ReImprimir Tiquete", "Desea reimprimir este Tiq uete?");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();

                            try {
                                int intDato=0;
                                String[] strDato;
                                strDato = adapter.getItem(pos).getDato().split("Tiquete:");
                                intDato = Integer.parseInt(strDato[1]);
                                for(int cont=0;cont < Global.g_tiquetes.size();cont ++){

                                    Tiquete tiquete = Global.g_tiquetes.get(cont);

                                    if (tiquete.getIdTiquete() == intDato) {
                                        if (tiquete.getNumImpresiones()<3) {
                                            Global.g_PosBuscar = cont;
                                            finish();
                                            break;
                                        }
                                        else{
                                            Global.g_PosBuscar =-2;
                                            finish();
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mensaje.MensajeAdvertencia(BuscarActivity.this, "Advertencia", e.getMessage());
                            }

                        }
                    });
                    sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();

                            try {
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                mensaje.MensajeAdvertencia(BuscarActivity.this, "Advertencia", e.getMessage());
                            }

                        }
                    });
                    sweetAlertDialog.show();


                }

                if(Global.g_OpcBuscar == 5)
                {
                    strDato = adapter.getItem(pos).getDato().split("Cod:");
                    strDato =  strDato[1].split("-");
                    for(int cont=0;cont < Global.g_docGenerales.size();cont ++){

                        DocGenerales docGenerales = Global.g_docGenerales.get(cont);

                        if (docGenerales.getIdComprobante().equals(strDato[0].trim())) {
                            Global.g_PosBuscar = cont;
                            break;
                        }
                    }
                    finish();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //Genera un timer cada x segundos
    /*private void setRepeatingVolleyTask() {

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        // What are you trying to catch here??
                        try {
                            if (Global.g_PosBuscar >=0) {
                                finish();
                            }
                        }
                        catch ( Exception e ) {
                            Log.e("ERR", e.toString());
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 2000);  // interval of one minute

    }*/


}
