package application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainController {
    @FXML
    private Label chatNameLabel;
    @FXML
    private Label clientNameLabel;
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private TextArea notifTextArea;
    
    private ChatService chatService; 
    
    @FXML
    private void handleCancelButton(ActionEvent event) {
    	messageField.clear();
    }
    public TextArea getNotifTextArea() {
        return notifTextArea;
    }

    public void setNotifTextArea(TextArea notifTextArea) {
        this.notifTextArea = notifTextArea;
    }
    public void setChatName(String chatName) {
        chatNameLabel.setText(chatName);
    }
    public void setClientName(String clientName) {
        clientNameLabel.setText(clientName);
    }
        
    public TextField getMessageField() {
        return messageField;
    }
    
    public TextArea getChatArea() {
        return chatArea;
    }


    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    public void initialize() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            dateLabel.setText(currentDate.format(dateFormatter));
            timeLabel.setText(currentTime.format(timeFormatter));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}