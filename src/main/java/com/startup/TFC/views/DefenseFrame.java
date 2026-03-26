package com.startup.TFC.views;

import com.startup.TFC.entities.*;
import com.startup.TFC.services.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        super("Gestión de Defensas de TFC", 860, 460);
        this.serviceDefense   = serviceDefense;
        this.serviceCommittee = serviceCommittee;
        this.serviceStudent   = serviceStudent;
        this.serviceProject   = serviceProject;
    }

    @PostConstruct
    public void init() { loadData(); }

    @Override
    protected void initColumns() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Fecha");
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
            tableModel.addRow(new Object[]{
                    d.getId(),
                    d.getDefenseDate() != null ? sdf.format(d.getDefenseDate()) : "—",
                    d.getCommittee()   != null ? d.getCommittee().getId() + " – " + d.getCommittee().getExamLocation() : "—",
                    d.getStudent()     != null ? d.getStudent().getStudentId() + " – " + d.getStudent().getName() : "—",
                    d.getFinalProject() != null ? d.getFinalProject().getTopic() : "—"
            });
            rowIds.add(d.getId());
        }
        updateCount();
    }

    @Override protected void showAddDialog()         { showDefenseDialog(null); }
    @Override protected void showEditDialog(int row) {
        serviceDefense.findById(rowIds.get(row)).ifPresent(this::showDefenseDialog);
    }
    @Override protected void deleteSelected(int row) { serviceDefense.deleteById(rowIds.get(row)); }

    private void showDefenseDialog(Defense existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Defensa" : "Nueva Defensa", true);
        dialog.setSize(520, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtDate = new JTextField(
                isEdit && existing.getDefenseDate() != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(existing.getDefenseDate()) : "", 12);

        // ── Selectores buscables ───────────────────────────────────────────
        List<Committee>    committees = new ArrayList<>();
        List<Student>      students   = new ArrayList<>();
        List<FinalProject> projects   = new ArrayList<>();
        serviceCommittee.findAll().forEach(committees::add);
        serviceStudent.findAll().forEach(students::add);
        serviceProject.findAll().forEach(projects::add);

        Committee[]    committeeHolder = { isEdit ? existing.getCommittee()    : null };
        Student[]      studentHolder   = { isEdit ? existing.getStudent()      : null };
        FinalProject[] projectHolder   = { isEdit ? existing.getFinalProject() : null };

        JTextField txtCommitteeDisplay = makeDisplayField(committeeHolder[0] != null
                ? committeeHolder[0].getId() + " – " + committeeHolder[0].getExamLocation() : "");
        JTextField txtStudentDisplay   = makeDisplayField(studentHolder[0] != null
                ? studentHolder[0].getStudentId() + " – " + studentHolder[0].getName() : "");
        JTextField txtProjectDisplay   = makeDisplayField(projectHolder[0] != null
                ? projectHolder[0].getTopic() : "");

        JButton btnPickCommittee = makePickBtn("Buscar...", e -> {
            SearchablePickerDialog<Committee> p = new SearchablePickerDialog<>(
                    dialog, "Seleccionar Tribunal", committees,
                    new String[]{"ID", "Lugar", "Nº Componentes"},
                    c -> new Object[]{c.getId(), c.getExamLocation(), c.getMemberCount()});
            Committee chosen = p.showAndGet();
            if (chosen != null) { committeeHolder[0] = chosen; txtCommitteeDisplay.setText(chosen.getId() + " – " + chosen.getExamLocation()); }
        });
        JButton btnPickStudent = makePickBtn("Buscar...", e -> {
            SearchablePickerDialog<Student> p = new SearchablePickerDialog<>(
                    dialog, "Seleccionar Alumno", students,
                    new String[]{"Matrícula", "DNI", "Nombre"},
                    s -> new Object[]{s.getStudentId(), s.getDni(), s.getName()});
            Student chosen = p.showAndGet();
            if (chosen != null) { studentHolder[0] = chosen; txtStudentDisplay.setText(chosen.getStudentId() + " – " + chosen.getName()); }
        });
        JButton btnPickProject = makePickBtn("Buscar...", e -> {
            SearchablePickerDialog<FinalProject> p = new SearchablePickerDialog<>(
                    dialog, "Seleccionar TFC", projects,
                    new String[]{"ID", "Tema"},
                    fp -> new Object[]{fp.getId(), fp.getTopic()});
            FinalProject chosen = p.showAndGet();
            if (chosen != null) { projectHolder[0] = chosen; txtProjectDisplay.setText(chosen.getTopic()); }
        });

        addFormRow(form, gbc, 0, "Fecha (dd/MM/yyyy):", txtDate);
        addFormRow(form, gbc, 1, "Tribunal:", makeSelectorPanel(txtCommitteeDisplay, btnPickCommittee));
        addFormRow(form, gbc, 2, "Alumno:",   makeSelectorPanel(txtStudentDisplay,   btnPickStudent));
        addFormRow(form, gbc, 3, "TFC:",      makeSelectorPanel(txtProjectDisplay,   btnPickProject));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnSave   = createSaveButton();
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnCancel); btnPanel.add(btnSave);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            Defense def = isEdit ? existing : new Defense();
            String ds = txtDate.getText().trim();
            if (!ds.isEmpty()) {
                try { def.setDefenseDate(new SimpleDateFormat("dd/MM/yyyy").parse(ds)); }
                catch (Exception ex) { showError("Fecha inválida. Use dd/MM/yyyy."); return; }
            }
            def.setCommittee(committeeHolder[0]);
            def.setStudent(studentHolder[0]);
            def.setFinalProject(projectHolder[0]);
            serviceDefense.save(def);
            loadData();
            dialog.dispose();
        });

        dialog.add(form,     BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JTextField makeDisplayField(String text) {
        JTextField f = new JTextField(text, 22);
        f.setEditable(false);
        f.setBackground(new Color(248, 248, 248));
        return f;
    }

    private JButton makePickBtn(String label, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.addActionListener(listener);
        return btn;
    }

    private JPanel makeSelectorPanel(JTextField display, JButton pickBtn) {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.add(display, BorderLayout.CENTER);
        p.add(pickBtn, BorderLayout.EAST);
        return p;
    }
}
