package repository.DataBase;

import domain.Friendship;
import domain.FriendshipStatus;
import domain.User;
import domain.validators.FriendshipValidator;
import repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipDBRepository implements Repository<Long, Friendship> {
    FriendshipValidator friendshipValidator;
    Connection connection;

    public FriendshipDBRepository(FriendshipValidator friendshipValidator) {
        this.friendshipValidator = friendshipValidator;
        try{
            this.connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/lab6","postgres","feliciamami");

        }catch (SQLException e){
            e.printStackTrace();
        };
    }

    @Override
    public Friendship findOne(Long id) {
        String query = "SELECT * FROM friendships WHERE \"id\" = ?";
        Friendship friendship = null;
        try(PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Long idUser1 = null;
            Long idUser2 = null;
            LocalDateTime friendsfrom = null;
            FriendshipStatus status = null;
            while (resultSet.next()) {
                idUser1 = resultSet.getLong("iduser1");
                idUser2 = resultSet.getLong("iduser2");
                Timestamp timestamp = resultSet.getTimestamp("friendsFrom");
                friendsfrom = timestamp != null ? timestamp.toLocalDateTime() : null;
                status = FriendshipStatus.valueOf(resultSet.getString("status"));
            }
            friendship = new Friendship(idUser1, idUser2, friendsfrom, status);
            friendship.setId(id);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return friendship;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Map<Long,Friendship> friendships = new HashMap<>();
        try(PreparedStatement preparedStatement=this.connection.prepareStatement("SELECT * FROM friendships");
            ResultSet resultSet=preparedStatement.executeQuery()){
            while (resultSet.next()){
                Long id=resultSet.getLong("id");
                Long idUser1=resultSet.getLong("iduser1");
                Long idUser2=resultSet.getLong("iduser2");
                Timestamp timestamp = resultSet.getTimestamp("friendsFrom");
                LocalDateTime friendsfrom = timestamp != null ? timestamp.toLocalDateTime() : null;
                FriendshipStatus status=FriendshipStatus.valueOf(resultSet.getString("status"));
                Friendship friendship=new Friendship(idUser1,idUser2,friendsfrom,status);

                friendship.setId(id);
                friendships.put(id,friendship);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return friendships.values();
    }
    public Iterable<Friendship> findAllRequest(Long id) {
        Iterable<Friendship> friendships = this.findAll();
        ArrayList<Friendship> friendshipsRequest = new ArrayList<>();
        for(Friendship friendship:friendships){
            if(friendship.getIdUser2().equals(id)&& friendship.getStatus()==FriendshipStatus.PENDING){
                friendshipsRequest.add(friendship);
            }
        }
        return friendshipsRequest;
    }
    public List<Long> findAllFriendships(Long id) {

        List<Long> allfriendships = new ArrayList<>();

        for (Friendship friendship : this.findAll()) {
            if (friendship.getIdUser2().equals(id)
                    && friendship.getStatus() == FriendshipStatus.ACCEPTED) {
                allfriendships.add(friendship.getIdUser1());
            }
            if (friendship.getIdUser1().equals(id)
                    && friendship.getStatus() == FriendshipStatus.ACCEPTED) {
                allfriendships.add(friendship.getIdUser2());
            }
        }
        return allfriendships;
    }


    @Override
    public Friendship save(Friendship entity) {
        if(entity==null){
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        String query = "INSERT INTO friendships (id, iduser1, iduser2, friendsfrom, status) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement preparedStatement=connection.prepareStatement(query);){
            preparedStatement.setLong(1,entity.getId());
            preparedStatement.setLong(2,entity.getIdUser1());
            preparedStatement.setLong(3,entity.getIdUser2());
            LocalDateTime localDateTime = entity.getFriendsFrom();
            Timestamp timestamp = Timestamp.valueOf(localDateTime);
            preparedStatement.setTimestamp(4,timestamp);
            preparedStatement.setString(5,entity.getStatus().toString());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public Friendship delete(Long id) {
        String query="DELETE FROM friendships WHERE \"id\" = ?";

        try(PreparedStatement preparedStatement=connection.prepareStatement(query);){
            preparedStatement.setLong(1,id);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        Friendship friendshipToDelete=null;
        for(Friendship friendship:findAll()){
            if(Objects.equals(friendship.getId(),id)){
                friendshipToDelete=friendship;
            }
        }
        return friendshipToDelete;
    }
    @Override
    public Friendship update(Friendship entity) {
        return entity;
    }

    public List<Friendship> findAllUserFriends(Long userId) {
        List<Friendship> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships WHERE iduser1 = ? OR iduser2 = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idUser1 = resultSet.getLong("iduser1");
                Long idUser2 = resultSet.getLong("iduser2");
                LocalDateTime friendsFrom = resultSet.getObject("friendsfrom", LocalDateTime.class);
                FriendshipStatus status = FriendshipStatus.valueOf(resultSet.getString("status"));

                Friendship friendship = new Friendship(idUser1, idUser2, friendsFrom, status);
                friendship.setId(id);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
