package v3;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Mojtaba on 4/11/2015.
 */
public class ServerTask implements Runnable {

    private Socket socket;
    private String response;
    private long initTime;

    public ServerTask(Socket socket, String response, long initTime) {
        this.socket = socket;
        this.response = response;
        this.initTime = initTime;

    }

    @Override
    public void run() {
//        long now = System.currentTimeMillis();
//        long l = now - initTime;
//        System.out.println(l);
//        if (l > DEAD_LOCK) {
//            writeResponse(socket, Server.ERROR_503);
//        } else {

            writeResponse(socket, response);
//        }
    }

    private void writeResponse(Socket socket, String response) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.write(response);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
