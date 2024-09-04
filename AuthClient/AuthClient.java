import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class AuthClient {
    public static void main(String[] args) throws IOException {
        // Obtener URLs desde variables de entorno
        String authUrl = System.getenv("AUTH_URL");
        String greetingUrl = System.getenv("GREETING_URL");

        // Generar usuario y clave aleatorios
        String username = generateRandomString(8);
        String password = generateRandomString(12);

        // Autenticarse y obtener token
        String token = authenticate(authUrl, username, password);
        if (token != null) {
            // Invocar el servicio de saludo
            String url = greetingUrl + "?nombre=" + username;
            invokeGreetingService(url, token);
        }
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String authenticate(String authUrl, String username, String password) throws IOException {
        URL url = new URL(authUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Crear el cuerpo de la petición
        String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        // Enviar la petición
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Leer la respuesta
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                System.out.println("Respuesta de autenticación: " + response.toString());

                // Parsear JSON manualmente
                String token = response.toString();
                System.out.println("Token obtenido: " + token);
                return token;
            }
        } else {
            System.out.println("Error en la autenticación: " + responseCode);
            return null;
        }
    }


    private static void invokeGreetingService(String greetingUrl, String token) throws IOException {
        URL url = new URL(greetingUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        // Leer la respuesta
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Respuesta del servicio de saludo: " + response.toString());
            }
        } else {
            System.out.println("Error en el servicio de saludo: " + responseCode);
        }
    }
}
