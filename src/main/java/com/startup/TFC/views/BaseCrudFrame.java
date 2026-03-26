package com.startup.TFC.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Frame base que provee estructura CRUD común:
 * tabla en el centro, botones Agregar/Editar/Eliminar/Recargar en la parte inferior.
 * Cada subclase implementa los métodos abstractos para adaptar columnas y datos.
 */
public abstract class BaseCrudFrame extends JFrame {

    protected JTable table;
    protected DefaultTableModel tableModel;

    protected JButton btnAdd;
    protected JButton btnEdit;
    protected JButton btnDelete;
    protected JButton btnRefresh;

    public BaseCrudFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initBaseComponents();
        initColumns();

    }

    private void initBaseComponents() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla solo lectura; edición por diálogo
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);

        // Panel de botones
        btnAdd     = new JButton("Agregar");
        btnEdit    = new JButton("Editar");
        btnDelete  = new JButton("Eliminar");
        btnRefresh = new JButton("Recargar");

        styleButton(btnAdd,    new Color(46, 139, 87));
        styleButton(btnEdit,   new Color(30, 100, 180));
        styleButton(btnDelete, new Color(180, 50, 50));
        styleButton(btnRefresh, new Color(90, 90, 90));

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un registro para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showEditDialog(row);
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un registro para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar este registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteSelected(row);
                loadData();
            }
        });
        btnRefresh.addActionListener(e -> loadData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    /** Define las columnas de la tabla */
    protected abstract void initColumns();

    /** Carga los datos desde el servicio a la tabla */
    protected abstract void loadData();

    /** Muestra el diálogo para agregar un nuevo registro */
    protected abstract void showAddDialog();

    /** Muestra el diálogo para editar el registro en la fila indicada */
    protected abstract void showEditDialog(int row);

    /** Elimina el registro en la fila indicada */
    protected abstract void deleteSelected(int row);
}
