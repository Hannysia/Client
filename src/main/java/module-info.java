module annaKnysh.clientside {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.java_websocket;
    requires jakarta.xml.bind;
    requires javafx.graphics;
    requires javafx.base;

    opens annaKnysh.clientside to javafx.fxml;
    opens annaKnysh.clientside.xml.auth to javafx.fxml,jakarta.xml.bind;
    opens annaKnysh.clientside.xml.chat to javafx.fxml,jakarta.xml.bind;
    opens annaKnysh.clientside.xml.message to javafx.fxml,jakarta.xml.bind;
    opens annaKnysh.clientside.xml.auxiliary to javafx.fxml,jakarta.xml.bind;
    opens annaKnysh.clientside.xml to javafx.fxml,jakarta.xml.bind;
    opens annaKnysh.clientside.controller to javafx.fxml;
    opens annaKnysh.clientside.chat to javafx.fxml;
    opens annaKnysh.clientside.view to javafx.fxml;
    exports annaKnysh.clientside.xml;
    exports annaKnysh.clientside.xml.chat;
    exports annaKnysh.clientside.xml.auth;
    exports annaKnysh.clientside.xml.auxiliary;
    exports annaKnysh.clientside.xml.message;
    exports annaKnysh.clientside.view;
    exports annaKnysh.clientside.controller;
    exports annaKnysh.clientside.model;
    exports annaKnysh.clientside.chat;

}