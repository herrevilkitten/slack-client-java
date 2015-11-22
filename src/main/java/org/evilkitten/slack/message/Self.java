package org.evilkitten.slack.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Self {
    private String id;
    private String name;
    private Preferences prefs;
    private long created;

    @JsonProperty("manual_presence")
    private String manualPresence;

    @Data
    public static class Preferences {
        @JsonProperty("highlight_words")
        private String highlightWords;

        @JsonProperty("emoji_use")
        private String emojiUse;
    }
}
