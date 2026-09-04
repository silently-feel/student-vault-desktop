package Student;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;

public class MainForm extends JFrame {
    private JTextField txtId, txtName, txtAge, txtCourse, txtSearch;
    private JLabel lblImagePreview, lblTotalCount;
    private JTable tableStudents;
    private DefaultTableModel tableModel;
    private JButton btnUpload, btnAdd, btnUpdate, btnDelete, btnClear, btnRefresh, btnSearch, btnExport;

    private byte[] studentImageBytes = null;
    private StudentDAO studentDAO;

    public MainForm() {
        studentDAO = new StudentDAO();

        setTitle("Student Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL: INPUT FORM ---
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Student Details"));

        panelForm.add(new JLabel(" Student ID (Auto):"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelForm.add(txtId);

        panelForm.add(new JLabel(" Name:"));
        txtName = new JTextField();
        panelForm.add(txtName);

        panelForm.add(new JLabel(" Age:"));
        txtAge = new JTextField();
        panelForm.add(txtAge);

        panelForm.add(new JLabel(" Course:"));
        txtCourse = new JTextField();
        panelForm.add(txtCourse);

        panelForm.add(new JLabel(" Photo:"));
        JPanel panelImage = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnUpload = new JButton("Upload Image");
        lblImagePreview = new JLabel("No Image");
        lblImagePreview.setPreferredSize(new Dimension(60, 60));
        panelImage.add(btnUpload);
        panelImage.add(lblImagePreview);
        panelForm.add(panelImage);

        add(panelForm, BorderLayout.NORTH);

        // --- CENTER PANEL: TABLE & SEARCH BAR ---
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));

        // Search sub-panel
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        panelSearch.add(new JLabel("Search Name/Course:"));
        panelSearch.add(txtSearch);
        panelSearch.add(btnSearch);
        panelCenter.add(panelSearch, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Name", "Age", "Course"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableStudents = new JTable(tableModel);
        panelCenter.add(new JScrollPane(tableStudents), BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);

        // --- BOTTOM PANEL: BUTTONS & STATUS BAR ---
        JPanel panelBottom = new JPanel(new BorderLayout());

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");
        btnRefresh = new JButton("Refresh");
        btnExport = new JButton("Export to CSV");

        panelButtons.add(btnAdd);
        panelButtons.add(btnUpdate);
        panelButtons.add(btnDelete);
        panelButtons.add(btnClear);
        panelButtons.add(btnRefresh);
        panelButtons.add(btnExport); // Added export button here

        panelBottom.add(panelButtons, BorderLayout.CENTER);

        // Total Count Status Bar
        JPanel panelStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        lblTotalCount = new JLabel("Total Students: 0");
        panelStatus.add(lblTotalCount);
        panelBottom.add(panelStatus, BorderLayout.SOUTH);

        add(panelBottom, BorderLayout.SOUTH);

        // --- EVENT LISTENERS ---

        btnUpload.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    FileInputStream fis = new FileInputStream(file);
                    studentImageBytes = new byte[(int) file.length()];
                    fis.read(studentImageBytes);
                    fis.close();
                    displayStudentImage(studentImageBytes);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error reading image file: " + ex.getMessage());
                }
            }
        });

        btnAdd.addActionListener(e -> {
            if (!validateInputs()) return;
            try {
                Student student = new Student(0, txtName.getText().trim(), Integer.parseInt(txtAge.getText().trim()), txtCourse.getText().trim(), studentImageBytes);
                if (studentDAO.addStudent(student)) {
                    JOptionPane.showMessageDialog(this, "Student added successfully!");
                    loadStudentData();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add student.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadStudentData();
        });

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                loadStudentData();
            } else {
                tableModel.setRowCount(0);
                List<Student> students = studentDAO.searchStudents(keyword);
                for (Student s : students) {
                    tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getCourse()});
                }
                lblTotalCount.setText("Found: " + students.size() + " student(s)");
            }
        });

        // Export to CSV / Excel Action Listener
        btnExport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save as CSV");
            fileChooser.setSelectedFile(new File("students_report.csv"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Ensure file ends with .csv
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    fileToSave = new File(filePath + ".csv");
                }

                try (FileWriter writer = new FileWriter(fileToSave)) {
                    // Write CSV headers
                    writer.append("ID,Name,Age,Course\n");

                    // Write rows from the current JTable model view
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        writer.append(tableModel.getValueAt(i, 0).toString()).append(",");
                        writer.append("\"").append(tableModel.getValueAt(i, 1).toString()).append("\",");
                        writer.append(tableModel.getValueAt(i, 2).toString()).append(",");
                        writer.append("\"").append(tableModel.getValueAt(i, 3).toString()).append("\"\n");
                    }

                    JOptionPane.showMessageDialog(this, "Data successfully exported to:\n" + fileToSave.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error exporting file: " + ex.getMessage());
                }
            }
        });

        btnClear.addActionListener(e -> clearFields());

        tableStudents.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableStudents.getSelectedRow();
            if (selectedRow != -1) {
                txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtAge.setText(tableModel.getValueAt(selectedRow, 2).toString());
                txtCourse.setText(tableModel.getValueAt(selectedRow, 3).toString());

                List<Student> list = studentDAO.getAllStudents();
                for (Student s : list) {
                    if (s.getId() == Integer.parseInt(txtId.getText())) {
                        studentImageBytes = s.getImage();
                        displayStudentImage(studentImageBytes);
                        break;
                    }
                }
            }
        });

        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a student to delete.");
                return;
            }
            int id = Integer.parseInt(txtId.getText());
            if (studentDAO.deleteStudent(id)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                loadStudentData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student.");
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a student to update.");
                return;
            }
            if (!validateInputs()) return;
            try {
                int id = Integer.parseInt(txtId.getText());
                Student student = new Student(id, txtName.getText().trim(), Integer.parseInt(txtAge.getText().trim()), txtCourse.getText().trim(), studentImageBytes);
                if (studentDAO.updateStudent(student)) {
                    JOptionPane.showMessageDialog(this, "Student updated successfully!");
                    loadStudentData();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update student.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        loadStudentData();
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty() || txtAge.getText().trim().isEmpty() || txtCourse.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (Name, Age, Course) must be filled out!");
            return false;
        }
        try {
            int age = Integer.parseInt(txtAge.getText().trim());
            if (age <= 0 || age > 120) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age between 1 and 120.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer number.");
            return false;
        }
        return true;
    }

    private void displayStudentImage(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                Image img = ImageIO.read(bis);
                Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(scaledImg));
                lblImagePreview.setText("");
            } catch (Exception e) {
                lblImagePreview.setIcon(null);
                lblImagePreview.setText("Error loading");
            }
        } else {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("No Image");
        }
    }

    private void loadStudentData() {
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getCourse()});
        }
        lblTotalCount.setText("Total Students: " + students.size());
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtAge.setText("");
        txtCourse.setText("");
        txtSearch.setText("");
        lblImagePreview.setIcon(null);
        lblImagePreview.setText("No Image");
        studentImageBytes = null;
        tableStudents.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainForm().setVisible(true));
    }
}