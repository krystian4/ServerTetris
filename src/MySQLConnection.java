import java.sql.*;
import java.util.ArrayList;


public class MySQLConnection {
    private Connection conn = null;
    private String url = "jdbc:mysql://localhost:3306/";
    private String dbName = "tetris";
    private String username = "root";

    public MySQLConnection() {
        try {
            conn = DriverManager.getConnection(url+dbName, username, "");
            System.out.println("Connected to database");
            createTop10ranking();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createTop10ranking() {
        try{
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM `top_ten_score`");
            if(rs.next()){
                return;
            }
            else{
                st.executeUpdate("INSERT INTO `top_ten_score` (`id`,`score`) VALUES (1,0),(2,0),(3,0),(4,0),(5,0),(6,0),(7,0),(8,0),(9,0),(10,0);");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void closeConnection(){
        try {
            conn.close();
            System.out.println("Connection to db closed");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addUser(String login,String password, String email){
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO `user` (`login`, `email`, `password`) VALUES (?, ?, ?);");
            ps.setString(1,login);
            ps.setString(2,email);
            ps.setString(3,password);
            ps.executeUpdate();

            ps = conn.prepareStatement("Select id from user where login = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int id = rs.getInt("id");
                System.out.println("userID = " +  id);
                ps = conn.prepareStatement("INSERT INTO `user_highscore` (`user_id`, `score`) VALUES (?,?);");
                ps.setInt(1, id);
                ps.setInt(2, 0);
                ps.executeUpdate();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Boolean isLoginTaken(String login){
        String query = "SELECT login FROM user WHERE login = ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public int getUserID(String login) {
        try{
            PreparedStatement ps;
            ps = conn.prepareStatement("Select id from user where login = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int id = rs.getInt("id");
                return id;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public boolean authorizeUser(String login, String password) {
        String query = "SELECT login, password FROM user WHERE login = ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String dbPassword = rs.getString("password");

                if(password.equals(dbPassword)) return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public String getUserHighscore(int id) {
        String query = "SELECT score FROM user_highscore WHERE user_id = ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                String score = rs.getString("score");
                return score;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "-1";
    }

    public void updateUserHighscore(int id, String highscore) {
        String query = "UPDATE `user_highscore` SET `score` = ? WHERE `user_highscore`.`user_id` = ?;";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(highscore));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateRanking(String login, String highscore) {
        //check ranking
        String query = "SELECT * FROM `top_ten_score`";
        int hscore = Integer.parseInt(highscore);
        try{
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery("SELECT * FROM `top_ten_score`");
            while(rs.next()){
                int rsScore = rs.getInt("score");
                String rsLogin = rs.getString("login");
                if(rsScore == hscore && rsLogin.equals(login)){
                    return;
                }
                if(rsScore <= hscore){
                    rs.updateString("login", login);
                    rs.updateInt("score", hscore);
                    rs.updateRow();
                    hscore = rsScore;
                    login = rsLogin;
                }
                if(hscore == 0){
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<String> getRanking(){
        ArrayList<String> ranking = new ArrayList<>();
        String query = "SELECT * FROM `top_ten_score`";
        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                int id = rs.getInt("id");
                String login = rs.getString("login");
                String score = Integer.toString(rs.getInt("score"));
                String oneRow = id + ".  Score: " + score + "\t" + login;
                System.out.println(oneRow);
                ranking.add(oneRow);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ranking;
    }

}

