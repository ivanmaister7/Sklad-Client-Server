package server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Tovar {
    private int id;
    private String name;
    private String info;
    private String maker;
    private int price;
    private int count;
    private int groupId;
    private boolean deleted;
    public boolean getDeleted(){ return deleted; }
}
