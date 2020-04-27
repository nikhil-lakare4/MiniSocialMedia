package MainView;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;

import MainView.Encryption.DES;

import static MainView.Main.*;

public class SignInController {

    public HomeController homeController;
    public String userId;

    @FXML
    private TextField userName;
    @FXML
    private PasswordField passWord;
    @FXML
    private Label label;
    
    String key = null;;
    DES des = null;

    public void initialize() throws IOException {
    	key = "QWERTYUI";
    	des = new DES();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Home.fxml"));
        homeLayout = loader.load();
        homeScene = new Scene(homeLayout, 800, 600);
        homeController = loader.getController();

        userName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    try {
                        onSignIn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        passWord.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    try {
                        onSignIn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    public void onSignIn() throws Exception {

        socket.getDout().writeUTF("signIn");
        
        String key = "QWERTYUI";
        DES des = new DES();
        
        socket.getDout().writeUTF(des.encryptText(userName.getText().trim(), key));
        socket.getDout().writeUTF(des.encryptText(passWord.getText().trim(), key));

        boolean permission = socket.getDin().readBoolean();

        if (permission == true) {
            this.label.setTextFill(Color.rgb(0, 255, 0));
            this.label.setText("Success. Here You Go!");
            userId = userName.getText();
            showHome();
        } else {
            this.label.setTextFill(Color.rgb(255, 0, 0));
            this.label.setText("Incorrect Username or Password. Retry");
        }
    }

    @FXML
    public void showHome() throws IOException {
        primaryStage.setScene(homeScene);
        primaryStage.show();

        socket.getDout().writeUTF("loadHome");
        socket.getDout().writeUTF(des.encryptText(userId, key));

        homeController.userId = userId;
        homeController.first_name = des.decryptText(socket.getDin().readUTF(), key);
        homeController.dpPath = des.decryptText(socket.getDin().readUTF(), key);
        homeController.homeLabel.setText(homeController.first_name);
        Image image = new Image(homeController.dpPath);
        homeController.imageView.setImage(image);
    }

    @FXML
    public void onSignUp() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("SignUp.fxml"));
        GridPane signUpLayout = loader.load();
        mainLayout.setCenter(signUpLayout);
    }
}
