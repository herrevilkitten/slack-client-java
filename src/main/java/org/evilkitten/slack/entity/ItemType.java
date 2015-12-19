package org.evilkitten.slack.entity;

import lombok.Getter;
import org.evilkitten.slack.response.rtm.mesage.PinnedItemEvent;

public enum ItemType {
  FILE("F"),
  CHANNEL_MESSAGE("C"),
  PRIVATE_GROUP_MESSAGE("G"),
  FILE_COMMENTS("Fc");

  @Getter
  private String shortName;

  ItemType(String shortName) {
    this.shortName = shortName;
  }

  public static ItemType fromString(String name) {
    for (ItemType itemType : values()) {
      if (itemType.toString().equalsIgnoreCase(name) || itemType.getShortName().equalsIgnoreCase(name)) {
        return itemType;
      }
    }
    throw new IllegalArgumentException(name + " is not a valid ItemType");
  }
}
