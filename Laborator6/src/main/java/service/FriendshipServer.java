package service;

import domain.Friend;
import domain.Friendship;
import domain.FriendshipStatus;
import domain.User;
import domain.validators.ValidatorException;
import repository.DataBase.FriendshipDBRepository;
import repository.DataBase.UserDBRepository;
import repository.FriendRepository;
import util.Page;
import util.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipServer {
    private FriendshipDBRepository friendshipDBRepository;
    private UserDBRepository userDBRepository;
    private FriendRepository friendRepository;
    public FriendshipServer(FriendshipDBRepository friendshipDBRepository, UserDBRepository userDBRepository, FriendRepository friendRepository) {
        this.friendRepository=friendRepository;
        this.friendshipDBRepository = friendshipDBRepository;
        this.userDBRepository = userDBRepository;
    }
    public User findUser(Long id){
        return this.userDBRepository.findOne(id);
    }
    public List<Friend> findAllFriendships(Long id) {

        List<Friend> allfriendships = new ArrayList<>();
        for (Friendship friendship : this.friendshipDBRepository.findAll()) {
            if (friendship.getIdUser2().equals(id)
                    && friendship.getStatus() == FriendshipStatus.ACCEPTED) {
                User u=this.userDBRepository.findOne(friendship.getIdUser1());
                Friend f=new Friend(friendship.getIdUser1(),u.getFirstName(), u.getLastName(), friendship.getFriendsFrom(),friendship.getStatus());
                allfriendships.add(f);
            }
            if (friendship.getIdUser1().equals(id)
                    && friendship.getStatus() == FriendshipStatus.ACCEPTED) {
                User u=this.userDBRepository.findOne(friendship.getIdUser2());
                Friend f=new Friend(friendship.getIdUser2(),u.getFirstName(), u.getLastName(), friendship.getFriendsFrom(),friendship.getStatus());
                allfriendships.add(f);
            }
        }
        return allfriendships;
    }
    public void sendRequest(Long id1,Long id2){
        Friendship f1=new Friendship(id1,id2, LocalDateTime.now(),FriendshipStatus.PENDING);
        f1.setId(getNewFriendshipId());
        this.friendshipDBRepository.save(f1);

    }
    public Iterable<Friendship> getFriendships() {
        return friendshipDBRepository.findAll();
    }
    public Long getNewFriendshipId() {
        Long newFriendshipId = 0L;
        for(Friendship f: getFriendships()) {
            newFriendshipId=f.getId();
        }
        newFriendshipId++;
        return newFriendshipId;
    }

    public void addFriendship(Long id1, Long id2) {
        Friendship friendship = null;
        for (Friendship f : friendshipDBRepository.findAll()) {
            if ((f.getIdUser1().equals(id1) && f.getIdUser2().equals(id2)) ||
                    (f.getIdUser1().equals(id2) && f.getIdUser2().equals(id1))) {
                friendship = f;
                break;
            }
        }
        if (friendship != null) {
            if (!friendship.getStatus().equals(FriendshipStatus.ACCEPTED)) {
                friendship.setStatus(FriendshipStatus.ACCEPTED);
                friendshipDBRepository.delete(friendship.getId());
                friendshipDBRepository.save(friendship);
            }
        } else {
            throw new ValidatorException("Friendship does not exist!");
        }
    }


    public void removeFriendship(Long id1, Long id2) {
        User user1=userDBRepository.findOne(id1);
        User user2=userDBRepository.findOne(id2);
        Long id=0L;
        for(Friendship f: friendshipDBRepository.findAll()) {
            if(f.getIdUser1().equals(id1)&&f.getIdUser2().equals(id2)|| f.getIdUser1().equals(id2)&&f.getIdUser2().equals(id1)) {
                id=f.getId();
            }
        }
        if(id==0L){
            throw new ValidatorException("The friendship doesn't exist!");
        }
        friendshipDBRepository.delete(id);
        user1.removeFriend(user2);
        user2.removeFriend(user1);
    }

    public List<Friend> getRequest(User user) {
        List<Friend> requests = new ArrayList<>();
        for(Friendship f: friendshipDBRepository.findAll()) {
            if(f.getIdUser2().equals(user.getId()) && f.getStatus() == FriendshipStatus.PENDING) {
               User user2=userDBRepository.findOne(f.getIdUser1());
                Friend friend=new Friend(f.getIdUser1(),user2.getFirstName(),user2.getLastName(),f.getFriendsFrom(),f.getStatus());
                if(friend!=null)
                    requests.add(friend);
            }
        }
        return requests;
    }

    public Page<Friend> findAllOnPage(Pageable pageable,User user) {
        List<Friend> allFriends = findAllFriendships(user.getId());
        int totalElements = allFriends.size();
        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), totalElements);
        List<Friend> friendsOnPage = new ArrayList<>();
        if (startIndex < totalElements) {
            friendsOnPage = allFriends.subList(startIndex, endIndex);
        }
        return new Page<>(friendsOnPage, totalElements);
    }

}
