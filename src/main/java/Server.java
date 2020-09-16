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

                String[] questions = new String[7];
                questions[0] = "Please Enter number of travelers: ";
                questions[1] = "Ferry or Air Travel (f/a)";
                questions[2] = "Number of days";
                questions[3] = "Meal included or Not (yes/no)";
                questions[4] = "Contact";
                questions[5] = "Phone number";
                questions[6] = "Address";

                String[] answers = new String[7];

                // Read data from the client
                InputStream clientIn = client.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(clientIn));
                boolean cancelOrder = false; // flag if the client type bye
                int totalCost = 0;
                for (int i = 0; i < 7; i++) {
                    if (i == 4) { // calc the price and send it to client
                        int numberOfTravelers = Integer.parseInt(answers[0]);
                        int costOfTravel = 0;
                        if (answers[1].toLowerCase().startsWith("f"))
                            costOfTravel = 600;
                        else
                            costOfTravel = 900;

                        int numberOfDays = Integer.parseInt(answers[2]);

                        int dayCost = 250;
                        if (answers[3].toLowerCase().startsWith("y"))
                            dayCost += 100;

                        totalCost = ((dayCost * numberOfDays) + costOfTravel) * numberOfTravelers;
                        pw.println("it will Cost:" + totalCost + " SEK");
                    }
                    pw.println(questions[i]);
                    pw.flush();

                    String answer = br.readLine();
                    if (answer != null) {
                        if (answer.equalsIgnoreCase("bye")) {
                            client.close();
                            cancelOrder = true;
                            break;
                        }
                        answers[i] = answer;
                    }
                }
                if (cancelOrder) {
                    System.out.println("Order cancelled");
                } else {
                    pw.println("Your Order is:\n");
                    StringBuilder order = new StringBuilder();
                    String orderLine = "Total Cost: " + totalCost;
                    order.append(orderLine);
                    order.append("\n");
                    pw.println(orderLine);
                    for (int i = 0; i < 7; i++) {
                        orderLine = questions[i] + " = " + answers[i];
                        order.append(orderLine);
                        order.append("\n");
                        System.out.println(orderLine);
                        pw.println(orderLine);
                    }
                    pw.flush();

                    String filename = "./Order" + System.currentTimeMillis() + ".txt";
                    saveFile(order.toString(), filename);
                    // TODO: send file
                }

            } catch (IOException ignored) {

            }
        }
    }

    private static void saveFile(String body, String filename) throws IOException {
        File file = new File(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(body);
        }
    }
}
