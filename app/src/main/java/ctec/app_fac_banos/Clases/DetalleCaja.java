package ctec.app_fac_banos.Clases;

public class DetalleCaja {


    private String tipoDocumento;/*  Tipo de docuemnto que origino el movimeinto en
                                    la caja(LV → Libro de viaje, TQ → Tiquete*/
    private Integer IdDocumento;/*Id del documento */
    private String detalleGeneral;//  Descripcion de el movimiento
    private double valorDebito; // valor debito del libro
    private double valorCredito; // valor credito del libro
    private double cantidad;//  Cantidad
    private double valorUnidad;//e Valor unitario
    private String tipoMovimiento;// D → Débito C → Crédito


    public DetalleCaja() {
    }

    public Integer getIdDocumento() {
        return IdDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        IdDocumento = idDocumento;
    }

    public double getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(double valorDebito) {
        valorDebito = valorDebito;
    }

    public double getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(double valorCredito) {
        valorCredito = valorCredito;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDetalleGeneral() {
        return detalleGeneral;
    }

    public void setDetalleGeneral(String detalleGeneral) {
        this.detalleGeneral = detalleGeneral;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getValorUnidad() {
        return valorUnidad;
    }

    public void setValorUnidad(double valorUnidad) {
        this.valorUnidad = valorUnidad;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
}
