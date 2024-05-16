package application;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatService extends Remote {
    void sendMessage(String message, String sender) throws RemoteException;
    List<String> receiveNewMessages(int lastReceivedIndex) throws RemoteException;
    void joinChat(String userName) throws RemoteException;
    void disconnect(String userName) throws RemoteException;
	void sendNotification(String message) throws RemoteException;
	String receiveNotif() throws RemoteException;
}



