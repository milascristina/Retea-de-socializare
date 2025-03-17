package com.example.laborator6;

import domain.validators.MessageValidator;
import domain.validators.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.DataBase.MessageDBRepository;
import repository.DataBase.UserDBRepository;
import service.MessageService;

import java.io.IOException;

public class HelloApplication extends Application {
    UserDBRepository userDBRepository=new UserDBRepository(new UserValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(new MessageValidator(userDBRepository), userDBRepository);
    private final MessageService messageService=new MessageService(messageDBRepository);
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        HelloController controller = fxmlLoader.getController();
        controller.setService1(this.messageService);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}