package ctec.app_fac_banos.Clases;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class TipoDevolucion {

    private String codigo;
    private String descripcion;

    public TipoDevolucion() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void insertarTipoDevo(SQLiteDatabase sqLiteDatabase ){
        ContentValues valores = new ContentValues();
        valores.put("codigo",this.codigo);
        valores.put("descripcion",this.descripcion);
        sqLiteDatabase.insertOrThrow(EstructuraBD.TABLA_TIPODEV,null,valores);
    }
}
