package src;

public class Singleton {
    // Static instance of the Singleton
    private static Singleton instance;

    // Private constructor prevents instantiation
    private Singleton() {}

    // Global access point
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    // Example method
    public void showMessage() {
        System.out.println("Hello from Singleton!");
    }
}
