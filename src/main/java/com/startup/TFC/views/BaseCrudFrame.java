package com.startup.TFC.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Frame base mejorado con:
 * - Barra de búsqueda/filtro en tiempo real sobre la tabla
 * - Contador de registros visibles vs totales
 * - Ordenamiento por columna (clic en encabezado)
 * - Filas con color alternado
 */
public abstract class BaseCrudFrame extends JFrame {

    protected JTable table;
    protected DefaultTableModel tableModel;
    protected TableRowSorter<DefaultTableModel> rowSorter;

    protected JButton btnAdd;
    protected JButton btnEdit;
    protected JButton btnDelete;
    protected JButton btnRefresh;

    private JTextField txtFilter;
    private JLabel lblCount;

    public BaseCrudFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setMinimumSize(new Dimension(600, 350));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initBaseComponents();
        initColumns();
    }

    private void initBaseComponents() {
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);

        // Filas con color alternado
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return c;
            }
        });

        // Ordenamiento por columna al hacer clic en encabezado
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // ── Barra superior: filtro + contador ─────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout(8, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        JPanel filterPanel = new JPanel(new BorderLayout(4, 0));
        JLabel lblSearch = new JLabel("🔍 Buscar:  ");
        lblSearch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtFilter = new JTextField();
        txtFilter.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtFilter.setToolTipText("Filtra por cualquier columna en tiempo real");

        JButton btnClear = new JButton("✕");
        btnClear.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnClear.setToolTipText("Limpiar filtro");
        btnClear.setMargin(new Insets(2, 6, 2, 6));
        btnClear.addActionListener(e -> { txtFilter.setText(""); applyFilter(); txtFilter.requestFocus(); });

        filterPanel.add(lblSearch, BorderLayout.WEST);
        filterPanel.add(txtFilter, BorderLayout.CENTER);
        filterPanel.add(btnClear,  BorderLayout.EAST);

        lblCount = new JLabel("0 registros");
        lblCount.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblCount.setForeground(Color.GRAY);
        lblCount.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(lblCount,    BorderLayout.EAST);

        txtFilter.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applyFilter(); }
        });
        rowSorter.addRowSorterListener(e -> updateCount());

        // ── Botones CRUD ───────────────────────────────────────────────────
        btnAdd     = new JButton("Agregar");
        btnEdit    = new JButton("Editar");
        btnDelete  = new JButton("Eliminar");
        btnRefresh = new JButton("Recargar");

        styleButton(btnAdd,     new Color(46, 139, 87));
        styleButton(btnEdit,    new Color(30, 100, 180));
        styleButton(btnDelete,  new Color(180, 50, 50));
        styleButton(btnRefresh, new Color(90, 90, 90));

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) { showWarning("Seleccione un registro para editar."); return; }
            showEditDialog(table.convertRowIndexToModel(viewRow));
        });
        btnDelete.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) { showWarning("Seleccione un registro para eliminar."); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar este registro?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteSelected(table.convertRowIndexToModel(viewRow));
                loadData();
            }
        });
        btnRefresh.addActionListener(e -> { txtFilter.setText(""); applyFilter(); loadData(); });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        setLayout(new BorderLayout());
        add(topPanel,             BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel,          BorderLayout.SOUTH);
    }

    private void applyFilter() {
        String text = txtFilter.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            try {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            } catch (java.util.regex.PatternSyntaxException ignored) {}
        }
        updateCount();
    }

    protected void updateCount() {
        int visible = table.getRowCount();
        int total   = tableModel.getRowCount();
        lblCount.setText(visible == total
                ? total + " registro" + (total != 1 ? "s" : "")
                : visible + " de " + total + " registros");
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    protected void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    protected void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected JButton createSaveButton() {
        JButton btn = new JButton("Guardar");
        btn.setBackground(new Color(46, 139, 87));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    protected void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    protected abstract void initColumns();
    protected abstract void loadData();
    protected abstract void showAddDialog();
    protected abstract void showEditDialog(int modelRow);
    protected abstract void deleteSelected(int modelRow);
}
