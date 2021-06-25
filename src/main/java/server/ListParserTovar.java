package server;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ListParserTovar {
    @Getter
    private List<Tovar> groupList = new ArrayList<>();
    public ListParserTovar(String s){
        String[] elem = s.substring(1,s.length()-1).split(", ");
        for (int i = 0; i < elem.length / 8; i++) {
            groupList.add(
                    new Tovar(
                            Integer.parseInt(elem[i*8].substring(9)),
                            elem[i*8+1].substring(5),
                            elem[i*8+2].substring(5),
                            elem[i*8+3].substring(6),
                            Integer.parseInt(elem[i*8+4].substring(6)),
                            Integer.parseInt(elem[i*8+5].substring(6)),
                            Integer.parseInt(elem[i*8+6].substring(8)),
                            Boolean.getBoolean(elem[i*8+7].substring(8,elem[i*8+7].length()-1))));
        }

    }
    public static void main(String[] args) {
        new ListParserTovar("[Group(id=1, name=group1, info=first, deleted=false), Group(id=2, name=group2, info=sec, deleted=false)]");
    }
}
//[Group(id=1, name=group1, info=first, deleted=false), Group(id=2, name=group2, info=sec, deleted=false)]