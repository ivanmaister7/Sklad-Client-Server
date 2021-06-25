package server;

import lombok.SneakyThrows;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Decryptor
{
    private final static int TREADS = 12;
    private LinkedBlockingQueue<byte[]> inQ;
    private LinkedBlockingQueue<Message> outQ = new LinkedBlockingQueue<Message>();
    private Thread[] tr = new Thread[TREADS];
    private DecryptorTread runnable;

    public void stop(){
        runnable.stop();
    }

    public LinkedBlockingQueue<Message> getDecrypted(){
        return outQ;
    }
    public LinkedBlockingQueue<byte[]> getEncrypted(){
        return inQ;
    }

    Decryptor(LinkedBlockingQueue<byte[]> encrypted)
    {
        this.inQ = encrypted;
        runnable = new DecryptorTread(inQ, outQ);
    }

    public void decrypt()
    {
        runnable.start();
        for (int i = 0; i < TREADS; i++)
        {
            tr[i] = new Thread(runnable);
            tr[i].start();
        }
    }
    public LinkedBlockingQueue<Message> getQueue() {
        return outQ;
    }
}


class DecryptorTread implements Runnable
{
    private LinkedBlockingQueue<byte[]> inQ;
    private LinkedBlockingQueue<Message> outQ;
    private boolean running = false;


    DecryptorTread(LinkedBlockingQueue<byte[]> encrypted, LinkedBlockingQueue<Message> decrypted)
    {
        this.inQ = encrypted;
        this.outQ = decrypted;
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
            Packet packet = new Packet(check);
            Message message = packet.getBMsg();
            System.out.println("Decryptor - " + Thread.currentThread().getName() + "\n" + message);
            try {
                outQ.put(message);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
