package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EditarRegistroController {

    @FXML
    private void guardarYVolverGestion(ActionEvent event) {
        try {
            // Aquí se conectará el UPDATE de SQL más adelante.
            // Por ahora, realiza la redirección automática a la gestión de equipos.
            Parent root = FXMLLoader.load(getClass().getResource("/application/gestionEquipos.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar gestionEquipos.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}