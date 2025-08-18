package com.github.highoncode55.taskboard.model;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Board {
    private long id;
    private String name;
    private List<Column> columns = new ArrayList<>();

    public Board(long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Board(String name){
        this.name = name;
    }
}
