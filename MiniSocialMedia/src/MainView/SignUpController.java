package MainView;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import MainView.Encryption.DES;

import static MainView.Main.socket;

public class SignUpController {

    @FXML
    private ImageView imageID;
    @FXML
    private TextField first_name;
    @FXML
    private TextField last_name;
    @FXML
    private TextField email_id;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField cpassword;
    @FXML
    private TextField birthday;
    @FXML
    private TextField contact_no;
    @FXML
    private RadioButton gender;
    @FXML
    private TextField about;

    private String dpPath;
    
    String key = null;
    DES des = null;

    public void initialize() throws Exception {
    	
    	key = "QWERTYUI";
    	des = new DES();
    	
        File file = new File("C:\\Users\\Nikhil Lakare\\Pictures\\signupicon.png");
        Image image = new Image(file.toURI().toString());
        imageID.setImage(image);
    }

    @FXML
    public void uploadImage() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Picture (*.jpg, *.jpeg, *.png, *.bmp)", "*.jpg", "*.jpeg", "*.png", "*.bmp");
        chooser.getExtensionFilters().add(extFilter);
        File file = chooser.showOpenDialog(Main.primaryStage);
        if (file != null) {
            dpPath = file.toURI().toString();
            Image image = new Image(file.toURI().toString());
            imageID.setImage(image);
        }
    }

    @FXML
    public void saveData() throws Exception {


        socket.getDout().writeUTF("signUp");

        socket.getDout().writeUTF(des.encryptText(first_name.getText(), key));
        socket.getDout().writeUTF(des.encryptText(last_name.getText(), key));
        socket.getDout().writeUTF(des.encryptText(email_id.getText(), key));
        socket.getDout().writeUTF(des.encryptText(contact_no.getText(), key));
        socket.getDout().writeUTF(des.encryptText(username.getText(), key));
        socket.getDout().writeUTF(des.encryptText(password.getText(), key));
        socket.getDout().writeUTF(des.encryptText(cpassword.getText(), key));
        socket.getDout().writeUTF(des.encryptText(birthday.getText(), key));
        socket.getDout().writeUTF(des.encryptText(gender.getText(), key));
        socket.getDout().writeUTF(des.encryptText(about.getText(), key));

        LocalDate dateOfSignUp = LocalDate.now();
        socket.getDout().writeUTF(des.encryptText(dateOfSignUp.toString(), key));
        socket.getDout().writeUTF(des.encryptText(dpPath, key));

        if (socket.getDin().readBoolean()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Success!");
            alert.setContentText("Your account has been successfully created! You can Sign In!");
            alert.showAndWait();
            Main.showSignIn();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Failure!");
            alert.setContentText("Please Retry!");
            alert.showAndWait();
        }
    }

    @FXML
    public void onCancel() throws IOException {
        Main.showSignIn();
    }

}
