package ctec.app_fac_banos.Clases;

public class Puesto {

    private Integer intIdPuesto;//  Numero del puesto
    private Boolean estadoPuesto;//  0 Libre -
    private String infPuesto;

    public Puesto() {
    }

    public Puesto(Integer intIdPuesto, Boolean estadoPuesto,String infPuesto) {
        this.intIdPuesto = intIdPuesto;
        this.estadoPuesto = estadoPuesto;
        this.infPuesto = infPuesto;
    }

    public String getInfPuesto() {
        return infPuesto;
    }

    public void setInfPuesto(String infPuesto) {
        this.infPuesto = infPuesto;
    }

    public Integer getIntIdPuesto() {
        return intIdPuesto;
    }

    public void setIntIdPuesto(Integer intIdPuesto) {
        this.intIdPuesto = intIdPuesto;
    }

    public Boolean getEstadoPuesto() {
        return estadoPuesto;
    }

    public void setEstadoPuesto(Boolean estadoPuesto) {
        this.estadoPuesto = estadoPuesto;
    }
}
