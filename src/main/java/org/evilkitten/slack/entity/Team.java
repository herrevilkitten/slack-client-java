package org.evilkitten.slack.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Team {
    private String id;
    private String name;

    @JsonProperty("email_domain")
    private String emailDomain;

    private String domain;

    @JsonProperty("msg_edit_window_mins")
    private long messageEditWindowInMinutes;
}
