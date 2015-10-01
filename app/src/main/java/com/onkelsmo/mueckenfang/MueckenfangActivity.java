package com.onkelsmo.mueckenfang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MueckenfangActivity extends Activity implements View.OnClickListener {
    private Animation fadeInAnimation;

    @Override
    protected void onResume() {
        super.onResume();
        View view = findViewById(R.id.root);
        view.startAnimation(fadeInAnimation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, GameActivity.class));
    }
}
