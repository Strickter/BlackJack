package com.example.android.blackjack;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;



public class MainActivity extends AppCompatActivity {
    ArrayList<Integer> shuffled = new ArrayList<>();
    ArrayList<Integer> dead = new ArrayList<>();
    ArrayList<Card> playerHand = new ArrayList<>();
    ArrayList<Card> dealerHand = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    boolean gameStarted = false;
    int cardsInPlayerHand;
    int cardsInDealerHand;
    boolean dealt = false;
    private int dealerCurrentTotal = 0;
    private int playerCurrentTotal = 0;
    boolean stand = false;
    boolean ace = false;
    private Card aceCard = new Card(9, 1);
    public int cardNumber = 0;
    private boolean pm;
    private boolean dm;
    final Random rand = new Random();

    /*STARTS A NEW GAME*/
    public void newGame(View view) {
            newShuffle();
            dead.clear();
            deal(view);
    }

    /*CREATES A NEW DECK*/
    public void newShuffle() {
        shuffled.clear();
        shuffled.trimToSize();
        int count = 0;
        while (count < 52) {
            for (int j = 0; j < 13; j++) {
                for (int i = 0; i < 4; i++) {
                    shuffled.add(j);
                    count++;
                }
            }
        }
    }
    /*UNTESTED BUT SHOULD WORK. CALLED IN cardGrab()*/
    public void shuffleDead() {
        int transfer;
        for (int i = 0; i < dead.size(); i++) {
            transfer = dead.get(i);
            shuffled.add(transfer);
        }
        dead.clear();
        dead.trimToSize();
    }

    /*PULLS A CARD AT RANDOM FROM SHUFFLED DECK*/
    public Card cardGrab() {
        int card;
        int cardValue;
        if (shuffled.isEmpty()) {
            shuffleDead();
            Toast.makeText(this, "Shuffling Decks", Toast.LENGTH_SHORT).show();
        }
        card = rand.nextInt(shuffled.size() - 1);
        cardNumber = shuffled.get(card);
        shuffled.remove(card);
        shuffled.trimToSize();
        if (cardNumber >= 10) {
            cardValue = 10;
        } else {
            cardValue = cardNumber + 2;
        }
        return new Card(cardNumber, cardValue);

    }

    public class Card {
        int number;
        int value;

        public Card(int m, int n) {
            number = m;
            value = n;
        }
    }

    /*DEALS NEW HAND*/
    public void deal(View view) {
        dealt = true;
        pm = true;
        dm = false;
        for (int i = 0; i < 2; i++) {
            onHit(view);
        }
        for (int j = 0; j < 2; j++) {
            dealerHit();
        }
        cardsInPlayerHand = 0;
        cardsInDealerHand = 0;
        gameEngine();
    }

    /*HANDLES MAJORITY OF CALCULATIONS*/
    private void gameEngine() {
        boolean inProg;
        if (cardsInPlayerHand < playerHand.size() || cardsInDealerHand < dealerHand.size() || stand) {
            inProg = true;
        } else {
            inProg = false;
        }
        while (inProg) {
            int playerCardCounter = 0;
            int dealerCardCounter = 0;
            int playerTotal = 0;
            int dealerTotal = 0;
            while (dealerCardCounter < dealerHand.size()) {
                dealerTotal = dealerTotal + dealerHand.get(dealerCardCounter).value;
                dealerCurrentTotal = dealerTotal;
                dealerCardCounter++;
                cardsInDealerHand = dealerCardCounter;
            }
            while (playerCardCounter < playerHand.size()) {
                playerTotal = playerTotal + playerHand.get(playerCardCounter).value;
                playerCurrentTotal = playerTotal;
                playerCardCounter++;
                cardsInPlayerHand = playerCardCounter;
            }

            if (playerCurrentTotal == 21 && dealerCurrentTotal == 21) {
                Toast.makeText(this, "Push", Toast.LENGTH_SHORT).show();
                reset();
                break;
            } else if (playerCurrentTotal == 21 && dealerCurrentTotal != 21) {
                Toast.makeText(this, "BlackJack! You Win!", Toast.LENGTH_SHORT).show();
                reset();
                break;
            } else if (playerCurrentTotal != 21 && dealerCurrentTotal == 21) {
                Toast.makeText(this, "Dealer Wins", Toast.LENGTH_SHORT).show();
                reset();
                break;
            } else if (playerCurrentTotal > 21 || dealerCurrentTotal > 21) {

                /*UNFINISHED*/
                aceCheck(playerHand);
                aceCheck(dealerHand);
                if (ace) {
                    ace = false;
                } else if (playerCurrentTotal > 21) {
                    Toast.makeText(this, "BUST", Toast.LENGTH_SHORT).show();
                    reset();
                    break;
                } else if (dealerCurrentTotal > 21) {
                    Toast.makeText(this, "Dealer Busts", Toast.LENGTH_SHORT).show();
                    reset();
                    break;
                }

            } else if (stand) {
                if (dealerCurrentTotal < 17) {
                    dealerHit();
                } else {
                    if (playerCurrentTotal > dealerCurrentTotal) {
                        Toast.makeText(this, "You WIN", Toast.LENGTH_SHORT).show();
                        stand = false;
                        reset();
                        break;
                    } else if (playerCurrentTotal == dealerCurrentTotal) {
                        Toast.makeText(this, "Push", Toast.LENGTH_SHORT).show();
                        stand = false;
                        reset();
                        break;
                    } else {
                        Toast.makeText(this, "Dealer Win", Toast.LENGTH_SHORT).show();
                        stand = false;
                        reset();
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    /* CHECKS FOR ACE IN HAND, IF SO ACE VALUE BECOMES 1 AND RETURNS ACE TRUE*/
    private void aceCheck(ArrayList<Card> array) {
        for (int q = 0; q < array.size(); q++) {
            if (array.get(q).value == 11) {
                array.set(q, aceCard);
                ace = true;
            }
        }
    }

    /*CLEARS THE BOARD*/
    private void reset() {
        View emptyView = findViewById(R.id.player_cards);
        ((LinearLayout) emptyView).removeAllViewsInLayout();
        View emptyView2 = findViewById(R.id.dealer_cards);
        ((LinearLayout) emptyView2).removeAllViewsInLayout();
        for (int i = 0; playerHand.size() > i; i++) {
            dead.add(playerHand.get(i).number);
        }
        for (int i = 0; dealerHand.size() > i; i++) {
            dead.add(dealerHand.get(i).number);
        }
        playerHand.clear();
        dealerHand.clear();
        dealerCurrentTotal = 0;
        playerCurrentTotal = 0;
        gameStarted = true;
    }

    /*UNFINISHED*/
    public void onHit(View view) {
        LinearLayout playerLayout = (LinearLayout) findViewById(R.id.player_cards);
        playerHand.add(cardGrab());
        if (pm){
            if (playerLayout.getChildCount() != 0) {
                playerLayout.removeViewAt(0);
            }
            pm = false;
        }
        cardViewMaker(playerHand, playerLayout);
//        try {
//            Thread.sleep(4000);
//        }
//        catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
    }

    public void onStand(View view) {
        stand = true;
        gameEngine();

    }

    private void dealerHit() {
        LinearLayout dealerLayout = (LinearLayout) findViewById(R.id.dealer_cards);
        dealerHand.add(cardGrab());

        if (dealerHand.size() > 1 || gameStarted) {
            if (stand && !dm) {
                if (dealerLayout.getChildCount() != 0) {
                    dealerLayout.removeViewAt(0);
                }
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(dipToPixels(this, 48), dipToPixels(this, 68));
                TextView firstCard = new TextView(this);
                firstCard.setGravity(Gravity.CENTER);
                firstCard.setBackgroundResource(R.drawable.blankfront);
                firstCard.setText("" + dealerHand.get(0).value);
                dealerLayout.addView(firstCard, 0, cardParams);
                cardViewMaker(dealerHand, dealerLayout);
                dm = true;
            } else {
                cardViewMaker(dealerHand, dealerLayout);
            }
//            try {
//                Thread.sleep(4000);
//            }
//            catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
        }
    }
    public void cardViewMaker(ArrayList<Card> hand, LinearLayout parent){
        int p = hand.size();
        int currentValue = hand.get(p - 1).value;
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(dipToPixels(this, 48), dipToPixels(this, 68));
        TextView newText = new TextView(this);
        newText.setGravity(Gravity.CENTER);
        newText.setBackgroundResource(R.drawable.blankfront);
        newText.setText("" + currentValue);
        parent.addView(newText, cardParams);
    }
    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
        return Math.round(pix);
    }
}