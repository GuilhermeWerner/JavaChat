import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clients = new ArrayList<>();
    }

    public void start() throws IOException {
        System.out.println("Listening on port: " + serverSocket.getLocalPort());

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

            ClientHandler handler = new ClientHandler(clientSocket, this);
            clients.add(handler);

            new Thread(handler).start();
        }
    }

    public void broadcast(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(4321);
        server.start();
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket clientSocket, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        writer = new PrintWriter(clientSocket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        try {
            String message = clientSocket.getRemoteSocketAddress().toString() + ": ";

            while ((message += reader.readLine()) != null) {
                server.broadcast(message, this);
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e);
        } finally {
            server.removeClient(this);

            try {
                reader.close();
                writer.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error disconnecting: " + e);
            }
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
