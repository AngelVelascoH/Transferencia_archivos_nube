import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.file.Files;

public class cliente1 {
    public static void main(String[] args) throws Exception {
        // Obtener los argumentos de la línea de comandos
        if (args.length != 3) {
            System.err.println("Uso: java SecurePUTClient <servidor> <puerto> <archivo>");
            System.exit(1);
        }
        String servidor = args[0];
        int puerto = Integer.parseInt(args[1]);
        String archivo = args[2];

        // Verificar que el archivo existe
        if (!Files.exists(new File(archivo).toPath())) {
            System.err.println("Error: El archivo no existe");
            System.exit(1);
        }

        // Cargar el certificado del servidor para la validación del certificado del servidor
        System.setProperty("javax.net.ssl.trustStore", "server.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "mipassword");

        // Crear el socket seguro
        SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) sf.createSocket(servidor, puerto);
        System.out.println("Socket creado");

        // Verificar la cadena de certificados del servidor
        socket.startHandshake();

        // Crear los streams de entrada y salida
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();

        // Leer el contenido del archivo a enviar
        byte[] contenido = Files.readAllBytes(new File(archivo).toPath());

        // Enviar la petición PUT
        String mensaje = "PUT " + archivo + " " + contenido.length + "\r\n";
        os.write(mensaje.getBytes());
        os.write(contenido);
        os.flush();

        // Leer la respuesta del servidor
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String respuesta = br.readLine();

        // Cerrar los streams y el socket
        os.close();
        is.close();
        socket.close();

        // Verificar la respuesta del servidor
        if (respuesta.equals("OK")) {
            System.out.println("El archivo fue recibido por el servidor con éxito");
        } else {
            System.err.println("Error: El servidor no pudo escribir el archivo en el disco local");
        }
    }
}
