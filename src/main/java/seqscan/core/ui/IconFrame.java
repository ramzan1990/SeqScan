package seqscan.core.ui;

import seqscan.core.Main;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Umarov on 3/18/2017.
 */
public class IconFrame extends  JFrame{
    public IconFrame(){
        try {
            ArrayList localArrayList = new ArrayList();
            localArrayList.add(new ImageIcon(Main.class.getResource("/images/ic16.png")).getImage());
            localArrayList.add(new ImageIcon(Main.class.getResource("/images/ic32.png")).getImage());
            localArrayList.add(new ImageIcon(Main.class.getResource("/images/ic64.png")).getImage());
            localArrayList.add(new ImageIcon(Main.class.getResource("/images/ic128.png")).getImage());
            this.setIconImages(localArrayList);
        } catch (Exception localException2) {
        }
    }
}
