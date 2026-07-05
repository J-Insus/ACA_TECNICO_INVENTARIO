package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class DatosEquipoController {

    // VARIABLES EXACTAS DE TU DOCUMENTACIÓN
    @FXML private TextField txtfieldMarcaM;
    @FXML private TextField txtfieldSerial;
    @FXML private ComboBox<String> comboTipoEquipo;
    @FXML private CheckBox checkCargador;
    @FXML private RadioButton radioGsi;
    @FXML private RadioButton radioGno;
    @FXML private TextArea txtAreaRevision;
    @FXML private TextField txtfieldObservaciones;

    // Instancia para agrupar los RadioButtons en el código
    private ToggleGroup grupoPrioridad;

    /**
     * El método initialize() se ejecuta automáticamente al cargar la pantalla.
     * Ideal para llenar las opciones del ComboBox.
     */
    @FXML
    public void initialize() {
        // Llenar el combo de selección con las opciones del documento
        comboTipoEquipo.setItems(FXCollections.observableArrayList("Portátil", "De Mesa", "Impresora", "Otro"));
        
        // Agrupar los radios por código por si no se configuró en Scene Builder
        grupoPrioridad = new ToggleGroup();
        radioGsi.setToggleGroup(grupoPrioridad);
        radioGno.setToggleGroup(grupoPrioridad);
        radioGno.setSelected(true); // Seleccionado 'No' por defecto
    }

    @FXML
    private void guardarYVolverHome(ActionEvent event) {
        // 1. Recuperar la cédula del cliente guardada globalmente
        String cedulaCliente = ConexionSQL.cedulaClienteActual;

        // 2. Extraer datos de la interfaz
        String marcaModelo = txtfieldMarcaM.getText().trim();
        String serial = txtfieldSerial.getText().trim();
        String tipoEquipo = comboTipoEquipo.getValue();
        boolean ingresaCargador = checkCargador.isSelected();
        boolean respaldarInfo = radioGsi.isSelected(); // Si está marcado 'Si', es true
        String razonRevision = txtAreaRevision.getText().trim();
        String observaciones = txtfieldObservaciones.getText().trim();

        // 3. Validar campos obligatorios (según documento, observaciones puede ser nulo)
        if (marcaModelo.isEmpty() || serial.isEmpty() || tipoEquipo == null || razonRevision.isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Por favor llene todos los campos obligatorios del diagnóstico.", AlertType.WARNING);
            return;
        }

        // 4. Sentencia SQL de Inserción (El campo 'estado' se pone solo como 'Pendiente' por defecto)
        String sql = "INSERT INTO ordenes_servicio (cedula_cliente, marca_modelo, serial, tipo_equipo, "
                   + "ingresa_cargador, respaldar_info, razon_revision, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionSQL.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedulaCliente);
            ps.setString(2, marcaModelo);
            ps.setString(3, serial);
            ps.setString(4, tipoEquipo);
            ps.setBoolean(5, ingresaCargador); // Mapea automáticamente true->1 / false->0
            ps.setBoolean(6, respaldarInfo);   // Mapea automáticamente true->1 / false->0
            ps.setString(7, razonRevision);
            
            // Si las observaciones están vacías, mandamos NULL a la BD
            if (observaciones.isEmpty()) {
                ps.setNull(8, java.sql.Types.VARCHAR);
            } else {
                ps.setString(8, observaciones);
            }

            // Ejecutar inserción en XAMPP
            ps.executeUpdate();
            System.out.println("🎉 [Base de Datos] Orden de servicio creada con éxito para el serial: " + serial);
            
            // Limpiar la variable temporal por seguridad
            ConexionSQL.cedulaClienteActual = "";

            // 5. Redirección final al Home limpia flujos
            Parent root = FXMLLoader.load(getClass().getResource("/application/home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            mostrarAlerta("Registro Exitoso", "La orden de servicio ha sido guardada correctamente en el sistema.", AlertType.INFORMATION);

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar la orden de servicio: " + e.getMessage());
            mostrarAlerta("Error de Transacción", "No se pudo registrar la orden. Verifique los datos de conexión.", AlertType.ERROR);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ Error al regresar a la pantalla principal: " + e.getMessage());
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
}