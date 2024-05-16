package application;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class NamePasswordDialog extends Dialog<Pair<String, String>> {
    private TextField nameField;
    private PasswordField passwordField;

    public NamePasswordDialog() {
        setTitle("Register");
        setHeaderText(null);

        nameField = new TextField();
        nameField.setPromptText("Name");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        GridPane grid = new GridPane();
        grid.add(nameField, 0, 0);
        grid.add(passwordField, 0, 2);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(nameField.getText(), passwordField.getText());
            }
            return null;
        });
    }
}

