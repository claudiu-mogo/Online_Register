package Pachet1;

import java.util.ArrayList;

public class BestPartialScore implements Strategy{

    @Override
    public Student getBest(ArrayList<Grade> grades) {
        // judecam dupa scorul de partial
        Student ret = null;
        Double best_score = 0.0;
        for (Grade grade : grades) {
            if (grade.getPartialScore() > best_score) {
                ret = grade.getStudent();
                best_score = grade.getPartialScore();
            }
        }
        return ret;
    }
}
