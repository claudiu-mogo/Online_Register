package Pachet1;

import java.util.ArrayList;

public class BestTotalScore implements Strategy{
    public Student getBest(ArrayList<Grade> grades) {
        // judecam dupa getTotal
        Student ret = null;
        Double best_score = 0.0;
        for (Grade grade : grades) {
            if (grade.getTotal() > best_score) {
                ret = grade.getStudent();
                best_score = grade.getTotal();
            }
        }
        return ret;
    }
}
