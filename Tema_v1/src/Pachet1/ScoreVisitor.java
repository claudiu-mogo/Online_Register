package Pachet1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreVisitor implements Visitor{

    private class Tuple<S, N1, N2> {
        private S stud;
        private N1 num;
        private N2 nota;

        Tuple(S stud, N1 num, N2 nota) {
            this.stud = stud;
            this.num = num;
            this.nota = nota;
        }

        // getters
        public N1 getNum() {
            return num;
        }

        public N2 getNota() {
            return nota;
        }

        public S getStud() {
            return stud;
        }

        // setters
        public void setNota(N2 nota) {
            this.nota = nota;
        }

        public void setNum(N1 num) {
            this.num = num;
        }

        public void setStud(S stud) {
            this.stud = stud;
        }

        @Override
        public String toString() {
            return stud + " " + num + " " + nota;
        }
    }

    HashMap<Teacher, ArrayList<Tuple<Student, String, Double>>> examScores;
    HashMap<Assistant, ArrayList<Tuple<Student, String, Double>>> partialScores;

    public ScoreVisitor() {
        examScores = new HashMap<Teacher, ArrayList<Tuple<Student, String, Double>>>();
        partialScores = new HashMap<Assistant, ArrayList<Tuple<Student, String, Double>>>();
    }

    public ArrayList<String> to_validate(Assistant assistant) {
        ArrayList<String> arr = new ArrayList<String>();
        for (Map.Entry<Assistant, ArrayList<Tuple<Student, String, Double>>> map : partialScores.entrySet()) {
            if (map.getKey().equals(assistant)) {
                for (Tuple tuple : map.getValue()) {
                    Course cautat = Catalog.getInstance().getCourse((String) tuple.getNum());
                    if (cautat.getAssistants().contains(assistant)) {
                        //System.out.println("Asistentul nu are laboratoare la cursul asta");
                        arr.add( ((Student)tuple.getStud()).getName() + " " + ((String) tuple.getNum()) + " " + ((Double) tuple.getNota()));
                    }
                }
            }
        }
        return arr;
    }

    public ArrayList<String> to_validate(Teacher teacher) {
        ArrayList<String> arr = new ArrayList<String>();
        for (Map.Entry<Teacher, ArrayList<Tuple<Student, String, Double>>> map : examScores.entrySet()) {
            if (map.getKey().equals(teacher)) {
                for (Tuple tuple : map.getValue()) {
                    Course cautat = Catalog.getInstance().getCourse((String) tuple.getNum());
                    if (cautat.getTitular().equals(teacher)) {
                        arr.add( ((Student)tuple.getStud()).getName() + " " + ((String) tuple.getNum()) + " " + ((Double) tuple.getNota()));
                    }
                }
            }
        }
        return arr;
    }

    public void add_to_dict(Teacher teacher, Student student, String curs, Double nota) {
        Tuple<Student, String, Double> tuple = new Tuple<Student, String, Double>(student, curs, nota);
        Course cautat = Catalog.getInstance().getCourse((String) tuple.getNum());
        if (!cautat.getTitular().equals(teacher)) {
            System.out.println("Profesorul nu are crus la materia asta");
            return;
        }
        for (Map.Entry<Teacher, ArrayList<Tuple<Student, String, Double>>> map : examScores.entrySet()) {
            if (map.getKey().equals(teacher)) {
                map.getValue().add(tuple);
                return;
            }
        }
        ArrayList<Tuple<Student, String, Double>> arrayList = new ArrayList<Tuple<Student, String, Double>>();
        arrayList.add(tuple);
        examScores.put(teacher, arrayList);
    }

    public void add_to_dict(Assistant assistant, Student student, String curs, Double nota) {
        Tuple<Student, String, Double> tuple = new Tuple<Student, String, Double>(student, curs, nota);
        Course cautat = Catalog.getInstance().getCourse((String) tuple.getNum());
        if (!cautat.getAssistants().contains(assistant)) {
            System.out.println("Asistentul nu are laboratoare la cursul asta");
            return;
        }
        for (Map.Entry<Assistant, ArrayList<Tuple<Student, String, Double>>> map : partialScores.entrySet()) {
            if (map.getKey().equals(assistant)) {
                map.getValue().add(tuple);
                return;
            }
        }
        ArrayList<Tuple<Student, String, Double>> arrayList = new ArrayList<Tuple<Student, String, Double>>();
        arrayList.add(tuple);
        partialScores.put(assistant, arrayList);
    }

    public ScoreVisitor(HashMap<Teacher, ArrayList<Tuple<Student, String, Double>>> examScores, HashMap<Assistant, ArrayList<Tuple<Student, String, Double>>> partialScores){
        this.examScores = examScores;
        this.partialScores = partialScores;
    }
    @Override
    public void visit(Assistant assistant) {
        for (Map.Entry<Assistant, ArrayList<Tuple<Student, String, Double>>> map : partialScores.entrySet()) {
            if (map.getKey().equals(assistant)) {
                for (Tuple tuple : map.getValue()) {
                    Course cautat = Catalog.getInstance().getCourse((String) tuple.getNum());
                    // daca nu exista asistentul pt cursul respectiv
                    if (!cautat.getAssistants().contains(assistant)) {
                        System.out.println("Asistentul nu are laboratoare la cursul asta");
                        return;
                    }
                    if (cautat.getGrade((Student) tuple.getStud()) == null) {
                        cautat.addGrade(new Grade((Double) tuple.getNota(), 0.0, (Student) tuple.getStud(), (String) tuple.getNum()));
                    } else {
                        cautat.getGrade((Student) tuple.getStud()).setPartialScore((Double) tuple.getNota());
                    }
                    Catalog.getInstance().notifyObservers(cautat.getGrade((Student) tuple.getStud()));
                }
                partialScores.remove(map.getKey());
            }
        }
        // partialScores.clear();
    }

    @Override
    public void visit(Teacher teacher) {
        for (Map.Entry<Teacher, ArrayList<Tuple<Student, String, Double>>> map : examScores.entrySet()) {
            if (map.getKey().equals(teacher)) {
                for (Tuple tuple : map.getValue()) {
                    Course cautat = Catalog.getInstance().getCourse((String) tuple.getNum());
                    if (!cautat.getTitular().equals(teacher)) {
                        System.out.println("Profesorul nu are crus la materia asta");
                        return;
                    }
                    if (cautat.getGrade((Student) tuple.getStud()) == null) {
                        cautat.addGrade(new Grade(0.0, (Double) tuple.getNota(), (Student) tuple.getStud(), (String) tuple.getNum()));
                    } else {
                        cautat.getGrade((Student) tuple.getStud()).setExamScore((Double) tuple.getNota());
                    }
                    Catalog.getInstance().notifyObservers(cautat.getGrade((Student) tuple.getStud()));
                }
                examScores.remove(map.getKey());
            }
        }
        // examScores.clear();
    }
}
