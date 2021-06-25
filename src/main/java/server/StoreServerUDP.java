package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StoreServerUDP {
    public static void main(String[] args) throws IOException {
        new StoreServerUDPThread().start();
    }
}
class StoreServerUDPThread extends Thread {

    protected DatagramSocket socket;
    protected boolean moreQuotes = true;
    byte[] data ;
    private LinkedBlockingQueue<byte[]> inQ = new LinkedBlockingQueue<>();
    private Decryptor decryptor = new Decryptor(inQ);
    private Processor processor = new Processor(decryptor.getQueue());
    private Encryptor encryptor = new Encryptor(processor.getQueue());

    public StoreServerUDPThread() throws IOException {
        this("StoreServerUDPThread");
    }

    public StoreServerUDPThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
        decryptor.decrypt();
        processor.process();
        encryptor.encrypt();

    }

    public void run() {
        System.out.println("Сервер запущено.");
        while (moreQuotes) {
            try {

                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                data = packet.getData();
                inQ.put(data);

                buf = encryptor.getQueue().poll(1000, TimeUnit.MILLISECONDS);

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                if(buf != null){
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}

