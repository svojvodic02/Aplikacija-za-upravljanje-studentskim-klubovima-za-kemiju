package com.example.zavrsniprojektvojvodic.utils;

import com.example.zavrsniprojektvojvodic.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DatabaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String DATABASE_FILE = "conf/database.properties";


    private static Connection connectToDatabase() throws SQLException, IOException {
        Properties svojstva = new Properties();
        svojstva.load(new FileReader(DATABASE_FILE));
        String urlBazePodataka = svojstva.getProperty("databaseUrl");
        String korisnickoIme = svojstva.getProperty("username");
        String lozinka = svojstva.getProperty("password");
        Connection veza = DriverManager.getConnection(urlBazePodataka,
                korisnickoIme,lozinka);
        return veza;
    }


    public static  List<Student> getStudents(){

        List<Student> students=new ArrayList<>();

        try(Connection connection=connectToDatabase()){

            String sqlQuery="SELECT * FROM STUDENT ";
            Statement stm=connection.createStatement();
            stm.execute(sqlQuery);
            ResultSet rsl=stm.getResultSet();

            while(rsl.next()){
                Long studentId=rsl.getLong("ID");
                String studentFirstName=rsl.getString("FIRST_NAME");
                String studentLastName=rsl.getString("LAST_NAME");
                String studentOIB=rsl.getString("OIB");
                Student newStudent=new Student(studentFirstName,studentId,studentLastName,studentOIB);

                students.add(newStudent);
            }

        }catch (SQLException | IOException e){
            String message = "There was an error while reading from students database.";
            logger.error(message, e);
            System.out.println(message);
        }

        return students;
    }


    public static void saveNewStudent(List<Student> students) {

        try (Connection connection = connectToDatabase()) {

            for(Student newStudent : students) {
                String insertStudentSql = "INSERT INTO STUDENT (FIRST_NAME, LAST_NAME, OIB) VALUES(?, ?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(insertStudentSql);
                pstmt.setString(1, newStudent.getName());
                pstmt.setString(2,newStudent.getLastName());
                pstmt.setString(3, newStudent.getOIB());
                pstmt.execute();

            }

        } catch (SQLException | IOException ex) {
            String message = "Error while saving a new student into the database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static Long getNextStudentID(){
        List<Student> students=getStudents();

        Long lastStudentID=students.stream().map(s -> s.getId()).max((s1,s2)->s1.compareTo(s2)).get();

        return lastStudentID+1;
    }

    public static void updateStudentFirstName(Student studentToUpdate)
            throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateStudenti =
                connection.prepareStatement(
                        "UPDATE STUDENT SET FIRST_NAME = ? WHERE ID = ?");
        updateStudenti.setString(1, studentToUpdate.getName());
        updateStudenti.setLong(2, studentToUpdate.getId());
        updateStudenti.executeUpdate();
        connection.close();
    }

    public static void updateStudentLastName(Student studentToUpdate) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateStudenti =
                connection.prepareStatement(
                        "UPDATE STUDENT SET LAST_NAME = ? WHERE ID = ?");
        updateStudenti.setString(1, studentToUpdate.getLastName());
        updateStudenti.setLong(2, studentToUpdate.getId());
        updateStudenti.executeUpdate();
        connection.close();
    }

    public static void updateStudentOIB(Student studentToUpdate) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateStudenti =
                connection.prepareStatement(
                        "UPDATE STUDENT SET OIB = ? WHERE ID = ?");
        updateStudenti.setString(1, studentToUpdate.getOIB());
        updateStudenti.setLong(2, studentToUpdate.getId());
        updateStudenti.executeUpdate();
        connection.close();
    }


    public static void deleteStudent(Student studentToDelete) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();

        PreparedStatement deleteStudentAuthors =
                connection.prepareStatement(
                        "DELETE FROM EXPERIMENT_AUTHORS WHERE STUDENT_ID = ?");
        deleteStudentAuthors.setLong(1, studentToDelete.getId());
        deleteStudentAuthors.executeUpdate();

        PreparedStatement deleteStudent =
                connection.prepareStatement(
                        "DELETE FROM STUDENT WHERE OIB = ?");
        deleteStudent.setString(1, studentToDelete.getOIB());
        deleteStudent.executeUpdate();
        connection.close();
    }

    public static  List<String> getStudentName(List<Student> students){

        List<String> studentsNames = new ArrayList<>();

        try {
            for (int i = 0; i < students.size(); i++) {
                studentsNames.add(students.get(i).getName());
            }
        }catch (NullPointerException e){
            String message = "Error getting names of all students.";
            logger.error(message, e);
            System.out.println(message);
        }

        return studentsNames;
    }


    public static  List<Student> getAuthorsOfExperiment(Long givenExperimentID){

        List<Student> allStudents=getStudents();
        List<Student> experimentAuthors=new ArrayList<>();

        try(Connection connection=connectToDatabase()){

            String sqlQuery="SELECT * FROM EXPERIMENT_AUTHORS";
            Statement stm=connection.createStatement();
            stm.execute(sqlQuery);
            ResultSet rsl=stm.getResultSet();

            while(rsl.next()){
                Long experimentId=rsl.getLong("EXPERIMENT_ID");
                Long studentId=rsl.getLong("STUDENT_ID");

                if(experimentId.equals(givenExperimentID)){
                    for(int i=0;i<allStudents.size();i++){
                        if(allStudents.get(i).getId().equals(studentId)){
                            experimentAuthors.add(allStudents.get(i));
                        }
                    }
                }
            }

        }catch (SQLException | IOException e){
            String message = "Error while getting the experiment authors.";
            logger.error(message, e);
            System.out.println(message);
        }


        return experimentAuthors;
    }

    public static List<Experiment> getExperiment(){

        List<Experiment> experiments=new ArrayList<>();

        try(Connection connection=connectToDatabase()){

            String sqlQuery="SELECT * FROM EXPERIMENT ";
            Statement stm=connection.createStatement();
            stm.execute(sqlQuery);
            ResultSet rsl=stm.getResultSet();

            while(rsl.next()){
                Long experimentId=rsl.getLong("ID");
                String experimentName=rsl.getString("NAME");
                LocalDate experimentStartDate = rsl.getDate("START_DATE").toLocalDate();
                LocalDate experimentEndDate;
                if(rsl.getDate("END_DATE")==null){
                    experimentEndDate=null;
                }else {
                    experimentEndDate = rsl.getDate("END_DATE").toLocalDate();
                }
                String experimentNotes=rsl.getString("NOTES");
                String experimentProgressString=rsl.getString("PROGRESS");
                Integer experimentProgress=Integer.parseInt(experimentProgressString);
                List<Student> experimentAuthors=getAuthorsOfExperiment(experimentId);


                Experiment newExperiment= new Experiment.Builder(experimentName).setId(experimentId).
                        setAuthors(experimentAuthors).setDateStarted(experimentStartDate).
                        setDateFinished(experimentEndDate).setNotes(experimentNotes).
                        setProgress(experimentProgress).createExperiment();

                experiments.add(newExperiment);
            }

        }catch (SQLException | IOException e){
            String message = "There was a error while reading from experiment database.";
            logger.error(message, e);
            System.out.println(message);
        }

        return experiments;
    }

    public static void saveExperiment(List<Experiment> experiments) {

        try (Connection connection = connectToDatabase()) {

            for(Experiment newExperiment : experiments) {
                String insertExperimentSql = "INSERT INTO EXPERIMENT(NAME, START_DATE, END_DATE, NOTES," +
                            "PROGRESS) VALUES(?, ?, ?, ?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(insertExperimentSql);
                ResultSet rs= pstmt.getResultSet();
                pstmt.setString(1, newExperiment.getName());
                pstmt.setDate(2, new Date(newExperiment.getDateStarted().atStartOfDay().atZone(
                        ZoneId.systemDefault()).toInstant().toEpochMilli()));
                if(newExperiment.getDateFinished()==null){
                    pstmt.setDate(3,null);
                }else {
                    pstmt.setDate(3, new Date(newExperiment.getDateFinished().atStartOfDay().atZone(
                            ZoneId.systemDefault()).toInstant().toEpochMilli()));
                }
                pstmt.setString(4, newExperiment.getNotes());
                pstmt.setInt(5,newExperiment.getProgress());
                pstmt.execute();

            }

        } catch (SQLException | IOException ex) {
            String message = "Error while saving a new experiment into the database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void saveExperimentAuthors(List<Student> experimentAuthors,Long experimentId){

        try (Connection connection = connectToDatabase()) {

            for(Student oneExperimentAuthor : experimentAuthors) {
                String insertExperimentAuthorSql = "INSERT INTO EXPERIMENT_AUTHORS(EXPERIMENT_ID,STUDENT_ID) VALUES(?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(insertExperimentAuthorSql);
                pstmt.setLong(1, experimentId);
                pstmt.setLong(2, oneExperimentAuthor.getId());
                pstmt.execute();

            }

        } catch (SQLException | IOException ex) {
            String message = "Error while saving a new experiment author into the database!";
            logger.error(message, ex);
            System.out.println(message);
        }

    }

    public static Long getNextExperimentId(){
        List<Experiment> experiments=getExperiment();

        Long lastExperimentId=experiments.stream().map(e -> e.getId()).max((e1,e2)->e1.compareTo(e2)).get();

        return lastExperimentId+1;
    }

    public static  List<String> getExperimentName(List<Experiment> experiments){

        List<String> experimentNames = new ArrayList<>();

        try {
            for (int i = 0; i < experiments.size(); i++) {
                experimentNames.add(experiments.get(i).getName());
            }
        }catch (NullPointerException e){
            String message = "Error getting names of all experiments.";
            logger.error(message, e);
            System.out.println(message);
        }

        return experimentNames;
    }

    public static void updateExperimentName(Experiment experimentToUpdate) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateExperiment =
                connection.prepareStatement(
                        "UPDATE EXPERIMENT SET NAME = ? WHERE ID = ?");
        updateExperiment.setString(1, experimentToUpdate.getName());
        updateExperiment.setLong(2, experimentToUpdate.getId());
        updateExperiment.executeUpdate();
        connection.close();
    }
    public static void updateExperimentStartingDate(Experiment experimentToUpdate) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateExperiment =
                connection.prepareStatement(
                        "UPDATE EXPERIMENT SET START_DATE = ? WHERE ID = ?");
        updateExperiment.setDate(1, new Date(experimentToUpdate.getDateStarted().atStartOfDay().atZone(
                ZoneId.systemDefault()).toInstant().toEpochMilli()));
        updateExperiment.setLong(2, experimentToUpdate.getId());
        updateExperiment.executeUpdate();
        connection.close();
    }
    public static void updateExperimentEndingDate(Experiment experimentToUpdate) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateExperiment =
                connection.prepareStatement(
                        "UPDATE EXPERIMENT SET END_DATE = ? WHERE ID = ?");
        updateExperiment.setDate(1, new Date(experimentToUpdate.getDateFinished().atStartOfDay().atZone(
                ZoneId.systemDefault()).toInstant().toEpochMilli()));
        updateExperiment.setLong(2, experimentToUpdate.getId());
        updateExperiment.executeUpdate();
        connection.close();
    }
    public static void updateExperimentNotes(Experiment experimentToUpdate) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateExperiment =
                connection.prepareStatement(
                        "UPDATE EXPERIMENT SET NOTES = ? WHERE ID = ?");
        updateExperiment.setString(1,experimentToUpdate.getNotes());
        updateExperiment.setLong(2, experimentToUpdate.getId());
        updateExperiment.executeUpdate();
        connection.close();
    }
    public static void updateExperimentProgress(Experiment experimentToUpdate) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateExperiment =
                connection.prepareStatement(
                        "UPDATE EXPERIMENT SET PROGRESS = ? WHERE ID = ?");
        updateExperiment.setInt(1,experimentToUpdate.getProgress());
        updateExperiment.setLong(2, experimentToUpdate.getId());
        updateExperiment.executeUpdate();
        connection.close();
    }

    public static void deleteExperiment(Experiment experimentToDelete)
            throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        deleteExperimentAuthors(experimentToDelete.getId());
        PreparedStatement deleteExperiment =
                connection.prepareStatement(
                        "DELETE FROM EXPERIMENT WHERE ID = ?");
        deleteExperiment.setLong(1, experimentToDelete.getId());
        deleteExperiment.executeUpdate();



        connection.close();
    }

    public static void deleteExperimentAuthors(Long experimentToDeleteId)throws SQLException, IOException {
        Connection connection = connectToDatabase();
        PreparedStatement deleteExperimentAuthors =
                connection.prepareStatement(
                        "DELETE FROM EXPERIMENT_AUTHORS WHERE EXPERIMENT_ID = ?");
        deleteExperimentAuthors.setLong(1, experimentToDeleteId);
        deleteExperimentAuthors.executeUpdate();
    }

    public static List<ChemistryClub> getChemistryClub(){

        List<ChemistryClub> chemistryClubs=new ArrayList<>();

        try(Connection connection=connectToDatabase()){

            String sqlQuery="SELECT * FROM CHEMISTRY_CLUB";
            Statement stm=connection.createStatement();
            stm.execute(sqlQuery);
            ResultSet rsl=stm.getResultSet();

            while(rsl.next()){
                Long chemistryClubId=rsl.getLong("ID");
                String chemistryClubName=rsl.getString("NAME");
                List<Experiment> chemistryClubExperiments=getChemistryClubExperiments(chemistryClubId);
                List<Student> chemistryClubMembers=getMembersOfChemistryClub(chemistryClubId);


                ChemistryClub newChemistryClub=new ChemistryClub(chemistryClubName,chemistryClubId,chemistryClubExperiments,chemistryClubMembers);

                chemistryClubs.add(newChemistryClub);
            }

        }catch (SQLException | IOException e){
            String message = "There was an error while reading from Chemistry clubs database.";
            logger.error(message, e);
            System.out.println(message);
        }

        return chemistryClubs;
    }

    public static  List<Student> getMembersOfChemistryClub(Long givenChemistryClubID){

        List<Student> allStudents=getStudents();
        List<Student> chemistryClubMembers=new ArrayList<>();

        try(Connection connection=connectToDatabase()){

            String sqlQuery="SELECT * FROM CHEMISTRY_CLUB_MEMBERS ";
            Statement stm=connection.createStatement();
            stm.execute(sqlQuery);
            ResultSet rsl=stm.getResultSet();

            while(rsl.next()){
                Long chemistryClubId=rsl.getLong("CHEMISTRY_CLUB_ID");
                Long studentId=rsl.getLong("STUDENT_ID");

                if(chemistryClubId.equals(givenChemistryClubID)){
                    for(int i=0;i<allStudents.size();i++){
                        if(allStudents.get(i).getId().equals(studentId)){
                            chemistryClubMembers.add(allStudents.get(i));
                        }
                    }
                }
            }

        }catch (SQLException | IOException e){
            String message = "Error while getting the chemistry club members.";
            logger.error(message, e);
            System.out.println(message);
        }


        return chemistryClubMembers;
    }

    public static  List<Experiment> getChemistryClubExperiments(Long givenChemistryClubID){

        List<Experiment> allExperiments=getExperiment();
        List<Experiment> chemistryClubExperiments=new ArrayList<>();

        try(Connection connection=connectToDatabase()){

            String sqlQuery="SELECT * FROM CHEMISTRY_CLUB_EXPERIMENTS ";
            Statement stm=connection.createStatement();
            stm.execute(sqlQuery);
            ResultSet rsl=stm.getResultSet();

            while(rsl.next()){
                Long chemistryClubId=rsl.getLong("CHEMISTRY_CLUB_ID");
                Long experimentId=rsl.getLong("EXPERIMENT_ID");

                if(chemistryClubId.equals(givenChemistryClubID)){
                    for(int i=0;i<allExperiments.size();i++){
                        if(allExperiments.get(i).getId().equals(experimentId)){
                            chemistryClubExperiments.add(allExperiments.get(i));
                        }
                    }
                }
            }

        }catch (SQLException | IOException e){
            String message = "Error while getting the chemistry club members.";
            logger.error(message, e);
            System.out.println(message);
        }


        return chemistryClubExperiments;
    }
    public static void saveChemistryClub(List<ChemistryClub> chemistryClubs) {

        try (Connection connection = connectToDatabase()) {


            for(ChemistryClub newChemistryClub : chemistryClubs) {

                String restartIncrement = "ALTER TABLE CHEMISTRY_CLUB ALTER COLUMN ID RESTART WITH ?";
                PreparedStatement restart = connection.prepareStatement(restartIncrement);
                restart.setLong(1, newChemistryClub.getId());
               restart.execute();
                String insertChemistryClubSql = "INSERT INTO CHEMISTRY_CLUB(NAME) VALUES(?);";
                PreparedStatement pstmt = connection.prepareStatement(insertChemistryClubSql);
                pstmt.setString(1, newChemistryClub.getName());
                pstmt.execute();

            }

        } catch (SQLException | IOException ex) {
            String message = "Error while saving a new chemistry club into the database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }
    public static void saveChemistryClubExperiments(List<Experiment> experiments,Long chemistryClubID) {

        try (Connection connection = connectToDatabase()) {

            for(Experiment oneChemistryClubExperiment : experiments) {
                String insertChemistryClubExperimentSql = "INSERT INTO CHEMISTRY_CLUB_EXPERIMENTS (CHEMISTRY_CLUB_ID,EXPERIMENT_ID) VALUES(?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(insertChemistryClubExperimentSql);
                pstmt.setLong(1, chemistryClubID);
                pstmt.setLong(2, oneChemistryClubExperiment.getId());
                pstmt.execute();

            }

        } catch (SQLException | IOException ex) {
            String message = "Error while saving a new chemistry club experiment into the database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static void saveChemistryClubMembers(List<Student> clubMembers,Long chemistryClubID) {

        try (Connection connection = connectToDatabase()) {

            for(Student oneClubMember : clubMembers) {
                String insertChemistryClubExperimentSql = "INSERT INTO CHEMISTRY_CLUB_MEMBERS  (CHEMISTRY_CLUB_ID,STUDENT_ID) VALUES(?, ?);";
                PreparedStatement pstmt = connection.prepareStatement(insertChemistryClubExperimentSql);
                pstmt.setLong(1, chemistryClubID);
                pstmt.setLong(2, oneClubMember.getId());
                pstmt.execute();

            }

        } catch (SQLException | IOException ex) {
            String message = "Error while saving a new club member into the database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }


    public static Long getNextChemistryClubID(){
        List<ChemistryClub> students=getChemistryClub();

        Long lastChemistryClubID=students.stream().map(c -> c.getId()).max((c1,c2)->c1.compareTo(c2)).get();

        return lastChemistryClubID+1;
    }

    public static void updateChemistryClubName(ChemistryClub chemistryClubToUpdate) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement updateChemistryClub =
                connection.prepareStatement(
                        "UPDATE CHEMISTRY_CLUB  SET NAME = ? WHERE ID = ?");
        updateChemistryClub.setString(1, chemistryClubToUpdate.getName());
        updateChemistryClub.setLong(2, chemistryClubToUpdate.getId());
        updateChemistryClub.executeUpdate();
        connection.close();
    }

    public static void deleteChemistryClub(ChemistryClub chemistryClubToDelete) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();

        chemistryClubExperimentsToDelete(chemistryClubToDelete);
        chemistryClubMembersToDelete(chemistryClubToDelete);
        PreparedStatement deleteChemistryClub =
                connection.prepareStatement(
                        "DELETE FROM CHEMISTRY_CLUB  WHERE ID = ?");
        deleteChemistryClub.setLong(1, chemistryClubToDelete.getId());
        deleteChemistryClub.executeUpdate();

    }
    public static void chemistryClubMembersToDelete(ChemistryClub chemistryClub)  throws SQLException, IOException{
        Connection connection = connectToDatabase();
            PreparedStatement deleteChemistryClubMembers =
                    connection.prepareStatement(
                            "DELETE FROM CHEMISTRY_CLUB_MEMBERS  WHERE CHEMISTRY_CLUB_ID = ?");
            deleteChemistryClubMembers.setLong(1, chemistryClub.getId());
            deleteChemistryClubMembers.executeUpdate();
    }
    public static void chemistryClubExperimentsToDelete(ChemistryClub chemistryClub)  throws SQLException, IOException{
        Connection connection = connectToDatabase();
        PreparedStatement deleteChemistryClubExperiments =
                connection.prepareStatement(
                        "DELETE FROM CHEMISTRY_CLUB_EXPERIMENTS   WHERE CHEMISTRY_CLUB_ID = ?");
        deleteChemistryClubExperiments.setLong(1, chemistryClub.getId());
        deleteChemistryClubExperiments.executeUpdate();
    }

    public static List<CommentRecord> getCommentsRecord(){

        List<CommentRecord> commentList=new ArrayList<>();

        try(Connection connection=connectToDatabase()){

            String sqlQuery="SELECT * FROM COMMENT";
            Statement stm=connection.createStatement();
            stm.execute(sqlQuery);
            ResultSet rsl=stm.getResultSet();

            while(rsl.next()){
                Long commentId=rsl.getLong("ID");
                String commentComment=rsl.getString("COMMENT");
                Long userId=rsl.getLong("USER_ID");
                Long experimentID=rsl.getLong("ID_OF_COMMENTED_EXPERIMENT");

                CommentRecord newComment=new CommentRecord(commentId,commentComment,userId,experimentID);

                commentList.add(newComment);
            }

        }catch (SQLException | IOException e){
            String message = "There was an error while reading from Comments database.";
            logger.error(message, e);
            System.out.println(message);
        }

        return commentList;
    }
    public static void saveCommentsRecords(List<CommentRecord> commentsList){
        try (Connection connection = connectToDatabase()) {

            for(CommentRecord newComment : commentsList) {
                String insertCommentSql = "INSERT INTO COMMENT(COMMENT,USER_ID,ID_OF_COMMENTED_EXPERIMENT) VALUES(?,?,?);";
                PreparedStatement pstmt = connection.prepareStatement(insertCommentSql);
                pstmt.setString(1, newComment.comment());
                pstmt.setLong(2, newComment.id());
                pstmt.setLong(3, newComment.idOfCommentedExperiment());
                pstmt.execute();
            }

        } catch (SQLException | IOException ex) {
            String message = "Error while saving a new comment into the database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static Long getNextCommentId(){
        List<CommentRecord> comments=getCommentsRecord();

        Long lastCommentId=comments.stream().map(c -> c.id()).max((c1,c2)->c1.compareTo(c2)).get();

        return lastCommentId+1;
    }

    public static List<ChemistryClub> getChemistryClubByFilters(ChemistryClubFilter chemistryClubFilter) {
        List<ChemistryClub> chemistryClubs = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        Integer paramOrdinalNumber = 1;

        try (Connection connection = connectToDatabase()) {
            String baseSqlQuery = "SELECT * FROM CHEMISTRY_CLUB WHERE 1=1 ";

            if(chemistryClubFilter.getChemistryClubId()==0){

            } else if (chemistryClubFilter.getChemistryClubId()!=null) {
                baseSqlQuery += " AND ID= ?";
                queryParams.put(paramOrdinalNumber, chemistryClubFilter.getChemistryClubId());
                paramOrdinalNumber++;
            }

            if (!chemistryClubFilter.getChemistryClubName().isEmpty()) {
                baseSqlQuery += " AND NAME = ?";
                queryParams.put(paramOrdinalNumber, chemistryClubFilter.getChemistryClubName());
                paramOrdinalNumber++;
            }

            PreparedStatement pstmt = connection.prepareStatement(baseSqlQuery);

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String sqp) {
                    pstmt.setString(paramNumber, sqp);
                } else if (queryParams.get(paramNumber) instanceof Long iqp) {
                    pstmt.setLong(paramNumber, iqp);
                }
            }

            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            mapResultSetToChemistryClubList(rs, chemistryClubs);
        } catch (SQLException | IOException ex) {
            String message = "Error while reading chemistry clubs from the database!";
            logger.error(message, ex);
            System.out.println(message);
        }

        return chemistryClubs;
    }

    private static void mapResultSetToChemistryClubList(ResultSet rs, List<ChemistryClub> chemistryClubs) throws SQLException {
        while (rs.next()) {
            ChemistryClub chemistryClub = getChemistryClubFromResultSet(rs);
            chemistryClubs.add(chemistryClub);
        }
    }

    private static ChemistryClub getChemistryClubFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID");
        String name = rs.getString("NAME");
        List<Student> students=getMembersOfChemistryClub(id);
        List<Experiment> experiments=getChemistryClubExperiments(id);

        ChemistryClub chemistryClub = new ChemistryClub(name,id,experiments,students);
        return chemistryClub;
    }

}
