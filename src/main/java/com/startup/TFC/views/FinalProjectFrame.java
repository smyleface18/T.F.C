package com.startup.TFC.views;

import com.startup.TFC.entities.FinalProject;
import com.startup.TFC.entities.Student;
import com.startup.TFC.services.ServiceFinalProject;
import com.startup.TFC.services.ServiceStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class FinalProjectFrame extends BaseCrudFrame {

    private final ServiceFinalProject serviceProject;
    private final ServiceStudent serviceStudent;
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public FinalProjectFrame(ServiceFinalProject serviceProject, ServiceStudent serviceStudent) {
        super("Gestión de Trabajos Fin de Carrera (TFC)", 700, 400);
        this.serviceProject = serviceProject;
        this.serviceStudent = serviceStudent;
        loadData();
    }

    @Override
    protected void initColumns() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Tema");
        tableModel.addColumn("Fecha Inicio");
        tableModel.addColumn("Alumno (Matrícula)");
        tableModel.addColumn("Alumno (Nombre)");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (FinalProject fp : serviceProject.findAll()) {
            String studentInfo = "-";
            String studentName = "-";
            if (fp.getStudent() != null) {
                studentInfo = String.valueOf(fp.getStudent().getStudentId());
                studentName = fp.getStudent().getName();
            }
            tableModel.addRow(new Object[]{
                    fp.getId(),
                    fp.getTopic(),
                    fp.getInitDate() != null ? sdf.format(fp.getInitDate()) : "-",
                    studentInfo,
                    studentName
            });
            rowIds.add(fp.getId());
        }
    }

    @Override
    protected void showAddDialog() { showProjectDialog(null); }

    @Override
    protected void showEditDialog(int row) {
        serviceProject.findById(rowIds.get(row)).ifPresent(this::showProjectDialog);
    }

    @Override
    protected void deleteSelected(int row) {
        serviceProject.deleteById(rowIds.get(row));
    }

    private void showProjectDialog(FinalProject existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar TFC" : "Nuevo TFC", true);
        dialog.setSize(420, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtTopic = new JTextField(isEdit ? existing.getTopic() : "", 20);
        JTextField txtDate  = new JTextField(
                isEdit && existing.getInitDate() != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(existing.getInitDate())
                        : "", 20);

        // Combo alumno (1:1 — solo alumnos sin TFC asignado o el alumno actual)
        JComboBox<String> cbStudent = new JComboBox<>();
        cbStudent.addItem("-- Sin alumno asignado --");
        List<Student> students = new ArrayList<>();
        for (Student s : serviceStudent.findAll()) {
            // Mostrar solo alumnos sin TFC, o el alumno ya asignado a este TFC
            boolean isCurrent = isEdit && existing.getStudent() != null
                    && existing.getStudent().getStudentId().equals(s.getStudentId());
            boolean hasTFC = s.getFinalProject() != null;
            if (!hasTFC || isCurrent) {
                students.add(s);
                cbStudent.addItem(s.getStudentId() + " - " + s.getName());
            }
        }
        if (isEdit && existing.getStudent() != null) {
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getStudentId().equals(existing.getStudent().getStudentId())) {
                    cbStudent.setSelectedIndex(i + 1);
                    break;
                }
            }
        }

        addRow(panel, gbc, 0, "Tema *:", txtTopic);
        addRow(panel, gbc, 1, "Fecha inicio (dd/MM/yyyy):", txtDate);
        addRow(panel, gbc, 2, "Alumno:", cbStudent);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar");
        btnSave.setBackground(new Color(46, 139, 87));
        btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String topic = txtTopic.getText().trim();
            if (topic.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El tema es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            FinalProject fp = isEdit ? existing : new FinalProject();
            fp.setTopic(topic);
            String dateStr = txtDate.getText().trim();
            if (!dateStr.isEmpty()) {
                try {
                    fp.setInitDate(new SimpleDateFormat("dd/MM/yyyy").parse(dateStr));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Formato de fecha incorrecto. Use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            int idx = cbStudent.getSelectedIndex();
            fp.setStudent(idx > 0 ? students.get(idx - 1) : null);
            serviceProject.save(fp);
            loadData();
            dialog.dispose();
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        p.add(field, gbc);
    }
}
