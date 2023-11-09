package com.aishoppingbuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGPTResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("created")
    private Long created;

    @JsonProperty("model")
    private String model;

    @JsonProperty("usage")
    private Map<String, Object> usage;

    @JsonProperty("choices")
    private List<ChatGPTChoice> choices;

    @Data
    public static class ChatGPTChoice {
        @JsonProperty("message")
        private ChatGPTMessage message;

        @JsonProperty("finish_reason")
        private String finishReason;

        @JsonProperty("index")
        private Integer index;
    }

    @Data
    public static class ChatGPTMessage {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;
    }
}