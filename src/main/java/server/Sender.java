package server;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Sender
{
    private final static int TREADS = 12;
    private LinkedBlockingQueue<byte[]> inQ;
    private LinkedBlockingQueue<Message> outQ = new LinkedBlockingQueue<Message>();
    private Thread[] tr = new Thread[TREADS];
    private SenderTread runnable;

    public void stop(){
        runnable.stop();
    }

    Sender(LinkedBlockingQueue<byte[]> encrypted)
    {
        this.inQ = encrypted;
        runnable = new SenderTread(inQ,outQ);
    }

    public LinkedBlockingQueue<Message> getResults(){
        return outQ;
    }

    public void sendMessage()
    {
        runnable.start();
        for (int i = 0; i < TREADS; i++)
        {
            tr[i] = new Thread(runnable);
            tr[i].start();
        }
    }
}

class SenderTread  implements Runnable
{
    private LinkedBlockingQueue<byte[]> inQ;
    private LinkedBlockingQueue<Message> outQ;
    private boolean running = false;

    SenderTread(LinkedBlockingQueue<byte[]> in, LinkedBlockingQueue<Message> out )
    {
        this.inQ = in;
        this.outQ=out;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    @SneakyThrows
    @Override
    public void run()
    {
        while (running)
        {
            byte[] check = new byte[0];
            try {
                check = inQ.poll(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (check == null) continue;

            Packet p = new Packet(check);
            int userId = p.getBMsg().getBUserId();
            System.out.println(p);
            System.out.println("Send to " + userId);

        }
    }
}
