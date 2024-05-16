package application;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;


public class ChatServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            ChatService chatService = new ChatServiceImpl();
            Naming.rebind("chat", chatService);
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

