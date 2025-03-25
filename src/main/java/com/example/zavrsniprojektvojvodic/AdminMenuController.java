package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.domain.UserSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AdminMenuController {

    public void LogOut() {

        Set<String> privilegeSet=new HashSet<>();

        UserSession userSession=UserSession.getInstace(null,null,null,null);

        userSession.cleanUserSession();


        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-page.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void AddStudents() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-add-students.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void EditStudents() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-edit-students.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void AddChemistryClub() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-add-chemistry-club.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ViewAllChemistryClubs() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view-all-clubs.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void EditChemistryClubs() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-edit-chemistry-club.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void LogScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-log-screen.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void ViewAllExperiments() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-view-all-experiments.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
