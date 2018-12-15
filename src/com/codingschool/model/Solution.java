package com.codingschool.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Solution {

    private int id = 0;
    private String created = dateNow();
    private String updated;
    private String description;
    private Exercise exercise;
    private User user;

    public Solution() {
    }

    public Solution (String description) {
        this.description = description;

    } //todo dodac tworzenie relacji do exercise i user

    private String dateNow(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public int getId() {
        return id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void save(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO solution (created, updated, description, exercise_id, user_id) " +
                    "VALUES (?, ?, ?,?,?)";
            String[] generatedColumns = {"id"};
            PreparedStatement prepStm = conn.prepareStatement(sql, generatedColumns);
            prepStm.setString(1, this.created);
            prepStm.setString(2, this.updated);
            prepStm.setString(3, this.description);
            prepStm.setInt(4,this.exercise.getId());
            prepStm.setInt(5, this.user.getId());
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
            String sql = "UPDATE solution SET updated = ?, description = ?, exercise_id = ?, user_id = ? WHERE id = ?";
            PreparedStatement prepStm = conn.prepareStatement(sql);
            prepStm.setString(1, dateNow());
            prepStm.setString(2, this.description);
            prepStm.setInt(3,this.exercise.getId());
            prepStm.setInt(4, this.user.getId());
            prepStm.setInt(5, this.id);
            prepStm.executeUpdate();
        } else {
            System.out.println("Takie rozwiązanie nie istnieje w baze danych.");
        }
    }

    public void delete(Connection conn) throws SQLException {
        if (this.id != 0) {
            String sql = "DELETE FROM solution WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, this.id);
            preparedStatement.executeUpdate();
            this.id = 0;
        }
    }

    public static Solution loadById(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM solution WHERE id = ?";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setInt(1, id);
        ResultSet rs = prepStm.executeQuery();

        if (rs.next()) {
            Solution loadedSolution = new Solution();
            loadedSolution.id = rs.getInt("id");
            loadedSolution.created = rs.getString("created");
            loadedSolution.updated = rs.getString("updated");
            loadedSolution.description = rs.getString("description");
            int exerciseId = rs.getInt("exercise_id");
            int userId = rs.getInt("user_id");
            if (exerciseId>0){
                loadedSolution.exercise = Exercise.loadById(conn,exerciseId);
            }
            if (userId>0){
                loadedSolution.user = User.loadById(conn, userId);
            }
            return loadedSolution;
        }
        return null;
    }

    public static Solution[] loadAll(Connection conn) throws SQLException {

        User[] users = User.loadAll(conn);
        Exercise[] exercises = Exercise.loadAll(conn);

        ArrayList<Solution> solutions = new ArrayList<Solution>();
        String sql = "SELECT * FROM solution";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        ResultSet rs = prepStm.executeQuery();
        while (rs.next()) {
            Solution loadedSolution = new Solution();
            loadedSolution.id = rs.getInt("id");
            loadedSolution.created = rs.getString("created");
            loadedSolution.updated = rs.getString("updated");
            loadedSolution.description = rs.getString("description");
            int exId = rs.getInt("exercise_id");
            int userId = rs.getInt("user_id");

            if (exId > 0) {
                for(int i = 0; i < exercises.length; i++){
                    if (exercises[i].getId() == exId){
                        loadedSolution.exercise = exercises[i];
                        break;
                    }
                }
            }

            if (userId > 0) {
                for(int i = 0; i < users.length; i++){
                    if (users[i].getId() == userId){
                        loadedSolution.user = users[i];
                        break;
                    }
                }
            }

            solutions.add(loadedSolution);
        }
        Solution[] sArray = new Solution[solutions.size()];
        sArray = solutions.toArray(sArray);
        return sArray;
    }

    public static Solution[] loadAllByUserId(Connection conn, int userId) throws SQLException {

        User user = User.loadById(conn, userId);
        Exercise[] exercises = Exercise.loadAll(conn);

        ArrayList<Solution> solutions = new ArrayList<Solution>();
        String sql = "SELECT * FROM solution WHERE user_id = ?";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setInt(1, userId);
        ResultSet rs = prepStm.executeQuery();
        while (rs.next()) {
            Solution loadedSolution = new Solution();
            loadedSolution.id = rs.getInt("id");
            loadedSolution.created = rs.getString("created");
            loadedSolution.updated = rs.getString("updated");
            loadedSolution.description = rs.getString("description");
            int exId = rs.getInt("exercise_id");

            if (exId > 0) {
                for(int i = 0; i < exercises.length; i++){
                    if (exercises[i].getId() == exId){
                        loadedSolution.exercise = exercises[i];
                        break;
                    }
                }
            }
            loadedSolution.user = user;
            solutions.add(loadedSolution);
        }
        Solution[] sArray = new Solution[solutions.size()];
        sArray = solutions.toArray(sArray);
        return sArray;
    }

    public static Solution[] loadAllByExerciseId(Connection conn, int exerciseId) throws SQLException {

        User[] users = User.loadAll(conn);
        Exercise exercise = Exercise.loadById(conn, exerciseId);

        ArrayList<Solution> solutions = new ArrayList<Solution>();
        String sql = "SELECT * FROM solution WHERE exercise_id = ?";
        PreparedStatement prepStm = conn.prepareStatement(sql);
        prepStm.setInt(1, exerciseId);
        ResultSet rs = prepStm.executeQuery();
        while (rs.next()) {
            Solution loadedSolution = new Solution();
            loadedSolution.id = rs.getInt("id");
            loadedSolution.created = rs.getString("created");
            loadedSolution.updated = rs.getString("updated");
            loadedSolution.description = rs.getString("description");
            loadedSolution.exercise = exercise;

            int userId = rs.getInt("user_id");

            if (userId > 0) {
                for(int i = 0; i < users.length; i++){
                    if (users[i].getId() == userId){
                        loadedSolution.user = users[i];
                        break;
                    }
                }
            }

            solutions.add(loadedSolution);
        }
        Solution[] sArray = new Solution[solutions.size()];
        sArray = solutions.toArray(sArray);
        return sArray;
    }
}

