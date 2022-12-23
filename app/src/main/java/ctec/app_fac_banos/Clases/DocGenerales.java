package ctec.app_fac_banos.Clases;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DocGenerales {

    private int intIdAgencia;
    private String idComprobante;
    private String nombreComprobante;
    private boolean requiereId2;

    public DocGenerales() {
    }

    public DocGenerales(int intIdAgencia, String idComprobante, String nombreComprobante, boolean requiereId2) {
        this.intIdAgencia = intIdAgencia;
        this.idComprobante = idComprobante;
        this.nombreComprobante = nombreComprobante;
        this.requiereId2 = requiereId2;
    }

    public int getIntIdAgencia() {
        return intIdAgencia;
    }

    public void setIntIdAgencia(int intIdAgencia) {
        this.intIdAgencia = intIdAgencia;
    }

    public String getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(String idComprobante) {
        this.idComprobante = idComprobante;
    }

    public String getNombreComprobante() {
        return nombreComprobante;
    }

    public void setNombreComprobante(String nombreComprobante) {
        this.nombreComprobante = nombreComprobante;
    }

    public boolean getRequiereId2() {
        return requiereId2;
    }

    public void setRequiereId2(boolean requiereId2) {
        this.requiereId2 = requiereId2;
    }

    public void insertarDocGenerales(SQLiteDatabase sqLiteDatabase ){
        ContentValues valores = new ContentValues();
        valores.put("intIdAgencia",this.intIdAgencia);
        valores.put("idComprobante",this.idComprobante.trim());
        valores.put("nombreComprobante",this.nombreComprobante.trim());
        valores.put("requiereId2",this.requiereId2);
        sqLiteDatabase.insertOrThrow(EstructuraBD.TABLA_DOCGEN,null,valores);
    }
}
