package seqscan.core;


import seqscan.core.managers.*;
import seqscan.core.ui.*;

import javax.swing.*;


public class Main {

    private static MainWindow mainWindow;
    private static ProjectState s;
    public static ProjectIO projectIO;

    public static void main(String[] args) {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
        s = new ProjectState();
        s.projectName = "DefaultProject";
        s.projectPath = "DefaultProject/";
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        mainWindow = new MainWindow();
        projectIO = new ProjectIO(s, mainWindow);
        projectIO.showWelcome();
    }

    public static void setState(ProjectState s) {
        Main.s = s;
    }
}
