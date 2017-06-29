package seqscan.core.ui;

import seqscan.core.managers.ProjectIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Created by Umarov on 1/23/2017.
 */
public class NewProjectFrame extends IconFrame {

    File f;

    public NewProjectFrame(ProjectIO io) {
        super();
        int w = 600;
        this.setSize(new Dimension(w, 400));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setLayout(new BorderLayout());


        JPanel topPanel = new JPanel();
        //topPanel.setSize(new Dimension(400, 250));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

        JTextField locationTF = new JTextField();
        int h = locationTF.getPreferredSize().height;
        h = Math.max(h, 25);
        //locationTF.setPreferredSize(new Dimension(300, 20));
        JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        browsePanel.setMaximumSize(new Dimension(w, 100));
        locationTF.setEditable(false);
        locationTF.setPreferredSize(new Dimension(340, h));
        JButton browse = new JButton("...");
        browse.setPreferredSize(new Dimension(30, h));
        //browse.setBorder(BorderFactory.createEtchedBorder(1));
        browse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                FileDialog fd = new FileDialog((Frame) null, "Save Project", FileDialog.SAVE);
                fd.setVisible(true);
                fd.setFilenameFilter(new FileFilter());

                if (fd.getFiles().length > 0) {
                    f = fd.getFiles()[0];
                    locationTF.setText(f.getAbsolutePath());
                }


            }
        });
        JLabel l1 = new JLabel("Location:");

        browsePanel.add(l1);
        browsePanel.add(locationTF);
        browsePanel.add(browse);
       // UI.addTo(topPanel, picLabel);
        topPanel.add(browsePanel);
        String options[] = {"TSS"};
        JComboBox textField = new JComboBox(options);
        textField.setPreferredSize(new Dimension(340, h));

        this.setLayout(new BorderLayout());
        JPanel lowerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));


        JButton b2 = new JButton("Create Project");
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (locationTF.getText().trim().length() != 0){
                    io.newProjectSelected(f);
                }
            }
        });
        lowerPanel.add(b2);
        lowerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        this.add(lowerPanel, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setTitle("Create New Project");
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                io.creationCanceled();
            }
        });
    }
}
