package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.Changes;
import com.example.zavrsniprojektvojvodic.domain.Student;
import com.example.zavrsniprojektvojvodic.domain.User;
import com.example.zavrsniprojektvojvodic.domain.UserSession;
import com.example.zavrsniprojektvojvodic.exceptions.NotInTheStudentRecords;
import com.example.zavrsniprojektvojvodic.exceptions.SameOIB;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class SignupPageController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    private static List<Student> allStudents=DatabaseUtils.getStudents();

    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private  TextField userOIBTextField;

    public void saveUserButton() throws UnsupportedEncodingException, NoSuchAlgorithmException {

        Long newUserId= FileUtils.getNextUserId();

        String newUserFirstName=firstNameTextField.getText();

        String newUserLastName=lastNameTextField.getText();

        String newUserEmail=emailTextField.getText();

        String newUserPassword=passwordTextField.getText();

        String newUserHashedPassword=FileUtils.hashingTheGivenPassword(newUserPassword);

        String isNewUserAdmin="NO";

        String newUserOIB=userOIBTextField.getText();

        if(newUserOIB.length()!=11){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("OIB needs to 11 characters long!");
            alert.setContentText("Please use a different OIB.");
            alert.showAndWait();
            userOIBTextField.setText("");
        }else if(newUserOIB.length()==11){

            User newUser=new User(newUserFirstName,newUserLastName,newUserEmail,newUserHashedPassword,isNewUserAdmin,newUserId,newUserOIB);


            List<User> users=FileUtils.getUserFromFile();

                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(newUser.getEmail())) {
                        emailTextField.setText(" ");

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error");
                        alert.setHeaderText("Email " + newUserEmail + " is already in use!");
                        alert.setContentText("Please use a different email.");
                        alert.showAndWait();
                    } else {
                        try {
                            checkIfStudentInRecord(newUser);
                            users.add(newUser);

                            FileUtils.saveLoginInFile(users);

                            Set<String> privleges=new HashSet<>();

                            UserSession currentUser=UserSession.getInstace("","",privleges,"");

                            List<Changes<String,String>> newChangesList=new ArrayList<>();
                            Changes<String,String> newChanges = new Changes<String,String>(currentUser.getUserFirstName() + currentUser.getUserLastName(),
                                    "new user added " + newUser.getName() + " at time:", currentTime,currentDate);
                            newChangesList.add(newChanges);
                            FileUtils.serializeChanges(newChangesList);


                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("SignUp Complete");
                            alert.setHeaderText("Creating of a new account has been successful!");
                            alert.setContentText("Username " + newUserEmail + " has been successfully saved.");

                            alert.showAndWait();

                            break;
                        } catch (NotInTheStudentRecords e) {

                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Error");
                            alert.setHeaderText("Student " + newUserFirstName + " " + newUserLastName + " doesn't exist in the records!");
                            alert.setContentText("Please check with your supervisor.");
                            alert.showAndWait();

                            logger.info(e.getMessage(), e);
                            System.out.println(e.getMessage());
                            firstNameTextField.setText("");
                            lastNameTextField.setText("");
                            emailTextField.setText("");
                            passwordTextField.setText("");
                            userOIBTextField.setText("");
                            break;
                        }

                    }
                }

        }


    }

    public void checkIfStudentInRecord(User newUser) throws NotInTheStudentRecords{
        Boolean studentExists=false;
        List<Student> allStudents=DatabaseUtils.getStudents();
        for(int i=0;i<allStudents.size();i++){
            if(allStudents.get(i).getOIB().contains(newUser.getOIB())){
                studentExists=true;
                break;
            }
        }

        if(!studentExists){
            throw new NotInTheStudentRecords("The student is not in the records!");
        }

    }

 /*   public static void checkIfOIBisInUse(String givenOIB) throws SameOIB {
        for(int i=0;i<allStudents.size();i++){
            if(allStudents.get(i).getOIB().contains(givenOIB)){
                throw new SameOIB("The OIB is already in use!");
            }
        }
    }*/

    public void backToLoginPageButton(){

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
