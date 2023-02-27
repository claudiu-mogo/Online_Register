package Pachet1;

import java.util.ArrayList;

public class BestExamScore implements Strategy{
    public Student getBest(ArrayList<Grade> grades) {
        // judecam dupa scorul de examen
        Student ret = null;
        Double best_score = 0.0;
        for (Grade grade : grades) {
            if (grade.getExamScore() > best_score) {
                ret = grade.getStudent();
                best_score = grade.getExamScore();
            }
        }
        return ret;
    }
}
