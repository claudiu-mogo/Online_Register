package Pachet1;

public class Student extends User{
    private Parent mother, father;

    public Student(String firstName, String lastName) {
        super(firstName, lastName);
    }

    // setters
    public void setMother(Parent mother) {
        this.mother = mother;
    }
    public void setFather(Parent father) {
        this.father = father;
    }

    // getters
    public Parent getMother() {
        return mother;
    }
    public Parent getFather() {
        return father;
    }
    public String getName() {
        return super.toString();
    }

    public String toString() {
        return "Nume: " + super.toString() + ", Parintii sunt: " + getMother() + " + " + getFather();
    }
}
