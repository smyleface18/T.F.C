package com.startup.TFC.views;

import com.startup.TFC.entities.ResearchGroup;
import com.startup.TFC.services.ServiceResearchGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ResearchGroupFrame extends BaseCrudFrame {

    private final ServiceResearchGroup serviceGroup;
    private final List<Long> rowIds = new ArrayList<>();

    @Autowired
    public ResearchGroupFrame(ServiceResearchGroup serviceGroup) {
        super("Gestión de Grupos de Investigación", 580, 380);
        this.serviceGroup = serviceGroup;
        loadData();
    }

    @Override
    protected void initColumns() {
        tableModel.addColumn("ID");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Nº Componentes");
        tableModel.addColumn("Alumnos inscritos");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowIds.clear();
        for (ResearchGroup g : serviceGroup.findAll()) {
            int studentCount = g.getStudents() != null ? g.getStudents().size() : 0;
            tableModel.addRow(new Object[]{
                    g.getId(),
                    g.getName(),
                    studentCount
            });
            rowIds.add(g.getId());
        }
    }

    @Override
    protected void showAddDialog() { showGroupDialog(null); }

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
        dialog.setSize(370, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtName  = new JTextField(isEdit ? existing.getName() : "", 20);


        addRow(panel, gbc, 0, "Nombre del grupo *:", txtName);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar");
        btnSave.setBackground(new Color(46, 139, 87));
        btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ResearchGroup g = isEdit ? existing : new ResearchGroup();
            g.setName(name);
            serviceGroup.save(g);
            loadData();
            dialog.dispose();
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.45;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.55;
        p.add(field, gbc);
    }
}
