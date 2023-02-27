package Pachet1;

import java.text.DecimalFormat;

public class Grade implements Comparable, Cloneable {

    private Double partialScore, examScore;
    private Student student;
    private String course; // Numele cursului

    // constructors
    public Grade() {
        partialScore = 0.0;
        examScore = 0.0;
        student = new Student("Nimic", "Nimic");
        course = "Nimic";
    }

    public Grade(Double partialScore, Double examScore, Student student, String course) {
        this.partialScore = partialScore;
        this.examScore = examScore;
        this.student = student;
        this.course = course;
    }

    public Grade(Student student, String course) {
        this(0.0, 0.0, student, course);
    }

    // getters
    public Double getPartialScore() {
        return partialScore;
    }
    public Double getExamScore() {
        return examScore;
    }
    public Student getStudent() {
        return student;
    }
    public String getCourse() {
        return course;
    }

    // setters
    public void setPartialScore(Double partialScore) {
        this.partialScore = partialScore;
    }
    public void setExamScore(Double examScore) {
        this.examScore = examScore;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
    public void setCourse(String course) {
        this.course = course;
    }

    // total
    public Double getTotal() {
        DecimalFormat df = new DecimalFormat("#.##");
        double rez = examScore + partialScore;
        return Double.parseDouble(df.format(rez));
    }
    @Override
    public int compareTo(Object o) {
        if (! (o instanceof Grade)) {
            System.out.println("Nu a fost primit un Grade la comparator");
            return 0;
        }
        Grade obj = (Grade) o;
        if (this.getTotal() - (obj).getTotal() == 0)
            return 0;
        return (this.getTotal() - (obj).getTotal() > 0 ? 1 : -1);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
