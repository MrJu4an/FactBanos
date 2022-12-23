package ctec.app_fac_banos.Clases;

import android.Manifest;

public class Constantes {

    public static final String ADMIN_USER_NAME = "0423";
    public static final String ADMIN_USER_PASSWORD = "0423";
    public static final String ADMIN_FORMAT_PASSWORD = "4758";
    public static final String IP_DEFAULT = "http://190.60.235.204/apilistening/";
    public static final int PUERTO_DEFAULT = 4000;

    public static final String PREF_IP_PRINCIPAL = "ipPrincipal";
    public static final String PREF_PUERTO_PRINCIPAL = "puerto";

    public static final String PREF_DESCARGA_COMPLETA = "DescargaCompleta";
    public static final String PREF_CONFIGURADO = "configurado";
    public static final String PREF_LOGUEADO = "Logueado";
    public static final String PREF_PUNTO_VENTA = "PtoVenta";
    public static final String PREF_ACTUAL_USER = "ActualUser";
    public static final String PREF_CODUSER = "CodUser";
    public static final String PREF_NUMDES = "numdes";
    public static final String PREF_NUMTIQ = "numtiq";
    public static final String PREF_NUMTIQS = "numtiqs";
    public static final String PREF_LOGOEMPRESA = "LogoEmpresa";
    public static final String PREF_IDESP= "idDespacho";

    public static final String LINE_SMALL_PRINT = "- - - - - - - - - - - - - - - - - - - - - ";
    public static final String LINE_MEDIUM_PRINT = "- - - - - - - - - - - - - - - - ";

    public static final String DEV_BY = "Desarrollado por:";
    public static final String CTEC ="Consultores Tecnologicos S.A.S";
    public static final String DATETIME_RELEASE = "04/11/2020 14:30";
    public static final String VIGILADO = "VIGILADO SUPERTRANSPORTE";

    public static final String API_DOWNLOADINI ="/Api/Listening/DownloadIni";
    public static final String API_DOWNLOAD ="/Api/Listening/Download";
    public static final String API_OFERTASXFECHA ="/api/Listening/OfertasporFecha";
    public static final String API_CONSUMEISO = "/api/Listening/ConsumeIso";
    public static final String API_CONSULTADOCUMENTO = "/api/Listening/ConsultarDoc";
    public static final String API_CONSULTARTIQUETES = "/api/Listening/ConsultarTiquetes";
    public static final String API_BUSCARTIQUETES = "/api/Listening/BuscarTiquetes";

    public static final int CLICK_MOVE_DB = 2;
    public static final int PERMISSION_ALL = 1;
    public static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.NFC,
            Manifest.permission.BIND_NFC_SERVICE,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

}
