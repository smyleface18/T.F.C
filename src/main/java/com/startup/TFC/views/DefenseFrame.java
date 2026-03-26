// ===== DefenseFrame.java =====
package com.startup.TFC.views;

import com.startup.TFC.entities.*;
import com.startup.TFC.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Component
public class DefenseFrame extends BaseCrudFrame {

    private final ServiceDefense serviceDefense;
    private final ServiceCommittee serviceCommittee;
    private final ServiceStudent serviceStudent;
    private final ServiceFinalProject serviceProject;
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public DefenseFrame(ServiceDefense serviceDefense,
                        ServiceCommittee serviceCommittee,
                        ServiceStudent serviceStudent,
                        ServiceFinalProject serviceProject) {
        super("Gestión de Defensas de TFC", 800, 420);
        this.serviceDefense   = serviceDefense;
        this.serviceCommittee = serviceCommittee;
        this.serviceStudent   = serviceStudent;
        this.serviceProject   = serviceProject;
        loadData();
    }



    @Override
    protected void initColumns() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Fecha Defensa");
        tableModel.addColumn("Tribunal");
        tableModel.addColumn("Alumno");
        tableModel.addColumn("TFC (Tema)");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Defense d : serviceDefense.findAll()) {
            String dateStr    = d.getDefenseDate() != null ? sdf.format(d.getDefenseDate()) : "-";
            String committee  = d.getCommittee() != null ? d.getCommittee().getId() + " - " + d.getCommittee().getExamLocation() : "-";
            String student    = d.getStudent() != null ? d.getStudent().getStudentId() + " - " + d.getStudent().getName() : "-";
            String project    = d.getFinalProject() != null ? d.getFinalProject().getTopic() : "-";
            tableModel.addRow(new Object[]{d.getId(), dateStr, committee, student, project});
            rowIds.add(d.getId());
        }
    }

    @Override
    protected void showAddDialog() { showDefenseDialog(null); }

    @Override
    protected void showEditDialog(int row) {
        serviceDefense.findById(rowIds.get(row)).ifPresent(this::showDefenseDialog);
    }

    @Override
    protected void deleteSelected(int row) { serviceDefense.deleteById(rowIds.get(row)); }

    private void showDefenseDialog(Defense existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Defensa" : "Nueva Defensa", true);
        dialog.setSize(430, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtDate = new JTextField(
                isEdit && existing.getDefenseDate() != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(existing.getDefenseDate()) : "", 15);

        // Combos
        JComboBox<String> cbCommittee = new JComboBox<>();
        cbCommittee.addItem("-- Seleccionar tribunal --");
        List<Committee> committees = new ArrayList<>();
        serviceCommittee.findAll().forEach(c -> { committees.add(c); cbCommittee.addItem(c.getId() + " - " + c.getExamLocation()); });

        JComboBox<String> cbStudent = new JComboBox<>();
        cbStudent.addItem("-- Seleccionar alumno --");
        List<Student> students = new ArrayList<>();
        serviceStudent.findAll().forEach(s -> { students.add(s); cbStudent.addItem(s.getStudentId() + " - " + s.getName()); });

        JComboBox<String> cbProject = new JComboBox<>();
        cbProject.addItem("-- Seleccionar TFC --");
        List<FinalProject> projects = new ArrayList<>();
        serviceProject.findAll().forEach(p -> { projects.add(p); cbProject.addItem(p.getId() + " - " + p.getTopic()); });

        if (isEdit) {
            preselectCombo(cbCommittee, committees, existing.getCommittee() != null ? existing.getCommittee().getId() : null);
            preselectComboStr(cbStudent, students, existing.getStudent() != null ? Long.valueOf(existing.getStudent().getStudentId()) : null);
            preselectCombo(cbProject, projects, existing.getFinalProject() != null ? existing.getFinalProject().getId() : null);
        }

        addRow(panel, gbc, 0, "Fecha (dd/MM/yyyy):", txtDate);
        addRow(panel, gbc, 1, "Tribunal:", cbCommittee);
        addRow(panel, gbc, 2, "Alumno:", cbStudent);
        addRow(panel, gbc, 3, "TFC:", cbProject);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar"); btnSave.setBackground(new Color(46,139,87)); btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            Defense def = isEdit ? existing : new Defense();
            String ds = txtDate.getText().trim();
            if (!ds.isEmpty()) {
                try { def.setDefenseDate(new SimpleDateFormat("dd/MM/yyyy").parse(ds)); }
                catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Fecha inválida. Use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE); return; }
            }
            def.setCommittee(cbCommittee.getSelectedIndex() > 0 ? committees.get(cbCommittee.getSelectedIndex()-1) : null);
            def.setStudent(cbStudent.getSelectedIndex() > 0 ? students.get(cbStudent.getSelectedIndex()-1) : null);
            def.setFinalProject(cbProject.getSelectedIndex() > 0 ? projects.get(cbProject.getSelectedIndex()-1) : null);
            serviceDefense.save(def);
            loadData();
            dialog.dispose();
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private <T> void preselectCombo(JComboBox<String> cb, List<T> list, Long id) {
        if (id == null) return;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Committee c && c.getId().equals(id)) { cb.setSelectedIndex(i+1); return; }
            if (list.get(i) instanceof FinalProject fp && fp.getId().equals(id)) { cb.setSelectedIndex(i+1); return; }
        }
    }

    private void preselectComboStr(JComboBox<String> cb, List<Student> list, Long id) {
        if (id == null) return;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStudentId().equals(id)) { cb.setSelectedIndex(i+1); return; }
        }
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        p.add(field, gbc);
    }
}
