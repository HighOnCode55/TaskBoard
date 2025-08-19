package com.github.highoncode55.taskboard.controller;

import com.github.highoncode55.taskboard.dao.BoardDAO;
import com.github.highoncode55.taskboard.model.Board;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.List;

public class BoardViewController {
    private BoardDAO boardDAO;
    @FXML
    private HBox boardHBox;
    @FXML
    private void initialize(){
        this.boardDAO = new BoardDAO();
    }

    public void loadBoardButton(long boardId){
        populateBoard(boardId);
    }

    private void loadBoards(){
        boardsPane.getChildren().clear();
        List<Board> allBoards = boardDAO.getAll();
        for (Board board : allBoards){
            Node tile = populateTile(board);
            boardsPane.getChildren().add(tile);
        }
    }

    private Node populateBoard(long boardId) {
        Button tileButton = new Button(board.getName());
        tileButton.setPrefSize(167, 112);
        tileButton.getStyleClass().add("board-tile");
        tileButton.setOnAction(event -> {
            System.out.println("Clicou no board com ID: " + board.getId());
            // Aqui virá a lógica para abrir a tela do board selecionado.
            // Por exemplo: abrirNovaJanela(board);
            handleLoadBoardButton(event, board.getId());
        });
        return tileButton;
    }

}
