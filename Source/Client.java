import java.io.*;
import java.net.*;

public class Client {
    private Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    public Client(String serverAddress, int serverPort) throws IOException {
        clientSocket = new Socket(serverAddress, serverPort);
        writer = new PrintWriter(clientSocket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void start() throws IOException {
        System.out.println("Connected to server: " + clientSocket.getRemoteSocketAddress());

        new Thread(() -> {
            try {
                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    System.out.println(inputLine);
                }
            } catch (IOException e) {
                System.out.println("Error receiving message: " + e);
            }
        }).start();

        BufferedReader stdio = new BufferedReader(new InputStreamReader(System.in));
        String message;

        while ((message = stdio.readLine()) != null) {
            writer.println(message);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 4321);
        client.start();
    }
}
