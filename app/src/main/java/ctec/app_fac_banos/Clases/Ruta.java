package ctec.app_fac_banos.Clases;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class Ruta {

    private int idAgencia;
    private int idRuta;
    private String nombreRuta;

    public Ruta() {
    }

    public Ruta(int idAgencia,  int idRuta, String nombreRuta) {
        this.idAgencia = idAgencia;
        this.idRuta = idRuta;
        this.nombreRuta = nombreRuta;
    }

    public int getidAgencia() {
        return idAgencia;
    }

    public void setidAgencia(int idAgencia) {this.idAgencia = idAgencia;}

    public int getidRuta() {
        return idRuta;
    }

    public void setidRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public String getnombreRuta() {
        return nombreRuta;
    }

    public void setnombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public void insertarRuta(SQLiteDatabase sqLiteDatabase ){
        ContentValues valores = new ContentValues();
        valores.put("idAgencia",this.idAgencia);
        valores.put("idRuta",this.idRuta);
        valores.put("nombreRuta",this.nombreRuta.trim());
        sqLiteDatabase.insertOrThrow(EstructuraBD.TABLA_RUTAS,null,valores);
    }
}
