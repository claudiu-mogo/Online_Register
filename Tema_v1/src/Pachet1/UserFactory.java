package Pachet1;

public class UserFactory {
    // consideram ca tipul de utilizator primit va fi un string, acelasi cu numele clasei
    public static User getUser(String userType, String firstName, String lastName) {
        if (userType.equals("Student")) {
            return new Student(firstName, lastName);
        }
        if (userType.equals("Parent")) {
            return new Parent(firstName, lastName);
        }
        if (userType.equals("Assistant")) {
            return new Assistant(firstName, lastName);
        }
        return new Teacher(firstName, lastName);
    }
}
