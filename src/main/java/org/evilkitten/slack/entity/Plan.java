package org.evilkitten.slack.entity;

public enum Plan {
  NONE(""),
  STANDARD("std"),
  PLUS("plus");

  private String name;

  Plan(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Plan fromString(String name) {
    for (Plan plan: Plan.values()) {
      if (plan.getName().equalsIgnoreCase(name) || plan.name().equalsIgnoreCase(name)) {
        return plan;
      }
    }
    throw new IllegalArgumentException("No team plan called '" + name + "'");
  }
}
