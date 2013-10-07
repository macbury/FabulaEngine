package com.macbury.fabula.game_objects.components;

import java.util.ArrayList;

import com.artemis.Component;
import com.macbury.fabula.game_objects.events.ConditionPage;

public class EventComponent extends Component {
  private ArrayList<ConditionPage> pages;
  private String name;
  public EventComponent() {
    this.pages = new ArrayList<ConditionPage>();
  }
}
