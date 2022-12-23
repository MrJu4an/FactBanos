package ctec.app_fac_banos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.LibroViaje;
import ctec.app_fac_banos.Clases.Mensaje;
import ctec.app_fac_banos.Clases.Puesto;
import ctec.app_fac_banos.ListView.ListViewAdapterPuestos;

public class ListPasajeros extends AppCompatActivity {
    private ListView listVPasajeros;
    ArrayList<Puesto> listaPuestos;
    private ListViewAdapterPuestos adaptadorPuestos;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    int posPuesto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pasajeros);

        listVPasajeros = findViewById(R.id.listVPasajeros);

        Bundle recDatos = getIntent().getExtras();
        final Integer posBuscarLib = Integer.parseInt(recDatos.get("posBuscarLib").toString());

        final LibroViaje libro = Global.g_libros.get(posBuscarLib);
        listaPuestos = new ArrayList<Puesto>(Arrays.asList(libro.getPuestosLibres()));
        adaptadorPuestos = new ListViewAdapterPuestos(this,listaPuestos);
        listVPasajeros.setAdapter(adaptadorPuestos);
        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();


        listVPasajeros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                if (listaPuestos.get(pos).getEstadoPuesto() == true)
                    return;
                posPuesto = pos;
                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(ListPasajeros.this,  " Advertencia",
                         "Desea eliminar el puesto " + listaPuestos.get(pos).getIntIdPuesto().toString()
                                 + " antes de vender? ");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            Puesto[] puestos;

                            puestos =  libro.getPuestosLibres();
                            puestos[posPuesto].setInfPuesto("");
                            puestos[posPuesto].setEstadoPuesto(true);

                            for (int cont =0;cont < Global.g_pasajeros.size(); cont ++){
                                if (Global.g_pasajeros.get(cont).getIntIdPuesto()== puestos[posPuesto].getIntIdPuesto()){
                                    Global.g_pasajeros.remove(cont);
                                }
                            }

                            libro.setPuestosLibres(puestos);
                            Global.g_libros.set(posBuscarLib,libro);


                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(ListPasajeros.this, "Advertencia", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
