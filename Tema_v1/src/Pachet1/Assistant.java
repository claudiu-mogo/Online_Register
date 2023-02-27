package Pachet1;

public class Assistant extends User implements Element{
    public Assistant(String firstName, String lastName) {
        super(firstName, lastName);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
