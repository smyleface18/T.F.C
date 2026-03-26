package com.startup.TFC.views;

import com.startup.TFC.entities.FinalProject;
import com.startup.TFC.entities.Student;
import com.startup.TFC.services.ServiceFinalProject;
import com.startup.TFC.services.ServiceStudent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class FinalProjectFrame extends BaseCrudFrame {

    private final ServiceFinalProject serviceProject;
    private final ServiceStudent serviceStudent;
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public FinalProjectFrame(ServiceFinalProject serviceProject, ServiceStudent serviceStudent) {
        super("Gestión de Trabajos Fin de Carrera", 780, 440);
        this.serviceProject = serviceProject;
        this.serviceStudent = serviceStudent;
    }

    @PostConstruct
    public void init() { loadData(); }

    @Override
    protected void initColumns() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Tema");
        tableModel.addColumn("Fecha Inicio");
        tableModel.addColumn("Matrícula Alumno");
        tableModel.addColumn("Nombre Alumno");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (FinalProject fp : serviceProject.findAll()) {
            tableModel.addRow(new Object[]{
                    fp.getId(),
                    fp.getTopic(),
                    fp.getInitDate() != null ? sdf.format(fp.getInitDate()) : "—",
                    fp.getStudent() != null ? fp.getStudent().getStudentId() : "—",
                    fp.getStudent() != null ? fp.getStudent().getName()      : "—"
            });
            rowIds.add(fp.getId());
        }
        updateCount();
    }

    @Override protected void showAddDialog()         { showProjectDialog(null); }
    @Override protected void showEditDialog(int row) {
        serviceProject.findById(rowIds.get(row)).ifPresent(this::showProjectDialog);
    }
    @Override protected void deleteSelected(int row) { serviceProject.deleteById(rowIds.get(row)); }

    private void showProjectDialog(FinalProject existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar TFC" : "Nuevo TFC", true);
        dialog.setSize(500, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtTopic = new JTextField(isEdit ? existing.getTopic() : "", 24);
        JTextField txtDate  = new JTextField(
                isEdit && existing.getInitDate() != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(existing.getInitDate()) : "", 12);

        // ── Selector de alumno (buscable) ─────────────────────────────────
        List<Student> students = new ArrayList<>();
        for (Student s : serviceStudent.findAll()) {
            boolean isCurrent = isEdit && existing.getStudent() != null
                    && existing.getStudent().getStudentId().equals(s.getStudentId());
            boolean hasTFC = s.getFinalProject() != null;
            if (!hasTFC || isCurrent) students.add(s);
        }

        Student[] studentHolder = { isEdit ? existing.getStudent() : null };
        JTextField txtStudentDisplay = new JTextField(
                studentHolder[0] != null
                        ? studentHolder[0].getStudentId() + " – " + studentHolder[0].getName() : "", 24);
        txtStudentDisplay.setEditable(false);
        txtStudentDisplay.setBackground(new Color(248, 248, 248));

        JButton btnPickStudent = new JButton("Buscar...");
        btnPickStudent.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnPickStudent.addActionListener(e -> {
            SearchablePickerDialog<Student> picker = new SearchablePickerDialog<>(
                    dialog, "Seleccionar Alumno (sin TFC asignado)",
                    students,
                    new String[]{"Matrícula", "DNI", "Nombre"},
                    s -> new Object[]{s.getStudentId(), s.getDni(), s.getName()}
            );
            Student chosen = picker.showAndGet();
            if (chosen != null) {
                studentHolder[0] = chosen;
                txtStudentDisplay.setText(chosen.getStudentId() + " – " + chosen.getName());
            }
        });
        JButton btnClearStudent = new JButton("✕");
        btnClearStudent.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnClearStudent.setMargin(new Insets(2, 4, 2, 4));
        btnClearStudent.addActionListener(e -> { studentHolder[0] = null; txtStudentDisplay.setText(""); });

        JPanel studentPanel = new JPanel(new BorderLayout(4, 0));
        studentPanel.add(txtStudentDisplay, BorderLayout.CENTER);
        JPanel sBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        sBtns.add(btnPickStudent); sBtns.add(btnClearStudent);
        studentPanel.add(sBtns, BorderLayout.EAST);

        addFormRow(form, gbc, 0, "Tema *:",                   txtTopic);
        addFormRow(form, gbc, 1, "Fecha inicio (dd/MM/yyyy):", txtDate);
        addFormRow(form, gbc, 2, "Alumno:",                   studentPanel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnSave   = createSaveButton();
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnCancel); btnPanel.add(btnSave);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String topic = txtTopic.getText().trim();
            if (topic.isEmpty()) { showError("El tema es obligatorio."); return; }
            FinalProject fp = isEdit ? existing : new FinalProject();
            fp.setTopic(topic);
            String ds = txtDate.getText().trim();
            if (!ds.isEmpty()) {
                try { fp.setInitDate(new SimpleDateFormat("dd/MM/yyyy").parse(ds)); }
                catch (Exception ex) { showError("Formato de fecha incorrecto. Use dd/MM/yyyy."); return; }
            }
            fp.setStudent(studentHolder[0]);
            serviceProject.save(fp);
            loadData();
            dialog.dispose();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
