package Pachet1;

import java.util.ArrayList;

public class PartialCourse extends Course {
    // studentii promovati pot recupera din cealalta componenta si doar sa aiba peste 5
    @Override
    public ArrayList<Student> getGraduatedStudents() {
        ArrayList<Student> arr = new ArrayList<Student>();
        for (Grade grade : super.getGrades()) {
            if (grade.getTotal() >= 5.0) {
                arr.add(grade.getStudent());
            }
        }
        return arr;
    }

    private PartialCourse(PartialCourseBuilder builder) {
        super(builder);
    }

    public static class PartialCourseBuilder extends CourseBuilder{

        @Override
        public Course build() {
            return new PartialCourse(this);
        }
    }

}
