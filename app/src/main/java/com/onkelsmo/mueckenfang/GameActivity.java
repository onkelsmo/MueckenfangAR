package com.onkelsmo.mueckenfang;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

public class GameActivity extends Activity implements View.OnClickListener, Runnable {
    private static final long MAXAGE_MS = 2000;
    public static final int DELAY_MILLIS = 100;
    public static final int TIMESCALE = 600;
    private boolean isRuning = false;
    private int round;
    private int score;
    private int midges;
    private int midgesCatched;
    private int time;
    private float scale;
    private Random random = new Random();
    private ViewGroup gameArea;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        scale = getResources().getDisplayMetrics().density;
        gameArea = (ViewGroup)findViewById(R.id.gamearea);
        mediaPlayer = MediaPlayer.create(this, R.raw.summen);
        startGame();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
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
        time = TIMESCALE;
        updateDisplay();
        handler.postDelayed(this, 1000);
    }

    private void updateDisplay() {
        TextView tvPoints = (TextView)findViewById(R.id.points);
        tvPoints.setText(Integer.toString(score));

        TextView tvRound = (TextView)findViewById(R.id.round);
        tvRound.setText(Integer.toString(round));

        TextView tvHits = (TextView)findViewById(R.id.hits);
        tvHits.setText(Integer.toString(midgesCatched));

        TextView tvTime = (TextView)findViewById(R.id.time);
        tvTime.setText(Integer.toString(time / (1000 / DELAY_MILLIS)));

        FrameLayout flHits = (FrameLayout)findViewById(R.id.bar_hits);
        FrameLayout flTime = (FrameLayout)findViewById(R.id.bar_time);

        LayoutParams lpHits = flHits.getLayoutParams();
        lpHits.width = Math.round(scale * 300 * Math.min(midgesCatched,midges) / midges);

        LayoutParams lpTime = flTime.getLayoutParams();
        lpTime.width = Math.round(scale * time * 300 / TIMESCALE);
    }

    private void countDown() {
        time = time - 1;
        if (time % (1000/DELAY_MILLIS) == 0) {
            float randomNumber = random.nextFloat();
            double probability = midges * 1.5;
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
        }
        vanishMidge();
        updateDisplay();
        if (!isGameOver()) {
            if (!isRoundOver()) {
                handler.postDelayed(this, DELAY_MILLIS);
            }
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

        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    @Override
    public void onClick(View midge) {
        midgesCatched++;
        score += 100;
        updateDisplay();
        Animation hitAnimation = AnimationUtils.loadAnimation(this, R.anim.hit);
        hitAnimation.setAnimationListener(new MidgeAnimationListener(midge));
        midge.startAnimation(hitAnimation);
        mediaPlayer.pause();
        midge.setOnClickListener(null);
    }

    @Override
    public void run() {
        countDown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(this);
    }

    private class MidgeAnimationListener implements Animation.AnimationListener {
        private View midge;

        public MidgeAnimationListener(View m) {
            midge = m;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    gameArea.removeView(midge);
                }
            });
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
