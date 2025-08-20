package com.github.highoncode55.taskboard.controller;

import com.github.highoncode55.taskboard.dao.BoardDAO;
import com.github.highoncode55.taskboard.dao.ColumnDAO;
import com.github.highoncode55.taskboard.model.Board;
import com.github.highoncode55.taskboard.model.Card;
import com.github.highoncode55.taskboard.model.Column;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

public class BoardViewController {
    private BoardDAO boardDAO;
    private ColumnDAO columnDAO;
    private long currentBoardId;

    @FXML
    private Button BoardNewColumn;

    @FXML
    private Button boardMenuButton;
    
    @FXML
    private HBox boardHBox;

    @FXML
    private void initialize(){
        this.boardDAO = new BoardDAO();
        this.columnDAO = new ColumnDAO();
    }

    // Called by MainViewController after loading BoardView.fxml
    public void loadBoardButton(long boardId){
        this.currentBoardId = boardId;
        loadColumns(boardId);
    }

    // Handler: add new column
    @FXML
    private void handleBoardNewColumnButton(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Column");
        dialog.setContentText("Name the new Column:");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/atlantafx/base/theme/primer-dark.css").toExternalForm()
        );
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            // Determine next order
            int nextOrder = 0;
            Board board = boardDAO.getFullBoard(currentBoardId);
            if (board != null && board.getColumns() != null && !board.getColumns().isEmpty()) {
                nextOrder = board.getColumns().stream().mapToInt(Column::getOrder).max().orElse(-1) + 1;
            }
            Column newCol = new Column();
            newCol.setName(name);
            newCol.setBoardId(currentBoardId);
            newCol.setType("DEFAULT");
            newCol.setOrder(nextOrder);
            columnDAO.create(newCol);
            loadColumns(currentBoardId);
        });
    }

    // Handler: go back to main view
    @FXML
    private void handleBoardMenuButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/highoncode55/taskboard/views/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/atlantafx/base/theme/primer-dark.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
