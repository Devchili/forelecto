package com.jimboy.forelecto;// import necessary packages

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.jimboy.forelecto.MainActivity;
import com.jimboy.forelecto.R;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Reference to the ImageView
        ImageView imageViewSplash = findViewById(R.id.imageViewSplash);

        // Use a Handler to delay the transition to the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start the main activity
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);

                // Finish the splash activity to prevent going back to it
                finish();
            }
        }, SPLASH_DELAY);
    }
}
