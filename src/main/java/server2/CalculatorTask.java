//package server2;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.Socket;
//
///**
// * Created by Mojtaba on 3/31/2015.
// */
//public class CalculatorTask implements Runnable {
//    public static final String ERROR_503 =
//            "HTTP/1.x 503 Service Unavailable\n" +
//                    "Connection: close\n" +
//                    "Content-Type: text/html; charset=UTF-8\n" +
//                    "\n\n" +
//                    "<!DOCTYPE html>\n" +
//                    "<html>\n" +
//                    "<head>\n" +
//                    "  <title>Calculator Server</title>\n" +
//                    "</head>\n" +
//                    "\n" +
//                    "<body>\n" +
//                    "  <h1>Calculator Server</h1>\n" +
//                    "  <p>Server is busy.</p>\n" +
//                    "<footer>&copy; by Mojtaba Zarezadeh </footer>\n" +
//                    "</body>\n" +
//                    "\n" +
//                    "</html>";
//    public static final String ERROR_404 =
//            "HTTP/1.x 404 Not Found\n" +
//                    "Connection: close\n" +
//                    "Content-Type: text/html; charset=UTF-8\n" +
//                    "\n\n" +
//                    "<!DOCTYPE html>\n" +
//                    "<html>\n" +
//                    "<head>\n" +
//                    "  <title>Calculator Server</title>\n" +
//                    "</head>\n" +
//                    "\n" +
//                    "<body>\n" +
//                    "  <h1>Calculator Server</h1>\n" +
//                    "  <p>Server is busy.</p>\n" +
//                    "<footer>&copy; by Mojtaba Zarezadeh </footer>\n" +
//                    "</body>\n" +
//                    "\n" +
//                    "</html>";
//    public static final long DEAD_LOCK = 3000;//3 Seconds.
//    private Socket connection;
//    private Server server;
//    private long initTime;
//
//    public CalculatorTask(Socket connection, Server server, long initTime) {
//        this.server = server;
//        this.connection = connection;
//        this.initTime = initTime;
//    }
//
//
//    @Override
//    public void run() {
//
//        BufferedReader inFromClient;
//
//
//        try {
//            String clientSentence;
//            String result;
//
//
//            DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
//            long now = System.currentTimeMillis();
//            if ((now - initTime) > DEAD_LOCK) {
//                outToClient.writeBytes(ERROR_503);
//            } else {
//                inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                clientSentence = inFromClient.readLine();
//                result = calculate(clientSentence.split(" ")[1].substring(1));
//                System.out.println("Received: " + clientSentence);
//
//                Thread.sleep(1000);//todo for debug
//                outToClient.writeBytes(result);
//                outToClient.flush();
//            }
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                connection.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private String calculate(String s) {
//        double number;
//        double result;
//        if (s.length() == 1 && s.substring(0, 1).equals("=")) {
//            try {
//                result = server.getCharge();
//                return getResults(result);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ERROR_404;
//            }
//
//        } else if (s.substring(0, 2).equals("+=")) {
//            try {
//                number = getNumber(s);
//                result = server.getCharge() + number;
//                server.setCharge(result);
//                return getResults(result);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ERROR_404;
//            }
//
//        } else if (s.substring(0, 2).equals("-=")) {
//            try {
//                number = getNumber(s);
//                result = server.getCharge() - number;
//                server.setCharge(result);
//                return getResults(result);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ERROR_404;
//            }
//
//
//        } else if (s.substring(0, 2).equals("/=")) {
//
//            try {
//                number = getNumber(s);
//                result = server.getCharge() / number;
//                server.setCharge(result);
//                return getResults(result);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ERROR_404;
//            }
//
//        } else if (s.substring(0, 2).equals("*=")) {
//
//            try {
//                number = getNumber(s);
//                result = server.getCharge() * number;
//                server.setCharge(result);
//                return getResults(result);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ERROR_404;
//            }
//
//        }
//
//        return ERROR_404;
//    }
//
//    private double getNumber(String s) throws Exception {
//        return Double.parseDouble(s.substring(2));
//    }
//
//    private String getResults(double r) {
//        return String.format("HTTP/1.x 200 OK\n" +
//                "Connection: close\n" +
//                "Content-Type: text/html; charset=UTF-8\n" +
//                "\n\n" +
//                "<!DOCTYPE html>\n" +
//                "<html>\n" +
//                "<head>\n" +
//                "  <title>Calculator Server</title>\n" +
//                "</head>\n" +
//                "\n" +
//                "<body>\n" +
//                "  <h1>Calculator Server</h1>\n" +
//                "  <p> The charge is : %f </p>\n" +
//                "<footer>&copy; by Mojtaba Zarezadeh </footer>\n" +
//                "</body>\n" +
//                "\n" +
//                "</html>", r);
//
//    }
//}
