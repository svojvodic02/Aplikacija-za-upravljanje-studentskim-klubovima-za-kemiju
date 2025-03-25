package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.Changes;
import com.example.zavrsniprojektvojvodic.domain.Student;
import com.example.zavrsniprojektvojvodic.domain.UserSession;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminEditStudentsController {

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
    private TextField editStudentFirstNameTextField;
    @FXML
    private TextField editStudentLastNameTextField;
    @FXML
    private TextField editStudentOIBTextField;
    @FXML
    private Button startEditButton;
    @FXML
    private Button confirmEditButton;
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

        editStudentFirstNameTextField.setDisable(true);
        editStudentLastNameTextField.setDisable(true);
        editStudentOIBTextField.setDisable(true);
        confirmEditButton.setDisable(true);
    }

    public void startEdit(){
        Optional<Student> studentToEdit=Optional.empty();

        Student chosenStudentFromTable=studentTableView.getSelectionModel().getSelectedItem();

        studentToEdit= Optional.ofNullable(chosenStudentFromTable);
        if(studentToEdit.isPresent()){
            editStudentFirstNameTextField.setDisable(false);
            editStudentLastNameTextField.setDisable(false);
            editStudentOIBTextField.setDisable(false);
            confirmEditButton.setDisable(false);
            startEditButton.setDisable(true);
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please chose a student from the table to edit!");
            alert.showAndWait();
        }
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

    public void editStudent() throws SQLException, IOException {

        Optional<Student> studentToEdit=Optional.empty();

        Student chosenStudentFromTable=studentTableView.getSelectionModel().getSelectedItem();

        studentToEdit= Optional.ofNullable(chosenStudentFromTable);

        String editedStudentFirstName=editStudentFirstNameTextField.getText();

        String editedStudentLastName=editStudentLastNameTextField.getText();

        String editedStudentOIB=editStudentOIBTextField.getText();

        if(!editedStudentFirstName.isEmpty()){
            if(studentToEdit.isPresent()){
                studentToEdit.get().setName(editedStudentFirstName);
                DatabaseUtils.updateStudentFirstName(studentToEdit.get());
            }
        }

        if(!editedStudentLastName.isEmpty()){
            if(studentToEdit.isPresent()){
                studentToEdit.get().setLastName(editedStudentLastName);
                DatabaseUtils.updateStudentLastName(studentToEdit.get());
            }
        }

        if(!editedStudentOIB.isEmpty()){
            if(studentToEdit.isPresent()){
                studentToEdit.get().setOIB(editedStudentOIB);
                DatabaseUtils.updateStudentOIB(studentToEdit.get());
            }
        }
        Set<String> privleges=new HashSet<>();

        UserSession currentUser=UserSession.getInstace("","",privleges,"");

        if(studentToEdit.isPresent()) {
            List<Changes<String,String>> newChangesList=new ArrayList<>();
            Changes<String,String> newChanges = new Changes<String,String>(currentUser.getUserFirstName(), studentToEdit.get().getName() , currentTime,currentDate);
            newChangesList.add(newChanges);

           FileUtils.serializeChanges(newChangesList);
        }


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Editing succesful");
        alert.setHeaderText("Editing of the student is done!");
        alert.setContentText("Student " + chosenStudentFromTable.getName()+ " is edited!");

        alert.showAndWait();

    }

}
