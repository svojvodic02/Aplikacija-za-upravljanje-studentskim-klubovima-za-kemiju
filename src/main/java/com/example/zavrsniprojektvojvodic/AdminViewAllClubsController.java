package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.constants.Constants;
import com.example.zavrsniprojektvojvodic.domain.*;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminViewAllClubsController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    @FXML
    private TableView<ChemistryClub> chemistryClubTableView;
    @FXML
    private TableColumn<ChemistryClub,String> chemistryClubIDTableColumn;
    @FXML
    private TableColumn<ChemistryClub, String> chemistryClubNameTableColumn;
    @FXML
    private TableColumn<ChemistryClub,String> chemistryClubNumberOfStudents;
    @FXML
    private TableColumn<ChemistryClub,String> chemistryClubNumberOfExperiments;
    @FXML
    private TextField chemistryClubIdSearchTextField;
    @FXML
    private TextField chemistryClubNameSearchTextField;
    @FXML
    private Button deleteChemistryClubButton;
    @FXML
    private Button confirmDeletionButton;
    @FXML
    private Button searchButton;

    @FXML
    public void initialize(){
        chemistryClubIDTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChemistryClub, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ChemistryClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getId().toString());
            }
        });
        chemistryClubNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChemistryClub, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ChemistryClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getName());
            }
        });

        chemistryClubNumberOfStudents.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChemistryClub, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ChemistryClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().howManyMembers().toString());
            }
        });
        chemistryClubNumberOfExperiments.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChemistryClub, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ChemistryClub, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().howManyExperiments().toString());
            }
        });

        List<ChemistryClub> allChemistryClubs= DatabaseUtils.getChemistryClub();

        ObservableList<ChemistryClub> chemistryClubObservableList= FXCollections.observableArrayList(allChemistryClubs);

        chemistryClubTableView.setItems(chemistryClubObservableList);

        confirmDeletionButton.setDisable(true);
    }

    public void startDeleting(){
        confirmDeletionButton.setDisable(false);

        searchButton.setDisable(true);
        chemistryClubIdSearchTextField.setDisable(true);
        chemistryClubNameSearchTextField.setDisable(true);
        deleteChemistryClubButton.setDisable(true);
    }

    public void searchChemistryClubs(){

        String chemistryClubName = chemistryClubNameSearchTextField.getText();
        String chemistryClubIdString = chemistryClubIdSearchTextField.getText();
        Optional<Long> chemistryClubId=Optional.empty();

        boolean canParseId=false;


        try {
            if (!chemistryClubIdString.isEmpty()) {
                chemistryClubId = Optional.of(Long.parseLong(chemistryClubIdString));
            }
            canParseId=true;
        }catch(NumberFormatException n){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("ID should be a number!");
            alert.setContentText("Please write a number.");
            alert.showAndWait();
            chemistryClubIdSearchTextField.setText("");
            System.out.println(n.getMessage());
            logger.info(n.getMessage(), n);
        }

        Optional<ChemistryClubFilter> chemistryClubFilter=Optional.empty();

        if(chemistryClubId.isPresent()) {
            chemistryClubFilter = Optional.of(new ChemistryClubFilter(chemistryClubName, chemistryClubId.get()));
        }else if(chemistryClubId.isEmpty()){
             chemistryClubFilter = Optional.of(new ChemistryClubFilter(chemistryClubName, 0L));
        }

        if(canParseId) {
            List<ChemistryClub> filteredChemistryClubList = DatabaseUtils.getChemistryClubByFilters(chemistryClubFilter.get());

            ObservableList observableChemistryClubList = FXCollections.observableArrayList(filteredChemistryClubList);

            chemistryClubTableView.setItems(observableChemistryClubList);
        }

    }

    public void deleteChemistryCLub() throws SQLException, IOException {

        Optional<ChemistryClub> chemistryClubToCheck=Optional.empty();

        ChemistryClub chosenChemistryClubToDelete=chemistryClubTableView.getSelectionModel().getSelectedItem();

        chemistryClubToCheck= Optional.ofNullable(chosenChemistryClubToDelete);

        if(chemistryClubToCheck.isPresent()){
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Are you sure you want to delete chemistry club?");
            alert1.setHeaderText(null);
            alert1.setContentText("Choose your option.");
            ButtonType buttonTypeOne = new ButtonType("Yes");
            ButtonType buttonTypeTwo = new ButtonType("No");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert1.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo,buttonTypeCancel);
            alert1.getDialogPane().lookupButton(buttonTypeCancel).setVisible(false);

            Optional<ButtonType> result = alert1.showAndWait();
            if (result.get() == buttonTypeOne){
                Optional<ChemistryClub> chemistryClubToDelete=Optional.empty();

                ChemistryClub chosenChemistryClubFromTable=chemistryClubTableView.getSelectionModel().getSelectedItem();

                chemistryClubToDelete= Optional.ofNullable(chosenChemistryClubFromTable);

                if(chemistryClubToDelete.isPresent()){
                    DatabaseUtils.deleteChemistryClub(chemistryClubToDelete.get());
                }

                Set<String> privleges=new HashSet<>();

                UserSession currentUser=UserSession.getInstace("","",privleges,"");

                if(chemistryClubToDelete.isPresent()) {
                    List<Changes<String,String>> newChangesList=new ArrayList<>();
                    Changes<String,String> newChanges = new Changes<String,String>(currentUser.getUserFirstName(), chemistryClubToDelete.get().getName() , currentTime,currentDate);
                    newChangesList.add(newChanges);
                    FileUtils.serializeChanges(newChangesList);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Deleting succesful");
                alert.setHeaderText("Deletion of chemistry club is done!");
                alert.setContentText("Chemistry club " + chosenChemistryClubFromTable.getName()+ " is deleted!");

                alert.showAndWait();

                confirmDeletionButton.setDisable(true);

                searchButton.setDisable(false);
                chemistryClubIdSearchTextField.setDisable(false);
                chemistryClubNameSearchTextField.setDisable(false);
                deleteChemistryClubButton.setDisable(false);

            } else if (result.get()==buttonTypeTwo){

            }else {
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please chose a chemistry club from the table to delete!");
            alert.showAndWait();
        }

    }

}
