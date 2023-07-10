import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class cliente2 {
    private static final String TRUSTSTORE_PATH = "server.jks"; // Ruta del truststore
    private static final String TRUSTSTORE_PASSWORD = "mipassword"; // Contraseña del truststore

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Uso: java ClientGET <IP_servidor> <puerto_servidor> <nombre_archivo>");
            System.exit(1);
        }

        String serverHost = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        // Cargar truststore
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream(TRUSTSTORE_PATH), TRUSTSTORE_PASSWORD.toCharArray());

        // Crear TrustManagerFactory y SSLContext
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        // Crear socket seguro
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverHost, serverPort);

        // Establecer modo de autenticación del servidor
        sslSocket.setNeedClientAuth(true);

        // Obtener streams de entrada y salida del socket
        OutputStream os = sslSocket.getOutputStream();
        InputStream is = sslSocket.getInputStream();

        // Enviar petición GET y nombre de archivo
        String request = "GET " + fileName + " HTTP/1.1\r\n";
        os.write(request.getBytes());
        os.flush();

        // Leer respuesta del servidor
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String response = reader.readLine();
        if (response.equals("OK")) {
            // Leer longitud del archivo
            long fileSize = Long.parseLong(reader.readLine());

            // Leer contenido del archivo y escribir en disco local
            byte[] buffer = new byte[8192];
            int bytesRead;
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            while ((bytesRead = is.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                if (bytesRead == fileSize) {
                    break;
                }

            }
            System.out.println("El archivo " + fileName + " se ha recibido con éxito.");
            fileOutputStream.close();

            
        } else {
            System.err.println("No se pudo recibir el archivo " + fileName + ".");
        }

        // Cerrar socket
        sslSocket.close();
    }
}
