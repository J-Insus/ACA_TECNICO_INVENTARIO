package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class EditarRegistroController {

    // VARIABLES DEL CLIENTE (PÁGINA 7 DEL DOCUMENTO)
    @FXML private TextField modNombreCliente;
    @FXML private TextField modCedula;
    @FXML private TextField modTelefono;

    // VARIABLES DEL EQUIPO (PÁGINA 7 DEL DOCUMENTO)
    @FXML private TextField modMarcaM;
    @FXML private TextField modSerial;
    @FXML private ComboBox<String> modTipoEquipo;
    @FXML private CheckBox modCargador;
    @FXML private RadioButton modSi;
    @FXML private RadioButton modNo;
    @FXML private TextArea modRevision;
    @FXML private TextField modObervaciones; // Nombre con 'b' según el PDF

    private ToggleGroup grupoModRespaldo;
    private int idOrdenActual;

    /**
     * Carga de forma conjunta los datos del cliente y del dispositivo usando INNER JOIN
     */
    @FXML
    public void initialize() {
        modTipoEquipo.setItems(FXCollections.observableArrayList("Portátil", "De Mesa", "Impresora", "Otro"));
        grupoModRespaldo = new ToggleGroup();
        modSi.setToggleGroup(grupoModRespaldo);
        modNo.setToggleGroup(grupoModRespaldo);

        if (ConexionSQL.cedulaClienteActual == null || ConexionSQL.cedulaClienteActual.isEmpty()) {
            System.err.println("❌ No se recibió un ID de orden válido.");
            return;
        }
        
        idOrdenActual = Integer.parseInt(ConexionSQL.cedulaClienteActual);

        // CONSULTA INTEGRAL: Une ordenes_servicio con clientes mediante la cédula
        String sql = "SELECT o.marca_modelo, o.serial, o.tipo_equipo, o.ingresa_cargador, o.respaldar_info, "
                   + "o.razon_revision, o.observaciones, c.cedula, c.nombre, c.telefono "
                   + "FROM ordenes_servicio o "
                   + "INNER JOIN clientes c ON o.cedula_cliente = c.cedula "
                   + "WHERE o.id_orden = ?";

        try (Connection con = ConexionSQL.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrdenActual);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // 1. Rellenar Información del Cliente extraída de la BD
                modNombreCliente.setText(rs.getString("nombre"));
                modCedula.setText(rs.getString("cedula"));
                modCedula.setEditable(false); // La cédula se bloquea por ser llave primaria resguardando consistencia SQL
                modTelefono.setText(rs.getString("telefono"));

                // 2. Rellenar Información del Dispositivo
                modMarcaM.setText(rs.getString("marca_modelo"));
                modSerial.setText(rs.getString("serial"));
                modTipoEquipo.setValue(rs.getString("tipo_equipo"));
                modRevision.setText(rs.getString("razon_revision"));
                modObervaciones.setText(rs.getString("observaciones"));
                modCargador.setSelected(rs.getBoolean("ingresa_cargador"));
                
                if (rs.getBoolean("respaldar_info")) {
                    modSi.setSelected(true);
                } else {
                    modNo.setSelected(true);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al unificar datos en la carga: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Guarda las modificaciones tanto del cliente como del equipo y vuelve al panel general
     */
    @FXML
    private void guardarYVolverGestion(ActionEvent event) {
        // Datos del cliente
        String nuevoNombre = modNombreCliente.getText().trim();
        String cedula = modCedula.getText().trim();
        String nuevoTelefono = modTelefono.getText().trim();

        // Datos del equipo
        String nuevaMarca = modMarcaM.getText().trim();
        String nuevoSerial = modSerial.getText().trim();
        String nuevoTipo = modTipoEquipo.getValue();
        boolean nuevoCargador = modCargador.isSelected();
        boolean nuevoRespaldo = modSi.isSelected();
        String nuevaRevision = modRevision.getText().trim();
        String nuevasObservaciones = modObervaciones.getText().trim();

        if (nuevoNombre.isEmpty() || nuevoTelefono.isEmpty() || nuevaMarca.isEmpty() || nuevoSerial.isEmpty() || nuevoTipo == null || nuevaRevision.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "Por favor, complete todos los campos obligatorios.", AlertType.WARNING);
            return;
        }
        
        
     // 🔒 RESTRICCIÓN: Validar que el teléfono contenga solo números
        if (!nuevoTelefono.matches("\\d+")) {
            mostrarAlerta("Formato Incorrecto", "El campo Teléfono solo puede contener números, no se permiten letras ni caracteres especiales.", AlertType.WARNING);
            return;
        }

        // Sentencias SQL de actualización secuencial
        String sqlCliente = "UPDATE clientes SET nombre = ?, telefono = ? WHERE cedula = ?";
        String sqlEquipo = "UPDATE ordenes_servicio SET marca_modelo = ?, serial = ?, tipo_equipo = ?, "
                         + "ingresa_cargador = ?, respaldar_info = ?, razon_revision = ?, observaciones = ? WHERE id_orden = ?";

        try (Connection con = ConexionSQL.conectar()) {
            // Desactivar el autocommit para realizar una transacción segura (Todo o nada)
            con.setAutoCommit(false);

            try (PreparedStatement psCliente = con.prepareStatement(sqlCliente);
                 PreparedStatement psEquipo = con.prepareStatement(sqlEquipo)) {
                
                // Transacción 1: Actualizar Cliente
                psCliente.setString(1, nuevoNombre);
                psCliente.setString(2, nuevoTelefono);
                psCliente.setString(3, cedula);
                psCliente.executeUpdate();

                // Transacción 2: Actualizar Dispositivo
                psEquipo.setString(1, nuevaMarca);
                psEquipo.setString(2, nuevoSerial);
                psEquipo.setString(3, nuevoTipo);
                psEquipo.setBoolean(4, nuevoCargador);
                psEquipo.setBoolean(5, nuevoRespaldo);
                psEquipo.setString(6, nuevaRevision);
                
                if (nuevasObservaciones.isEmpty()) {
                    psEquipo.setNull(7, java.sql.Types.VARCHAR);
                } else {
                    psEquipo.setString(7, nuevasObservaciones);
                }
                psEquipo.setInt(8, idOrdenActual);
                psEquipo.executeUpdate();

                // Confirmar los cambios combinados en XAMPP
                con.commit();
                System.out.println("🎉 [Éxito Relacional] Datos de cliente y equipo actualizados en conjunto.");
                
            } catch (SQLException e) {
                con.rollback(); // Si algo falla, revierte los cambios para proteger la integridad
                throw e;
            }

            ConexionSQL.cedulaClienteActual = "";

            // Redirección a gestionEquipos
            Parent root = FXMLLoader.load(getClass().getResource("/application/gestionEquipos.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            mostrarAlerta("Actualización Completada", "Los datos generales de la ficha técnica han sido modificados.", AlertType.INFORMATION);

        } catch (SQLException e) {
            System.err.println("❌ Error en la transacción combinada: " + e.getMessage());
            mostrarAlerta("Error de Servidor", "No se pudieron guardar los cambios relacionales.", AlertType.ERROR);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ Error de navegación: " + e.getMessage());
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