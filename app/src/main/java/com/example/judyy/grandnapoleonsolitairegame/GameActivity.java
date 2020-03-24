package com.example.judyy.grandnapoleonsolitairegame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import android.util.*;



public class GameActivity extends AppCompatActivity {

    private int[] location = new int[2];
    public Stack[] stacks = new Stack[53];
    public Card[] cards = new Card[52];
    Context context = this;
    Recorder recorder;
    HintSolver solver;
    public Button pauseButton;
    public Button hintButton;
    public TextView stepCounter;
    public String type = "normal"; // Set default game type to random
    public static int edtStep;
    public static Chronometer edtTime, timer;
    public static Boolean done = false;
    Snackbar mHintSnackbar;

    //temp variable for hint
    public static ArrayList<Card> hintCardsList = new ArrayList<Card>();
    //predefine my colours
    private static int GREEN = Color.argb(123,0,255,0);
    private static int RED = Color.argb(123,255,0,0);
    private static int CELLAR_RED = Color.argb(255,255,0,0);
    private static int numMCTrialsPerBoard = 50;
    private static int mcDEPTH = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game);

        // Set game difficulty
        Intent intent = getIntent();
        type = intent.getStringExtra(DifficultySelectionActivity.EXTRA_MESSAGE);

        //Initialize recorder
        recorder = new Recorder(this);

        //Initialize Hint Solver
        solver = new HintSolver(this);

        //Display card to table

        boolean gameIsWinnable = false;
//        long t = System.currentTimeMillis();

//        while (!gameIsWinnable && System.currentTimeMillis() - t < 1000) {
        while (!gameIsWinnable) {
            generateCardSetup(type, cards);

            if(type.equals("dummy")) break;

            for (int i = 0; i < this.numMCTrialsPerBoard && !gameIsWinnable; i++) {
                Card[] clonedCards = cards.clone();
                Stack[] clonedStacks = new Stack[53];
                generateInitialStackSetup(clonedCards, clonedStacks);
                if (i %2 == 0){
                    gameIsWinnable = mcSimulation(clonedStacks, clonedCards, 1);
                }
                else {
                    gameIsWinnable = mcSimulation(clonedStacks, clonedCards, -1);
                }
            }
        }

        generateInitialStackSetup(cards, stacks);
        displayCards(cards, stacks);

        //zoom button
        final GameLayout gameLayout = findViewById(R.id.zoom_linear_layout);
        final ImageView zoomToggle = findViewById(R.id.zoom_toggle);
        zoomToggle.setImageResource(R.drawable.zoom_btn);
        zoomToggle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean zooming = gameLayout.toggleZooming();
                if (zooming){
                    // Sort of Blue More like Green color filter
                    zoomToggle.setColorFilter(Color.argb(123, 0, 255, 162));
                } else {
                    zoomToggle.clearColorFilter();
                }
            }
        });
        //quit game button
        final ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setImageResource(R.drawable.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Pop-up to confirm quitting game appears
                AlertDialog.Builder quitPopUp = new AlertDialog.Builder(context);
                quitPopUp.setCancelable(true);
                quitPopUp.setTitle("Quit Game");
                quitPopUp.setMessage("Are you sure you want to quit the current game?");
                quitPopUp.setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                quitPopUp.setNegativeButton("No", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = quitPopUp.create();
                dialog.show();

            }
        });

        //undo button
        final ImageView undoBtn = findViewById(R.id.undo_btn);
        undoBtn.setImageResource(R.drawable.undo_btn);
        undoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                recorder.undoOneStep();
            }
        });

        //TODO new hint functionality
        final ImageView hintBtn = findViewById(R.id.hint_btn);
        hintBtn.setImageResource(R.drawable.hint_btn);
        hintBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                System.out.println("CONTEXT IS THIS: " + context);
                Intent victoryScreen = new Intent(context, VictoryScreen.class);
                startActivity(victoryScreen);
//                DragDrop.clearCardColours(cards);
//                //call method to acquire list of moves
//                ArrayList<Pair<Card, Stack>> availableMoves = getMoves(cards,stacks);
//
//                //pick a move to give hint
//                mHintSnackbar = Snackbar.make(gameLayout, R.string.No_Hint, Snackbar.LENGTH_SHORT);
//                if(availableMoves.size() == 0){
//                    mHintSnackbar.show();
//                }
//                else{
//                    int index = new Random().nextInt(availableMoves.size());
//                    Pair<Card, Stack> aMove = availableMoves.get(index);
//                    aMove.first.getImageView().setColorFilter(GREEN);
//                    //if colouring cellar, crashes, since no card in it
//                    if(aMove.second.getLastCard() == null){//case cellar
//                        aMove.second.getImageView().setColorFilter(CELLAR_RED);
//                    }
//                    else{//case normal card
//                        aMove.second.getLastCard().getImageView().setColorFilter(RED);
//                    }
//                    availableMoves.remove(aMove);
//                }
            }
        });

    }
    /**
     * Function to check if a game is playable
     * @return true if a game is winnable from the current state
     */
    private boolean mcSimulation(Stack[] gameStacks, Card[] gameCards, int direction){
                ArrayList<Pair<Card, Stack>> availableMoves = null;
                Random r = new Random();
                DragDrop.setDirection(direction);
                int i=0;
//                ArrayList<String> history = new ArrayList<String>();
                while (true){
                    if (i > mcDEPTH){
//                        printMoveHistory(history);
                        return true;
                    }
                    i++;
                    availableMoves = getMoves(gameCards, gameStacks);
                    if (availableMoves.size() == 0 ){
                        return (DragDrop.isWin(gameStacks));
                    }
                    Pair<Card, Stack> aMove = availableMoves.get(r.nextInt(availableMoves.size()));
                    int currentStackID = aMove.first.getCurrentStackID();
//                    history.add("From: " + gameStacks[currentStackID].getLastCard().toString() + "To " + aMove.second.toString());
                    while(gameStacks[currentStackID].getCurrentCards().size() != 0){
//                        System.out.println("stuff happens?");
                        DragDrop.updateCardOnStacks(
                                gameStacks[currentStackID]
                                ,gameStacks[currentStackID].getLastCard()
                                ,gameStacks[aMove.second.getStackID()]);

                    }
                }
    }

    /**
     * Encodes the cards into an array of arrays
     * @param gameCards
     * @param encodedCards
     */
    private void encodeBoardState(Card[] gameCards, int[][] encodedCards){
        for (int i=0; i<gameCards.length; i++){
            encodedCards[i][0] = gameCards[i].getSuit();
            encodedCards[i][1] = gameCards[i].getNumber();
        }
    }
    /**
     * Decodes the cards into an array of arrays
     * @param gameCards
     * @param encodedCards
     */
    private void decode(int[][] encodedCards, Card[] gameCards){
        for (int i=0; i<gameCards.length; i++){
            gameCards[i] = new Card(encodedCards[i][0], encodedCards[i][1]);
        }
    }
    private void printMoveHistory(ArrayList<String> history){
        DragDrop.setDirection(0);
        for (String s: history){
            System.out.println();
            System.out.println();
            System.out.println("+++++++++++++++NEW MOVE+++++++++++++++++++++++++++++");
            System.out.println(s);
            System.out.println("+++++++++++++++END MOVE+++++++++++++++++++++++++++++");

        }
    }

    /**
     * Function to create a list of moves
     *
     * @return an ArrayList of Pairs (tuples) of cards, first is source, second is destination
     */
    private ArrayList<Pair<Card, Stack>> getMoves(Card[] cardsToCheck, Stack[] stackToCheck) {
        //tuple list of cards to represent moves
        ArrayList<Pair<Card, Stack>> moveList = new ArrayList<Pair<Card, Stack>>();

        for (Card currCard : cardsToCheck) {
            //skip checking the cards that are on the solution stacks
            if (currCard.getCurrentStackID() > 19 && currCard.getCurrentStackID() < 24){
                continue;
            }
            //means that card can be moved
            if (currCard.getCanMove()) {
                //now need to find where
                for (Stack aStack : stackToCheck) {
                    //last card is top card
                    Card topCard = aStack.getLastCard();
                    //ignore empty stacks
                    if (topCard == null) continue;

                    boolean validStack = DragDrop.canStack(aStack.getStackID(), currCard.getCurrentStackID());   // Check if the stack can be stacked.
                    if (validStack && aStack.getStackID() < 44 && DragDrop.compareCardsHint(aStack, currCard)) { //lets just ignore cellar for now
                        Pair<Card, Stack> move = new Pair<Card, Stack>(currCard, aStack);
                        moveList.add(move);
                    }
                }
            }
        }

        if (moveList.size() != 0){
            return moveList;
        }
        //no moves found, check cellar
        else{
            //there's room in cellar
            Stack cellar = stackToCheck[48];
            if(cellar.getLastCard() == null){
                //same logic as before but this time we'll check for cards that are 'by themselves', i.e. not stacked
                for (Card currCard : cardsToCheck) {
                    //skip checking the cards that are on the solution stacks
                    if (currCard.getCurrentStackID() > 19 && currCard.getCurrentStackID() < 24)
                        continue;
                    //means that card can be moved and only 1 card on a stack
                    if (currCard.getCanMove() && stackToCheck[currCard.getCurrentStackID()].getCurrentCards().size() == 1) {
                        Pair<Card, Stack> move = new Pair<Card, Stack>(currCard,cellar);
                        moveList.add(move);
                    }
                }
                return moveList;
            }
            else{
                //this should be empty
                return moveList;
            }
        }

    }
    /**
     * Function to create a list of moves
     *
     * @return an ArrayList of Pairs (tuples) of cards, first is source, second is destination
     */
    private ArrayList<Pair<Card, Stack>> getAllMovesIncludingCellar(Card[] cardsToCheck, Stack[] stackToCheck) {
        //tuple list of cards to represent moves
        ArrayList<Pair<Card, Stack>> moveList = new ArrayList<Pair<Card, Stack>>();

        for (Card currCard : cardsToCheck) {
            //skip checking the cards that are on the solution stacks
            if (currCard.getCurrentStackID() > 19 && currCard.getCurrentStackID() < 24){
                continue;
            }
            //means that card can be moved
            if (currCard.getCanMove()) {
                //now need to find where
                for (Stack aStack : stackToCheck) {
                    //last card is top card
                    Card topCard = aStack.getLastCard();
                    //ignore empty stacks
                    if (topCard == null) continue;

                    boolean validStack = DragDrop.canStack(aStack.getStackID(), currCard.getCurrentStackID());   // Check if the stack can be stacked.
                    if (validStack && DragDrop.compareCardsHint(aStack, currCard)) { //lets just ignore cellar for now
                        Pair<Card, Stack> move = new Pair<Card, Stack>(currCard, aStack);
                        moveList.add(move);
                    }
                }
            }
        }
        return moveList;
    }
    /**
     *
     * @param type  type of game that user selected 1 - random game or 2 - predetermined game
     */
    public void generateCardSetup(String type, Card[] cards){

        if (type.equals("normal")) {
            // Randomly pick a number for base, and fill base with those cards in alternating suit color.
            Random rand = new Random();
            int numb = 0;
            while (numb == 0) {
                numb = rand.nextInt(14);
            }
            int suit = 1;
            for (int i = 20; i < 24; i++) {
                cards[i] = new Card(suit, numb);
                suit++;
            }

            // Fill stacks with cards except cellar
            int index = 0;
            int bound = 14;
            if (numb == 13) {
                bound = numb;
            }
            for (suit = 1; suit < 5; suit++) {
                for (int num = 1; num < bound; num++) {
                    if (num == numb) {
                        num++;
                    }
                    if (index == 20) {
                        index = index + 4;
                    }
                    cards[index] = new Card(suit, num);
                    index++;
                }
            }

            // Shuffle Cards
            for (int i = 0; i < cards.length; i++) {
                // Skip base cards
                if (i == 20) {
                    i = i + 4;
                }
                // Generate random number
                Random r = new Random();
                int randomCard = r.nextInt(cards.length);
                // Skip base cards
                while (randomCard > 19 && randomCard < 24) {
                    randomCard = r.nextInt(cards.length);
                }
                // Swap the two selected cards
                Card tempCard = cards[i];
                cards[i] = cards[randomCard];
                cards[randomCard] = tempCard;
            }
        } else if (type.equals("dummy")) {
            // when predetermined selected - by place card into stack associated
            // TODO - Find at least a layout of solving game - Below is just a dummy layout
            // 1 for Diamonds, 2 for Clubs, 3 for Hearts, 4 for Spades

            cards[0] = new Card(2, 2);
            cards[1] = new Card(1, 7);
            cards[2] = new Card(1, 1);
            cards[3] = new Card(2, 4);
            cards[4] = new Card(3, 2);
            cards[5] = new Card(4, 9);
            cards[6] = new Card(1, 6);
            cards[7] = new Card(3, 8);
            cards[8] = new Card(2, 13);
            cards[9] = new Card(1, 5);
            cards[10] = new Card(3, 1);
            cards[11] = new Card(2, 5);
            cards[12] = new Card(2, 6);
            cards[13] = new Card(3, 12);
            cards[14] = new Card(2, 12);
            cards[15] = new Card(1, 4);
            cards[16] = new Card(4, 4);
            cards[17] = new Card(2, 8);
            cards[18] = new Card(2, 11);
            cards[19] = new Card(1, 12);
            cards[20] = new Card(1, 10);
            cards[21] = new Card(2, 10);
            cards[22] = new Card(3, 10);
            cards[23] = new Card(4, 10);
            cards[24] = new Card(4, 7);
            cards[25] = new Card(2, 7);
            cards[26] = new Card(3, 7);
            cards[27] = new Card(4, 1);
            cards[28] = new Card(4, 6);
            cards[29] = new Card(4, 5);
            cards[30] = new Card(4, 12);
            cards[31] = new Card(3, 3);
            cards[32] = new Card(3, 11);
            cards[33] = new Card(2, 1);
            cards[34] = new Card(2, 9);
            cards[35] = new Card(1, 11);
            cards[36] = new Card(4, 3);
            cards[37] = new Card(1, 13);
            cards[38] = new Card(4, 13);
            cards[39] = new Card(1, 3);
            cards[40] = new Card(1, 8);
            cards[41] = new Card(1, 9);
            cards[42] = new Card(4, 8);
            cards[43] = new Card(1, 2);
            cards[44] = new Card(3, 6);
            cards[45] = new Card(2, 3);
            cards[46] = new Card(3, 9);
            cards[47] = new Card(4, 11);
            cards[48] = new Card(3, 4);
            cards[49] = new Card(3, 13);
            cards[50] = new Card(4, 2);
            cards[51] = new Card(3, 5);
        }
    }
    public void generateInitialStackSetup(Card[] cards, Stack[] stacks){
        // Create 53 stacks

        stacks[48] = new Stack(48);
        for (int i = 0; i < cards.length; i++) {
            if (i < 4 || (i >= 40 && i < 45) || i == 51) {
                cards[i].setCanMove(true);
            } else {
                cards[i].setCanMove(false);
            }
            if (i < 48) {
                stacks[i] = new Stack(i);
                stacks[i].addCardToStack(cards[i]);
            } else {
                stacks[i+1] = new Stack(i+1);
                stacks[i + 1].addCardToStack(cards[i]);
            }
        }
        new DragDrop().main(cards, stacks);
    }

    /**
     * Display card on the game page.
     *
     * @param stacks stack position on the page
     * @return None
     * @param cards card that will be added to stack
     */
    public void displayCards(Card[] cards, Stack[] stacks) {

        stacks[0].setImageView((ImageView) findViewById(R.id.stack0));
        stacks[1].setImageView((ImageView) findViewById(R.id.stack1));
        stacks[2].setImageView((ImageView) findViewById(R.id.stack2));
        stacks[3].setImageView((ImageView) findViewById(R.id.stack3));
        stacks[4].setImageView((ImageView) findViewById(R.id.stack4));
        stacks[5].setImageView((ImageView) findViewById(R.id.stack5));
        stacks[6].setImageView((ImageView) findViewById(R.id.stack6));
        stacks[7].setImageView((ImageView) findViewById(R.id.stack7));
        stacks[8].setImageView((ImageView) findViewById(R.id.stack8));
        stacks[9].setImageView((ImageView) findViewById(R.id.stack9));
        stacks[10].setImageView((ImageView) findViewById(R.id.stack10));
        stacks[11].setImageView((ImageView) findViewById(R.id.stack11));
        stacks[12].setImageView((ImageView) findViewById(R.id.stack12));
        stacks[13].setImageView((ImageView) findViewById(R.id.stack13));
        stacks[14].setImageView((ImageView) findViewById(R.id.stack14));
        stacks[15].setImageView((ImageView) findViewById(R.id.stack15));
        stacks[16].setImageView((ImageView) findViewById(R.id.stack16));
        stacks[17].setImageView((ImageView) findViewById(R.id.stack17));
        stacks[18].setImageView((ImageView) findViewById(R.id.stack18));
        stacks[19].setImageView((ImageView) findViewById(R.id.stack19));
        stacks[20].setImageView((ImageView) findViewById(R.id.stack20));
        stacks[21].setImageView((ImageView) findViewById(R.id.stack21));
        stacks[22].setImageView((ImageView) findViewById(R.id.stack22));
        stacks[23].setImageView((ImageView) findViewById(R.id.stack23));
        stacks[24].setImageView((ImageView) findViewById(R.id.stack24));
        stacks[25].setImageView((ImageView) findViewById(R.id.stack25));
        stacks[26].setImageView((ImageView) findViewById(R.id.stack26));
        stacks[27].setImageView((ImageView) findViewById(R.id.stack27));
        stacks[28].setImageView((ImageView) findViewById(R.id.stack28));
        stacks[29].setImageView((ImageView) findViewById(R.id.stack29));
        stacks[30].setImageView((ImageView) findViewById(R.id.stack30));
        stacks[31].setImageView((ImageView) findViewById(R.id.stack31));
        stacks[32].setImageView((ImageView) findViewById(R.id.stack32));
        stacks[33].setImageView((ImageView) findViewById(R.id.stack33));
        stacks[34].setImageView((ImageView) findViewById(R.id.stack34));
        stacks[35].setImageView((ImageView) findViewById(R.id.stack35));
        stacks[36].setImageView((ImageView) findViewById(R.id.stack36));
        stacks[37].setImageView((ImageView) findViewById(R.id.stack37));
        stacks[38].setImageView((ImageView) findViewById(R.id.stack38));
        stacks[39].setImageView((ImageView) findViewById(R.id.stack39));
        stacks[40].setImageView((ImageView) findViewById(R.id.stack40));
        stacks[41].setImageView((ImageView) findViewById(R.id.stack41));
        stacks[42].setImageView((ImageView) findViewById(R.id.stack42));
        stacks[43].setImageView((ImageView) findViewById(R.id.stack43));
        stacks[44].setImageView((ImageView) findViewById(R.id.stack44));
        stacks[45].setImageView((ImageView) findViewById(R.id.stack45));
        stacks[46].setImageView((ImageView) findViewById(R.id.stack46));
        stacks[47].setImageView((ImageView) findViewById(R.id.stack47));
        stacks[48].setImageView((ImageView) findViewById(R.id.stack48));
        stacks[49].setImageView((ImageView) findViewById(R.id.stack49));
        stacks[50].setImageView((ImageView) findViewById(R.id.stack50));
        stacks[51].setImageView((ImageView) findViewById(R.id.stack51));
        stacks[52].setImageView((ImageView) findViewById(R.id.stack52));

        cards[0].setImageView((ImageView) findViewById(R.id.card0));
        cards[1].setImageView((ImageView) findViewById(R.id.card1));
        cards[2].setImageView((ImageView) findViewById(R.id.card2));
        cards[3].setImageView((ImageView) findViewById(R.id.card3));
        cards[4].setImageView((ImageView) findViewById(R.id.card4));
        cards[5].setImageView((ImageView) findViewById(R.id.card5));
        cards[6].setImageView((ImageView) findViewById(R.id.card6));
        cards[7].setImageView((ImageView) findViewById(R.id.card7));
        cards[8].setImageView((ImageView) findViewById(R.id.card8));
        cards[9].setImageView((ImageView) findViewById(R.id.card9));
        cards[10].setImageView((ImageView) findViewById(R.id.card10));
        cards[11].setImageView((ImageView) findViewById(R.id.card11));
        cards[12].setImageView((ImageView) findViewById(R.id.card12));
        cards[13].setImageView((ImageView) findViewById(R.id.card13));
        cards[14].setImageView((ImageView) findViewById(R.id.card14));
        cards[15].setImageView((ImageView) findViewById(R.id.card15));
        cards[16].setImageView((ImageView) findViewById(R.id.card16));
        cards[17].setImageView((ImageView) findViewById(R.id.card17));
        cards[18].setImageView((ImageView) findViewById(R.id.card18));
        cards[19].setImageView((ImageView) findViewById(R.id.card19));
        cards[20].setImageView((ImageView) findViewById(R.id.card20));
        cards[21].setImageView((ImageView) findViewById(R.id.card21));
        cards[22].setImageView((ImageView) findViewById(R.id.card22));
        cards[23].setImageView((ImageView) findViewById(R.id.card23));
        cards[24].setImageView((ImageView) findViewById(R.id.card24));
        cards[25].setImageView((ImageView) findViewById(R.id.card25));
        cards[26].setImageView((ImageView) findViewById(R.id.card26));
        cards[27].setImageView((ImageView) findViewById(R.id.card27));
        cards[28].setImageView((ImageView) findViewById(R.id.card28));
        cards[29].setImageView((ImageView) findViewById(R.id.card29));
        cards[30].setImageView((ImageView) findViewById(R.id.card30));
        cards[31].setImageView((ImageView) findViewById(R.id.card31));
        cards[32].setImageView((ImageView) findViewById(R.id.card32));
        cards[33].setImageView((ImageView) findViewById(R.id.card33));
        cards[34].setImageView((ImageView) findViewById(R.id.card34));
        cards[35].setImageView((ImageView) findViewById(R.id.card35));
        cards[36].setImageView((ImageView) findViewById(R.id.card36));
        cards[37].setImageView((ImageView) findViewById(R.id.card37));
        cards[38].setImageView((ImageView) findViewById(R.id.card38));
        cards[39].setImageView((ImageView) findViewById(R.id.card39));
        cards[40].setImageView((ImageView) findViewById(R.id.card40));
        cards[41].setImageView((ImageView) findViewById(R.id.card41));
        cards[42].setImageView((ImageView) findViewById(R.id.card42));
        cards[43].setImageView((ImageView) findViewById(R.id.card43));
        cards[44].setImageView((ImageView) findViewById(R.id.card44));
        cards[45].setImageView((ImageView) findViewById(R.id.card45));
        cards[46].setImageView((ImageView) findViewById(R.id.card46));
        cards[47].setImageView((ImageView) findViewById(R.id.card47));
        cards[48].setImageView((ImageView) findViewById(R.id.card48));
        cards[49].setImageView((ImageView) findViewById(R.id.card49));
        cards[50].setImageView((ImageView) findViewById(R.id.card50));
        cards[51].setImageView((ImageView) findViewById(R.id.card51));
    }

    // Set stack location
    private void setStacksLocation() {
        for (int i = 0; i < stacks.length; i++) {
//            System.out.print("This is good'"+i+"\n");
            stacks[i].getImageView().getLocationOnScreen(location);
            stacks[i].setSize(stacks[i].getImageView().getWidth(), stacks[i].getImageView().getHeight());
            stacks[i].setXYCoordinates(location[0], location[1]);
        }
        for (int i = 0; i < cards.length; i++) {
            int tempID = cards[i].getCurrentStackID();
            cards[i].setXYPositions(stacks[tempID].getLeftSideLocation(), stacks[tempID].getTopSideLocation());
        }
        System.out.println("CONTEXT WHEN STARTING: " + context);
        new DragDrop().main(context, cards, stacks, recorder, solver);

    }

    // Temporary solution to actually finding location of ImageViews.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setStacksLocation();
    }

}
