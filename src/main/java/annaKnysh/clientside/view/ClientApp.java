package annaKnysh.clientside.view;

import annaKnysh.clientside.controller.ClientController;
import annaKnysh.clientside.controller.IClientController;
import annaKnysh.clientside.controller.LoginController;
import annaKnysh.clientside.model.Client;
import annaKnysh.clientside.model.IClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.util.Objects;

@SuppressWarnings("CallToPrintStackTrace")
public class ClientApp extends Application {
    private static IClientController clientController;
    private static IClient client;
    private static Stage primaryStage;
    private static String username;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientApp.primaryStage = primaryStage;
        showLoginScreen();
    }

    public static void initializeClient(String serverIp, String username) throws URISyntaxException, InterruptedException {
        String serverUrl = "ws://" + serverIp;
        client = Client.getInstance(serverUrl);
        if (client.connectBlocking()) {
            if (clientController == null) {
                clientController = new ClientController();
            }
            clientController.setClient(client);
            client.addListener(clientController);
            clientController.sendAuthInfo(username);
        } else {
            throw new InterruptedException("Failed to connect to the server.");
        }
    }
    public static void showClientScreen() {
        Platform.runLater(() -> {
            try {
                clientController.clearListeners();
                FXMLLoader loader = new FXMLLoader(ClientApp.class.getResource("/annaKnysh/clientside/Client.fxml"));
                Parent root = loader.load();
                IClientController mainController = loader.getController();
                mainController.setClient(client);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(ClientApp.class.getResource("/annaKnysh/clientside/css/chat.css")).toExternalForm());
                primaryStage.setTitle("Client of " + username);
                primaryStage.setScene(scene);
                primaryStage.show();
                clientController.getUserChats();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void showLoginScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/annaKnysh/clientside/Login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        System.out.println("Login: " + loginController);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setUsername(String user) {
        username = user;
    }

    public static String getUsername() {
        return username;
    }

    @SuppressWarnings("unused")
    public IClient getClient() {
        return client;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Application is stopping.");
        if (client != null && client.isOpen()) {
            client.close();
        }
        super.stop();
    }
}
