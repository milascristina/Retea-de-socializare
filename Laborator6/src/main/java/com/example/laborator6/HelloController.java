package com.example.laborator6;

import domain.User;
import domain.validators.UserValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import repository.DataBase.UserDBRepository;
import service.MessageService;
import service.UserServer;

import java.io.IOException;

public class HelloController {
    @FXML
    private TextField numeField;
    @FXML
    private TextField prenumeField;
    @FXML
    private PasswordField parolaField;
    @FXML
    private Label welcomeText;

    private MessageService messageService;
    @FXML
    private Label errorMessage;

    public void setService1(MessageService service) {
        this.messageService = service;
    }

    public void onLoginButtonAction(ActionEvent actionEvent) {
        User user = conectare();

        if (user != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fereastra2.fxml"));
                SplitPane root = fxmlLoader.load();
                Fereastra2Controller controller = fxmlLoader.getController();
                controller.setService2(this.messageService);
                Stage stage = new Stage();
                stage.setTitle("Retea de socializare");
                stage.setScene(new Scene(root, 300, 200));

                if (welcomeText != null) {
                    Stage currentStage = (Stage) welcomeText.getScene().getWindow();
                    currentStage.close();
                }
                controller.setUser(user);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorMessage.setText("Autentificare eșuată. Te rugăm să încerci din nou.");
            errorMessage.setStyle("-fx-text-fill: red;");
        }
    }


    public User conectare() {
        String nume = numeField.getText();
        String prenume = prenumeField.getText();
        String parola = parolaField.getText();

        if (nume.isEmpty() || prenume.isEmpty() || parola.isEmpty()) {
            errorMessage.setText("Te rugăm să completezi toate câmpurile.");
            errorMessage.setStyle("-fx-text-fill: red;");
            return null;
        }

        UserValidator userValidator = new UserValidator();
        UserDBRepository userDBRepository = new UserDBRepository(userValidator);
        UserServer userServer = new UserServer(userDBRepository);
        User user = userServer.findOne(nume, prenume, parola);

        if (user == null) {
            errorMessage.setText("Autentificare eșuată. Te rugăm să încerci din nou.");
            errorMessage.setStyle("-fx-text-fill: red;");
        }

        return user;
    }


}
