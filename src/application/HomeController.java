package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage; 

public class HomeController {

    @FXML
    private void irNuevaEntrada(ActionEvent event) {
        try {
            // Carga la vista de Datos del Cliente
            Parent root = FXMLLoader.load(getClass().getResource("entradaCliente.fxml"));
            
            // Obtiene el escenario (Stage) actual a partir del botón presionado
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Configura la nueva escena y la muestra
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar datosCliente.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void irGestionar(ActionEvent event) {
        try {
            // Carga la vista de Monitoreo/Gestión de Equipos
            Parent root = FXMLLoader.load(getClass().getResource("gestionEquipos.fxml"));
            
            // Obtiene el escenario (Stage) actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Configura la nueva escena y la muestra
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar monitoreoEquipos.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}