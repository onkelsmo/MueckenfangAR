package com.onkelsmo.mueckenfang;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MueckenfangActivity extends Activity implements View.OnClickListener {
    private Animation fadeInAnimation;
    private Animation wiggleAnimation;
    private Button startButton;
    private Handler handler = new Handler();
    private Runnable wiggleRunnable = new WiggleButton();

    @Override
    protected void onResume() {
        super.onResume();
        TextView tv = (TextView)findViewById(R.id.highscore);
        tv.setText(Integer.toString(readHighscore()));
        View view = findViewById(R.id.root);
        view.startAnimation(fadeInAnimation);
        handler.postDelayed(wiggleRunnable, 1000*10);
    }

    private int readHighscore() {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getInt("HIGHSCORE", 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(wiggleRunnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode > readHighscore()) {
            writeHighscore(resultCode);
        }
    }

    private void writeHighscore(int highscore) {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("HIGHSCORE", highscore);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startButton = (Button)findViewById(R.id.button);
        startButton.setOnClickListener(this);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        wiggleAnimation = AnimationUtils.loadAnimation(this, R.anim.wiggle);
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(this, GameActivity.class), 1);
    }

    private class WiggleButton implements Runnable {
        @Override
        public void run() {
            startButton.startAnimation(wiggleAnimation);
        }
    }
}
