package ctec.app_fac_banos.Clases;

public class DetallePasajero {

    private Integer puesto;// integer Numero del puesto del pasajero
    private Integer nroTiquete;// integer Numero del pasaje
    private String destinoPasajero;// string Lugar donde termina su viaje el pasajero
    private String nombrePasajero;// string Nombres y apellidos del pasajero
    private Double tarifa;// double Costo de la ruta y servicio
    private String idPasajero;// string Numero de identificación del pasajero,normalmente cedula de ciudadanía

    public DetallePasajero() {
    }

    public Integer getPuesto() {
        return puesto;
    }

    public void setPuesto(Integer puesto) {
        this.puesto = puesto;
    }

    public Integer getNroTiquete() {
        return nroTiquete;
    }

    public void setNroTiquete(Integer nroTiquete) {
        this.nroTiquete = nroTiquete;
    }

    public String getDestinoPasajero() {
        return destinoPasajero;
    }

    public void setDestinoPasajero(String destinoPasajero) {
        this.destinoPasajero = destinoPasajero;
    }

    public String getNombrePasajero() {
        return nombrePasajero;
    }

    public void setNombrePasajero(String nombrePasajero) {
        this.nombrePasajero = nombrePasajero;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
    }

    public String getIdPasajero() {
        return idPasajero;
    }

    public void setIdPasajero(String idPasajero) {
        this.idPasajero = idPasajero;
    }
}
