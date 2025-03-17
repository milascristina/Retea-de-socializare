package com.example.laborator6;

import domain.Friend;
import domain.Friendship;
import domain.FriendshipStatus;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.MessageValidator;
import domain.validators.UserValidator;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import repository.DataBase.FriendshipDBRepository;
import repository.DataBase.MessageDBRepository;
import repository.DataBase.UserDBRepository;
import repository.FriendRepository;
import service.FriendshipServer;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import service.MessageService;
import service.UserServer;
import javafx.event.ActionEvent;

import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class Fereastra2Controller {
    private User user;
    private FriendshipServer friendshipServer;
    private MessageService messageService;

    @FXML
    private TextField numeF;
    @FXML
    private TextField prenumeF;
    @FXML
    private TableView<Friend> FriendshipTableView;
    @FXML
    private TableColumn<Friend, Long> idFriendshipCol;
    @FXML
    private TableColumn<Friend, String> PrenumeFriendshipColumn;
    @FXML
    private TableColumn<Friend, String> NameFriendshipColumn;

    @FXML
    private TableColumn<Friend, String> FriendsFromColumn;

    @FXML
    private TableView<Friend> RequestTableView;

    @FXML
    private TableColumn<Friend, Long> idRequest;
    @FXML
    private TableColumn<Friend, String> numeRequest;
    @FXML
    private TableColumn<Friend, String> prenumeRequest;
    @FXML
    private TableColumn<Friend, String> dataRequest;
    @FXML
    private TableColumn<Friend, String> statusRequest;


    public void setService2(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setUser(User user) {
        this.user = user;
        System.out.println("User set: " + this.user);

        FriendRepository friendRepository = new FriendRepository();
        UserDBRepository userDBRepository = new UserDBRepository(new UserValidator());
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(new FriendshipValidator(userDBRepository));
        friendshipServer = new FriendshipServer(friendshipDBRepository,userDBRepository,friendRepository);


        initializeUser();
    }

    private void initializeUser() {
        System.out.println("Fereastra2Controller initialized with user: " + this.user);
        idFriendshipCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        NameFriendshipColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNume()));
        PrenumeFriendshipColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenume()));
        FriendsFromColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getData();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });

        idRequest.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        numeRequest.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNume()));
        prenumeRequest.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenume()));
        dataRequest.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getData();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(date != null ? date.format(formatter) : "");
        });
        statusRequest.setCellValueFactory(cellData -> {
            FriendshipStatus status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.name() : "");
        });


        loadFriend(this.user);
        loadRequest(this.user);

    }

    private void loadFriend(User user) {
        List<Friend> friends;
        Optional<Long> userIdOpt = new UserServer(new UserDBRepository(new UserValidator())).findId(user);
        System.out.println("ID/:" + userIdOpt.get());
        friends = friendshipServer.findAllFriendships(userIdOpt.get());

        FriendshipTableView.getItems().setAll(friends);
    }

    public void onAddButtonAction(ActionEvent actionEvent) {
        String nume = numeF.getText();
        String prenume = prenumeF.getText();
        Long id = new UserServer(new UserDBRepository(new UserValidator())).findID(nume, prenume);
        if (id == null) {
            System.out.println("ID not found");
            return;
        }
        Optional<Long> userIdOpt = new UserServer(new UserDBRepository(new UserValidator())).findId(user);
        if (userIdOpt.isPresent()) {
            friendshipServer.sendRequest(userIdOpt.get(), id);
        } else {
            System.out.println("User ID not found");
        }
    }


    private void loadRequest(User user) {
        List<Friend> requests;
        requests=this.friendshipServer.getRequest(user);
        for(Friend friend : requests) {
            System.out.println(friend);
        }
        RequestTableView.getItems().setAll(requests);
        if(requests.size() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CERERI");
            alert.setHeaderText("Ai cereri de prietenie care asteapta raspuns!");
            alert.show();
            PauseTransition pause = new PauseTransition(Duration.seconds(5));

            pause.setOnFinished(event -> alert.close());
            pause.play();
        }

    }
    @FXML
    public void onDeleteButtonAction(ActionEvent actionEvent) {
        Friend selectedFriend = FriendshipTableView.getSelectionModel().getSelectedItem();

        if (selectedFriend != null) {
            Long friendId = selectedFriend.getId();
            Optional<Long> userIdOpt = new UserServer(new UserDBRepository(new UserValidator())).findId(user);
            if (userIdOpt.isPresent()) {
                friendshipServer.removeFriendship(userIdOpt.get(), friendId);
                FriendshipTableView.getItems().remove(selectedFriend);
            }
        }
    }


    @FXML
    public void onChatButtonAction(ActionEvent actionEvent) {
        Friend selectedFriend = FriendshipTableView.getSelectionModel().getSelectedItem();

        if (selectedFriend != null) {
            try {
                // Print to track the flow
                System.out.println("Entering onChatButtonAction...");

                // Load the FXML file for the chat window
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fereastraChat.fxml"));
                Parent root = loader.load(); // Load the FXML file into the root object
                // Get the controller of the FXML file
                ChatController chatController = loader.getController();
                // Create a User object for the selected friend
                User friendUser = this.friendshipServer.findUser(selectedFriend.getId());

                chatController.setService3(this.messageService);
                // Initialize the chat window with the data
                chatController.initData(this.user, friendUser);

                // Create a new stage and set the scene
                Stage stage = new Stage(); // Create a new stage here
                stage.setTitle("Chat with " + selectedFriend.getNume() + " " + selectedFriend.getPrenume());
                stage.setScene(new Scene(root)); // Set the loaded root as the scene's root
                stage.show(); // Show the new stage
            } catch (IOException e) {
                e.printStackTrace();
                // Display an error alert if the FXML loading fails
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot open chat window");
                alert.setContentText("An error occurred while trying to open the chat window.");
                alert.showAndWait();
            }
        } else {
            // Display a warning if no friend is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("No friend selected");
            alert.setContentText("Please select a friend to chat with.");
            alert.showAndWait();
        }
    }



    @FXML
    public void onAcceptedButtonAction(ActionEvent actionEvent) {
        Friend selectedRequest = RequestTableView.getSelectionModel().getSelectedItem();

        if (selectedRequest != null) {
            String nume = selectedRequest.getNume();
            String prenume = selectedRequest.getPrenume();
            LocalDateTime data = selectedRequest.getData();
            Long friendId = new UserServer(new UserDBRepository(new UserValidator()))
                    .findID(nume, prenume);

            if (friendId != null) {
                friendshipServer.addFriendship(this.user.getId(), friendId);
                RequestTableView.getItems().remove(selectedRequest);
                FriendshipTableView.getItems().add(selectedRequest);
            }
        }
    }


    @FXML
    public void onAfisarePrietenPaginatButton(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/laborator6/afisare.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            AfisarePaginataController controller = fxmlLoader.getController();
            controller.setUser(this.user);
            controller.initialize();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Eroare la încărcarea FXML: " + e.getMessage());
        }

    }
}
