package com.startup.TFC.views;

import com.startup.TFC.entities.Professor;
import com.startup.TFC.entities.ResearchGroup;
import com.startup.TFC.entities.Student;
import com.startup.TFC.services.ServiceProfessor;
import com.startup.TFC.services.ServiceResearchGroup;
import com.startup.TFC.services.ServiceStudent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class StudentFrame extends BaseCrudFrame {

    private final ServiceStudent serviceStudent;
    private final ServiceProfessor serviceProfessor;
    private final ServiceResearchGroup serviceGroup;
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public StudentFrame(ServiceStudent serviceStudent,
                        ServiceProfessor serviceProfessor,
                        ServiceResearchGroup serviceGroup) {
        super("Gestión de Alumnos", 820, 480);
        this.serviceStudent   = serviceStudent;
        this.serviceProfessor = serviceProfessor;
        this.serviceGroup     = serviceGroup;
    }

    @PostConstruct
    public void init() { loadData(); }

    @Override
    protected void initColumns() {
        tableModel.addColumn("Matrícula");
        tableModel.addColumn("DNI");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Director");
        tableModel.addColumn("Grupo de Investigación");
        tableModel.addColumn("Fecha Ingreso Grupo");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Student s : serviceStudent.findAll()) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getDni(),
                    s.getName(),
                    s.getDirector() != null ? s.getDirector().getDni() + " – " + s.getDirector().getName() : "—",
                    s.getResearchGroup() != null ? s.getResearchGroup().getName() : "—",
                    s.getGroupJoinDate() != null ? sdf.format(s.getGroupJoinDate()) : "—"
            });
            rowIds.add(s.getStudentId());
        }
        updateCount();
    }

    @Override protected void showAddDialog()          { showStudentDialog(null); }
    @Override protected void showEditDialog(int row)  {
        serviceStudent.findById(rowIds.get(row)).ifPresent(this::showStudentDialog);
    }
    @Override protected void deleteSelected(int row)  { serviceStudent.deleteById(rowIds.get(row)); }

    private void showStudentDialog(Student existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Alumno" : "Agregar Alumno", true);
        dialog.setSize(500, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtDni  = new JTextField(existing != null ? existing.getDni()  : "", 22);
        JTextField txtName = new JTextField(existing != null ? existing.getName() : "", 22);

        // ── Selector de director (buscable) ────────────────────────────────
        List<Professor> professors = new ArrayList<>();
        serviceProfessor.findAll().forEach(professors::add);

        Professor[] directorHolder = { existing != null ? existing.getDirector() : null };

        JTextField txtDirectorDisplay = new JTextField(
                directorHolder[0] != null
                        ? directorHolder[0].getDni() + " – " + directorHolder[0].getName()
                        : "", 22);
        txtDirectorDisplay.setEditable(false);
        txtDirectorDisplay.setBackground(new Color(248, 248, 248));

        JButton btnPickDirector = new JButton("Buscar...");
        btnPickDirector.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnPickDirector.addActionListener(e -> {
            SearchablePickerDialog<Professor> picker = new SearchablePickerDialog<>(
                    dialog, "Seleccionar Director",
                    professors,
                    new String[]{"DNI", "Nombre", "Área", "Domicilio"},
                    p -> new Object[]{ ((Professor) p).getDni(), ((Professor) p).getName(), ((Professor) p).getArea(), ((Professor) p).getAddress()}
            );
            Professor chosen = picker.showAndGet();
            if (chosen != null) {
                directorHolder[0] = chosen;
                txtDirectorDisplay.setText(chosen.getDni() + " – " + chosen.getName());
            }
        });
        JButton btnClearDirector = new JButton("✕");
        btnClearDirector.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnClearDirector.setMargin(new Insets(2, 4, 2, 4));
        btnClearDirector.setToolTipText("Quitar director");
        btnClearDirector.addActionListener(e -> { directorHolder[0] = null; txtDirectorDisplay.setText(""); });

        JPanel directorPanel = new JPanel(new BorderLayout(4, 0));
        directorPanel.add(txtDirectorDisplay, BorderLayout.CENTER);
        JPanel dirBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        dirBtns.add(btnPickDirector); dirBtns.add(btnClearDirector);
        directorPanel.add(dirBtns, BorderLayout.EAST);

        // ── Selector de grupo (buscable) ───────────────────────────────────
        List<ResearchGroup> groups = new ArrayList<>();
        serviceGroup.findAll().forEach(groups::add);

        ResearchGroup[] groupHolder = { existing != null ? existing.getResearchGroup() : null };

        JTextField txtGroupDisplay = new JTextField(
                groupHolder[0] != null ? groupHolder[0].getName() : "", 22);
        txtGroupDisplay.setEditable(false);
        txtGroupDisplay.setBackground(new Color(248, 248, 248));

        JButton btnPickGroup = new JButton("Buscar...");
        btnPickGroup.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnPickGroup.addActionListener(e -> {
            SearchablePickerDialog<ResearchGroup> picker = new SearchablePickerDialog<>(
                    dialog, "Seleccionar Grupo de Investigación",
                    groups,
                    new String[]{"ID", "Nombre", "Nº Componentes"},
                    g -> new Object[]{
                            ((ResearchGroup) g).getId(),
                            ((ResearchGroup) g).getName()}
            );
            ResearchGroup chosen = picker.showAndGet();
            if (chosen != null) {
                groupHolder[0] = chosen;
                txtGroupDisplay.setText(chosen.getName());
            }
        });
        JButton btnClearGroup = new JButton("✕");
        btnClearGroup.setFont(new Font("SansSerif", Font.PLAIN, 10));
        btnClearGroup.setMargin(new Insets(2, 4, 2, 4));
        btnClearGroup.setToolTipText("Quitar grupo");
        btnClearGroup.addActionListener(e -> { groupHolder[0] = null; txtGroupDisplay.setText(""); });

        JPanel groupPanel = new JPanel(new BorderLayout(4, 0));
        groupPanel.add(txtGroupDisplay, BorderLayout.CENTER);
        JPanel grpBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        grpBtns.add(btnPickGroup); grpBtns.add(btnClearGroup);
        groupPanel.add(grpBtns, BorderLayout.EAST);

        JTextField txtGroupDate = new JTextField(
                existing != null && existing.getGroupJoinDate() != null
                        ? new SimpleDateFormat("dd/MM/yyyy").format(existing.getGroupJoinDate()) : "", 12);

        addFormRow(form, gbc, 0, "DNI *:",                    txtDni);
        addFormRow(form, gbc, 1, "Nombre *:",                 txtName);
        addFormRow(form, gbc, 2, "Director:",                 directorPanel);
        addFormRow(form, gbc, 3, "Grupo de investigación:",   groupPanel);
        addFormRow(form, gbc, 4, "Fecha ingreso (dd/MM/yyyy):", txtGroupDate);

        // ── Botones ────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnSave   = createSaveButton();
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnCancel); btnPanel.add(btnSave);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String dni  = txtDni.getText().trim();
            String name = txtName.getText().trim();
            if (dni.isEmpty() || name.isEmpty()) {
                showError("DNI y Nombre son obligatorios."); return;
            }
            Student s = isEdit ? existing : new Student();
            s.setDni(dni);
            s.setName(name);
            s.setDirector(directorHolder[0]);
            s.setResearchGroup(groupHolder[0]);

            String ds = txtGroupDate.getText().trim();
            if (!ds.isEmpty() && groupHolder[0] != null) {
                try { s.setGroupJoinDate(new SimpleDateFormat("dd/MM/yyyy").parse(ds)); }
                catch (Exception ex) { showError("Formato de fecha incorrecto. Use dd/MM/yyyy."); return; }
            } else {
                s.setGroupJoinDate(null);
            }
            serviceStudent.save(s);
            loadData();
            dialog.dispose();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
