package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.Changes;
import com.example.zavrsniprojektvojvodic.domain.CurrentExperiment;
import com.example.zavrsniprojektvojvodic.domain.Experiment;
import com.example.zavrsniprojektvojvodic.domain.UserSession;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminViewAllExperimentsController {

    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

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

        List<Experiment> allExperiments= DatabaseUtils.getExperiment();
        ObservableList<Experiment> observableListExperiments= FXCollections.observableArrayList(allExperiments);

        experimentTableView.setItems(observableListExperiments);

    }

    public void filterExperiment(){
        List<Experiment> experimentsFilteredList= DatabaseUtils.getExperiment();
        String experimentNameString=experimentNameTextField.getText();

        experimentsFilteredList=experimentsFilteredList.stream().
                filter(c -> c.getName().contains(experimentNameString)).
                collect(Collectors.toList());

        ObservableList<Experiment> observableExperimentList= FXCollections.observableArrayList(experimentsFilteredList);

        experimentTableView.setItems(observableExperimentList);
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
            alert.setContentText("Please chose an experiment from the table to edit!");
            alert.showAndWait();
        }

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
