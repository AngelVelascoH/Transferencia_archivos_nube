import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.Scanner;

public class server {
    private static final String KEYSTORE_LOCATION = "ssl2/server.jks";
    private static final String KEYSTORE_PASSWORD = "mipassword";
    private static final int BUFFER_SIZE = 1024;
    private static final String OK_RESPONSE = "OK\n";
    private static final String ERROR_RESPONSE = "ERROR\n";

    public static void main(String[] args) throws Exception {
        SSLServerSocketFactory sslServerSocketFactory = createSSLServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(50000);

        System.out.println("Server started.");
        System.out.println("Listening for connections...");

        while (true) {
            try {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("Client connected.");

                Thread t = new Thread(() -> {
                    try {
                        handleConnection(sslSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleConnection(SSLSocket sslSocket) throws IOException {
        InputStream is = sslSocket.getInputStream();
        OutputStream os = sslSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();

        if (line == null) {
            return;
        }

        String[] parts = line.split(" ");
        String command = parts[0];

        if (command.equalsIgnoreCase("GET")) {
            String fileName = parts[1];

            File file = new File(fileName);

            if (file.exists() && file.isFile()) {
                // Enviar la respuesta OK con la longitud y contenido del archivo
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                byte[] buffer = new byte[(int) file.length()];
                bis.read(buffer, 0, buffer.length);
                os.write(OK_RESPONSE.getBytes());
                os.write(Long.toString(file.length()).getBytes());
                os.write('\n');
                os.write(buffer, 0, buffer.length);
                os.flush();
            } else {
                // Enviar la respuesta ERROR
                os.write(ERROR_RESPONSE.getBytes());
                os.flush();
            }
        } else if (command.equalsIgnoreCase("PUT")) {
            String fileName = parts[1];
            long fileSize = Long.parseLong(parts[2]);

            File file = new File(fileName);

            if (!file.exists() && fileSize > 0) {
                // Crear el archivo y escribir en él
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                int totalBytesRead = 0;

                while (totalBytesRead < fileSize && (bytesRead = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    bos.flush();
                    totalBytesRead += bytesRead;
                }

                if (totalBytesRead == fileSize) {
                    // Enviar la respuesta OK
                    os.write(OK_RESPONSE.getBytes());
                    os.flush();
                } else {
                    // Enviar la respuesta ERROR
                    os.write(ERROR_RESPONSE.getBytes());
                    os.flush();
                }
                bos.close();
            } else {
                // Enviar la respuesta ERROR
                os.write(ERROR_RESPONSE.getBytes());
                os.flush();
            }
        }
    }

    private static SSLServerSocketFactory createSSLServerSocketFactory() throws Exception {
    KeyStore keyStore = KeyStore.getInstance("JKS");
    InputStream inputStream = new FileInputStream(KEYSTORE_LOCATION);
    keyStore.load(inputStream, KEYSTORE_PASSWORD.toCharArray());

    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
    keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
    trustManagerFactory.init(keyStore);

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

    return sslContext.getServerSocketFactory();
}
}


