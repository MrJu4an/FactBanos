package ctec.app_fac_banos.Clases;

public class EstructuraBD {


    //constantes Campos Tabla Rutas
    public static final String TABLA_RUTAS = "RUTAS";
    public static final String CAMPO_idAgencia = "idAgencia";
    public static final String CAMPO_idRuta = "idRuta";
    public static final String CAMPO_nombreRuta = "nombreRuta";

    //constantes Campos Tabla RutasServicios
    public static final String TABLA_RUTASERV = "RUTASERV";
    public static final String CAMPO_idAgenciaSer= "idAgencia";
    public static final String CAMPO_idRutaSer = "idRuta";
    public static final String CAMPO_idServicio = "idServicio";
    public static final String CAMPO_nomServicio = "nomServicio";
    public static final String CAMPO_tarifa = "tarifa";
    public static final String CAMPO_tarifaTemporada= "tarifaTemporada";

    //constantes Campos Tabla Tipos Devolucion
    public static final String TABLA_TIPODEV = "TIPODEV";
    public static final String CAMPO_codigo= "codigo";
    public static final String CAMPO_descripcion = "descripcion";

    //constantes Campos Tabla Comprobantes Contables "Documentos Generales"
    public static final String TABLA_DOCGEN = "DOCGEN";
    public static final String CAMPO_intIdAgencia = "intIdAgencia";
    public static final String CAMPO_idComprobante = "idComprobante";
    public static final String CAMPO_nombreComprobante = "nombreComprobante";
    public static final String CAMPO_requiereId2= "requiereId2";



    public static final String CREAR_TABLA_RUTAS = "CREATE TABLE RUTAS (idAgencia INTEGER, idRuta INTEGER ,nombreRuta TEXT)";

    public static final String CREAR_TABLA_RUTASERV = "CREATE TABLE RUTASERV (idAgencia INTEGER,idRuta INTEGER,idServicio TEXT," +
            "nomServicio TEXT,tarifa INTEGER,tarifaTemporada INTEGER)";

    public static final String CREAR_TABLA_TIPODEV = "CREATE TABLE TIPODEV (codigo TEXT, descripcion TEXT)";

    public static final String CREAR_TABLA_DOCGEN = "CREATE TABLE DOCGEN (intIdAgencia INTEGER, idComprobante TEXT ," +
            "nombreComprobante TEXT,requiereId2 INTEGER)";

    public static final String DELETE_TABLA_RUTAS = "DELETE FROM " + TABLA_RUTAS;
    public static final String DELETE_TABLA_RUTASERV = "DELETE FROM " + TABLA_RUTASERV;
    public static final String DELETE_TABLA_TIPODEV = "DELETE FROM " + TABLA_TIPODEV;
    public static final String DELETE_TABLA_DOCGEN = "DELETE FROM " + TABLA_DOCGEN;
}
