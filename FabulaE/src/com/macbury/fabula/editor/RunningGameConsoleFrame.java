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
import com.macbury.fabula.editor.gamerunner.GameRunnable;
import com.macbury.fabula.editor.gamerunner.GameRunnable.GameRunnableCallback;
import com.macbury.fabula.manager.GameManager;
import com.macbury.fabula.utils.GameRunner;

import java.awt.Window.Type;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RunningGameConsoleFrame extends JDialog implements WindowListener, GameRunnableCallback, ActionListener {
  
  private JPanel contentPane;
  private GameManager gameManager;
  private GameRunnable gameRunnable;
  private Thread gameThread;
  private JTextPane logView;
  private JButton btnClear;
  private JButton btnStop;

  /**
   * Create the frame.
   */
  public RunningGameConsoleFrame() {
    addWindowListener(this);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setTitle("Game console");
    setType(Type.UTILITY);
    setBounds(100, 100, 639, 551);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new BorderLayout(0, 0));
    
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    contentPane.add(toolBar, BorderLayout.NORTH);
    
    this.btnClear = new JButton("Clear");
    btnClear.addActionListener(this);
    toolBar.add(btnClear);
    
    this.btnStop = new JButton("Stop");
    btnStop.addActionListener(this);
    toolBar.add(btnStop);
    
    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);
    
    this.logView = new JTextPane();
    logView.setFont(new Font("Consolas", Font.PLAIN, 13));
    logView.setEditable(false);
    scrollPane.setViewportView(logView);
  }

  public void runGame(GameManager gameManager) {
    this.logView.setText("");
    this.gameManager = gameManager;
    gameManager.pause();
    
    this.gameRunnable = new GameRunnable(this);
    this.gameThread   = new Thread(gameRunnable);
    this.gameThread.start();
    System.gc();
    this.setVisible(true);
  }

  @Override
  public void windowActivated(WindowEvent arg0) {
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    
  }

  private void finishGame() {
    if (this.gameRunnable != null) {
      this.gameRunnable.stop();
    }
    gameManager.resume();
    System.gc();
  }

  @Override
  public void windowClosing(WindowEvent arg0) {
    finishGame();
  }

  @Override
  public void windowDeactivated(WindowEvent arg0) {
  }

  @Override
  public void windowDeiconified(WindowEvent arg0) {
  }

  @Override
  public void windowIconified(WindowEvent arg0) {
  }

  @Override
  public void windowOpened(WindowEvent arg0) {
  }

  @Override
  public void onGameStart() {
    log("Starting game");
  }

  @Override
  public void onGameEnd() {
    log("Finishing game");
    this.gameRunnable = null;
  }

  @Override
  public void onLog(String line) {
    log(line);
  }

  private void log(String line) {
    logView.setText(logView.getText() + line + "\n");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btnClear) {
      logView.setText("");
    }
    
    if (e.getSource() == btnStop) {
      logView.setText("");
      if (this.gameRunnable != null) {
        this.gameRunnable.stop();
      }
    }
  }
  
}
