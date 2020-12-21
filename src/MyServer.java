import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyServer {
    private final int port;
    private ServerSocket serverSocket;
    public static ArrayList<Integer> loggedUsersIDs = new ArrayList<>();

    public static void main(String[] args) {
       MyServer Server = new MyServer(4444);
    }

    public MyServer(int port) {
        System.out.println("Starting server...");
        this.port = port;
        try{
            serverSocket = new ServerSocket(port);
            handleUsers();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleUsers() {
        Socket socket;
        while(true){
            try {
                socket = serverSocket.accept();
                new ConnectedUser(socket).start();
            } catch (IOException e) {
                System.out.println("Cannot connect to user");
                e.printStackTrace();
            }
        }
    }
}