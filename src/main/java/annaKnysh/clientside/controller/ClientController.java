package annaKnysh.clientside.controller;

import annaKnysh.clientside.chat.ChatDisplayData;
import annaKnysh.clientside.model.IClient;
import annaKnysh.clientside.chat.InterfaceFactory;
import annaKnysh.clientside.view.ClientApp;
import annaKnysh.clientside.xml.XMLUtility;
import annaKnysh.clientside.xml.auth.AuthenticationAnswer;
import annaKnysh.clientside.xml.chat.Chat;
import annaKnysh.clientside.xml.chat.ChatListResponse;
import annaKnysh.clientside.xml.chat.ChatRequest;
import annaKnysh.clientside.xml.message.Message;
import annaKnysh.clientside.xml.message.MessagesResponse;
import jakarta.xml.bind.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.*;
import java.io.*;
import java.time.*;
import java.util.*;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class ClientController implements IClientListener, IClientController {
    //region Variables
    private static ClientController instance;

    @FXML
    private Label usernameLabel;

    @FXML
    private ListView<ChatDisplayData> chatListView;

    @FXML
    private VBox chatBox;

    @FXML
    private TextArea messageField;

    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private Button sendButton;

    private IClient client; // Використання інтерфейсу IClient
    public TextField newChatUsername;
    private LocalDate currentDisplayedDate = null;
    private int currentChatId = -1;
    //endregion

    //region Constructor
    // Конструктор класу
    public ClientController() {}
    //endregion

    //region Initialization
    @FXML
    public void initialize() {
        // Встановлення імені користувача
        usernameLabel.setText(ClientApp.getUsername());

        // Налаштування cell factory для списку чатів
        chatListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ChatDisplayData> call(ListView<ChatDisplayData> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ChatDisplayData item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            HBox hbox = new HBox(10);
                            ImageView avatar = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/annaKnysh/clientside/img/user.png"))));
                            avatar.setFitHeight(30);
                            avatar.setFitWidth(30);
                            Label chatName = new Label(item.toString());
                            hbox.getChildren().addAll(avatar, chatName);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        // Підключення стилів
        String stylesheet = Objects.requireNonNull(getClass().getResource("/annaKnysh/clientside/css/chat.css")).toExternalForm();
        chatScrollPane.getStylesheets().add(stylesheet);
        chatListView.getStylesheets().add(stylesheet);

        // Додавання listener для вибору чату
        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                currentChatId = newSelection.chatId();
                getChatMessages(newSelection.chatId());
                addMessageField();
            }
        });

        // Додавання обробника подій прокрутки
        chatScrollPane.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            double width = chatScrollPane.getContent().getBoundsInLocal().getWidth();
            double vvalue = chatScrollPane.getVvalue();
            chatScrollPane.setVvalue(vvalue - deltaY / width);
        });

        // Додавання фільтра подій прокрутки для плавної прокрутки
        chatScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaX() != 0) {
                event.consume();
            }
        });
    }
    //endregion

    //region Chat Messages
    // Метод для отримання повідомлень чату
    private void getChatMessages(int chatId) {
        chatBox.getChildren().clear();
        try {
            ChatRequest request = new ChatRequest("getMessages", chatId, ClientApp.getUsername());
            String requestXml = XMLUtility.toXML(request);
            client.send(requestXml);
        } catch (JAXBException e) {
            System.out.println("Error requesting chat messages: " + e.getMessage());
        }
    }


    private void addMessageField() {
        // Find the HBox container for the message field and send button
        HBox messageFieldContainer = (HBox) chatScrollPane.getParent().lookup(".messageFieldCont");

        // Clear existing children to avoid duplicates
        messageFieldContainer.getChildren().clear();

        // Create new message field
        messageField = InterfaceFactory.createMessageField();

        // Create new send button
        sendButton = new Button("Send");
        sendButton.setMaxWidth(50);
        sendButton.setMinWidth(50);
        sendButton.setOnAction(event -> onSend());

        // Add message field and send button to the container
        messageFieldContainer.getChildren().addAll(messageField, sendButton);
    }


    // Відображення повідомлення
    public void displayMessage(Message message) {
        LocalDateTime timestamp = message.getTimestamp();
        LocalDate messageDate = timestamp.toLocalDate();

        if (currentDisplayedDate == null || !currentDisplayedDate.equals(messageDate)) {
            currentDisplayedDate = messageDate;
            chatBox.getChildren().add(InterfaceFactory.createDateLabel(messageDate));
        }

        chatBox.getChildren().add(InterfaceFactory.createMessageBox(message));
    }

    // Оновлення вікна чату
    private void updateChatBox(List<Message> messages) {
        chatBox.getChildren().clear();
        currentDisplayedDate = null;
        for (Message message : messages) {
            if (currentChatId == message.getChatId()) {
                displayMessage(message);
            }
        }
        addMessageField();
    }
    @Override
    public void onMessage(String xmlMessage) {
        Platform.runLater(() -> {
            if (xmlMessage.contains("<chatListResponse>")) {
                handleChatList(xmlMessage);
            } else if (xmlMessage.contains("<message>")) {
                handleInputMessage(xmlMessage);
            } else if (xmlMessage.contains("<authenticationAnswer")) {
                handleAuthResponse(xmlMessage);
            } else if (xmlMessage.contains("<messagesResponse>")) {
                handleMessagesResponse(xmlMessage);
            } else {
                if (!xmlMessage.contains("<messagesResponse/>") && !xmlMessage.contains("<chatListResponse/>")) {
                    System.out.println(xmlMessage);
                }
            }
        });
    }
    // Обробка вхідного повідомлення
    private void handleInputMessage(String xmlMessage) {
        try {
            Message message = XMLUtility.fromXML(xmlMessage, Message.class);
            ChatDisplayData selectedChat = chatListView.getSelectionModel().getSelectedItem();
            if (selectedChat != null && message.getChatId() == selectedChat.chatId()) {
                displayMessage(message);
            }
        } catch (Exception e) {
            System.out.println("Error parsing XML: " + e.getMessage());
        }
    }

    // Обробка відповіді з повідомленнями
    private void handleMessagesResponse(String xmlMessage) {
        try {
            JAXBContext context = JAXBContext.newInstance(MessagesResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xmlMessage);
            MessagesResponse response = (MessagesResponse) unmarshaller.unmarshal(reader);
            updateChatBox(response.getMessages());
        } catch (Exception e) {
            System.out.println("Error parsing messages: " + e.getMessage());
        }
    }
    //endregion

    //region Chat List
    // Обробка списку чатів
    private void handleChatList(String xmlMessage) {
        try {
            ChatListResponse response = XMLUtility.fromXML(xmlMessage, ChatListResponse.class);
            updateChatList(response.getChats());
        } catch (Exception e) {
            System.out.println("Error parsing chat list: " + e.getMessage());
        }
    }

    // Оновлення списку чатів
    public void updateChatList(List<Chat> chats) {
        Platform.runLater(() -> {
            chatListView.getItems().clear();
            for (Chat chat : chats) {
                chatListView.getItems().add(new ChatDisplayData(chat.getChat_id(), chat.getChatDisplayName(ClientApp.getUsername()), chat.getUsernameFirst(), chat.getUsernameSecond()));
            }
        });
    }
    //endregion

    //region Authentication
    // Обробка відповіді аутентифікації
    private void handleAuthResponse(String xmlMessage) {
        try {
            AuthenticationAnswer response = XMLUtility.fromXML(xmlMessage, AuthenticationAnswer.class);
            if (response.isAuthenticated()) {
                Platform.runLater(() -> {
                    try {
                        ClientApp.setUsername(response.getUsername());
                        ClientApp.showClientScreen();
                        client.sendAddressInfo(ClientApp.getUsername());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                showAlert("Authentication Failed", "Please check your username and password.");
            }
        } catch (JAXBException e) {
            showAlert("Error", "Error parsing authentication response: " + e.getMessage());
        }
    }
    //endregion

    //region Client Management
    // Встановлення клієнта
    public void setClient(IClient client) {
        this.client = client;
        if (client != null) {
            client.addListener(this);
        }
    }

    // Очищення спостерігачів
    public void clearListeners() {
        client.clearListeners();
    }

    // Надсилання аутентифікаційної інформації
    public void sendAuthInfo(String username) {
        client.sendAuthRequest(username);
    }

    // Отримання клієнта
    public IClient getClient() {
        return client;
    }
    //endregion

    //region User Chats
    // Запит на отримання чатів користувача
    @FXML
    public void getUserChats() {
        if (client != null && client.isOpen()) {
            try {
                ChatRequest request = new ChatRequest("getChats", ClientApp.getUsername(), null);
                String requestXml = XMLUtility.toXML(request);
                client.send(requestXml);
            } catch (JAXBException e) {
                System.out.println("Error creating chat list request: " + e.getMessage());
            }
        } else {
            System.out.println("Connection is not established. Please connect to the server first.");
        }
    }

    // Створення нового чату
    @FXML
    public void createNewChat() {
        System.out.println("Creating new chat");
        String username2 = newChatUsername.getText().trim();
        if (!username2.isEmpty()) {
            try {
                ChatRequest chatRequest = new ChatRequest("createChat", ClientApp.getUsername(), username2);
                String chatRequestXml = XMLUtility.toXML(chatRequest);
                if (chatRequestXml != null) {
                    client.send(chatRequestXml);
                    getUserChats();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            newChatUsername.clear();
        }
    }

    // Ініціалізація нового чату
    private void initiateNewChat(int chatId, String username) {
        ChatDisplayData newChat = new ChatDisplayData(chatId, username, null, null);
        Platform.runLater(() -> chatListView.getItems().add(newChat));
    }

    // Видалення чату
    @FXML
    public void deleteChat() {
        final int selectedIdx = chatListView.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            ChatDisplayData selectedChat = chatListView.getItems().get(selectedIdx);
            try {
                ChatRequest deleteRequest = new ChatRequest("deleteChat", selectedChat.chatId());
                String requestXml = XMLUtility.toXML(deleteRequest);
                client.send(requestXml);
                chatListView.getItems().remove(selectedIdx);
                chatBox.getChildren().clear();
            } catch (JAXBException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Please select a chat to delete.\n");
        }
    }
    //endregion

    //region Message Sending
    // Обробка надсилання повідомлення
    @FXML
    public void onSend() {
        if (client != null && client.isOpen()) {
            String messageContent = messageField.getText().trim();
            if (!messageContent.isEmpty()) {
                ChatDisplayData selectedChat = chatListView.getSelectionModel().getSelectedItem();
                if (selectedChat != null) {
                    int chatId = selectedChat.chatId();
                    Message message = new Message(ClientApp.getUsername(), selectedChat.displayName(), messageContent, chatId);
                    client.sendMessage(ClientApp.getUsername(), selectedChat.displayName(), messageContent, chatId);
                    messageField.clear();
                    displayMessage(message);
                }
            }
        }
    }
    //endregion

    //region Alerts
    // Показати сповіщення
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    //endregion
}
