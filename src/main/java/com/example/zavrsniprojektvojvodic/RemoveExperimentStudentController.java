package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.Changes;
import com.example.zavrsniprojektvojvodic.domain.Experiment;
import com.example.zavrsniprojektvojvodic.domain.Student;
import com.example.zavrsniprojektvojvodic.domain.UserSession;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RemoveExperimentStudentController {
    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    @FXML
    private TableView<Experiment> experimentTableView;

    @FXML
    private TableColumn<Experiment,String> experimentIDTableColumn;
    @FXML
    private TableColumn<Experiment, String> experimentNameTableColumn;
    @FXML
    private TableColumn<Experiment, String> experimentStartDateTableColumn;
    @FXML
    private TableColumn<Experiment, String> experimentEndDateTableColumn;

    @FXML
    public void initialize() {
        experimentIDTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Experiment, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getId().toString());
            }
        });

        experimentNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Experiment, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        experimentStartDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Experiment, String> param) {
                LocalDate experimentDateStarted = param.getValue().getDateStarted();
                String experimentDateStartedString = experimentDateStarted.format(Constants.DATE_TIME_FORMAT);
                return new ReadOnlyStringWrapper(experimentDateStartedString);
            }
        });
        experimentEndDateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Experiment, String> param) {
                LocalDate experimentEndDate = param.getValue().getDateFinished();
                String experimentEndDateString=null;
                if(experimentEndDate==null){
                    experimentEndDateString=null;
                }else {
                    experimentEndDateString = experimentEndDate.format(Constants.DATE_TIME_FORMAT);
                }
                return new ReadOnlyStringWrapper(experimentEndDateString);
            }
        });


        List<Experiment> allStudentExperiments= DatabaseUtils.getExperiment();
        List<Student> allStudents=DatabaseUtils.getStudents();

        List<Experiment> experimentsFilteredList = new ArrayList<>();
        Set<String> privilegeSet=new HashSet<>();

        UserSession newUserSession=UserSession.getInstace("No","No",privilegeSet,"No");
        Optional<Student> currentStudent=Optional.empty();

        for(int j=0;j<allStudents.size();j++){
            if(allStudents.get(j).getOIB().equals(newUserSession.getOIB())){
                currentStudent=Optional.of(new Student(allStudents.get(j).getName(),allStudents.get(j).getId(),
                        allStudents.get(j).getLastName(),allStudents.get(j).getOIB()));
                break;
            }
        }
        if(currentStudent.isPresent()) {
            for (int i = 0; i < allStudentExperiments.size(); i++) {
                for(int k=0;k< allStudentExperiments.get(i).getAuthors().size();k++){
                    if(allStudentExperiments.get(i).getAuthors().get(k).getOIB().equals(currentStudent.get().getOIB())){
                        experimentsFilteredList.add(allStudentExperiments.get(i));
                    }
                }
            }
        }


        ObservableList<Experiment> observableExperimentList= FXCollections.observableArrayList(experimentsFilteredList);

        experimentTableView.setItems(observableExperimentList);

    }

    public void deleteExperiment() throws SQLException, IOException {

        Optional<Experiment> experimentToDelete=Optional.empty();

        Experiment chosenExperimentFromTable=experimentTableView.getSelectionModel().getSelectedItem();

        experimentToDelete= Optional.ofNullable(chosenExperimentFromTable);

        if(experimentToDelete.isPresent()){
            if(experimentToDelete.isPresent()){
                DatabaseUtils.deleteExperiment(experimentToDelete.get());
            }

            Set<String> privleges=new HashSet<>();

            UserSession currentUser=UserSession.getInstace("","",privleges,"");

            if(experimentToDelete.isPresent()) {
                List<Changes<String,String>> newChangesList=new ArrayList<>();
                Changes<String,String> newChanges = new Changes<String,String>(currentUser.getUserFirstName(), experimentToDelete.get().getName(), currentTime,currentDate);
                newChangesList.add(newChanges);
                FileUtils.serializeChanges(newChangesList);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Deleting succesful");
            alert.setHeaderText("Deletion of the experiment is done!");
            alert.setContentText("Experiment " + chosenExperimentFromTable.getName()+ " is deleted!");

            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please chose an experiment from the table to delete!");
            alert.showAndWait();
        }

    }
}
