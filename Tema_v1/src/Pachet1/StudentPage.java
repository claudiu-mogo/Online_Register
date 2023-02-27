package Pachet1;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class StudentPage extends JFrame implements ListSelectionListener {
    JList<String> lista_cursuri;
    String nume_stud;
    DefaultListModel<String> model;
    DefaultListModel<Assistant> model2;
    JList<Assistant> lista_asistenti;
    JTextField nume, titular, asistent, partial, examen;
    JLabel l1, l2, l3, l4, l5, l6;

    // panou pentru lista de cursuri
    public JPanel createPanel(Student student) {
        nume_stud = student.getName();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(50, 30);

        // generare lista de cursuri
        Catalog catalog = Catalog.getInstance();
        model = new DefaultListModel<>();
        for (Course course: catalog.getAllCourses()) {
            if (course.getGroups() != null) {
                if (course.getAllStudents().contains(student)) {
                    model.addElement(course.getName());
                }
            }
        }
        lista_cursuri = new JList<String>(model);
        lista_cursuri.addListSelectionListener(this);
        panel.add(new JScrollPane(lista_cursuri));

        return panel;
    }

    // panou cu text-fielduri pentru datele dintr-un curs
    public JPanel date_curs(Student student) {
        JPanel newPanel = new JPanel(null);
        nume = new JTextField("", 20);
        nume.setBounds(150,50, 200, 40);
        titular = new JTextField("", 20);
        titular.setBounds(150, 100, 200, 40);
        asistent = new JTextField("", 20);
        asistent.setBounds(150, 150, 200, 40);
        partial = new JTextField("", 20);
        partial.setBounds(150, 200, 200, 40);
        examen = new JTextField("", 20);
        examen.setBounds(150, 250, 200, 40);
        newPanel.add(nume);
        newPanel.add(titular);
        newPanel.add(asistent);
        newPanel.add(partial);
        newPanel.add(examen);
        l1 = new JLabel("Nume: ");
        l1.setBounds(20, 50, 150, 40);
        l2 = new JLabel("Titular: ");
        l2.setBounds(20, 100, 150, 40);
        l3 = new JLabel("Asistent: ");
        l3.setBounds(20, 150, 150, 40);
        l4 = new JLabel("Nota Partial: ");
        l4.setBounds(20, 200, 150, 40);
        l5 = new JLabel("Nota Examen: ");
        l5.setBounds(20, 250, 150, 40);
        l6 = new JLabel("Lista Asistenti: ");
        l6.setBounds(20, 300, 150, 40);
        newPanel.add(l1);
        newPanel.add(l2);
        newPanel.add(l3);
        newPanel.add(l4);
        newPanel.add(l5);
        newPanel.add(l6);

        JPanel panel_mic = new JPanel(new BorderLayout());
        panel_mic.setBounds(150, 300, 200, 200);
        // lista asistenti
        model2 = new DefaultListModel<Assistant>();
        lista_asistenti = new JList<Assistant>(model2);
        panel_mic.add(new JScrollPane(lista_asistenti));
        newPanel.add(panel_mic);
        return newPanel;
    }

    // constructor care adauga panourile
    public StudentPage(Student student) {
        setLayout(new GridLayout(1,2));
        setTitle(student.getName());
        add(createPanel(student));
        add(date_curs(student));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000,600);
        setVisible(true);
    }

    // cand apasam pe o valoare din lista se modifica TextFieldurile
    // daca o nota este 0.0, inseamna ca in acea categorie nu s-a adaugat inca nota
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(lista_cursuri.isSelectionEmpty())
            return;
        model2.removeAllElements();
        for (Course course: Catalog.getInstance().getAllCourses()) {
            if (course.getName().equals((String)lista_cursuri.getSelectedValue())) {
                nume.setText(nume_stud);
                titular.setText(course.getTitular().toString());
                partial.setText("nu e grade trecut");
                examen.setText("nu e grade trecut");
                for (Student student: course.getAllStudents()) {
                    if (nume_stud.equals(student.getName())) {
                        for (Assistant assistant: course.getAssistants()) {
                            model2.addElement(assistant);
                        }
                        for (Group gr: course.getGroups().values()) {
                            if (gr.contains(student)) {
                                asistent.setText(gr.getAssistant().toString());
                            }
                        }
                        if (course.getAllStudentGrades().get(student) != null) {
                            if (course.getAllStudentGrades().get(student).getPartialScore() != 0)
                                partial.setText(course.getAllStudentGrades().get(student).getPartialScore().toString());
                            if (course.getAllStudentGrades().get(student).getExamScore() != 0)
                                examen.setText(course.getAllStudentGrades().get(student).getExamScore().toString());
                        }
                    }
                }
            }
        }
    }
}