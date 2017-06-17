package com.example.mario.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Klasa ResultActivity
 *
 * to ostatni z ekranów aplikacji, potwierdzający poinformowanie koerowcy o
 * konieczności zatrzymania się. Jeśli autobus wybranej linii jest na trasie, wyświetla on komunikat o tym,
 * że kierowca został poinformowany, jeśli zaś na trasie nie ma autobusu z wybranych linii, prosi
 * użytkownika o ponowne wybranie innej linii.
 *
 *      Metody klasy ResultActivity
 *          void onCreate(Bundle savedInstanceState) ->
 *              Wyświetla ekran ResultActivity oraz stosowny komunikat w zależności, czy autobus wybranej
 *              linii jest na trasie, czy też nie.
 *          void onBackButtonClick(View w) ->
 *              Metoda uruchamiana po naciśnięciu przycisku "Powrót". Wyświetla z powrotem główny ekran aplikacji.
 */
public class ResultActivity extends AppCompatActivity {

    public static final String RESULT_MESSAGE = "Result";
    public static final String LINE_MESSAGE = "Line";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        if (intent.getStringExtra(ResultActivity.RESULT_MESSAGE).equals("true"))
        {
            findViewById(R.id.backButton).setBackground(getDrawable(R.color.colorSuccess));

            ((TextView)findViewById(R.id.resultTitle)).setText("Poinformowano kierowcę");
            ((TextView)findViewById(R.id.resultText)).setText(
                    "Poinformowano kierowcę najbliższego autobusu linii " +
                            intent.getStringExtra(ResultActivity.LINE_MESSAGE) +
                            ". Proszę oczekiwać na przyjazd autobusu na wybranym przystanku."
            );
        }
        else
        {
            findViewById(R.id.backButton).setBackground(getDrawable(R.color.colorAccent));

            ((TextView)findViewById(R.id.resultTitle)).setText("Brak autobusu");

            ((TextView)findViewById(R.id.resultText)).setText(
                    "Niestety autobusy wybranej linni w tym momencie nie kursują. " +
                            "Proszę wybrać inną linię autobusu."
            );
        }
    }

    public void onBackButtonClick(View v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
