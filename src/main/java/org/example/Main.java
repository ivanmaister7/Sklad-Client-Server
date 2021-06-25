package org.example;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import server.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public static final int WEIGHT = 800;
    public static final int HEIGHT = 600;
    public static final int SPACE_BETWEEN = 10;
    public static final int GROUP_WEIGHT = 800 / 4;
    public static final int GROUP_HEIGHT = 600 / 10;
    private List<Button> buttonGroupList = new ArrayList<>();
    private List<Label> labelGroupList = new ArrayList<>();
    private List<Button> buttonEditList = new ArrayList<>();
    private List<Button> buttonDeleteList = new ArrayList<>();
    private List<Button> buttonTovarList = new ArrayList<>();
    private List<Label> labelTovarList = new ArrayList<>();
    private List<Button> buttonEditTovarList = new ArrayList<>();
    private List<Button> buttonDeleteTovarList = new ArrayList<>();
    private StoreClientTCP client = new StoreClientTCP();

    Button buttonFind;
    Button buttonDoFind;
    Button moreLess1;
    Button moreLess2;
    TextArea findResult;

    Button buttonAdd;
    Label labelName;
    Button buttonAddTovar;
    Label labelNameTovar;

    Label newGroup;
    Label name;
    TextField nameField;
    Label info;
    TextField infoField;
    Button saveGroup;
    Button editGroup;
    Button buttonCancel;

    Label newTovar;
    Label nameTovar;
    TextField nameFieldTovar;
    Label infoTovar;
    TextField infoFieldTovar;
    Label priceTovar;
    TextField priceFieldTovar;
    Label makerTovar;
    TextField makerFieldTovar;
    Label countTovar;
    TextField countFieldTovar;
    Button saveTovar;
    Button editTovar;

    Button nowEdit;
    Button nowEditTovar;

    Group root;
    private int count = 0;
    private int countT = 0;
    private int nowGroupId;
    private String nowGroup;

    public Main() throws UnknownHostException {
    }

    @Override
    public void start(Stage stage) throws InterruptedException {

        // BUTTON +GROUP
        buttonAdd = new Button();
        buttonAdd.setText("+");
        buttonAdd.setPrefSize(WEIGHT * 0.0625,HEIGHT * 0.083);
        buttonAdd.setLayoutX(0);
        buttonAdd.setLayoutY(0);
        buttonAdd.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                showAddForm(true);
                showMainMenu(false);
            }
        });
        root = new Group(buttonAdd);
        //LABEL "SKLAD"
        labelName = new Label();
        labelName.setPrefSize(WEIGHT * 0.875,HEIGHT * 0.083);
        labelName.setText("SKLAD");
        labelName.setLayoutX(WEIGHT * 0.0625);
        labelName.setLayoutY(0);
        labelName.setAlignment(Pos.CENTER);
        root.getChildren().add(labelName);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(root);
        Scene scene = new Scene(scrollPane, WEIGHT, HEIGHT);
        stage.setTitle("Button Example");
        stage.setScene(scene);
        stage.show();

        // BUTTON +TOVAR
        buttonAddTovar = new Button();
        buttonAddTovar.setText("+");
        buttonAddTovar.setPrefSize(WEIGHT * 0.0625,HEIGHT * 0.083);
        buttonAddTovar.setLayoutX(SPACE_BETWEEN + GROUP_HEIGHT);
        buttonAddTovar.setLayoutY(0);
        buttonAddTovar.setVisible(false);
        buttonAddTovar.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                showTovarMenu(false);
                showAddTovarForm(true);
            }
        });
        root.getChildren().add(buttonAddTovar);
        //LABEL "GROUP + name"
        labelNameTovar = new Label();
        labelNameTovar.setPrefSize(WEIGHT * 0.5,HEIGHT * 0.083);
        labelNameTovar.setText("Group");
        labelNameTovar.setLayoutX(WEIGHT * 0.2);
        labelNameTovar.setLayoutY(0);
        labelNameTovar.setVisible(false);
        labelNameTovar.setAlignment(Pos.CENTER);
        root.getChildren().add(labelNameTovar);

        //ADD GROUP FORM
        newGroup = new Label("New Group");
        newGroup.setPrefSize(WEIGHT * 0.875,HEIGHT * 0.083);
        newGroup.setLayoutX(WEIGHT * 0.0625);
        newGroup.setLayoutY(0);
        newGroup.setAlignment(Pos.CENTER);
        newGroup.setVisible(false);
        root.getChildren().add(newGroup);

        name = new Label("Name:");
        name.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        name.setLayoutX(WEIGHT * 0.2);
        name.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 2);
        name.setAlignment(Pos.CENTER);
        name.setVisible(false);
        root.getChildren().add(name);

        nameField = new TextField();
        nameField.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        nameField.setLayoutX(WEIGHT * 0.4);
        nameField.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 2);
        nameField.setAlignment(Pos.CENTER);
        nameField.setVisible(false);
        root.getChildren().add(nameField);

        info = new Label("Info:");
        info.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        info.setLayoutX(WEIGHT * 0.2);
        info.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 4);
        info.setAlignment(Pos.CENTER);
        info.setVisible(false);
        root.getChildren().add(info);

        infoField = new TextField();
        infoField.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        infoField.setLayoutX(WEIGHT * 0.4);
        infoField.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 4);
        infoField.setAlignment(Pos.CENTER);
        infoField.setVisible(false);
        root.getChildren().add(infoField);

        saveGroup = new Button("Save");
        saveGroup.setPrefSize(GROUP_HEIGHT + 4 * SPACE_BETWEEN,GROUP_HEIGHT);
        saveGroup.setLayoutX(WEIGHT * 0.4);
        saveGroup.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 6);
        saveGroup.setVisible(false);
        saveGroup.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                saveGroup.setStyle("-fx-background-color: lightgray;");
                Packet check = new Packet((byte) 1,1L,
                        new Message(16,0,nameField.getText()));
                client.getInQ().put(check);
                Thread.sleep(200);
                Packet ansCheck = client.getAnswerQ().poll();
                if(ansCheck.getBMsg().getMessage().equals("true")){
                    saveGroup.setStyle("-fx-background-color: red;");
                    return;
                }

                Packet p = new Packet((byte) 1,1L,
                        new Message(11,0,nameField.getText() + " " + infoField.getText()));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();
                createButton();
                createField();
                createEdit();
                createDelete();
                saveGroup.setStyle("-fx-background-color: lightgray;");
                showAddForm(false);
                showMainMenu(true);
            }
        });
        root.getChildren().add(saveGroup);

        //ADD TOVAR FORM
        newTovar = new Label("New Tovar");
        newTovar.setPrefSize(WEIGHT * 0.875,HEIGHT * 0.083);
        newTovar.setLayoutX(WEIGHT * 0.0625);
        newTovar.setLayoutY(0);
        newTovar.setAlignment(Pos.CENTER);
        newTovar.setVisible(false);
        root.getChildren().add(newTovar);

        nameTovar = new Label("Name:");
        nameTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        nameTovar.setLayoutX(WEIGHT * 0.2);
        nameTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 1);
        nameTovar.setAlignment(Pos.CENTER);
        nameTovar.setVisible(false);
        root.getChildren().add(nameTovar);

        nameFieldTovar = new TextField();
        nameFieldTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        nameFieldTovar.setLayoutX(WEIGHT * 0.4);
        nameFieldTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 1);
        nameFieldTovar.setAlignment(Pos.CENTER);
        nameFieldTovar.setVisible(false);
        root.getChildren().add(nameFieldTovar);

        infoTovar = new Label("Info:");
        infoTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        infoTovar.setLayoutX(WEIGHT * 0.2);
        infoTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 2);
        infoTovar.setAlignment(Pos.CENTER);
        infoTovar.setVisible(false);
        root.getChildren().add(infoTovar);

        infoFieldTovar = new TextField();
        infoFieldTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        infoFieldTovar.setLayoutX(WEIGHT * 0.4);
        infoFieldTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 2);
        infoFieldTovar.setAlignment(Pos.CENTER);
        infoFieldTovar.setVisible(false);
        root.getChildren().add(infoFieldTovar);

        priceTovar = new Label("Price:");
        priceTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        priceTovar.setLayoutX(WEIGHT * 0.2);
        priceTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 3);
        priceTovar.setAlignment(Pos.CENTER);
        priceTovar.setVisible(false);
        root.getChildren().add(priceTovar);

        priceFieldTovar = new TextField();
        priceFieldTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        priceFieldTovar.setLayoutX(WEIGHT * 0.4);
        priceFieldTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 3);
        priceFieldTovar.setAlignment(Pos.CENTER);
        priceFieldTovar.setVisible(false);
        root.getChildren().add(priceFieldTovar);


        makerTovar = new Label("Maker:");
        makerTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        makerTovar.setLayoutX(WEIGHT * 0.2);
        makerTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 4);
        makerTovar.setAlignment(Pos.CENTER);
        makerTovar.setVisible(false);
        root.getChildren().add(makerTovar);

        makerFieldTovar = new TextField();
        makerFieldTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        makerFieldTovar.setLayoutX(WEIGHT * 0.4);
        makerFieldTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 4);
        makerFieldTovar.setAlignment(Pos.CENTER);
        makerFieldTovar.setVisible(false);
        root.getChildren().add(makerFieldTovar);


        countTovar = new Label("Count:");
        countTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        countTovar.setLayoutX(WEIGHT * 0.2);
        countTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 5);
        countTovar.setAlignment(Pos.CENTER);
        countTovar.setVisible(false);
        root.getChildren().add(countTovar);

        countFieldTovar = new TextField();
        countFieldTovar.setPrefSize(WEIGHT * 0.2,HEIGHT * 0.083);
        countFieldTovar.setLayoutX(WEIGHT * 0.4);
        countFieldTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 5);
        countFieldTovar.setAlignment(Pos.CENTER);
        countFieldTovar.setVisible(false);
        root.getChildren().add(countFieldTovar);

        saveTovar = new Button("Save");
        saveTovar.setPrefSize(GROUP_HEIGHT + 4 * SPACE_BETWEEN,GROUP_HEIGHT);
        saveTovar.setLayoutX(WEIGHT * 0.4);
        saveTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 6);
        saveTovar.setVisible(false);
        saveTovar.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                saveTovar.setStyle("-fx-background-color: lightgray;");
                Packet check = new Packet((byte) 1,1L,
                        new Message(26,0,nameFieldTovar.getText()));
                client.getInQ().put(check);
                Thread.sleep(200);
                Packet ansCheck = client.getAnswerQ().poll();
                if(ansCheck.getBMsg().getMessage().equals("true")){
                    saveTovar.setStyle("-fx-background-color: red;");
                    return;
                }

                Packet p = new Packet((byte) 1,1L,
                        new Message(21,0,
                                nameFieldTovar.getText() + " " +
                                        priceFieldTovar.getText() + " " +
                                        countFieldTovar.getText()+ " " +
                                        infoFieldTovar.getText() + " " +
                                        nowGroupId + " " +
                                        makerFieldTovar.getText()

                        ));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();
                saveTovar.setStyle("-fx-background-color: lightgray;");
                showAddTovarForm(false);
                showTovarMenu(true);
            }
        });
        root.getChildren().add(saveTovar);

        //Another button
        editGroup = new Button("Edit");
        editGroup.setPrefSize(GROUP_HEIGHT + 4 * SPACE_BETWEEN,GROUP_HEIGHT);
        editGroup.setLayoutX(WEIGHT * 0.4);
        editGroup.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 6);
        editGroup.setVisible(false);
        editGroup.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                int id = getIdRow(nowEdit);
                Packet p = new Packet((byte) 1,1L,
                        new Message(12,0, buttonGroupList.get(id).getText() + " " + nameField.getText() + " " + infoField.getText()));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();
                showEditForm(false);
                showMainMenu(true);
            }
        });
        root.getChildren().add(editGroup);

        editTovar = new Button("Edit");
        editTovar.setPrefSize(GROUP_HEIGHT + 4 * SPACE_BETWEEN,GROUP_HEIGHT);
        editTovar.setLayoutX(WEIGHT * 0.4);
        editTovar.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 6);
        editTovar.setVisible(false);
        editTovar.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                int id = getIdRow(nowEditTovar);
                Packet p = new Packet((byte) 1,1L,
                        new Message(22,0,
                                buttonTovarList.get(id).getText() + " " +
                                        nameFieldTovar.getText() + " " +
                                        infoFieldTovar.getText() + " " +
                                        priceFieldTovar.getText() + " " +
                                        makerFieldTovar.getText() + " " +
                                        countFieldTovar.getText()
                        ));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();
                showEditTovarForm(false);
                showTovarMenu(true);
            }
        });
        root.getChildren().add(editTovar);

        buttonCancel = new Button();
        buttonCancel.setText("<--");
        buttonCancel.setPrefSize(WEIGHT * 0.0625,HEIGHT * 0.083);
        buttonCancel.setLayoutX(0);
        buttonCancel.setLayoutY(0);
        buttonCancel.setVisible(false);
        buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                findResult.setVisible(false);
                showMainMenu(true);
                showEditForm(false);
                showTovarMenu(false);
                showEditTovarForm(false);
                showFindForm(false);
            }
        });
        root.getChildren().add(buttonCancel);

        buttonFind = new Button();
        buttonFind.setText("FIND");
        buttonFind.setPrefSize(WEIGHT * 0.0625,HEIGHT * 0.083);
        buttonFind.setLayoutX(WEIGHT - WEIGHT * 0.0625);
        buttonFind.setLayoutY(0);
        buttonFind.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                showMainMenu(false);
                buttonCancel.setVisible(true);
                showFindForm(true);
            }
        });
        root.getChildren().add(buttonFind);

        moreLess1 = new Button("<");
        moreLess1.setPrefSize(GROUP_HEIGHT-SPACE_BETWEEN,GROUP_HEIGHT-SPACE_BETWEEN);
        moreLess1.setLayoutX(WEIGHT * 0.6 + SPACE_BETWEEN);
        moreLess1.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 3);
        moreLess1.setAlignment(Pos.CENTER);
        moreLess1.setVisible(false);
        moreLess1.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                if(moreLess1.getText().equals("<")){
                    moreLess1.setText(">");
                }
                else if(moreLess1.getText().equals(">")){
                    moreLess1.setText("=");
                }
                else if(moreLess1.getText().equals("=")){
                    moreLess1.setText("<");
                }
            }
        });
        root.getChildren().add(moreLess1);

        moreLess2 = new Button("<");
        moreLess2.setPrefSize(GROUP_HEIGHT-SPACE_BETWEEN,GROUP_HEIGHT-SPACE_BETWEEN);
        moreLess2.setLayoutX(WEIGHT * 0.6 + SPACE_BETWEEN);
        moreLess2.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 5);
        moreLess2.setAlignment(Pos.CENTER);
        moreLess2.setVisible(false);
        moreLess2.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                if(moreLess2.getText().equals("<")){
                    moreLess2.setText(">");
                }
                else if(moreLess2.getText().equals(">")){
                    moreLess2.setText("=");
                }
                else if(moreLess2.getText().equals("=")){
                    moreLess2.setText("<");
                }
            }
        });
        root.getChildren().add(moreLess2);

        findResult = new TextArea();
        findResult.setPrefColumnCount(40);
        findResult.setPrefRowCount(30);
        findResult.setVisible(false);
        findResult.setLayoutX(WEIGHT * 0.2);
        findResult.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 1);
        root.getChildren().add(findResult);

        buttonDoFind = new Button("Find");
        buttonDoFind.setPrefSize(GROUP_HEIGHT + 4 * SPACE_BETWEEN,GROUP_HEIGHT);
        buttonDoFind.setLayoutX(WEIGHT * 0.4);
        buttonDoFind.setLayoutY((GROUP_HEIGHT + SPACE_BETWEEN) * 6);
        buttonDoFind.setVisible(false);
        buttonDoFind.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                findResult.setVisible(true);
                List<String> cr = new ArrayList<>();
                if(!nameFieldTovar.getText().equals(""))
                    cr.add("name = " + "'" + nameFieldTovar.getText() + "' ");
                else if(!infoFieldTovar.getText().equals(""))
                    cr.add("info = " + "'" + infoFieldTovar.getText() + "' ");
                else if(!priceFieldTovar.getText().equals(""))
                    cr.add("price " + moreLess1.getText() + " " + priceFieldTovar.getText());
                else if(!makerFieldTovar.getText().equals(""))
                    cr.add("maker = " + "'" + makerFieldTovar.getText() + "' ");
                else if(!countFieldTovar.getText().equals(""))
                    cr.add("count " + moreLess2.getText() + " " + countFieldTovar.getText());
                String query = "";
                for(String s : cr){
                    query += " AND " + s;
                }
                if(!query.equals("")){
                    query = query.substring(4);
                }

                Packet p = new Packet((byte) 1,1L,new Message(24,0,query));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();

                List<Tovar> tovar = new ListParserTovar(ans.getBMsg().getMessage()).getGroupList();
                String res = "";

                for(Tovar t : tovar){
                    res += t.toString() + "\n";
                }
                if(res.equals("")){
                    res = "No Find...";
                }
                findResult.setText(res);
                buttonDoFind.setVisible(false);

            }
        });
        root.getChildren().add(buttonDoFind);

        Packet p = new Packet((byte) 1,1L,new Message(14,0,""));
        client.getInQ().put(p);
        Thread.sleep(200);
        Packet ans = client.getAnswerQ().poll();
        for (int i = 0; i < new ListParser(ans.getBMsg().getMessage()).getGroupList().size(); i++) {
            createButton();
            createField();
            createEdit();
            createDelete();
        }
        showMainMenu(true);

    }

    private void showTovarMenu(boolean b) throws InterruptedException {
        buttonAddTovar.setVisible(b);
        labelNameTovar.setVisible(b);
        buttonCancel.setVisible(b);
        if(b){
            labelNameTovar.setText("Group " + nowGroup);
        }

        List<server.Tovar> tovar;
        if(b){
            Packet p = new Packet((byte) 1,1L,new Message(24,0,"group_id = " + nowGroupId));
            client.getInQ().put(p);
            Thread.sleep(200);
            Packet ans = client.getAnswerQ().poll();

            tovar = new ListParserTovar(ans.getBMsg().getMessage()).getGroupList();

            buttonTovarList = new ArrayList<>();
            labelTovarList = new ArrayList<>();
            buttonEditTovarList = new ArrayList<>();
            buttonDeleteTovarList = new ArrayList<>();
            countT = 0;
            for (int i = 0; i < tovar.size(); i++) {
                createButtonTovar();
                createFieldTovar();
                createEditTovar();
                createDeleteTovar();
            }
            for (int i = 0; i < buttonTovarList.size(); i++) {
                buttonTovarList.get(i).setText(tovar.get(i).getName());
                labelTovarList.get(i).setText(tovar.get(i).getInfo());
            }

        }
        for (int i = 0; i < buttonTovarList.size(); i++) {
            buttonTovarList.get(i).setVisible(b);
            labelTovarList.get(i).setVisible(b);
            buttonEditTovarList.get(i).setVisible(b);
            buttonDeleteTovarList.get(i).setVisible(b);
        }
    }

    public void showFindForm(boolean b) {
        showAddTovarForm(b);
        saveTovar.setVisible(false);
        buttonDoFind.setVisible(b);
        moreLess1.setVisible(b);
        moreLess2.setVisible(b);
    }
    public void showAddTovarForm(boolean b) {
        nameFieldTovar.setDisable(false);
        infoFieldTovar.setDisable(false);
        priceFieldTovar.setDisable(false);
        makerFieldTovar.setDisable(false);
        countFieldTovar.setDisable(false);
        newTovar.setVisible(b);
        nameTovar.setVisible(b);
        nameFieldTovar.setVisible(b);
        infoTovar.setVisible(b);
        infoFieldTovar.setVisible(b);
        priceTovar.setVisible(b);
        priceFieldTovar.setVisible(b);
        makerTovar.setVisible(b);
        makerFieldTovar.setVisible(b);
        countTovar.setVisible(b);
        countFieldTovar.setVisible(b);
        saveTovar.setVisible(b);
        buttonCancel.setVisible(b);
        if(!b){
            nameFieldTovar.setText("");
            infoFieldTovar.setText("");
            priceFieldTovar.setText("");
            makerFieldTovar.setText("");
            countFieldTovar.setText("");
        }
    }

    public void showAddForm(boolean b){
        newGroup.setVisible(b);
        name.setVisible(b);
        nameField.setVisible(b);
        info.setVisible(b);
        infoField.setVisible(b);
        saveGroup.setVisible(b);
        buttonCancel.setVisible(b);
        if(!b){
            nameField.setText("");
            infoField.setText("");
        }
    }
    public void showEditForm(boolean b){
        showAddForm(b);
        saveGroup.setVisible(false);
        editGroup.setVisible(b);
    }
    public void showEditTovarForm(boolean b){
        showAddTovarForm(b);
        saveTovar.setVisible(false);
        editTovar.setVisible(b);
    }
    public void showMainMenu(boolean b) throws InterruptedException {
        List<server.Group> groups;
        if(b){
            Packet p = new Packet((byte) 1,1L,new Message(14,0,""));
            client.getInQ().put(p);
            Thread.sleep(200);
            Packet ans = client.getAnswerQ().poll();
            groups = new ListParser(ans.getBMsg().getMessage()).getGroupList();
            for (int i = 0; i < buttonGroupList.size(); i++) {
                buttonGroupList.get(i).setText(groups.get(i).getName());
                labelGroupList.get(i).setText(groups.get(i).getInfo());
            }
        }
        buttonAdd.setVisible(b);
        labelName.setVisible(b);
        buttonFind.setVisible(b);
        for (int i = 0; i < buttonGroupList.size(); i++) {
            buttonGroupList.get(i).setVisible(b);
            labelGroupList.get(i).setVisible(b);
            buttonEditList.get(i).setVisible(b);
            buttonDeleteList.get(i).setVisible(b);
        }
    }
    public void createButton(){
        Button b = new Button();
        b.setPrefSize(GROUP_WEIGHT,GROUP_HEIGHT);
        b.setLayoutX(SPACE_BETWEEN);
        b.setLayoutY(100 + (GROUP_HEIGHT + SPACE_BETWEEN) * count);
        b.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                nowGroup = b.getText();
                Packet p1 = new Packet((byte) 1,1L,new Message(15,0,nowGroup));
                client.getInQ().put(p1);
                Thread.sleep(100);
                Packet ans1 = client.getAnswerQ().poll();
                nowGroupId = Integer.parseInt(ans1.getBMsg().getMessage());
                showTovarMenu(true);
                showMainMenu(false);

            }
        });
        root.getChildren().add(b);
        buttonGroupList.add(b);
    }
    public void createField(){
        Label field = new Label();
        field.setPrefSize(WEIGHT - 5 * SPACE_BETWEEN - 2 * GROUP_HEIGHT - GROUP_WEIGHT,GROUP_HEIGHT);
        field.setLayoutX(GROUP_WEIGHT + 2 * SPACE_BETWEEN);
        field.setLayoutY(100 + (GROUP_HEIGHT + SPACE_BETWEEN) * count);
        field.setStyle("-fx-border-color: grey;-fx-border-radius: 10px");

        root.getChildren().add(field);
        labelGroupList.add(field);
    }
    public void createEdit(){
        Button b = new Button();
        b.setPrefSize(GROUP_HEIGHT,GROUP_HEIGHT);
        b.setLayoutX(WEIGHT - 2 * (SPACE_BETWEEN + GROUP_HEIGHT));
        b.setText("edit");
        b.setLayoutY(100 + (GROUP_HEIGHT + SPACE_BETWEEN) * count);
        b.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                nowEdit = b;
                int id = getIdRow(nowEdit);
                nameField.setText(buttonGroupList.get(id).getText());
                infoField.setText(labelGroupList.get(id).getText());
                showMainMenu(false);
                showEditForm(true);
            }
        });
        root.getChildren().add(b);
        buttonEditList.add(b);
    }
    public void createDelete(){
        Button b = new Button();
        b.setPrefSize(GROUP_HEIGHT,GROUP_HEIGHT);
        b.setLayoutX(WEIGHT - SPACE_BETWEEN - GROUP_HEIGHT);
        b.setText("del");
        b.setLayoutY(100 + (GROUP_HEIGHT + SPACE_BETWEEN) * count++);
        b.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                showMainMenu(false);

                Packet p = new Packet((byte) 1,1L,new Message(13,0,buttonGroupList.get(getIdRow(b)).getText()));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();
                buttonGroupList.remove(buttonGroupList.size() - 1);
                labelGroupList.remove(labelGroupList.size() - 1);
                buttonEditList.remove(buttonEditList.size() - 1);
                buttonDeleteList.remove(buttonDeleteList.size() - 1);
                showMainMenu(true);
                count--;
            }
        });
        root.getChildren().add(b);
        buttonDeleteList.add(b);
    }

    public void createButtonTovar(){
        Button b = new Button();
        b.setPrefSize(GROUP_WEIGHT,GROUP_HEIGHT);
        b.setLayoutX(SPACE_BETWEEN);
        b.setLayoutY(100 + (GROUP_HEIGHT + SPACE_BETWEEN) * countT);
        b.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                int id = getIdRow(b);

                Packet p = new Packet((byte) 1,1L,new Message(24,0,"name = " + "'" + buttonTovarList.get(id).getText() + "'"));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();

                List<Tovar> tovar = new ListParserTovar(ans.getBMsg().getMessage()).getGroupList();

                showTovarMenu(false);
                showEditTovarForm(true);

                nameFieldTovar.setText(tovar.get(0).getName());
                nameFieldTovar.setDisable(true);
                infoFieldTovar.setText(tovar.get(0).getInfo());
                infoFieldTovar.setDisable(true);
                priceFieldTovar.setText(""+tovar.get(0).getPrice());
                priceFieldTovar.setDisable(true);
                makerFieldTovar.setText(tovar.get(0).getMaker());
                makerFieldTovar.setDisable(true);
                countFieldTovar.setText(""+tovar.get(0).getCount());
                countFieldTovar.setDisable(true);

                editTovar.setVisible(false);
            }
        });
        root.getChildren().add(b);
        buttonTovarList.add(b);
    }
    public void createFieldTovar(){
        Label field = new Label();
        field.setPrefSize(WEIGHT - 5 * SPACE_BETWEEN - 2 * GROUP_HEIGHT - GROUP_WEIGHT,GROUP_HEIGHT);
        field.setLayoutX(GROUP_WEIGHT + 2 * SPACE_BETWEEN);
        field.setLayoutY(100 + (GROUP_HEIGHT + SPACE_BETWEEN) * countT);
        field.setStyle("-fx-border-color: grey;-fx-border-radius: 10px");

        root.getChildren().add(field);
        labelTovarList.add(field);
    }
    public void createEditTovar(){
        Button b = new Button();
        b.setPrefSize(GROUP_HEIGHT,GROUP_HEIGHT);
        b.setLayoutX(WEIGHT - 2 * (SPACE_BETWEEN + GROUP_HEIGHT));
        b.setText("edit");
        b.setLayoutY(100 + (GROUP_HEIGHT + SPACE_BETWEEN) * countT);
        b.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                nowEditTovar = b;
                int id = getIdRow(nowEditTovar);

                Packet p = new Packet((byte) 1,1L,new Message(24,0,"name = " + "'" + buttonTovarList.get(id).getText() + "'"));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();

                List<Tovar> tovar = new ListParserTovar(ans.getBMsg().getMessage()).getGroupList();

                nameFieldTovar.setText(tovar.get(0).getName());
                infoFieldTovar.setText(tovar.get(0).getInfo());
                priceFieldTovar.setText(""+tovar.get(0).getPrice());
                makerFieldTovar.setText(tovar.get(0).getMaker());
                countFieldTovar.setText(""+tovar.get(0).getCount());

                showTovarMenu(false);
                showEditTovarForm(true);
            }
        });
        root.getChildren().add(b);
        buttonEditTovarList.add(b);
    }
    public void createDeleteTovar(){
        Button b = new Button();
        b.setPrefSize(GROUP_HEIGHT,GROUP_HEIGHT);
        b.setLayoutX(WEIGHT - SPACE_BETWEEN - GROUP_HEIGHT);
        b.setText("del");
        b.setLayoutY(100 + (GROUP_HEIGHT + SPACE_BETWEEN) * countT++);
        b.setOnAction(new EventHandler<ActionEvent>() {
            @SneakyThrows
            @Override public void handle(ActionEvent e) {
                showTovarMenu(false);

                Packet p = new Packet((byte) 1,1L,new Message(23,0,buttonTovarList.get(getIdRow(b)).getText()));
                client.getInQ().put(p);
                Thread.sleep(200);
                Packet ans = client.getAnswerQ().poll();
                buttonTovarList.remove(buttonTovarList.size() - 1);
                labelTovarList.remove(labelTovarList.size() - 1);
                buttonEditTovarList.remove(buttonEditTovarList.size() - 1);
                buttonDeleteTovarList.remove(buttonDeleteTovarList.size() - 1);
                showTovarMenu(true);
                countT--;
            }
        });
        root.getChildren().add(b);
        buttonDeleteTovarList.add(b);
    }

    private int getIdRow(Button b) {
       return (int)(b.getLayoutY() - 100)/(GROUP_HEIGHT + SPACE_BETWEEN);
    }

    public static void main(String args[]){
        launch(args);
    }
}