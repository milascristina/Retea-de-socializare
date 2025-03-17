package com.example.laborator6;

import domain.Message;
import domain.User;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import service.MessageService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Observer;
import java.util.Observable;

public class ChatController implements Observer {
    private User currentUser;
    private User friendUser;
    private MessageService messageService;
    private Message replyMessage;

    @FXML
    private ListView<String> messagesListView;
    @FXML
    private TextField messageTextField;
    @FXML
    private Button sendButton;
    public void setService3(MessageService messageService) {
        this.messageService = messageService;
    }
    public void initData(User currentUser, User friendUser) {
        this.currentUser = currentUser;
        this.friendUser = friendUser;

        this.messageService.loadMessages(currentUser.getId(), friendUser.getId());

        this.messageService.getObservableMessages().addListener((ListChangeListener<Message>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Message newMessage : change.getAddedSubList()) {
                        this.messagesListView.getItems().add(formatMessage(newMessage));
                    }
                }
            }
        });

        this.messageService.addObserver(this);

        for (Message message : this.messageService.getObservableMessages()) {
            messagesListView.getItems().add(formatMessage(message));
        }
    }

    private String formatMessage(Message message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String timestamp = message.getData().format(formatter);
        if (message.getReply() != null) {
            return String.format(
                    "[%s] %s: %s\n   ↳ Reply to: %s",
                    timestamp,
                    message.getFrom().getLastName(),
                    message.getMessage(),
                    message.getReply().getMessage()
            );
        }

        return String.format("[%s] %s: %s", timestamp, message.getFrom().getLastName(), message.getMessage());
    }

    @FXML
    private void onSendButtonAction() {
        String text = messageTextField.getText().trim();
        if (!text.isEmpty()) {
            this.messageService.sendMessage(currentUser, List.of(friendUser), text, java.time.LocalDateTime.now(), replyMessage);

            messageTextField.clear();
            replyMessage = null;
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MessageService) {
            Message newMessage = (Message) arg;
            if (!messagesListView.getItems().contains(formatMessage(newMessage))) {
                messagesListView.getItems().add(formatMessage(newMessage));
            }
        }
    }


}
