import java.io.*;
import java.net.*;

public class MyServer {
    public static void main(String[] args) {
        System.out.println("Server running...\n");
        connectToServer();
    }

    private static void connectToServer() {
        try (
                ServerSocket serverSocket = new ServerSocket(4444);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()))
        ) {

            // Initiate conversation with client
            /*Echo kkp = new Echo();
            String outputLine = kkp.echoMsg("");*/
            String outputLine;

            //out.println(outputLine);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("echo: " + inputLine);
                outputLine = inputLine;//kkp.echoMsg(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye."))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}