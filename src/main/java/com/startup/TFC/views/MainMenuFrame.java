package com.startup.TFC.views;

import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del sistema TFC.
 * Actúa como menú de navegación hacia cada módulo.
 */
public class MainMenuFrame extends JFrame {

    private final ApplicationContext context;

    public MainMenuFrame(ApplicationContext context) {
        this.context = context;

        setTitle("Sistema de Gestión TFC - Escuela de Informática");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título
        JLabel titleLabel = new JLabel("Sistema de Gestión de Trabajos Fin de Carrera", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 8, 8));

        JButton btnStudents    = createMenuButton("👤  Alumnos");
        JButton btnProfessors  = createMenuButton("🎓  Profesores");
        JButton btnProjects    = createMenuButton("📄  Trabajos Fin de Carrera (TFC)");
        JButton btnCommittees  = createMenuButton("⚖️   Tribunales");
        JButton btnDefenses    = createMenuButton("📅  Defensas");
        JButton btnGroups      = createMenuButton("🔬  Grupos de Investigación");

        btnStudents.addActionListener(e -> openStudentFrame());
        btnProfessors.addActionListener(e -> openProfessorFrame());
        btnProjects.addActionListener(e -> openFinalProjectFrame());
        btnCommittees.addActionListener(e -> openCommitteeFrame());
        btnDefenses.addActionListener(e -> openDefenseFrame());
        btnGroups.addActionListener(e -> openResearchGroupFrame());

        buttonPanel.add(btnStudents);
        buttonPanel.add(btnProfessors);
        buttonPanel.add(btnProjects);
        buttonPanel.add(btnCommittees);
        buttonPanel.add(btnDefenses);
        buttonPanel.add(btnGroups);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("Escuela de Informática - Universidad de Cundinamarca", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.ITALIC, 10));
        footer.setForeground(Color.GRAY);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void openStudentFrame() {
        StudentFrame frame = context.getBean(StudentFrame.class);
        frame.setVisible(true);
    }

    private void openProfessorFrame() {
        ProfessorFrame frame = context.getBean(ProfessorFrame.class);
        frame.setVisible(true);
    }

    private void openFinalProjectFrame() {
        FinalProjectFrame frame = context.getBean(FinalProjectFrame.class);
        frame.setVisible(true);
    }

    private void openCommitteeFrame() {
        CommitteeFrame frame = context.getBean(CommitteeFrame.class);
        frame.setVisible(true);
    }

    private void openDefenseFrame() {
        DefenseFrame frame = context.getBean(DefenseFrame.class);
        frame.setVisible(true);
    }

    private void openResearchGroupFrame() {
        ResearchGroupFrame frame = context.getBean(ResearchGroupFrame.class);
        frame.setVisible(true);
    }
}
