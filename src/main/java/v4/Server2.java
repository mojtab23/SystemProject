package v4;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mojtaba on 4/9/2015.
 */
public class Server2 {

    public static final int MAX_TASK = 10;
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
    private final ServerThreadPoolExecutor executor;
    private int port;
    private volatile double charge = 0;

    public Server2(int port) {
        this.port = port;

        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(2);
        executor = new ServerThreadPoolExecutor(10, 10, 30, TimeUnit.SECONDS, blockingQueue);

        RejectedExecutionHandler rejectedExecutionHandler = (r, executor1) -> {
            try {
                System.out.println("rejected.");
                PrintWriter writer = new PrintWriter(((CalculatorTask2) r).getConnection().getOutputStream());
                writer.print(ERROR_503);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    ((CalculatorTask2) r).getConnection().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        };
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        acceptRequest();

    }

    public double getCharge() {
        return charge;
    }
//    private ServerSocket serverSocket;

    public void setCharge(double charge) {
        synchronized (this.getClass()) {
            this.charge = charge;
        }
    }

    private void acceptRequest() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) try {
                Socket connection = serverSocket.accept();
                int activeCount = executor.getActiveCount();
                System.out.println(activeCount);
                if (activeCount >= MAX_TASK) {
                    PrintWriter writer = new PrintWriter(connection.getOutputStream());
                    writer.print(ERROR_503);
                    continue;
                }
                executor.execute(new CalculatorTask2(connection, this, System.currentTimeMillis()));
            } catch (IOException ignored) {
            }
        } catch (IOException ignored) {
        }
    }

}
