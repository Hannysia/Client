package annaKnysh.clientside.controller;

import annaKnysh.clientside.view.ClientApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class LoginController {
    @FXML
    private TextField serverIPField;
    @FXML
    private TextField usernameField;

    public void handleLoginButton() {
        String serverIp = serverIPField.getText().trim();
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Validation Error", "Login fields must be filled.");
        }
        try {
            ClientApp.initializeClient(serverIp, username);
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Server port must be a number.");
        } catch (Exception e) {
            showAlert("Connection Error", "Failed to connect to the server. Please check your IP and port.");
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
