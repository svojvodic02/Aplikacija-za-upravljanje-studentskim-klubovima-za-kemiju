package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.*;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StudentPageController {

    @FXML
    private TextField experimentNameTextField;
    @FXML
    private TableView<Experiment> experimentTableView;
    @FXML
    private TableColumn<Experiment, String> experimentNameTableColumn;
    @FXML
    private TableColumn<Experiment, String> experimentStartDateTableColumn;
    @FXML
    private TableColumn<Experiment, String> experimentEndDateTableColumn;
    @FXML
    private TableColumn<Experiment, String> experimentProgressTableColumn;


    @FXML
    public void initialize() {
        experimentNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment,String>, ObservableValue<String>>() {
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

        experimentProgressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment,String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Experiment, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getProgress().toString());
            }
        });

        List<Experiment> studentExperiments= getExperimentsForTable();
        ObservableList<Experiment> observableListStudentExperiments=FXCollections.observableArrayList(studentExperiments);

        experimentTableView.setItems(observableListStudentExperiments);

    }

    private static List<Experiment> getExperimentsForTable() {
        List<Experiment> allStudentExperiments= DatabaseUtils.getExperiment();
        List<Student> allStudents=DatabaseUtils.getStudents();

        List<Experiment> experimentsFilteredList = new ArrayList<>();
        Set<String> privilegeSet=new HashSet<>();

        UserSession newUserSession=UserSession.getInstace("","",privilegeSet,"");
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

        return experimentsFilteredList;
    }

    @FXML
    private void searchExperiment(){

        List<Experiment> experimentsFilteredList= getExperimentsForTable();

        String experimentNameString=experimentNameTextField.getText();

        experimentsFilteredList=experimentsFilteredList.stream().
                filter(c -> c.getName().contains(experimentNameString)).
                collect(Collectors.toList());

        ObservableList<Experiment> observableExperimentList= FXCollections.observableArrayList(experimentsFilteredList);

        experimentTableView.setItems(observableExperimentList);


    }

    public void editExperiment(){

        Optional<Experiment> experimentToEdit=Optional.empty();

        Experiment chosenExperimentToEdit=experimentTableView.getSelectionModel().getSelectedItem();

        experimentToEdit= Optional.ofNullable(chosenExperimentToEdit);

        if(experimentToEdit.isPresent()) {
            CurrentExperiment currentExperiment = CurrentExperiment.getInstace(experimentToEdit.get().getName(), experimentToEdit.get().getId(),
                    experimentToEdit.get().getAuthors(), experimentToEdit.get().getDateStarted(), experimentToEdit.get().getDateFinished(), experimentToEdit.get().getNotes(),
                    experimentToEdit.get().getProgress());


            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("edit-experiment.fxml"));
            try {
                Scene scene = new Scene(fxmlLoader.load(), 450, 600);
                HelloApplication.getMainStage().setScene(scene);
                HelloApplication.getMainStage().show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please chose an item from the table!");
            alert.showAndWait();
        }

    }

    public void viewExperiment(){

        Optional<Experiment> experimentToView=Optional.empty();

        Experiment chosenExperimentToView=experimentTableView.getSelectionModel().getSelectedItem();

        experimentToView= Optional.ofNullable(chosenExperimentToView);

        if(experimentToView.isPresent()) {
            CurrentExperiment currentExperiment = CurrentExperiment.getInstace(experimentToView.get().getName(), experimentToView.get().getId(),
                    experimentToView.get().getAuthors(), experimentToView.get().getDateStarted(), experimentToView.get().getDateFinished(), experimentToView.get().getNotes(),
                    experimentToView.get().getProgress());


            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view-experiment.fxml"));
            try {
                Scene scene = new Scene(fxmlLoader.load(), 600, 600);
                HelloApplication.getMainStage().setScene(scene);
                HelloApplication.getMainStage().show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please chose an experiment from the table!");
            alert.showAndWait();
        }

    }



}

