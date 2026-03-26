package com.startup.TFC.views;

import com.startup.TFC.entities.Professor;
import com.startup.TFC.entities.ResearchGroup;
import com.startup.TFC.entities.Student;
import com.startup.TFC.services.ServiceProfessor;
import com.startup.TFC.services.ServiceResearchGroup;
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
public class StudentFrame extends BaseCrudFrame {

    private final ServiceStudent serviceStudent;
    private final ServiceProfessor serviceProfessor;
    private final ServiceResearchGroup serviceGroup;

    // Guarda los IDs para poder recuperar el objeto al editar/eliminar
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public StudentFrame(ServiceStudent serviceStudent,
                        ServiceProfessor serviceProfessor,
                        ServiceResearchGroup serviceGroup) {
        super("Gestión de Alumnos", 750, 450);
        this.serviceStudent   = serviceStudent;
        this.serviceProfessor = serviceProfessor;
        this.serviceGroup     = serviceGroup;
        loadData(); // recarga con servicios listos
    }

    @Override
    protected void initColumns() {
        tableModel.addColumn("Matrícula");
        tableModel.addColumn("DNI");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Director (DNI)");
        tableModel.addColumn("Grupo de Investigación");
        tableModel.addColumn("Fecha Ingreso Grupo");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Student s : serviceStudent.findAll()) {
            String directorDni = s.getDirector() != null ? s.getDirector().getDni() + " - " + s.getDirector().getName() : "-";
            String groupName   = s.getResearchGroup() != null ? s.getResearchGroup().getName() : "-";
            String groupDate   = s.getGroupJoinDate() != null ? sdf.format(s.getGroupJoinDate()) : "-";

            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getDni(),
                    s.getName(),
                    directorDni,
                    groupName,
                    groupDate
            });
            rowIds.add(Long.valueOf(s.getStudentId()));
        }
    }

    @Override
    protected void showAddDialog() {
        showStudentDialog(null);
    }

    @Override
    protected void showEditDialog(int row) {
        Long id = rowIds.get(row);
        Optional<Student> opt = serviceStudent.findById(id);
        opt.ifPresent(s -> showStudentDialog(s));
    }

    @Override
    protected void deleteSelected(int row) {
        Long id = rowIds.get(row);
        serviceStudent.deleteById(id);
    }

    private void showStudentDialog(Student existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Alumno" : "Agregar Alumno", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos
        JTextField txtDni  = new JTextField(existing != null ? existing.getDni() : "", 20);
        JTextField txtName = new JTextField(existing != null ? existing.getName() : "", 20);

        // ComboBox para director
        JComboBox<String> cbDirector = new JComboBox<>();
        cbDirector.addItem("-- Sin director --");
        List<Professor> professors = new ArrayList<>();
        for (Professor p : serviceProfessor.findAll()) {
            professors.add(p);
            cbDirector.addItem(p.getDni() + " - " + p.getName());
        }
        if (existing != null && existing.getDirector() != null) {
            for (int i = 0; i < professors.size(); i++) {
                if (professors.get(i).getDni().equals(existing.getDirector().getDni())) {
                    cbDirector.setSelectedIndex(i + 1);
                    break;
                }
            }
        }

        // ComboBox para grupo
        JComboBox<String> cbGroup = new JComboBox<>();
        cbGroup.addItem("-- Sin grupo --");
        List<ResearchGroup> groups = new ArrayList<>();
        for (ResearchGroup g : serviceGroup.findAll()) {
            groups.add(g);
            cbGroup.addItem(g.getId() + " - " + g.getName());
        }
        if (existing != null && existing.getResearchGroup() != null) {
            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).getId().equals(existing.getResearchGroup().getId())) {
                    cbGroup.setSelectedIndex(i + 1);
                    break;
                }
            }
        }

        JTextField txtGroupDate = new JTextField(
                existing != null && existing.getGroupJoinDate() != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(existing.getGroupJoinDate())
                        : "", 20);

        // Agregar al panel
        addRow(panel, gbc, 0, "DNI:", txtDni);
        addRow(panel, gbc, 1, "Nombre:", txtName);
        addRow(panel, gbc, 2, "Director:", cbDirector);
        addRow(panel, gbc, 3, "Grupo:", cbGroup);
        addRow(panel, gbc, 4, "Fecha ingreso (dd/MM/yyyy):", txtGroupDate);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave   = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        btnSave.setBackground(new Color(46, 139, 87));
        btnSave.setForeground(Color.WHITE);
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            String dni  = txtDni.getText().trim();
            String name = txtName.getText().trim();
            if (dni.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "DNI y Nombre son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = isEdit ? existing : new Student();
            student.setDni(dni);
            student.setName(name);

            int dirIdx = cbDirector.getSelectedIndex();
            student.setDirector(dirIdx > 0 ? professors.get(dirIdx - 1) : null);

            int grpIdx = cbGroup.getSelectedIndex();
            student.setResearchGroup(grpIdx > 0 ? groups.get(grpIdx - 1) : null);

            String dateStr = txtGroupDate.getText().trim();
            if (!dateStr.isEmpty() && grpIdx > 0) {
                try {
                    Date d = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
                    student.setGroupJoinDate(d);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Formato de fecha incorrecto. Use dd/MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                student.setGroupJoinDate(null);
            }

            serviceStudent.save(student);
            loadData();
            dialog.dispose();
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
}
