package com.codingschool.model;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private String username;
    private String password;
    private String email;
    private int id = 0;
    // TODO czy musimy obslugiwac biginty
//    private BigInteger id = new BigInteger("0");
    private int userGroupId = 0;

    public User(){

    }

    public User (String username, String password, String email){
        this.username = username;
        this.email = email;
        this.setPassword(password);

    }

    public void setPassword(String password){
        this.password = BCrypt.hashpw(password,BCrypt.gensalt());
    }

    public void save(Connection conn) throws SQLException {
        if (this.id == 0){
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            String[] generatedColumns = {"id"};
            PreparedStatement prepStm = conn.prepareStatement(sql, generatedColumns);
            prepStm.setString(1, this.username);
            prepStm.setString(2, this.password);
            prepStm.setString(3, this.email);
            prepStm.executeUpdate();
            ResultSet rs = prepStm.getGeneratedKeys();

            if (rs.next()){
                this.id = rs.getInt(1);
            }
        }
    }

    //TODO sprawdzenie czy mail sie nie powtarza (setter na mailu?)

    //TODO save() dodawanie usergroupid
}
