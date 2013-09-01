package com.macbury.fabula.editor.gamerunner;

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
import com.macbury.fabula.editor.WorldEditorFrame;
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
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import java.awt.FlowLayout;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/*
 * adb wait-for-device
 * adb push data /sdcard/bca
 * adb shell am start -n com.macbury.fabula.player/.MainActivity
 */

public class RunningGameConsoleFrame extends JDialog implements WindowListener, GameRunnableCallback, ActionListener {
  
  private static final String TAG = "Running Game";
  private GameManager gameManager;
  private GameRunnable gameRunnable;
  private Thread gameThread;

  /**
   * Create the frame.
   */
  public RunningGameConsoleFrame() {
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setResizable(false);
    addWindowListener(this);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setTitle("Game");
    setType(Type.UTILITY);
    setBounds(100, 100, 308, 95);
    getContentPane().setLayout(new BorderLayout(2, 2));
    
    JPanel panel = new JPanel();
    panel.setBorder(new EmptyBorder(0, 0, 0, 0));
    getContentPane().add(panel, BorderLayout.CENTER);
    panel.setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Please Wait... Game is running!");
    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel.setBounds(62, 11, 179, 14);
    panel.add(lblNewLabel);
    
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setBounds(10, 36, 283, 14);
    panel.add(progressBar);
  }

  public void runGame(WorldEditorFrame worldEditorFrame, GameManager gameManager) {
    this.gameManager = gameManager;
    gameManager.pause();
    
    this.gameRunnable = new GameRunnable(this);
    this.gameThread   = new Thread(gameRunnable);
    this.gameThread.start();
    System.gc();
    this.setLocationRelativeTo(worldEditorFrame);
    this.setVisible(true);
  }

  @Override
  public void windowActivated(WindowEvent arg0) {
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    log("Finishing game");
    if (gameRunnable != null) {
      gameRunnable.stop();
    }
    
    this.gameThread = null;
    this.gameRunnable = null;
    gameManager.resume();
    
  }


  @Override
  public void windowClosing(WindowEvent arg0) {
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
  }

  @Override
  public void onGameEnd() {
    this.dispose();
  }

  @Override
  public void onLog(String line) {
    Gdx.app.log(TAG, line);
  }

  private void log(String line) {
    Gdx.app.log(TAG, line);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
  }
}
