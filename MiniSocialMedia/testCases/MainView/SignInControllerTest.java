package MainView;

import static MainView.Main.socket;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SignInControllerTest {
	
	public static SignInController signInController;
	public static SetupSocket socket;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		signInController = new SignInController();
		socket = MainView.SetupSocket.getINSTANCE();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		signInController = null;
	}

	@Test
	void testOnSignIn() throws IOException {
		
		assertNotNull(socket);
		
		String userName = "nikhil_lakare4";
		String passWord = "655532";
		
		socket.getDout().writeUTF("signIn");

        socket.getDout().writeUTF(userName);
        socket.getDout().writeUTF(passWord);
		
		boolean result = socket.getDin().readBoolean();
		
		assertTrue(result);
		
		userName = "sagar_tejwani";
		passWord = "1234";
		
		socket.getDout().writeUTF("signIn");

        socket.getDout().writeUTF(userName);
        socket.getDout().writeUTF(passWord);
		
		result = socket.getDin().readBoolean();
		
		assertFalse(result);
		
	}
	
}
