import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ConnectedUser extends Thread{
    private final Socket socket;
    private DataOutputStream send;
    private DataInputStream receive;
    private boolean isUserConnected;
    private int id;

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
                    MyServer.isUserLogged.set(id, 0);
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
                case "updateHighScore":{
                    updateHighScore();
                    break;
                }
                default:
                    System.out.println("Not recognized command!:" + userData);
                    break;
            }



        }
    }

    private void updateHighScore() {
        try {
            String highscore = receive.readUTF();
            MyServer.userHighscore.set(id, highscore);
            System.out.println("Highscore updated: " + highscore);
        } catch (IOException e) {
            e.printStackTrace();
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

            if(MyServer.logins.indexOf(login) == -1){
                MyServer.logins.add(login);
                MyServer.passwords.add(password);
                MyServer.isUserLogged.add(0);
                MyServer.userHighscore.add("0");
                send.writeUTF("1");
            }
            else{
                send.writeUTF("0");
                System.out.println("Login already taken!");
                send.writeUTF("Login already taken!");
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
            MyServer.logins.indexOf(login);

            id = MyServer.logins.indexOf(login);
            System.out.println("Index of login: " + id);

            if(id > -1){
                System.out.println("Authorization...");
                if(password.equals(MyServer.passwords.get(id))){
                    System.out.println("Authorization complete");
                    if(MyServer.isUserLogged.get(id) == 0){
                        send.writeUTF("1");
                        System.out.println("Logging in");
                        send.writeUTF(MyServer.userHighscore.get(id));
                        System.out.println("Wyslano hs");
                        MyServer.isUserLogged.set(id, 1);
                    }
                    else{
                        System.out.println("Already logged in");
                        send.writeUTF("0");
                    }
                }
                else{
                    System.out.println("Wrong password or login.");
                    send.writeUTF("0");
                }
            }
            else{
                System.out.println("No login in the system");
                send.writeUTF("0");
            }
            send.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
