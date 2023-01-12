package ctec.app_fac_banos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.TcpIpOutputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.Clases.DescargaDatos;
import ctec.app_fac_banos.Clases.Global;
import ctec.app_fac_banos.Clases.Mensaje;

public class MenuActivity extends AppCompatActivity {

    private CardView cardVentaTiq,cardCuadreCaj,cardAnuDevTiq,cardCerrarCaja,cardDescargaDatos;
    private CardView cardSalir;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    SimpleDateFormat dateFormat;
    Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mensaje = new Mensaje();
        cardVentaTiq = findViewById(R.id.cardVentaTiq);
        cardCuadreCaj = findViewById(R.id.cardCuadreCaj);
        cardAnuDevTiq = findViewById(R.id.cardAnuDevTiq);
        cardCerrarCaja = findViewById(R.id.cardCerrarCaja);
        cardDescargaDatos = findViewById(R.id.cardDescargaDatos);
        cardSalir = findViewById(R.id.cardSalida);

        setTitle("Menu Principal");
        cardVentaTiq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Datos impresora
                String host = Global.g_HostImp;
                Integer port = Integer.parseInt(Global.g_portImp);
                probarImpresora(host, port);

                startActivity(new Intent(MenuActivity.this, FacBanosActivity.class));
            }
        });

        cardCuadreCaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, CuadreCajaActivity.class));
            }
        });


        cardAnuDevTiq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, DevolverTiqActivity.class));
            }
        });

        cardCerrarCaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(MenuActivity.this,"Informacion","Esta seguro de cerrar caja?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startActivity(new Intent(MenuActivity.this, CierreCajaActivity.class));
                        sweetAlertDialog.dismiss();
                    }
                });
                sweetAlertDialog.show();

            }
        });

        cardDescargaDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(MenuActivity.this,"Informacion","Desea descargar datos?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        DescargaDatos descargaDatos = new DescargaDatos(MenuActivity.this);
                        descargaDatos.DescargaResolucion(Global.g_Caja);
                        sweetAlertDialog.dismiss();
                    }
                });
                sweetAlertDialog.show();

            }
        });

        cardSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });


    }

    public void probarImpresora(String host, int port){
        try{
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try(TcpIpOutputStream outputStream = new TcpIpOutputStream(host, port)) {
                        EscPos escpos = new EscPos(outputStream);
                        Style title = new Style()
                                .setFontSize(Style.FontSize._2, Style.FontSize._2)
                                .setJustification(EscPosConst.Justification.Center);
                        escpos.writeLF(title, "Bienvenido")
                        .feed(5)
                        .cut(EscPos.CutMode.FULL);

                    } catch (IOException e){
                        mensaje.MensajeAdvertencia(MenuActivity.this,"Advertencia","No se pudo conectar con la impresora. \n" + e.getMessage() );
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e){
            mensaje.MensajeAdvertencia(MenuActivity.this,"Advertencia","No se pudo conectar con la impresora. \n" + e.getMessage() );
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (readPreference("sesion").equals("0"))
            finish();
    }

    @Override
    public void onBackPressed() {

    }

    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = getSharedPreferences("FacBan", MODE_PRIVATE);

            valor = prefs.getString(key, "-1");
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(MenuActivity.this,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }
}
