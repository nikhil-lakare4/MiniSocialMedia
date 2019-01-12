package MainView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

import static MainView.Main.*;

public class HomeController {

    public static String userId;
    public ChatController chatController;
    public BorderPane chatLayout;
    public String first_name;
    public String dpPath;
    @FXML
    public Label homeLabel;
    @FXML
    public ImageView imageView;

    @FXML
    public void onLogout() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Do you really want to Logout?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (chatController != null && chatController.chatBoxController != null) {
                socket.getDout().writeUTF("killThread");
            }
            primaryStage.setScene(mainScene);
            primaryStage.show();
            Main.showSignIn();
        }
    }

    @FXML
    public void onChats() throws IOException {

        if (chatController != null && chatController.chatBoxController != null) {
            socket.getDout().writeUTF("killThread");
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Chat.fxml"));
        chatLayout = loader.load();
        chatController = loader.getController();
        homeLayout.setCenter(chatLayout);
    }
}
