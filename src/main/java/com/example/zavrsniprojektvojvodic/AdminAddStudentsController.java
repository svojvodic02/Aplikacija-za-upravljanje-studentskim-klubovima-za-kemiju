package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.Changes;
import com.example.zavrsniprojektvojvodic.domain.Experiment;
import com.example.zavrsniprojektvojvodic.domain.Student;
import com.example.zavrsniprojektvojvodic.domain.UserSession;
import com.example.zavrsniprojektvojvodic.exceptions.SameOIB;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminAddStudentsController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    @FXML
    private TableView<Student> studentTableView;
    @FXML
    private TableColumn<Student,String> studentFirstNameTableColumn;
    @FXML
    private TableColumn<Student, String> studentLastNameTableColumn;
    @FXML
    private TableColumn<Student, String> studentOIBTableColumn;
    @FXML
    private TextField studentFirstNameSearchTextField;
    @FXML
    private TextField studentOIBSearchTextField;
    @FXML
    private TextField newStudentFirstNameTextField;
    @FXML
    private TextField newStudentLastNameTextField;
    @FXML
    private TextField newStudentOIBTextField;
    @FXML
    private Button saveStudentButton;
    @FXML
    private Button addStudentButton;
    @FXML
    private Button deleteStudentButton;

    @FXML
    public void initialize(){
        studentFirstNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });
        studentLastNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getLastName());
            }
        });
        studentOIBTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getOIB());
            }
        });

        List<Student> allStudentsList= DatabaseUtils.getStudents();

        ObservableList<Student> observableStudentsList= FXCollections.observableArrayList(allStudentsList);

        studentTableView.setItems(observableStudentsList);

        setWhatIsDisabled(true);

    }

    @FXML
    private void setWhatIsDisabled(Boolean state) {

        newStudentFirstNameTextField.setDisable(state);

        newStudentLastNameTextField.setDisable(state);

        newStudentOIBTextField.setDisable(state);

        saveStudentButton.setDisable(state);
    }

    @FXML
    private void startAddingStudent(){
        setWhatIsDisabled(false);

        addStudentButton.setDisable(true);

        deleteStudentButton.setDisable(true);

    }

    public void searchStudent(){


        List<Student> studentsFilteredList = DatabaseUtils.getStudents();

        String studentFirstNameString=studentFirstNameSearchTextField.getText();

        studentsFilteredList=studentsFilteredList.stream().
                filter(s -> s.getName().contains(studentFirstNameString)).
                collect(Collectors.toList());

        String studentOIBString=studentOIBSearchTextField.getText();

        studentsFilteredList=studentsFilteredList.stream().
                filter(s -> s.getOIB().contains(studentOIBString)).
                collect(Collectors.toList());

        ObservableList<Student> observableStudentsList= FXCollections.observableArrayList(studentsFilteredList);

        studentTableView.setItems(observableStudentsList);


    }

    @FXML
    public void addNewStudent(){

        Long newStudentId= DatabaseUtils.getNextStudentID();

        String newStudentFirstName=newStudentFirstNameTextField.getText();

        String newStudentLastName=newStudentLastNameTextField.getText();

        String newStudentOIB=newStudentOIBTextField.getText();

        Boolean alreadyUsingTheGivenOIB=true;

        try{
            checkIfOIBisInUse(newStudentOIB);
            alreadyUsingTheGivenOIB=false;
        }catch (SameOIB e){
            System.out.println(e.getMessage());
            logger.info(e.getMessage(), e);

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("OIB already in use!");
            alert.setContentText("Please use a different OIB.");
            alert.showAndWait();
            newStudentOIBTextField.setText("");
            System.out.println(e.getMessage());
            logger.info(e.getMessage(), e);
        }

        if(newStudentOIB.length()!=11){
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Wrong OIB!");
            alert.setHeaderText("OIB should be 11 characters long!");
            alert.setContentText("Please write the correct OIB!");
            newStudentOIBTextField.setText("");
            alert.showAndWait();

        }else if(newStudentOIB.length()==11 && alreadyUsingTheGivenOIB==false){
            Student newStudent=new Student(newStudentFirstName,newStudentId,newStudentLastName,newStudentOIB);

            List<Student> studentList=new ArrayList<>();
            studentList.add(newStudent);

            DatabaseUtils.saveNewStudent(studentList);

            Set<String> privleges=new HashSet<>();

            UserSession currentUser=UserSession.getInstace("","",privleges,"");

            List<Changes<String,String>> newChangesList=new ArrayList<>();
            Changes<String,String> newChanges=new Changes<String,String>(currentUser.getUserFirstName(),newStudentFirstName,currentTime,currentDate);
            newChangesList.add(newChanges);
            FileUtils.serializeChanges(newChangesList);

            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("New student added!");
            alert.setHeaderText("Saving of a new student has been successful!");
            alert.setContentText("Student " + newStudentFirstName +" "+ newStudentLastName +" has been successfully saved.");

            alert.showAndWait();

            setWhatIsDisabled(true);

            addStudentButton.setDisable(false);
            deleteStudentButton.setDisable(false);
            newStudentFirstNameTextField.setText("");
            newStudentLastNameTextField.setText("");
            newStudentOIBTextField.setText("");

        }

    }

    public void deleteStudent() throws SQLException, IOException {

        Optional<Student> studentToDeleteCheck=Optional.empty();

        Student chosenStudentToDelete=studentTableView.getSelectionModel().getSelectedItem();

        studentToDeleteCheck= Optional.ofNullable(chosenStudentToDelete);

        if(studentToDeleteCheck.isPresent()) {

            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Are you sure you want to delete the student?");
            alert1.setHeaderText(null);
            alert1.setContentText("Choose your option.");
            ButtonType buttonTypeOne = new ButtonType("Yes");
            ButtonType buttonTypeTwo = new ButtonType("No");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert1.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
            alert1.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);

            Optional<ButtonType> result = alert1.showAndWait();
            if (result.get() == buttonTypeOne) {
                Optional<Student> studentToDelete = Optional.empty();

                Student chosenStudentFromTable = studentTableView.getSelectionModel().getSelectedItem();

                studentToDelete = Optional.ofNullable(chosenStudentFromTable);

                if (studentToDelete.isPresent()) {
                    DatabaseUtils.deleteStudent(studentToDelete.get());
                }

                Set<String> privleges = new HashSet<>();

                UserSession currentUser = UserSession.getInstace("", "", privleges, "");

                if (studentToDelete.isPresent()) {
                    List<Changes<String, String>> newChangesList = new ArrayList<>();
                    Changes<String, String> newChanges = new Changes<String, String>(currentUser.getUserFirstName(), studentToDelete.get().getName(), currentTime, currentDate);
                    newChangesList.add(newChanges);
                    FileUtils.serializeChanges(newChangesList);
                }


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Deleting succesful");
                alert.setHeaderText("Deletion of the student is done!");
                alert.setContentText("Student " + chosenStudentFromTable.getName() + " is deleted!");

                alert.showAndWait();
            } else if (result.get() == buttonTypeTwo) {

            } else {
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please chose a student from the table to delete!");
            alert.showAndWait();
        }

    }


    public static void checkIfOIBisInUse(String givenOIB) throws SameOIB {
        List<Student> allStudents=DatabaseUtils.getStudents();
        for(int i=0;i<allStudents.size();i++){
            if(allStudents.get(i).getOIB().contains(givenOIB)){
                throw new SameOIB("The OIB is already in use!");
            }
        }
    }

}
