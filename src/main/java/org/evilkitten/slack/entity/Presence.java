package org.evilkitten.slack.entity;

public enum Presence {
  AWAY("away");

  private String name;

  Presence(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
