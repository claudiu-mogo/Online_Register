package Pachet1;

import java.util.*;

public class Group extends ArrayList<Student> implements Comparable{
    private Assistant assistant;
    private String id;
    Comparator<Student> cmp;

    @Override
    public int compareTo(Object o) {
        if (! (o instanceof Group)) {
            System.out.println("Nu a fost primit un Group la comparator");
            return 0;
        }
        Group obj = (Group) o;
        if (this.id.equals(obj.id))
            return 0;
        return this.id.compareTo(obj.id);
    }

    public Group(String id, Assistant assistant) {
        this.assistant = assistant;
        this.id = id;
        cmp = null;
    }

    public Group(String id, Assistant assistant, Comparator<Student> comp) {
        this(id, assistant);
        cmp = comp;
    }

    public Assistant getAssistant() {
        return assistant;
    }

    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean add(Student student) {
        super.add(student);
        if (cmp != null)
            Collections.sort(this, cmp);
        return true;
    }
}
