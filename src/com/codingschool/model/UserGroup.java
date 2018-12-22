package com.codingschool.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserGroup {

    private int id = 0;
    private String name;

    public UserGroup() {
    }

    public UserGroup(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void save(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO user_group (name) VALUES (?)";
            String[] generatedColumns = {"id"};
            PreparedStatement prepStm = conn.prepareStatement(sql, generatedColumns);
            prepStm.setString(1, this.name);
            prepStm.executeUpdate();
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
            String sql = "UPDATE user_group SET name = ? WHERE id = ?";
            PreparedStatement prepStm = conn.prepareStatement(sql);
            prepStm.setString(1, this.name);
            prepStm.setInt(2, this.id);
            prepStm.executeUpdate();
        } else {
            System.out.println("Taka grupa nie istnieje w baze danych.");
        }
    }

    public void delete(Connection conn) throws SQLException {
        if (this.id != 0) {
            String sql = "DELETE FROM user_group WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static UserGroup loadById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM user_group WHERE id = ?";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setInt(1, id);
        ResultSet rs = prepStm.executeQuery();

        if (rs.next()) {
            UserGroup loadedGroup = new UserGroup();
            loadedGroup.id = rs.getInt("id");
            loadedGroup.name = rs.getString("name");
            return loadedGroup;
        }
        return null;
    }

    public static UserGroup[] loadAll(Connection conn) throws SQLException {
        ArrayList<UserGroup> groups = new ArrayList<UserGroup>();
        String sql = "SELECT * FROM user_group";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        ResultSet rs = prepStm.executeQuery();
        while (rs.next()) {
            UserGroup loadedGroup = new UserGroup();
            loadedGroup.id = rs.getInt("id");
            loadedGroup.name = rs.getString("name");
            groups.add(loadedGroup);
        }
        UserGroup[] gArray = new UserGroup[groups.size()];
        gArray = groups.toArray(gArray);
        return gArray;
    }
}




