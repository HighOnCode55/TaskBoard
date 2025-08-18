package com.github.highoncode55.taskboard.controller;

import com.github.highoncode55.taskboard.dao.BoardDAO;
import com.github.highoncode55.taskboard.model.Board;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.TilePane;

import java.util.List;
import java.util.Optional;

public class MainViewController {
    private BoardDAO boardDAO;
    
    @FXML
    private TilePane boardsTilePane;

    @FXML
    private Button newBoardButton;

    private void loadBoards(){
        boardsTilePane.getChildren().clear();
        List<Board> allBoards = boardDAO.getAll();
        for (Board board : allBoards){
            Node tile = populateTile(board);
            boardsTilePane.getChildren().add(tile);
        }
    }

    public void handleNewBoardButton(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Board");
        dialog.setHeaderText("Create new Board");
        dialog.setContentText("Name of the new Board:");
        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(name -> {
            Board board = new Board(name);
            boardDAO.create(board);
            loadBoards();
        });
    }

    private Node populateTile(Board board) {
        Button tileButton = new Button(board.getName());
        tileButton.setPrefSize(167, 112);
        tileButton.getStyleClass().add("board-tile");
        tileButton.setOnAction(event -> {
            System.out.println("Clicou no board com ID: " + board.getId());
            // Aqui virá a lógica para abrir a tela do board selecionado.
            // Por exemplo: abrirNovaJanela(board);
        });
        return tileButton;
    }

    private void initialize(){
        this.boardDAO = new BoardDAO();
        loadBoards();

    }
}
