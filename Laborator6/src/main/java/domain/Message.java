package domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime data;
    private Message reply;

    public Message(User from,List<User> to,String message, LocalDateTime data,Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply = reply;
    }

    public User getFrom() {
        return from;
    }
    public void setFrom(User from) {
        this.from = from;
    }
    public List<User> getTo() {
        return to;
    }
    public void setTo(List<User> to) {
        this.to = to;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }


    public Message getReply() {
        return reply;
    }
}
