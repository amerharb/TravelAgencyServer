import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void start(int portNumber) {
        ServerSocket server = null;
        Socket client;

        // Create Server side socket
        try {
            server = new ServerSocket(portNumber);
        } catch (IOException ie) {
            System.out.println("Cannot open socket." + ie);
            System.exit(1);
        }
        System.out.println("ServerSocket is created " + server);

        // Wait for the data from the client and reply
        while (true) {
            try {
                // Listens for a connection to be made to this socket and accepts it.
                // The method blocks until a connection is made
                System.out.println("Waiting for connect request...");
                client = server.accept();
                System.out.println("Connect request is accepted...");
                String clientHost = client.getInetAddress().getHostAddress();
                int clientPort = client.getPort();
                System.out.println("Client host = " + clientHost + " Client port = " + clientPort);

                // Send Welcome message
                OutputStream clientOut = client.getOutputStream();
                PrintWriter pw = new PrintWriter(clientOut, true);
                String ansMsg = "Welcome to vacation planner! Our prices are as follows:\n" +
                        "The travel cost by ferry per person is 600 SEK. For air travel, it will be 900 SEK.\n" +
                        "Accommodation is 250 SEK per person per night. Meals cost 100 SEK per person per day.\n" +
                        "Please Enter number of travelers:-";
                pw.println(ansMsg);
                pw.flush();

                // Read data from the client
                InputStream clientIn = client.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(clientIn));
                String msgFromClient = br.readLine();
                System.out.println("Message received from client = " + msgFromClient);

                if (msgFromClient != null) {
                    // Send response to the client
                    if (!msgFromClient.equalsIgnoreCase("bye")) {
                        pw.println(ansMsg);
                    }

                    // Close sockets
                    if (msgFromClient.equalsIgnoreCase("bye")) {
                        server.close();
                        client.close();
                        break;
                    }
                }
            } catch (IOException ignored) {

            }
        }
    }

}
