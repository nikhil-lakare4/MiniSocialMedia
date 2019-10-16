package MainView;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SetupSocketTest {
	
	public static SetupSocket socket;
	public static Main main;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		main = new Main();
		socket = MainView.SetupSocket.getINSTANCE();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		socket = null;
	}

	@Test
	void testGetINSTANCE() {
		assertNotNull(socket);
	}

	@Test
	void testGetDin() {
		assertNotNull(socket.getDin());
	}

	@Test
	void testGetDout() {
		assertNotNull(socket.getDout());
	}

}
