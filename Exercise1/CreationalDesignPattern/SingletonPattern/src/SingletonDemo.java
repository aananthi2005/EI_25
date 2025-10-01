package src;

public class SingletonDemo {
    public static void main(String[] args) {
        // Only one instance created
        Singleton object1 = Singleton.getInstance();
        Singleton object2 = Singleton.getInstance();

        object1.showMessage();

        if (object1 == object2) {
            System.out.println("Both objects are the same instance.");
        } else {
            System.out.println("Different instances (not a proper singleton).");
        }
    }
}
