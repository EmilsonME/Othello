/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.util.*;

/**
 *
 * @author Emilson
 */
public class Othello implements ActionListener {

    private JFrame mainFrame;
    private JPanel boardPanel, scorePanel;
    private GridLayout layout;
    private JButton[] cell = new JButton[100];
    private List validSquare;
    private String[] board;
    private String oppColor, aiColor = "black", manColor = "white", mover;
    private List<Integer> validCellForMove = new ArrayList<Integer>();
    //  private Hashtable hash = new Hashtable<Integer, ArrayList<Integer>>();
    int[][] arrFlip = new int[100][40];
    int[][] arrFlip2 = new int[100][40];
    List<Integer> arrToBeFlip = new ArrayList();
    boolean gameOver = false;
    Thread t1 = new Thread();
    private JLabel blckLbl, whtLbl, blckScore, whtScore;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Othello game = new Othello();
        game.initialBoard();

    }

    public Othello() {
        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Othello");
        mainFrame.setLayout(null);
        mainFrame.setSize(550, 638);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);

        layout = new GridLayout(10, 10, 0, 0);
        boardPanel = new JPanel();
        boardPanel.setBounds(18, 5, 500, 500);
        boardPanel.setLayout(layout);

        scorePanel = new JPanel();
        scorePanel.setBounds(18, 510, 500, 80);
        scorePanel.setBackground(Color.gray);
        scorePanel.setLayout(null);
        mainFrame.add(scorePanel);

        blckLbl = new JLabel("Black: ");
        blckLbl.setBounds(100, 8, 100, 70);
        blckLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        scorePanel.add(blckLbl);

        blckScore = new JLabel("2");
        blckScore.setBounds(190, 8, 100, 70);
        blckScore.setFont(new Font("Segoe UI", Font.BOLD, 30));
        scorePanel.add(blckScore);

        whtLbl = new JLabel("White: ");
        whtLbl.setBounds(275, 8, 160, 70);
        whtLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
        scorePanel.add(whtLbl);

        whtScore = new JLabel("2");
        whtScore.setBounds(375, 8, 100, 70);
        whtScore.setFont(new Font("Segoe UI", Font.BOLD, 30));
        scorePanel.add(whtScore);

        validSquare = new ArrayList();
        board = new String[100];
        try {
            Image img = ImageIO.read(getClass().getResource("white2.png"));
            Image img2 = ImageIO.read(getClass().getResource("black2.png"));
            for (int x = 0; x < 100; x++) {

                if (x >= 11 && x < 89 && x % 10 != 0 && x % 10 != 9) {
                    boardPanel.add(cell[x] = new JButton(""));
                    cell[x].setIcon(new ImageIcon(img));
                    cell[x].setEnabled(false);
                    cell[x].setDisabledIcon(cell[x].getIcon());
                    cell[x].addActionListener(this);
                    cell[x].setContentAreaFilled(false);

                    validSquare.add(x);
                    board[x] = "empty";
                } else {
                    boardPanel.add(cell[x] = new JButton(" "));
                    cell[x].setIcon(new ImageIcon(img2));
                    cell[x].setBorderPainted(false);
                    cell[x].setFocusPainted(false);
                    cell[x].setEnabled(false);
                    cell[x].setDisabledIcon(cell[x].getIcon());
                    board[x] = "X";

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainFrame.add(boardPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int ctr = 0; ctr < 100; ctr++) {
            if (ctr >= 11 && ctr < 89 && ctr % 10 != 0 && ctr % 10 != 9) {
                if (e.getSource() == cell[ctr]) {
                    playerMove(ctr);
                }
            }
        }
    }

    public void initialBoard() {

        printOnBoard("white", 44);
        printOnBoard("black", 45);
        printOnBoard("black", 54);
        printOnBoard("white", 55);
        validMove(board, "white");

    }

    public void printOnBoard(String color, int index) {
        try {
            Image img = ImageIO.read(getClass().getResource(color + ".png"));
            cell[index].setIcon(new ImageIcon(img));
            cell[index].setFocusPainted(false);
            cell[index].setEnabled(false);
            cell[index].setDisabledIcon(cell[index].getIcon());
            board[index] = color;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validMove(String[] board1, String plyrColor) {
        //remove previous validMoves indicator
        try {
            Image img = ImageIO.read(getClass().getResource("white2.png"));
            if (!validCellForMove.isEmpty()) {
                for (int ctr = 0; ctr < validCellForMove.size(); ctr++) {

                    cell[validCellForMove.get(ctr)].setIcon(new ImageIcon(img));
                    cell[validCellForMove.get(ctr)].setFocusPainted(false);
                    cell[validCellForMove.get(ctr)].setEnabled(false);
                    cell[validCellForMove.get(ctr)].setDisabledIcon(cell[validCellForMove.get(ctr)].getIcon());

                }
                validCellForMove.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int temp;
        int[] direction = {-10, 10, -1, 1, -11, -9, 9, 11};//up,down,left,right,up-left, up-right, down-left, down-right
        for (int ctr = 0; ctr < 100; ctr++) {
            if ("empty".equals(board1[ctr])) {
                for (int ctr2 = 0; ctr2 < 8; ctr2++) {
                    temp = findBracket(plyrColor, board1, direction[ctr2], ctr);
                    if (temp != 0) {
                        if (!validCellForMove.contains(temp)) {
                            validCellForMove.add(temp);
                        }
                    }
                }
                Set<Integer> set = new HashSet<>();
                set.addAll(arrToBeFlip);
                arrToBeFlip.clear();
                arrToBeFlip.addAll(set);
                for (int y = 0; y < arrToBeFlip.size(); y++) {
                    arrFlip[ctr][y] = arrToBeFlip.get(y);
                   // arrFlip2[ctr][y] = arrToBeFlip.get(y);
                    // System.out.println(ctr + ":" + arrFlip[ctr][y]);
                }
                arrToBeFlip.clear();
            }
        }
        viewValidMoves(validCellForMove);
    }

    public int findBracket(String plyrColor, String[] board2, int dir, int looper) {
        int temp = 0, flag = looper, ctr = 0;

        ArrayList<Integer> arrStoreTemp = new ArrayList();

        if (plyrColor.equals("white")) {
            oppColor = "black";
        } else {
            oppColor = "white";
        }

        if (board[looper + dir].equals(oppColor)) {
            looper += dir;
            while (board[looper].equals(oppColor)) {
                arrStoreTemp.add(looper);
                looper += dir;
                ctr++;
            }
            if (board[looper].equals(plyrColor)) {
                temp = flag;
                arrToBeFlip.addAll(arrStoreTemp);
            }
        } else {
            return 0;
        }
        return temp;
    }

    public void viewValidMoves(List<Integer> validMove) {
        for (int looper = 0; looper < validMove.size(); looper++) {
            try {
                Image img = ImageIO.read(getClass().getResource("highlight.png"));
                cell[validMove.get(looper)].setEnabled(true);
                cell[validMove.get(looper)].setIcon(new ImageIcon(img));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playerMove(int x) {
        mover = manColor;
        int[] arrTemp;

        printOnBoard(mover, x);
        //System.out.println(hash.get(x));
        arrTemp = convert(arrFlip, x);
        for(int z = 0; z<100;z++ ){
            for (int y = 0; y < arrFlip[z].length; y++) {
                // System.out.println(arrFlip[x][y]);
                arrFlip[z][y] = 0;
            }
        }

        validCellForMove.remove(validCellForMove.indexOf(x));

        reversePiece(arrTemp, mover);
        scoreCounter();

        validMove(board, aiColor);
        if (!validCellForMove.isEmpty()) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        aiThink();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            int counter = 0;
            for (int y = 0; y < 100; y++) {
                if ((y >= 11 && y < 89 && y % 10 != 0 && y % 10 != 9)) {
                    if (board[y].equals("black") || board[y].equals("white")) {
                        counter++;
                    }
                }
            }
            if (counter == 64) {
                System.out.print("game OVER");
                winnerChecker();
            } else {
                JOptionPane.showMessageDialog(null, "No Valid Moves For AI. Change of turn.", "Sorry", JOptionPane.INFORMATION_MESSAGE);
                validMove(board, manColor);
            }
        }
    }

    public int[] convert(int[][] arrTemp, int x) {
        // System.out.println(arrTemp[x].length);
        int[] arrNum = new int[arrTemp[x].length];
        for (int ctr = 0; ctr < arrTemp[x].length; ctr++) {
            arrNum[ctr] = arrTemp[x][ctr];
        }
        return arrNum;

    }

    public void reversePiece(int[] arrToBeFlip, String mover) {

        for (int ctr = 0; ctr < arrToBeFlip.length; ctr++) {
            if (arrToBeFlip[ctr] != 0) {
                printOnBoard(mover, arrToBeFlip[ctr]);
            }
        }
    }

    public void aiThink() {

        String boardTemp[] = Arrays.copyOf(board, board.length);
        List<Integer> TempvalidCellForMove = new ArrayList<>();
        TempvalidCellForMove.addAll(validCellForMove);
        int[][] intTemp = new int[arrFlip.length][];
            for (int i = 0; i < arrFlip.length; i++) {
            intTemp[i] = arrFlip[i].clone();
        }

        Simulate sim = new Simulate(boardTemp, TempvalidCellForMove, intTemp);
        int move = sim.DisplayValuesOfTree();
        
        if(move == -1) {
            move = DisplayValuesOfTreee();
            aiMove(move);
        }
        
        if(validCellForMove.contains(move)){
            aiMove( move );
        } else {

            move = DisplayValuesOfTreee();
            aiMove(move);
        }
    }

    public void aiMove(int index) {
        mover = aiColor;
        int[] arrTemp;

        printOnBoard(mover, index);

        arrTemp = convert(arrFlip, index);
       for(int z = 0; z<100;z++ ){
            for (int y = 0; y < arrFlip[z].length; y++) {
                // System.out.println(arrFlip[x][y]);
                arrFlip[z][y] = 0;
            }
        }

        validCellForMove.remove(validCellForMove.indexOf(index));
        reversePiece(arrTemp, mover);
        scoreCounter();
        validMove(board, manColor);
        if (validCellForMove.isEmpty()) {
            int counter = 0;
            for (int y = 0; y < 100; y++) {
                if ((y >= 11 && y < 89 && y % 10 != 0 && y % 10 != 9)) {
                    if (board[y].equals("black") || board[y].equals("white")) {
                        counter++;
                    }
                }
            }
            if (counter == 64) {
                System.out.print("game OVER");
                winnerChecker();
            } else {
                JOptionPane.showMessageDialog(null, "No Valid Moves for MAN. Change of turn[Ai turn ].", "Sorry", JOptionPane.INFORMATION_MESSAGE);
                validMove(board, aiColor);
                aiThink();
            }
        }
    }
    
    public int DisplayValuesOfTreee() {
        

        Random treeVal = new Random();

        int treeVal2 = treeVal.nextInt(validCellForMove.size());
        return validCellForMove.get(treeVal2);
        
    }

    public void scoreCounter() {
        int whtCounter = 0, blckCounter = 0;
        for (int counter = 0; counter < 100; counter++) {
            if (counter >= 11 && counter < 89 && counter % 10 != 0 && counter % 10 != 9) {
                if (board[counter].equals("white")) {
                    whtCounter++;
                } else if (board[counter].equals("black")) {
                    blckCounter++;
                }
            }
        }
        whtScore.setText(Integer.toString(whtCounter));
        blckScore.setText(Integer.toString(blckCounter));
    }
    public void winnerChecker(){
        String winner;
        int wScore = Integer.parseInt(whtScore.getText());
        int bScore = Integer.parseInt(blckScore.getText());
        
        if(wScore == bScore){
             JOptionPane.showMessageDialog(null, "Draw!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
         winner = (bScore > wScore) ? "AI" : "YOU";        
        JOptionPane.showMessageDialog(null, winner+" win!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
