package com.example.mario.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


/**
 * Klasa ClosestsTabFragment
 *
 * to klasa reprezentująca część graficznego interfejsu użytkownika.
 * Służy ona do wyświetlania otrzymanej od klasy BusListListener listy 5 najbliżej znajdujących
 * się autobusów
 *
 * Metody klasy ClosestsTabFragment:
 *      void onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) ->
 *          tutaj ma ona domyślne działanie - zwraca gotowy fragment interfejsu.
 *
 *      void onActivityCreated(Bundle savedInstanceState) ->
 *          metoda uruchamiana, gdy cały interfejs jest gotowy.
 *          Służy ona do ustawienia w klasie Selector domyślnie wybranych wartości oraz do uruchomienia
 *          komunikacji między klasą ClosestsTabFragment, a klasą BusListListener, od którego będzie
 *          otrzymywać listę najbliżej znajdujących się autobusów.
 */
public class ClosestsTabFragment extends Fragment
{
    private ArrayAdapter<String> arrayAdapter;
    private View view;
    private BusListListener busListListener;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_closests_tab, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //Get BusManagement instance
        BusManagement busManagement = ((MainActivity)getActivity()).getBusManagement();

        //Set default values in selector
        busManagement.getSelector().setSelectedDirection(
                ((Spinner)getActivity().findViewById(R.id.directionSpinner)).getSelectedItem().toString()
        );

        busManagement.getSelector().setSelectedStop(
                ((Spinner)getActivity().findViewById(R.id.stopSpinner)).getSelectedItem().toString()
        );

        //Add handler
        handler = new Handler(
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        arrayAdapter.clear();
                        arrayAdapter.addAll(busListListener.getNearest().subList(
                                0, (busListListener.getNearest().size() > 5 ? 5 : busListListener.getNearest().size()))
                        );
                        arrayAdapter.notifyDataSetChanged();

                        return false;
                    }
                }
        );

        busListListener = new BusListListener(((MainActivity) getActivity()).getBusManagement(), handler);

        arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                busListListener.getNearest()
        );

        busListListener.setArrayAdapter(arrayAdapter);

        ((ListView)view.findViewById(R.id.closestsList)).setAdapter(arrayAdapter);

        busListListener.start();
    }
}
