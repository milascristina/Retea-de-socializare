package domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;


public class Friendship extends Entity<Long>{
    LocalDateTime friendsfrom;

    Long idUser1;
    Long idUser2;
    private FriendshipStatus status;


    public Friendship(Long User1, Long User2,LocalDateTime friendsfrom,FriendshipStatus status) {
        this.idUser1 = User1;
        this.idUser2=User2;
        this.friendsfrom = friendsfrom;
        this.status=status;
    }
    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
    public Long getIdUser1() {
        return idUser1;
    }
    public Long getIdUser2() {
        return idUser2;
    }
    public LocalDateTime getFriendsFrom() {
        return friendsfrom;
    }
    public LocalDateTime getDate() {
        return friendsfrom;
    }
}
