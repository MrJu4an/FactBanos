package ctec.app_fac_banos.Clases;

public class CuadreCaja {

    private String EstadoCaja;//  (“Abierta”,”Cerrada”)
    private DetalleCaja detallecaja[];// Lista de objetos DetalleCaja

    public CuadreCaja() {
    }

    public String getEstadoCaja() {
        return EstadoCaja;
    }

    public void setEstadoCaja(String estadoCaja) {
        EstadoCaja = estadoCaja;
    }

    public DetalleCaja[] getDetallecaja() {
        return detallecaja;
    }

    public void setDetallecaja(DetalleCaja[] detallecaja) {
        this.detallecaja = detallecaja;
    }
}
