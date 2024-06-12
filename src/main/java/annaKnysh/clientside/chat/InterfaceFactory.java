package annaKnysh.clientside.chat;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import annaKnysh.clientside.view.ClientApp;
import annaKnysh.clientside.xml.message.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class InterfaceFactory {
    //region DateLabel
    public static HBox createDateLabel(LocalDate date) {
        String formattedDate = formatDate(date);
        Label dateLabel = new Label(formattedDate);
        dateLabel.setAlignment(Pos.CENTER);
        dateLabel.getStyleClass().add("messageTime");  // Updated to match the new CSS class for date labels
        HBox dateBox = new HBox();
        dateBox.setAlignment(Pos.CENTER);
        dateBox.getChildren().add(dateLabel);
        return dateBox;
    }
    //endregion
    //region Message Input Field
    public static TextArea createMessageField() {
        TextArea messageField = new TextArea();
        messageField.setWrapText(true);
        messageField.getStyleClass().add("messageField");
        return messageField;
    }
    //endregion
    //region Message
    public static VBox createMessageBox(Message message) {
        LocalDateTime timestamp = message.getTimestamp();
        VBox messageBox = new VBox();
        Label senderLabel = new Label();
        senderLabel.getStyleClass().add("userName");  // Updated to match the new CSS class for username labels

        Text messageText = new Text(message.getContent());
        messageText.setWrappingWidth(300);
        messageText.getStyleClass().add("textBodyMessage");  // Updated to match the new CSS class for message text

        TextFlow messageFlow = new TextFlow(messageText);
        messageFlow.setMaxWidth(300);
        messageFlow.getStyleClass().add("message-text-flow");  // Updated to match the new CSS class for message text flow

        Label timeLabel = new Label(timestamp.format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.getStyleClass().add("messageTime");  // Updated to match the new CSS class for time labels

        HBox messageContainer = new HBox();
        messageContainer.setMaxWidth(300);

        StackPane textContainer = new StackPane(messageFlow);
        textContainer.setMaxWidth(300);
        ImageView imageView = new ImageView();
        // Load image from file
        Image avatar = new Image(Objects.requireNonNull(InterfaceFactory.class.getResource("/annaKnysh/clientside/img/user_circle.png")).toExternalForm());
        imageView.setImage(avatar);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        if (!message.getFrom().equals(ClientApp.getUsername())) {
            senderLabel.setText(message.getFrom());
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            textContainer.getStyleClass().add("bodyMessageLeft");  // Updated to match the new CSS class for left-aligned messages
            messageContainer.getChildren().addAll(imageView,textContainer);
        } else {
            senderLabel.setText("You");
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            textContainer.getStyleClass().add("bodyMessageRight");  // Updated to match the new CSS class for right-aligned messages
            messageContainer.getChildren().addAll(textContainer, imageView);
        }
        messageContainer.setStyle("-fx-spacing: 10");
        messageBox.getChildren().addAll(senderLabel, messageContainer, timeLabel);
        return messageBox;
    }

    private static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault());
        return date.format(formatter);
    }
    //endregion
}
