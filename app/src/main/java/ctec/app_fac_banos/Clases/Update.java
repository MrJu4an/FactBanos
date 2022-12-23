package ctec.app_fac_banos.Clases;

import java.io.*;
import java.net.*;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.View;
import android.widget.ProgressBar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.R;

/**
 * Clase que se encarga de actualizar la aplicacion.
 */
public class Update
{
    //-------------------------------------------------------------------------------------------------------
    // Constantes
    //-------------------------------------------------------------------------------------------------------

    private static final String URL = "http://www.ctaltda.net/movil/";
    private static final String APP_VERSION = "AppVersion.txt";
    private  static String APP_NAME = "FacBañosPereira";
    private  String APP_ARCHIVO = "FacBañosPereira.apk";
    //-------------------------------------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------------------------------------

    private Mensaje mensaje;
    private Intent intent;
    private String textoMensaje;
    private String  versionAppServidor;
    private String versionAppActual;
    SweetAlertDialog sweetAlertDialog;
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
    public Update( Activity activity )
    {
        mensaje = new Mensaje( );
        this.activity = activity;

        sweetAlertDialog = new SweetAlertDialog(this.activity);
        simpleProgressBar= this.activity.findViewById(R.id.progressBar); // initiate the progress bar
        simpleProgressBar.setMax(100); // get maximum value of the progress bar
        simpleProgressBar.setVisibility(View.VISIBLE);

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
            versionAppServidor = obtenerVersionAppServidor( ) ;
            //Obtiene la version de la aplicacion actual.
            versionAppActual =  obtenerVersionAppActual( ) ;
            return 1;
        }

        protected void onPostExecute( Object result )
        {
            //Valida la version para verificar si se debe actualizar.
            if( !versionAppServidor.equals(versionAppActual) )
            {
                //Llama la clase que se encarga de actualizar la aplicacion a la version descargada del servidor.
                new ActualizarAplicacion( ).execute( "" );
                //Crea el dialogo con la barra de progreso.
                textoMensaje = "Descargando actualizacion de la aplicacion.\nPor favor espere...";
                //mensaje.( activity, Mensaje.MSG_INFO, textoMensaje, General.ANCHO, ProgressDialog.STYLE_HORIZONTAL );
            }
            else
            {
                sweetAlertDialog.dismiss();
                mensaje.MensajeNormal( activity, "Mensaje","No existen actualizaciones!!!" );
                try{
                    simpleProgressBar.setVisibility(View.INVISIBLE);
                }
                catch(Exception exception){
                    simpleProgressBar.setVisibility(View.INVISIBLE);
                }

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
            sweetAlertDialog.dismiss();
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
            sweetAlertDialog = mensaje.progreso(this.activity,"Actualizando Software");
            sweetAlertDialog.show();
            //Establece la ruta donde esta guardado el archivo. Por defeto se deja en la tarjeta SD.
            File rutaSD = Environment.getExternalStorageDirectory( );
            //Crea un objeto donde se almacena el archivo descargado.
            File file = new File( rutaSD, APP_ARCHIVO );
            //Elimina el archivo de la descarga anterior si existe.
            if( file.exists( ) )
                file.delete( );

            //Llama la clase que se encarga de obtener la version.
            new ObtenerVersion( ).execute( "" );
        } catch( Exception exception ) {
            exception.printStackTrace( );
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia( activity, "Advertencia",exception.getMessage());

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
            File rutaSD = Environment.getExternalStorageDirectory( );
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
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia( activity, "Advertencia",exception.getMessage() );
        }
    }

    /**
     * Obtiene la version de la aplicacion que esta en el servidor.
     * @return version Devuelve la version de la aplicacion.
     */
    private String obtenerVersionAppServidor( )
    {
        //Declara las variables.
        String version = "0.0";
        String versiones = "";
        String buffer = "";

        try
        {
            //Abre la direccion url donde se encuentra el archivo.
            URL url = new URL( URL + APP_VERSION );
            //Lee el archivo del servidor.
            BufferedReader bufferReader = new BufferedReader( new InputStreamReader( url.openStream( ) ) );

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
                    version = app.substring(app.indexOf("(") +1, app.indexOf(")"));
            }

			/*for(int i = 0; i < apps.length; i++)
			{
				if(apps[i].substring(0, apps[i].length() - 5).equals(APP_NAME))
					version = apps[i].substring(apps[i].length() - 4, apps[i].length() - 1);
			}*/

            //Cierra la conexion al archivo.
            bufferReader.close( );
        }

        catch( Exception exception )
        {
            exception.printStackTrace( );
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia( activity, "Advertencia",exception.getMessage() );
        }

        return version;
    }

    /**
     * Obtiene la version actual de la aplicacion que esta instalada.
     * @return version Version actual de la aplicacion.
     */
    private String obtenerVersionAppActual( )
    {
        //Declara las varaibles.
        String version = "0.0";

        try
        {
            version = activity.getPackageManager( ).getPackageInfo( activity.getPackageName( ), 0 ).versionName;
        }

        catch( Exception exception )
        {
            exception.printStackTrace( );
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia( activity, "Advertencia",exception.getMessage() );
        }

        return version;
    }

    /**
     * Descarga la actualizacion de la aplicacion que se encuentra en el servidor.
     */
    private void descargarActualizacion( )
    {
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
            File rutaSD = Environment.getExternalStorageDirectory( );
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
                simpleProgressBar.setProgress (( int ) ( descargaSize * 100 / fileSize ));
                //mensaje.setProgressBar(  );
            }

            //Cierra la lectura del archivo.
            fileOutput.close( );
            sweetAlertDialog.dismiss();
            try{
                simpleProgressBar.setVisibility(View.INVISIBLE);
            }
            catch(Exception exception){

            }

        }

        catch( Exception exception )
        {
            exception.printStackTrace( );
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia( activity, "Advertencia",exception.getMessage() );
        }
    }

    /**
     * Instala la aplicacion que se ha descargado.
     */
    private void instalarAplicacion( )
    {
        try
        {
            sweetAlertDialog.dismiss();
            String fileName = Environment.getExternalStorageDirectory( ) + "/" + APP_ARCHIVO;
            intent = new Intent( Intent.ACTION_VIEW );
            Uri uri = Uri.fromFile( new File( fileName ) );
            intent.setDataAndType( uri, "application/vnd.android.package-archive" );
            activity.startActivity( intent );
        } catch( Exception exception ) {
            exception.printStackTrace( );
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia( activity, "Advertencia",exception.getMessage() );
        }
    }
}
