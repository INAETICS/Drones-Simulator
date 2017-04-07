package org.inaetics.dronesimulator.common;

public class Tuple<Left, Right> { 
  private final Left left; 
  private final Right right; 
  
  public Tuple(Left left, Right right) { 
    this.left = left; 
    this.right = right; 
  }
  
  public Left getLeft() {
    return this.left;
  }

  public Right getRight() {
    return this.right;
  }
} 