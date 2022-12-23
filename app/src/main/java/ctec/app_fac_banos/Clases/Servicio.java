package ctec.app_fac_banos.Clases;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Servicio {

    private int idAgencia;// de tipo entero => identificador de agencia
    private int idRuta;// de tipo entero => identificador de ruta
    private String idServicio;// de tipo string => identificador del servicio
    private String nombreServicio;// de tipo string => nombre del servicio
    private Double tarifa;// de tipo double => valor del servicio
    private Double tarifaTemporada;// de tipo double => valor del servicio en temporada

    public Servicio(){
    }

    public Servicio(int idAgencia, int idRuta, String idServicio, String nombreServicio, Double tarifa, Double tarifaTemporada) {
        this.idAgencia = idAgencia;
        this.idRuta = idRuta;
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.tarifa = tarifa;
        this.tarifaTemporada = tarifaTemporada;
    }

    public int getIdAgencia() {
        return idAgencia;
    }

    public void setIdAgencia(int idAgencia) {
        this.idAgencia = idAgencia;
    }

    public int getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getNomServicio() {
        return nombreServicio;
    }

    public void setNomServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
    }

    public Double getTarifaTemporada() {
        return tarifaTemporada;
    }

    public void setTarifaTemporada(Double tarifaTemporada) {
        this.tarifaTemporada = tarifaTemporada;
    }

    public void insertarServicio(SQLiteDatabase sqLiteDatabase ){
        ContentValues valores = new ContentValues();
        valores.put("idAgencia",this.idAgencia);
        valores.put("idRuta",this.idRuta);
        valores.put("idServicio",this.idServicio.trim());
        valores.put("nomServicio",this.nombreServicio.trim());
        valores.put("tarifa",this.tarifa);
        valores.put("tarifaTemporada",this.tarifaTemporada);
        sqLiteDatabase.insertOrThrow(EstructuraBD.TABLA_RUTASERV,null,valores);
    }
}
