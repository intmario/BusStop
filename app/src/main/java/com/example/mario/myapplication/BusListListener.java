package com.example.mario.myapplication;

import android.os.Handler;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa BusListListener
 *
 * Służy do komunikacji klasy BusManagement z klasą CLosestsTabFragment.
 * Co wyznaczony czas pobiera ona od BusManagement listę najbliższych autobusów i wysyła ją
 * do interfejsu graficznego, by ten ją wyświetlił.
 *
 *      Metody klasy BusListListener:
 *           List<String> getNearest() ->
 *                zwraca listę linii najbliżej znajdujących się autobusów
 *          void run() ->
 *              metoda odpowiedzialna za uruchomienie wątku. To tutaj co wyznaczony czas pobierana jest
 *              lista najbliższych autobusów i wysyłana jest informacja o zmianie listy
 *              do interfejsu graficznego.
 */

public class BusListListener extends Thread
{
    private List<String> nearest = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter = null;
    private BusManagement busManagement = null;
    private Handler handler = null;

    public void setArrayAdapter(ArrayAdapter<String> arrayAdapter) {this.arrayAdapter = arrayAdapter;}

    public List<String> getNearest() {return nearest;}

    public BusListListener(BusManagement busManagement, Handler handler)
    {
        this.busManagement = busManagement;
        this.handler = handler;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                nearest = busManagement.getNearestLines();
                handler.sendEmptyMessage(0);
                this.sleep(1000);
            }
            catch (InterruptedException ex) {}
        }
    }
}
