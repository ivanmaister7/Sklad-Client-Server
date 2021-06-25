package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StoreServerTCP {
    static final int PORT = 8080;
    private static LinkedBlockingQueue<byte[]> inQ = new LinkedBlockingQueue<>();
    private static Decryptor decryptor = new Decryptor(inQ);
    private static Processor processor = new Processor(decryptor.getQueue());
    private static Encryptor encryptor = new Encryptor(processor.getQueue());

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        decryptor.decrypt();
        processor.process();
        encryptor.encrypt();
        System.out.println("Сервер запущено.");

        try {
            while (true) {
                Socket socket = s.accept();
                try {
                    new StoreServerTCPTread(socket,inQ,decryptor, processor, encryptor);
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            s.close();
        }
    }
}
class StoreServerTCPTread extends Thread {
    private LinkedBlockingQueue<byte[]> inQ ;
    private Decryptor decryptor ;
    private Processor processor ;
    private Encryptor encryptor ;

    public StoreServerTCPTread(Socket s, LinkedBlockingQueue<byte[]> inQ,Decryptor decryptor,Processor processor, Encryptor encryptor) throws IOException {
        socket = s;
        this.inQ = inQ;
        this.decryptor = decryptor;
        this.processor = processor;
        this.encryptor = encryptor;
        // takes input from the client socket
        in = new DataInputStream(socket.getInputStream());
        //writes on client socket
        out = new DataOutputStream(socket.getOutputStream());
        //Якщо будь-який з вище перерахованих викликів приведе до виникнення
        //виключення, тоді викликаючий буде відповідальний за закриття сокета
        //інакше потік закриє його
        start();//викликаємо run();
    }

    public void run() {
        try {
            while (true) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024];
                byte[] buf = new byte[256];
                int len = in.read(buffer);
                if(len < 0) continue;
                baos.write(buffer, 0 , len);

                byte result[] = baos.toByteArray();

                Packet p = new Packet(result);
                System.out.println("Recieved from client : " + p);

                if(p.isValid()){
                    inQ.put(result);
                    buf = encryptor.getQueue().poll(1000, TimeUnit.MILLISECONDS);
                }
                else{
                    buf = new Packet((byte)1,1L,new Message(-1,0,"INVALID")).toByte();
                }

                if(buf != null){
                    out.write(buf);
                }

            }
            //System.out.println("Закриваємо сокет на сервері");
        } catch (IOException e) {
            System.err.println("IO Exception");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Сокет не закрито ...");
            }
        }
    }

    private Socket socket;
    private InputStream  in;
    private OutputStream  out;
}
