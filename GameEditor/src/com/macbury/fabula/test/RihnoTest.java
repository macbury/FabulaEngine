package com.macbury.fabula.test;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.macbury.fabula.js.ScriptFunctions;

public class RihnoTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    Context cx = ContextFactory.getGlobal().enterContext();
    cx.setOptimizationLevel(-1);
    
    Scriptable objectScope = cx.initStandardObjects();
    objectScope.put("scriptfunctions", objectScope, new ScriptFunctions());
    cx.evaluateString(objectScope, " for(var fn in scriptfunctions) { if(typeof scriptfunctions[fn] === 'function') {this[fn] = (function() {var method = scriptfunctions[fn];return function() {return method.apply(scriptfunctions,arguments);};})();}};", "function transferrer", 1, null);
    FooJsTest fjt          = new FooJsTest();
    
    ScriptableObject.putProperty(objectScope, "Foo", fjt);
    cx.evaluateString(objectScope, "Foo.add();", "ROBOT", 2, null);
    cx.evaluateString(objectScope, "Foo.add();", "ROBOT", 2, null);
    cx.evaluateString(objectScope, "function onAction(){ pause(); }", "ROBOT", 2, null);
    
    Function f = (Function)(objectScope.get("onAction", objectScope));
    cx.setOptimizationLevel(-1);
    try {
        cx.callFunctionWithContinuations(f, objectScope, new Object[1]);
    } catch (ContinuationPending pending) {
      System.out.println("The script was paused!");
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println("Resuming the script...");
      int saved = (Integer)pending.getApplicationState();
      cx.resumeContinuation(pending.getContinuation(), objectScope, saved);
    }
    
    System.err.println(cx.evaluateString(objectScope, "Foo.get();", "ROBOT", 2, null));
    System.err.println(fjt.get());
    
    Context.exit();
  }
  
  
}
