import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectedUser extends Thread{
    private Socket socket;
    private DataOutputStream send;
    private DataInputStream receive;
    private boolean isUserConnected;

    public ConnectedUser(Socket socket) {
        this.socket = socket;
        System.out.println("Connection from: " + socket.getInetAddress());

        try {
            //output stream for sending data to client
            send = new DataOutputStream(socket.getOutputStream());

            //input stream for receiving data from client
            receive = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Problem during streams init");
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        super.run();
        isUserConnected = true;
        String userData = "START";
        //handle user request
        while(isUserConnected){
            //listen for users data
            try {
                userData = receive.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (userData != null){
                System.out.println(userData);
            }
            else{
                System.out.println("Received null");
            }

        }
    }
}
