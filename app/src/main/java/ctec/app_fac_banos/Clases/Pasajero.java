package ctec.app_fac_banos.Clases;

public class Pasajero {

    private Integer intNumeroTiquete; //Numero de tiquete despues de grabar
    private Integer intIdPuesto; // Puesto que se pretende vender
    private Integer strIdPasajero;
    private String strNombres;// Nombres del pasajero
    private String strApellidoUno;// Primer Apellido Pasajero
    private String strApellidoDos;// Segundo Apellido Pasajero
    private Integer  intIdRutaVendida;// Ruta que se vende al pasajero
    private Integer intIdServicioVendido ;// Servicio asociado a la rut
    private String strcodigoTiquete ; // Codigo Impresion Tiquete
    private Double dblValorTiquete ; // Valor tiquete pasajero

    //DetallePasajero


    public Pasajero() {
    }

    public Pasajero(Integer intNumeroTiquete,Integer intIdPuesto, Integer stridPasajero, String strNombres, String strApellidoUno, String strApellidoDos,
                    Integer intIdRutaVendida, Integer intIdServicioVendido,String strcodigoTiquete,Double dblValorTiquete) {
        this.intNumeroTiquete = intNumeroTiquete;
        this.intIdPuesto = intIdPuesto;
        this.strIdPasajero = stridPasajero;
        this.strNombres = strNombres;
        this.strApellidoUno = strApellidoUno;
        this.strApellidoDos = strApellidoDos;
        this.intIdRutaVendida = intIdRutaVendida;
        this.intIdServicioVendido = intIdServicioVendido;
        this.strcodigoTiquete = strcodigoTiquete;
        this.dblValorTiquete = dblValorTiquete;
    }

    public Double getDblValorTiquete() {
        return dblValorTiquete;
    }

    public void setDblValorTiquete(Double dblValorTiquete) {
        this.dblValorTiquete = dblValorTiquete;
    }

    public String getStrcodigoTiquete() {
        return strcodigoTiquete;
    }

    public void setStrcodigoTiquete(String strcodigoTiquete) {
        this.strcodigoTiquete = strcodigoTiquete;
    }

    public Integer getIntNumeroTiquete() {
        return intNumeroTiquete;
    }

    public void setIntNumeroTiquete(Integer intNumeroTiquete) {
        this.intNumeroTiquete = intNumeroTiquete;
    }

    public Integer getIntIdPuesto (){
        return this.intIdPuesto;
    }

    public void setIntIdPuesto(int intIdPuesto) {
        this.intIdPuesto = intIdPuesto;
    }

    public Integer getStridPasajero() {
        return strIdPasajero;
    }

    public void setStridPasajero(int stridPasajero) {
        this.strIdPasajero = stridPasajero;
    }

    public String getStrNombres() {
        return strNombres;
    }

    public void setStrNombres(String strNombres) {
        this.strNombres = strNombres;
    }

    public String getStrApellidoUno() {
        return strApellidoUno;
    }

    public void setStrApellidoUno(String strApellidoUno) {
        this.strApellidoUno = strApellidoUno;
    }

    public String getStrApellidoDos() {
        return strApellidoDos;
    }

    public void setStrApellidoDos(String strApellidoDos) {
        this.strApellidoDos = strApellidoDos;
    }

    public Integer getIntIdRutaVendida() {
        return intIdRutaVendida;
    }

    public void setIntIdRutaVendida(int intIdRutaVendida) {
        this.intIdRutaVendida = intIdRutaVendida;
    }

    public Integer getIntIdServicioVendido() {
        return intIdServicioVendido;
    }

    public void setIntIdServicioVendido(int intIdServicioVendido) {
        this.intIdServicioVendido = intIdServicioVendido;
    }
}
