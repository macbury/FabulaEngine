package com.macbury.fabula.editor.inspector;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.LookAndFeelTweaks;

public abstract class SpinnerPropertyEditor extends AbstractPropertyEditor {
  @SuppressWarnings("rawtypes")
  private final Class type;
  private Object lastGoodValue;

  @SuppressWarnings("rawtypes")
  public SpinnerPropertyEditor(Class type) {
    if (!Number.class.isAssignableFrom(type)) {
      throw new IllegalArgumentException("type must be a subclass of Number");
    }

    editor = new JSpinner(getModel());
    this.type = type;
    ((JSpinner)editor).setValue(getDefaultValue());
    ((JSpinner)editor).setBorder(LookAndFeelTweaks.EMPTY_BORDER);

  }

  public abstract SpinnerModel getModel();

  @Override
  public Object getValue() {
    lastGoodValue = ((JSpinner)editor).getValue();
    return lastGoodValue;
  }

  @Override
  public void setValue(Object value) {
    if (value instanceof Number) {
      ((JSpinner)editor).setValue(value);
    } else {
      ((JSpinner)editor).setValue(getDefaultValue());
    }
    lastGoodValue = value;
  }

  @SuppressWarnings("unchecked")
  private Object getDefaultValue() {
    try {
      return type.getConstructor(new Class[] {String.class}).newInstance(
          new Object[] {"0"});
    } catch (Exception e) {
      // will not happen
      throw new RuntimeException(e);
    }
  }
}
