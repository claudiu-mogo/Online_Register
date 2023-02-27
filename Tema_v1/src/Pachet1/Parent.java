package Pachet1;

import java.util.ArrayList;

public class Parent extends User implements Observer{


    private ArrayList<String> arr;
    public Parent(String firstName, String lastName) {
        super(firstName, lastName);
        arr = new ArrayList<String>();
    }

    public ArrayList<String> getNotif() {
        return arr;
    }

    @Override
    public void update(Notification notification) {
        System.out.println("S-a primit: " + notification);
        arr.add(notification + "");
    }
}
