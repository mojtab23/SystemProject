package v4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Mojtaba on 4/9/2015.
 */
public class CalculatorTask2 implements Runnable {
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
    public static final long DEAD_LOCK = 3000;//3 Seconds.
    private final long initTime;
    private Socket connection;
    private Server2 server;


    public CalculatorTask2(Socket connection, Server2 server, long initTime) {
        this.connection = connection;
        this.server = server;
        this.initTime = initTime;
    }

    @Override
    public void run() {
        BufferedReader inFromClient;


        try {
            String clientSentence;
            String result;


            DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
            if ((System.currentTimeMillis() - initTime) > DEAD_LOCK) {
                outToClient.writeBytes(ERROR_503);
            } else {
                Thread.sleep(1000);//todo for debug
                inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                clientSentence = inFromClient.readLine();
                System.out.println("Received: " + clientSentence);
                String number = calculate(clientSentence.split(" ")[1].substring(1));

                result = String.format("HTTP/1.x 200 OK\n" +
                        "connection: close\n" +
                        "Content-Type: text/html; charset=UTF-8\n" +
                        "\n\n" +
                        "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "  <title>Page Title</title>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "  <h1>Calculator Server</h1>\n" +
                        "  <p> %s </p>\n" +
                        "<footer>&copy; by Mojtaba Zarezadeh </footer>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>", number);

                outToClient.writeBytes(result);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Socket getConnection() {
        return connection;
    }

    private String calculate(String s) {
        double number;
        double result;
        if (s.length() == 1 && s.substring(0, 1).equals("=")) {
            try {
                result = server.getCharge();
                return String.format("The Charge is : %f", result);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        } else if (s.substring(0, 2).equals("+=")) {
            try {
                number = getNumber(s);
                result = server.getCharge() + number;
                server.setCharge(result);
                return String.format("The new charge is : %f", result);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        } else if (s.substring(0, 2).equals("-=")) {
            try {
                number = getNumber(s);
                result = server.getCharge() - number;
                server.setCharge(result);
                return String.format("The new charge is : %f", result);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }


        } else if (s.substring(0, 2).equals("/=")) {

            try {
                number = getNumber(s);
                result = server.getCharge() / number;
                server.setCharge(result);
                return String.format("The new charge is : %f", result);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        } else if (s.substring(0, 2).equals("*=")) {

            try {
                number = getNumber(s);
                result = server.getCharge() * number;
                server.setCharge(result);
                return String.format("The new charge is : %f", result);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }

        }

        return "Error";
    }

    private double getNumber(String s) throws Exception {
        return Double.parseDouble(s.substring(2));
    }

}
