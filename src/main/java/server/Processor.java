package server;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Processor {
    private final static int TREADS = 12;
    private LinkedBlockingQueue<Message> inQ;
    private LinkedBlockingQueue<Message> outQ = new LinkedBlockingQueue<Message>();
    private Thread[] tr = new Thread[TREADS];
    private ProcessorTread runnable;
    private Database database = new Database();

    public void stop() {
        runnable.stop();
    }

    public Processor(LinkedBlockingQueue<Message> in) {
        this.inQ = in;
        database.initialization("HelloDB");
        runnable = new ProcessorTread(inQ, outQ, database);
    }

    public void process() {
        runnable.start();
        for (int i = 0; i < TREADS; i++) {
            tr[i] = new Thread(runnable);
            tr[i].start();
        }
    }

    public LinkedBlockingQueue<Message> getQueue() {
        return outQ;
    }
}


class ProcessorTread implements Runnable {

    LinkedBlockingQueue<Message> inQ;
    LinkedBlockingQueue<Message> outQ;
    private Database database;
    private boolean running = false;

    ProcessorTread(LinkedBlockingQueue<Message> encrypted, LinkedBlockingQueue<Message> decrypted, Database database) {
        this.inQ = encrypted;
        this.outQ = decrypted;
        this.database = database;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    @SneakyThrows
    @Override
    public void run() {

        while (running) {

            Message check = new Message(0,0,"");
            try {
                check = inQ.poll(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (check == null)
                continue;

            processCommand(check);

        }
    }

    private void processCommand(Message check) throws InterruptedException {
        String c = check.getMessage();
        int command = check.getCType();
        if(command / 10 == 1){
            if (command % 10 == 1){
                String[] group = c.split(" ");
                database.putGroup(group[0],group[1]);
                System.out.println("Додали групу" + c);
                outQ.put(new Message(200, check.getBUserId(), "Додали групу" + c));
                Thread.sleep(50);
            }
            else if (command % 10 == 2){
                String[] group = c.split(" ");
                int id = database.getGroupId(group[0]);
                database.updateGroup(id,"name",group[1]);
                database.updateGroup(id,"info",group[2]);
                System.out.println("Редагувати групу" + c);
                outQ.put(new Message(200, check.getBUserId(), "Редагувати групу" + c));
                Thread.sleep(50);
            }
            else if (command % 10 == 3){
                database.deleteGroup(c);
                System.out.println("Видалити групу" + c);
                outQ.put(new Message(200, check.getBUserId(), "Видалити групу" + c));
                Thread.sleep(50);
            }
            else if (command % 10 == 5){
                database.getGroupId(c);
                outQ.put(new Message(200, check.getBUserId(), "" + database.getGroupId(c)));
                Thread.sleep(50);
            }
            else if (command % 10 == 6){
                outQ.put(new Message(200, check.getBUserId(), "" + database.containGroup(c)));
                Thread.sleep(50);
            }
            else{
                System.out.println("Список1");
                outQ.put(new Message(200, check.getBUserId(), database.getGroupList().toString()));
                Thread.sleep(50);
            }
        }
        else if(command / 10 == 2){
            if (command % 10 == 1){
                String[] group = c.split(" ");
                database.putTovar(group[0],
                        Integer.parseInt(group[1]),
                        Integer.parseInt(group[2]),
                        group[3],
                        Integer.parseInt(group[4]),
                        group[5]
                        );
                System.out.println("Додали товар");
                outQ.put(new Message(200, check.getBUserId(),"added"));
                Thread.sleep(50);
            }
            else if (command % 10 == 2){
                String[] group = c.split(" ");
                int id = database.getTovarId(group[0]);
                database.updateTovar(id,"name",group[1]);
                database.updateTovar(id,"info",group[2]);
                database.updateTovar(id,"price",Integer.parseInt(group[3]));
                database.updateTovar(id,"maker",group[4]);
                database.updateTovar(id,"count",Integer.parseInt(group[5]));

                System.out.println("Редагувати товар");
                outQ.put(new Message(200, check.getBUserId(),"edited"));
                Thread.sleep(50);
            }
            else if (command % 10 == 3){
                database.deleteTovar(c);
                System.out.println("Видалити товар");
                outQ.put(new Message(200, check.getBUserId(),"deleted"));
                Thread.sleep(50);
            }
            else if (command % 10 == 6){
                outQ.put(new Message(200, check.getBUserId(), "" + database.containTovar(c)));
                Thread.sleep(50);
            }
            else{
                List<String> criteria = new ArrayList<>();
                criteria.add(c);
                outQ.put(new Message(200, check.getBUserId(), database.getTovarListByCriteria(criteria).toString()));
                Thread.sleep(50);
            }
        }
    }
}