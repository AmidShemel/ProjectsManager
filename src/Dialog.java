import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

public class Dialog {



    //Діалогове вікно, інформаційне
    public static void infoBox(String infoMessage, String titleBar, String headerMessage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }
    public static void infoBox(String infoMessage, String titleBar) {
        infoBox(infoMessage, titleBar, null);
    }

    //Діалогове вікно підтвердження
    public static boolean confirmBox(String infoMessage, String titleBar, String headerMessage){

        Alert response = new Alert(AlertType.CONFIRMATION);
        response.setTitle(titleBar);
        response.setHeaderText(headerMessage);
        response.setContentText(infoMessage);


        ButtonType yes = new ButtonType("Так");
        ButtonType no = new ButtonType("Ні");

        response.getButtonTypes().setAll(yes, no);

        Optional<ButtonType> result = response.showAndWait();


        if (result.get() == yes) return true;

        return false;
    }
    public static boolean confirmBox(String infoMessage, String titleBar){
        return confirmBox(infoMessage, titleBar, null);
    }

    //Діалогове вікно з двома полями вводу
    public static void twoInputs(){
        // Create the custom dialog.
        javafx.scene.control.Dialog<Pair<String, String>> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("TestName");
        dialog.setHeaderText("header is here");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));

        TextField from = new TextField();
        from.setPromptText("From");
        TextField to = new TextField();
        to.setPromptText("To");

        gridPane.add(from, 0, 0);
        //gridPane.add(new Label("To:"), 1, 0);
        gridPane.add(to, 2, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        // Platform.runLater(() -> from.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(from.getText(), to.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> System.out.println("From=" + pair.getKey() + ", To=" + pair.getValue()));
    }

    //Діалогове вікно з великим полем вводу
    public static void largeInput(){
        // Create the custom dialog.
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(null);

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 5, 1, 5));

        TextArea inText = new TextArea();
        inText.setWrapText(true);
        inText.setPromptText("Код ліцензування");
        gridPane.add(inText, 0, 0);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the TextArea field by default.
        //Platform.runLater(() -> inText.requestFocus());

        // Get the result to a String when the ok button is clicked.
        dialog.setResultConverter(ok -> {
            if (ok == ButtonType.OK) {
                return inText.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        //result.ifPresent(code -> System.out.println("Result: " + code));
        result.ifPresent(code -> SaveData.licLimit = code);
    }
}