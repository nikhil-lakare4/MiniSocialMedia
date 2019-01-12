package MainView;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static SetupSocket socket;
    public static Stage primaryStage;
    public static BorderPane mainLayout;
    public static Scene mainScene;
    public static BorderPane homeLayout;
    public static Scene homeScene;

    public static void showSignIn() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("SignIn.fxml"));
        GridPane signInLayout = loader.load();
        mainLayout.setCenter(signInLayout);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        socket = SetupSocket.getINSTANCE();
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Mini Social Media");
        showMainView();
        showSignIn();
    }

    public void showMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("MainView.fxml"));
        mainLayout = loader.load();
        mainScene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        socket.getDout().writeUTF("exit");
        socket.closeSocket();
    }
}
