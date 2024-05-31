package annaKnysh.clientside.model;

import annaKnysh.clientside.controller.IClientListener;
import annaKnysh.clientside.xml.UserConnectionInfo;
import annaKnysh.clientside.xml.XMLUtility;
import annaKnysh.clientside.xml.auth.AuthenticationQuery;
import annaKnysh.clientside.xml.message.Message;
import jakarta.xml.bind.JAXBException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class Client extends WebSocketClient implements IClient {
    private static IClient instance;
    private final Set<IClientListener> listeners = new HashSet<>();

    //region Singleton
    // Метод для отримання інстансу клієнта
    public static IClient getInstance(String url) throws URISyntaxException {
        if (instance == null) {
            instance = new Client(url);
        }
        return instance;
    }
    //endregion

    //region Constructor
    // Приватний конструктор
    private Client(String url) throws URISyntaxException {
        super(new URI(url));
    }
    //endregion

    //region Connection Management
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to server on port: " + getURI().getPort());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from the server: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error occurred: " + ex.getMessage());
    }
    //endregion

    //region Listener Management
    @Override
    public void addListener(IClientListener listener) {
        listeners.add(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    private void notifyListeners(String message) {
        listeners.forEach(listener -> listener.onMessage(message));
    }

    @Override
    public Set<IClientListener> getListeners() {
        return listeners;
    }
    //endregion

    //region Message Handling
    @Override
    public void onMessage(String message) {
        notifyListeners(message);
    }

    @Override
    public void sendMessage(String from, String recipient, String content, int chatId) {
        try {
            Message msg = new Message(from, recipient, content, chatId);
            String xmlMessage = XMLUtility.toXML(msg);
            send(xmlMessage);
        } catch (JAXBException e) {
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }
    //endregion

    //region Authentication
    @Override
    public void sendAuthRequest(String username) {
        try {
            AuthenticationQuery authenticationQuery = new AuthenticationQuery(username);
            String xmlMessage = XMLUtility.toXML(authenticationQuery);
            send(xmlMessage);
            System.out.println(xmlMessage);
        } catch (JAXBException e) {
            System.err.println("Error serializing auth request: " + e.getMessage());
        }
    }
    //endregion

    //region User Info
    @Override
    public void sendAddressInfo(String username) {
        UserConnectionInfo info = new UserConnectionInfo(username, getURI().getPort());
        try {
            String xmlInfo = XMLUtility.toXML(info);
            send(xmlInfo);
        } catch (JAXBException e) {
            System.err.println("Error serializing connection info: " + e.getMessage());
        }
    }
    //endregion
}
