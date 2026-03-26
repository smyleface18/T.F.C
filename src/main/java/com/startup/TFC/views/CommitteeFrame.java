package com.startup.TFC.views;

import com.startup.TFC.entities.Committee;
import com.startup.TFC.entities.Professor;
import com.startup.TFC.services.ServiceCommittee;
import com.startup.TFC.services.ServiceProfessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommitteeFrame extends BaseCrudFrame {

    private final ServiceCommittee serviceCommittee;
    private final ServiceProfessor serviceProfessor;
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public CommitteeFrame(ServiceCommittee serviceCommittee, ServiceProfessor serviceProfessor) {
        super("Gestión de Tribunales", 780, 460);
        this.serviceCommittee = serviceCommittee;
        this.serviceProfessor = serviceProfessor;
    }

    @PostConstruct
    public void init() { loadData(); }

    @Override
    protected void initColumns() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Lugar de Examen");
        tableModel.addColumn("Nº Componentes");
        tableModel.addColumn("Profesores asignados");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        for (Committee c : serviceCommittee.findAll()) {
            String profs = c.getProfessors() != null && !c.getProfessors().isEmpty()
                    ? c.getProfessors().stream()
                        .map(p -> p.getDni() + " " + p.getName())
                        .collect(Collectors.joining("; "))
                    : "—";
            tableModel.addRow(new Object[]{c.getId(), c.getExamLocation(), c.getMemberCount(), profs});
            rowIds.add(c.getId());
        }
        updateCount();
    }

    @Override protected void showAddDialog()         { showCommitteeDialog(null); }
    @Override protected void showEditDialog(int row) {
        serviceCommittee.findById(rowIds.get(row)).ifPresent(this::showCommitteeDialog);
    }
    @Override protected void deleteSelected(int row) { serviceCommittee.deleteById(rowIds.get(row)); }

    private void showCommitteeDialog(Committee existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Tribunal" : "Nuevo Tribunal", true);
        dialog.setSize(640, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        // ── Formulario superior ────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtLocation = new JTextField(isEdit ? existing.getExamLocation() : "", 24);
        JTextField txtCount    = new JTextField(
                isEdit && existing.getMemberCount() != null ? String.valueOf(existing.getMemberCount()) : "", 6);
        addFormRow(form, gbc, 0, "Lugar de examen *:", txtLocation);
        addFormRow(form, gbc, 1, "Nº componentes:",    txtCount);

        // ── Panel de profesores con búsqueda integrada ─────────────────────
        List<Professor> allProfessors = new ArrayList<>();
        serviceProfessor.findAll().forEach(allProfessors::add);

        // Tabla de profesores YA asignados
        DefaultTableModel assignedModel = new DefaultTableModel(
                new String[]{"DNI", "Nombre", "Área"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        List<Professor> assignedList = new ArrayList<>();
        if (isEdit && existing.getProfessors() != null) {
            existing.getProfessors().forEach(p -> {
                assignedList.add(p);
                assignedModel.addRow(new Object[]{p.getDni(), p.getName(), p.getArea()});
            });
        }
        JTable assignedTable = new JTable(assignedModel);
        assignedTable.setRowHeight(24);
        assignedTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        assignedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JLabel lblAssigned = new JLabel("Profesores asignados al tribunal (" + assignedList.size() + "):");
        lblAssigned.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton btnAddProf = new JButton("+ Agregar profesor...");
        btnAddProf.setBackground(new Color(30, 100, 180));
        btnAddProf.setForeground(Color.WHITE);
        btnAddProf.setFocusPainted(false);
        btnAddProf.addActionListener(e -> {
            // Mostrar solo los que NO están asignados aún
            List<Professor> available = allProfessors.stream()
                    .filter(p -> assignedList.stream().noneMatch(a -> a.getDni().equals(p.getDni())))
                    .collect(Collectors.toList());
            if (available.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Todos los profesores ya están asignados.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            SearchablePickerDialog<Professor> picker = new SearchablePickerDialog<>(
                    dialog, "Agregar Profesor al Tribunal",
                    available,
                    new String[]{"DNI", "Nombre", "Área", "Domicilio"},
                    p -> new Object[]{p.getDni(), p.getName(), p.getArea(), p.getAddress()}
            );
            Professor chosen = picker.showAndGet();
            if (chosen != null) {
                assignedList.add(chosen);
                assignedModel.addRow(new Object[]{chosen.getDni(), chosen.getName(), chosen.getArea()});
                lblAssigned.setText("Profesores asignados al tribunal (" + assignedList.size() + "):");
            }
        });

        JButton btnRemoveProf = new JButton("– Quitar seleccionado");
        btnRemoveProf.setBackground(new Color(180, 50, 50));
        btnRemoveProf.setForeground(Color.WHITE);
        btnRemoveProf.setFocusPainted(false);
        btnRemoveProf.addActionListener(e -> {
            int sel = assignedTable.getSelectedRow();
            if (sel < 0) { JOptionPane.showMessageDialog(dialog, "Seleccione un profesor de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
            assignedList.remove(sel);
            assignedModel.removeRow(sel);
            lblAssigned.setText("Profesores asignados al tribunal (" + assignedList.size() + "):");
        });

        JPanel profBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        profBtns.add(btnAddProf); profBtns.add(btnRemoveProf);

        JPanel profPanel = new JPanel(new BorderLayout(4, 4));
        profPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));
        profPanel.add(lblAssigned,              BorderLayout.NORTH);
        profPanel.add(new JScrollPane(assignedTable), BorderLayout.CENTER);
        profPanel.add(profBtns,                 BorderLayout.SOUTH);

        // ── Botones finales ────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnSave   = createSaveButton();
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnCancel); btnPanel.add(btnSave);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String location = txtLocation.getText().trim();
            if (location.isEmpty()) { showError("El lugar de examen es obligatorio."); return; }
            Committee c = isEdit ? existing : new Committee();
            c.setExamLocation(location);
            try { c.setMemberCount(Integer.parseInt(txtCount.getText().trim())); }
            catch (NumberFormatException ex) { c.setMemberCount(null); }
            c.setProfessors(new ArrayList<>(assignedList));
            serviceCommittee.save(c);
            loadData();
            dialog.dispose();
        });

        dialog.add(form,     BorderLayout.NORTH);
        dialog.add(profPanel, BorderLayout.CENTER);
        dialog.add(btnPanel,  BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
