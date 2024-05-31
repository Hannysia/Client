package annaKnysh.clientside.model;

import annaKnysh.clientside.controller.IClientListener;

import java.util.Set;

@SuppressWarnings({"unused", "RedundantThrows"})
public interface IClient {
    void connect() throws InterruptedException;
    void addListener(IClientListener observer);
    void clearListeners();
    void sendMessage(String from, String recipient, String content, int chatId);
    void sendAuthRequest(String username);
    void sendAddressInfo(String username);
    Set<IClientListener> getListeners();
    boolean connectBlocking() throws InterruptedException;
    boolean isOpen();
    void close();
    void send(String requestXml);
}

