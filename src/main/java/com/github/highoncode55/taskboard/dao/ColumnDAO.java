package com.github.highoncode55.taskboard.dao;

import com.github.highoncode55.taskboard.model.Column;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColumnDAO {
    public void create(Column column){
        String sql = "INSERT INTO columns (name, `order`, type, board_id) VALUES (?, ?, ?, ?);";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, column.getName());
                pstmt.setInt(2, column.getOrder());
                pstmt.setString(3, column.getType());
                pstmt.setLong(4, column.getBoardId());
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    try (java.sql.ResultSet keys = pstmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            column.setId(keys.getLong(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void update(Column column){
    }
    public void delete(long columnId){
    }
    public Column getById(long columnId){
        String sql = "SELECT id, name, `order`, type, board_id FROM columns WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, columnId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Column c = new Column();
                        c.setId(rs.getLong("id"));
                        c.setName(rs.getString("name"));
                        c.setOrder(rs.getInt("order"));
                        c.setType(rs.getString("type"));
                        c.setBoardId(rs.getLong("board_id"));
                        return c;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Column> getByBoardId(long boardId){
        String sql = "SELECT id, name, `order`, type, board_id FROM columns WHERE board_id=? ORDER BY `order` ASC";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, boardId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    List<Column> list = new ArrayList<>();
                    while (rs.next()) {
                        Column c = new Column();
                        c.setId(rs.getLong("id"));
                        c.setName(rs.getString("name"));
                        c.setOrder(rs.getInt("order"));
                        c.setType(rs.getString("type"));
                        c.setBoardId(rs.getLong("board_id"));
                        list.add(c);
                    }
                    return list;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Column getAll(){
        return null;
    }
}
