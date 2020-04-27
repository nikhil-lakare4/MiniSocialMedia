package MainView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MainView.Encryption.DES;

import static MainView.Main.homeLayout;
import static MainView.Main.socket;

public class ChatController {

    public static String friendName;
    public ChatBoxController chatBoxController;
    public BorderPane chatBoxLayout;
    @FXML
    public ListView<String> friendListView;
    
    String key = null;;
    DES des = null;

    public void initialize() throws IOException {
    	
    	key = "QWERTYUI";
    	des = new DES();

        List<String> friendList = new ArrayList<>();
        socket.getDout().writeUTF("getOnlineFriends");
        int num = socket.getDin().readInt();
        for (int i = 0; i < num; i++) {
            friendList.add(des.decryptText(socket.getDin().readUTF(), key));
        }
        ObservableList<String> list = FXCollections.observableList(friendList);
        friendListView.setItems(list);

        friendListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                friendName = friendListView.getSelectionModel().getSelectedItem();
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("ChatBox.fxml"));
                    chatBoxLayout = loader.load();
                    chatBoxController = loader.getController();
                    homeLayout.setCenter(chatBoxLayout);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

