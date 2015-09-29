package com.onkelsmo.mueckenfang;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity {
    private boolean isRuning = false;
    private int round;
    private int score;
    private int midges;
    private int midgesCatched;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        startGame();
    }

    private void startGame() {
        isRuning = true;
        round = 0;
        score = 0;
        startRound();
    }

    private void startRound() {
        round = round + 1;
        midges = round * 10;
        midgesCatched = 0;
        time = 60;
        updateDisplay();
    }
    private void updateDisplay() {

    }
}
