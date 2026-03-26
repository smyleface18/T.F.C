package com.startup.TFC.views;

import com.startup.TFC.entities.Professor;
import com.startup.TFC.entities.Student;
import com.startup.TFC.services.ServiceProfessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProfessorFrame extends BaseCrudFrame {

    private final ServiceProfessor serviceProfessor;
    private final List<String> rowDnis = new ArrayList<>();

    @Autowired
    public ProfessorFrame(ServiceProfessor serviceProfessor) {
        super("Gestión de Profesores", 700, 450);
        this.serviceProfessor = serviceProfessor;
    }

    @PostConstruct
    public void init() { loadData(); }

    @Override
    protected void initColumns() {
        tableModel.addColumn("DNI");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Domicilio");
        tableModel.addColumn("Área");
        tableModel.addColumn("Alumnos asignado");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowDnis.clear();
        for (Professor p : serviceProfessor.findAll()) {
            Student supervised = p.getSupervisedStudent();
            String student = (supervised != null)
                    ? supervised.getName() + " DNI:" + supervised.getDni()
                    : "-";  // Ningún alumno asignado
            tableModel.addRow(new Object[]{
                    p.getDni(), p.getName(), p.getAddress(), p.getArea(), student
            });
            rowDnis.add(p.getDni());
        }
        updateCount();
    }

    @Override protected void showAddDialog()         { showProfessorDialog(null); }
    @Override protected void showEditDialog(int row) {
        serviceProfessor.findById(rowDnis.get(row)).ifPresent(this::showProfessorDialog);
    }
    @Override protected void deleteSelected(int row) { serviceProfessor.deleteById(rowDnis.get(row)); }

    private void showProfessorDialog(Professor existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Profesor" : "Agregar Profesor", true);
        dialog.setSize(420, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtDni     = new JTextField(isEdit ? existing.getDni()     : "", 20);
        JTextField txtName    = new JTextField(isEdit ? existing.getName()     : "", 20);
        JTextField txtAddress = new JTextField(isEdit ? existing.getAddress()  : "", 20);
        JTextField txtArea    = new JTextField(isEdit ? existing.getArea()     : "", 20);

        if (isEdit) {
            txtDni.setEditable(false);
            txtDni.setBackground(new Color(240, 240, 240));
            txtDni.setToolTipText("El DNI es la clave primaria y no puede modificarse");
        }

        addFormRow(form, gbc, 0, "DNI *:",       txtDni);
        addFormRow(form, gbc, 1, "Nombre *:",     txtName);
        addFormRow(form, gbc, 2, "Domicilio:",    txtAddress);
        addFormRow(form, gbc, 3, "Área *:",       txtArea);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnSave   = createSaveButton();
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnCancel); btnPanel.add(btnSave);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String dni  = txtDni.getText().trim();
            String name = txtName.getText().trim();
            String area = txtArea.getText().trim();
            if (dni.isEmpty() || name.isEmpty() || area.isEmpty()) {
                showError("DNI, Nombre y Área son obligatorios."); return;
            }
            Professor p = isEdit ? existing : new Professor();
            if (!isEdit) p.setDni(dni);
            p.setName(name);
            p.setAddress(txtAddress.getText().trim());
            p.setArea(area);
            serviceProfessor.save(p);
            loadData();
            dialog.dispose();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

        JPanel helpPanel = new JPanel(new BorderLayout());
        helpPanel.setBorder(BorderFactory.createTitledBorder("Alumnos que ayuda"));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> studentList = new JList<>(listModel);
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        helpPanel.add(new JScrollPane(studentList), BorderLayout.CENTER);

// Cargar los nombres de todos los estudiantes
        List<Student> allStudents = (List<Student>) serviceProfessor.getAllStudents(); // método nuevo en el service
        for (Student s : allStudents) {
            listModel.addElement(s.getName() + " | DNI: " + s.getDni());
        }

// Seleccionar los que ya están asignados (en edición)
        if (isEdit && existing.getHelpedStudents() != null) {
            int[] selectedIndices = existing.getHelpedStudents().stream()
                    .mapToInt(s -> allStudents.indexOf(s))
                    .toArray();
            studentList.setSelectedIndices(selectedIndices);
        }

        form.add(helpPanel, gbc); // agregar al formulario
    }
}
