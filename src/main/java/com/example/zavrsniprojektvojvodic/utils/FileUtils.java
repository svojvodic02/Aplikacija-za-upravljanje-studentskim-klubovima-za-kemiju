package com.example.zavrsniprojektvojvodic.utils;

import com.example.zavrsniprojektvojvodic.domain.Changes;
import com.example.zavrsniprojektvojvodic.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String LOGIN_FILE = "dat/login.txt";

    private static final String SERIALIZATION_FILE_NAME="dat/serialized.bin";

    public static void saveLoginInFile(List<User> logins){

        File categoryFile=new File(LOGIN_FILE);

        try(PrintWriter pw=new PrintWriter(categoryFile)){

            for(User user : logins){
                pw.println(user.getName());
                pw.println(user.getLastName());
                pw.println(user.getEmail());
                pw.println(user.getPassword());
                pw.println(user.getIsTheUserAnAdmin());
                pw.println(user.getId());
                pw.println(user.getOIB());
            }

        }catch(IOException e){
            String message = "Dogodila se pogreška kod pisanja datoteke login.txt!";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static String hashingTheGivenPassword(String givenPassword){
        int hash = 7;
        for (int i = 0; i < givenPassword.length(); i++) {
            hash = hash*31 + givenPassword.charAt(i);
        }
        String hashedPassword=String.valueOf(hash);
        return HexFormat.of().formatHex(hashedPassword.getBytes());
    }

    public static List<User> getUserFromFile(){

        List<User> userList=new ArrayList<>();

        File userFile=new File(LOGIN_FILE);

        try(BufferedReader reader=new BufferedReader(new FileReader(userFile))){

            Optional<String> userOptional=Optional.empty();

            while((userOptional = Optional.ofNullable(reader.readLine())).isPresent()){

                Optional<User> newUserOptional=Optional.empty();

                String userFirstName=userOptional.get();
                String userLastName= reader.readLine();
                String userEmail= reader.readLine();
                String userPassword= reader.readLine();
                String isUserAdmin=reader.readLine();
                String stringUserId=reader.readLine();
                Long userId=Long.parseLong(stringUserId);
                String userOIB=reader.readLine();


                newUserOptional=Optional.of(new User(userFirstName,userLastName,userEmail,userPassword,isUserAdmin,userId,userOIB));

                if(newUserOptional.isPresent()){
                    userList.add(newUserOptional.get());
                }
            }

        }catch(IOException ex){
            String message = "Dogodila se pogreška kod čitanja datoteke category.txt!";
            //logger.error(message, ex);
            System.out.println(message);
        }

        return userList;
    }


    public static Long getNextUserId(){

        List<User> users=getUserFromFile();

        Long lastItemId=users.stream().map(u -> u.getId()).max((u1,u2)->u1.compareTo(u2)).get();

        return lastItemId+1;

    }

    /*public static void serializeChanges(Changes changesToSerialize) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE_NAME))) {
            oos.writeObject(changesToSerialize.getWhoDidTheChange());
            oos.writeObject(changesToSerialize.getWhatWasTheChange());
            oos.writeObject(changesToSerialize.getWhatTimeTheChangeOccured());
        }
        catch(IOException ex) {
            System.out.println(ex);
            logger.error("Serialization error!", ex);
        }
    }*/

    public static void serializeChanges(List<Changes<String,String>> changesList){
        File serializeChangesFile = new File(SERIALIZATION_FILE_NAME);

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializeChangesFile)))
        {
            oos.writeObject(changesList);
        }  catch(IOException ex) {
            System.out.println(ex);
            logger.error("Serialization error!", ex);
        }
    }


  public static List<Changes<String,String>> deserializeChanges()
    {
        List<Changes<String,String>> changesList = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE_NAME))) {
            changesList = (List<Changes<String,String>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
            logger.error("Deserialization error!", e);
        }


        return changesList;
    }


}

