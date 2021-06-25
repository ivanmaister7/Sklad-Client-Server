package server;

import java.net.DatagramSocket;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Receiver implements IReceiver
{
    private final static int TREADS = 12;
    private LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private Thread[] tr = new Thread[TREADS];
    private ReceiverTread runnable = new ReceiverTread(queue);
    public void stop(){
        runnable.stop();
    }

    @Override
    public void receive(byte[] data) {
        runnable.start();
        for (int i = 0; i < TREADS; i++)
        {
            tr[i] = new Thread(runnable);
            tr[i].start();
        }
    }
    public void receiveMessage(byte[] data) {
        runnable.setData(data);
    }
    public LinkedBlockingQueue<byte[]> getQueue() {
        return queue;
    }
}

class ReceiverTread implements Runnable
{
    protected DatagramSocket socket = null;
    private LinkedBlockingQueue<byte[]> bc;
    private byte[] data;
    private Boolean added = false;
    private boolean running = false;

    ReceiverTread(LinkedBlockingQueue<byte[]> bc)
    {
        this.bc = bc;
    }

    public void setData(byte[] data) { this.data = data; }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run()
    {
        while (running)
        {

        }
    }

    public static String generateMessage(int k)
    {
        String message = "Message";
        message += " + " + k;
        return message;
    }
}
