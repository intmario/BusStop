package com.example.mario.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Klasa LoadingActivity
 *
 * Jest to klasa graficznego ekranu powitalnego
 * Wyświetla ona ikonę aplikacji na starcie, po czym uruchamia główny ekran aplikacji.
 *
 *      metody klasy LoadingActivity:
 *          void onCreate(Bundle savedInstanceState) ->
 *              pokazuje ekran powitalny, po czym uruchamia główny ekran aplikacji.
 */
public class LoadingActivity extends Activity
{
    private final int delayTime = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        new Thread(){
            @Override
            public void run()
            {
                try {
                    super.run();
                    Thread.sleep(delayTime);
                }
                catch (InterruptedException ex) {}
                finally{
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();
    }

}
