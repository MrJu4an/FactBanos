package ctec.app_fac_banos.Clases;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.ConfigActivity;
import ctec.app_fac_banos.R;

public class UpdateApp {

   // private static final String URL = "http://190.60.235.204/apilistening/app/";
    private String URL;
    private static final String APP_VERSION = "AppVersion.txt";
    private static final String APP_NAME = "fac_banos";
    private static String APP_ARCHIVO = "fac_banos.apk";

    //-------------------------------------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------------------------------------

    private Context context;
    private Mensaje mensaje;
    private SweetAlertDialog sweetAlertDialog;
    private Intent intent;
    private String textoMensaje;
    private int versionAppServidor;
    private int versionAppActual;
    ProgressBar simpleProgressBar;
    //-------------------------------------------------------------------------------------------------------
    // Controles
    //-------------------------------------------------------------------------------------------------------

    private Activity activity;

    //-------------------------------------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------------------------------------

    /**
     * Constructor de la clase.
     * @param activity Activity donde se realiza la actualizacion.
     */
    public UpdateApp(Activity activity, Context context )
    {

        mensaje = new Mensaje();
        this.activity = activity;
        this.context = context;
        URL = readPreference("DirecApi") + "App/";
        sweetAlertDialog = new SweetAlertDialog(context);
        this.activity = activity;

        sweetAlertDialog = new SweetAlertDialog(this.activity);
    }

    //-------------------------------------------------------------------------------------------------------
    // Clases
    //-------------------------------------------------------------------------------------------------------

    /**
     * Clase que se encarga de verificar y actualizar la aplicacion.
     * @author Joseph Guzman
     */
    private class ObtenerVersion extends AsyncTask<String, Void, Object>
    {
        //Declara las vaariables.


        protected Integer doInBackground( String... args )
        {
            //Obtiene la version de la aplicacion en el servidor.
            //versionAppServidor = Double.parseDouble( obtenerVersionAppServidor( ) );
            versionAppServidor =  obtenerVersionAppServidor( ) ;
            //Obtiene la version de la aplicacion actual.
            //versionAppActual = Double.parseDouble( obtenerVersionAppActual( ) );
            versionAppActual = obtenerVersionAppActual( );
            return 1;
        }

        protected void onPostExecute( Object result )
        {
            //Valida la version para verificar si se debe actualizar.
            if( versionAppServidor > versionAppActual )
            {

                //Llama la clase que se encarga de actualizar la aplicacion a la version descargada del servidor.
                new ActualizarAplicacion( ).execute( "" );
                //Crea el dialogo con la barra de progreso.
                textoMensaje = "Descargando actualizacion de la aplicacion.\nPor favor espere...";
                //mensaje.progreso( activity, Mensaje.MSG_INFO, textoMensaje, "0", ProgressDialog.STYLE_HORIZONTAL );
                sweetAlertDialog = mensaje.progreso(activity,"Actualizando");
                sweetAlertDialog.show();
            }

            super.onPostExecute( result );
        }
    }

    /**
     * Clase que se encarga de descargar la aplicacion y actualizarla.
     * @author Joseph Guzman
     */
    private class ActualizarAplicacion extends AsyncTask<String, Void, Object>
    {
        protected Integer doInBackground( String... args )
        {
            //Descarga la actualizacion.
            descargarActualizacion( );
            //Instala la actualizacion.
            instalarAplicacion( );
            return 1;
        }

        protected void onPostExecute( Object result )
        {
            if (sweetAlertDialog!=null)
            {
                sweetAlertDialog.dismiss();
            }
            super.onPostExecute( result );
        }
    }

    //-------------------------------------------------------------------------------------------------------
    // Metodos
    //-------------------------------------------------------------------------------------------------------

    /**
     * Actualiza la aplicacion instalada validando la version con la que esta en el servidor.
     */
    public void actualizarAplicacion()
    {
        try
        {
            //Establece la ruta donde esta guardado el archivo. Por defeto se deja en la tarjeta SD.
            File rutaSD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //Crea un objeto donde se almacena el archivo descargado.
            File file = new File( rutaSD, APP_ARCHIVO );
            //Elimina el archivo de la descarga anterior si existe.
            if( file.exists( ) )
                file.delete( );

            //Llama la clase que se encarga de obtener la version.
            new ObtenerVersion( ).execute( "" );
        } catch( Exception exception ) {
            exception.printStackTrace( );
            mensaje.MensajeConfirmacionError( activity, "Alerta", exception.getMessage( ));
        }
    }

    /**
     * Actualiza la aplicacion instalada validando las version con la que esta en el servidor.
     * @param nombreCliente Nombre del cliente.
     */
    @Deprecated
    public void actualizarAplicacion( String nombreCliente )
    {
        try
        {
            //Verifica el nombre de cliente para la descarga del archivo.
            if( nombreCliente.equals( "GAITANA" ) )
                APP_ARCHIVO = "Alistamiento_Gaitana.apk";

            //Establece la ruta donde esta guardado el archivo. Por defeto se deja en la tarjeta SD.
            File rutaSD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //Crea un objeto donde se almacena el archivo descargado.
            File file = new File( rutaSD, APP_ARCHIVO );
            //Elimina el archivo de la descarga anterior si existe.
            if( file.exists( ) )
                file.delete( );

            //Llama la clase que se encarga de obtener la version.
            new ObtenerVersion( ).execute( "" );
        }

        catch( Exception exception )
        {
            exception.printStackTrace( );
            mensaje.MensajeConfirmacionError( activity, "Alerta", exception.getMessage( ));
        }
    }

    /**
     * Obtiene la version de la aplicacion que esta en el servidor.
     * @return version Devuelve la version de la aplicacion.
     */
    //public String obtenerVersionAppServidor( )
    public Integer obtenerVersionAppServidor( )
    {
        //Declara las variables.
        String version = "0.0";
        String versiones = "";
        String buffer = "";
        Integer versionN = 0;

        try
        {

            getVersionServer versionServer = new getVersionServer();

            Thread tversion = new Thread(versionServer);
            tversion.start();
            tversion.join();
            versionN = versionServer.getValue();


            ////Abre la direccion url donde se encuentra el archivo.
            //java.net.URL url = new URL( URL + APP_VERSION );

            ////Lee el archivo del servidor.
            ////BufferedReader bufferReader = new BufferedReader( new InputStreamReader( url.openStream( ) ) );

            ////Recorre las lineas del archivo.
            //while( ( buffer = bufferReader.readLine( ) ) != null )
            //{
            //    versiones += buffer;
            //}

            ////Obtiene las versiones de las aplicaciones que hay en el servidor.
            //String[] apps = versiones.split(";");
            //Recorre la lista de aplicaciones para obtener la que se necesita.
            //for (String app: apps){
            //    if (app.substring(0, app.indexOf("(")).equals(APP_NAME))
            ////        //version = app.substring(app.indexOf("(") +1, app.indexOf(")"));
            //        versionN =  Integer.parseInt(  app.substring(app.indexOf("(") +1, app.indexOf(")")));
            //}

			/*for(int i = 0; i < apps.length; i++)
			{
				if(apps[i].substring(0, apps[i].length() - 5).equals(APP_NAME))
					version = apps[i].substring(apps[i].length() - 4, apps[i].length() - 1);
			}*/

            ////Cierra la conexion al archivo.
            //bufferReader.close( );
        }

        catch( Exception exception )
        {
            exception.printStackTrace( );
        }

        //return version;
        return  versionN;
    }

    /**
     * Obtiene la version actual de la aplicacion que esta instalada.
     * @return version Version actual de la aplicacion.
     */
    //public String obtenerVersionAppActual( )
    public Integer obtenerVersionAppActual( )
    {
        //Declara las varaibles.
        //String version = "0.0";
        Integer version =  0;

        try
        {
            version = activity.getPackageManager( ).getPackageInfo( activity.getPackageName( ), 0 ).versionCode;
        }

        catch( Exception exception )
        {
            exception.printStackTrace( );
        }

        return version;
    }

    /**
     * Descarga la actualizacion de la aplicacion que se encuentra en el servidor.
     */
    private void descargarActualizacion( )
    {
        downloadVersion download_version = new downloadVersion();
        Thread tdownload = new Thread(download_version);
        tdownload.start();

        ////Declara las variables.
        //int fileSize = 0, descargaSize = 0, bufferLength = 0;
        //byte[ ] buffer = new byte[ 1024 ];
        /*
        try
        {
            //Define la url de la que se va a descargar el archivo.
            URL url = new URL( URL + APP_ARCHIVO );
            //Establece la conexion con la url.
            HttpURLConnection urlConnection = ( HttpURLConnection ) url.openConnection( );
            //Establece el metodo GET para la conexion.
            urlConnection.setRequestMethod( "GET" );
            urlConnection.setDoOutput( true );
            urlConnection.connect( );

            //Establece la ruta donde se guardara el archivo. Por defeto se deja en la tarjeta SD.
            File rutaSD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //Crea un objeto donde se almacena el archivo descargado.
            File file = new File( rutaSD, APP_ARCHIVO );

            //Usa un objeto del tipo fileoutputstream para escribir el archivo que se descargo en el nuevo.
            FileOutputStream fileOutput = new FileOutputStream( file );
            //Lee los datos desde la url.
            InputStream inputStream = urlConnection.getInputStream( );
            //Obtiene el tamaño del archivo
            fileSize = urlConnection.getContentLength();

            //Recorre el buffer para escribir el archivo de destino siempre teniendo constancia de la cantidad descargada y el total del tamaño.
            while( ( bufferLength = inputStream.read( buffer ) ) > 0 )
            {
                fileOutput.write( buffer, 0, bufferLength );
                descargaSize += bufferLength;
                //ProgressHelper progressHelper= sweetAlertDialog.getProgressHelper();
                //progressHelper.setProgress(( float ) ( descargaSize * 100 / fileSize )); //( int ) ( descargaSize * 100 / fileSize ) );
            }

            //Cierra la lectura del archivo.
            fileOutput.close( );
        }

        catch( Exception exception )
        {
            exception.printStackTrace( );
        }*/
    }

    /**
     * Instala la aplicacion que se ha descargado.
     */
    private void instalarAplicacion( )
    {
        try
        {
            //File rutaSD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + APP_ARCHIVO;
            intent = new Intent( Intent.ACTION_VIEW );
            Uri uri = Uri.fromFile( new File( fileName ) );
            intent.setDataAndType( uri, "application/vnd.android.package-archive" );
            activity.startActivity( intent );
        } catch( Exception exception ) {
            exception.printStackTrace( );
        }
    }

    public  class getVersionServer implements  Runnable{
        private volatile int value;

        @Override
        public void run() {
            try {

                //Declara las variables.
                String versiones = "";
                String buffer = "";

                //Abre la direccion url donde se encuentra el archivo.
                java.net.URL url = new URL(URL +APP_VERSION);

                //Lee el archivo del servidor.
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(url.openStream()));
                //Recorre las lineas del archivo.
                while( ( buffer = bufferReader.readLine( ) ) != null )
                {
                    versiones += buffer;
                }

                //Obtiene las versiones de las aplicaciones que hay en el servidor.
                String[] apps = versiones.split(";");
                //Recorre la lista de aplicaciones para obtener la que se necesita.
                for (String app: apps){
                    if (app.substring(0, app.indexOf("(")).equals(APP_NAME))
                        //version = app.substring(app.indexOf("(") +1, app.indexOf(")"));
                        value =  Integer.parseInt(  app.substring(app.indexOf("(") +1, app.indexOf(")")));
                }

                //Cierra la conexion al archivo.
                bufferReader.close( );

            }catch (Exception ex){
                Log.e("UPDATE",ex.getMessage());
            }
        }
        public int getValue(){
            return value;
        }
    }

    public class downloadVersion implements Runnable{

        @Override
        public void run() {
            //Declara las variables.
            int fileSize = 0, descargaSize = 0, bufferLength = 0;
            byte[ ] buffer = new byte[ 1024 ];

            try
            {
                //Define la url de la que se va a descargar el archivo.
                URL url = new URL( URL + APP_ARCHIVO );
                //Establece la conexion con la url.
                HttpURLConnection urlConnection = ( HttpURLConnection ) url.openConnection( );
                //Establece el metodo GET para la conexion.
                urlConnection.setRequestMethod( "GET" );
                urlConnection.setDoOutput( true );
                urlConnection.connect( );

                //Establece la ruta donde se guardara el archivo. Por defeto se deja en la tarjeta SD.
                File rutaSD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                //Crea un objeto donde se almacena el archivo descargado.
                File file = new File( rutaSD, APP_ARCHIVO );

                //Usa un objeto del tipo fileoutputstream para escribir el archivo que se descargo en el nuevo.
                FileOutputStream fileOutput = new FileOutputStream( file );
                //Lee los datos desde la url.
                InputStream inputStream = urlConnection.getInputStream( );
                //Obtiene el tamaño del archivo
                fileSize = urlConnection.getContentLength();

                //Recorre el buffer para escribir el archivo de destino siempre teniendo constancia de la cantidad descargada y el total del tamaño.
                while( ( bufferLength = inputStream.read( buffer ) ) > 0 )
                {
                    fileOutput.write( buffer, 0, bufferLength );
                    descargaSize += bufferLength;
                    //ProgressHelper progressHelper= sweetAlertDialog.getProgressHelper();
                    //progressHelper.setProgress(( float ) ( descargaSize * 100 / fileSize )); //( int ) ( descargaSize * 100 / fileSize ) );
                }

                //Cierra la lectura del archivo.
                fileOutput.close( );
            }
            catch( Exception exception )
            {
                exception.printStackTrace( );
            }
        }
    }


    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = context.getSharedPreferences("FacBan", 0);

            valor = prefs.getString(key, "");
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(context,"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }
}
