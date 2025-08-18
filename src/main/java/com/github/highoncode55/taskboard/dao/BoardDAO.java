package com.github.highoncode55.taskboard.dao;

import com.github.highoncode55.taskboard.model.Board;
import com.github.highoncode55.taskboard.model.Card;
import com.github.highoncode55.taskboard.model.Column;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {
    public Board create(Board board){
        String sql = "INSERT INTO boards (name) VALUES (?);";

        try {
            Connection conn = DatabaseConnection.getConnection();

            // Agora, o try-with-resources gerencia apenas o PreparedStatement
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, board.getName());
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            board.setId(generatedKeys.getLong(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar o board: " + e.getMessage());
        }
        return board;
    }

    public void update(Board board){
        String sql = "UPDATE boards SET name=? WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(2, board.getName());
                pstmt.setLong(1, board.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Erro ao atualizar o board: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void delete(long boardId) {
        String sql = "DELETE FROM boards WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, boardId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Erro ao deletar o board: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Board getFullBoard(long boardId) {
        Board board = this.getById(boardId);

        if (board == null) {
            return null;
        }

        ColumnDAO columnDAO = new ColumnDAO();
        CardDAO cardDAO = new CardDAO();
        try {
            List<Column> columns = columnDAO.getByBoardId(board.getId());

            for (Column column : columns) {
                List<Card> cards = cardDAO.getByColumnId(column.getId());
                column.setCards(cards); // Preenche a lista de cards dentro do objeto Coluna
            }

            // Adicionar a lista de colunas (já preenchidas com cards) ao objeto Board
            board.setColumns(columns);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return board;
        }

    public Board getById(long boardId) {
        String sql = "SELECT id, name FROM boards WHERE id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setLong(1, boardId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        String name = rs.getString("name");
                        return new Board(id, name);
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Board> getAll() {
        List<Board> boards = new ArrayList<>();
        String sql = "SELECT id, name FROM boards ORDER BY name;";
        try {
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String name = rs.getString("name");
                    Board board = new Board(id, name);
                    boards.add(board);
                }
                return boards;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}