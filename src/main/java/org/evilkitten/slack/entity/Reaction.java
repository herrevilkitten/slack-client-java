package org.evilkitten.slack.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Reaction {
  private String name;
  private int count;
  private List<String> userIds = new ArrayList<>();
}
