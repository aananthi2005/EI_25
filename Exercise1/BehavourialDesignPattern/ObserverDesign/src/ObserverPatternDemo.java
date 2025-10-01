package src;

public class ObserverPatternDemo {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();
        station.addObserver(new MobileApp());
        station.addObserver(new TVDisplay());

        station.setTemperature(30);
        station.setTemperature(35);
    }
}
