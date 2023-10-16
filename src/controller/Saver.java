package controller;


import chessComponent.*;
import chessComponent.chessLibrary.*;
import model.ChessColor;
import model.ChessboardPoint;
import ui.Chessboard;

import javax.swing.*;
import javax.xml.stream.Location;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Saver {
    /**
     * 这是一个用来储存数据的方法
     * 包括保存一次行棋
     * 读取棋盘
     * 判断棋盘的正不正确
     */
    ArrayList<Procedure> procedures = new ArrayList<>();
    Chessboard chessboard;

    File tt = new File("D:\\java代码\\IdeaProjects\\final project\\src\\save\\tt.txt");
    public ArrayList<ArrayList<String>> steps = new ArrayList<>();

    public ClickController clickController;
    public Saver(Chessboard chessboard, ClickController clickController) {
        endgame();
        this.chessboard = chessboard;
        this.clickController = clickController;
    }


    public void saveProcedure(){
        /**
         * 这是一个存储游戏数据的方法
         * 需要在swapplayer里面调用
         */
        SquareComponent[][] chesses = chessboard.getChessComponents();
        String[] records = {"", "", "", ""};
        for(int i=0; i < chesses.length; i++){
            for(int j = 0; j < chesses[i].length; j++){
                if(chesses[i][j].isReversal){
                    records[i] = records[i] + "1";
                }else{
                    records[i] = records[i] + "0";
                }
                if(chesses[i][j] instanceof EmptySlotComponent){
                    records[i] = records[i] + "h";
                }else{
                    if(chesses[i][j].chessColor == ChessColor.BLACK){
                        switch (chesses[i][j].type){
                            case 1 -> records[i] = records[i] + "a";
                            case 2 -> records[i] = records[i] + "b";
                            case 3 -> records[i] = records[i] + "c";
                            case 4 -> records[i] = records[i] + "d";
                            case 5 -> records[i] = records[i] + "e";
                            case 6 -> records[i] = records[i] + "f";
                            case 7 -> records[i] = records[i] + "g";
                        }
                    }else{
                        switch (chesses[i][j].type){
                            case 1 -> records[i] = records[i] + "A";
                            case 2 -> records[i] = records[i] + "B";
                            case 3 -> records[i] = records[i] + "C";
                            case 4 -> records[i] = records[i] + "D";
                            case 5 -> records[i] = records[i] + "E";
                            case 6 -> records[i] = records[i] + "F";
                            case 7 -> records[i] = records[i] + "G";
                        }
                    }
                }
                records[i] += " ";
            }
        }

        try {
            RandomAccessFile bw = new RandomAccessFile("D:\\java代码\\IdeaProjects\\final project\\src\\save\\tt.txt", "rw");
            bw.seek(bw.length());
            bw.write(String.format("%s\n", chessboard.getCurrentColor()).getBytes(StandardCharsets.UTF_8));
            for(int i = 0; i < records.length; i++){
                bw.write(String.format( "%s\n",records[i]).getBytes(StandardCharsets.UTF_8));
            }
            bw.write(String.format( "%d\n",chessboard.redPoints).getBytes(StandardCharsets.UTF_8));
            bw.write(String.format("%d\n",chessboard.blackPoints).getBytes(StandardCharsets.UTF_8));
            bw.write("\n".getBytes(StandardCharsets.UTF_8));
            bw.close();
            //我们每个步骤的标志就是，在每个步骤之间会用\n分开
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SquareComponent[][] currentchesses = new SquareComponent[4][8];
        for(int i = 0; i < chesses.length; i++){
            for(int j = 0; j < chesses[i].length; j++){
                ChessboardPoint p = new ChessboardPoint(chesses[i][j].chessboardPoint.getX(), chesses[i][j].chessboardPoint.getY());
                Point pp = chessboard.calculatePoint(p.getX(), p.getY());
                ChessColor c = chesses[i][j].chessColor;

                switch (chesses[i][j].type){
                    case 0 -> currentchesses[i][j] = new EmptySlotComponent(p, pp, clickController, chessboard.CHESS_SIZE);
                    case 1 -> currentchesses[i][j] = new SoldierChessComponent(p, pp, c, clickController, chessboard.CHESS_SIZE);
                    case 2 -> currentchesses[i][j] = new CannonChessComponent(p, pp, c, clickController, chessboard.CHESS_SIZE);
                    case 3 -> currentchesses[i][j] = new HorseChessComponent(p, pp, c, clickController, chessboard.CHESS_SIZE);
                    case 4 -> currentchesses[i][j] = new ChariotChessComponent(p, pp, c, clickController, chessboard.CHESS_SIZE);
                    case 5 -> currentchesses[i][j] = new MinisterChessComponent(p, pp, c, clickController, chessboard.CHESS_SIZE);
                    case 6 -> currentchesses[i][j] = new AdviserChessComponent(p, pp, c, clickController, chessboard.CHESS_SIZE);
                    case 7 -> currentchesses[i][j] = new GeneralChessComponent(p, pp, c, clickController, chessboard.CHESS_SIZE);
                }
            }
        }
        procedures.add(new Procedure(chessboard.getCurrentColor(), currentchesses, chessboard.redPoints, chessboard.blackPoints));
    }

    public void readProcedure(List<String> chessData){
        /**
         * 这个方法是在chessboard的loadgame里面调用的
         */
        endgame();
        ArrayList<Integer> gaps = new ArrayList<>();
        //gaps的数量就是棋盘有多少步
        int count = 0;
        for(String s : chessData){
            if(s.isBlank()){
                gaps.add(count);
            }
            count++;
        }
        for(int i = 0; i < gaps.size(); i++){
            steps.add(new ArrayList<String>());
        }
        //为steps添加这么多步的位置
        //step的一维是每一步
        //二维是每一步的每一行
        int num = 0;
        for(int i = 0; i < chessData.size(); i++){
            if(gaps.contains(i)){
                num ++;
            }else{
                steps.get(num).add(chessData.get(i));
            }
        }
        //在这里进行了两个棋盘判断，检测有没有现行颜色，检测棋盘结构
        if(!testChessboardStructure()){
            return;
        }



        String[][] lines = new String[4][];
        for(int k = 0; k < steps.size(); k++){
            //下面是每一个procedure的参数
            ChessColor cc = ChessColor.NONE;
            SquareComponent[][] chess = new SquareComponent[4][8];
            int redPoints = 0;
            int blackPoints = 0;

            if(steps.get(k).get(1).equals(ChessColor.RED)){
                cc = ChessColor.RED;
            }else{
                cc = ChessColor.BLACK;
            }
            for(int j = 1; j < 5; j++){
                lines[j-1] = steps.get(k).get(j).split(" ");
            }
            redPoints = Integer.parseInt(steps.get(k).get(5));
            blackPoints = Integer.parseInt(steps.get(k).get(6));

            for(int i = 0; i < lines.length; i++){
                for(int j = 0; j < lines[i].length; j++){
                    switch (lines[i][j].charAt(1)){
                        case 'h' -> chess[i][j] = new EmptySlotComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'a' -> chess[i][j] = new SoldierChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.BLACK,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'b' -> chess[i][j] = new CannonChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.BLACK,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'c' -> chess[i][j] = new HorseChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.BLACK,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'd' -> chess[i][j] = new ChariotChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.BLACK,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'e' -> chess[i][j] = new MinisterChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.BLACK,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'f' -> chess[i][j] = new AdviserChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.BLACK,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'g' -> chess[i][j] = new GeneralChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.BLACK,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'A' -> chess[i][j] = new SoldierChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.RED,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'B' -> chess[i][j] = new CannonChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.RED,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'C' -> chess[i][j] = new HorseChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.RED,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'D' -> chess[i][j] = new ChariotChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.RED,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'E' -> chess[i][j] = new MinisterChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.RED,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'F' -> chess[i][j] = new AdviserChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.RED,chessboard.clickController, chessboard.CHESS_SIZE);
                        case 'G' -> chess[i][j] = new GeneralChessComponent(new ChessboardPoint(i,j), chessboard.calculatePoint(i,j),ChessColor.RED,chessboard.clickController, chessboard.CHESS_SIZE);
                        default -> {
                            JOptionPane.showMessageDialog(null,"错误代码103：棋子非规定类型","警告！",JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    switch (lines[i][j].charAt(0)){
                        case '0' -> chess[i][j].isReversal = false;
                        case '1' -> chess[i][j].isReversal = true;
                        default -> {
                            JOptionPane.showMessageDialog(null,"错误代码103：棋子非规定类型","警告！",JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
            }
            procedures.add(new Procedure(cc,chess,redPoints,blackPoints));
        }

        testStep();
    }

    public void loadGameFromProcedure(){
        /**
         * 加载procedure的最后一次棋盘
         */
        List<String> chessData;
        try {
            chessData = Files.readAllLines(Paths.get("D:\\java代码\\IdeaProjects\\final project\\src\\save\\tt.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        readProcedure(chessData);
        if(procedures.size() < 2){
            return;
        }
        Procedure p = procedures.get(procedures.size()-1);
        chessboard.setCurrentColor(p.currentColor);
        chessboard.setCurrentColor(chessboard.getCurrentColor() == ChessColor.BLACK ? ChessColor.RED : ChessColor.BLACK);
        chessboard.redPoints = p.redPoints;
        chessboard.blackPoints = p.blackPoints;

        System.out.println(p.currentColor);
        System.out.println("red: " + p.redPoints);
        System.out.println("black:" + p.blackPoints ) ;
        for(int i = 0; i < p.chesses.length; i ++){
            for(int j = 0; j < p.chesses[i].length; j ++){
                System.out.println(""+p.chesses[i][j].isReversal +  p.chesses[i][j].type);
            }
        }

        for(int i = 0; i < p.chesses.length; i ++){
            for(int j = 0; j < p.chesses[i].length; j ++){
                p.chesses[i][j].setVisible(true);
                chessboard.putChessOnBoard(p.chesses[i][j]);
            }
        }
        System.out.println();
    }

    public void loadGameFromProcedure1(){
        /**
         * 悔棋用的
         * 加载procedure的最后一次棋盘
         */
        List<String> chessData;
        try {
            chessData = Files.readAllLines(Paths.get("D:\\java代码\\IdeaProjects\\final project\\src\\save\\tt.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        readProcedure(chessData);
        procedures.remove(procedures.size()-1);
        if(procedures.size() < 2){
            return;
        }
        Procedure p = procedures.get(procedures.size()-1);
        chessboard.setCurrentColor(p.currentColor);
        chessboard.setCurrentColor(chessboard.getCurrentColor() == ChessColor.BLACK ? ChessColor.RED : ChessColor.BLACK);
        chessboard.redPoints = p.redPoints;
        chessboard.blackPoints = p.blackPoints;

        System.out.println(p.currentColor);
        System.out.println("red: " + p.redPoints);
        System.out.println("black:" + p.blackPoints ) ;
        for(int i = 0; i < p.chesses.length; i ++){
            for(int j = 0; j < p.chesses[i].length; j ++){
                System.out.println(""+p.chesses[i][j].isReversal +  p.chesses[i][j].type);
            }
        }

        for(int i = 0; i < p.chesses.length; i ++){
            for(int j = 0; j < p.chesses[i].length; j ++){
                p.chesses[i][j].setVisible(true);
                chessboard.putChessOnBoard(p.chesses[i][j]);
            }
        }
        System.out.println();
    }

    public void loadGameFromProcedure2(){
        /**
         * 加载棋盘用的
         * 加载procedure的最后一次棋盘
         */

        Procedure p = procedures.get(procedures.size()-1);
        chessboard.setCurrentColor(p.currentColor);
        chessboard.setCurrentColor(chessboard.getCurrentColor() == ChessColor.BLACK ? ChessColor.RED : ChessColor.BLACK);
        chessboard.redPoints = p.redPoints;
        chessboard.blackPoints = p.blackPoints;

        System.out.println(p.currentColor);
        System.out.println("red: " + p.redPoints);
        System.out.println("black:" + p.blackPoints ) ;
        for(int i = 0; i < p.chesses.length; i ++){
            for(int j = 0; j < p.chesses[i].length; j ++){
                System.out.println(""+p.chesses[i][j].isReversal +  p.chesses[i][j].type);
            }
        }

        for(int i = 0; i < p.chesses.length; i ++){
            for(int j = 0; j < p.chesses[i].length; j ++){
                p.chesses[i][j].setVisible(true);
                chessboard.putChessOnBoard(p.chesses[i][j]);
            }
        }
        System.out.println();
    }

    public void recall(){
        /**
         * 一个悔棋的方法，在悔棋按钮被点击的时候调用
         */
        JOptionPane.showMessageDialog(null,"您只有一次悔棋机会","君子落子有信",JOptionPane.WARNING_MESSAGE);
        loadGameFromProcedure1();
    }

    public void endgame(){
        //清空棋局，清空procedures
        procedures.clear();
        steps.clear();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\java代码\\IdeaProjects\\final project\\src\\save\\tt.txt"));
            bw.write(new String(""));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFile(String path){
        /**
         * 这是一个将tt的内容保存到其他文件的方法，也就是存档
         * 直接在点击不同按钮的效果那里，调用这个方法，然后令path等于相应的路径即可
         * 如果点击存档1，地址就是 D:\java代码\IdeaProjects\Class\Demo\src\save\t1.txt
         * 存档2 地址 D:\java代码\IdeaProjects\Class\Demo\src\save\t2.txt
         * 存档3 地址 D:\java代码\IdeaProjects\Class\Demo\src\save\t3.txt
         */
        try {
            BufferedReader br = new BufferedReader(new FileReader("D:\\java代码\\IdeaProjects\\final project\\src\\save\\tt.txt"));
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            String s = "";
            while((s = br.readLine()) != null){
                bw.write(s + "\n");
            }
            bw.flush();
            bw.close();
            bw.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //判断棋盘是否是4*8，错误编码 102
    public boolean testChessboardStructure(){
        for(int i = 0; i < steps.size(); i++){
            ArrayList<String> history = new ArrayList<>();
            history = steps.get(i);
            if(history.size() < 7){
                JOptionPane.showMessageDialog(null,"错误代码102：棋盘格式错误","警告！",JOptionPane.WARNING_MESSAGE);
                return false;
            }
            String[][] chesses = new String[4][];
            for(int j = 1; j <= 4; j++){
                chesses[j-1] = history.get(j).split(" ");
            }//这里是用chesses 把棋盘上每个子的缩写都保存下来了
            for(int j = 0; j < chesses.length; j ++){
                if(chesses[j].length != 8){
                    JOptionPane.showMessageDialog(null,"错误代码102：棋盘格式错误","警告！",JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean testCurrentcolor(){
        for(int i =0; i < steps.size(); i++){
            if(!steps.get(0).equals(ChessColor.RED.getName()) && !steps.get(0).equals(ChessColor.BLACK.getName())){
                JOptionPane.showMessageDialog(null,"错误代码104：缺少行棋方","警告！",JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public void testStep(){
        /**
         * 获取每一个棋盘
         * 得出移动的棋子（不是被吃掉的棋子）
         * 判断移动的棋子的这个移动在不在他的行动canwalkto范围之内
         * 如果不在的话，就报错棋盘行棋步骤错误
         */
        for(int i = 0; i < procedures.size()-1; i++){
            ArrayList<SquareComponent> positionChange = new ArrayList<>();
            SquareComponent[][] cc = procedures.get(i).chesses;
            SquareComponent[][] cf = procedures.get(i+1).chesses;
            for(int j = 0; j < cc.length; j++){
                for(int k = 0; k < cc[j].length; k++){
                    if(cc[j][k] != cf[j][k]){
                        positionChange.add(cc[j][k]);
                    }
                }
            }
            if(positionChange.size() >= 3){
                //跳出弹窗： 第i次行棋步骤错误
               return;
            }
            for(int j = 0; j < positionChange.size(); j++){
                ArrayList<ChessboardPoint> can = positionChange.get(j).canWalkTo(cc);
                int count = 0;
                for(int k = 0; k < positionChange.size(); k++){
                    if(can.get(k).getX() == positionChange.get(j).getChessboardPoint().getX()
                    && can.get(k).getY() == positionChange.get(j).getChessboardPoint().getY()){
                        count++;
                    }
                }
                if(count == 0){
                    return;
                }
            }
        }
    }

    public void method(){
        testCurrentcolor();
    }
}
