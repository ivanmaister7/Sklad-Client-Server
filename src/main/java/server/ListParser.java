package server;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ListParser {
    @Getter
    private List<Group> groupList = new ArrayList<>();
    public ListParser(String s){
        String[] elem = s.substring(1,s.length()-1).split(", ");
        for (int i = 0; i < elem.length / 4; i++) {
            groupList.add(
                    new Group(
                            Integer.parseInt(elem[i*4].substring(9)),
                            elem[i*4+1].substring(5),
                            elem[i*4+2].substring(5),
                            Boolean.getBoolean(elem[i*4+3].substring(8,elem[i*4+3].length()-1))));
        }

    }
    public static void main(String[] args) {
        new ListParser("[Group(id=1, name=group111, info=first, deleted=false), Group(id=5, name=group5, info=five, deleted=false), Group(id=10, name=ds, info=ssdds, deleted=false), Group(id=12, name=r, info=r, deleted=false), Group(id=13, name=new, info=enew, deleted=false)]");
    }
}
//[Group(id=1, name=group111, info=first, deleted=false), Group(id=5, name=group5, info=five, deleted=false), Group(id=10, name=ds, info=ssdds, deleted=false), Group(id=12, name=r, info=r, deleted=false), Group(id=13, name=new, info=enew, deleted=false)]
//[Group(id=1, name=group1, info=first, deleted=false), Group(id=2, name=group2, info=sec, deleted=false)]