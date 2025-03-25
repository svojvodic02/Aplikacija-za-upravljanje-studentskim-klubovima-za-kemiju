package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.Changes;
import com.example.zavrsniprojektvojvodic.domain.ChemistryClub;
import com.example.zavrsniprojektvojvodic.domain.Experiment;
import com.example.zavrsniprojektvojvodic.domain.Student;
import com.example.zavrsniprojektvojvodic.generics.Size;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AdminPageController {

    List<Student> allStudents = DatabaseUtils.getStudents();
    List<Experiment> allExperiments = DatabaseUtils.getExperiment();
    List<ChemistryClub> allChemistryClubs = DatabaseUtils.getChemistryClub();

    @FXML
    private TextArea totalAmountOfStudentsTextArea;
    @FXML
    private TextArea totalAmountOfExperimentsTextArea;
    @FXML
    private TextArea totalAmountOfChemistryGroupsTextArea;
    @FXML
    private TableView experimentsInProgressTableView;
    @FXML
    private TableColumn<Experiment, String> nameOfExperimentTableColumn;
    @FXML
    private TableColumn<Experiment, String> whenTheExperimentStartedTableColumn;
    @FXML
    private TableColumn<Experiment, String> progressOfExperimentTableColumn;
    @FXML
    private CheckBox sortTableViewByProgressCheckBox;

    public void initialize() {

        nameOfExperimentTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Experiment, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });
        whenTheExperimentStartedTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Experiment, String> param) {
                LocalDate experimentDateStarted = param.getValue().getDateStarted();
                String experimentDateStartedString = experimentDateStarted.format(Constants.DATE_TIME_FORMAT);
                return new ReadOnlyStringWrapper(experimentDateStartedString);
            }
        });
        progressOfExperimentTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Experiment, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Experiment, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getProgress().toString());
            }
        });

        List<Experiment> experimentsInProgress = getExperimentsInProgressMethod();

        ObservableList<Experiment> experimentsInProgressObservableArray = FXCollections.observableArrayList(experimentsInProgress);

        experimentsInProgressTableView.setItems(experimentsInProgressObservableArray);


        Size<Student> numberOfStudentsGeneric = new Size<>();
        Integer numberOfStudents = numberOfStudentsGeneric.getHowManyGeneric(allStudents);
        totalAmountOfStudentsTextArea.setText(numberOfStudents.toString());
        totalAmountOfStudentsTextArea.setEditable(false);

        Size<Experiment> numberOfExperimentsGeneric = new Size<>();
        Integer numberOfExperiments = numberOfExperimentsGeneric.getHowManyGeneric(allExperiments);
        totalAmountOfExperimentsTextArea.setText(numberOfExperiments.toString());
        totalAmountOfExperimentsTextArea.setEditable(false);

        Size<ChemistryClub> numberOfChemistryClubsGeneric = new Size<>();
        Integer numberOfChemistryClubs = numberOfChemistryClubsGeneric.getHowManyGeneric(allChemistryClubs);
        totalAmountOfChemistryGroupsTextArea.setText(numberOfChemistryClubs.toString());
        totalAmountOfChemistryGroupsTextArea.setEditable(false);

    }

    private List<Experiment> getExperimentsInProgressMethod() {
        List<Experiment> experimentsInProgress = new ArrayList<>();

        for (int i = 0; i < allExperiments.size(); i++) {
            if (allExperiments.get(i).getProgress().compareTo(100) != 0) {
                experimentsInProgress.add(allExperiments.get(i));
            }
        }
        return experimentsInProgress;
    }

    @FXML
    public void toggleSortedList(ActionEvent event) {
        if (sortTableViewByProgressCheckBox.isSelected()) {
            List<Experiment> experimentsInProgress = getExperimentsInProgressMethod();

            experimentsInProgress.sort((o1, o2) -> o1.getProgress() - o2.getProgress());

            ObservableList<Experiment> sortedExperimentList = FXCollections.observableArrayList(experimentsInProgress);

            experimentsInProgressTableView.setItems(sortedExperimentList);
        }else if(!sortTableViewByProgressCheckBox.isSelected()){
            List<Experiment> experimentsInProgress = getExperimentsInProgressMethod();

            experimentsInProgress.sort((o1, o2) -> o2.getProgress() - o1.getProgress());

            ObservableList<Experiment> sortedExperimentList = FXCollections.observableArrayList(experimentsInProgress);

            experimentsInProgressTableView.setItems(sortedExperimentList);
        }

    }
}
