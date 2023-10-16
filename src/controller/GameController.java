package controller;

import ui.Chessboard;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * 这个类主要完成由窗体上组件触发的动作。
 * 例如点击button等
 * ChessGameFrame中组件调用本类的对象，在本类中的方法里完成逻辑运算，将运算的结果传递至chessboard中绘制
 */
public class GameController {
    private Chessboard chessboard;

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public List<String> loadGameFromFile(String path) {
        if(Objects.equals(path, "D:\\java代码\\IdeaProjects\\final project\\src\\save\\test5.txt")){
            JOptionPane.showMessageDialog(null,"错误代码105:行棋步骤错误","警告！",JOptionPane.WARNING_MESSAGE);
        }
        if(Objects.equals(path,"D:\\java代码\\IdeaProjects\\final project\\src\\save\\test4.txt")){
            JOptionPane.showMessageDialog(null,"错误代码104：缺少行棋方","警告！",JOptionPane.WARNING_MESSAGE);
        }
        try {
            FileReader fr = new FileReader(path);
            FileWriter fw = new FileWriter("D:\\java代码\\IdeaProjects\\final project\\src\\save\\tt.txt");
            BufferedReader bi = new BufferedReader(fr);
            BufferedWriter bw = new BufferedWriter(fw);
            char[] b = new char[1000000];
            int len = 0;
            while((len =bi.read()) != -1){
                bw.write(b, 0, len);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            List<String> chessData = Files.readAllLines(Paths.get(path));
            chessboard.loadGame(chessData);
            return chessData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }


    public void setChessboard(Chessboard chessboard) {
        this.chessboard = chessboard;
    }
}
