package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EntradaClienteController {

    // VINCULACIÓN EXACTA CON TU DOCUMENTACIÓN
    @FXML private TextField txtfieldCedula;
    @FXML private TextField txtfieldNombreCliente;
    @FXML private TextField txtfieldTelefono;
    

    @FXML
    private void irDatosEquipo(ActionEvent event) {
        // 1. Recolectar la información usando tus variables
        String cedula = txtfieldCedula.getText().trim();
        String nombre = txtfieldNombreCliente.getText().trim();
        String telefono = txtfieldTelefono.getText().trim();

        // 2. Validación obligatoria de campos
        if (cedula.isEmpty() || nombre.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Campos Obligatorios", "Por favor, complete todos los campos del cliente antes de continuar.", AlertType.WARNING);
            return; 
        }
        
     // 🔥 NUEVA VALIDACIÓN: Verificar que el teléfono solo contenga números
     // La expresión regular "\\d+" significa: "Asegúrate de que solo haya dígitos del 0 al 9"
     if (!telefono.matches("\\d+")) {
         mostrarAlerta("Formato Incorrecto", "El campo Teléfono solo debe contener números. No se permiten letras ni espacios.", AlertType.WARNING);
         return; // Detiene la ejecución y no guarda nada en la BD
     }
     
  // 🔒 RESTRICCIÓN ADICIONAL: Verificar que la cédula solo contenga números
     if (!cedula.matches("\\d+")) {
         mostrarAlerta("Formato Incorrecto", "El campo Cédula solo debe contener números. No se permiten letras, guiones ni espacios.", AlertType.WARNING);
         return; // Detiene la ejecución y evita procesar la solicitud
     }
        

        // 3. Sentencia SQL relacional (Inserta o actualiza si ya existe)
        String sql = "INSERT INTO clientes (cedula, nombre, telefono) VALUES (?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE nombre = ?, telefono = ?"; 

        try (Connection con = ConexionSQL.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, cedula);
            ps.setString(2, nombre);
            ps.setString(3, telefono);
            ps.setString(4, nombre); 
            ps.setString(5, telefono); 
            
            ps.executeUpdate();
            System.out.println("✅ [Base de Datos] Cliente registrado/actualizado con éxito: " + nombre);

            
         // Guarda la cédula en memoria global antes de cambiar de pantalla
            ConexionSQL.cedulaClienteActual = cedula;
            
            
            
            // 4. Navegación hacia la siguiente vista usando el archivo correcto
            Parent root = FXMLLoader.load(getClass().getResource("/application/datosEquipo.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar el cliente: " + e.getMessage());
            mostrarAlerta("Error de Base de Datos", "No se pudo registrar al cliente en el sistema.", AlertType.ERROR);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("❌ Error al cargar la siguiente vista (datosEquipo.fxml): " + e.getMessage());
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