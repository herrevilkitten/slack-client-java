package org.evilkitten.slack.entity;

import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
public class Response {
  private boolean ok;
  private Self self;
  private Team team;
  private List<Bot> bots = new ArrayList<>();
  private URI url;
  private String error;
}
