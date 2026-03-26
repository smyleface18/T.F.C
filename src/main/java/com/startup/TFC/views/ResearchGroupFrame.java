package com.startup.TFC.views;

import com.startup.TFC.entities.ResearchGroup;
import com.startup.TFC.services.ServiceResearchGroup;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResearchGroupFrame extends BaseCrudFrame {

    private final ServiceResearchGroup serviceGroup;
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public ResearchGroupFrame(ServiceResearchGroup serviceGroup) {
        super("Gestión de Grupos de Investigación", 620, 420);
        this.serviceGroup = serviceGroup;
    }

    @PostConstruct
    public void init() {
        loadData();
    }

    @Override
    protected void initColumns() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Alumnos inscritos"); // ahora solo usamos alumnos
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        for (ResearchGroup g : serviceGroup.findAll()) {
            List<String> studentNames = g.getStudents().stream()
                    .map(student -> student.getName() + ", DNI: " + student.getDni())
                    .toList();
            tableModel.addRow(new Object[]{
                    g.getId(),
                    g.getName(),
                    studentNames.toString()
            });
            rowIds.add(g.getId());
        }
        updateCount();
    }

    @Override
    protected void showAddDialog() {
        showGroupDialog(null);
    }

    @Override
    protected void showEditDialog(int row) {
        serviceGroup.findById(rowIds.get(row)).ifPresent(this::showGroupDialog);
    }

    @Override
    protected void deleteSelected(int row) {
        serviceGroup.deleteById(rowIds.get(row));
    }

    private void showGroupDialog(ResearchGroup existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Grupo" : "Nuevo Grupo de Investigación", true);
        dialog.setSize(400, 210);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtName = new JTextField(isEdit ? existing.getName() : "", 22);
        JTextField txtCount = new JTextField(
                isEdit && existing.getStudents() != null ? String.valueOf(existing.getStudents().size()) : "",
                8
        );

        addFormRow(form, gbc, 0, "Nombre del grupo *:", txtName);
        addFormRow(form, gbc, 1, "Alumnos inscritos:", txtCount);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnSave = createSaveButton();
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                showError("El nombre es obligatorio.");
                return;
            }
            ResearchGroup g = isEdit ? existing : new ResearchGroup();
            g.setName(name);
            // No seteamos count manualmente, ya que depende de la lista de alumnos
            serviceGroup.save(g);
            loadData();
            dialog.dispose();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}