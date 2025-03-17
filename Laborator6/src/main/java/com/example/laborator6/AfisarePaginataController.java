package com.example.laborator6;

import domain.Friend;
import domain.Friendship;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.DataBase.FriendshipDBRepository;
import repository.DataBase.UserDBRepository;
import repository.FriendRepository;
import service.FriendshipServer;
import util.Page;
import util.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class AfisarePaginataController {
    private FriendshipServer friendshipServer;
    private User user;
    @FXML
    private TableView<Friend> tableView;
    @FXML
    private TableColumn<Friend, Long> tableColumnId;
    @FXML
    private TableColumn<Friend, String> tableColumnNume;
    @FXML
    private TableColumn<Friend, String> tableColumnPrenume;
    @FXML
    private TableColumn<Friend, LocalDateTime> tableColumnData;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private Label pageNumber;
    private ObservableList<Friend> model = FXCollections.observableArrayList();



    private int currentPage = 0;
    private int pageSize = 2;
    private int numberOfElements = 0;

    @FXML
    public void initialize() {
        tableColumnId.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        tableColumnNume.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNume()));
        tableColumnPrenume.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenume()));
        tableColumnData.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getData()));

        tableView.setItems(model);
        currentPage = 0;
        initModel();
    }


    @FXML
    private void initModel() {
        if (user == null) {
            System.err.println("User is not set. Cannot initialize model.");
            return; // Sau throw new IllegalStateException("User must be set before calling initModel");
        }

        FriendRepository friendRepository = new FriendRepository();
        UserDBRepository userDBRepository = new UserDBRepository(new UserValidator());
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(new FriendshipValidator(userDBRepository));
        friendshipServer = new FriendshipServer(friendshipDBRepository, userDBRepository, friendRepository);

        Page<Friend> page = friendshipServer.findAllOnPage(new Pageable(currentPage, pageSize), user);
        List<Friend> friends = StreamSupport.stream(page.getElementsOnPage().spliterator(), false).toList();
        System.out.println("friends: "+ friends.size());
        model.setAll(friends);

        prevButton.setDisable(currentPage == 0);
        int noOfPages = (int) (Math.ceil((double) page.getTotalNumberElements() / pageSize));
        nextButton.setDisable(currentPage + 1 == noOfPages);
        pageNumber.setText((currentPage + 1) + " / " + noOfPages);
    }

    @FXML
    private void onPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            initModel();
        }
    }

    @FXML
    private void onNextPage() {
        currentPage++;
        initModel();
    }
    public void setUser(User user) {
        this.user=user;
        System.out.println("User set: " + this.user);
    }
}
