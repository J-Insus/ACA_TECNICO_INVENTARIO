package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class GestionEquiposController {

    @FXML private TextField txtfieldBuscar;
    @FXML private CheckBox checkPendientes;
    @FXML private CheckBox checkRevision;
    @FXML private CheckBox checkListos;
    @FXML private TableView<OrdenServicio> tblVistaTabla;
    @FXML private Button btnDelete;

    // COLUMNAS ACTUALIZADAS SEGÚN TUS NUEVOS REQUERIMIENTOS
    @FXML private TableColumn<OrdenServicio, Integer> colId;
    @FXML private TableColumn<OrdenServicio, String> colNombre; // 🔄 Cambiado de colCedula a colNombre
    @FXML private TableColumn<OrdenServicio, String> colMarca;
    @FXML private TableColumn<OrdenServicio, String> colSerial;
    @FXML private TableColumn<OrdenServicio, String> colTipo;
    @FXML private TableColumn<OrdenServicio, String> colCargador; // New
    @FXML private TableColumn<OrdenServicio, String> colRespaldo; // New
    @FXML private TableColumn<OrdenServicio, String> colRazon;    // New
    @FXML private TableColumn<OrdenServicio, String> colEstado;

    private ObservableList<OrdenServicio> listaOrdenes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Enlazar las columnas con las propiedades del nuevo OrdenServicio.java
        colId.setCellValueFactory(new PropertyValueFactory<>("idOrden"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCliente")); // Mapea getNombreCliente()
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marcaModelo"));
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoEquipo"));
        colCargador.setCellValueFactory(new PropertyValueFactory<>("ingresaCargador"));
        colRespaldo.setCellValueFactory(new PropertyValueFactory<>("respaldarInfo"));
        colRazon.setCellValueFactory(new PropertyValueFactory<>("razonRevision"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        checkPendientes.setSelected(true);
        checkRevision.setSelected(true);
        checkListos.setSelected(true);

        cargarDatos();

        txtfieldBuscar.textProperty().addListener((observable, oldValue, newValue) -> cargarDatos());
        checkPendientes.selectedProperty().addListener((observable, oldValue, newValue) -> cargarDatos());
        checkRevision.selectedProperty().addListener((observable, oldValue, newValue) -> cargarDatos());
        checkListos.selectedProperty().addListener((observable, oldValue, newValue) -> cargarDatos());
    }

    private void cargarDatos() {
        listaOrdenes.clear();
        String busqueda = txtfieldBuscar.getText().trim();

        StringBuilder estadosFiltro = new StringBuilder();
        if (checkPendientes.isSelected()) estadosFiltro.append("'Pendiente',");
        if (checkRevision.isSelected()) estadosFiltro.append("'En Revision',");
        if (checkListos.isSelected()) estadosFiltro.append("'Listo',");

        if (estadosFiltro.length() > 0) {
            estadosFiltro.setLength(estadosFiltro.length() - 1);
        } else {
            estadosFiltro.append("''");
        }

        // 💡 CONSULTA AVANZADA: INNER JOIN para traer el nombre del cliente y no la cédula
        String sql = "SELECT o.id_orden, c.nombre AS nombre_cliente, o.marca_modelo, o.serial, o.tipo_equipo, "
                   + "o.ingresa_cargador, o.respaldar_info, o.razon_revision, o.observaciones, o.estado, o.fecha_ingreso "
                   + "FROM ordenes_servicio o "
                   + "INNER JOIN clientes c ON o.cedula_cliente = c.cedula "
                   + "WHERE o.estado IN (" + estadosFiltro + ") "
                   + "AND (c.nombre LIKE ? OR o.serial LIKE ? OR o.marca_modelo LIKE ?)";

        try (Connection con = ConexionSQL.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String comodin = "%" + busqueda + "%";
            ps.setString(1, comodin);
            ps.setString(2, comodin);
            ps.setString(3, comodin);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Convertir los valores booleanos (1 o 0) en "Sí" o "No" de forma elegante
                String traeCargador = rs.getBoolean("ingresa_cargador") ? "Sí" : "No";
                String necesitaRespaldo = rs.getBoolean("respaldar_info") ? "Sí" : "No";

                listaOrdenes.add(new OrdenServicio(
                    rs.getInt("id_orden"),
                    rs.getString("nombre_cliente"), // Extraído del JOIN de clientes
                    rs.getString("marca_modelo"),
                    rs.getString("serial"),
                    rs.getString("tipo_equipo"),
                    traeCargador,
                    necesitaRespaldo,
                    rs.getString("razon_revision"),
                    rs.getString("observaciones"),
                    rs.getString("estado"),
                    rs.getString("fecha_ingreso")
                ));
            }
            tblVistaTabla.setItems(listaOrdenes);

        } catch (SQLException e) {
            System.err.println("❌ Error al consultar y unir las tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void cambiarAEnRevision(ActionEvent event) {
        actualizarEstadoSeleccionado("En Revision");
    }

    @FXML
    public void cambiarAListo(ActionEvent event) {
        actualizarEstadoSeleccionado("Listo");
    }

    private void actualizarEstadoSeleccionado(String nuevoEstado) {
        OrdenServicio seleccionada = tblVistaTabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección Requerida", "Por favor, seleccione un equipo de la tabla.", AlertType.WARNING);
            return;
        }

        String sql = "UPDATE ordenes_servicio SET estado = ? WHERE id_orden = ?";
        try (Connection con = ConexionSQL.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, seleccionada.getIdOrden());
            ps.executeUpdate();

            System.out.println("🔄 Estado actualizado con éxito a: " + nuevoEstado);
            cargarDatos(); 

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void irHome(ActionEvent event) {
        cambiarPantalla(event, "/application/home.fxml");
    }

    @FXML
    public void irEditarRegistro(ActionEvent event) {
        OrdenServicio seleccionada = tblVistaTabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selección Requerida", "Seleccione un registro para modificar.", AlertType.WARNING);
            return;
        }
        ConexionSQL.cedulaClienteActual = String.valueOf(seleccionada.getIdOrden()); 
        cambiarPantalla(event, "/application/editarRegistro.fxml");
    }

    private void cambiarPantalla(ActionEvent event, String rutaFxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(rutaFxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Error al cambiar a la vista: " + rutaFxml);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    @FXML
    public void eliminarRegistro(ActionEvent event) {
        // 1. Obtener la orden seleccionada en la tabla
        OrdenServicio seleccionada = tblVistaTabla.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            mostrarAlerta("Selección Requerida", "Por favor, seleccione un registro de la tabla para poder eliminarlo.", AlertType.WARNING);
            return;
        }

        // 2. Alerta de confirmación (Buenas prácticas de UX/UI)
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de que desea eliminar por completo la Orden #" + seleccionada.getIdOrden() + "? Esta acción no se puede deshacer.");
        
        // Esperar la respuesta del usuario
        java.util.Optional<javafx.scene.control.ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.OK) {
            
            // 3. Sentencia SQL para borrar físicamente el registro de la orden
            String sql = "DELETE FROM ordenes_servicio WHERE id_orden = ?";
            
            try (Connection con = ConexionSQL.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, seleccionada.getIdOrden());
                ps.executeUpdate();
                
                System.out.println("🗑️ [Base de Datos] Orden #" + seleccionada.getIdOrden() + " eliminada con éxito.");
                
                // 4. Refrescar la interfaz eliminando el ítem de la lista visible directamente
                listaOrdenes.remove(seleccionada);
                
                mostrarAlerta("Registro Eliminado", "La orden de servicio ha sido removida del sistema correctamente.", AlertType.INFORMATION);
                
            } catch (SQLException e) {
                System.err.println("❌ Error SQL al intentar eliminar la orden: " + e.getMessage());
                mostrarAlerta("Error de Servidor", "No se pudo eliminar el registro de la base de datos local.", AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
}