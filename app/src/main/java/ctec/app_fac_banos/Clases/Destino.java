package ctec.app_fac_banos.Clases;

public class Destino {

    private String ciudadDestino;// string Ciudad destino Libro
    private Integer cantidadTiquetes;// integer Cantidad de tiquetes vendidos para ese destino
    private Double tarifa;// double Valor del destino
    private Double valorTotalDestino;// double Cantidad multiplicado por tarifa

    public Destino() {
    }

    public String getCiudadDestino() {
        return ciudadDestino;
    }

    public void setCiudadDestino(String ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    public Integer getCantidadTiquetes() {
        return cantidadTiquetes;
    }

    public void setCantidadTiquetes(Integer cantidadTiquetes) {
        this.cantidadTiquetes = cantidadTiquetes;
    }

    public Double getTarifa() {
        return tarifa;
    }

    public void setTarifa(Double tarifa) {
        this.tarifa = tarifa;
    }

    public Double getValorTotalDestino() {
        return valorTotalDestino;
    }

    public void setValorTotalDestino(Double valorTotalDestino) {
        this.valorTotalDestino = valorTotalDestino;
    }
}
