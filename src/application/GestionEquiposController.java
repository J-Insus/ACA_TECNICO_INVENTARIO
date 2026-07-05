package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GestionEquiposController {

    @FXML
    private void irHome(ActionEvent event) {
        try {
            // Carga la vista principal del Home
            Parent root = FXMLLoader.load(getClass().getResource("/application/home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar home.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void irEditarRegistro(ActionEvent event) {
        try {
            // Carga la vista de edición de registros
            Parent root = FXMLLoader.load(getClass().getResource("/application/editarRegistro.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar editarRegistro.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}