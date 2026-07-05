package application;

public class OrdenServicio {
    private int idOrden;
    private String nombreCliente; // 🔄 Cambiado de cedulaCliente a nombreCliente
    private String marcaModelo;
    private String serial;
    private String tipoEquipo;
    private String ingresaCargador; // 🔄 Cambiado a String para mostrar "Sí" o "No" directamente
    private String respaldarInfo;   // 🔄 Cambiado a String para mostrar "Sí" o "No" directamente
    private String razonRevision;
    private String observaciones;
    private String estado;
    private String fechaIngreso;

    // Constructor actualizado
    public OrdenServicio(int idOrden, String nombreCliente, String marcaModelo, String serial, String tipoEquipo,
                         String ingresaCargador, String respaldarInfo, String razonRevision, String observaciones,
                         String estado, String fechaIngreso) {
        this.idOrden = idOrden;
        this.nombreCliente = nombreCliente;
        this.marcaModelo = marcaModelo;
        this.serial = serial;
        this.tipoEquipo = tipoEquipo;
        this.ingresaCargador = ingresaCargador;
        this.respaldarInfo = respaldarInfo;
        this.razonRevision = razonRevision;
        this.observaciones = observaciones;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
    }

    // Getters actualizados
    public int getIdOrden() { return idOrden; }
    public String getNombreCliente() { return nombreCliente; } // 🔄 Getter actualizado
    public String getMarcaModelo() { return marcaModelo; }
    public String getSerial() { return serial; }
    public String getTipoEquipo() { return tipoEquipo; }
    public String getIngresaCargador() { return ingresaCargador; } // 🔄 Getter actualizado
    public String getRespaldarInfo() { return respaldarInfo; }     // 🔄 Getter actualizado
    public String getRazonRevision() { return razonRevision; }
    public String getObservaciones() { return observaciones; }
    public String getEstado() { return estado; }
    public String getFechaIngreso() { return fechaIngreso; }
}