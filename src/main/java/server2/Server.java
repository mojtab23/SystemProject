//package server2;
//
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by Mojtaba on 3/31/2015.
// */
//public class Server {
//
//
//    //    private final List<CalculatorTask2> connections = new LinkedList<>();
//    private ThreadPoolExecutor pool;
//    private int port;
//    private volatile double charge = 0;
//
//    public Server(int port) {
//        pool = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10));
//        this.port = port;
//        Thread thread = new Thread(this::acceptRequest);
//        System.out.println("started...");
//        thread.start();
//
//    }
//
//    public double getCharge() {
//        return charge;
//    }
////    private ServerSocket serverSocket;
//
//    public void setCharge(double charge) {
//        synchronized (this.getClass()) {
//            this.charge = charge;
//        }
//    }
//
//    private void acceptRequest() {
//        try (ServerSocket serverSocket = new ServerSocket(port)) {
//            while (true) {
//                try {
//                    Socket connection = serverSocket.accept();
//                    System.out.println("new request.");
//                    if (pool.getActiveCount() >= 10) {
//                        System.out.println("reject.");
//                        DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
//                        outToClient.writeBytes(CalculatorTask2.ERROR_503);
//                        outToClient.flush();
//                        connection.close();
//                        continue;
//                    }
//                    Runnable task = new CalculatorTask(connection, this, System.currentTimeMillis());
//                    pool.execute(task);
//                } catch (IOException ignored) {
//                }
//
//            }
//        } catch (IOException ignored) {
//        }
//    }
//
//}
