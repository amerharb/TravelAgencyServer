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
                String welcomeMsg = "Welcome to vacation planner! Our prices are as follows:\n" +
                        "The travel cost by ferry per person is 600 SEK. For air travel, it will be 900 SEK.\n" +
                        "Accommodation is 250 SEK per person per night. Meals cost 100 SEK per person per day.\n";
                pw.println(welcomeMsg);
                pw.flush();

                String[] questions = new String[6];
                questions[0] = "Please Enter number of travelers: ";
                questions[1] = "Ferry or Air Travel";
                questions[2] = "Number of days";
                questions[3] = "Contact";
                questions[4] = "Phone number";
                questions[5] = "Address";

                String[] answers = new String[6];

                // Read data from the client
                InputStream clientIn = client.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(clientIn));
                for (int i = 0; i < 6; i++) {
                    pw.println(questions[i]);
                    pw.flush();

                    String answer = br.readLine();
                    if (answer != null) {
                        if (answer.equalsIgnoreCase("bye")) {
                            server.close();
                            client.close();
                            break;
                        }
                        answers[i] = answer;
                    }
                }
                for (int i = 0; i < 6; i++) {
                    System.out.println(questions[i] + " = " + answers[i]);
                }

            } catch (IOException ignored) {

            }
        }
    }

}
