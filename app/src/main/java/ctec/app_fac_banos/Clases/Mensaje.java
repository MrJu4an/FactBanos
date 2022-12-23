package ctec.app_fac_banos.Clases;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ctec.app_fac_banos.R;

public class Mensaje {
    public static Boolean confirmacion;

    /**
     * Barra de progreso
     *
     * @param context
     */
    public SweetAlertDialog progreso(Context context, String Descripcion) {

        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.PrimaryColor);
        pDialog.setTitleText(Descripcion);
        pDialog.setCancelable(false);

        return pDialog;
    }

    /**
     * Muestra un mensaje con el solo titulo y descripcion
     *
     * @param context
     * @param titulo
     * @param descripcion
     */

    public void MensajeNormal(Context context, String titulo, String descripcion) {
        new SweetAlertDialog(context)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();
    }

    /**
     * Mensaje de Error
     *
     * @param context
     * @param titulo
     * @param descripcion
     */
    public void MensajeError(Context context, String titulo, String descripcion) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();
    }

    /**
     * Mensaje de Advertencia
     *
     * @param context
     * @param titulo
     * @param descripcion
     */
    public void MensajeAdvertencia(Context context, String titulo, String descripcion) {

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();
    }

    /**
     * Mensaje Exitoso
     *
     * @param context
     * @param titulo
     * @param descripcion
     */

    public void MensajeExitoso(Context context, String titulo, String descripcion) {

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();
    }

/*    public void MensajeExitoso(Context context, String titulo, String descripcion) {

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();

    }*/

    /**
     * Mensaje normal con icono
     *
     * @param context
     * @param titulo
     * @param descripcion
     */
    public void MensajeNormalConIcon(Context context, String titulo, String descripcion) {
        new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();

    }

    public SweetAlertDialog MensajeConfirmacionAdvertencia(Context contex, String titulo, String descripcion) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(contex, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar");
        /**sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override public void onClick(SweetAlertDialog sDialog) {
        Toast.makeText(getApplicationContext(), "True", Toast.LENGTH_LONG).show();
        sDialog.dismissWithAnimation();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }

    public SweetAlertDialog MensajeConfirmacionAdvertenciaConBotones(Context contex, String titulo, String descripcion) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(contex, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setCancelText("Cancelar")
                .setConfirmText("Aceptar")
                .showCancelButton(true);
        /**.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override public void onClick(SweetAlertDialog sDialog) {
        sDialog.cancel();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }

    public SweetAlertDialog MensajeConfirmacionAdvertenciaConUnBoton(Context contex, String titulo, String descripcion) {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(contex, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setCancelText("Cancelar")
                .setConfirmText("Aceptar")
                .showCancelButton(false);

        /**.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override public void onClick(SweetAlertDialog sDialog) {
        sDialog.cancel();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }

    public SweetAlertDialog MensajeConfirmacionExitosoConUnBoton(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .showCancelButton(true);
        /**.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        sDialog.cancel();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }

    public SweetAlertDialog MensajeConfirmacionError(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar");
        /**sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        Toast.makeText(getApplicationContext(), "True", Toast.LENGTH_LONG).show();
        sDialog.dismissWithAnimation();
        }
        })
         .show();*/
        return sweetAlertDialog;
    }

    public SweetAlertDialog MensajeConfirmacionErrorConBotones(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setCancelText("Cancelar")
                .setConfirmText("Aceptar")
                .showCancelButton(true);
        /**.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        sDialog.cancel();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }


}
