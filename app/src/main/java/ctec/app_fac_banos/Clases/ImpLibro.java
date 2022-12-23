package ctec.app_fac_banos.Clases;

import java.util.ArrayList;

public class ImpLibro {

    private String fechaSalida;
    private String horaSalida;
    private String movil;
    private String placa;
    private String conductor;
    private String estadoLibro;
    private String origen;
    private String destino;
    private String fechaCierre;
    private Double valorTotalLibro;
    private Double valorTotalDevoluciones;
    private ArrayList<Destino> destinos;
    private ArrayList<DetallePasajero> pasajeros;

    public ImpLibro() {
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getEstadoLibro() {
        return estadoLibro;
    }

    public void setEstadoLibro(String estadoLibro) {
        this.estadoLibro = estadoLibro;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public Double getValorTotalLibro() {
        return valorTotalLibro;
    }

    public void setValorTotalLibro(Double valorTotalLibro) {
        this.valorTotalLibro = valorTotalLibro;
    }

    public Double getValorTotalDevoluciones() {
        return valorTotalDevoluciones;
    }

    public void setValorTotalDevoluciones(Double valorTotalDevoluciones) {
        this.valorTotalDevoluciones = valorTotalDevoluciones;
    }

    public ArrayList<Destino> getDestinos() {
        return destinos;
    }

    public void setDestinos(ArrayList<Destino> destinos) {
        this.destinos = destinos;
    }

    public ArrayList<DetallePasajero> getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(ArrayList<DetallePasajero> pasajeros) {
        this.pasajeros = pasajeros;
    }
}
