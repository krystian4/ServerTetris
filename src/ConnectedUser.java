import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

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
            }
            catch (SocketException e){
                try{
                    System.out.println("User disconnected");
                    socket.close();
                    isUserConnected = false;
                    return;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(userData);
            switch (userData){
                case "login":{
                    loginUser();
                    break;
                }
                case "register":{
                    registerUser();
                    break;
                }
                default:
                    System.out.println("Not recognized command!");
                    break;
            }



        }
    }

    private void registerUser() {
        try {
            String email = receive.readUTF();
            String login = receive.readUTF();
            String password = receive.readUTF();

            System.out.println("Email: " + email);
            System.out.println("Login: " + login);
            System.out.println("Password: " + password);
            if(login.equals("bad") && password.equals("bad") ){
                send.writeUTF("0");
            }
            else{
                send.writeUTF("1");
            }
            send.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loginUser() {
        try {
            String login = receive.readUTF();
            String password = receive.readUTF();
            System.out.println("Login: " + login);
            System.out.println("Password: " + password);
            if(login.equals("bad") && password.equals("bad") ){
                send.writeUTF("0");
            }
            else{
                send.writeUTF("1");
            }
            send.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
