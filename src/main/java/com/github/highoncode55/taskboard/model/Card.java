package com.github.highoncode55.taskboard.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class Card {
    private final long id;
    private String title;
    private String description;
    private long columnId;
    private int order;
}
