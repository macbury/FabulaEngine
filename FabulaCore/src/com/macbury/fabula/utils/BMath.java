package com.macbury.fabula.utils;

public class BMath {
  public static float lerp(float a, float b, float t) {
    if (t < 0.0f)
      return a;
    if (t > 1.0f)
      return b;
    
    return a + t * (b - a);
  }
}
