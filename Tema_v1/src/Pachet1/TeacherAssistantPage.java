package Pachet1;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

public class TeacherAssistantPage extends JFrame implements ActionListener {
    JList<String> lista_cursuri;
    String nume_user;
    Assistant assist;
    Teacher tch;
    ScoreVisitor scv;
    DefaultListModel<String> model1, model_grade;
    JList<String> lista_grade;
    JButton button;

    // panou cu cursurile asistentului
    public JPanel createPanel(Assistant assistant) {
        nume_user = assistant.toString();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(100, 30);

        // butonul
        button = new JButton("Validate");
        button.setBackground(Color.RED);
        button.addActionListener(this);
        panel.add(button, BorderLayout.SOUTH);

        // generare lista de cursuri
        Catalog catalog = Catalog.getInstance();
        model1 = new DefaultListModel<>();
        for (Course course: catalog.getAllCourses()) {
            if (course.getGroups() != null) {
                if (course.getAssistants().contains(assistant)) {
                    model1.addElement(course.getName());
                }
            }
        }
        lista_cursuri = new JList<String>(model1);
        panel.add(new JScrollPane(lista_cursuri));

        return panel;
    }

    public JPanel createPanel(Teacher teacher) {
        nume_user = teacher.toString();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(100, 30);

        // butonul
        button = new JButton("Validate");
        button.setBackground(Color.YELLOW);
        button.addActionListener(this);
        panel.add(button, BorderLayout.SOUTH);

        // generare lista de cursuri
        Catalog catalog = Catalog.getInstance();
        model1 = new DefaultListModel<>();
        for (Course course: catalog.getAllCourses()) {
            if (course.getGroups() != null) {
                if (course.getTitular().equals(teacher)) {
                    model1.addElement(course.getName());
                }
            }
        }
        lista_cursuri = new JList<String>(model1);
        panel.add(new JScrollPane(lista_cursuri));

        return panel;
    }

    // panou cu notele ce trebuie validate
    public JPanel note_nevalidate(ScoreVisitor sv, Assistant assistant) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(100, 30);

        // generare lista de gradeuri
        model_grade = new DefaultListModel<>();
        ArrayList<String> arr = new ArrayList<String>();
        arr = sv.to_validate(assistant);
        for (String s: arr) {
            model_grade.addElement(s);
        }
        lista_grade = new JList<String>(model_grade);
        panel.add(new JScrollPane(lista_grade));

        return panel;
    }

    public JPanel note_nevalidate(ScoreVisitor sv, Teacher tch) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(100, 30);

        // generare lista de gradeuri
        model_grade = new DefaultListModel<>();
        ArrayList<String> arr = new ArrayList<String>();
        arr = sv.to_validate(tch);
        for (String s: arr) {
            model_grade.addElement(s);
        }
        lista_grade = new JList<String>(model_grade);
        panel.add(new JScrollPane(lista_grade));

        return panel;
    }

    // constructor care uneste cele 2 panel-uri
    public TeacherAssistantPage(Assistant assistant, ScoreVisitor sv) {
        assist = assistant;
        tch = null;
        scv = sv;
        setLayout(new GridLayout(1,2));
        setResizable(false);
        setTitle(assistant.toString());
        add(createPanel(assistant));
        add(note_nevalidate(sv, assistant));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750,200);
        setVisible(true);
    }

    public TeacherAssistantPage(Teacher teacher, ScoreVisitor sv) {
        assist = null;
        tch = teacher;
        scv = sv;
        setLayout(new GridLayout(1,2));
        setResizable(false);
        setTitle(teacher.toString());
        add(createPanel(teacher));
        add(note_nevalidate(sv, teacher));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750,200);
        setVisible(true);
    }


    // cand apasam pe buton, se apeleaza metodele de visitor si se sterg elementele din lista
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            if (assist != null)
                assist.accept(scv);
            else
                tch.accept(scv);
            model_grade.removeAllElements();
        }
    }
}
