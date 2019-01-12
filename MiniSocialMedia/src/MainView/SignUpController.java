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

    public void initialize() throws Exception {
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

        socket.getDout().writeUTF(first_name.getText().trim());
        socket.getDout().writeUTF(last_name.getText());
        socket.getDout().writeUTF(email_id.getText());
        socket.getDout().writeUTF(contact_no.getText());
        socket.getDout().writeUTF(username.getText());
        socket.getDout().writeUTF(password.getText());
        socket.getDout().writeUTF(cpassword.getText());
        socket.getDout().writeUTF(birthday.getText());
        socket.getDout().writeUTF(gender.getText());
        socket.getDout().writeUTF(about.getText());

        LocalDate dateOfSignUp = LocalDate.now();
        socket.getDout().writeUTF(dateOfSignUp.toString());
        socket.getDout().writeUTF(dpPath);

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
