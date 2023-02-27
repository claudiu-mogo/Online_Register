package Pachet1;

import java.util.*;

public abstract class Course {
    private String name;
    private Teacher titular;
    private HashSet<Assistant> assistants;
    private ArrayList<Grade> grades;
    private HashMap<String, Group> groups;
    private Strategy strategy;
    private int credits;
    public void addAssistant(String ID, Assistant assistant) {
        for (HashMap.Entry<String, Group> set : groups.entrySet()) {
            if (set.getKey().equals(ID)) {
                set.getValue().setAssistant(assistant);
                return;
            }
        }
        assistants.add(assistant);
    }

    public void addStudent(String id, Student student) {
        for (HashMap.Entry<String, Group> set :
                groups.entrySet()) {
            if (set.getKey().equals(id) && !set.getValue().contains(student)) {
                set.getValue().add(student);
                if (set.getValue().cmp != null) {
                    Collections.sort(set.getValue(), set.getValue().cmp);
                }
            }
        }
    }

    public void addGroup(Group group) {

        if (groups == null) {
            groups = new HashMap<String, Group>();
        }
        groups.put(group.getId(), group);
    }

    public void addGroup(String ID, Assistant assistant) {
        if (groups == null) {
            groups = new HashMap<String, Group>();
        }
        Group group = new Group(ID, assistant);

        if (assistants == null) {
            assistants = new HashSet<Assistant>();
        }
        assistants.add(assistant);
        group.setAssistant(assistant);

        addGroup(group);
    }

    public void addGroup(String ID, Assistant assist, Comparator<Student> comp) {
        if (groups == null) {
            groups = new HashMap<String, Group>();
        }
        Group group = new Group(ID, assist, comp);
        if (assistants == null) {
            assistants = new HashSet<Assistant>();
        }
        assistants.add(assist);

        group.setAssistant(assist);
        addGroup(group);
    }

    public Grade getGrade(Student student) {
        if (grades == null)
            return null;
        for (Grade grade : grades) {
            if (grade.getStudent().equals(student)) {
                return grade;
            }
        }
        System.out.println("Nu am gasit un grade asociat pt student");
        return null;
    }

    public void addGrade(Grade grade) {
        if (grades == null) {
            grades = new ArrayList<Grade>();
        }
        if (grade.getCourse().equals(this.getName())) {
            grades.add(grade);
        }
    }

    public ArrayList<Student> getAllStudents() {
        ArrayList<Student> arr = new ArrayList<Student>();
        for (Group group: groups.values()) {
            for (Student student: group) {
                arr.add(student);
            }
        }
        return arr;
    }

    public HashMap<Student, Grade> getAllStudentGrades() {
        HashMap<Student, Grade> map = new HashMap<Student, Grade>();
        for (Grade grade : grades) {
            map.put(grade.getStudent(), grade);
        }
        return map;
    }

    // metoda implementata in PartialCourse si FullCourse
    public abstract ArrayList<Student> getGraduatedStudents();

    // getters
    public String getName() {
        return name;
    }

    public Teacher getTitular() {
        return titular;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public HashSet<Assistant> getAssistants() {
        return assistants;
    }

    public HashMap<String, Group> getGroups() {
        return groups;
    }

    public int getCredits() {
        return credits;
    }

    public String toString() {
        String s="Nume: " + this.getName() + ", Titular: " + this.getTitular() + ", asistenti: ";
        for (Assistant assistant : assistants) {
            s += assistant + ", ";
        }
        s += "Credite: " + this.getCredits();
        s += ", Grupe: ";
        for (String i: groups.keySet()) {
            s += i + ", ";
        }
        return s;
    }

    public Student getBestStudent() {
        return strategy.getBest(grades);
    }

    // constructor pe baza builder-ului
    protected Course(CourseBuilder builder) {
        this.name = builder.name;
        this.titular = builder.titular;
        this.assistants = builder.assistants;
        this.grades = builder.grades;
        this.groups = builder.groups;
        this.strategy = builder.strategy;
        this.credits = builder.credits;
    }

    // CourseBuilder facut ca in breviar
    public static abstract class CourseBuilder {
        private String name;
        private Teacher titular;
        private HashSet<Assistant> assistants;
        private ArrayList<Grade> grades;
        private HashMap<String, Group> groups;
        private Strategy strategy;
        private int credits;

        public CourseBuilder() {

        }
        public CourseBuilder name(String name) {
            this.name = name;
            return this;
        }
        public CourseBuilder titular(Teacher titular) {
            this.titular = titular;
            return this;
        }
        public CourseBuilder assistants(HashSet<Assistant> assistants) {
            this.assistants = assistants;
            return this;
        }
        public CourseBuilder grades(ArrayList<Grade> grades) {
            this.grades = grades;
            return this;
        }
        public CourseBuilder groups(HashMap<String, Group> groups) {
            this.groups = groups;
            return this;
        }
        public CourseBuilder strategy(Strategy strategy) {
            this.strategy = strategy;
            return this;
        }
        public CourseBuilder credits(int credits) {
            this.credits = credits;
            return this;
        }
        public abstract Course build();
    }

    // Snapshot stuff
    private class Snapshot {
        private ArrayList<Grade> oldGrades;

        Snapshot() {
            oldGrades = new ArrayList<Grade>();
        }

        // getter
        public ArrayList<Grade> getOldGrades() {
            return oldGrades;
        }

        // setter
        public void setOldGrades(ArrayList<Grade> oldGrades) throws CloneNotSupportedException {
            this.oldGrades.clear();
            for (Grade grade: oldGrades) {
                this.oldGrades.add((Grade)grade.clone());
            }
        }
    }

    Snapshot snapshot;
    public void makeBackup() throws CloneNotSupportedException {
        snapshot = new Snapshot();
        snapshot.setOldGrades(grades);
    }

    public void undo() {
        grades.clear();
        if (snapshot.getOldGrades() != null) {
            for (Grade grade: snapshot.getOldGrades()) {
                grades.add(grade);
            }
        }
    }

}
