package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
    private ChatService chatService;
    private String clientName;
    private MainController controller;
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Connexion au service de chat distant via RMI
        chatService = (ChatService) Naming.lookup("rmi://localhost/chat");
        
        // Chargement de l'interface utilisateur depuis le fichier FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        
        // Authentification du client
        clientName = getClientNameAndPassword(primaryStage);
        controller.setClientName(clientName);
        chatService.joinChat(clientName);
        
        // Affichage d'un message de bienvenue dans la zone de chat
        controller.getChatArea().appendText("Welcome " + clientName + " ! You can find here your received messages..." + "\n");
        
        // Configuration de l'action à effectuer lorsque l'utilisateur envoie un message
        controller.getMessageField().setOnAction(event -> sendMessage());
        Button sendButton = (Button) loader.getNamespace().get("sendButton");
        sendButton.setOnAction(event -> sendMessage());  
        
        // Configuration de l'action à effectuer lorsque l'utilisateur se déconnecte
        Button disconnectButton = (Button) loader.getNamespace().get("disconnectButton");
        disconnectButton.setOnAction(event -> disconnect(primaryStage));
        
        // Configuration de la fenêtre principale
        primaryStage.setTitle("Chat Interface");
        primaryStage.setScene(new Scene(root, 565, 383));
        primaryStage.show();
        
        // Configuration de l'action à effectuer lorsque l'utilisateur ferme la fenêtre
        primaryStage.setOnCloseRequest(event -> handleCloseRequest());
        
        // Démarrage d'un thread pour recevoir les messages du serveur
        Thread receiverThread = new Thread(this::receiveMessages);
        receiverThread.setDaemon(true); 
        receiverThread.start();
        
        // Démarrage d'un thread pour recevoir les notifications du serveur
        Thread receiverNThread = new Thread(this::receiveNotif);
        receiverNThread.setDaemon(true); 
        receiverNThread.start();
    }

    private void disconnect(Stage primaryStage) {
    	handleCloseRequest();
        primaryStage.close();
        Platform.exit();
    }
    private String getClientNameAndPassword(Stage primaryStage) throws Exception {
        while (true) {
            NamePasswordDialog dialog = new NamePasswordDialog();
            dialog.setTitle("Register");
            dialog.setHeaderText("Enter your name and password:");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Optional<Pair<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                Pair<String, String> pair = result.get();
                String name = pair.getKey();
                String password = pair.getValue();

                if ((name + "RMI").equals(password)) {
                    return name;
                } else {
                	Alert alert = new Alert(Alert.AlertType.ERROR);
                	alert.setTitle("Error");
                    alert.setHeaderText(null);
                	if(password.isEmpty()) {
                		alert.setContentText("You should provide your password. Please try again.");
                        alert.showAndWait();
                	}else {
                		alert.setContentText("Incorrect password. Please try again.");
                        alert.showAndWait();
                	}                      
                }
            } else {
                primaryStage.close();
                Platform.exit();
                return null;
            }
        }
    }
    private void sendMessage() {
        String message = controller.getMessageField().getText().trim();
        if (!message.isEmpty()) {
            try {
                chatService.sendMessage(message, clientName);
                controller.getMessageField().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void receiveMessages() {
        try {
            // Initialise l'index du dernier message reçu à -1
            int lastReceivedIndex = -1;            
            // Boucle infinie pour continuellement recevoir des messages
            while (true) {
                // Récupère les nouveaux messages depuis le service de chat
                List<String> newMessages = chatService.receiveNewMessages(lastReceivedIndex);                
                // Vérifie s'il y a des nouveaux messages
                if (!newMessages.isEmpty()) {
                    // Met à jour l'index du dernier message reçu
                    lastReceivedIndex += newMessages.size();                   
                    // Itère sur les nouveaux messages et les ajoute à la zone de chat
                    for (String message : newMessages) {
                        controller.getChatArea().appendText(message + "\n");
                    }
                }       
                // Attend une seconde avant de vérifier à nouveau s'il y a de nouveaux messages
                Thread.sleep(1000); 
            }
        } catch (Exception e) {
            // Capture et affiche les exceptions qui peuvent survenir
            e.printStackTrace();
        }
    }

   
    private String lastReceivedNotification = ""; 

    private void receiveNotif() {
        try {
            // Boucle infinie pour continuellement recevoir des notifications
            while (true) {
                // Récupère la notification depuis le service de chat
                String notification = chatService.receiveNotif();
                
                // Vérifie si une notification a été reçue et si elle est différente de la dernière notification reçue
                if (notification != null && !notification.equals(lastReceivedNotification)) {
                    // Exécute les mises à jour de l'interface utilisateur sur le thread de l'interface graphique JavaFX
                    Platform.runLater(() -> {
                        // Ajoute la notification à la zone de texte des notifications
                        controller.getNotifTextArea().appendText(notification + "\n");
                        // Met à jour la dernière notification reçue
                        lastReceivedNotification = notification;
                    });
                }
                // Attend une seconde avant de vérifier à nouveau s'il y a de nouvelles notifications
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            // Capture et affiche les exceptions qui peuvent survenir
            e.printStackTrace();
        }
    }
    
    private void handleCloseRequest() {
        try {
            chatService.disconnect(clientName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}