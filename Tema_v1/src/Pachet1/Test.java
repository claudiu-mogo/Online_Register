package Pachet1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;

public class Test {
    public static void main(String args[]) throws Exception {
        Catalog catalog = Catalog.getInstance();
        ScoreVisitor sv = new ScoreVisitor();

        Object obj = new JSONParser().parse(new FileReader("Test_oficial.json"));
        JSONObject jo = (JSONObject) obj;

        // iterate through all courses
        JSONArray courses = (JSONArray) jo.get("courses");
        Iterator iterator_courses = courses.iterator();
        while (iterator_courses.hasNext()) {
            // parse data for a single course
            JSONObject course = (JSONObject) iterator_courses.next();
            String type = (String) course.get("type");
            String strategy = (String) course.get("strategy");
            String name = (String) course.get("name");

            // teacher
            JSONObject teacher = (JSONObject) course.get("teacher");
            String nume = (String) teacher.get("lastName");
            String prenume = (String) teacher.get("firstName");
            User tchr = UserFactory.getUser("Teacher", prenume, nume);

            // assistants
            JSONArray assistantsArray = (JSONArray) course.get("assistants");
            HashSet<Assistant> assistants_course = new HashSet<>();
            Iterator assistants_iterator = assistantsArray.iterator();
            while (assistants_iterator.hasNext()) {
                JSONObject assistant_instance = (JSONObject) assistants_iterator.next();
                nume = (String) assistant_instance.get("lastName");
                prenume = (String) assistant_instance.get("firstName");
                User assist = UserFactory.getUser("Assistant", prenume, nume);
                assistants_course.add((Assistant) assist);
            }

            // Groups
            JSONArray groupsArray = (JSONArray) course.get("groups");
            HashMap<String, Group> groups_course = new HashMap<>();
            Iterator groups_iterator = groupsArray.iterator();
            while (groups_iterator.hasNext()) {
                JSONObject group_instance = (JSONObject) groups_iterator.next();
                String id = (String) group_instance.get("ID");

                // asistent curs
                JSONObject assistant_instance = (JSONObject) group_instance.get("assistant");
                nume = (String) assistant_instance.get("lastName");
                prenume = (String) assistant_instance.get("firstName");
                for (Assistant assistant: assistants_course) {
                    if (assistant.toString().equals(prenume + " " + nume)) {
                        groups_course.put(id, new Group(id, assistant, new Comparator<Student>() {
                            @Override
                            public int compare(Student o1, Student o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        }));
                        System.out.println(id);
                    }
                }

                // studenti curs
                JSONArray studentsArray = (JSONArray) group_instance.get("students");
                Iterator students_iterator = studentsArray.iterator();
                while (students_iterator.hasNext()) {
                    JSONObject unStudent = (JSONObject) students_iterator.next();
                    nume = (String) unStudent.get("lastName");
                    prenume = (String) unStudent.get("firstName");
                    int ok = 1;
                    Student save = null;
                    for (Course alteCursuri: catalog.getAllCourses()) {
                        for (Student std : alteCursuri.getAllStudents()) {
                            if (std.getName().equals(prenume + " " + nume)) {
                                ok = 0;
                                save = std;
                            }

                        }
                    }
                    if (ok == 1) {
                        User theStudent = UserFactory.getUser("Student", prenume, nume);

                        // parintii
                        JSONObject father = (JSONObject) unStudent.get("father");
                        if (father != null) {
                            User pop = UserFactory.getUser("Parent", (String) father.get("firstName"), (String) father.get("lastName"));
                            catalog.addObserver((Parent) pop);
                            ((Student) theStudent).setFather((Parent) pop);
                        } else {
                            ((Student) theStudent).setFather(null);
                        }

                        JSONObject mother = (JSONObject) unStudent.get("mother");
                        if (mother != null) {
                            User mom = UserFactory.getUser("Parent", (String) mother.get("firstName"), (String) mother.get("lastName"));
                            catalog.addObserver((Parent) mom);
                            ((Student) theStudent).setMother((Parent) mom);
                        } else {
                            ((Student) theStudent).setMother(null);
                        }

                        groups_course.get(id).add((Student) theStudent);
                    } else {
                        groups_course.get(id).add(save);
                    }

                }
            }

            // get the strategy
            Strategy strtg;
            if (strategy.equals("BestExamScore"))
                strtg = new BestExamScore();
            else if (strategy.equals("BestPartialScore"))
                strtg = new BestPartialScore();
            else strtg = new BestTotalScore();

            if (type.equals("FullCourse")) {
                Course course1 = new FullCourse.FullCourseBuilder().name(name).titular((Teacher) tchr)
                        .assistants(assistants_course).groups(groups_course).strategy(strtg).credits(5).build();
                catalog.addCourse(course1);
                System.out.println("Curs: "+ course1);
            } else {
                Course course1 = new PartialCourse.PartialCourseBuilder().name(name).titular((Teacher) tchr)
                        .assistants(assistants_course).groups(groups_course).strategy(strtg).credits(5).build();
                catalog.addCourse(course1);
                System.out.println("Curs: "+ course1);
            }
            System.out.println(catalog);

        }

        // Grades
        JSONArray examScores = (JSONArray) jo.get("examScores");
        Iterator gradesIterator = examScores.iterator();
        while (gradesIterator.hasNext()) {
            JSONObject gradeInstance = (JSONObject) gradesIterator.next();
            JSONObject teacher = (JSONObject) gradeInstance.get("teacher");
            String numeProf = teacher.get("firstName") + " " + teacher.get("lastName");
            JSONObject student = (JSONObject) gradeInstance.get("student");
            String numeStud = student.get("firstName") + " " + student.get("lastName");
            String numeCurs = (String) gradeInstance.get("course");
            System.out.println(numeStud);
            Double grade;
            if (gradeInstance.get("grade") instanceof Double) {
                grade = (Double) gradeInstance.get("grade");
            } else {
                grade = (Long) gradeInstance.get("grade") * 1.0;
            }
            for (Course course: catalog.getAllCourses()) {
                if (course.getTitular().toString().equals(numeProf) && course.getName().equals(numeCurs)) {
                    for (Student st: course.getAllStudents()) {
                        if (st.getName().equals(numeStud)) {
                            sv.add_to_dict(course.getTitular(), st, numeCurs, grade);
                            course.getTitular().accept(sv);
                        }
                    }
                }
            }
        }

        JSONArray partialScores = (JSONArray) jo.get("partialScores");
        gradesIterator = partialScores.iterator();
        while (gradesIterator.hasNext()) {
            JSONObject gradeInstance = (JSONObject) gradesIterator.next();
            JSONObject assistant = (JSONObject) gradeInstance.get("assistant");
            String numeProf = assistant.get("firstName") + " " + assistant.get("lastName");
            JSONObject student = (JSONObject) gradeInstance.get("student");
            String numeStud = student.get("firstName") + " " + student.get("lastName");
            String numeCurs = (String) gradeInstance.get("course");
            Double grade;
            if (gradeInstance.get("grade") instanceof Double) {
                grade = (Double) gradeInstance.get("grade");
            } else {
                grade = (Long) gradeInstance.get("grade") * 1.0;
            }
            for (Course course: catalog.getAllCourses()) {
                if (course.getName().equals(numeCurs)) {
                    for (Assistant assistant1: course.getAssistants()) {
                        if (assistant1.toString().equals(numeProf)) {
                            for (Student st: course.getAllStudents()) {
                                if (st.getName().equals(numeStud)) {
                                    sv.add_to_dict(assistant1, st, numeCurs, grade);
                                    assistant1.accept(sv);
                                }
                            }
                        }
                    }
                }
            }
        }

        // teste suplimentare
        ArrayList<Course> courseArrayList = new ArrayList<>();
        for (Course course: catalog.getAllCourses()) {
            courseArrayList.add(course);
        }

        System.out.println("Grades so far:");
        System.out.println(courseArrayList.get(0).getName());
        for (Grade grade: courseArrayList.get(0).getGrades()) {
            System.out.print(grade.getStudent().getName() + ", " + grade.getTotal() + "; ");
        }
        System.out.print('\n');
        System.out.println("Best student: " + courseArrayList.get(0).getBestStudent().getName());
        try {
            courseArrayList.get(0).makeBackup();
        } catch (Exception e) {
            System.out.println("atat s-a putut");
        }
        User newStudent = UserFactory.getUser("Student", "Cameliu", "Ana");
        courseArrayList.get(0).addStudent("312CC", (Student) newStudent);
        courseArrayList.get(0).addGrade(new Grade(3.2, 0.0, (Student) newStudent, courseArrayList.get(0).getName()));
        for (Student student: courseArrayList.get(0).getAllStudents()) {
            if (student.getName().equals("Sebastian Moisescu")) {
                courseArrayList.get(0).getGrade(student).setExamScore(2.2);
            }
        }
        System.out.println("All new grades");
        for (Grade grade: courseArrayList.get(0).getGrades()) {
            System.out.print(grade.getStudent().getName() + ", " + grade.getTotal() + "; ");
        }
        System.out.print('\n');
        courseArrayList.get(0).undo();
        System.out.println("Grades dupa undo");
        for (Grade grade: courseArrayList.get(0).getGrades()) {
            System.out.print(grade.getStudent().getName() + ", " + grade.getTotal() + "; ");
        }
        System.out.print('\n');
        // Nu ar trebui sa functioneze, grade-ul este null:
        // System.out.println("Nota student nou: " + courseArrayList.get(0).getGrade((Student) newStudent).getTotal());
        // dar studentul exista in cursul respectiv:
        System.out.println("Student nou: " + courseArrayList.get(0).getAllStudents().contains((Student) newStudent));


        // adaugare grupa cu student nou
        User newStudent2 = UserFactory.getUser("Student", "Robert", "Manea");
        Parent mamaTest = new Parent("O", "Mama");
        ((Student) newStudent2).setMother(mamaTest);
        catalog.addObserver(mamaTest);
        for (Assistant assistant: courseArrayList.get(0).getAssistants()) {
            if (assistant.toString().equals("Andrei Georgescu")) {
                Group newGroup = new Group("321CC", assistant);
                newGroup.add((Student) newStudent2);
                courseArrayList.get(0).addGroup(newGroup);
                Grade ceva = new Grade(4.5, 0.0, (Student) newStudent2, courseArrayList.get(0).getName());
                courseArrayList.get(0).addGrade(ceva);
                catalog.notifyObservers(ceva);
            }
        }
        sv.add_to_dict(courseArrayList.get(0).getTitular(), (Student) newStudent2, courseArrayList.get(0).getName(), 2.35);


        AuthenticatorPage ap1 = new AuthenticatorPage(sv);
        for (Course course: catalog.getAllCourses()) {
            CoursePage cp = new CoursePage(course);
        }

    }
}
