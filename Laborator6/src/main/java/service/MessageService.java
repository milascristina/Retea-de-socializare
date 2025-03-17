package service;

import domain.Message;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import repository.DataBase.MessageDBRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Observable;


public class MessageService extends Observable {
    private MessageDBRepository messageDBRepository;
    private ObservableList<Message> messageList = FXCollections.observableArrayList();

    public MessageService(MessageDBRepository repo) {
        this.messageDBRepository = repo;
    }

    public void loadMessages(Long user1Id, Long user2Id) {
        List<Message> messages = messageDBRepository.findMessagesBetweenUsers(user1Id, user2Id);
        messageList.setAll(messages);
    }

    public void addMessage(Message message) {
        messageList.add(message);
        setChanged();
        notifyObservers(message);
    }


    public ObservableList<Message> getObservableMessages() {
        return messageList;
    }

    public void sendMessage(User from, List<User> to, String messageText, LocalDateTime dateTime, Message reply) {
        Message newMessage = new Message(from, to, messageText, dateTime, reply);
        messageDBRepository.save(newMessage);

        addMessage(newMessage);
        setChanged();
        notifyObservers(newMessage);
    }


}
