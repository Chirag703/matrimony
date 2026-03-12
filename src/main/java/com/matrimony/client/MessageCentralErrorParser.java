package com.matrimony.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCentralErrorParser {

    private static final Logger log = LoggerFactory.getLogger(MessageCentralErrorParser.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T parse(String body, Class<T> clazz) {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (Exception e) {
            log.error("Failed to parse error response: {}", body, e);
            try {
                T defaultInstance = clazz.getDeclaredConstructor().newInstance();
                log.warn("Returning default instance of {} due to parsing failure", clazz.getName());
                return defaultInstance;
            } catch (Exception ex) {
                String errorMsg = String.format(
                        "Failed to create default instance of %s and could not parse error response",
                        clazz.getName());
                log.error(errorMsg, ex);
                throw new IllegalStateException(errorMsg, ex);
            }
        }
    }
}
