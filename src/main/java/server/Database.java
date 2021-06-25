package server;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection con;

    public Database() {
    }

    public void initialization(String name) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.con = DriverManager.getConnection("jdbc:sqlite:" + name);
            PreparedStatement st = this.con.prepareStatement("create table if not exists 'test' " +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "'name' text, 'info' text," +
                    "'maker' text , 'price' integer," +
                    "'count' integer, " +
                    "'group_id' integer,'deleted' integer );");
            int var3 = st.executeUpdate();
            st = this.con.prepareStatement("create table if not exists 'gr' " +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "'name' text, 'info' text," +
                    "'deleted' integer );");
            var3 = st.executeUpdate();
//            st = this.con.prepareStatement("drop table gr");
//            var3 = st.executeUpdate();
//
//            st = this.con.prepareStatement("drop table test");
//            var3 = st.executeUpdate();
        } catch (ClassNotFoundException var4) {
            System.out.println("Не знайшли драйвер JDBC");
            var4.printStackTrace();
            System.exit(0);
        } catch (SQLException var5) {
            System.out.println("Не вірний SQL запит");
            var5.printStackTrace();
        }

    }
    public void putTovar(String name, int price, int count,String info, int groupId, String maker){
        if(containTovar(name)){
            return;
        }
        try {
            PreparedStatement statement = this.con.prepareStatement("INSERT INTO test(name, info, maker, price, count, group_id, deleted) " +
                    "VALUES (?,?,?,?,?,?,?)");
            statement.setString(1, name);
            statement.setString(2, info);
            statement.setString(3, maker);
            statement.setInt(4, price);
            statement.setInt(5, count);
            statement.setInt(6, groupId);
            statement.setInt(7, 0);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вставку");
            var4.printStackTrace();
        }
    }
    public void putTovar(String name, int price, int count, int groupId, String maker){
        putTovar(name,price,count,"-",groupId,maker);
    }
    public void putTovar(String name, int price, int count,String info, int groupId){
        putTovar(name,price,count,info,groupId,"-");
    }
    public void putTovar(String name, int price, int count, int groupId){
        putTovar(name,price,count,"-",groupId,"-");
    }
    public void updateTovar(int id,String colName, String value){
        try {
            PreparedStatement statement = this.con.prepareStatement("UPDATE test SET '"+ colName +"' = ? WHERE id = ?");
            statement.setString(1, value);
            statement.setInt(2, id);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вставку");
            var4.printStackTrace();
        }
    }
    public void updateTovar(int id,String colName,  int value){
        try {
            PreparedStatement statement = this.con.prepareStatement("UPDATE test SET '"+ colName +"' = ? WHERE id = ?");
            statement.setInt(1, value);
            statement.setInt(2, id);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вставку");
            var4.printStackTrace();
        }
    }
    public void deleteTovar(String name){
        updateTovar(getTovarId(name),"deleted",1);
    }
    public int getTovarId(String name){
        int id = -1;
        try {
            PreparedStatement st = this.con.prepareStatement("SELECT id FROM test WHERE name = ? AND deleted = 0");
            st.setString(1,name);
            ResultSet res = st.executeQuery();
            while(res.next()) {
                id = res.getInt("id");
            }
            res.close();
            st.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            var4.printStackTrace();
        }
        return id;
    }
    public List<Tovar> getTovarList(){
        return getTovarListByCriteria(new ArrayList<>());
    }
    public List<Tovar> getTovarListByCriteria(List<String> criterias){
        ResultSet res = null;
        List<Tovar> list = new ArrayList<>();
        String sql = "SELECT * FROM test WHERE deleted = 0 ";
        for(String criteria : criterias) {
            sql += "AND " + criteria + " ";
        }
        try {
            Statement st = this.con.createStatement();
            res = st.executeQuery(sql);
            while(res.next()) {
                list.add(new Tovar(res.getInt("id"),res.getString("name")
                        ,res.getString("info"),res.getString("maker")
                        ,res.getInt("price"),res.getInt("count")
                        ,res.getInt("group_id"),(res.getInt("deleted") == 1 ? true : false)));
            }
            res.close();
            st.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            var4.printStackTrace();
        }
        return list;
    }
    public void showTovar(List<Tovar> list) throws SQLException {
        for(Tovar t : list){
            System.out.println(t.getId() + " " +
                    t.getName() + " " +
                    t.getInfo() + " " +
                    t.getMaker() + " " +
                    t.getPrice() + " " +
                    t.getCount() + " " +
                    t.getGroupId() + " " +
                    t.getDeleted());
        }
    }
    public boolean containTovar(String name){
        return getTovarId(name) != -1;
    }


    public void putGroup(String name,String info){
        if(containGroup(name)){
            return;
        }
        try {
            PreparedStatement statement = this.con.prepareStatement("INSERT INTO gr(name, info, deleted) " +
                    "VALUES (?,?,?)");
            statement.setString(1, name);
            statement.setString(2, info);
            statement.setInt(3, 0);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вставку");
            var4.printStackTrace();
        }
    }
    public void putGroup(String name){
        putGroup(name, "-");
    }
    public void updateGroup(int id,String colName, String value){
        try {
            PreparedStatement statement = this.con.prepareStatement("UPDATE gr SET '"+ colName +"' = ? WHERE id = ?");
            statement.setString(1, value);
            statement.setInt(2, id);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вставку");
            var4.printStackTrace();
        }
    }
    private void updateGroup(int id,String colName, int value){
        try {
            PreparedStatement statement = this.con.prepareStatement("UPDATE gr SET '"+ colName +"' = ? WHERE id = ?");
            statement.setInt(1, value);
            statement.setInt(2, id);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вставку");
            var4.printStackTrace();
        }
    }
    public int getGroupId(String name){
        int id = -1;
        try {
            PreparedStatement st = this.con.prepareStatement("SELECT id FROM gr WHERE name = ? AND deleted = 0");
            st.setString(1,name);
            ResultSet res = st.executeQuery();
            while(res.next()) {
                id = res.getInt("id");
            }
            res.close();
            st.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            var4.printStackTrace();
        }
        return id;
    }
    public List<Group> getGroupList(){
        ResultSet res = null;
        List<Group> list = new ArrayList<>();
        String sql = "SELECT * FROM gr WHERE deleted = 0 ";
        try {
            Statement st = this.con.createStatement();
            res = st.executeQuery(sql);
            while(res.next()) {
                list.add(new Group(res.getInt("id"),res.getString("name")
                        ,res.getString("info"),(res.getInt("deleted") == 1 ? true : false)));
            }
            res.close();
            st.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            var4.printStackTrace();
        }
        return list;
    }
    public void deleteGroup(String name){
        try {
            PreparedStatement statement = this.con.prepareStatement("UPDATE test SET deleted = 1 WHERE group_id = ?");
            statement.setInt(1,getGroupId(name));
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException var4) {
            System.out.println("Не вірний SQL запит на вставку");
            var4.printStackTrace();
        }
        updateGroup(getGroupId(name),"deleted",1);
    }
    public void showGroup(List<Group> list) throws SQLException{
        for(Group g : list){
            System.out.println(g.getId() + " " +
                    g.getName() + " " +
                    g.getInfo() + " " +
                    g.getDeleted());
        }
    }
    public boolean containGroup(String name){
        return getGroupId(name) != -1;
    }

    public static void main(String[] args) throws SQLException {
        Database sqlTest = new Database();
        sqlTest.initialization("HelloDB");
        sqlTest.putTovar("Apple1",5,10,"RedChief",1,"Kherson");
        sqlTest.putTovar("Apple2",7,10,1,"Kherson");
        sqlTest.putTovar("Apple3",8,10,"GreenChief",1);
        sqlTest.putTovar("Apple4",10,10,1);
        sqlTest.updateTovar(sqlTest.getTovarId("Apple3"), "maker","Kiev");
        sqlTest.updateTovar(sqlTest.getTovarId("Apple4"), "count",20);
        sqlTest.showTovar(sqlTest.getTovarList());
        sqlTest.deleteTovar("Apple2");
       sqlTest.showTovar(sqlTest.getTovarList());
        System.out.println(sqlTest.containTovar("Apple1"));
        System.out.println(sqlTest.containTovar("Apple2"));

        System.out.println("____________________________________");
        List<String> li = new ArrayList<>();
        sqlTest.showTovar(sqlTest.getTovarListByCriteria(li));
        System.out.println("____________________________________");
        sqlTest.putGroup("Fruits","All fruits");
        sqlTest.showGroup(sqlTest.getGroupList());

        sqlTest.deleteGroup("Fruits");
        System.out.println("____________________________________");
        sqlTest.showTovar(sqlTest.getTovarListByCriteria(li));
        System.out.println("____________________________________");
//        sqlTest.putGroup("Fruits","All fruits");
        sqlTest.showGroup(sqlTest.getGroupList());
//        List<String> li2 = new ArrayList<>();
//        li2.add("count > 15");
//        li2.add("price < 15");
//        li2.add("group_id = 2b");
//        sqlTest.show(sqlTest.getTovarListByCriteria(li2));
    }
}
