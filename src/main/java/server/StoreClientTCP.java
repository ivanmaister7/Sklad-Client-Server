package server;

import lombok.Getter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class StoreClientTCP {
    static final int MAX_THREADS = 10;
    @Getter
    private LinkedBlockingQueue<Packet> inQ = new LinkedBlockingQueue<>();
    @Getter
    private LinkedBlockingQueue<Packet> answerQ = new LinkedBlockingQueue<>();

    public StoreClientTCP() throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(null);
        new StoreClientTCPTread(addr,inQ,answerQ);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress addr = InetAddress.getByName(null);
        while (StoreClientTCPTread.threadCount() < MAX_THREADS) {
            if (StoreClientTCPTread.threadCount() < MAX_THREADS)
                //new StoreClientTCPTread(addr,inQ);
            Thread.currentThread().sleep(100);
        }
    }
}
class StoreClientTCPTread extends Thread {
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private static int counter = 0;
    private int id = counter++;
    private static int threadcount = 0;
    private LinkedBlockingQueue<Packet> inQ ;
    private LinkedBlockingQueue<Packet> answerQ ;

    public StoreClientTCPTread(InetAddress addr, LinkedBlockingQueue<Packet> inQ, LinkedBlockingQueue<Packet> answerQ) {
        this.inQ = inQ;
        this.answerQ = answerQ;
        System.out.println("Запустимо клієнт з номером " + id);
        threadcount++;
        try {
            socket = new Socket(addr, StoreServerTCP.PORT);
        }
        catch (IOException e) {
            System.err.println("Не вдалося з'єднатися з сервером");
            // Якщо не вдалося створити сокер нічого
            // не потрібно чистити
        }
        try {
            out = new DataOutputStream(socket.getOutputStream());
            //takes input from socket
            in = new DataInputStream(socket.getInputStream());
            start();
        }
        catch (IOException e) {
            // Сокет має бути закритий при будь якій помилці
            // крім помилки конструктора сокета
            try {
                socket.close();
            }
            catch (IOException e2) {
                System.err.println("Сокет не закрито");
            }
        }
        // Якщо все відбудеться нормально сокет буде закрито
        // в методі run() потоку.
    }

    public static int threadCount() {
        return threadcount;
    }

    public void run() {
        try {
            while (true) {
                Packet p = inQ.poll();
                if(p == null) continue;
                byte [] arr = p.toByte();

                while (true) {
                    out.write(arr);

                    //printing request to console
                    System.out.println("Sent to server : " + p);

                    // Receiving reply from server
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte buffer[] = new byte[1024];
                    int len = in.read(buffer);
                    if(len < 0) continue;
                    baos.write(buffer, 0 , len);

                    byte result[] = baos.toByteArray();

                    Packet packet = new Packet(result);

                    if(packet.isValid() && !packet.getBMsg().getMessage().equals("INVALID")){
                        answerQ.put(packet);
                        System.out.println("Received from server : " + packet);
                        break;
                    }
                }
            }
            //out.write("END".getBytes());
//            out.close();
//            in.close();
//            socket.close();
        }
        catch (IOException e) {
            System.err.println("IO Exception");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Завжди закриває:
            try {
                socket.close();
            }
            catch (IOException e) {
                System.err.println("Socket not closed");
            }
            //threadcount--; // Завершуємо цей потік
        }
    }

}
