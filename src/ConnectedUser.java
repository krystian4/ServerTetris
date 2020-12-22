import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class ConnectedUser extends Thread{
    private final Socket socket;
    private DataOutputStream send;
    private DataInputStream receive;
    private boolean isUserConnected;
    private int id;
    private String login;
    private MySQLConnection dataBase;

    public ConnectedUser(Socket socket) {
        this.socket = socket;
        System.out.println("Connection from: " + socket.getInetAddress());

        dataBase = new MySQLConnection();
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
                    dataBase.closeConnection();
                    isUserConnected = false;
                    MyServer.loggedUsersIDs.remove((Object) id);
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
                case "ranking":{
                    sendRanking();

                    break;
                }
                default:
                    System.out.println("Not recognized command!:" + userData);
                    break;
            }



        }
    }

    private void sendRanking() {
        ArrayList<String> ranking = dataBase.getRanking();
        Iterator iter = ranking.iterator();

        try{
            while(iter.hasNext()){
                send.writeUTF((String) iter.next());
            }
            send.writeUTF("end");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateHighScore() {
        try {
            String highscore = receive.readUTF();
            dataBase.updateUserHighscore(id, highscore);
            dataBase.updateRanking(login, highscore);
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

            if(!dataBase.isLoginTaken(login)){
                dataBase.addUser(login, password, email);
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
            login = receive.readUTF();
            String password = receive.readUTF();
            System.out.println("Login: " + login);
            System.out.println("Password: " + password);

            id = dataBase.getUserID(login);

            System.out.println("Index of login: " + id);

            if(id > -1){
                System.out.println("Authorization...");
                if(dataBase.authorizeUser(login, password)){
                    System.out.println("Authorization complete");

                    if(MyServer.loggedUsersIDs.contains(id)){
                        System.out.println("Already logged in");
                        send.writeUTF("0");
                    }
                    else{
                        send.writeUTF("1");
                        System.out.println("Logging in");
                        send.writeUTF(dataBase.getUserHighscore(id));
                        System.out.println("Highscore sent");
                        MyServer.loggedUsersIDs.add(id);
                    }
                }
                else{
                    System.out.println("Wrong password or login.");
                    send.writeUTF("-1");
                }
            }
            else{
                System.out.println("No login in the system");
                send.writeUTF("-1");
            }
            send.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
