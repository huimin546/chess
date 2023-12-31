package controller;


import chessComponent.SquareComponent;
import chessComponent.EmptySlotComponent;
import chessComponent.chessLibrary.CannonChessComponent;
import model.ChessColor;
import model.ChessboardPoint;
import ui.ChessGameFrame;
import ui.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ClickController {
    private final Chessboard chessboard;
    private SquareComponent first;
    public Saver saver;

    public int count = 0;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
        saver = new Saver(chessboard, this);
    }


    public void onClick(SquareComponent squareComponent) {
        //判断第一次点击
        if (first == null) {
            if (handleFirst(squareComponent)) {
                squareComponent.setSelected(true);
                first = squareComponent;
                first.repaint();
                //下面这个方法返回的是一个能走的格子的数组，需要GUI高亮边框
                for (ChessboardPoint i : first.canWalkTo(chessboard.getChessComponents())) {
                    chessboard.squareComponents[i.getX()][i.getY()].squareColor = new Color(191, 120, 46);
                    chessboard.squareComponents[i.getX()][i.getY()].repaint();
                }
            }
        } else {
            if (first == squareComponent) { // 再次点击取消选取
                squareComponent.setSelected(false);
                SquareComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
                for(int i = 0 ; i <4; i ++){
                    for(int j = 0; j< 8; j++){
                        chessboard.squareComponents[i][j].squareColor = new Color(236, 143, 44);
                        chessboard.squareComponents[i][j].repaint();
                    }
                }
            } else if (handleSecond(squareComponent)) {
                //repaint in swap chess method.
                //这里要增加记录行棋步骤，交换棋子（如果second是空棋子，就直接记录，如果不是空棋子，就加一个棋子被吃掉
                chessboard.swapChessComponents(first, squareComponent);
                swapPlayer();
                first.setSelected(false);
                first = null;
            }else{
                JOptionPane.showMessageDialog(null,"您不能这么走","警告！",JOptionPane.WARNING_MESSAGE);
                //可写可不写：跳出弹框：违规步骤
            }
        }

    }


    /**
     * @param squareComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(SquareComponent squareComponent) {
        if (!squareComponent.isReversal()) {
            squareComponent.setReversal(true);
            System.out.printf("onClick to reverse a chess [%d,%d]\n", squareComponent.getChessboardPoint().getX(), squareComponent.getChessboardPoint().getY());
            count ++;
            theBeginner(squareComponent.chessColor);
            squareComponent.repaint();
            //squareComponent.paintImmediately(squareComponent.getBounds());
            //这里要增加一个 计入内存 行棋步骤：翻开棋子
            swapPlayer();
            return false;
        }
        return squareComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param squareComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(SquareComponent squareComponent) {

        //第二次点击没翻开或空棋子，进入if
        if (!squareComponent.isReversal()) {
            if (!(squareComponent instanceof EmptySlotComponent)) {
                //不是空棋子，也就是没翻开黑或红棋子，只有炮可以走没翻开的红黑棋子
                if(first instanceof CannonChessComponent){
                    return first.canMoveTo(chessboard.getChessComponents(), squareComponent.getChessboardPoint());
                }
                return false;
            }
        }
        //空棋子或者翻开的黑红棋子
        return first.canMoveTo(chessboard.getChessComponents(), squareComponent.getChessboardPoint());
    }

    public void swapPlayer() {
        saver.saveProcedure();
        chessboard.setCurrentColor(chessboard.getCurrentColor() == ChessColor.BLACK ? ChessColor.RED : ChessColor.BLACK);
        ChessGameFrame.getStatusLabel().setText(String.format("%s's TURN", chessboard.getCurrentColor().getName()));
        int[] blackeaten = classifyblack();
        int[] redeaten = classifyred();
        ChessGameFrame.scoreLabel.setText(String.format("%d : %d", chessboard.blackPoints, chessboard.redPoints));
        ChessGameFrame.scoreLabel1.setText(blackeaten[7]+"");
        ChessGameFrame.scoreLabe.setText(blackeaten[6]+"");
        ChessGameFrame.scoreLabl.setText(blackeaten[5]+"");
        ChessGameFrame.scoreLael.setText(blackeaten[4]+"");
        ChessGameFrame.scoreLbel.setText(blackeaten[3]+"");
        ChessGameFrame.scoreabel.setText(blackeaten[2] +"");
        ChessGameFrame.scorLabel.setText(blackeaten[1] +"");

        ChessGameFrame.scoreLab.setText(redeaten[7]+"");
        ChessGameFrame.scoreLa.setText(redeaten[6]+"");
        ChessGameFrame.scoreLb.setText(redeaten[5]+"");
        ChessGameFrame.scoreab.setText(redeaten[4]+"");
        ChessGameFrame.scorLab.setText(redeaten[3]+"");
        ChessGameFrame.scoeLab.setText(redeaten[2] +"");
        ChessGameFrame.screLab.setText(redeaten[1] +"");

        for(int i = 0 ; i <4; i ++){
            for(int j = 0; j< 8; j++){
                chessboard.squareComponents[i][j].squareColor = new Color(236, 143, 44);
                chessboard.squareComponents[i][j].repaint();
            }
        }
        chessboard.points();
        chessboard.trigger();
        chessboard.endGame();
    }

    public void theBeginner(ChessColor cs){
        if(count == 1){
            chessboard.setCurrentColor(cs);
            if(chessboard.mode.equals("bot")){
                chessboard.playerAColor = cs;
                if( chessboard.playerAColor== ChessColor.BLACK){
                    chessboard.robotColor = ChessColor.RED;
                }else{
                    chessboard.robotColor = ChessColor.BLACK;
                }
            }
        }
    }

    public int[] classifyblack(){
        chessboard.redPoints = 0;
        int[] eatenblack =  new int[8];
        for(int i = 0; i < chessboard.blackDead.size(); i++){
            switch (chessboard.blackDead.get(i).type){
                case 1 -> {
                    chessboard.redPoints += 1;
                    eatenblack[chessboard.blackDead.get(i).type] += 1;  //兵加一
                }
                case 2 -> {
                    chessboard.redPoints += 5;
                    eatenblack[chessboard.blackDead.get(i).type] += 1;
                }
                case 3 -> {
                    chessboard.redPoints += 5;
                    eatenblack[chessboard.blackDead.get(i).type] += 1;
                }
                case 4 -> {
                    chessboard.redPoints += 5;
                    eatenblack[chessboard.blackDead.get(i).type] += 1;
                }
                case 6 -> {
                    chessboard.redPoints += 10;
                    eatenblack[chessboard.blackDead.get(i).type] += 1;
                }
                case 7 -> {
                    chessboard.redPoints += 30;
                    eatenblack[chessboard.blackDead.get(i).type] += 1;
                }
            }
        }
        return eatenblack;
    }

    public int[] classifyred(){
        chessboard.blackPoints = 0;
        int[] eatenred =  new int[8];
        for(int i = 0; i < chessboard.redDead.size(); i++){
            switch (chessboard.redDead.get(i).type){
                case 1 -> {
                    chessboard.blackPoints += 1;
                    eatenred[chessboard.redDead.get(i).type] += 1;  //兵加一
                }
                case 2 -> {
                    chessboard.blackPoints += 5;
                    eatenred[chessboard.redDead.get(i).type] += 1;
                }
                case 3 -> {
                    chessboard.blackPoints += 5;
                    eatenred[chessboard.redDead.get(i).type] += 1;
                }
                case 4 -> {
                    chessboard.blackPoints += 5;
                    eatenred[chessboard.redDead.get(i).type] += 1;
                }
                case 6 -> {
                    chessboard.blackPoints += 10;
                    eatenred[chessboard.redDead.get(i).type] += 1;
                }
                case 7 -> {
                    chessboard.blackPoints += 30;
                    eatenred[chessboard.redDead.get(i).type] += 1;
                }
            }
        }
        return eatenred;
    }
}
