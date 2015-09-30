package com.onkelsmo.mueckenfang;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

public class GameActivity extends Activity implements View.OnClickListener {
    private static final long MAXAGE_MS = 2000;
    private boolean isRuning = false;
    private int round;
    private int score;
    private int midges;
    private int midgesCatched;
    private int time;
    private float scale;
    private Random random = new Random();
    private ViewGroup gameArea;

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
        if (midgesCatched >= midges) {
            startRound();
            return true;
        }
        return false;
    }

    private boolean isGameOver() {
        if (time == 0 && midgesCatched < midges) {
            gameOver();
            return true;
        }
        return false;
    }

    private void gameOver() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.gameover);
        dialog.show();
        isRuning = false;
    }

    private void vanishMidge() {
        int count = 0;
        while (count < gameArea.getChildCount()) {
            ImageView midge = (ImageView)gameArea.getChildAt(count);
            Date dateOfBirth = (Date)midge.getTag(R.id.date_of_birth);
            long age = (new Date()).getTime() - dateOfBirth.getTime();
            
            if (age > MAXAGE_MS) {
                gameArea.removeView(midge);
            } else {
               count++;
            }
        }
    }

    private void showMidge() {
        gameArea = (ViewGroup)findViewById(R.id.gamearea);

        int width = gameArea.getWidth();
        int height = gameArea.getHeight();

        int midgeWidth = Math.round(scale * 50);
        int midgeHeight = Math.round(scale * 42);

        int left = random.nextInt(width - midgeWidth);
        int top = random.nextInt(height - midgeHeight);

        ImageView midge = new ImageView(this);
        midge.setImageResource(R.drawable.muecke);
        midge.setOnClickListener(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(midgeWidth, midgeHeight);
        params.leftMargin = left;
        params.topMargin = top;
        params.gravity = Gravity.TOP + Gravity.START;

        gameArea.addView(midge, params);

        midge.setTag(R.id.date_of_birth, new Date());
    }

    @Override
    public void onClick(View midge) {
        midgesCatched++;
        score += 100;
        updateDisplay();
        gameArea.removeView(midge);
    }
}
