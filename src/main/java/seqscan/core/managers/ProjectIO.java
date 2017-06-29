package seqscan.core.managers;

import seqscan.core.Main;
import seqscan.core.ui.*;
import seqscan.core.utils.Common;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Umarov on 1/25/2017.
 */
public class ProjectIO {


    private static boolean isSaved;
    private ProjectState s;
    private MainWindow mainWindow;
    private JFrame ff;
    private JFrame npf;

    public ProjectIO(ProjectState s, MainWindow mainWindow) {
        this.s = s;
        this.mainWindow = mainWindow;
        ff = new FirstFrame(this);
    }

    public void newProject() {
        saveProjectAs();
    }

    public boolean openProject() {
        FileDialog fd = new FileDialog((Frame) null, "Open Project", FileDialog.LOAD);
        fd.setVisible(true);
        fd.setFilenameFilter(new seqscan.core.ui.FileFilter());
        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            openProject2(f);
        }
        return isSaved;
    }

    public void openProject2(File f) {
        ObjectInputStream ois = null;
        try {
            FileInputStream fin = new FileInputStream(f.getAbsolutePath());
            GZIPInputStream gis = new GZIPInputStream(fin);
            ois = new ObjectInputStream(gis);
            s = (ProjectState) ois.readObject();
            Main.setState(s);
            mainWindow.setTitle("seqscan - " + s.projectName);
            s.projectPath = f.getParentFile().toString() + File.separator;
            isSaved = true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Cannot open the project!", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public boolean saveProjectAs() {
        FileDialog fd = new FileDialog((Frame) null, "Save Project", FileDialog.SAVE);
        fd.setVisible(true);
        fd.setFilenameFilter(new seqscan.core.ui.FileFilter());

        if (fd.getFiles().length > 0) {
            File f = fd.getFiles()[0];
            String oldPath = s.projectPath + s.projectName;
            s.projectName = f.getName();
            s.projectPath = f.getAbsolutePath();
            saveProjectAs2(oldPath);
            return true;
        }
        return false;
    }

    public void saveProjectAs2(String oldPath) {
        mainWindow.setTitle("seqscan - " + s.projectName);
        new File(s.projectPath).mkdir();
        s.projectPath += File.separator;
        new File(s.projectPath + s.projectName).mkdir();
        if (isSaved && oldPath != null) {
            try {
                Common.copy(oldPath + File.separator + "images" + File.separator, s.projectPath + s.projectName + File.separator + "images" + File.separator);
                Common.copy(oldPath + File.separator + "compounds" + File.separator, s.projectPath + s.projectName + File.separator + "compounds" + File.separator);
            } catch (Exception e) {
                new File(s.projectPath + s.projectName + File.separator + "images").mkdir();
                new File(s.projectPath + s.projectName + File.separator + "parts").mkdir();
            }
        } else {
            new File(s.projectPath + s.projectName + File.separator + "images").mkdir();
            new File(s.projectPath + s.projectName + File.separator + "parts").mkdir();
        }
        isSaved = true;
        saveProject();
    }

    public void saveProject() {
        if (!isSaved) {
            saveProjectAs();
            return;
        }
        try {
            FileOutputStream fout = new FileOutputStream(s.projectPath + s.projectName + ".bdp");
            GZIPOutputStream gz = new GZIPOutputStream(fout);
            ObjectOutputStream oos = new ObjectOutputStream(gz);
            oos.writeObject(s);
            oos.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error While Saving!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void newProjectSelected(File f) {
        s.projectName = f.getName();
        s.projectPath = f.getAbsolutePath();
        saveProjectAs2(null);
        mainWindow.setTitle("seqscan - " + s.projectName);
        clear();
        mainWindow.setVisible(true);
        remember();
    }

    public void openProjectSelected() {
        if (openProject()) {
            clear();
            mainWindow.setVisible(true);
            mainWindow.writeToConsole("Testing\nTesting\nTesting");
            mainWindow.setTitle("seqscan - " + s.projectName);
            remember();
        }

    }

    public void remember() {
        try {
            String path = s.projectPath + s.projectName + ".bdp";
            File f = getPFile();
            f.createNewFile();
            Scanner scan = new Scanner(f);
            ArrayList<String> projects = new ArrayList<>();
            while (scan.hasNextLine()) {
                projects.add(scan.nextLine());
            }
            scan.close();
            if (projects.contains(path)) {
                projects.remove(projects.indexOf(path));
            }
            projects.add(0, path);
            while (projects.size() > 10) {
                projects.remove(projects.size() - 1);
            }
            String s = "";
            for (String p : projects) {
                s += p;
                if (projects.indexOf(p) != projects.size() - 1)
                    s += "\n";
            }
            Files.write(getPFile().toPath(), s.getBytes(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove(int j) {
        try {
            File f = getPFile();
            Scanner scan = new Scanner(f);
            ArrayList<String> projects = new ArrayList<>();
            while (scan.hasNextLine()) {
                projects.add(scan.nextLine());
            }
            scan.close();
            String s = "";
            for (int i = 0; i < projects.size(); i++) {
                if (i == j) {
                    continue;
                }
                String p = projects.get(i);
                s += p;
            }
            if (s.endsWith("\n")) {
                s = s.substring(0, s.length() - 2);
            }
            Files.delete(getPFile().toPath());
            Files.write(getPFile().toPath(), s.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRecent() {
        try {
            Scanner scan = new Scanner(getPFile());
            ArrayList<String> projects = new ArrayList<>();
            while (scan.hasNextLine()) {
                projects.add(scan.nextLine());
            }
            scan.close();
            return projects.toArray(new String[0]);
        } catch (IOException e) {
        }
        return null;
    }

    private File getPFile() {
        File f = new File(System.getProperty("user.home") + File.separator + "SeqScan" + File.separator + "projects");
        File theDir = new File(System.getProperty("user.home") + File.separator + "SeqScan");
        if (!theDir.exists()) {
            theDir.mkdir();
        }
        try {
            f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    public void openRecentSelected(String f, int selectedIndex) {
        openProject2(new File(f));
        if (isSaved) {
            clear();
            mainWindow.setTitle("SeqScan - " + s.projectName);
            mainWindow.setVisible(true);
            mainWindow.writeToConsole("Testing\nTesting\nTesting");
            remember();
        } else {
            //remove(selectedIndex);
        }

    }

    public void checkSaved() {
        if (!isSaved) {
            saveProjectAs();
        }
    }

    public void showWelcome() {
        ff.setVisible(true);
    }

    public void newProjectSelected() {
        ff.setVisible(false);
        npf = new NewProjectFrame(this);
        npf.setVisible(true);
    }

    private void clear() {
        if (ff != null) {
            ff.setVisible(false);
            ff.dispose();
        }
        if (npf != null) {
            npf.setVisible(false);
            npf.dispose();
        }
    }

    public void creationCanceled() {
        ff.setVisible(true);
    }

    public void saveImage() {
        try {
            BufferedImage image = null;
            String name = "graph";
            File f = new File(s.projectPath + s.projectName + File.separator + "images" + File.separator + name + ".png");
            for (int i = 0; f.exists(); i++) {
                f = new File(s.projectPath + s.projectName + File.separator + "images" + File.separator + name + i + ".png");
            }
            ImageIO.write(image, "PNG", f);
            mainWindow.setStatusLabel("Image saved");
        } catch (Exception e) {

        }
    }

}
