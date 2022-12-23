package ctec.app_fac_banos.Clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ConexionSQliteHelper extends SQLiteOpenHelper {


    public ConexionSQliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EstructuraBD.CREAR_TABLA_RUTAS);
        db.execSQL(EstructuraBD.CREAR_TABLA_RUTASERV);
        db.execSQL(EstructuraBD.CREAR_TABLA_TIPODEV);
        db.execSQL(EstructuraBD.CREAR_TABLA_DOCGEN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS RUTAS");
        db.execSQL("DROP TABLE IF EXISTS RUTASERV");
        db.execSQL("DROP TABLE IF EXISTS TIPODEV");
        db.execSQL("DROP TABLE IF EXISTS DOCGEN");
        onCreate(db);
    }
}
