package com.startup.TFC.views;

import com.startup.TFC.entities.Professor;
import com.startup.TFC.services.ServiceProfessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProfessorFrame extends BaseCrudFrame {

    private final ServiceProfessor serviceProfessor;
    private final List<String> rowDnis = new ArrayList<>();

    @Autowired
    public ProfessorFrame(ServiceProfessor serviceProfessor) {
        super("Gestión de Profesores", 680, 420);
        this.serviceProfessor = serviceProfessor;
        loadData();
    }

    @Override
    protected void initColumns() {
        tableModel.addColumn("DNI");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Domicilio");
        tableModel.addColumn("Área");
    }

    @Override
    protected void loadData() {
        tableModel.setRowCount(0);
        rowDnis.clear();
        for (Professor p : serviceProfessor.findAll()) {
            tableModel.addRow(new Object[]{p.getDni(), p.getName(), p.getAddress(), p.getArea()});
            rowDnis.add(p.getDni());
        }
    }

    @Override
    protected void showAddDialog() { showProfessorDialog(null); }

    @Override
    protected void showEditDialog(int row) {
        String dni = rowDnis.get(row);
        serviceProfessor.findById(dni).ifPresent(this::showProfessorDialog);
    }

    @Override
    protected void deleteSelected(int row) {
        serviceProfessor.deleteById(rowDnis.get(row));
    }

    private void showProfessorDialog(Professor existing) {
        boolean isEdit = existing != null;
        JDialog dialog = new JDialog(this, isEdit ? "Editar Profesor" : "Agregar Profesor", true);
        dialog.setSize(380, 260);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtDni     = new JTextField(isEdit ? existing.getDni() : "", 18);
        JTextField txtName    = new JTextField(isEdit ? existing.getName() : "", 18);
        JTextField txtAddress = new JTextField(isEdit ? existing.getAddress() : "", 18);
        JTextField txtArea    = new JTextField(isEdit ? existing.getArea() : "", 18);

        if (isEdit) txtDni.setEditable(false); // DNI es PK, no se puede cambiar

        addRow(panel, gbc, 0, "DNI *:", txtDni);
        addRow(panel, gbc, 1, "Nombre *:", txtName);
        addRow(panel, gbc, 2, "Domicilio:", txtAddress);
        addRow(panel, gbc, 3, "Área *:", txtArea);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave   = createSaveButton();
        JButton btnCancel = new JButton("Cancelar");
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String dni  = txtDni.getText().trim();
            String name = txtName.getText().trim();
            String area = txtArea.getText().trim();
            if (dni.isEmpty() || name.isEmpty() || area.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "DNI, Nombre y Área son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
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

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JButton createSaveButton() {
        JButton btn = new JButton("Guardar");
        btn.setBackground(new Color(46, 139, 87));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        p.add(field, gbc);
    }
}
