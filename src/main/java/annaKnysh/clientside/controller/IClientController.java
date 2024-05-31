package annaKnysh.clientside.controller;

import annaKnysh.clientside.model.IClient;
import annaKnysh.clientside.xml.chat.Chat;
import annaKnysh.clientside.xml.message.Message;
import java.util.List;

@SuppressWarnings("unused")
public interface IClientController extends IClientListener {
    void setClient(IClient client);
    void clearListeners();
    void getUserChats();
    void onSend();
    void createNewChat();
    void deleteChat();
    void sendAuthInfo(String username);
    void displayMessage(Message message);
    void updateChatList(List<Chat> chats);
}

