package src;

public class MobileApp implements Observer {
    @Override
    public void update(int temperature) {
        System.out.println("Mobile App: Temperature updated to " + temperature + "°C");
    }
}
