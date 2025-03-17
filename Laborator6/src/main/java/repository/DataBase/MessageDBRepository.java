package repository.DataBase;

import domain.Message;
import domain.User;
import domain.validators.MessageValidator;
import domain.validators.ValidatorException;
import repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepository implements Repository <Long, Message> {
    private MessageValidator validator;
    private Connection connection;
    private UserDBRepository userDBRepository;

    public MessageDBRepository(MessageValidator validator,UserDBRepository userDBRepository) {
        this.validator = validator;
        this.userDBRepository = userDBRepository;
        try{
            this.connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/lab6","postgres","feliciamami");

        }catch (SQLException e){
            e.printStackTrace();
        };
    }

    @Override
    public Message findOne(Long id) {
        String query = "SELECT * FROM messages WHERE id = ?";
        Message message = null;
        try(PreparedStatement preparedStatement=connection.prepareStatement(query)){
            preparedStatement.setLong(1,id);
            ResultSet resultSet=preparedStatement.executeQuery();

            if(resultSet.next()){
                message = mapResultSetToMessage(resultSet);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error fetching message with ID " + id, e);
        }
        return message;
    };
    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                messages.add(mapResultSetToMessage(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all messages", e);
        }

        return messages;
    }

    @Override
    public Message save(Message message){
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        String query = "INSERT INTO messages (from_user_id, to_user_ids, message, sent_at, reply_to_id) VALUES (?, ?, ?, ?, ?)";
        this.validator.validate(message);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, message.getFrom().getId()); // Presupunem că avem un obiect User
            preparedStatement.setString(2, String.join(",", message.getTo().stream().map(User::getId).map(String::valueOf).toArray(String[]::new)));
            preparedStatement.setString(3, message.getMessage());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(message.getData()));
            preparedStatement.setObject(5, message.getReply() != null ? message.getReply().getId() : null);

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                message.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving message", e);
        }

        return message;
    }

    @Override
    public Message delete(Long id){
        String selectQuery = "SELECT * FROM messages WHERE id = ?";
        Message messageToDelete = null;

        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setLong(1, id);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                messageToDelete = mapResultSetToMessage(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching message for deletion", e);
        }

        if (messageToDelete == null) {
            return null;
        }

        String deleteQuery = "DELETE FROM messages WHERE id = ?";

        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting message", e);
        }

        return messageToDelete;
    }
    @Override
    public Message update(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        String checkQuery = "SELECT * FROM messages WHERE id = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setLong(1, message.getId());
            ResultSet resultSet = checkStatement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if message exists for update", e);
        }

        String updateQuery = "UPDATE messages SET from_user_id = ?, to_user_ids = ?, message = ?, sent_at = ?, reply_to_id = ? WHERE id = ?";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setLong(1, message.getFrom().getId());
            updateStatement.setString(2, String.join(",", message.getTo().stream().map(User::getId).map(String::valueOf).toArray(String[]::new)));
            updateStatement.setString(3, message.getMessage());
            updateStatement.setTimestamp(4, Timestamp.valueOf(message.getData()));
            updateStatement.setObject(5, message.getReply() != null ? message.getReply().getId() : null);
            updateStatement.setLong(6, message.getId());

            int rowsAffected = updateStatement.executeUpdate();
            if (rowsAffected == 0) {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating message", e);
        }

        return message;
    }


    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long fromUserId = rs.getLong("from_user_id");
        String toUserIds = rs.getString("to_user_ids");
        String messageText = rs.getString("message");
        Timestamp sentAt = rs.getTimestamp("sent_at");
        Long replyToId = (Long) rs.getObject("reply_to_id");

        User fromUser = this.userDBRepository.findOne(fromUserId);
        List<User> toUsers = parseUserIds(toUserIds);

        Message reply = (replyToId != null) ? findOne(replyToId) : null;

        return new Message(fromUser, toUsers, messageText, sentAt.toLocalDateTime(), reply);
    }

    private List<User> parseUserIds(String toUserIds) {
        List<User> users = new ArrayList<>();
        if (toUserIds != null && !toUserIds.isEmpty()) {
            String[] ids = toUserIds.split(",");
            for (String id : ids) {
                users.add(this.userDBRepository.findOne(Long.valueOf(id)));
            }
        }
        return users;
    }
    public List<Message> findMessagesBetweenUsers(Long userId1, Long userId2) {
        String query = "SELECT * FROM messages WHERE (from_user_id = ? AND to_user_ids LIKE ?) OR (from_user_id = ? AND to_user_ids LIKE ?)";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, userId1);
            preparedStatement.setString(2, "%" + userId2 + "%");
            preparedStatement.setLong(3, userId2);
            preparedStatement.setString(4, "%" + userId1 + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                messages.add(mapResultSetToMessage(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching messages between users", e);
        }
        return messages;
    }

}