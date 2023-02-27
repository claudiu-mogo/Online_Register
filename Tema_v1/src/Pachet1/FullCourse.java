package Pachet1;

import java.util.ArrayList;

public class FullCourse extends Course {
    // studentii promovati au peste jumatate din punctaj in fiecare categorie (partial si examen)
    @Override
    public ArrayList<Student> getGraduatedStudents() {
        ArrayList<Student> arr = new ArrayList<Student>();
        for (Grade grade : super.getGrades()) {
            if (grade.getPartialScore() >= 3.0 && grade.getExamScore() >= 2.0) {
                arr.add(grade.getStudent());
            }
        }
        return arr;
    }

    private FullCourse(FullCourseBuilder builder) {
        super(builder);
    }
    public static class FullCourseBuilder extends CourseBuilder{

        @Override
        public Course build() {
            return new FullCourse(this);
        }
    }
}
