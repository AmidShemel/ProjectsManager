import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Программа читає проекти з текстового файлу і показує в таблиці
 * Також присутня перевірка ліцензії
 */

public class Main extends Application {

    public static String projectsDB = "projectsDB.db";
    public static String tempDB = "tempDB.db";
    public static String simcoEdit = "simcoEdit.exe";
    public static String esprit = "esprit.exe";

    public static boolean btDelOff = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainStage.fxml"));
        primaryStage.setTitle("Project manager");
        primaryStage.setScene(new Scene(root, 650, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {

        optionsReader();

        try {
            if(args[0].equals("-d")){
                btDelOff = true;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("This program can work with arguments");
        }

        launch(args);

        try {
            SaveData.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error data saving");
        }
    }

    private static void optionsReader(){
        // "G:\IJ_Projects\WorkDB\settings.ini"
        //"L:\\IJ_Projects\\HizZtools_final\\settings.ini"
        String line = "";
        try {
            BufferedReader optionReader = new BufferedReader (new FileReader("settings.ini"));
            while ((line = optionReader.readLine())!= null){
                switch (line){
                    case "[BDPATH]":
                        projectsDB = line = optionReader.readLine();
                        break;
                    case "[BDTEMPPATH]":
                        tempDB = line = optionReader.readLine();
                        break;
                    case "[SIMCOPATH]":
                        simcoEdit = line = optionReader.readLine();
                        break;
                    case "[ESPRITPATH]":
                        esprit = line = optionReader.readLine();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
