package annaKnysh.clientside.chat;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
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

public class InterfaceFactory {
//region DateLabel
    public static HBox createDateLabel(LocalDate date) {
        String formattedDate = formatDate(date);
        Label dateLabel = new Label(formattedDate);
        dateLabel.setAlignment(Pos.CENTER);
        dateLabel.getStyleClass().add("date-label");
        HBox dateBox = new HBox();
        dateBox.setAlignment(Pos.CENTER);
        dateBox.getChildren().add(dateLabel);
        return dateBox;
    }
//endregion

//region Message
    public static VBox createMessageBox(Message message) {
        LocalDateTime timestamp = message.getTimestamp();
        VBox messageBox = new VBox();
        Label senderLabel = new Label();
        senderLabel.getStyleClass().add("usernameLabel");

        Text messageText = new Text(message.getContent());
        messageText.setWrappingWidth(300);
        messageText.getStyleClass().add("message-text");

        TextFlow messageFlow = new TextFlow(messageText);
        messageFlow.setMaxWidth(300);
        messageFlow.getStyleClass().add("message-text-flow");

        Label timeLabel = new Label(timestamp.format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.getStyleClass().add("time-label");

        HBox messageContainer = new HBox();
        messageContainer.setMaxWidth(300);

        StackPane textContainer = new StackPane(messageFlow);
        textContainer.setMaxWidth(300);

        messageContainer.getChildren().add(textContainer);

        if (!message.getFrom().equals(ClientApp.getUsername())) {
            senderLabel.setText(message.getFrom());
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            textContainer.getStyleClass().add("bodyMessageLeft");
        } else {
            senderLabel.setText("You");
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            textContainer.getStyleClass().add("bodyMessageRight");
        }

        messageBox.getChildren().addAll(senderLabel, messageContainer, timeLabel);
        return messageBox;
    }

    private static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault());
        return date.format(formatter);
    }
    //endregion
}
