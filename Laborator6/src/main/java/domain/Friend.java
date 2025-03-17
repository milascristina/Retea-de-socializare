package domain;

import java.time.LocalDateTime;

public class Friend{
    private Long id;
    private String nume;
    private String prenume;
    private LocalDateTime data;
    private FriendshipStatus status;


    public Friend(Long id,String nume, String prenume, LocalDateTime data, FriendshipStatus status) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.data = data;
        this.status = status;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getNume() {
        return nume;
    }
    public void setNume(String nume) {
        this.nume = nume;
    }
    public String getPrenume() {
        return prenume;
    }
    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }
    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }
    public FriendshipStatus getStatus() {
        return status;
    }
    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}
