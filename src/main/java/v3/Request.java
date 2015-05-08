package v3;

import java.net.Socket;

/**
 * Created by Mojtaba on 4/11/2015.
 */
public class Request {
    private Socket socket;
    private String request;
    private long initTime;


    public Request(Socket socket, String request, long initTime) {
        this.socket = socket;
        this.request = request;
        this.initTime = initTime;
    }


    public Socket getSocket() {
        return socket;
    }

    public String getRequest() {
        return request;
    }

    public long getInitTime() {
        return initTime;
    }
}
