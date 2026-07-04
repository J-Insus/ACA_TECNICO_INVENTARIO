package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// CORRECCIÓN: Añadida la extensión .fxml y una barra diagonal '/' al inicio para la ruta relativa correcta
			Parent root = FXMLLoader.load(getClass().getResource("/application/home.fxml"));
			
			// Creamos la escena con el tamaño ideal para tu diseño
			Scene scene = new Scene(root, 1280, 720);
			
			// Vincular el archivo css por si decides meter estilos allí más adelante
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setTitle("FlowDesk - Control de Soporte Técnico");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false); // Evita que se deforme el diseño al maximizar si no quieres
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}