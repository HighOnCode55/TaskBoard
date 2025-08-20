package com.github.highoncode55.taskboard.dao;

import com.github.highoncode55.taskboard.model.Board;
import com.github.highoncode55.taskboard.model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CardDAO {
    public void create(Card card){

    }
    public void update(Card card){

    }
    public void delete(long cardId){

    }

    public List<Card> getByColumnId(long columnId){
        String sql = "SELECT id, title, description, column_id, `order`, is_blocked FROM cards WHERE column_id=? ORDER BY `order` ASC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, columnId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    java.util.ArrayList<Card> cards = new java.util.ArrayList<>();
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        String title = rs.getString("title");
                        String description = rs.getString("description");
                        long colId = rs.getLong("column_id");
                        int order = rs.getInt("order");
                        Boolean isBlocked = rs.getBoolean("is_blocked");
                        cards.add(new Card(id, title, description, colId, order, isBlocked));
                    }
                    return cards;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Card getById(long cardId){
        String sql = "SELECT * FROM cards WHERE id=?";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, cardId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        String title = rs.getString("title");
                        String description = rs.getString("description");
                        long columnId = rs.getLong("column_id");
                        int order = rs.getInt("order");
                        Boolean isBlocked = rs.getBoolean("is_blocked");
                        return new Card(id, title, description, columnId, order, isBlocked);
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
