package com.macbury.fabula.editor;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDialog;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.manager.GameManager;

import java.awt.Window.Type;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class RunningGameConsoleFrame extends JDialog implements WindowListener {
  
  private JPanel contentPane;
  private GameManager gameManager;

  /**
   * Create the frame.
   */
  public RunningGameConsoleFrame() {
    addWindowListener(this);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setTitle("Game console");
    setType(Type.UTILITY);
    setAlwaysOnTop(true);
    setBounds(100, 100, 762, 262);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new BorderLayout(0, 0));
    
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    contentPane.add(toolBar, BorderLayout.NORTH);
    
    JButton btnClear = new JButton("Clear");
    toolBar.add(btnClear);
    
    JButton btnStop = new JButton("Stop");
    toolBar.add(btnStop);
    
    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);
    
    JTextPane textPane = new JTextPane();
    textPane.setEditable(false);
    scrollPane.setViewportView(textPane);
  }

  public void runGame(GameManager gameManager) {
    this.gameManager = gameManager;
    gameManager.pause();
    System.gc();
    this.setVisible(true);
  }

  @Override
  public void windowActivated(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    
  }

  private void finishGame() {
    gameManager.resume();
    System.gc();
  }

  @Override
  public void windowClosing(WindowEvent arg0) {
    finishGame();
  }

  @Override
  public void windowDeactivated(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowDeiconified(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowIconified(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void windowOpened(WindowEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  
}
