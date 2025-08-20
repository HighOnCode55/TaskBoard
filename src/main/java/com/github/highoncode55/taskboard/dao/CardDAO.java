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
        String sql = "INSERT INTO cards (title, description, column_id, `order`) VALUES (?, ?, ?, ?);";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, card.getTitle());
                pstmt.setString(2, card.getDescription());
                pstmt.setLong(3, card.getColumnId());
                pstmt.setInt(4, card.getOrder());
                pstmt.executeUpdate();
                // We could read generated keys here, but Card fields are final; skipping assignment.
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void update(Card card){
        String sql = "UPDATE cards SET title = ?, description = ?, column_id = ?, `order` = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, card.getTitle());
                pstmt.setString(2, card.getDescription());
                pstmt.setLong(3, card.getColumnId());
                pstmt.setInt(4, card.getOrder());
                pstmt.setLong(5, card.getId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // Minimal helper to update only column and order during drag and drop
    public void updateOrderAndColumn(long cardId, long newColumnId, int newOrder) {
        String sql = "UPDATE cards SET column_id = ?, `order` = ? WHERE id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, newColumnId);
                pstmt.setInt(2, newOrder);
                pstmt.setLong(3, cardId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void delete(long cardId){

    }

    public List<Card> getByColumnId(long columnId){
        String sql = "SELECT id, title, description, column_id, `order` FROM cards WHERE column_id=? ORDER BY `order` ASC";
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
                        cards.add(new Card(id, title, description, colId, order));
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
                        return new Card(id, title, description, columnId, order);
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
