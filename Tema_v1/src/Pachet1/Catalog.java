package Pachet1;

import java.util.ArrayList;
import java.util.Vector;

public class Catalog implements Subject{
    private static Catalog ctlg = null;
    private ArrayList<Course> courses;
    private static Vector obs;

    // constructor private pt Singleton
    private Catalog() {
        courses = new ArrayList<Course>();
    }

    public static Catalog getInstance() {
        if (ctlg == null) {
            ctlg = new Catalog();
            obs = new Vector<>();

        }
        return ctlg;
    }

    public void addCourse(Course course) {
        if (courses.contains(course))
            return;
        courses.add(course);
    }

    public void removeCourse(Course course) {
        if (courses.contains(course)) {
            courses.remove(course);
        }
    }

    public Course getCourse(String name) {
        for (Course course : courses) {
            if (course.getName().equals(name)) {
                return course;
            }
        }
        return null;
    }

    public ArrayList<Course> getAllCourses() {
        return courses;
    }

    public String toString() {
        String s = "Catalog : ";
        for (Course course : courses) {
            s += course.getName() + ", ";
        }
        return s;
    }

    @Override
    public void addObserver(Observer observer) {
        if (!obs.contains(observer)) {
            obs.addElement(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        if(obs.contains(observer)) {
            obs.removeElement(observer);
        }
    }

    // trimitem notificare in functie de cat de multe parti din nota are un student
    @Override
    public void notifyObservers(Grade grade) {
        for (int i = 0; i < obs.size() ; i++) {
            if (grade.getStudent().getFather() != null && grade.getStudent().getFather().equals((Observer)obs.elementAt(i))) {
                if (grade.getPartialScore() == 0)
                    ((Observer)obs.elementAt(i)).update(new Notification(grade.getStudent().getName() +
                            " " + grade.getTotal() + " Doar examen"));
                else if (grade.getExamScore() == 0) {
                    ((Observer)obs.elementAt(i)).update(new Notification(grade.getStudent().getName() +
                            " " + grade.getTotal() + " Doar partial"));
                } else {
                    ((Observer)obs.elementAt(i)).update(new Notification(grade.getStudent().getName() +
                            " " + grade.getTotal() + " Nota completa"));
                }
            }
            if (grade.getStudent().getMother() != null && grade.getStudent().getMother().equals((Observer)obs.elementAt(i))) {
                if (grade.getPartialScore() == 0)
                    ((Observer)obs.elementAt(i)).update(new Notification(grade.getStudent().getName() +
                            " " + grade.getTotal() + " Doar examen"));
                else if (grade.getExamScore() == 0) {
                    ((Observer)obs.elementAt(i)).update(new Notification(grade.getStudent().getName() +
                            " " + grade.getTotal() + " Doar partial"));
                } else {
                    ((Observer)obs.elementAt(i)).update(new Notification(grade.getStudent().getName() +
                            " " + grade.getTotal() + " Nota completa"));
                }
            }
        }
    }
}
