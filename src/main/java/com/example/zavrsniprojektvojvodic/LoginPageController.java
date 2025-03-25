package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.domain.User;
import com.example.zavrsniprojektvojvodic.domain.UserSession;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.UnsupportedEncodingException;
import java.security.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LoginPageController {

    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private CheckBox toggleVisiblePasswordCheckBox;

    @FXML
    void initialize(){
        passwordPasswordField.setVisible(true);
        passwordTextField.setVisible(false);
    }
    @FXML
    public void signUpButton(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-page.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void loginButton() throws UnsupportedEncodingException, NoSuchAlgorithmException {

        List<User> users= FileUtils.getUserFromFile();

        String userEmail= emailTextField.getText();
        Optional<String> userPasswordOptional=Optional.empty();
        if(!passwordPasswordField.getText().isEmpty()) {
             userPasswordOptional= Optional.of(passwordPasswordField.getText());
        }
        if(!passwordTextField.getText().isEmpty()){
            userPasswordOptional=Optional.of(passwordTextField.getText());
        }

        String userPassword= userPasswordOptional.get();

        String hashedUserPassword=FileUtils.hashingTheGivenPassword(userPassword);


        Set<String> privilegeSet=new HashSet<>();


        for(int i=0;i< users.size();i++){
            if(users.get(i).getEmail().contains(userEmail) && users.get(i).getPassword().contains(hashedUserPassword)){
                if(users.get(i).getIsTheUserAnAdmin().contains("YES")){
                    privilegeSet.add(users.get(i).getIsTheUserAnAdmin());

                    privilegeSet.add("IS_ADMIN");
                    UserSession.getInstace(users.get(i).getName(),users.get(i).getLastName(),privilegeSet,users.get(i).getOIB());

                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin-page.fxml"));
                    try {
                        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                        HelloApplication.getMainStage().setScene(scene);
                        HelloApplication.getMainStage().show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                }else {
                    privilegeSet.add(users.get(i).getIsTheUserAnAdmin());

                    privilegeSet.add("IS_NOT_ADMIN");
                    UserSession.getInstace(users.get(i).getName(),users.get(i).getLastName(),privilegeSet,users.get(i).getOIB());

                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("student-page.fxml"));
                    try {
                        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                        HelloApplication.getMainStage().setScene(scene);
                        HelloApplication.getMainStage().show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @FXML
    public void toggleVisiblePassword(ActionEvent event) {
        if (toggleVisiblePasswordCheckBox.isSelected()) {
            passwordTextField.setText(passwordPasswordField.getText());
            passwordTextField.setVisible(true);
            passwordPasswordField.setVisible(false);
            return;
        }
        passwordPasswordField.setText(passwordTextField.getText());
        passwordPasswordField.setVisible(true);
        passwordTextField.setVisible(false);
    }

}
