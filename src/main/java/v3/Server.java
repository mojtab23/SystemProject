package v3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mojtaba on 4/11/2015.
 */
public class Server {
    public static final long DEAD_LOCK = 3000;//3 Seconds.
    public static final String ERROR_503 =
            "HTTP/1.x 503 Service Unavailable\n" +
                    "Connection: close\n" +
                    "Content-Type: text/html; charset=UTF-8\n" +
                    "\n\n" +
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <title>Calculator Server</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "  <h1>Calculator Server</h1>\n" +
                    "  <p>Server is busy.</p>\n" +
                    "<footer>&copy; by Mojtaba Zarezadeh </footer>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";
    public static final String ERROR_404 =
            "HTTP/1.x 404 Not Found\n" +
                    "Connection: close\n" +
                    "Content-Type: text/html; charset=UTF-8\n" +
                    "\n\n" +
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <title>Calculator Server</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "  <h1>Calculator Server</h1>\n" +
                    "  <p>404 NOT FOUND ;D </p>\n" +
                    "<footer>&copy; by Mojtaba Zarezadeh </footer>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";
    private final static int MAX_TASK = 10;
    private final static int capacity = 10;
    private final BlockingQueue<Request> queue;
    private ThreadPoolExecutor pool;
    private int port;
    private double charge = 0;


    public Server(int port) {
        this.port = port;
        System.out.println("started...");
        queue = new ArrayBlockingQueue<>(capacity);
        calculateRequest();
        acceptRequest();
        pool = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
    }

    private void acceptRequest() {

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    try {
                        Socket connection = serverSocket.accept();
                        long time = System.currentTimeMillis();
                        String header = readHeader(connection.getInputStream());
                        requestType(header, connection, time);
                    } catch (IOException ignored) {
                    }

                }
            } catch (IOException ignored) {
            }

        }).start();


    }

    private void requestType(String header, Socket connection, long time) {
        String request = header.split(" ")[1].substring(1);
        if (request.length() == 1 && request.substring(0, 1).equals("="))
            execute(connection, getResults(getCharge()), time);
        else if (queue.size() != capacity) {
            queue.add(new Request(connection, request, time));
        } else
            execute(connection, ERROR_503, time);
    }

    private void execute(Socket connection, String s, long time) {
        if (pool.getActiveCount() < MAX_TASK) pool.execute(new ServerTask(connection, s, time));
        else System.out.println("Ignored.");
    }

    private String readHeader(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.readLine();

    }

    private void calculateRequest() {

        new Thread(() -> {
            while (true) {
                try {
                    Request task = null;
                    task = queue.take();
                    long now = System.currentTimeMillis();
                    long l = now - task.getInitTime();
                    if (l < DEAD_LOCK) {
                        String response = calculate(task.getRequest());
                        Thread.sleep(1000);
                        execute(task.getSocket(), response, task.getInitTime());
                    } else {
                        System.out.println("deadlock");
                        execute(task.getSocket(), ERROR_503, task.getInitTime());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        ).start();

    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    private String getResults(double r) {
        System.out.println("ok");
        return String.format("HTTP/1.x 200 OK\n" +
                "Connection: close\n" +
                "Content-Type: text/html; charset=UTF-8\n" +
                "\n\n" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>Calculator Server</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "  <h1>Calculator Server</h1>\n" +
                "  <p> The charge is : %f </p>\n" +
                "<footer>&copy; by Mojtaba Zarezadeh </footer>\n" +
                "</body>\n" +
                "\n" +
                "</html>", r);

    }

    private String calculate(String s) {
        double number;
        double result;
        if (s.substring(0, 2).equals("+=")) {
            try {
                number = getNumber(s);
                result = getCharge() + number;
                setCharge(result);
                return getResults(result);
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR_404;
            }

        } else if (s.substring(0, 2).equals("-=")) {
            try {
                number = getNumber(s);
                result = getCharge() - number;
                setCharge(result);
                return getResults(result);
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR_404;
            }


        } else if (s.substring(0, 2).equals("/=")) {

            try {
                number = getNumber(s);
                result = getCharge() / number;
                setCharge(result);
                return getResults(result);
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR_404;
            }

        } else if (s.substring(0, 2).equals("*=")) {

            try {
                number = getNumber(s);
                result = getCharge() * number;
                setCharge(result);
                return getResults(result);
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR_404;
            }

        }

        return ERROR_404;
    }

    private double getNumber(String s) throws Exception {
        return Double.parseDouble(s.substring(2));
    }
}

