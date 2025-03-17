package repository;

import domain.Friend;
import domain.Friendship;

import java.util.List;

public class FriendRepository {
    private List<Friend> friends;

    public Friend findOne(Long id) {
        for (Friend friend : friends) {
            if (friend.getId().equals(id)) {
                return friend;
            }
        }
        return null;
    }
    public List<Friend> findAll() {
        return friends;
    }

    public Friend save(Friend friend) {
        friends.add(friend);
        return friend;
    }

    public Friend update(Friend friend) {
        return null;
    }

    public Friend delete(Long id) {
        Friend friend=friends.remove(friends.indexOf(findOne(id)));
        return friend;
    }
}
