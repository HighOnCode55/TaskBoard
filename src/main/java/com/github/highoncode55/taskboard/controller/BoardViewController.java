package com.github.highoncode55.taskboard.controller;

import com.github.highoncode55.taskboard.dao.BoardDAO;
import com.github.highoncode55.taskboard.model.Board;
import com.github.highoncode55.taskboard.model.Card;
import com.github.highoncode55.taskboard.model.Column;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Comparator;

public class BoardViewController {
    private BoardDAO boardDAO;
    @FXML
    private HBox boardHBox;

    @FXML
    private void initialize(){
        this.boardDAO = new BoardDAO();
    }

    // Called by MainViewController after loading BoardView.fxml
    public void loadBoardButton(long boardId){
        loadColumns(boardId);
    }

    // Populates the boardHBox with the columns (left to right)
    private void loadColumns(long boardId){
        boardHBox.getChildren().clear();

        Board board = boardDAO.getFullBoard(boardId);
        if (board == null) {
            return;
        }

        // Ensure columns are ordered by the 'order' field
        board.getColumns().sort(Comparator.comparingInt(Column::getOrder));

        for (Column column : board.getColumns()){
            Node columnNode = createColumnNode(column);
            boardHBox.getChildren().add(columnNode);
        }
    }

    // Creates a VBox for a single column and fills it with its cards (top to bottom)
    private Node createColumnNode(Column column) {
        VBox colBox = new VBox();
        colBox.setSpacing(8);
        colBox.setStyle("-fx-background-color: #161b22; -fx-padding: 15; -fx-border-color: -color-border-muted; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        colBox.setPrefWidth(220);

        // Header with column name (icons can be added later)
        Label header = new Label(column.getName());
        header.setStyle("-fx-font-weight: bold;");

        VBox cardsBox = new VBox();
        cardsBox.setSpacing(8);
        VBox.setVgrow(cardsBox, Priority.ALWAYS);

        // Fill cards ordered
        loadCards(cardsBox, column);

        colBox.getChildren().addAll(header, cardsBox);
        return colBox;
    }

    private void loadCards(VBox cardsBox, Column column){
        cardsBox.getChildren().clear();

        // Ensure cards are ordered by the 'order' field
        column.getCards().sort(Comparator.comparingInt(Card::getOrder));

        for (Card card : column.getCards()){
            Button cardNode = new Button(card.getTitle());
            cardNode.setMaxWidth(Double.MAX_VALUE);
            cardNode.setWrapText(true);
            cardNode.getStyleClass().add("card-item");
            // Visual hint if blocked
            if (Boolean.TRUE.equals(card.getIsBlocked())) {
                cardNode.setStyle("-fx-background-color: #3a1f1f;");
            }
            cardsBox.getChildren().add(cardNode);
        }
    }
}
