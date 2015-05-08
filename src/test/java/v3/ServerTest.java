package v3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Mojtaba on 4/12/2015.
 */
public class ServerTest {
    public static void main(String[] args) throws IOException {
        Server server = new Server(10000);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                Socket socket = null;
                try {
                    socket = new Socket("127.0.0.1", 10000);

                    socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(socket.getOutputStream());
                    writer.write("GET /+=2 HTTP/1.1 \n\n");
                    writer.flush();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.println("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }


    }
}