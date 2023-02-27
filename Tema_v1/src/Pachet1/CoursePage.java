package Pachet1;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CoursePage extends JFrame implements ActionListener {
    // in aceasta pagina, Gr poate reprezenta atat Grade, cat si Group
    JList<String> lista_studenti;
    JList<String> lista_note;
    JList<String> lista_asistenti;
    Course curs;

    DefaultListModel<String> model1, model2, model3;

    JTextField student, asistent, grade;
    JLabel l1, l2, l3;
    JButton button;
    // panel care afiseaza toti studentii de la curs si grupele pt fiecare in parte
    public JPanel panel_studenti(Course course) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(200, 400);
        model1 = new DefaultListModel<>();
        for (Group group: course.getGroups().values()) {
            for (Student student: group) {
                model1.addElement(student.getName() + " - " + group.getId());
            }
        }
        lista_studenti = new JList<String>(model1);
        panel.add(new JScrollPane(lista_studenti));
        return panel;
    }

    // lista cu notele (partial + examen = total) si studentii pt ele
    public JPanel panel_note(Course course) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(50, 30);
        model2 = new DefaultListModel<>();
        for (Grade grade: course.getGrades()) {
            model2.addElement(grade.getStudent().getName() + ": " + grade.getPartialScore() + " + " + grade.getExamScore() + " = " + grade.getTotal());
        }
        lista_note = new JList<String>(model2);
        panel.add(new JScrollPane(lista_note));

        return panel;
    }

    // asistentii si grupele lor
    public JPanel panel_asistenti(Course course) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(50, 30);
        model3 = new DefaultListModel<>();
        for (Group group: course.getGroups().values()) {
            model3.addElement(group.getAssistant() + " - " + group.getId());
        }
        lista_asistenti = new JList<String>(model3);
        panel.add(new JScrollPane(lista_asistenti));

        return panel;
    }

    // panel cu 3 campuri
    // Student: se adauga un nume de student
    // Gr: poate fi grade sub forma "a.b c.d" sau o grupa
    // Asistent: numele si prenumele unui asistent nou
    public JPanel inputPanel(Course course) {
        JPanel newPanel = new JPanel(null);
        student = new JTextField("", 20);
        student.setBounds(75,25, 150, 30);
        grade = new JTextField("", 20);
        grade.setBounds(75, 75, 150, 30);
        asistent = new JTextField("", 20);
        asistent.setBounds(75, 125, 150, 30);
        newPanel.add(student);
        newPanel.add(grade);
        newPanel.add(asistent);

        l1 = new JLabel("Student: ");
        l1.setBounds(20, 25, 150, 30);
        l2 = new JLabel("Gr: ");
        l2.setBounds(20, 75, 150, 30);
        l3 = new JLabel("Asistent: ");
        l3.setBounds(20, 125, 150, 30);
        newPanel.add(l1);
        newPanel.add(l2);
        newPanel.add(l3);

        // butonul
        button = new JButton("Update");
        button.setBackground(Color.ORANGE);
        button.addActionListener(this);
        button.setBounds(20, 175, 220, 40);
        newPanel.add(button);
        return newPanel;
    }

    public CoursePage(Course course) {
        curs = course;
        setLayout(new GridLayout(2,2));
        setTitle(course.getName());
        add(panel_studenti(course));
        add(panel_note(course));
        add(panel_asistenti(course));
        add(inputPanel(course));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600,500);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Catalog ctlg = Catalog.getInstance();
        String textFieldStudent = student.getText();
        String textFieldGrade = grade.getText();
        String textFieldAsistent = asistent.getText();

        if (e.getSource() instanceof JButton) {
            // vedem daca se vrea schimbarea unui asistent la grupa din "Gr"
            if (textFieldAsistent.length() > 2) {
                String[] splited = textFieldAsistent.split("\\s+");
                if (splited.length != 2 || !curs.getGroups().keySet().contains(textFieldGrade))
                    return;
                model3.removeAllElements();
                curs.addAssistant(textFieldGrade, new Assistant(splited[0], splited[1]));
                for (Group group: curs.getGroups().values()) {
                    model3.addElement(group.getAssistant() + " - " + group.getId());
                }
            } else {
                // Adaugarea unui student in grupa de la "Gr"
                if (textFieldStudent.length() > 2 && textFieldGrade.charAt(1) != '.') {
                    String[] splited = textFieldStudent.split("\\s+");
                    if (splited.length != 2 || !curs.getGroups().keySet().contains(textFieldGrade))
                        return;
                    model1.removeAllElements();
                    int ok = 1;
                    for (Student std: curs.getAllStudents()) {
                        if (std.getName().equals(splited[0] + " " + splited[1]))
                            ok = 0;
                    }
                    if (ok == 1)
                        curs.addStudent(textFieldGrade, new Student(splited[0], splited[1]));
                    for (Group group: curs.getGroups().values()) {
                        for (Student student: group) {
                            model1.addElement(student.getName() + " - " + group.getId());
                        }
                    }
                } else {
                    // Schimbarea notei Studentului cu cea trecuta la "Gr"
                    String[] splited1 = textFieldStudent.split("\\s+");
                    String[] splited2 = textFieldGrade.split("\\s+");
                    if (splited1.length != 2 || splited2.length != 2 || splited2[0].charAt(1) != '.' || splited2[1].charAt(1) != '.')
                        return;
                    double e1 = Double.parseDouble(splited2[0]);
                    double e2 = Double.parseDouble(splited2[1]);
                    for (Group group: curs.getGroups().values()) {
                        for (Student student1: group) {
                            if (student1.getName().equals(textFieldStudent)) {
                                if (curs.getAllStudentGrades().get(student1) != null) {
                                    curs.getAllStudentGrades().get(student1).setPartialScore(e1);
                                    curs.getAllStudentGrades().get(student1).setExamScore(e2);
                                } else {
                                    Grade grd = new Grade(e1, e2, student1, curs.getName());
                                    curs.addGrade(grd);
                                }
                                model2.removeAllElements();
                                for (Grade grade: curs.getGrades()) {
                                    model2.addElement(grade.getStudent().getName() + ": " + grade.getPartialScore() + " + " + grade.getExamScore() + " = " + grade.getTotal());
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
