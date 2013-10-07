package com.macbury.fabula.editor.events;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;

import com.artemis.Entity;
import com.macbury.fabula.screens.WorldEditScreen;

import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EventEditorFrame extends JDialog implements ActionListener {
  
  private final JPanel contentPanel = new JPanel();
  private JTextField textField;
  private JButton okButton;
  private JButton cancelButton;
  
  /**
   * Create the dialog.
   * @param screen 
   */
  public EventEditorFrame(WorldEditScreen screen) {
    setModalityType(ModalityType.APPLICATION_MODAL);
    setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle("Event Editor");
    setBounds(100, 100, 873, 617);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new BorderLayout(0, 0));
    
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    contentPanel.add(tabbedPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel();
    tabbedPane.addTab("ConditionPage 1", null, panel, null);
    panel.setLayout(new BorderLayout(0, 0));
    
    JSplitPane splitPane = new JSplitPane();
    splitPane.setContinuousLayout(true);
    splitPane.setResizeWeight(0.3);
    panel.add(splitPane);
    
    JPanel panel_1 = new JPanel();
    splitPane.setLeftComponent(panel_1);
    
    JPanel panel_2 = new JPanel();
    splitPane.setRightComponent(panel_2);
    
    JPanel panel_3 = new JPanel();
    panel_3.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPanel.add(panel_3, BorderLayout.NORTH);
    panel_3.setLayout(new BorderLayout(0, 0));
    
    JLabel lblNewLabel = new JLabel("Event name:  ");
    panel_3.add(lblNewLabel, BorderLayout.WEST);
    
    textField = new JTextField();
    lblNewLabel.setLabelFor(textField);
    panel_3.add(textField, BorderLayout.CENTER);
    textField.setColumns(10);
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    this.okButton = new JButton("OK");
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    
    this.cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    cancelButton.setActionCommand("Cancel");
    buttonPane.add(cancelButton);
  }

  public void setEvent(Entity event) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == cancelButton) {
      this.setVisible(false);
    }
  }
}
