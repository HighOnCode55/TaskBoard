package com.github.highoncode55.taskboard.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter @Setter
public class Column {
    private String name;
    private long id;
    private long boardId;
    private String type;
    private int order;
    private List<Card> cards = new ArrayList<>();
}
