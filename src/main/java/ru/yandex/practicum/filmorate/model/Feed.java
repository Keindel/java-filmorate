package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feed {
    // timestamp, user_id, eventType, operation,eventId, entity_id
    private int timestamp;
    private int user_id;
    private String eventType;
    private String operation;
    private int event_Id;
    private int entity_Id;
}
