package com.example.mario.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Klasa StopTabFragment
 *
 * to klasa reprezentująca fragment elementu graficznego. Jest to ekran wyboru
 * przystanku, kierunku trasy oraz linii autobusu przez użytkownika.
 *
 *      Metody klasy StopTabFragment:
 *          View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) ->
 *              Metoda tworząca i wyświetlająca ekran. Dodatkowo tworzy ona słuchaczy dla pól wyborów, by
 *              te reagowały na każdą zmianę wartości.
 *          void createLinesLayout() ->
 *              pobiera z pliku strings.xml wartości linii autobusów i tworzy
 *              na ekranie siatkę przycisków za pomocą których użytkownik wybiera linie autobusu.
 *          void setStopsList() ->
 *              W zależności od wybranego kierunku wyświetla na ekranie odpowiednią liste przystanków.
 *              Reaguje na zmianę kierunku trasy przez użytkownika.
 */
public class StopTabFragment extends Fragment
{
    private View view;

    //This contains user selection from this activity
    BusManagement.Selector selector = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_stop_tab, container, false);
        createLinesLayout();

        this.selector = ((MainActivity)getActivity()).getBusManagement().getSelector();

        //Set spinner listeners
        ((Spinner)view.findViewById(R.id.directionSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).getBusManagement().getSelector().setSelectedDirection(
                        ((Spinner)getActivity().findViewById(R.id.directionSpinner)).getSelectedItem().toString()
                );

                setStopsList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setStopsList();
            }
        });

        ((Spinner)view.findViewById(R.id.stopSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).getBusManagement().getSelector().setSelectedStop(
                        ((Spinner)getActivity().findViewById(R.id.stopSpinner)).getSelectedItem().toString()
                );
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setStopsList();
            }
        });

        return view;
    }

    private void createLinesLayout()
    {
        //Get grid
        GridLayout grid = (GridLayout)view.findViewById(R.id.lineSelectorGrid);

        //Read available lines
        List<String> busLines = new ArrayList<>( Arrays.asList(getResources().getStringArray(R.array.lines)) );

        //Set grid
        int rows = (int)Math.ceil(busLines.size() / 4.0);
        int columns = 4;

        grid.setRowCount(rows);
        grid.setColumnCount(columns);

        //Insert into grid
        for (int i = 0; i < rows; ++i)
        {
            int lastVal = (i*4 + 4 > busLines.size()) ? busLines.size() % 4 : 4;

            for (int j = 0; j < lastVal; ++j)
            {
                GridLayout.LayoutParams lineParams = new GridLayout.LayoutParams(
                        GridLayout.spec(i), GridLayout.spec(j, 0.25f)
                );

                lineParams.setMargins(6, 6, 6, 6);

                LineButton line = new LineButton(getContext());
                line.setLayoutParams(lineParams);
                line.setText(busLines.get(i*4 + j));

                grid.addView(line);
            }
        }
    }

    private void setStopsList()
    {
        BusManagement.Selector selector = ((MainActivity)getActivity()).getBusManagement().getSelector();
        String selectedDirection = selector.getSelectedDirection();
        ArrayAdapter<String> adapter;
        List<String> stops = new ArrayList<>(selector.getAvailableStops());

        if (selectedDirection.equals(selector.getAvailableDirections().get(0)))
            Collections.reverse(stops);

        stops.remove(stops.size() - 1);

        adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                stops
        );

        ((Spinner)view.findViewById(R.id.stopSpinner)).setAdapter(adapter);
    }
}
