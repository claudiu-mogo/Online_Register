package Pachet1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParentPage extends JFrame implements ActionListener {
    JList<String> lista_notificari;
    JPanel current;
    String nume_user;
    Parent par;
    DefaultListModel<String> model;
    JButton button;

    // panel cu o lista basic
    public JPanel createPanel(Parent parent) {
        nume_user = parent.toString();
        model = new DefaultListModel<>();
        JPanel panel = new JPanel(new BorderLayout());

        button = new JButton("Refresh");
        button.setBackground(Color.RED);
        button.addActionListener(this);
        panel.add(button, BorderLayout.SOUTH);

        panel.setSize(100, 30);
        for (String s: parent.getNotif()) {
            model.addElement(s);
        }
        lista_notificari = new JList<String>(model);
        panel.add(new JScrollPane(lista_notificari));

        return panel;
    }

    public ParentPage(Parent parent) {
        setLayout(new GridLayout(1,1));
        setResizable(false);
        setTitle(parent.toString());
        par = parent;
        current = createPanel(parent);
        add(current);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400,200);
        setVisible(true);
    }

    // cand se apasa pe "Refresh", se reconstruieste lista de notificari a parintelui
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            model.removeAllElements();
            for (String s: par.getNotif()) {
                model.addElement(s);
            }
        }
    }
}
