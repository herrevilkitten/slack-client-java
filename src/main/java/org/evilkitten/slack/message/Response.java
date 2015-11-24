package org.evilkitten.slack.message;

import lombok.Data;
import org.evilkitten.slack.entity.Bot;
import org.evilkitten.slack.entity.Self;
import org.evilkitten.slack.entity.Team;

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
