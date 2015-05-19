package project5;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mojtab23 on 5/19/15.
 */
public class ServerTest extends TestCase {
    static int i = 1;

    public static void main(String[] args) {
        new Thread(() -> {
            new Server(10000);
        }
        ).start();


//        sendRequest("GET /-=10 HTTP/1.1");
//        sendRequest("GET //=2 HTTP/1.1");
//        sendRequest("GET /-=10 HTTP/1.1");
//        sendRequest("GET /+=100000 HTTP/1.1");


    }


    private static void sendRequest(String request) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        request = request + "\n\n";
        final String finalRequest = request;
        Thread thread = new Thread(() -> {
            System.out.println((i++) + " Started");
            Socket socket = null;
            try {
                socket = new Socket("127.0.0.1", 10000);

                socket.getOutputStream();
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                writer.write(finalRequest);
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
        });
        thread.start();
    }

}