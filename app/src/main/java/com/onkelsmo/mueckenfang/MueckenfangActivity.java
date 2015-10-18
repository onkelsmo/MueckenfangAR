package com.onkelsmo.mueckenfang;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MueckenfangActivity extends Activity implements View.OnClickListener, Html.ImageGetter {
    private static final String HIGHSCORE_SERVER_BASE_URL = "http://myhighscoreserver.appspot.com/highscoreserver";
    private static final String HIGHSCORE_SERVER_GAME_ID = "mueckenfang";

    private Animation fadeInAnimation;
    private Animation wiggleAnimation;
    private Button startButton;
    private Handler handler = new Handler();
    private Runnable wiggleRunnable = new WiggleButton();
    private LinearLayout nameInput;
    private Button saveButton;
    private String highscoreHtml = "";

    @Override
    protected void onResume() {
        super.onResume();
        showHighscore();
        View view = findViewById(R.id.root);
        view.startAnimation(fadeInAnimation);
        handler.postDelayed(wiggleRunnable, 1000*10);
        internetHighscores("", 0);
    }

    private void showHighscore() {
        TextView tv = (TextView)findViewById(R.id.highscore);
        int highscore = readHighscore();
        if(highscore > 0) {
            tv.setText(Integer.toString(highscore) + " von " + readHighscoreName());
        } else {
            tv.setText("-");
        }
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
            nameInput.setVisibility(View.VISIBLE);
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
        nameInput = (LinearLayout)findViewById(R.id.name_input);
        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);
        nameInput.setVisibility(View.INVISIBLE);

        startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(this);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        wiggleAnimation = AnimationUtils.loadAnimation(this, R.anim.wiggle);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_button) {
            startActivityForResult(new Intent(this, GameActivity.class), 1);
        } else if (v.getId() == R.id.save_button) {
            writeHighscoreName();
            showHighscore();
            nameInput.setVisibility(View.INVISIBLE);
            internetHighscores(readHighscoreName(), readHighscore());
        }
    }

    private void writeHighscoreName() {
        EditText et = (EditText)findViewById(R.id.player_name);
        String name = et.getText().toString().trim();
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("HIGHSCORE_NAME", name);
        editor.commit();
    }

    private String readHighscoreName() {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getString("HIGHSCORE_NAME", "");
    }

    private void internetHighscores(final String name, final int points) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(HIGHSCORE_SERVER_BASE_URL
                    + "?game=" + HIGHSCORE_SERVER_GAME_ID
                    + "&name=" + URLEncoder.encode(name, "UTF-8")
                    + "&points=" + Integer.toString(points)
                    + "&max=100");

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    InputStreamReader input = new InputStreamReader(conn.getInputStream(), "UTF8");
                    BufferedReader reader = new BufferedReader(input, 2000);

                    List<String> highscoreList = new ArrayList<String>();
                    String line = reader.readLine();
                    while (line != null) {
                        highscoreList.add(line);
                        line = reader.readLine();
                    }

                    highscoreHtml = "";
                    for (String s : highscoreList) {
                        highscoreHtml += "<b>"
                                + s.replace(",", "</b> <font color='red'>")
                                + "</font><img src='muecke'><br />";
                    }
                } catch (IOException e) {
                    highscoreHtml = "Fehler: " + e.getMessage();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView)findViewById(R.id.highscores);
                        tv.setText(Html.fromHtml(highscoreHtml, MueckenfangActivity.this, null));
                    }
                });
            }
        })).start();
    }

    @Override
    public Drawable getDrawable(String source) {
        int id = getResources().getIdentifier(source, "drawable", this.getPackageName());
        Drawable d = getResources().getDrawable(id);
        d.setBounds(0, 0, 30, 30);
        return d;
    }

    private class WiggleButton implements Runnable {
        @Override
        public void run() {
            startButton.startAnimation(wiggleAnimation);
        }
    }
}
