package org.evilkitten.slack.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URL;
import java.util.Map;

@Data
public class Bot {
    private String id;
    private boolean deleted;
    private String name;
    private Map<String, URL> icons;

    @Data
    public static class Icon {
        @JsonProperty("image_48")
        private URL image48;
    }
}
