package com.macbury.fabula.editor.tiles;

import java.awt.EventQueue;

import javax.swing.JDialog;
import java.awt.Window.Type;
import javax.swing.JComboBox;
import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import java.awt.Dialog.ModalityType;
import javax.swing.DefaultComboBoxModel;

import com.macbury.fabula.editor.tiles.TilesetGenerator.TileGeneratorListener;
import com.macbury.fabula.manager.ResourceManager;
import com.macbury.fabula.terrain.Tileset;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.text.JTextComponent;

public class TilesetBuilderDialog extends JDialog implements ActionListener, TileGeneratorListener {
  private JComboBox<String> tilesetComboBox;
  private JProgressBar progressBar;
  private JTextField autoTileTextField;
  private JTextField texturesTextField;
  private TilesetGenerator generator;
  private JButton btnSelectAutoTileDirectory;
  private JButton btnSelectTexturesDirectory;
  private JButton btnBuild;
  private JTextComponent logPane;
  
  public TilesetBuilderDialog() {
    generator = new TilesetGenerator(this);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setResizable(false);
    setType(Type.POPUP);
    setTitle("Tile Builder");
    setBounds(100, 100, 753, 376);
    getContentPane().setLayout(null);
    
    this.tilesetComboBox = new JComboBox<String>();
    
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
    
    for (Tileset tileset : ResourceManager.shared().allTilesets()) {
      model.addElement(tileset.getName());
    }
    
    tilesetComboBox.setModel(model);
    tilesetComboBox.setBounds(75, 11, 662, 20);
    getContentPane().add(tilesetComboBox);
    
    JLabel lblNewLabel = new JLabel("Tilleset");
    lblNewLabel.setBounds(10, 14, 46, 14);
    getContentPane().add(lblNewLabel);
    
    this.btnBuild = new JButton("Build!");
    btnBuild.addActionListener(this);
    btnBuild.setBounds(10, 313, 727, 23);
    getContentPane().add(btnBuild);
    
    this.progressBar = new JProgressBar();
    progressBar.setBounds(75, 106, 626, 14);
    getContentPane().add(progressBar);
    
    JLabel lblNewLabel_1 = new JLabel("Autotiles");
    lblNewLabel_1.setBounds(10, 45, 46, 14);
    getContentPane().add(lblNewLabel_1);
    
    autoTileTextField = new JTextField();
    autoTileTextField.setBounds(75, 42, 626, 20);
    getContentPane().add(autoTileTextField);
    autoTileTextField.setColumns(10);
    autoTileTextField.setText(generator.getTempAbsolutePath("autotiles"));
    
    this.btnSelectAutoTileDirectory = new JButton("...");
    btnSelectAutoTileDirectory.addActionListener(this);
    btnSelectAutoTileDirectory.setBounds(711, 42, 26, 23);
    getContentPane().add(btnSelectAutoTileDirectory);
    
    this.btnSelectTexturesDirectory = new JButton("...");
    btnSelectTexturesDirectory.addActionListener(this);
    btnSelectTexturesDirectory.setBounds(711, 72, 26, 23);
    getContentPane().add(btnSelectTexturesDirectory);
    
    texturesTextField = new JTextField();
    texturesTextField.setColumns(10);
    texturesTextField.setBounds(75, 73, 626, 20);
    texturesTextField.setText(generator.getTempAbsolutePath("textures"));
    getContentPane().add(texturesTextField);
    
    JLabel lblTextures = new JLabel("Textures");
    lblTextures.setBounds(10, 79, 46, 14);
    getContentPane().add(lblTextures);
    
    JLabel lblProgress = new JLabel("Progress");
    lblProgress.setBounds(10, 106, 46, 14);
    getContentPane().add(lblProgress);
    
    JSeparator separator = new JSeparator();
    separator.setBounds(10, 300, 727, 2);
    getContentPane().add(separator);
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 134, 727, 155);
    getContentPane().add(scrollPane);
    
    this.logPane = new JTextPane();
    logPane.setFont(new Font("Consolas", Font.PLAIN, 12));
    logPane.setEditable(false);
    scrollPane.setViewportView(logPane);
    
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btnSelectAutoTileDirectory || e.getSource() == btnSelectTexturesDirectory) {
      JTextField targetTextField = e.getSource() == btnSelectAutoTileDirectory ? autoTileTextField : texturesTextField;
      
      JFileChooser chooser = new JFileChooser(targetTextField.getText());
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      Integer opt = chooser.showSaveDialog(this);
      if (opt == JFileChooser.APPROVE_OPTION) {
        targetTextField.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    }
    
    if (e.getSource() == btnBuild) {
      build();
    }
  }

  private void build() {
    btnBuild.setEnabled(false);
    progressBar.setIndeterminate(true);
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        Tileset tileset = ResourceManager.shared().getTileset((String)tilesetComboBox.getSelectedItem());
        generator.build(tileset, autoTileTextField.getText(), texturesTextField.getText());
      }
    });
    
    thread.start();
  }

  @Override
  public void onProgress(int progress, int max) {
    if (max == 0) {
      progressBar.setIndeterminate(true);
    } else {
      progressBar.setIndeterminate(false);
      progressBar.setMaximum(max);
      progressBar.setValue(progress);
    }
  }

  @Override
  public void onLog(String line) {
    logPane.setText(logPane.getText() + line + "\n");
  }

  @Override
  public void onFinish() {
    progressBar.setIndeterminate(false);
    progressBar.setMaximum(0);
    progressBar.setValue(0);
    btnBuild.setEnabled(true);
  }
}
