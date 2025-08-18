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
        return null;
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
                        String title = rs.getString("name");
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
