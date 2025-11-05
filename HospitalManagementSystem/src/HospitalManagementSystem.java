import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.stream.Stream;

// ===== Patient Class (Node) =====
class Patient {
    int id;
    String name;
    int age;
    String disease;
    String doctor;
    Patient next;

    Patient(int id, String name, int age, String disease, String doctor) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.disease = disease;
        this.doctor = doctor;
        this.next = null;
    }
}

// ===== Linked List Class =====
class LinkedListHospital {
    Patient head;

    public void addPatient(int id, String name, int age, String disease, String doctor) {
        if (searchPatientByID(id) != null) throw new IllegalArgumentException("Patient ID already exists.");
        Patient newPatient = new Patient(id, name, age, disease, doctor);
        if (head == null) head = newPatient;
        else {
            Patient temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newPatient;
        }
    }

    public boolean updatePatient(int id, String name, int age, String disease, String doctor) {
        Patient temp = head;
        while (temp != null) {
            if (temp.id == id) {
                temp.name = name;
                temp.age = age;
                temp.disease = disease;
                temp.doctor = doctor;
                return true;
            }
            temp = temp.next;
        }
        return false;
    }

    public boolean deletePatient(int id) {
        if (head == null) return false;
        if (head.id == id) { head = head.next; return true; }

        Patient prev = head;
        Patient curr = head.next;
        while (curr != null) {
            if (curr.id == id) { prev.next = curr.next; return true; }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    public Patient searchPatientByName(String name) {
        Patient temp = head;
        while (temp != null) {
            if (temp.name.equalsIgnoreCase(name)) return temp;
            temp = temp.next;
        }
        return null;
    }

    public Patient searchPatientByID(int id) {
        Patient temp = head;
        while (temp != null) {
            if (temp.id == id) return temp;
            temp = temp.next;
        }
        return null;
    }

    public void showAll(DefaultTableModel model) {
        model.setRowCount(0);
        Patient temp = head;
        while (temp != null) {
            model.addRow(new Object[]{temp.id, temp.name, temp.age, temp.disease, temp.doctor});
            temp = temp.next;
        }
    }
}

// ===== GUI UTILITY (Image Panel for Background) =====
class ImagePanel extends JPanel {
    private Image backgroundImage;
    private static final String IMAGE_PATH = "images/backgroundd.png";

    public ImagePanel() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/" + IMAGE_PATH);
            if (imageStream != null) {
                backgroundImage = ImageIO.read(imageStream);
                imageStream.close();
            } else {
                setBackground(new Color(248, 250, 255));
            }
        } catch (Exception e) {
            setBackground(new Color(248, 250, 255));
        }
        setLayout(new BorderLayout());
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

// ===== PROFESSIONAL GUI CLASS (HospitalManagementPro) =====
class HospitalManagementPro extends JFrame implements ActionListener {
    LinkedListHospital list = new LinkedListHospital();
    JTextField idField, nameField, ageField, diseaseField, doctorField, searchField;
    JTable table;
    DefaultTableModel model;
    JButton addBtn, updateBtn, deleteBtn, clearBtn, searchBtn, showBtn;

    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font FIELD_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private static final Color SECONDARY_COLOR = new Color(240, 248, 255, 240);

    HospitalManagementPro() {
        try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); }
        catch (Exception e) { System.out.println("Could not set Nimbus Look and Feel."); }

        setTitle("🏥 Professional Hospital Management System (Linked List)");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        ImagePanel backgroundPanel = new ImagePanel();
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout(20, 20));

        // HEADER
        JLabel header = new JLabel("Professional Patient Records Management", JLabel.CENTER);
        header.setFont(HEADER_FONT);
        header.setForeground(PRIMARY_COLOR.darker());
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        backgroundPanel.add(header, BorderLayout.NORTH);

        // MAIN CONTAINER
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // INPUT PANEL
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2), "Patient Details Input",
                0, 0, FIELD_LABEL_FONT, PRIMARY_COLOR));
        inputPanel.setBackground(SECONDARY_COLOR);
        inputPanel.setPreferredSize(new Dimension(350, 300));

        idField = createStyledTextField();
        nameField = createStyledTextField();
        ageField = createStyledTextField();
        diseaseField = createStyledTextField();
        doctorField = createStyledTextField();

        inputPanel.add(createStyledLabel("Patient ID:")); inputPanel.add(idField);
        inputPanel.add(createStyledLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(createStyledLabel("Age:")); inputPanel.add(ageField);
        inputPanel.add(createStyledLabel("Disease:")); inputPanel.add(diseaseField);
        inputPanel.add(createStyledLabel("Doctor:")); inputPanel.add(doctorField);

        // BUTTON PANEL
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setOpaque(false);

        addBtn = createStyledButton("➕ Add New", new Color(34, 139, 34));
        updateBtn = createStyledButton("✏️ Update", new Color(0, 120, 215));
        deleteBtn = createStyledButton("🗑️ Delete", new Color(200, 0, 0));
        showBtn = createStyledButton("📋 Show All", new Color(30, 144, 255));
        clearBtn = createStyledButton("🧹 Clear Fields", new Color(255, 165, 0));
        searchBtn = createStyledButton("🔍 Search by Name", new Color(105, 105, 105));

        buttonPanel.add(addBtn); buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn); buttonPanel.add(showBtn);
        buttonPanel.add(clearBtn); buttonPanel.add(searchBtn);

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setOpaque(false);
        westPanel.add(inputPanel, BorderLayout.NORTH);
        westPanel.add(buttonPanel, BorderLayout.CENTER);

        mainContainer.add(westPanel, BorderLayout.WEST);

        // TABLE
        model = new DefaultTableModel(new String[]{"Patient ID", "Name", "Age", "Disease", "Doctor"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // SEARCH
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(250, 30));
        searchPanel.add(new JLabel("Search Patient by Name:")); searchPanel.add(searchField);

        backgroundPanel.add(mainContainer, BorderLayout.CENTER);
        backgroundPanel.add(searchPanel, BorderLayout.SOUTH);

        // ACTIONS
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        showBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        searchBtn.addActionListener(this);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) loadSelectedRowToFields();
        });

        setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FIELD_LABEL_FONT);
        label.setForeground(new Color(30, 30, 30));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR.brighter()));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hover = bgColor.brighter();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){ btn.setBackground(hover); }
            public void mouseExited(MouseEvent e){ btn.setBackground(bgColor); }
        });
        return btn;
    }

    private void loadSelectedRowToFields() {
        int r = table.getSelectedRow();
        if (r != -1) {
            idField.setText(model.getValueAt(r,0).toString());
            nameField.setText(model.getValueAt(r,1).toString());
            ageField.setText(model.getValueAt(r,2).toString());
            diseaseField.setText(model.getValueAt(r,3).toString());
            doctorField.setText(model.getValueAt(r,4).toString());
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn || e.getSource() == updateBtn) {
                if (Stream.of(idField,nameField,ageField,diseaseField,doctorField).anyMatch(f -> f.getText().trim().isEmpty())) {
                    JOptionPane.showMessageDialog(this,"Fill all fields!","Error",JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (e.getSource() == addBtn) {
                int id = Integer.parseInt(idField.getText());
                int age = Integer.parseInt(ageField.getText());
                list.addPatient(id,nameField.getText(),age,diseaseField.getText(),doctorField.getText());
                list.showAll(model);
                clearFields();
            } else if (e.getSource() == updateBtn) {
                int id = Integer.parseInt(idField.getText());
                int age = Integer.parseInt(ageField.getText());
                list.updatePatient(id,nameField.getText(),age,diseaseField.getText(),doctorField.getText());
                list.showAll(model);
            } else if (e.getSource() == deleteBtn) {
                int id = Integer.parseInt(idField.getText());
                list.deletePatient(id);
                list.showAll(model);
                clearFields();
            } else if (e.getSource() == showBtn) {
                list.showAll(model);
            } else if (e.getSource() == clearBtn) {
                clearFields();
            } else if (e.getSource() == searchBtn) {
                String name = searchField.getText();
                Patient p = list.searchPatientByName(name);
                model.setRowCount(0);
                if (p != null) model.addRow(new Object[]{p.id,p.name,p.age,p.disease,p.doctor});
            }
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
    }

    private void clearFields() {
        idField.setText(""); nameField.setText(""); ageField.setText("");
        diseaseField.setText(""); doctorField.setText(""); searchField.setText("");
        table.clearSelection();
    }
}

// ===== MAIN CLASS =====
public class HospitalManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalManagementPro::new);
    }
}
