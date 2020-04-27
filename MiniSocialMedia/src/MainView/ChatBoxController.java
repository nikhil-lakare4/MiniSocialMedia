package MainView;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Scanner;

import MainView.Encryption.DES;

import static MainView.Main.socket;

public class ChatBoxController {

	public String currentFriend;

	@FXML
	private ScrollPane scrollPane;
	@FXML
	private Label friendName;
	@FXML
	private TextField textField;
	@FXML
	private VBox vBox;

	String key = null;;
	DES des = null;

	public void initialize() {

		key = "QWERTYUI";
		des = new DES();

		currentFriend = ChatController.friendName;
		friendName.setText(currentFriend);

		Text text, time;
		try {
			File textFile = new File("C:\\MSMData\\" + HomeController.userId + "\\chats\\" + currentFriend + ".txt");
			textFile.getParentFile().mkdirs();
			textFile.createNewFile();
			Scanner s = new Scanner(textFile);
			s.useDelimiter("\n");
			while (s.hasNext()) {
				HBox hBox = new HBox();
				hBox.setPadding(new Insets(10, 20, 10, 20));
				if (s.next().equals("R")) {
					hBox.setAlignment(Pos.CENTER_RIGHT);
				} else {
					hBox.setAlignment(Pos.CENTER_LEFT);
				}
				text = new Text(des.decryptText(s.next(), key));
				text.setFill(Color.WHITE);
				text.setFont(Font.font(20));
				time = new Text(des.decryptText(s.next(), key));
				time.setFill(Color.DARKGREY);
				time.setFont(Font.font(10));
				HBox.setMargin(time, new Insets(12, 0, 0, 10));
				HBox textBox = new HBox();
				textBox.setStyle("-fx-background-color: #2962FF; -fx-background-radius: 10");
				textBox.setPadding(new Insets(0, 10, 5, 10));
				textBox.setMaxWidth(scrollPane.getPrefWidth() / 2);
				textBox.getChildren().addAll(text, time);
				hBox.getChildren().add(textBox);
				vBox.getChildren().add(hBox);

			}
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		vBox.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				scrollPane.setVvalue(1.0);
			}
		});

		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.ENTER)) {
					onSend();
				}
			}
		});

		ReadTextThread readTextThread = new ReadTextThread();
		readTextThread.start();
	}

	@FXML
	public void onSend() {

		try {
			FileWriter fw = new FileWriter(
					"C:\\MSMData\\" + HomeController.userId + "\\chats\\" + currentFriend + ".txt", true);
			fw.write("R" + "\n");
			fw.write(des.encryptText(textField.getText(), key)+"\n");
			fw.write(des.encryptText(LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)), key)+"\n");
			fw.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		Text text = new Text(textField.getText());
		text.setFill(Color.WHITE);
		text.setFont(Font.font(20));
		Text time = new Text(LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)));
		time.setFill(Color.DARKGRAY);
		time.setFont(Font.font(10));
		HBox.setMargin(time, new Insets(12, 0, 0, 10));
		HBox textBox = new HBox();
		textBox.setStyle("-fx-background-color: #2962FF; -fx-background-radius: 10");
		textBox.setPadding(new Insets(0, 10, 5, 10));
		textBox.setMaxWidth(scrollPane.getPrefWidth() / 2);
		textBox.getChildren().addAll(text, time);
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER_RIGHT);
		hBox.setPadding(new Insets(10, 20, 10, 20));
		hBox.getChildren().add(textBox);
		vBox.getChildren().add(hBox);

		try {
			socket.getDout().writeUTF("sendText");
			socket.getDout().writeUTF(des.encryptText(currentFriend, key));
			socket.getDout().writeUTF(des.encryptText(textField.getText(), key));
			socket.getDout().writeUTF(des.encryptText(time.getText(), key));

		} catch (IOException e) {
			e.printStackTrace();
		}
		textField.clear();
	}

	public class ReadTextThread extends Thread {

		public void run() {

			while (true) {

				try {
					// read the message sent to this client
					String receivedText = socket.getDin().readUTF();
					String receivedTime = socket.getDin().readUTF();

					if (receivedText.equals("null") && receivedTime.equals("null"))
						break;

					try {
						FileWriter fw = new FileWriter(
								"C:\\MSMData\\" + HomeController.userId + "\\chats\\" + currentFriend + ".txt", true);
						fw.write("L" + "\n");
						fw.write(receivedText + "\n");
						fw.write(receivedTime + "\n");
						fw.close();
					} catch (Exception e) {
						System.out.println(e);
					}

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// Update UI here.
							Text text = new Text(des.decryptText(receivedText,key));
							text.setFill(Color.WHITE);
							text.setFont(Font.font(20));
							Text time = new Text(des.decryptText(receivedTime, key));
							time.setFill(Color.DARKGRAY);
							time.setFont(Font.font(10));
							HBox.setMargin(time, new Insets(12, 0, 0, 10));
							HBox textBox = new HBox();
							textBox.setStyle("-fx-background-color: #2962FF; -fx-background-radius: 10");
							textBox.setPadding(new Insets(0, 10, 5, 10));
							textBox.setMaxWidth(scrollPane.getPrefWidth() / 2);
							textBox.getChildren().addAll(text, time);
							HBox hBox = new HBox();
							hBox.setAlignment(Pos.CENTER_LEFT);
							hBox.setPadding(new Insets(10, 20, 10, 20));
							hBox.getChildren().add(textBox);
							vBox.getChildren().add(hBox);
						}
					});

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Thread Killed!");
		}
	}
}
