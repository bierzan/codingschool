package com.codingschool.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Exercise {

    private int id = 0;
    private String title;
    private String description;

    public Exercise() {
    }

    public Exercise(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void save(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO exercise (title, description) VALUES (?, ?)";
            String[] generatedColumns = {"id"};
            PreparedStatement prepStm = conn.prepareStatement(sql, generatedColumns);
            prepStm.setString(1, this.title);
            prepStm.setString(2, this.description);
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
            String sql = "UPDATE exercise SET title = ?, description = ? WHERE id = ?";
            PreparedStatement prepStm = conn.prepareStatement(sql);
            prepStm.setString(1, this.title);
            prepStm.setString(2, this.description);
            prepStm.setInt(3, this.id);
            prepStm.executeUpdate();
        } else {
            System.out.println("Takie ćwiczenie nie istnieje w baze danych.");
        }
    }

    public void delete(Connection conn) throws SQLException {
        if (this.id != 0) {
            String sql = "DELETE FROM exercise WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static Exercise loadById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM exercise WHERE id = ?";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setInt(1, id);
        ResultSet rs = prepStm.executeQuery();

        if (rs.next()) {
            Exercise loadedExercise = new Exercise();
            loadedExercise.id = rs.getInt("id");
            loadedExercise.title = rs.getString("title");
            loadedExercise.description = rs.getString("description");
            return loadedExercise;
        }
        return null;
    }

    public static Exercise[] loadAll(Connection conn) throws SQLException {
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        String sql = "SELECT * FROM exercise";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        ResultSet rs = prepStm.executeQuery();
        while (rs.next()) {
            Exercise lodadeExercise = new Exercise();
            lodadeExercise.id = rs.getInt("id");
            lodadeExercise.title = rs.getString("title");
            lodadeExercise.description = rs.getString("description");

            exercises.add(lodadeExercise);
        }
        Exercise[] eArray = new Exercise[exercises.size()];
        eArray = exercises.toArray(eArray);
        return eArray;
    }

}
