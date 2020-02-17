//Team P1 4
package edu.gsu.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import java.lang.Math;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button[3][3];
    private Handler mHandler;

    private Random random = new Random();
    private boolean player1Turn = true;

    private int roundCount = 0;
    private boolean gameOver = false;
    private boolean gameStart = false;
    private int player1Points;
    private int player2Points;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    private String player1 = "X";
    private String player2 = "O";
    private String p1_human = "P1: HUMAN";
    private String p1_cpu = "P1: CPU";
    private String p2_human = "P2: HUMAN";
    private String p2_cpu = "P2: CPU";

    private boolean isPlayer1_AI = false;
    private boolean isPlayer2_AI = true;

    private Move bestMove;

    private static class Move {
        private int row, col;

        Move(){
            row = 0;
            col = 0;
        }

        public int getRow(){return row;}
        public int getCol(){return col;}
        public void setRow(int row){this.row = row;}
        public void setCol(int col){this.col = col;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }

        Button buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameStart && isPlayer1_AI && !isPlayer2_AI) {
                    gameStart = true;
                    //textViewPlayer1.setTextColor(0xff77dd77);
                    //startAnim();
                    roundCount++;
                    Move bestMove = new Move();
                    if(roundCount < 2 && buttons[1][1].getText().equals("")){
                        bestMove.setRow(1);
                        bestMove.setCol(1);
                    }else
                        bestMove = findBestMove(getBoardState());
                    buttons[bestMove.getRow()][bestMove.getCol()].setText("X");
                    player1Turn = !player1Turn;
                } if(!gameStart && isPlayer1_AI && isPlayer2_AI){
                    mHandler = new Handler();
                    mHandler.post(mUpdate);
                    gameStart = true;
                } else {
                    gameStart = true;
                }
            }
        });

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBoardState();
                roundCount = 0;
                gameOver = gameStart = false;
                player1Turn = true;
                //stopAnim();
            }
        });

        final Button button_p1 = findViewById(R.id.button_p1_switch);
        button_p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isGameStarted(getBoardState()) && !gameStart){
                    if(!isPlayer1_AI) {
                        isPlayer1_AI = true;
                        button_p1.setText(p1_cpu);
                        //textViewPlayer1.setText("AI1 " + isPlayer1_AI);
                    }
                    else{
                        isPlayer1_AI = false;
                        button_p1.setText(p1_human);
                        //textViewPlayer1.setText("AI1 " + isPlayer1_AI);
                    }
                }
            }
        });

        final Button button_p2 = findViewById(R.id.button_p2_switch);
        button_p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isGameStarted(getBoardState()) && !gameStart){
                    if(!isPlayer2_AI) {
                        isPlayer2_AI = true;
                        button_p2.setText(p2_cpu);
                        //textViewPlayer2.setText("AI2 " + isPlayer2_AI);
                    }
                    else{
                        isPlayer2_AI = false;
                        button_p2.setText(p2_human);
                        //textViewPlayer2.setText("AI2 " + isPlayer2_AI);
                    }
                }
            }
        });
    }

    private void startAnim(){
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        if(player1Turn) {
            textViewPlayer2.clearAnimation();
            textViewPlayer1.startAnimation(anim);
        }
        else {
            textViewPlayer1.clearAnimation();
            textViewPlayer2.startAnimation(anim);
        }
    }

    private void stopAnim(){
        textViewPlayer1.clearAnimation();
        textViewPlayer2.clearAnimation();
    }

    private Runnable mUpdate = new Runnable() {
        public void run() {

            //gameStart = true;
            if (player1Turn) {
                Move bestMove = new Move();
                if(roundCount < 2 && buttons[1][1].getText().equals("")){
                    bestMove.setRow(1);
                    bestMove.setCol(1);
                }else
                    bestMove = findBestMove(getBoardState());
                buttons[bestMove.getRow()][bestMove.getCol()].setText("X");
            } else {
                bestMove = findBestMove(getBoardState());
                buttons[bestMove.getRow()][bestMove.getCol()].setText("O");
            }

            //textViewPlayer2.setText("Player 2: " + roundCount);
            roundCount++;
            if (checkForWin()) {
                if (player1Turn) {
                    player1Wins();
                } else {
                    player2Wins();
                }
            } else if (roundCount == 9) {
                draw();
            } else {
                player1Turn = !player1Turn;
            }
            if(!gameOver)
                mHandler.postDelayed(this, 1000);
        }
    };

    private void onlyAI(){
        //textViewPlayer2.setText("Round: " + roundCount);
        while(roundCount <= 9) {
            gameStart = true;
            if (player1Turn) {
                bestMove = findBestMove(getBoardState());
                buttons[bestMove.getRow()][bestMove.getCol()].setText("X");
            } else {
                bestMove = findBestMove(getBoardState());
                buttons[bestMove.getRow()][bestMove.getCol()].setText("O");
            }

            try {
                //set time in mili
                Thread.sleep(1500);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //textViewPlayer2.setText("Player 2: " + roundCount);
            roundCount++;
            if (checkForWin()) {
                if (player1Turn) {
                    player1Wins();
                    break;
                } else {
                    player2Wins();
                    break;
                }
            } else if (roundCount == 9) {
                draw();
                break;
            } else {
                player1Turn = !player1Turn;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("") || gameOver || !gameStart) {
            return;
        }

        if (player1Turn && !isPlayer1_AI) {
            ((Button) v).setText("X");
            if(!checkForWin()) {
                Move bestMove = new Move();

                if (roundCount < 8 && isPlayer2_AI) {
                    player1Turn = !player1Turn;
                     if(roundCount < 2 && buttons[1][1].getText().equals("")){
                         bestMove.setRow(1);
                         bestMove.setCol(1);
                     }else
                        bestMove = findBestMove(getBoardState());
                    buttons[bestMove.getRow()][bestMove.getCol()].setText("O");
                    roundCount++;
                }
            }
        } else if(!isPlayer2_AI){
            ((Button) v).setText("O");
            if(!checkForWin()) {
                if (roundCount < 8 && isPlayer1_AI) {
                    player1Turn = !player1Turn;
                    bestMove = findBestMove(getBoardState());
                    buttons[bestMove.getRow()][bestMove.getCol()].setText("X");
                    roundCount++;
                }
            }
        }

        roundCount++;
        //textViewPlayer2.setText("Player 2: " + roundCount);
        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            //if(!isPlayer1_AI && !isPlayer2_AI)
                player1Turn = !player1Turn;
        }

    }

    private String[][] getBoardState(){
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }
        return field;
    }

    private void setBoardState(){

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void messagePlayer(String msg) {

            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_window, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            ((TextView)popupWindow.getContentView().findViewById(R.id.text_view_popup)).setText(msg);
            View random = findViewById(R.id.text_view_p1);
            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
            popupWindow.showAtLocation(random, Gravity.CENTER, 0, 0);

            // dismiss the popup window when touched
            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }
            });

    }

    private void player1Wins() {
        messagePlayer("Player 1 Wins!");
        player1Points++;
        textViewPlayer1.setText("Player 1: " + player1Points);
        gameOver = true;
    }

    private void player2Wins() {
        messagePlayer("Player 2 Wins!");
        player2Points++;
        textViewPlayer2.setText("Player 2: " + player2Points);
        gameOver = true;
    }

    private void draw() {
        messagePlayer("Game ends in DRAW!");
        gameOver = true;
    }

    boolean isGameTied(String[][] board)
    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j].equals(""))
                    return false;
        return true;
    }

    boolean isGameStarted(String[][] board)
    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j].equals("X") || board[i][j].equals("O"))
                    return true;
        return false;
    }

    int evalBoard(String[][] b){
        //Checking Rows for X or O victory.
        for (int row = 0; row < 3; row++)
        {
            if (b[row][0].equals(b[row][1]) && b[row][1].equals(b[row][2]))
            {
                if(isPlayer2_AI && !player1Turn) {
                    if (b[row][0].equals(player2))
                        return +10;
                    else if (b[row][0].equals(player1))
                        return -10;
                }else if(isPlayer1_AI && player1Turn) {
                    if (b[row][0].equals(player1))
                        return +10;
                    else if (b[row][0].equals(player2))
                        return -10;
                }
            }
        }

        //Checking Columns for X or O victory.
        for (int col = 0; col < 3; col++)
        {
            if (b[0][col].equals(b[1][col]) && b[1][col].equals(b[2][col]))
            {
                if(isPlayer2_AI && !player1Turn) {
                    if (b[0][col].equals(player2))
                        return +10;
                    else if (b[0][col].equals(player1))
                        return -10;
                }else if(isPlayer1_AI && player1Turn) {
                    if (b[0][col].equals(player1))
                        return +10;
                    else if (b[0][col].equals(player2))
                        return -10;
                }
            }
        }

        //Checking Diagonals for X or O victory.
        if (b[0][0].equals(b[1][1]) && b[1][1].equals(b[2][2]))
        {
            if(isPlayer2_AI && !player1Turn){
                if(b[0][0].equals(player2))
                    return +10;
                else if(b[0][0].equals(player1))
                    return -10;
            }else if(isPlayer1_AI && player1Turn){
                if(b[0][0].equals(player1))
                    return +10;
                else if(b[0][0].equals(player2))
                    return -10;
            }
        }

        if (b[0][2].equals(b[1][1]) && b[1][1].equals(b[2][0]))
        {
            if(isPlayer2_AI && !player1Turn){
                if(b[0][2].equals(player2))
                    return +10;
                else if(b[0][2].equals(player1))
                    return -10;
            }else if(isPlayer1_AI && player1Turn){
                if(b[0][2].equals(player1))
                    return +10;
                else if(b[0][2].equals(player2))
                    return -10;
            }
        }

        //if none of them have won then return 0
        return 0;
    }

    int minimax(String[][] board, int depth, boolean isMax)
    {
        int score = evalBoard(board);

        //If Maximizer has won the game return his/her evaluated score
        if (score == 10)
            return score - depth;

        //If Minimizer has won the game return his/her evaluated score
        if (score == -10)
            return score + depth;

        //If there are no more moves and no winner then it is a tie
        if (isGameTied(board))
            return 0;

        //If this is maximizer's move
        if (isMax)
        {
            int best = -1000;

            // Traverse all cells
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    //Check if cell is empty
                    if (board[i][j].equals(""))
                    {
                        //Make the move
                        if(isPlayer2_AI && !player1Turn)
                            board[i][j] = player2;
                        else if(isPlayer1_AI && player1Turn)
                            board[i][j] = player1;

                        //Call minimax recursively and choose the maximum value
                        best = Math.max(best, minimax(board, depth+1, isMax = !isMax));

                        //Undo the move
                        board[i][j] = "";
                    }
                }
            }
            return best;
        }

        //If this is minimizer's move
        else
        {
            int best = 1000;

            //Traverse all cells
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    //Check if cell is empty
                    if (board[i][j].equals(""))
                    {
                        if(isPlayer2_AI && !player1Turn)
                            board[i][j] = player1;
                        else if(isPlayer1_AI && player1Turn)
                             board[i][j] = player2;

                        //Call minimax recursively and choose the minimum value
                        best = Math.min(best, minimax(board, depth+1, isMax = !isMax));

                        //Undo the move
                        board[i][j] = "";
                    }
                }
            }
            return best;
        }
    }

    //This will return the best possible move for the player
    Move findBestMove(String[][] board)
    {
        int bestVal = -1000;
        Move bestMove = new Move();
        bestMove.setRow(-1);
        bestMove.setCol(-1);

        //Traverse all cells, evaluate minimax function for
        //all empty cells. And return the cell with optimal value
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                //Check if cell is empty
                if (board[i][j].equals(""))
                {
                    if(isPlayer2_AI && !player1Turn)
                        board[i][j] = player2;
                    else if(isPlayer1_AI && player1Turn)
                        board[i][j] = player1;

                    //compute evaluation function for this move.
                    int moveVal = minimax(board, 0, false);

                    //Undo the move
                    board[i][j] = "";

                    //If the value of the current move is
                    //more than the best value, then update best
                    if (moveVal > bestVal)
                    {
                        bestMove.setRow(i);
                        bestMove.setCol(j);
                        bestVal = moveVal;
                    }else if (moveVal == bestVal)
                    {
                        String[] randMove = new String[2];
                        randMove[0] = "moveVal";
                        randMove[1] = "bestVal";
                        int randomMove = random.nextBoolean() ? 0 : 1;

                        if(randMove[randomMove].equals("moveVal")) {
                            bestMove.setRow(i);
                            bestMove.setCol(j);
                            bestVal = moveVal;
                        }
                    }
                }
            }
        }

        return bestMove;
    }
}
