package com.example.midterm_alternative_assignments;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MILLIS = 1000; // 로고 화면 표시 시간 (3초)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_layout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SPLASH_DELAY_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // 로고 화면 액티비티를 종료
            }
        }).start();
    }
}
