package server;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Encryptor
{
    private final static int TREADS = 12;
    private LinkedBlockingQueue<Message> inQ;
    private LinkedBlockingQueue<byte[]> outQ = new LinkedBlockingQueue<byte[]>();
    private Thread[] tr = new Thread[TREADS];
    private EncryptorTread runnable;

    public void stop(){
        runnable.stop();
    }

    Encryptor(LinkedBlockingQueue<Message> encrypted)
    {
        this.inQ = encrypted;
        runnable = new EncryptorTread(inQ, outQ);
    }

    public void encrypt()
    { //init
        runnable.start();
        for (int i = 0; i < TREADS; i++)
        {
            tr[i] = new Thread(runnable);
            tr[i].start();
        }

    }

    public LinkedBlockingQueue<byte[]> getQueue() {
        return outQ;
    }
}


class EncryptorTread implements Runnable
{
    private LinkedBlockingQueue<Message> inQ;
    private LinkedBlockingQueue<byte[]> outQ;
    private boolean running = false;

    EncryptorTread(LinkedBlockingQueue<Message> decrypted, LinkedBlockingQueue<byte[]> encrypted)
    {
        this.inQ = decrypted;
        this.outQ = encrypted;
    }

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
            Message check = new Message(0,0, "test");
            try{
                check = inQ.poll(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            if (check == null) continue;
            byte[] message = new Packet((byte) 1,1L,check).toByte();

            try
            {
                outQ.put(message);
                Thread.sleep(50);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
