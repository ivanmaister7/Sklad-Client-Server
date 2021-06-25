package server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Group {
    int id;
    String name;
    String info;
    boolean deleted;
    public boolean getDeleted(){ return deleted; }
}
