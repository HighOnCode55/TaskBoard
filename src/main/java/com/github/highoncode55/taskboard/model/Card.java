package com.github.highoncode55.taskboard.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class Card {
    private final long id;
    private final String title;
    private final String description;
    private final long columnId;
    private final int order;
}
