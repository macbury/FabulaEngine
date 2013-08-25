package com.macbury.fabula.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;

public class ScriptFunctions{
  public void pause() {
    Context cx = Context.enter();
    try {
        ContinuationPending pending = cx.captureContinuation();
        pending.setApplicationState(1);
        throw pending;
    } finally {
        Context.exit();
    }
  }
}
