package com.example.mario.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


/**
 * Klasa MainActivity
 *
 * Jest to klasa graficznego głównego ekranu aplikacji.
 * Wyświetla się po ekranie powitalnym
 *
 *      metody klasy MainActivity:
 *          void onCreate(Bundle savedInstanceState) ->
 *              wyświetla ekran główny oraz uruchamia metody createTabs oraz startBuses;
 *          void createTabs() ->
 *              tworzy 2 karty na ekranie głównym:
 *                  StopTabFragment - zawiera pola, w których
 *                  użytkownik wybiera kierunek trasy, przystanek oraz linie autobusu;
 *                  ClosestsTabFragment - pokazuje najbliższe wybranemu przystankowi autobusy;
 *          void startBuses() ->
 *              za pomocą obiektu klasy BusManagement startuje autobusy
 *          void onSubmitButtonClick(View v) ->
 *              metoda wyświetlana po wciśnięciu przycisku "Zatrzymaj autobus"
 *              Gdy użytkownik nie wybrał żadnej linii, pokazuje ona stosowny komunikat, gdy zaś wybrał co najmniej
 *              jedną linię, prosi go o potwierdzenie wyboru, po czym wywołuje metodę submit;
 *          void submit() ->
 *              informuje najbliższy spośród wybranych autobus o konieczności zatrzymania się
 *              na wybranym przystanku i wyświetla końcowy ekran potwierdzający wybór
 *              autobusu - ResultActivity
 */
public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar = null;
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    private BusManagement busManagement = null;

    public BusManagement getBusManagement() {return this.busManagement;}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createTabs();
        startBuses();
    }

    private void createTabs()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.pager);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position)
            {
                return (position == 0) ? new StopTabFragment() : new ClosestsTabFragment();
            }

            @Override
            public int getCount()
            {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position)
            {
                return (position == 0) ? "Zatrzynaj" : "Najbliższe";
            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }

    private void startBuses()
    {
        this.busManagement = new BusManagement(this);
        busManagement.startBuses();
    }

    public void onSubmitButtonClick(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (busManagement.getSelector().getSelectedLines().size() == 0)
        {
            builder.setTitle("Nie wybrano linii.");
            builder.setMessage("Proszę wybrać przynajmniej jedną linię autobusu.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
        }
        else
        {
            builder.setTitle("Potwierdź wybór");
            builder.setMessage("Czy chcesz zatrzymać wybrany autobus?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    submit();
                }
            });
            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
        }

        builder.create().show();
    }

    private void submit() {
        Intent intent = new Intent(this, ResultActivity.class);
        int prevIndex = Integer.MAX_VALUE;
        String nearestLine = null;

        Log.i("Lines: ", busManagement.getSelector().getSelectedLines().toString());

        for (String s : busManagement.getSelector().getSelectedLines())
            if (busManagement.getNearestLines().contains(s))
                if (busManagement.getNearestLines().indexOf(s) < prevIndex)
                {
                    prevIndex = busManagement.getNearestLines().indexOf(s);
                    nearestLine = busManagement.getNearestLines().get(prevIndex);
                }

        if (nearestLine != null) {
            intent.putExtra(ResultActivity.RESULT_MESSAGE, "true");
            intent.putExtra(ResultActivity.LINE_MESSAGE, nearestLine);
        }
        else
        {
            intent.putExtra(ResultActivity.RESULT_MESSAGE, "false");
        }

        startActivity(intent);
    }
}
