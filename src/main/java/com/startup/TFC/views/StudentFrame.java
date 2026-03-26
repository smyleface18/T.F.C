package com.startup.TFC.views;

import com.startup.TFC.entities.Student;
import com.startup.TFC.services.ServiceStudent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class StudentFrame extends JFrame {

    private ServiceStudent serviceStudent;
    private JTable table;
    private DefaultTableModel model;

    @Autowired
    public StudentFrame(ServiceStudent serviceStudent) {
        this.serviceStudent = serviceStudent;

        setTitle("Lista de Estudiantes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadStudents();
    }

    private void initComponents() {
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("DNI");
        model.addColumn("Nombre");

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnRefresh = new JButton("Recargar");
        btnRefresh.addActionListener(e -> loadStudents());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);

        add(panel);
    }

    private void loadStudents() {
        model.setRowCount(0); // limpiar tabla

        Iterable<Student> students = serviceStudent.findAll();

        for (Student s : students) {
            model.addRow(new Object[]{
                    s.getStudentId(),
                    s.getDni(),
                    s.getName()
            });
        }
    }
}