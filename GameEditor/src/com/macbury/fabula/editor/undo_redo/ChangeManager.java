package com.macbury.fabula.editor.undo_redo;

public class ChangeManager {
  private Node currentIndex = null;
  private Node parentNode = new Node();
  private ChangeManagerListener listener;

  /**
   * Creates a new ChangeManager object which is initially empty.
   */
  public ChangeManager(ChangeManagerListener listener){
    currentIndex = parentNode;
    this.listener = listener;
    this.listener.onChangeManagerChange(this);
  }

  /**
   * Clears all Changables contained in this manager.
   */
  public void clear(){
    parentNode   = new Node();
    currentIndex = parentNode;
    this.listener.onChangeManagerChange(this);
  }


  /**
   * Adds a Changeable to manage.
   * @param changeable 
   */
  public void addChangeable(Changeable changeable){
    Node node          = new Node(changeable);
    currentIndex.right = node;
    node.left          = currentIndex;
    currentIndex       = node;
    this.listener.onChangeManagerChange(this);
  }

  /**
   * Determines if an undo can be performed.
   * @return
   */
  public boolean canUndo(){
    return currentIndex != parentNode;
  }

  /**
   * Determines if a redo can be performed.
   * @return
   */
  public boolean canRedo(){
    return currentIndex.right != null;
  }

  /**
   * Undoes the Changeable at the current index. 
   * @throws IllegalStateException if canUndo returns false. 
   */
  public void undo(){
    //validate
    if ( !canUndo() ){
      throw new IllegalStateException("Cannot undo. Index is out of range.");
    }
    //undo
    currentIndex.changeable.undo();
    //set index
    moveLeft();
  }
  
  /**
   * Moves the internal pointer of the backed linked list to the left.
   * @throws IllegalStateException If the left index is null.
   */
  private void moveLeft(){
    if ( currentIndex.left == null ){
      throw new IllegalStateException("Internal index set to null.");
    }
    currentIndex = currentIndex.left;
    
    this.listener.onChangeManagerChange(this);
    System.gc();
  }

  /**
   * Moves the internal pointer of the backed linked list to the right.
   * @throws IllegalStateException If the right index is null.
   */
  private void moveRight(){
    if ( currentIndex.right == null ){
      throw new IllegalStateException("Internal index set to null.");
    }
    currentIndex = currentIndex.right;
    this.listener.onChangeManagerChange(this);
    System.gc();
  }

  /**
   * Redoes the Changable at the current index.
   * @throws IllegalStateException if canRedo returns false. 
   */
  public void redo(){
    //validate
    if ( !canRedo() ){
      throw new IllegalStateException("Cannot redo. Index is out of range.");
    }
    //reset index
    moveRight();
    //redo
    currentIndex.changeable.redo();
  }

  
  
  private class Node {
    private Node left  = null;
    private Node right = null;
    
    private final Changeable changeable;

    public Node(Changeable c){
      changeable = c;
    }

    public Node(){
      changeable = null;
    }
  }
}
