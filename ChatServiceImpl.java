package application;
import java.rmi.RemoteException;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {
    private List<String> messages;
    private String Notification;


    protected ChatServiceImpl() throws RemoteException {
        super();
        messages = new ArrayList<>();

    }
    
    @Override
    public synchronized void sendNotification(String message) throws RemoteException {
    	Notification= message;
    }

    @Override
    public synchronized void sendMessage(String message, String sender) throws RemoteException {
        messages.add(sender + ": " + message);
        System.out.println(sender + " sent: " + message);
    }

    @Override
    public synchronized List<String> receiveNewMessages(int lastReceivedIndex) throws RemoteException {
        List<String> newMessages = new ArrayList<>();
        for (int i = lastReceivedIndex + 1; i < messages.size(); i++) {
            newMessages.add(messages.get(i));
        }
        return newMessages;
    }
    
    @Override
    public synchronized String receiveNotif() throws RemoteException {
        return Notification;
    }
   
    @Override
    public synchronized void joinChat(String userName) throws RemoteException {
        sendNotification(userName + " has joined the chat.");
    }

    @Override
    public synchronized void disconnect(String userName) throws RemoteException {
        sendNotification(userName + " has disconnected from the chat.");
    }

    
}
