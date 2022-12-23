package ctec.app_fac_banos.Clases;

public class Tiquete {

    Integer numeroTiquete;
    Integer puesto;
    String  codigoTiquete;

    String bus;
    Double valor;
    String fechaSalidaLibro;
    String hora;
    Integer numPuesto;
    Integer idAgenciaLibro;
    String origen;
    String detino;
    String idPasajero;
    String nombrePasajero;
    Integer idTiquete;
    Integer idLibro;
    String cajaVende;
    String fechaVenta;
    Integer agenciaVenta;
    String nombreAgenciaVenta;
    String observaciones;
    String codigoBarras;
    Integer numImpresiones;
            

    public Tiquete() {
    }

    public Tiquete(Integer numeroTiquete, Integer puesto,String codigoTiquete) {
        this.numeroTiquete = numeroTiquete;
        this.puesto = puesto;
        this.codigoTiquete = codigoTiquete;
    }

    public Integer getNumeroTiquete() {
        return numeroTiquete;
    }

    public void setNumeroTiquete(Integer numeroTiquete) {
        this.numeroTiquete = numeroTiquete;
    }

    public Integer getPuesto() {
        return puesto;
    }

    public void setPuesto(Integer puesto) {
        this.puesto = puesto;
    }

    public String getCodigoTiquete() {
        return codigoTiquete;
    }

    public void setCodigoTiquete(String codigoTiquete) {
        this.codigoTiquete = codigoTiquete;
    }

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getFechaSalidaLibro() {
        return fechaSalidaLibro;
    }

    public void setFechaSalidaLibro(String fechaSalidaLibro) {
        this.fechaSalidaLibro = fechaSalidaLibro;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Integer getNumPuesto() {
        return numPuesto;
    }

    public void setNumPuesto(Integer numPuesto) {
        this.numPuesto = numPuesto;
    }

    public Integer getIdAgenciaLibro() {
        return idAgenciaLibro;
    }

    public void setIdAgenciaLibro(Integer idAgenciaLibro) {
        this.idAgenciaLibro = idAgenciaLibro;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDetino() {
        return detino;
    }

    public void setDetino(String detino) {
        this.detino = detino;
    }

    public String getIdPasajero() {
        return idPasajero;
    }

    public void setIdPasajero(String idPasajero) {
        this.idPasajero = idPasajero;
    }

    public String getNombrePasajero() {
        return nombrePasajero;
    }

    public void setNombrePasajero(String nombrePasajero) {
        this.nombrePasajero = nombrePasajero;
    }

    public Integer getIdTiquete() {
        return idTiquete;
    }

    public void setIdTiquete(Integer idTiquete) {
        this.idTiquete = idTiquete;
    }

    public Integer getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    public String getCajaVende() {
        return cajaVende;
    }

    public void setCajaVende(String cajaVende) {
        this.cajaVende = cajaVende;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Integer getAgenciaVenta() {
        return agenciaVenta;
    }

    public void setAgenciaVenta(Integer agenciaVenta) {
        this.agenciaVenta = agenciaVenta;
    }

    public String getNombreAgenciaVenta() {
        return nombreAgenciaVenta;
    }

    public void setNombreAgenciaVenta(String nombreAgenciaVenta) {
        this.nombreAgenciaVenta = nombreAgenciaVenta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Integer getNumImpresiones() {
        return numImpresiones;
    }

    public void setNumImpresiones(Integer numImpresiones) {
        this.numImpresiones = numImpresiones;
    }
}
