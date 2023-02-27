package Pachet1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test_neoficial {

    private static String[] parse_input(Iterator itr2) {
        Iterator<Map.Entry> itr1 = ((Map) itr2.next()).entrySet().iterator();
        String[] ret = new String[2];
        String nume = "";
        String prenume = "";
        while (itr1.hasNext()) {
            Map.Entry date = itr1.next();
            if (date.getKey().equals("Nume"))
                nume = date.getValue().toString();
            if (date.getKey().equals("Prenume"))
                prenume = date.getValue().toString();
        }
        ret[1] = nume;
        ret[0] = prenume;
        return ret;
    }

    public static void main(String args[]) throws Exception {
        Catalog catalog = Catalog.getInstance();
        ScoreVisitor sv = new ScoreVisitor();
        Iterator<Map.Entry> itr1;
        Iterator itr2;

        Object obj = new JSONParser().parse(new FileReader("JSONExample.json"));
        JSONObject jo = (JSONObject) obj;

        ArrayList<User> studenti = new ArrayList<User>(10);
        ArrayList<User> mothers = new ArrayList<>(10);
        ArrayList<User> fathers = new ArrayList<>(10);
        ArrayList<User> assistants = new ArrayList<>(10);
        ArrayList<User> teachers = new ArrayList<>(10);

        // asistenti
        JSONArray ja_assistants = (JSONArray) jo.get("Assistants");
        itr2 = ja_assistants.iterator();
        while (itr2.hasNext())
        {
            String[] nume = parse_input(itr2);
            User assistant = UserFactory.getUser("Assistant", nume[0], nume[1]);
            assistants.add(assistant);
        }

        // profesori
        JSONArray ja_teachers = (JSONArray) jo.get("Teachers");
        itr2 = ja_teachers.iterator();
        while (itr2.hasNext())
        {
            String[] nume = parse_input(itr2);
            User teacher = UserFactory.getUser("Teacher", nume[0], nume[1]);
            teachers.add(teacher);
        }

        // parinti
        JSONArray ja_mother = (JSONArray) jo.get("Mothers");
        itr2 = ja_mother.iterator();
        while (itr2.hasNext())
        {
            String[] nume = parse_input(itr2);
            User mother = UserFactory.getUser("Parent", nume[0], nume[1]);
            mothers.add(mother);
            catalog.addObserver((Parent) mother);
        }

        JSONArray ja_father = (JSONArray) jo.get("Fathers");
        itr2 = ja_father.iterator();
        while (itr2.hasNext()) {
            String[] nume = parse_input(itr2);
            User father = UserFactory.getUser("Parent", nume[0], nume[1]);
            fathers.add(father);
            catalog.addObserver((Parent) father);
        }

        // citire studenti
        JSONArray ja = (JSONArray) jo.get("Students");
        itr2 = ja.iterator();
        while (itr2.hasNext()) {
            itr1 = ((Map) itr2.next()).entrySet().iterator();
            String nume_stud = "";
            String prenume_stud = "";
            String nume_mama = "";
            String nume_tata = "";
            while (itr1.hasNext()) {
                Map.Entry date = itr1.next();
                if (date.getKey().equals("Nume"))
                    nume_stud = date.getValue().toString();
                if (date.getKey().equals("Prenume"))
                    prenume_stud = date.getValue().toString();
                if (date.getKey().equals("Mama"))
                    nume_mama = date.getValue().toString();
                if (date.getKey().equals("Tata"))
                    nume_tata = date.getValue().toString();
            }
            User studentul = UserFactory.getUser("Student", prenume_stud, nume_stud);
            for (User mom: mothers) {
                if (((Parent) mom).toString().equals(nume_mama)) {
                    ((Student)studentul).setMother((Parent) mom);
                }
            }
            for (User pop: fathers) {
                if (((Parent) pop).toString().equals(nume_tata)) {
                    ((Student)studentul).setFather((Parent) pop);
                }
            }
            studenti.add(studentul);
        }

        // set-up curs 1 (cu grupe si gradeuri dinainte) + citire din fisier
        JSONObject jo_course1 = (JSONObject) jo.get("Course1");
        HashSet<Assistant> set1 = new HashSet<>();
        HashMap<String, Group> grupe1 = new HashMap<String, Group>();
        Strategy strtg;
        Teacher titular = null;
        int credits1 = Integer.parseInt((String) jo_course1.get("Credits"));
        String titular1_name = (String) jo_course1.get("Titular");
        String course1_name = (String) jo_course1.get("Nume_curs") ;
        String strategy1_name = (String) jo_course1.get("Strategy");
        if (strategy1_name.equals("BestExamScore"))
            strtg = new BestExamScore();
        else if (strategy1_name.equals("BestPartialScore"))
            strtg = new BestPartialScore();
        else strtg = new BestTotalScore();
        for (User t: teachers) {
            if (t.toString().equals(titular1_name)){
                titular = (Teacher) t;
            }
        }

        Map asistenti1 = (Map) jo_course1.get("Asistenti");
        Iterator<Map.Entry> iterator_as = asistenti1.entrySet().iterator();
        while(iterator_as.hasNext()) {
            Map.Entry date = iterator_as.next();
            for (User a: assistants) {
                if (a.toString().equals(date.getValue())) {
                    set1.add((Assistant) a);
                    grupe1.put((String) date.getKey(), new Group((String) date.getKey(), (Assistant) a));
                }
            }
        }

        Map grupe_1 = (Map) jo_course1.get("Groups");
        Iterator<Map.Entry> itr3 = grupe_1.entrySet().iterator();
        while (itr3.hasNext()) {
            Map.Entry date = itr3.next();
            String id = (String) date.getKey();
            JSONArray ja_studenti_grupa = (JSONArray) date.getValue();
            itr2 = ja_studenti_grupa.iterator();
            while (itr2.hasNext()) {
                String nume_de_cautat = (String) itr2.next();
                for (User s: studenti) {
                    if (((Student)s).getName().equals(nume_de_cautat)) {
                        grupe1.get(id).add((Student) s);
                    }
                }
            }
        }
        ArrayList<Grade> grds1 = new ArrayList<Grade>();
        grds1.add(new Grade(4.1, 4.0, (Student) studenti.get(0), "POO"));
        grds1.add(new Grade(5.2, 3.0, (Student) studenti.get(1), "POO"));
        Course course1 = new FullCourse.FullCourseBuilder().name(course1_name).titular(titular)
                .assistants(set1).groups(grupe1).grades(grds1).strategy(strtg).credits(credits1).build();
        System.out.println("Curs: "+ course1);
        System.out.println("Best Student: " + course1.getBestStudent());
        catalog.addCourse(course1);
        System.out.println(catalog);


        // set-up curs 2 (cu adaugare dupa build)
        Course course2 = new FullCourse.FullCourseBuilder().name("USO").titular((Teacher) teachers.get(1)).strategy(new BestExamScore()).credits(4).build();
        course2.addGroup("321", (Assistant) assistants.get(2));
        course2.addGroup("322", (Assistant) assistants.get(3));
        course2.addStudent("321", (Student) studenti.get(0));
        course2.addStudent("321", (Student) studenti.get(1));
        course2.addStudent("321", (Student) studenti.get(4));
        course2.addStudent("322", (Student) studenti.get(2));
        course2.addStudent("322", (Student) studenti.get(3));
        course2.addStudent("322", (Student) studenti.get(5));

        catalog.addCourse(course2);
        System.out.println(catalog);
        course2.addGrade(new Grade(4.4, 0.0, (Student) studenti.get(0), "USO"));
        course2.addGrade(new Grade(5.2, 3.4, (Student) studenti.get(1), "USO"));

        // Nota pusa de alt prof
        sv.add_to_dict((Teacher) teachers.get(0), (Student) studenti.get(2), "USO", 2.6);

        // testare pagini
        StudentPage sp = new StudentPage((Student) studenti.get(0));
        sv.add_to_dict((Assistant) assistants.get(0), (Student) studenti.get(1), "POO", 6.0);
        sv.add_to_dict((Assistant) assistants.get(0), (Student) studenti.get(3), "POO", 3.2);
        System.out.println("Starea 1" + ((Parent) fathers.get(1)).getNotif());
        AuthenticatorPage ap1 = new AuthenticatorPage(sv);
        AuthenticatorPage ap2 = new AuthenticatorPage(sv);

        // test back-up
        System.out.println("Grades so far:");
        for (Grade grade: course1.getGrades()) {
            System.out.print(grade.getStudent().getName() + ", " + grade.getTotal() + "; ");
        }
        System.out.print('\n');
        try {
            course1.makeBackup();
        } catch (Exception e) {
            System.out.println("atat s-a putut");
        }
        course1.addGrade(new Grade(3.2, 3.3, (Student) studenti.get(4), "POO"));
        course1.getGrade((Student) studenti.get(0)).setPartialScore(5.2);
        System.out.println("All new grades");
        for (Grade grade: course1.getGrades()) {
            System.out.print(grade.getStudent().getName() + ", " + grade.getTotal() + "; ");
        }
        System.out.print('\n');
        course1.undo();
        System.out.println("Grades dupa undo");
        for (Grade grade: course1.getGrades()) {
            System.out.print(grade.getStudent().getName() + ", " + grade.getTotal() + "; ");
        }
        System.out.print('\n');

        CoursePage cp = new CoursePage(course1);
    }

    // am folosit acest main alternativ ca sa generez JSON-ul. De aceea am zis sa il las comentat
    /*
    public static void main(String args[]) throws FileNotFoundException {
        JSONObject jo = new JSONObject();

        Map m;
        JSONArray ja_student = new JSONArray();

        m = new LinkedHashMap(4);
        m.put("Prenume", "Anuta");
        m.put("Nume", "Blandiana");
        m.put("Mama", "Ana Blandiana");
        m.put("Tata", "Anul Blandiana");
        ja_student.add(m);

        m = new LinkedHashMap(4);
        m.put("Prenume", "Khaby");
        m.put("Nume", "Lame");
        m.put("Mama", "Mama Paul");
        m.put("Tata", "Tata Paul");
        ja_student.add(m);

        m = new LinkedHashMap(4);
        m.put("Prenume", "Jake");
        m.put("Nume", "Paul");
        m.put("Mama", "Mama Paul");
        m.put("Tata", "Tata Paul");
        ja_student.add(m);

        m = new LinkedHashMap(4);
        m.put("Prenume", "Logan");
        m.put("Nume", "Paul");
        m.put("Mama", "Mama Paul");
        m.put("Tata", "Tata Paul");
        ja_student.add(m);

        m = new LinkedHashMap(4);
        m.put("Prenume", "Alt");
        m.put("Nume", "Nume");
        m.put("Mama", "Mama Nume");
        m.put("Tata", "Tata Nume");
        ja_student.add(m);

        m = new LinkedHashMap(4);
        m.put("Prenume", "Mario");
        m.put("Nume", "Razvan");
        m.put("Mama", "Mama 1");
        m.put("Tata", "Mama 2");
        ja_student.add(m);

        // putting phoneNumbers to JSONObject
        jo.put("Students", ja_student);

        // parinti
        JSONArray ja_mother = new JSONArray();
        m = new LinkedHashMap(2);
        m.put("Prenume", "Ana");
        m.put("Nume", "Blandiana");
        ja_mother.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Mama");
        m.put("Nume", "Paul");
        ja_mother.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Mama");
        m.put("Nume", "Nume");
        ja_mother.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Mama");
        m.put("Nume", "1");
        ja_mother.add(m);

        jo.put("Mothers", ja_mother);

        JSONArray ja_father = new JSONArray();
        m = new LinkedHashMap(2);
        m.put("Prenume", "Anul");
        m.put("Nume", "Blandiana");
        ja_father.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Tata");
        m.put("Nume", "Paul");
        ja_father.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Tata");
        m.put("Nume", "Nume");
        ja_father.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Mama");
        m.put("Nume", "2");
        ja_father.add(m);

        jo.put("Fathers", ja_father);

        // asistenti

        JSONArray ja_asist = new JSONArray();
        m = new LinkedHashMap(2);
        m.put("Prenume", "Edi");
        m.put("Nume", "Staniloiu");
        ja_asist.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Andreia");
        m.put("Nume", "Ocanoaia");
        ja_asist.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Andreea");
        m.put("Nume", "Nica");
        ja_asist.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Ceva");
        m.put("Nume", "Asistent");
        ja_asist.add(m);

        jo.put("Assistants", ja_asist);

        // profi
        JSONArray ja_teach = new JSONArray();
        m = new LinkedHashMap(2);
        m.put("Prenume", "Razvan");
        m.put("Nume", "Deaconescu");
        ja_teach.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Razvan");
        m.put("Nume", "Rughinis");
        ja_teach.add(m);

        m = new LinkedHashMap(2);
        m.put("Prenume", "Luminita");
        m.put("Nume", "Costache");
        ja_teach.add(m);

        jo.put("Teachers", ja_teach);

        // curs1
        JSONObject j_course = new JSONObject();
        j_course.put("Nume_curs", "POO");
        j_course.put("Titular", "Razvan Deaconescu");
        Map grupe_curs = new LinkedHashMap(2);
        ArrayList<String> st1 = new ArrayList<String>();
        st1.add("Anuta Blandiana");
        st1.add("Khaby Lame");
        st1.add("Alt nume");
        ArrayList<String> st2 = new ArrayList<String>();
        st2.add("Jake Paul");
        st2.add("Logan Paul");
        st2.add("Mario Razvan");
        grupe_curs.put("321", st1);
        grupe_curs.put("322", st2);
        j_course.put("Groups", grupe_curs);
        j_course.put("Credits", 5);
        j_course.put("Strategy", "BestExamScore");

        Map asistenti_curs = new LinkedHashMap(2);
        asistenti_curs.put("321", "Edi Staniloiu");
        asistenti_curs.put("322", "Andreia Ocanoaia");

        j_course.put("Asistenti", asistenti_curs);

        jo.put("Course1", j_course);

        // writing JSON to file:"JSONExample.json" in cwd
        PrintWriter pw = new PrintWriter("JSONExample.json");
        pw.write(jo.toJSONString());

        pw.flush();
        pw.close();
    } */
}
