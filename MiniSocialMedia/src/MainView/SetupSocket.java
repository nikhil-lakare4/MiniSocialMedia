package MainView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class SetupSocket {

    private static Socket S;
    private static DataInputStream Din;
    private static DataOutputStream Dout;
    private static SetupSocket INSTANCE;

    static {
        try {
            INSTANCE = new SetupSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SetupSocket() throws Exception {
        S = new Socket("localhost", 8087);
        Din = new DataInputStream(S.getInputStream());
        Dout = new DataOutputStream(S.getOutputStream());
        System.out.println("Socket Created Successfully!");
    }

    public static SetupSocket getINSTANCE() {
        return INSTANCE;
    }

    public static DataInputStream getDin() {
        return Din;
    }

    public static DataOutputStream getDout() {
        return Dout;
    }

    public static void closeSocket() throws Exception {
        S.close();
        Din.close();
        Dout.close();
        System.out.println("Socket Closed Successfully!");
    }
}
