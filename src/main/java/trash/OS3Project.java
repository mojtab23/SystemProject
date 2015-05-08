package trash;

import ir.estakhri.webserver.WebServer;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * @author Mr E
 */
public class OS3Project extends Thread {

    static int charge = 0;
    static boolean bool = true;
    static ArrayBlockingQueue<obj> bq;
    //    static ThreadFactory threadFactory;
    static ThreadPoolExecutor executorPool;

    public static void main(String[] args) throws IOException, Exception {
        Threadrun r = new Threadrun();
        WebServer web = new WebServer(8080);
//        Date mt=new Date();
//        mt.
//        Calendar time = Calendar.getInstance();
//        Calendar.
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        bq = new ArrayBlockingQueue<obj>(10);
//        threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(10, 10, 10, TimeUnit.DAYS,
                new ArrayBlockingQueue<Runnable>(10));
        r.start();
        System.out.println("started...");
        while (true) {
            Socket sockfd = web.acceptRequest();
            String str = WebServer.getRequest(sockfd);
            if (str.contains("get")) {
                executorPool.execute(new WorkerThread(str, sockfd));
                continue;
            }
//            String st[] = sdf.format(time.getTime()).toString().split(":");
            Date d = new Date();
//            todo zamana ba long begir
//            System.currentTimeMillis();

            int it = d.getHours() * 3600 + d.getMinutes() * 60 + d.getSeconds();

            bq.add(new obj(str, sockfd, it));
            System.out.println("req");
        }
    }

    public static class obj {

        private String str;
        private Socket sockfd;
        private int time;

        public obj(String str, Socket sockfd, int time) {
            this.str = str;
            this.sockfd = sockfd;
            this.time = time;
        }

        public int gettime() {
            return time;
        }

        public String getstr() {
            return str;
        }

        public Socket getsockfd() {
            return sockfd;
        }
    }

    public static class WorkerThread implements Runnable {

        private String command;
        private Socket sockfd;

        public WorkerThread(String s, Socket sokfd) {
            this.command = s;
            this.sockfd = sokfd;
        }

        @Override
        public void run() {
            command = command.substring(1);
            if (command.startsWith("add")) {
                charge += Integer.parseInt(command.substring(4));
            } else if (command.startsWith("sub")) {
                charge -= Integer.parseInt(command.substring(4));
            } else if (command.startsWith("div")) {
                charge /= Integer.parseInt(command.substring(4));
            } else if (command.startsWith("mult")) {
                charge *= Integer.parseInt(command.substring(5));
            } else if (command.startsWith("get")) {
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }
            try {
                WebServer.sendAnswer(sockfd, charge + "");
                bool = true;
                WebServer.closeConnection(sockfd);
            } catch (Exception ex) {
            }
        }

        @Override
        public String toString() {
            return this.command;
        }
    }

    public static class Threadrun extends Thread {

        @Override
        public void run() {
            while (true) {
                System.out.println("" + bool + bq.size());
//                if (bool && bq.size() != 0) {
//                if (bool) {

                bool = false;
                System.out.println(bq.size());
                obj ooo = null;
                try {
                    ooo = bq.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                    if(ooo==null){
//                        System.out.println("not obj");
//                        bool=true;
//                        continue;
//                    }
                System.out.println(bq.size());
//                    Calendar time = Calendar.getInstance();
//                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//                    String st[] = sdf.format(time.getTime()).toString().split(":");
                Date d = new Date();
                int it = d.getHours() * 3600 + d.getMinutes() * 60 + d.getSeconds();
                if (it - ooo.gettime() > 10) {
                    bool = true;
                    WebServer.closeConnection(ooo.getsockfd());
                    continue;
                }
                executorPool.execute(new WorkerThread(ooo.getstr(), ooo.getsockfd()));
//                }
            }
        }
    }
}
