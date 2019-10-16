package MainView;

import static MainView.Main.socket;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SignUpControllerTest {
	
	public static SignUpController signUpController;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		signUpController = new SignUpController();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		signUpController = null;
	}

	@Test
	void testSaveData() throws IOException {
		
		String first_name, last_name, email_id, contact_no, username, password,cpassword,birthday,gender,about;
		
		first_name = "Mohit";
		last_name = "Nakhale";
		email_id = "mohitnakhale7@gmail.com";
		contact_no = "4684516";
		username = "mohitnakhale7";
		password = "123456";
		cpassword = "123456";
		birthday = "18/10/19";
		gender = "Male";
		about = "Footballer";
		
		socket.getDout().writeUTF("signUp");

        socket.getDout().writeUTF(first_name);
        socket.getDout().writeUTF(last_name);
        socket.getDout().writeUTF(email_id);
        socket.getDout().writeUTF(contact_no);
        socket.getDout().writeUTF(username);
        socket.getDout().writeUTF(password);
        socket.getDout().writeUTF(cpassword);
        socket.getDout().writeUTF(birthday);
        socket.getDout().writeUTF(gender);
        socket.getDout().writeUTF(about);

        LocalDate dateOfSignUp = LocalDate.now();
        socket.getDout().writeUTF(dateOfSignUp.toString());
        socket.getDout().writeUTF("NA");
        
        boolean result = socket.getDin().readBoolean();
        
        assertFalse(result);
        
        socket.getDout().writeUTF("signUp");

        socket.getDout().writeUTF(first_name);
        socket.getDout().writeUTF(last_name);
        socket.getDout().writeUTF(email_id);
        socket.getDout().writeUTF(contact_no);
        socket.getDout().writeUTF(username);
        socket.getDout().writeUTF(password);
        socket.getDout().writeUTF(cpassword);
        socket.getDout().writeUTF(birthday);
        socket.getDout().writeUTF(gender);
        socket.getDout().writeUTF(about);

        dateOfSignUp = LocalDate.now();
        socket.getDout().writeUTF(dateOfSignUp.toString());
        socket.getDout().writeUTF("NA");
        
        result = socket.getDin().readBoolean();
        
        assertFalse(result);
        
	}
}
