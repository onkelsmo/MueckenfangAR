package com.onkelsmo.mueckenfang;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends Activity {
    private boolean isRuning = false;
    private int round;
    private int score;
    private int midges;
    private int midgesCatched;
    private int time;
    private float scale;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        scale = getResources().getDisplayMetrics().density;
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
        TextView tvPoints = (TextView)findViewById(R.id.points);
        tvPoints.setText(Integer.toString(score));

        TextView tvRound = (TextView)findViewById(R.id.round);
        tvRound.setText(Integer.toString(round));

        TextView tvHits = (TextView)findViewById(R.id.hits);
        tvHits.setText(Integer.toString(midgesCatched));

        TextView tvTime = (TextView)findViewById(R.id.time);
        tvTime.setText(Integer.toString(time));

        FrameLayout flHits = (FrameLayout)findViewById(R.id.bar_hits);
        FrameLayout flTime = (FrameLayout)findViewById(R.id.bar_time);

        LayoutParams lpHits = flHits.getLayoutParams();
        lpHits.width = Math.round(scale * 300 * Math.min(midgesCatched,midges) / midges);

        LayoutParams lpTime = flTime.getLayoutParams();
        lpTime.width = Math.round(scale * time * 300 / 60);
    }

    private void countDown() {
        time = time - 1;

        float randomNumber = random.nextFloat();
        if (randomNumber < midges * 1.5 / 60) {
            showMidge();
        }

        double probability = midges * 1.5f / 60;
        if (probability > 1) {
            showMidge();
            if (randomNumber < probability - 1) {
                showMidge();
            }
        } else {
            if (randomNumber < probability) {
                showMidge();
            }
        }
        vanishMidge();
        updateDisplay();
        if (!isGameOver()) {
            isRoundOver();
        }
    }

    private boolean isRoundOver() {
        return false;
    }

    private boolean isGameOver() {
        return false;
    }

    private void vanishMidge() {

    }

    private void showMidge() {

    }
}
