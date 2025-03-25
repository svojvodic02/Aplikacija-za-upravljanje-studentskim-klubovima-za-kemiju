package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.domain.UserSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StudentMenuController {


    public void AddExperiment() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add-experiment-student.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void RemoveExperiment() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("remove-experiment-student.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void EditExperiment() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("edit-experiment.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 600);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void AllExperiment() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("student-page.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void LogOut() {

        Set<String> privilegeSet=new HashSet<>();

        UserSession userSession=UserSession.getInstace("","",privilegeSet,"");

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




}
