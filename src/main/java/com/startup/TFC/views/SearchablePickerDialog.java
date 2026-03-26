package com.startup.TFC.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Function;

/**
 * Diálogo genérico de selección con búsqueda en tiempo real.
 * Reemplaza los JComboBox cuando la lista puede ser larga.
 *
 * Uso:
 *   SearchablePickerDialog<Professor> picker = new SearchablePickerDialog<>(
 *       parent, "Seleccionar Profesor",
 *       professors,
 *       new String[]{"DNI", "Nombre", "Área"},
 *       p -> new Object[]{p.getDni(), p.getName(), p.getArea()}
 *   );
 *   Professor selected = picker.showAndGet();
 */
public class SearchablePickerDialog<T> extends JDialog {

    private T selected = null;
    private final List<T> items;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTable table;

    public SearchablePickerDialog(Window parent,
                                  String title,
                                  List<T> items,
                                  String[] columns,
                                  Function<T, Object[]> rowMapper) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        this.items = items;

        setSize(560, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(8, 8));

        // ── Barra de búsqueda ──────────────────────────────────────────────
        JPanel searchPanel = new JPanel(new BorderLayout(6, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));
        JLabel searchIcon = new JLabel("🔍");
        JTextField txtSearch = new JTextField();
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtSearch.putClientProperty("JTextField.placeholderText", "Buscar...");
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // ── Tabla ──────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (T item : items) {
            tableModel.addRow(rowMapper.apply(item));
        }

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Filtro en tiempo real
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Doble clic para seleccionar
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) confirmSelection();
            }
        });

        // Enter para seleccionar
        table.getInputMap(JComponent.WHEN_FOCUSED)
             .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "select");
        table.getActionMap().put("select", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { confirmSelection(); }
        });

        // ── Botones ────────────────────────────────────────────────────────
        JButton btnSelect = new JButton("Seleccionar");
        JButton btnClear  = new JButton("Quitar selección");
        JButton btnCancel = new JButton("Cancelar");

        styleBtn(btnSelect, new Color(30, 100, 180));
        styleBtn(btnClear,  new Color(140, 80, 20));

        btnSelect.addActionListener(e -> confirmSelection());
        btnClear.addActionListener(e -> { selected = null; dispose(); });
        btnCancel.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnPanel.add(btnClear);
        btnPanel.add(btnCancel);
        btnPanel.add(btnSelect);

        // ── Contador de resultados ─────────────────────────────────────────
        JLabel lblCount = new JLabel(items.size() + " registros");
        lblCount.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblCount.setForeground(Color.GRAY);
        lblCount.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        sorter.addRowSorterListener(e -> {
            lblCount.setText(table.getRowCount() + " de " + items.size() + " registros");
        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(lblCount, BorderLayout.WEST);
        southPanel.add(btnPanel, BorderLayout.EAST);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Foco automático en búsqueda al abrir
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { txtSearch.requestFocusInWindow(); }
        });
    }

    private void confirmSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        selected = items.get(modelRow);
        dispose();
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Abre el diálogo y retorna el objeto seleccionado, o null si se canceló. */
    public T showAndGet() {
        setVisible(true);
        return selected;
    }
}
