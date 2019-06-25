import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 *
 */

public class Controller extends Observable implements Initializable {

    private ObservableList<Project> ProjectList1 = FXCollections.observableArrayList();
    private ObservableList<Project> ProjectList2 = FXCollections.observableArrayList();
    private ObservableList<Project> backupList;
    private String projectDB = Main.projectsDB;
    private String tempDB = Main.tempDB;
    private String LS = System.getProperty("line.separator");
    private ContextMenu cm;
    private MenuItem mi1, mi2;

    // FXMLs
    @FXML
    private VBox mainWindow;
    @FXML
    private Button btnMarge;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDelete1;
    @FXML
    private Button btnDelete2;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label labelCount;
    @FXML
    private CheckBox advancedSearch;
    @FXML
    private TableView tableViewOne;
    @FXML
    private TableView tableViewTwo;
    @FXML
    private TableColumn<Project, String> tabOneColName;
    @FXML
    private TableColumn<Project, String> tabOneColDate;
    @FXML
    private TableColumn<Project, String> tabOneColPath;
    @FXML
    private TableColumn<Project, String> tabTwoColName;
    @FXML
    private TableColumn<Project, String> tabTwoColDate;
    @FXML
    private TableColumn<Project, String> tabTwoColPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SaveData.deSerialize();

        //this.resourceBundle = resources;
        txtSearch.setPromptText("Пошук проекту");

        tabOneColName.setCellValueFactory(new PropertyValueFactory<Project, String>("projectName"));
        tabOneColDate.setCellValueFactory(new PropertyValueFactory<Project, String>("projectDate"));
        tabOneColPath.setCellValueFactory(new PropertyValueFactory<Project, String>("projectPath"));

        tabTwoColName.setCellValueFactory(new PropertyValueFactory<Project, String>("projectName"));
        tabTwoColDate.setCellValueFactory(new PropertyValueFactory<Project, String>("projectDate"));
        tabTwoColPath.setCellValueFactory(new PropertyValueFactory<Project, String>("projectPath"));

        initContextMenu();
        initListeners();
        fillData();
        enableKeys();
        //initLoader();

    }

    private void enableKeys(){
        btnDelete1.setVisible(Main.btDelOff);
    }

    private void fillData() {
        fillTableOne();
        fillTableTwo();
    }

    private void fillTableOne(){
        ProjectList1.clear();
        fillProjectData(projectDB);
        backupList = FXCollections.observableArrayList();
        backupList.addAll(getProjectList1());
        tableViewOne.setItems(getProjectList1());
    }

    private void fillTableTwo(){
        ProjectList2.clear();
        fillProjectData(tempDB);
        tableViewTwo.setItems(getProjectList2());
        //tableViewTwo.refresh();
    }

    private void initContextMenu(){
        cm = new ContextMenu();
        mi1 = new MenuItem("Показати в папці");
        cm.getItems().add(mi1);
        mi2 = new MenuItem("Відкрити папку");
        cm.getItems().add(mi2);
        cm.setAutoHide(true);
    }

    private void initListeners(){

        // Оновлення лічильника загальної кількості проектів
        getProjectList1().addListener(new ListChangeListener<Project>() {
            @Override
            public void onChanged(Change<? extends Project> c) {
                updateCountLabel();
            }
        });

        // Реагує на подвійній клік миші
        tableViewOne.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2){
                    //System.out.println("double click on " + (tableViewOne.getSelectionModel().getFocusedIndex() + 1) + "th row" );
                    String path = ((Project) tableViewOne.getSelectionModel().getSelectedItem()).getProjectPath();
                    //Запуск проекту в CIMCOEdit.exe
                    try {
                        Process process = new ProcessBuilder(Main.esprit, path).start();
                    } catch (IOException e) {
                        Dialog.infoBox("Помилка відкриття файлу\n" + e.toString(), "Помилка");
                    }
                }
            }
        });

        // Реагує на натскання лівої клавіші миші (контекстне меню)
        tableViewOne.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY) {
                    cm.show(tableViewOne, event.getScreenX(), event.getScreenY());
                } else {
                    cm.hide();
                }
            }
        });

        //Контекстне меню: відкриває папку в якій знаходиться проект та виділяє його
        mi1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //System.out.println((Project)tableViewOne.getSelectionModel().getSelectedItem());
                String path = ((Project) tableViewOne.getSelectionModel().getSelectedItem()).getProjectPath();
                System.out.println(path);
                try {
                    Runtime.getRuntime().exec("explorer.exe /select," + path);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });

        //Контекстне меню: відкриває папку в якій знаходиться проект
        mi2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //System.out.println((Project)tableViewOne.getSelectionModel().getSelectedItem());
                String path = ((Project) tableViewOne.getSelectionModel().getSelectedItem()).getProjectPath();
                path = path.substring(0, path.lastIndexOf("\\"));
                System.out.println(path);
                try {
                    Runtime.getRuntime().exec("explorer.exe /open," + path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Видаляє виділений прокт по натисканні клавіші DELETE
        tableViewTwo.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()==KeyCode.DELETE){
                    if(!projectIsSelected(tableViewTwo)) return;
                    if (!Dialog.confirmBox("Видалити проект з історії?", "УВАГА!")) return;
                    deleteProjectRow(tempDB, 2);
                    fillTableTwo();
                }
            }
        });

        //Виконує пошук проекту з кожним введенням нового символу
        // Якщо в полі пошуку введений тект деактивує кнопку 'Видалити'
        txtSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                actionSearch();
                if(txtSearch.getText().equals("")){
                    btnDelete1.setDisable(false);
                } else {
                    btnDelete1.setDisable(true);
                }
            }
        });

        // Вмикає приховану кнопку 'Видалити' натисканням поєднанням клавіш ctrl + Q
        mainWindow.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode()== KeyCode.Q && event.isControlDown()){
                    //btnAdd.setDisable(!btnAdd.isDisable());
                    btnDelete1.setVisible(!btnDelete1.isVisible());
                }
            }
        });
    }

    private void updateCountLabel(){
        //labelCount.setText(resourceBundle.getString("text_count_rows") + ": "
        labelCount.setText("Загальна кількість проектів: " + getProjectList1().size());

    }

    public void actionButtonPressed(ActionEvent actionEvent){

        Object source = actionEvent.getSource();
        Button button = (Button)source;

//        if(!(source instanceof Button)) {
//            return;
//        }

        switch (button.getId()){
//            case "btnAdd":
//                System.out.println("Add");
//                if(projectIsSelected(selectedProject)){
//                    System.out.println(numOfRow);
//                }
//                //editDialogController.setProject(new Project());
//                showDialog();
//                if(editDialogController.getProject().getProjectName().trim().length()==0 || editDialogController.getProject().getProjectDate().trim().length()==0){
//                    System.out.println("FIO or NUM = NULL");
//                }else {
//                    add(editDialogController.getProject());
//                    fileIOAdresBook.add(editDialogController.getProject());
//                }
//                break;
//            case "btnEdit":
//                System.out.println("Edit");
//
//
//                if(!ProjectIsSelected(selectedProject)){
//                    return;
//                }
//                editDialogController.setProject(selectedProject);
//                showDialog();
//                break;
//            case "btnSearch":
//                System.out.println("Search");
//                actionSearch();
//                break;
            case "btnMarge":
//                System.out.println("Marge");
                margeProjects(tempDB);
                fillTableTwo();
                break;
            case "btnSave":
//                System.out.println("Save");
                saveProject(tempDB, projectDB);
                fillData();
                break;
            case "btnDelete1":
//                System.out.println("Delete1");
                if(!projectIsSelected(tableViewOne)) return;
                if (!Dialog.confirmBox("Видалити проект з історії?", "УВАГА!")) return;
                deleteProjectRow(projectDB, 1);
                fillTableOne();
                break;
            case "btnDelete2":
//                System.out.println("Delete2");
                if(!projectIsSelected(tableViewTwo)) return;
                if (!Dialog.confirmBox("Видалити проект з історії?", "УВАГА!")) return;
                deleteProjectRow(tempDB, 2);
                fillTableTwo();
                break;
        }
    }

    private boolean projectIsSelected(TableView table){

        Project selectedProject = (Project) table.getSelectionModel().getSelectedItem();

        if(selectedProject == null){
            Dialog.infoBox("Виберіть проект для видалення", "Помилка");
            return  false;
        }
        return true;
    }

    public void actionSearch(){
        getProjectList1().clear();
        for(Project project: backupList){
            if(project.getProjectName().toLowerCase().contains(txtSearch.getText().toLowerCase())           //Шукати за ім'ям проекту
                    || project.getProjectDate().toLowerCase().contains(txtSearch.getText().toLowerCase())   //Шукати за датою проекту
                    || project.getProjectPath().toLowerCase().contains(txtSearch.getText().toLowerCase())   //Шукати за розташуванням проекту
                    && advancedSearch.isSelected()
            ){
                getProjectList1().add(project);
            } else if(project.getProjectName().toLowerCase().contains(txtSearch.getText().toLowerCase())           //Шукати за ім'ям проекту
                    || project.getProjectDate().toLowerCase().contains(txtSearch.getText().toLowerCase())          //Шукати за датою проекту
            ) {
                getProjectList1().add(project);
            }
        }
    }

    public ObservableList<Project> getProjectList1() {
        return ProjectList1;
    }

    public ObservableList<Project> getProjectList2() {
        return ProjectList2;
    }

    public void fillProjectData(String filePath){
        try {

            BufferedReader bReader = new BufferedReader(new FileReader(filePath));

            String line = "";
            String delimiter = "|";

            while ((line = bReader.readLine()) != null) {
                String projName="", projData="", projPath = "";
                int p1 = 0;

                for(int i1=0; i1<3; i1++){
                    switch (i1){
                        case 0:
                            projName = line.substring(p1, line.indexOf(delimiter));
                            break;
                        case 1:
                            projData = line.substring(p1, line.indexOf(delimiter, p1));
                            break;
                         case 2:
                            projPath = line.substring(p1, line.length());
                            break;
                    }
                    p1 = line.indexOf(delimiter, p1)+1;
                }
                if(filePath.equals(projectDB)){
                    ProjectList1.add(new Project(projName, projData, projPath));
                } else {
                    ProjectList2.add(new Project(projName, projData, projPath));
                }

            }

            bReader.close();

            System.out.println("Rows readed from file: " + filePath);

        } catch (Exception e){
            System.out.println(e.getMessage());
            Dialog.infoBox(e.getMessage(), "Помилка");
        }
    }

    private void margeProjects(String filePath){

        try {
            BufferedReader bReader = new BufferedReader(new FileReader(filePath));

            String[] mass = new String[getProjectList2().size()];
            String line = "";
            int count1;

            while ((line = bReader.readLine()) != null) {
                count1 = 0;
                while (true){
                    if (line.equals(mass[count1])) {
                        break;
                    } else {
                        if (mass[count1]==null){
                            mass[count1] = line;
                            break;
                        }
                        count1++;
                    }
                }
            }

            BufferedWriter bWriter = new BufferedWriter(new FileWriter(filePath));
            for (String s : mass) {
                if (s != null) {
                    bWriter.write(s + LS);
                } else {
                    break;
                }
            }
            bWriter.close();
            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Dialog.infoBox(e.getMessage(), "Помилка");
        }
    }

    private void saveProject(String fromFile, String toFile){

        BufferedReader bReader = null;
        try {
            bReader = new BufferedReader(new FileReader(fromFile));
            String line = "", line2 = "";
            while ((line = bReader.readLine()) != null){
                line2 = line2 + line + LS;
            }
            bReader.close();

            FileWriter fWriter = new FileWriter(toFile, true);
            fWriter.write(line2);
            fWriter.close();
            FileWriter fWriter2 = new FileWriter(fromFile, false);
            fWriter2.write("");
            fWriter2.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Dialog.infoBox(e.getMessage(), "Помилка");
        } catch (IOException e) {
            e.printStackTrace();
            Dialog.infoBox(e.getMessage(), "Помилка");
        }
    }

    private void deleteProjectRow(String filePath, int numButton){

        int count = -1, sToDel;

        switch (numButton){
            case 1:
                sToDel = tableViewOne.getSelectionModel().getFocusedIndex();
                break;
            case 2:
                sToDel = tableViewTwo.getSelectionModel().getFocusedIndex();
                break;
            default:
                return;
        }


        try{
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            String line = "", line2 = "";

            while ((line = br.readLine()) != null) {
                count++;
                if(count!=sToDel) line2 += line + LS;
            }
            br.close();

            FileWriter pw = new FileWriter (filePath, false);
            //System.out.println(line2);
            pw.write(line2);
            pw.close();

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

//    private boolean confirmToDelete(){
//
//        Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
//        alert.setTitle ("УВАГА!");
//        alert.setHeaderText (null);
//        alert.setContentText ("Видалити проект з історії?");
//
//        Optional<ButtonType> result = alert.showAndWait ();
//        if(result.get() == ButtonType.OK){
//            return  true;
//        }
//        return false;
//    }
//    private void showMessage(String title, String headerText, String contentText){
//        Alert alert = new Alert (Alert.AlertType.INFORMATION);
//        alert.setTitle (title);
//        alert.setHeaderText (headerText);
//        alert.setContentText (contentText);
//        alert.showAndWait ();
//    }

//    private void inputDialog(){
//        TextInputDialog dialog = new TextInputDialog("walter");
//        dialog.setTitle("Text Input Dialog");
//        dialog.setHeaderText("Look, a Text Input Dialog");
//        dialog.setContentText("Please enter your name:");
//
//        // Traditional way to get the response value.
//        Optional<String> result = dialog.showAndWait();
//        if (result.isPresent()){
//            System.out.println("Your name: " + result.get());
//        }
//
//        // The Java 8 way to get the response value (with lambda expression).
//        result.ifPresent(name -> System.out.println("Your name: " + name));
//    }

//    public void twoInputs(){
//        // Create the custom dialog.
//        Dialog<Pair<String, String>> dialog = new Dialog<>();
//        dialog.setTitle("TestName");
//        dialog.setHeaderText("header is here");
//
//        // Set the button types.
//        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
//
//        GridPane gridPane = new GridPane();
//        gridPane.setHgap(10);
//        gridPane.setVgap(10);
//        gridPane.setPadding(new Insets(20, 10, 10, 10));
//
//        TextField from = new TextField();
//        from.setPromptText("From");
//        TextField to = new TextField();
//        to.setPromptText("To");
//
//        gridPane.add(from, 0, 0);
//        //gridPane.add(new Label("To:"), 1, 0);
//        gridPane.add(to, 2, 0);
//
//        dialog.getDialogPane().setContent(gridPane);
//
//        // Request focus on the username field by default.
//        // Platform.runLater(() -> from.requestFocus());
//
//        // Convert the result to a username-password-pair when the login button is clicked.
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == loginButtonType) {
//                return new Pair<>(from.getText(), to.getText());
//            }
//            return null;
//        });
//
//        Optional<Pair<String, String>> result = dialog.showAndWait();
//
//        result.ifPresent(pair -> System.out.println("From=" + pair.getKey() + ", To=" + pair.getValue()));
//    }

}
