package ctec.app_fac_banos.Clases;


public class LibroViaje {

    private String strMovil;//  # del movil asociado al Libro
    private Integer intIdRuta;//  Nro de la ruta asociada al Libro
    private String strDescripcionRuta;//  Descipcion de la Ruta
    private String dtmFechaSalida;// Fecha de salida del vehiculo (yyyy-MM-dd)
    private String strHoraSalida;//  Hora de salida del vehiculo (hh:mm)
    private Integer intIdLibro;//  Numero del Libro de Viaje
    private String strTipoLibro;//  Tipo de Libro (GE)
    private Integer intRutaPrincipal;//  Id de la ruta principal
    private Integer intPuestosDisponibles;//  Numero de puestos disponibles del libro
    private Double fltTarifaRuta;//  Tarifa asociada a la ruta y el servicio
    private Integer intPuestos;//  Numero de puestos del movil
    private String strIdServicio;//  Numero asociado al el servicio y ruta
    private Puesto puestosLibres[];//  Lista del objeto Puesto
    private String estadoLibro;//  Numero asociado al el servicio y ruta

    public LibroViaje() {
    }

    public LibroViaje(String strMovil, Integer intIdRuta, String strDescripcionRuta, String dtmFechaSalida,
                      String strHoraSalida, Integer intIdLibro, String strTipoLibro, Integer intRutaPrincipal,
                      Integer intPuestosDisponibles, Double fltTarifaRuta, Integer intPuestos, String strIdServicio,
                      Puesto[] puestosLibres,String estadoLibro) {
        this.strMovil = strMovil;
        this.intIdRuta = intIdRuta;
        this.strDescripcionRuta = strDescripcionRuta;
        this.dtmFechaSalida = dtmFechaSalida;
        this.strHoraSalida = strHoraSalida;
        this.intIdLibro = intIdLibro;
        this.strTipoLibro = strTipoLibro;
        this.intRutaPrincipal = intRutaPrincipal;
        this.intPuestosDisponibles = intPuestosDisponibles;
        this.fltTarifaRuta = fltTarifaRuta;
        this.intPuestos = intPuestos;
        this.strIdServicio = strIdServicio;
        this.puestosLibres = puestosLibres;
        this.estadoLibro = estadoLibro;
    }

    public String getEstadoLibro() {
        return estadoLibro;
    }

    public void setEstadoLibro(String estadoLibro) {
        this.estadoLibro = estadoLibro;
    }

    public String getStrMovil() {
        return strMovil;
    }

    public void setStrMovil(String strMovil) {
        this.strMovil = strMovil;
    }

    public Integer getIntIdRuta() {
        return intIdRuta;
    }

    public void setIntIdRuta(Integer intIdRuta) {
        this.intIdRuta = intIdRuta;
    }

    public String getStrDescripcionRuta() {
        return strDescripcionRuta;
    }

    public void setStrDescripcionRuta(String strDescripcionRuta) {
        strDescripcionRuta = strDescripcionRuta;
    }

    public String getDtmFechaSalida() {
        return dtmFechaSalida;
    }

    public void setDtmFechaSalida(String dtmFechaSalida) {
        this.dtmFechaSalida = dtmFechaSalida;
    }

    public String getStrHoraSalida() {
        return strHoraSalida;
    }

    public void setStrHoraSalida(String strHoraSalida) {
        strHoraSalida = strHoraSalida;
    }

    public Integer getIntIdLibro() {
        return intIdLibro;
    }

    public void setIntIdLibro(Integer intIdLibro) {
        intIdLibro = intIdLibro;
    }

    public String getStrTipoLibro() {
        return strTipoLibro;
    }

    public void setStrTipoLibro(String strTipoLibro) {
        strTipoLibro = strTipoLibro;
    }

    public Integer getIntRutaPrincipal() {
        return intRutaPrincipal;
    }

    public void setIntRutaPrincipal(Integer intRutaPrincipal) {
        intRutaPrincipal = intRutaPrincipal;
    }

    public Integer getIntPuestosDisponibles() {
        return intPuestosDisponibles;
    }

    public void setIntPuestosDisponibles(Integer intPuestosDisponibles) {
        intPuestosDisponibles = intPuestosDisponibles;
    }

    public Double getFltTarifaRuta() {
        return fltTarifaRuta;
    }

    public void setFltTarifaRuta(Double fltTarifaRuta) {
        fltTarifaRuta = fltTarifaRuta;
    }

    public Integer getIntPuestos() {
        return intPuestos;
    }

    public void setIntPuestos(Integer intPuestos) {
        intPuestos = intPuestos;
    }

    public String getStrIdServicio() {
        return strIdServicio;
    }

    public void setStrIdServicio(String strIdServicio) {
        strIdServicio = strIdServicio;
    }

    public Puesto[] getPuestosLibres() {
        return puestosLibres;
    }

    public void setPuestosLibres(Puesto[] puestosLibres) {
        puestosLibres = puestosLibres;
    }

}
