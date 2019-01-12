package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class MainServer {
    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 1080;
        /*
         * Open a server socket on the portNumber (default 2222). Note that we can
         * not choose a port less than 1023 if we are not privileged users (root).
         */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        System.out.println("Client " + i + " connected");
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. The thread broadcast the incoming messages to all clients and
 * routes the private message to the particular client. When a client leaves the
 * chat room this thread informs also all the clients about that and terminates.
 */
class clientThread extends Thread {

    private String clientName = null;
    private DataInputStream Din = null;
    private DataOutputStream Dout = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {
            /*
             * Create input and output streams for this client.
             */
            Din = new DataInputStream(clientSocket.getInputStream());
            Dout = new DataOutputStream(clientSocket.getOutputStream());

            String url = "jdbc:oracle:thin:@localhost:1521:xe";
            String user = "DarkKnight532";
            String pass = "DarkKnight";

            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();

            boolean permission;

            String choice;

            while (true) {

                permission = false;

                choice = Din.readUTF();

                if (choice.equals("signIn")) {

                    System.out.println("Signing in......");

                    String uName = Din.readUTF();
                    String pWord = Din.readUTF();
                    System.out.println(uName + " " + pWord);
                    try {
                        String sql = "SELECT username, password FROM USERINFO" +
                                " where username = '" + uName + "' and password = '" + pWord + "'";
                        ResultSet rs = st.executeQuery(sql);

                        if (rs.next()) {
                            permission = true;
                            clientName = rs.getString("username");
                            System.out.println(clientName + " signed in!");
                            sql = "UPDATE USERINFO SET STATUS = 'online' where USERNAME = '"+clientName+"'";
                            if (st.executeUpdate(sql) == 1) {
                                System.out.println(clientName + " is online!");
                            }
                        }

                        Dout.writeBoolean(permission);
                        rs.close();

                    } catch (SQLException ex) {
                        System.err.println(ex);
                    }

                } else if (choice.equals("signUp")) {

                    String first_name = Din.readUTF();
                    String last_name = Din.readUTF();
                    String email_id = Din.readUTF();
                    String contact_no = Din.readUTF();
                    String username = Din.readUTF();
                    String password = Din.readUTF();
                    String cpassword = Din.readUTF();
                    String birthdate = Din.readUTF();
                    String gender = Din.readUTF();
                    String about = Din.readUTF();
                    String dateOfSignUp = Din.readUTF();
                    String dpPath = Din.readUTF();
                    String status = "offline";

                    try {

                        String sql = "INSERT INTO USERINFO VALUES('" + first_name + "', '" + last_name + "', '" + email_id + "', '" + contact_no + "', " +
                                "'" + username + "', '" + password + "', '" + birthdate + "', '" + about + "', '" + gender + "'," +
                                " '" + dateOfSignUp + "', '" + dpPath + "', '"+status+"')";

                        if(st.executeUpdate(sql) == 1)
                            permission = true;

                        Dout.writeBoolean(permission);

                    } catch (SQLException ex) {
                        Dout.writeBoolean(permission);
                        System.err.println(ex);
                    }

                } else if (choice.equals("loadHome")) {

                    try {

                        String userId = Din.readUTF();

                        String sql = "SELECT first_name, dppath FROM USERINFO" +
                                " where username = '" + userId + "'";
                        ResultSet rs = st.executeQuery(sql);

                        if (rs.next()) {
                            Dout.writeUTF(rs.getString("first_name"));
                            Dout.writeUTF(rs.getString("dppath"));
                        }

                        rs.close();
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                } else if(choice.equals("getOnlineFriends")) {

                    String sql = "SELECT COUNT(USERNAME) AS COUNT FROM USERINFO WHERE STATUS = 'online'";
                    ResultSet rs = st.executeQuery(sql);
                    int count;
                    if(rs.next())
                        count = rs.getInt("COUNT");
                    else
                        count = 0;
                    Dout.writeInt(count);

                    sql = "SELECT USERNAME FROM USERINFO WHERE STATUS = 'online'";
                    rs = st.executeQuery(sql);

                    while(rs.next())
                        Dout.writeUTF(rs.getString("username"));

                    rs.close();

                } else if (choice.equals("sendText")) {

                    String receiverName = Din.readUTF();
                    String receivedText = Din.readUTF();
                    String receivedTime = Din.readUTF();

                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i] != this
                                    && threads[i].clientName != null
                                    && threads[i].clientName.equals(receiverName)) {
                                threads[i].Dout.writeUTF(receivedText);
                                threads[i].Dout.writeUTF(receivedTime);
                                break;
                            }
                        }
                    }

                } else if (choice.equals("exit")) {

                    String sql = "UPDATE USERINFO SET STATUS = 'offline' where USERNAME = '"+clientName+"'";
                    if(st.executeUpdate(sql) == 1) {
                        System.out.println(clientName + " is offline!");
                    }
                    break;
                } else if (choice.equals("killThread")){

                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i] == this) {
                                threads[i].Dout.writeUTF("null");
                                threads[i].Dout.writeUTF("null");
                                break;
                            }
                        }
                    }
                }
            }
            /*
             * Clean up. Set the current thread variable to null so that a new client
             * could be accepted by the server.
             */
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                        System.out.println("Client " + i + " Cleaned Up!");
                    }
                }
            }
            /*
             * Close the output stream, close the input stream, close the socket.
             */
            con.close();
            st.close();
            Din.close();
            Dout.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
