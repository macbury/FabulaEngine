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
import javax.swing.SwingUtilities;

import com.badlogic.gdx.Gdx;
import com.macbury.fabula.editor.WorldEditorFrame;
import com.macbury.fabula.editor.adb.AdbManager;
import com.macbury.fabula.editor.gamerunner.GameRunnable.GameRunnableCallback;
import com.macbury.fabula.manager.G;
import com.macbury.fabula.manager.GameManager;

import java.awt.Window.Type;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import java.awt.FlowLayout;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;


public class RunningGameConsoleFrame extends JDialog implements WindowListener, ActionListener {
  public static enum TargetType {
    Device, Emulator
  }
  private static final String TAG = "Running Game";
  private GameManager gameManager;
  private RunnerThread runThread;
  private TargetType   target;
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
    
    JLabel lblNewLabel = new JLabel("Starting game!");
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
    worldEditorFrame.saveMap();
    G.db.save();
    runThread = new RunnerThread();
    runThread.start();
    System.gc();
    this.setLocationRelativeTo(worldEditorFrame);
    this.setVisible(true);
  }

  @Override
  public void windowActivated(WindowEvent arg0) {
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
    this.runThread = null;
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
  public void actionPerformed(ActionEvent e) {
  }
  
  public TargetType getTarget() {
    return target;
  }

  public void setTarget(TargetType target) {
    this.target = target;
  }

  private class RunnerThread extends Thread {
    @Override
    public void run() {
      try {
        if (target == TargetType.Device) {
          AdbManager.adbPush(G.fs("").file().getAbsolutePath(), "/sdcard/"+GameManager.ANDROID_GAME_DIRECTORY_NAME);
          AdbManager.stopApplication(GameManager.ANDROID_APP_PACKAGE);
          AdbManager.startApplication(GameManager.ANDROID_APP_PACKAGE+"/.MainActivity");
        } else {
          GameRunner runner = new GameRunner();
          runner.startProcess().waitFor();
        }
        
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
      
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          RunningGameConsoleFrame.this.setVisible(false);
          RunningGameConsoleFrame.this.dispose();
        }
      });
    }
  }
}
