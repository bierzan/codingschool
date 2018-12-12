package com.codingschool.model;


import com.codingschool.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;

public class User {

    private String username;
    private String password;
    private String email;
    private int id = 0;
    // TODO czy musimy obslugiwac biginty
//    private BigInteger id = new BigInteger("0");
    private UserGroup group;

    public User() {

    }

    public User(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.setPassword(password);
    }

    public User(String username, String password, String email, int groupId) {
        this.username = username;
        this.email = email;
        this.setPassword(password);
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.group = UserGroup.loadById(conn, groupId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void save(Connection conn) throws SQLException {

        if (this.id == 0) {
            PreparedStatement prepStm;
            try {
                String sql = "INSERT INTO users (username, password, email, user_group_id) VALUES (?, ?, ?,?)";
                String[] generatedColumns = {"id"};
                prepStm = conn.prepareStatement(sql, generatedColumns);
                prepStm.setString(1, this.username);
                prepStm.setString(2, this.password);
                prepStm.setString(3, this.email);
                prepStm.setInt(4, this.group.getId());
                prepStm.executeUpdate();
            } catch (NullPointerException e) { //kiedy nie ma przypisanej grupy

                String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
                String[] generatedColumns = {"id"};
                prepStm = conn.prepareStatement(sql, generatedColumns);
                prepStm.setString(1, this.username);
                prepStm.setString(2, this.password);
                prepStm.setString(3, this.email);
                prepStm.executeUpdate();

            }

            ResultSet rs = prepStm.getGeneratedKeys();

            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        } else {
            update(conn);
        }

    }

    public void update(Connection conn) throws SQLException {
        if (this.id > 0) {
            PreparedStatement prepStm;
            try {
                String sql = "UPDATE users SET username = ?, password = ?, email = ?, user_group_id = ? WHERE id = ?";
                prepStm = conn.prepareStatement(sql);
                prepStm.setString(1, this.username);
                prepStm.setString(2, this.password);
                prepStm.setString(3, this.email);
                prepStm.setInt(4, this.group.getId());
                prepStm.setInt(5, this.id);
                prepStm.executeUpdate();
            } catch (NullPointerException e) {
                String sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
                prepStm = conn.prepareStatement(sql);
                prepStm.setString(1, this.username);
                prepStm.setString(2, this.password);
                prepStm.setString(3, this.email);
                prepStm.setInt(4, this.id);
                prepStm.executeUpdate();
            }

        } else {
            System.out.println("Taki użytkownik nie istnieje w baze danych.");
        }
    }

    public void delete(Connection conn) throws SQLException {
        if (this.id != 0) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static User loadById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setInt(1, id);
        ResultSet rs = prepStm.executeQuery();

        if (rs.next()) {
            User loadedUser = new User();
            loadedUser.id = rs.getInt("id");
            loadedUser.username = rs.getString("username");
            loadedUser.password = rs.getString("password");
            loadedUser.email = rs.getString("email");
            int groupId = rs.getInt("user_group_id");
            if (groupId > 0) {
                loadedUser.group = UserGroup.loadById(conn, groupId);
            }
            return loadedUser;
        }
        return null;
    }

    public static User[] loadAll(Connection conn) throws SQLException {

        UserGroup[] groups = UserGroup.loadAll(conn);

        ArrayList<User> users = new ArrayList<User>();
        String sql = "SELECT * FROM users";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        ResultSet rs = prepStm.executeQuery();
        while (rs.next()) {
            User loadedUser = new User();
            loadedUser.id = rs.getInt("id");
            loadedUser.username = rs.getString("username");
            loadedUser.password = rs.getString("password");
            loadedUser.email = rs.getString("email");
            int groupId = rs.getInt("user_group_id");
            if (groupId > 0) {

                for (int i = 0; i < groups.length; i++) {
                    if (groups[i].getId() == groupId){
                        loadedUser.group = groups[i];
                    }
                }
            }
            users.add(loadedUser);
        }
        User[] uArray = new User[users.size()];
        uArray = users.toArray(uArray);
        return uArray;
    }


    //TODO sprawdzenie czy mail sie nie powtarza (setter na mailu?) - czy sprawdza to sql?

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public UserGroup getGroup() {
        return group;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGroup(int groupId){
        try {
            Connection conn = DatabaseConnection.getConnection();
            this.group = UserGroup.loadById(conn, groupId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setGroup(UserGroup group){
        this.group = group;
    }
}
