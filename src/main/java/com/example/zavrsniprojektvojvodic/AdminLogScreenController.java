package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.domain.Changes;
import com.example.zavrsniprojektvojvodic.domain.ChemistryClub;
import com.example.zavrsniprojektvojvodic.domain.Student;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminLogScreenController {

    @FXML
    private TableView<Changes<String,String>> changesTableView;
    @FXML
    private TableColumn<Changes<String,String>,String> whoDidTheChangeTableCollumn;
    @FXML
    private TableColumn<Changes<String,String>,String> whatWasTheChangeTableCollumn;
    @FXML
    private TableColumn<Changes<String,String>,String> timeOfTheChangeTableCollumn;
    @FXML
    private TableColumn<Changes<String,String>,String> dateOfTheChangeTableCollumn;

    public void initialize(){

        whoDidTheChangeTableCollumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes<String,String>, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes<String,String>, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getWhoDidTheChange());
            }
        });
        whatWasTheChangeTableCollumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes<String,String>, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes<String,String>, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getWhatWasTheChange());
            }
        });
        timeOfTheChangeTableCollumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes<String,String>, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes<String,String>, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getWhatTimeTheChangeOccured());
            }
        });
        dateOfTheChangeTableCollumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes<String,String>, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes<String,String>, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getWhatDateTheChangeOccured());
            }
        });

        List<Changes<String,String>> changesList= FileUtils.deserializeChanges();
        ObservableList<Changes<String,String>> changesObservableList=FXCollections.observableArrayList(changesList);

        changesTableView.setItems(changesObservableList);

    }
}
