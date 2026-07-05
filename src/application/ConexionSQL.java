package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {
	
	public static String cedulaClienteActual = "";

    // Parámetros de configuración de la base de datos en XAMPP
    private static final String URL = "jdbc:mysql://localhost:3306/flowdesk_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = ""; // En XAMPP por defecto viene vacía

    /**
     * Método estático para obtener la conexión a la base de datos.
     * @return Objeto Connection listo para ejecutar instrucciones SQL.
     */
    public static Connection conectar() {
        Connection conexion = null;
        try {
            // Registrar explícitamente el Driver de MySQL (opcional en versiones modernas, pero recomendado)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer el puente de comunicación
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("🎉 [Éxito] Conexión establecida correctamente con flowdesk_db.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ [Error] No se encontró el Driver JDBC de MySQL. Revisa el Build Path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ [Error] Falló la conexión a MySQL. ¿Está encendido XAMPP?");
            e.printStackTrace();
        }
        return conexion;
    }
}