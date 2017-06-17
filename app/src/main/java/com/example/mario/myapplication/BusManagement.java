package com.example.mario.myapplication;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Klasa BusManagement
 *
 * jest odpowiedzialna za zarządzanie autobusami. Jest to symulator centrum dowodzenia.
 * Klasa ta tworzy, wypuszcza na trasę oraz zatrzymuje autobusy.
 * Klasa BusManagement zawiera dwie klasy wewnętrzne: Bus oraz Selector, które są opisane poniżej.
 *
 *      Metody klasy BusManagement:
 *          void createBuses() ->
 *              tworzy listę autobusów
 *          void startBuses() ->
 *              wypuszcza autobusy na trasę
 *          void stopBuses() ->
 *              zatrzymuje wszystkie autobusy
 *          List<Bus> getNearestList() ->
 *              zwraca listę autobusów posortowaną względem odległości od wybranego przystanku.
 *              Im autobus jest bliżej przystanku, tym bliżej głowy listy się znajduje.
 *          List<String> getNearestLines() ->
 *              zwraca listę linni autobusów posortowaną względem odległości od wybranego przystanku.
 *          Double getBusPositionRelativeToStop() ->
 *              zwraca odległość autobusu od wybranego przystanku

 * Klasy wewnętrzne:
 *
 *      Klasa Selector
 *
 *      to klasa odpowiedziana za wybór parametrów przez użytkownika. Przechowuje ona
 *      możliwe do wybrania wartości linii autobusu, kierunku oraz przystanków oraz zapisuje wartości,
 *      które wybrał użytkownik.
 *
 *          Metody klasy Selector:
 *              void getAvailableValues() ->
 *                 pobiera z pliku strings.xml możliwe do wyboru wartości
 *                 kierunku trasy, przystanków i linii autobusów
 *
 *
 *      Klasa Bus - to klasa reprezentująca autobus. Przechowuje ona najważniejsze dane autobusu takie jak
 *      linia, kierunek trasy czy prędkość oraz porusza autobus w odpowiednim kierunku.
 *
 *          Metody klasy Bus:
 *              void
 *              void stopMe() ->
 *                  zatrzymuje autobus
 *              void setNextStop() ->
 *                  na podstawie pokonanej odległości ustala jaki będzie kolejny przystanek autobusu
 *              boolean isRouteFinished() ->
 *                  sprawdza czy autobus zakończył swój kurs - czy przejechał większą odległość
 *                  niż ma trasa
 *              void startNewRoute() ->
 *                  zawraca autobus na pętli - kieruje go na trasę w przeciwnym kierunku
 *              void notifyBus() ->
 *                  powiadamia kierowcę o konieczności zatrzymania się na najbliższym przystanku
 *              void run() ->
 *                  metoda uruchamiająca wątek. Startuje ona i przesuwa autobus do przodu, jednocześnie sprawdzając,
 *                  czy nie zakończył swojego kursu, jeśli tak zawraca go metodą startNewRoute; Ustala
 *                  ona kolejny przystanek po każdej zmianie położenia autobusu za pomocą metody setNextStop;
 */
class BusManagement
{
    private static final Double TOTAL_DISTANCE = 50.0d;
    private final Double BUS_STOP_DISTANCE;

    private Selector selector = null;

    private List<Bus> buses = new ArrayList<>();

    public Selector getSelector() {return this.selector;}

    public BusManagement(Context context)
    {
        selector = new Selector(context);
        BUS_STOP_DISTANCE = BusManagement.TOTAL_DISTANCE / (selector.getAvailableStops().size() - 1);

        createBuses();
    }

    private void createBuses()
    {
        Random r = new Random();

        for (int i = 0; i < 50; ++i)
        {
            buses.add(new Bus(
                    selector.getAvailableLines().get(r.nextInt(selector.getAvailableLines().size())),
                    selector.getAvailableDirections().get(r.nextInt(selector.getAvailableDirections().size())),
                    selector.getAvailableStops().get(r.nextInt(selector.getAvailableStops().size() - 2) + 1),
                    0.1d + (i / 30.0d), this
            ));
        }
    }

    public void startBuses()
    {
        for (Bus b : buses)
            b.start();
    }

    public void stopBuses()
    {
        for (Bus b : buses)
            b.stopMe();
    }

    public List<Bus> getNearestList()
    {
        List<Pair<Bus, Double>> nearest = new ArrayList<>();
        List<Bus> sortedLines = new ArrayList<>();

        for (Bus b : buses)
            if (getBusPositionRelativeToStop(b) <= 0.0d)
                nearest.add(new Pair<>(b, Math.abs(getBusPositionRelativeToStop(b))));

        Collections.sort(nearest, new Comparator<Pair<Bus, Double>>() {
            @Override
            public int compare(Pair<Bus, Double> o1, Pair<Bus, Double> o2) {
                return o1.second.compareTo(o2.second);
            }
        });

        for (Pair<Bus, Double> p : nearest)
            sortedLines.add(p.first);

        return sortedLines;
    }

    public List<String> getNearestLines()
    {
        List<String> nearestLines = new ArrayList<>();

        for (Bus b : getNearestList())
            nearestLines.add(b.getLine());

        return nearestLines;
    }

    private Double getBusPositionRelativeToStop(Bus b)
    {
        String direction = selector.getSelectedDirection();
        String stop = selector.getSelectedStop();

        Double stopDistance = BUS_STOP_DISTANCE * selector.getAvailableStops().indexOf(stop);
        Double relativePosition;

        if (selector.getSelectedDirection().equals(selector.getAvailableDirections().get(0)))
            stopDistance = TOTAL_DISTANCE - stopDistance;

        if (b.direction.equals(direction))
            relativePosition = b.totalDistance - stopDistance;
        else
            relativePosition = -((TOTAL_DISTANCE - b.totalDistance) + stopDistance * 2);

        return relativePosition;
    }


    public static class Selector
    {
        private Context context;

        private String selectedDirection;
        private String selectedStop;
        private List<String> selectedLines = new ArrayList<>();

        private List<String> availableDirections = null;
        private List<String> availableStops = null;
        private List<String> availableLines = null;

        //Seters
        public void setSelectedDirection(String selection) {this.selectedDirection = selection;}
        public void setSelectedStop(String selection) {this.selectedStop = selection;}
        public void setSelectedLines(String selection, boolean remove)
        {
            if (!remove)
            {
                if (!this.selectedLines.contains(selection))
                    selectedLines.add(selection);
            }
            else if (this.selectedLines.contains(selection))
                selectedLines.remove(selection);
        }

        //Getters
        public List<String> getAvailableDirections() {return this.availableDirections;}
        public List<String> getAvailableStops() {return this.availableStops;}
        public List<String> getAvailableLines() {return this.availableLines;}

        public String getSelectedDirection() {return this.selectedDirection;}
        public String getSelectedStop() {return this.selectedStop;}
        public List<String> getSelectedLines() {return this.selectedLines;}

        public Selector(Context context)
        {
            this.context = context;

            getAvailableValues();
        }

        private void getAvailableValues()
        {
            availableDirections = new ArrayList<>(
                    Arrays.asList(context.getResources().getStringArray(R.array.directions))
            );
            availableStops = new ArrayList<>(
                    Arrays.asList(context.getResources().getStringArray(R.array.stops))
            );
            availableLines = new ArrayList<>(
                    Arrays.asList(context.getResources().getStringArray(R.array.lines))
            );
        }
    }

    public static class Bus extends Thread implements Comparable<Bus>
    {
        private boolean isMoving = false;
        private String line;
        private String direction;
        private String nextStop;
        private Double speed;
        private BusManagement parent;

        private Double totalDistance = 0.0d;

        //Geters
        public String getLine() {return this.line;}

        public Bus(String line, String direction, String nextStop, Double speed, BusManagement parent)
        {
            this.isMoving = false;
            this.line = line;
            this.direction = direction;
            this.nextStop = nextStop;
            this.speed = speed ;
            this.parent = parent;
        }

        public void stopMe() {this.isMoving = false;}

        public void setNextStop()
        {
            this.nextStop = direction.equals(parent.getSelector().getAvailableDirections().get(1)) ?
                    parent.getSelector().getAvailableStops().get(
                            (int)Math.floor(totalDistance / parent.BUS_STOP_DISTANCE)
                    ) :
                    parent.getSelector().getAvailableStops().get(
                            (parent.getSelector().getAvailableStops().size() - 1) - (int)Math.floor(totalDistance / parent.BUS_STOP_DISTANCE)
                    );
        }

        private boolean isRouteFinished() {return this.totalDistance >= BusManagement.TOTAL_DISTANCE;}

        private void startNewRoute()
        {
            direction = direction.equals(parent.getSelector().getAvailableDirections().get(0)) ?
                    parent.getSelector().getAvailableDirections().get(1) :
                    parent.getSelector().getAvailableDirections().get(0);

            this.totalDistance = 0.0d;

            try {
                this.sleep(3000);
            }
            catch (InterruptedException ex) { }
        }

        public void notifyBus() {}

        @Override
        public void run()
        {
            this.isMoving = true;

            while (this.isMoving)
            {
                try {
                    this.sleep(1000);
                }
                catch (InterruptedException ex) { }
                finally {
                    this.totalDistance += speed;

                    if (isRouteFinished())
                        startNewRoute();

                    setNextStop();
                }
            }
        }

        @Override
        public int compareTo(Bus b2)
        {
            if (parent.getSelector().getAvailableDirections().get(1).equals(direction))
                return this.totalDistance.compareTo(b2.totalDistance);
            else
                return b2.totalDistance.compareTo(this.totalDistance);
        }
    }
}
