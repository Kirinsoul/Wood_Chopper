import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
/*
 * Created by JFormDesigner on Wed Jan 18 15:01:46 EST 2017
 */



/**
 * @author Stefano Tabone
 */
public class GuiMain extends JFrame {

    private WoodCutter script;

    public GuiMain(WoodCutter wood) {
        initComponents();
        this.script = wood;
        setValues();
    }

    /*
       Start button to set some values in script:
       - Does the user want to bank?
       - what kind of tree does the user want to chop?
       - where does the user want to chop?
     */

    private void button1ActionPerformed(ActionEvent e) throws  NullPointerException{

        script.setShouldBank(cmbBank.getSelectedItem().toString().equalsIgnoreCase("yes"));  //set should bank
        script.setShouldStart(true); //start script
        this.setVisible(false); //dispose of form
    }

    /*
       Set initial values in form
     */
    private void setValues(){

        cmbTree.setModel(new DefaultComboBoxModel<>(script.getTrees()));
        cmbLocation.setModel(new DefaultComboBoxModel<>(script.findTreeLocation("Tree")));

    }

    /*
        Changes the combobox values depending on what the user picked
     */
    private void cmbTreeItemStateChanged(ItemEvent e) {
        String s = cmbTree.getSelectedItem().toString();

        cmbLocation.setModel(new DefaultComboBoxModel<>(script.findTreeLocation(s)));

    }




    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        lblMain = new JLabel();
        lblBank = new JLabel();
        cmbBank = new JComboBox<>();
        button1 = new JButton();
        lblTree = new JLabel();
        lblLocation = new JLabel();
        cmbLocation = new JComboBox();
        cmbTree = new JComboBox();

        //======== this ========
        Container contentPane = getContentPane();

        //---- lblMain ----
        lblMain.setText("Wood Cutting !");
        lblMain.setFont(new Font("Segoe UI", Font.BOLD, 28));

        //---- lblBank ----
        lblBank.setText("Bank?");
        lblBank.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        //---- cmbBank ----
        cmbBank.setModel(new DefaultComboBoxModel<>(new String[] {
            "YES",
            "NO"
        }));

        //---- button1 ----
        button1.setText("START");
        button1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button1.addActionListener(e -> button1ActionPerformed(e));

        //---- lblTree ----
        lblTree.setText("Tree");
        lblTree.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        //---- lblLocation ----
        lblLocation.setText("Location");
        lblLocation.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        //---- cmbTree ----
        cmbTree.addItemListener(e -> cmbTreeItemStateChanged(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(32, 32, 32)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(lblTree)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbTree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(lblBank)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbBank, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblMain)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGap(38, 38, 38)
                            .addComponent(button1, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(lblLocation)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(39, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblMain)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(lblBank)
                        .addComponent(cmbBank, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(lblTree)
                        .addComponent(cmbTree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(lblLocation)
                            .addGap(24, 24, 24)
                            .addComponent(button1, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE))
                        .addComponent(cmbLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(26, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel lblMain;
    private JLabel lblBank;
    private JComboBox<String> cmbBank;
    private JButton button1;
    private JLabel lblTree;
    private JLabel lblLocation;
    private JComboBox cmbLocation;
    private JComboBox cmbTree;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
