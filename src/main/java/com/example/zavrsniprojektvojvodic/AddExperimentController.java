package com.example.zavrsniprojektvojvodic;

import com.example.zavrsniprojektvojvodic.domain.*;
import com.example.zavrsniprojektvojvodic.exceptions.DateNull;
import com.example.zavrsniprojektvojvodic.exceptions.ProgressNotANumber;
import com.example.zavrsniprojektvojvodic.utils.DatabaseUtils;
import com.example.zavrsniprojektvojvodic.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddExperimentController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    Set<String> privileges =new HashSet<>();
    UserSession currentUser=UserSession.getInstace("","", privileges,"");
    private List<Student> allStudentsList= DatabaseUtils.getStudents();

    private List<ChemistryClub> allChemistryClubs=DatabaseUtils.getChemistryClub();

    List<String> newExperimentAuthorsListNames=new ArrayList<>();

    String currentTime= LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    String currentDate=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    @FXML
    private TextField experimentNameTextField;
    @FXML
    private ListView allstudentAuthorsListView;
    @FXML
    private  DatePicker experimentStartingDatePicker;
    @FXML
    private DatePicker experimentEndingDatePicker;
    @FXML
    private TextArea experimentNotesTextArea;
    @FXML
    private TextField experimentProgressTextField;

    @FXML
    public void initialize() {

        Optional<Student> currentStudent=Optional.empty();

        for(int i=0;i< allStudentsList.size();i++){
            if(allStudentsList.get(i).getOIB().equals(currentUser.getOIB())){
                currentStudent= Optional.ofNullable(allStudentsList.get(i));
            }
        }

        List<Student> studentsInTheSameGroupList=new ArrayList<>();

        for(int j=0;j< allChemistryClubs.size();j++){
            for(int k=0;k<allChemistryClubs.get(j).getListOfStudents().size();k++){
                if(allChemistryClubs.get(j).getListOfStudents().get(k).getOIB().equals(currentStudent.get().getOIB())){
                    studentsInTheSameGroupList=allChemistryClubs.get(j).getListOfStudents();
                    break;
                }
            }
        }

        List<String> studentNameList=DatabaseUtils.getStudentName(studentsInTheSameGroupList);
        ObservableList<String> observableStudentListNamesList= FXCollections.observableArrayList(studentNameList);

       allstudentAuthorsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

       allstudentAuthorsListView.setItems(observableStudentListNamesList);


        allstudentAuthorsListView.setOnMouseClicked(new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                ObservableList<String> selectedItems =  allstudentAuthorsListView.getSelectionModel().getSelectedItems();

                for(String studentName : selectedItems){
                    newExperimentAuthorsListNames.add(studentName);
                }

            }

        });

        experimentStartingDatePicker.setEditable(false);
        experimentEndingDatePicker.setEditable(false);


    }

    public void saveNewExperiment(){

        String newExperimentName=experimentNameTextField.getText();

        Long experimentNewId=DatabaseUtils.getNextExperimentId();

        List<Student> newExperimentStudentList=new ArrayList<>();


        for(int i=0;i<newExperimentAuthorsListNames.size();i++){
            for(int j=0;j<allStudentsList.size();j++){
                if(allStudentsList.get(j).getName().equals(newExperimentAuthorsListNames.get(i))){
                    newExperimentStudentList.add(allStudentsList.get(i));
                }
            }
        }

        Experiment newExperiment=new Experiment.Builder(newExperimentName).setId(experimentNewId).setAuthors(newExperimentStudentList).createExperiment();

        boolean dateIsNull=true;


          try {
              checkIfDateIsCorrect(experimentStartingDatePicker.getValue());
              LocalDate experimentStartingData = experimentStartingDatePicker.getValue();
              newExperiment.setDateStarted(experimentStartingData);
              dateIsNull=false;
          } catch (DateNull d) {
              Alert alert = new Alert(Alert.AlertType.WARNING);
              alert.setTitle("Error");
              alert.setHeaderText("Starting date should be written!");
              alert.setContentText("Please choose a date.");
              alert.showAndWait();
              experimentProgressTextField.setText("");
              System.out.println(d.getMessage());
              logger.info(d.getMessage(), d);
          }



        if(experimentEndingDatePicker.getValue()!=null){
            LocalDate experimentEndingData = experimentEndingDatePicker.getValue();
            newExperiment.setDateFinished(experimentEndingData);
        }else if(experimentEndingDatePicker.getValue()==null){
           LocalDate experimentEndingData=null;
        }


        String experimentNotes=experimentNotesTextArea.getText();
        newExperiment.setNotes(experimentNotes);

        boolean wrongProgressInput=true;

        Integer experimentProgress=0;

        do {
            try {
                checkProgressNumber(experimentProgressTextField.getText());
                wrongProgressInput=false;

            }catch(ProgressNotANumber e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Progress should be a number!");
                alert.setContentText("Please write a number.");
                alert.showAndWait();
                experimentProgressTextField.setText("");
                System.out.println(e.getMessage());
                logger.info(e.getMessage(), e);
            }

        }while(wrongProgressInput);

        if(!experimentProgressTextField.getText().isEmpty()){
            String experimentProgressString=experimentProgressTextField.getText();
            if(wrongProgressInput==false || dateIsNull==false) {
                experimentProgress = Integer.parseInt(experimentProgressString);
                newExperiment.setProgress(experimentProgress);
                List<Experiment> experimentList=new ArrayList<>();
                experimentList.add(newExperiment);

                DatabaseUtils.saveExperiment(experimentList);
                DatabaseUtils.saveExperimentAuthors(newExperimentStudentList,experimentNewId);

                Set<String> privleges=new HashSet<>();

                UserSession currentUser=UserSession.getInstace("","",privleges,"");

                List<Changes<String,String>> newChangesList=new ArrayList<>();
                Changes<String,String> newChanges=new Changes<>(currentUser.getUserFirstName(), newExperimentName,currentTime,currentDate);
                newChangesList.add(newChanges);

                FileUtils.serializeChanges(newChangesList);

                Alert alert=new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Experiment creation completed");
                alert.setHeaderText("Creating of a new experiment has been successful!");
                alert.setContentText("Experiment " + newExperimentName + " has been successfully saved.");

                alert.showAndWait();
            }else {

            }
        }


        newExperimentAuthorsListNames.clear();


    }

    public static void checkProgressNumber(String givenProgressNumber) throws ProgressNotANumber {
        for(char c: givenProgressNumber.toCharArray()){
            if(!Character.isDigit(c)){
                throw new ProgressNotANumber("Progress must be a number!");
            }
        }
    }

    public static void checkIfDateIsCorrect(LocalDate value)throws DateNull {
        if (value == null) {
            throw new DateNull("Staring date should be chosen!");
        }
    }

}
