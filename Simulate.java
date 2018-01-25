import java.util.*;

public class Simulate {

    private String[] board;
    private String oppColor, aiColor = "black", manColor = "white";
    private List<Integer> validCellForMove = new ArrayList<>();
    int[][] arrFlip = new int[100][40];
    List<Integer> arrToBeFlip = new ArrayList();

    int counter2, depth = 0;

    int[][] arrTemp1 = new int[100][40];

    String[] child1;
    List<String> child2 = new ArrayList<>();
    List<String> child3 = new ArrayList<>();

    public Simulate(String[] board, List<Integer> validCellForMove, int[][] arrFlip) {
        this.board = board;
        this.validCellForMove = validCellForMove;
        this.arrFlip = arrFlip;

        Start();
    }

    public int DisplayValuesOfTree() {
        
        // root
        Tree R0 = new Tree(-1, -1);
        
        List<Tree> lvl1 = new ArrayList<>();
        
        // level 1
        for( String x : child1 ) {
            String[] xs = x.split(",");
            lvl1.add( R0.addChild( Integer.parseInt(xs[0]), Integer.parseInt(xs[1]) ) );
        }
        
        List<Tree> lvl2 = new ArrayList<>();
        
        // level 2
        for( String x : child2 ) {
            String[] xs = x.split(",");
               
            // find parent
            for( Tree t : lvl1 ) {
                if( Integer.parseInt(xs[0]) == t.index ) {
                    lvl2.add( t.addChild( Integer.parseInt(xs[1]), Integer.parseInt(xs[2]) ) );
                }
            }
        }
        
        List<Tree> lvl3 = new ArrayList<>();
        
        // level 3
        for( String x : child3 ) {
            String[] xs = x.split(",");
               
            // find parent
            for( Tree t : lvl2 ) {
                if( Integer.parseInt(xs[0]) == t.index ) {
                    lvl3.add( t.addChild( Integer.parseInt(xs[1]), Integer.parseInt(xs[2]) ) );
                }
            }
        }
        
        try {
            Tree maxNode = Tree.dfs(R0, R0);
            return maxNode.parent.parent.index;
        } catch(NullPointerException e) {
            return -1;
        }
        
    }

    private void Start() {
        for (int x = 0; x < validCellForMove.size(); x++) {
            for (int y = 0; y < arrFlip[x].length; y++) {
                arrTemp1[validCellForMove.get(x)][y] = arrFlip[validCellForMove.get(x)][y];
            }
        }
        child1 = counter(arrTemp1);

        for (int x = 0; x < validCellForMove.size(); x++) {
            aiMove(validCellForMove.get(x));

        }

    }

    public String[] counter(int[][] arr) {
        String str[] = new String[validCellForMove.size()];
        int ctr = 0;
        for (int x = 0; x < 100; x++) {
            if (validCellForMove.contains(x)) {
                int counter = 0;

                for (int z = 0; z < 40; z++) {
                    if (arrTemp1[x][z] != 0) {
                        counter++;
                    }
                }
                str[ctr] = x + "," + counter;
                ctr++;
            }

        }
        return str;
    }

    public void printOnBoard(String color, int index) {
        board[index] = color;
    }

    public void validMove(String[] board1, String plyrColor, int index) {
        validCellForMove.clear();

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
                counter2 = 0;
                Set<Integer> set = new HashSet<>();
                set.addAll(arrToBeFlip);
                arrToBeFlip.clear();
                arrToBeFlip.addAll(set);
                for (int y = 0; y < arrToBeFlip.size(); y++) {
                    arrFlip[ctr][y] = arrToBeFlip.get(y);
                    counter2++;
                }
                arrToBeFlip.clear();
                if (counter2 != 0) {
                    if (depth == 1) {

                        child2.add(index + "," + ctr + "," + counter2);

                    } else if (depth == 2) {
                        child3.add(index + "," + ctr + "," + counter2);
                    }
                }

            }
        }

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

    public void aiMove(int index) {

        depth = 1;
        int[] arrTemp;
        String boardTemp[] = Arrays.copyOf(board, board.length);
        List<Integer> DvalidCellForMove = new ArrayList<>();
        DvalidCellForMove.addAll(validCellForMove);
        int[][] intTemp = new int[arrFlip.length][];

        board[index] = aiColor;
        arrTemp = convert(arrFlip, index);

        validCellForMove.remove(validCellForMove.indexOf(index));
        reversePiece(arrTemp, aiColor);

        validMove(board, manColor, index);

        for (int i = 0; i < arrFlip.length; i++) {
            intTemp[i] = arrFlip[i].clone();
        }

        String boardTemp2[] = Arrays.copyOf(board, board.length);
        List<Integer> DvalidCellForMove2 = new ArrayList<>();
        DvalidCellForMove2.addAll(validCellForMove);

        for (int x : DvalidCellForMove2) {
            DplayerMove(board, x, arrFlip);
            for (int i = 0; i < arrFlip.length; i++) {
                arrFlip[i] = intTemp[i].clone();
            }
            board = Arrays.copyOf(boardTemp2, boardTemp2.length);
        }

        DvalidCellForMove2.clear();
        board = Arrays.copyOf(boardTemp, boardTemp.length);
        validCellForMove.clear();
        validCellForMove.addAll(DvalidCellForMove);
    }

    public void DplayerMove(String[] board1, int index, int[][] arrFlip) {

        int[] arrTemp;
        depth = 2;
        board1[index] = manColor;
        arrTemp = convert(arrFlip, index);

        reversePiece(arrTemp, manColor);
        validMove(board1, aiColor, index);

    }

    public void reversePiece(int[] arrToBeFlip, String mover) {

        for (int ctr = 0; ctr < arrToBeFlip.length; ctr++) {
            if (arrToBeFlip[ctr] != 0) {
                printOnBoard(mover, arrToBeFlip[ctr]);

            }
        }
    }

    public int[] convert(int[][] arrTemp, int x) {

        int[] arrNum = new int[arrTemp[x].length];
        for (int ctr = 0; ctr < arrTemp[x].length; ctr++) {
            if (arrTemp[x][ctr] != 0) {
                arrNum[ctr] = arrTemp[x][ctr];
            }
        }
        return arrNum;
    }
}

