package com.startup.TFC.views;

import com.startup.TFC.entities.Committee;
import com.startup.TFC.entities.Professor;
import com.startup.TFC.services.ServiceCommittee;
import com.startup.TFC.services.ServiceProfessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CommitteeFrame extends BaseCrudFrame {

    private final ServiceCommittee serviceCommittee;
    private final ServiceProfessor serviceProfessor;
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public CommitteeFrame(ServiceCommittee serviceCommittee, ServiceProfessor serviceProfessor) {
        super("Gestión de Tribunales", 700, 420);
        this.serviceCommittee = serviceCommittee;
        this.serviceProfessor = serviceProfessor;
    }

    @PostConstruct
    public void init() {
        loadData();
    }

    @Override
    protected void initColumns() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Lugar de Examen");
        tableModel.addColumn("Nº Componentes");
        tableModel.addColumn("Profesores");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        for (Committee c : serviceCommittee.findAll()) {
            String profs = "-";
            if (c.getProfessors() != null && !c.getProfessors().isEmpty()) {
                profs = c.getProfessors().stream()
                        .map(p -> p.getDni() + " " + p.getName())
                        .collect(Collectors.joining(", "));
            }
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getExamLocation(),
                    c.getMemberCount(),
                    profs
            });
            rowIds.add(c.getId());
        }
    }

    @Override
    protected void showAddDialog() { showCommitteeDialog(null); }

    @Override
    protected void showEditDialog(int row) {
        serviceCommittee.findById(rowIds.get(row)).ifPresent(this::showCommitteeDialog);
    }

    @Override
    protected void deleteSelected(int row) {
        serviceCommittee.deleteById(rowIds.get(row));
    }

    private void showCommitteeDialog(Committee existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Tribunal" : "Nuevo Tribunal", true);
        dialog.setSize(480, 370);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtLocation = new JTextField(isEdit ? existing.getExamLocation() : "", 20);
        JTextField txtCount    = new JTextField(
                isEdit && existing.getMemberCount() != null ? String.valueOf(existing.getMemberCount()) : "", 5);

        // Lista de profesores con selección múltiple
        List<Professor> allProfessors = new ArrayList<>();
        serviceProfessor.findAll().forEach(allProfessors::add);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        allProfessors.forEach(p -> listModel.addElement(p.getDni() + " - " + p.getName()));
        JList<String> professorList = new JList<>(listModel);
        professorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Preseleccionar los que ya están en el tribunal
        if (isEdit && existing.getProfessors() != null) {
            List<Integer> selectedIndices = new ArrayList<>();
            for (int i = 0; i < allProfessors.size(); i++) {
                final int idx = i;
                boolean selected = existing.getProfessors().stream()
                        .anyMatch(p -> p.getDni().equals(allProfessors.get(idx).getDni()));
                if (selected) selectedIndices.add(i);
            }
            int[] arr = selectedIndices.stream().mapToInt(Integer::intValue).toArray();
            professorList.setSelectedIndices(arr);
        }

        addRow(panel, gbc, 0, "Lugar de examen *:", txtLocation);
        addRow(panel, gbc, 1, "Nº componentes:", txtCount);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 1.0; gbc.gridwidth = 2;
        panel.add(new JLabel("Profesores del tribunal (Ctrl+click para selección múltiple):"), gbc);
        gbc.gridy = 3;
        panel.add(new JScrollPane(professorList), gbc);
        gbc.gridwidth = 1;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar");
        btnSave.setBackground(new Color(46, 139, 87));
        btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String location = txtLocation.getText().trim();
            if (location.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El lugar de examen es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Committee c = isEdit ? existing : new Committee();
            c.setExamLocation(location);
            try {
                c.setMemberCount(Integer.parseInt(txtCount.getText().trim()));
            } catch (NumberFormatException ex) {
                c.setMemberCount(null);
            }
            List<Professor> selected = new ArrayList<>();
            professorList.getSelectedIndices();
            for (int idx : professorList.getSelectedIndices()) {
                selected.add(allProfessors.get(idx));
            }
            c.setProfessors(selected);
            serviceCommittee.save(c);
            loadData();
            dialog.dispose();
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4; gbc.gridwidth = 1;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        p.add(field, gbc);
    }
}
