import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;

public class DatabaseManager {

    private static final String ARCHIVO_CONFIG = "db.properties";

    private String url;
    private String usuario;
    private String contrasena;

    public DatabaseManager() {
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        Properties props = new Properties();
        File archivo = new File(ARCHIVO_CONFIG);

        if (!archivo.exists()) {
            crearConfiguracionPorDefecto(archivo);
        }

        try (FileInputStream fis = new FileInputStream(archivo)) {
            props.load(fis);

            String host = props.getProperty("db.host", "localhost");
            String port = props.getProperty("db.port", "3306");
            String name = props.getProperty("db.name", "escape_math");

            usuario = props.getProperty("db.user", "root");
            contrasena = props.getProperty("db.password", "");

            url = "jdbc:mysql://" + host + ":" + port + "/" + name
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("No se encontro el driver de MySQL: " + e.getMessage());
            }

        } catch (IOException e) {
            System.err.println("No se pudo leer db.properties: " + e.getMessage());
        }
    }

    private void crearConfiguracionPorDefecto(File archivo) {
        String contenido =
                "# Datos de conexion a MySQL para Escape Math\n" +
                "db.host=localhost\n" +
                "db.port=3306\n" +
                "db.name=escape_math\n" +
                "db.user=root\n" +
                "db.password=\n";

        try (FileWriter fw = new FileWriter(archivo)) {
            fw.write(contenido);
        } catch (IOException e) {
            System.err.println("No se pudo crear db.properties: " + e.getMessage());
        }
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    public String probarConexion() {
        try (Connection _ = conectar()) {
            return null;
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    public boolean guardarPuntuacion(String nombreUsuario, int puntaje, int retosCompletados, boolean gano) {
        String sql = "INSERT INTO puntuaciones (usuario, puntaje, retos_completados, resultado) VALUES (?, ?, ?, ?)";

        try (Connection c = conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);
            ps.setInt(2, puntaje);
            ps.setInt(3, retosCompletados);
            ps.setString(4, gano ? "GANO" : "PERDIO");

            int filas = ps.executeUpdate();

            System.out.println("Filas insertadas: " + filas);

            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar puntuacion: " + e.getMessage());
            return false;
        }
    }

    public List<Puntuacion> obtenerTop3() {
        List<Puntuacion> lista = new ArrayList<>();

        String sql = "SELECT usuario, puntaje, retos_completados, resultado, fecha " +
                "FROM puntuaciones " +
                "ORDER BY puntaje DESC, fecha ASC " +
                "LIMIT 3";

        try (Connection c = conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Puntuacion(
                        rs.getString("usuario"),
                        rs.getInt("puntaje"),
                        rs.getInt("retos_completados"),
                        rs.getString("resultado"),
                        String.valueOf(rs.getTimestamp("fecha"))
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el top 3: " + e.getMessage());
        }

        System.out.println("Top encontrados: " + lista.size());

        return lista;
    }
}