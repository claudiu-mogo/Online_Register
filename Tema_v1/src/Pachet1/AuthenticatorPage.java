package Pachet1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthenticatorPage extends JFrame implements ActionListener {
    JTextField nume, profesie;
    ScoreVisitor scoreVisitor;
    JLabel l1, l2;
    JButton button;

    // panel cu 2 TextFielduri si un buton
    public JPanel createPanel() {
        JPanel panel = new JPanel(null);
        panel.setSize(200, 50);
        nume = new JTextField("", 20);
        nume.setBounds(150,50, 200, 40);
        profesie = new JTextField("", 20);
        profesie.setBounds(150, 100, 200, 40);
        panel.add(nume);
        panel.add(profesie);
        l1 = new JLabel("Nume: ");
        l1.setBounds(20, 50, 150, 40);
        l2 = new JLabel("Profesie: ");
        l2.setBounds(20, 100, 150, 40);
        panel.add(l1);
        panel.add(l2);
        button = new JButton("Login");
        button.setBackground(Color.GRAY);
        button.addActionListener(this);
        button.setBounds(20, 150, 330, 30);
        panel.add(button);
        return panel;
    }

    public AuthenticatorPage(ScoreVisitor sv) {
        scoreVisitor = sv;
        setLayout(new GridLayout(1,1));
        setResizable(false);
        setTitle("Login");
        add(createPanel());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400,275);
        setVisible(true);
    }

    // cand se apasa pe "Login", se verifica daca exista userul cu profesia respectiva
    // si se apeleaza constructorul paginii respective
    @Override
    public void actionPerformed(ActionEvent e) {
        Catalog ctlg = Catalog.getInstance();
        String textFieldValue = profesie.getText();
        String textFieldNume = nume.getText();

        if(e.getSource() instanceof JButton) {
            if (textFieldValue.equals("Assistant")) {
                for (Course course: ctlg.getAllCourses()) {
                    for (Assistant assistant: course.getAssistants()) {
                        if (assistant.toString().equals(textFieldNume)) {
                            TeacherAssistantPage tap = new TeacherAssistantPage(assistant, scoreVisitor);
                            return;
                        }
                    }
                }
            }
            if (textFieldValue.equals("Teacher")) {
                for (Course course: ctlg.getAllCourses()) {
                    if (course.getTitular().toString().equals(textFieldNume)) {
                        TeacherAssistantPage tap = new TeacherAssistantPage(course.getTitular(), scoreVisitor);
                        return;
                    }
                }
            }
            if (textFieldValue.equals("Student")) {
                for (Course course: ctlg.getAllCourses()) {
                    for (Student student: course.getAllStudents()) {
                        if (student.getName().equals(textFieldNume)) {
                            StudentPage sp = new StudentPage(student);
                            return;
                        }
                    }
                }
            }
            if (textFieldValue.equals("Parent")) {
                for (Course course: ctlg.getAllCourses()) {
                    for (Student student: course.getAllStudents()) {
                        if (student.getMother() != null && student.getMother().toString().equals(textFieldNume)) {
                            ParentPage pp = new ParentPage(student.getMother());
                            return;
                        }
                        if (student.getFather() != null && student.getFather().toString().equals(textFieldNume)) {
                            ParentPage pp = new ParentPage(student.getFather());
                            return;
                        }
                    }
                }

            }
        }
    }
}
