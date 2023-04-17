package ctec.app_fac_banos.Clases;


import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Global {

    public static String g_DirecApi;
    public static String g_NomUbica;
    public static String g_Token;
    public static String g_Usuario;
    public static String g_PassWord;
    public static String g_Serial;
    public static String g_Caja;
    public static String g_CajaNomb;
    public static String g_FechaApr;
    public static Resolucion g_Resolucion;
    public static String g_NomEmp;
    public static String g_DirEmp;
    public static String g_Nit;
    public static int g_ValorConcep;
    public static int g_ValorIva;
    public static Double g_NumFacIni,g_NumFacFin,g_NumFacAct;
    public static String g_NomConcepto;
    public static Usuario g_User;
    public static Gedetsuptip[] detalles;

    public static Integer g_PosBuscar;
    public static Integer g_OpcBuscar; //1:Libros 2:Servicios 3:Rutas 4:Tiquetes 5:Comprobantes Contables
    public static Double g_Total;
    public static ArrayList<LibroViaje> g_libros;
    public static ArrayList<Tiquete> g_tiquetes;
    public static ArrayList<Servicio> g_servicios;
    public static ArrayList<DocGenerales> g_docGenerales;
    public static ArrayList<Ruta> g_rutas;
    public static ArrayList<Pasajero> g_pasajeros;
    public static ConexionSQliteHelper conn;

    //Datos Impresora
    public static String g_HostImp;
    public static String g_portImp;


    public static Bitmap encode(String contents, int width, int height) {
        Bitmap bitmap = null;
        int codeWidth = 3 + // start guard
                (7 * 6) + // left bars
                5 + // middle guard
                (7 * 6) + // right bars
                3; // end guard
        codeWidth = Math.max(codeWidth, width);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,
                    BarcodeFormat.CODE_128, codeWidth, height, null);
//			MatrixToImageWriter.writeToStream(bitMatrix, "png",
//					new FileOutputStream(imgPath));
            int aWidth = bitMatrix.getWidth();
            int aHeight = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, bos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}



