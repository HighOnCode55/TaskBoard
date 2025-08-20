package com.github.highoncode55.taskboard.controller;

import com.github.highoncode55.taskboard.dao.BoardDAO;
import com.github.highoncode55.taskboard.dao.ColumnDAO;
import com.github.highoncode55.taskboard.dao.CardDAO;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

public class BoardViewController {
    private BoardDAO boardDAO;
    private ColumnDAO columnDAO;
    private CardDAO cardDAO;
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
        this.cardDAO = new CardDAO();
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
            // Default new columns are BLOCKED
            newCol.setType("BLOCKED");
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

        // Top control grid: lock (left), title (center), gear (right)
        GridPane topGrid = new GridPane();
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        ColumnConstraints c3 = new ColumnConstraints();
        c1.setPercentWidth(20);
        c2.setPercentWidth(60);
        c3.setPercentWidth(20);
        topGrid.getColumnConstraints().addAll(c1, c2, c3);

        // Determine blocked state from column type
        boolean isBlocked = "BLOCKED".equalsIgnoreCase(column.getType());
        org.kordamp.ikonli.javafx.FontIcon lockIcon = new org.kordamp.ikonli.javafx.FontIcon(isBlocked ? "codicon-lock" : "codicon-unlock");
        lockIcon.setIconSize(14);
        org.kordamp.ikonli.javafx.FontIcon gearIcon = new org.kordamp.ikonli.javafx.FontIcon("codicon-gear");
        gearIcon.setIconSize(16);

        // Title in the center cell
        Label header = new Label(column.getName());
        header.setStyle("-fx-font-weight: bold;");

        GridPane.setHalignment(lockIcon, HPos.LEFT);
        GridPane.setHalignment(header, HPos.CENTER);
        GridPane.setHalignment(gearIcon, HPos.RIGHT);
        topGrid.add(lockIcon, 0, 0);
        topGrid.add(header, 1, 0);
        topGrid.add(gearIcon, 2, 0);

        // Actions: toggle blocked and show gear menu
        lockIcon.setOnMouseClicked(e -> toggleColumnBlocked(column));
        gearIcon.setOnMouseClicked(e -> showColumnMenu(gearIcon, column, e));

        VBox cardsBox = new VBox();
        cardsBox.setSpacing(8);
        VBox.setVgrow(cardsBox, Priority.ALWAYS);

        // Fill cards ordered
        loadCards(cardsBox, column);

        // Add "Add Card" button at the end
        Button addCardBtn = new Button();
        // Use codicon-add icon
        org.kordamp.ikonli.javafx.FontIcon addIcon = new org.kordamp.ikonli.javafx.FontIcon("codicon-add");
        addIcon.setIconSize(16);
        addCardBtn.setGraphic(addIcon);
        addCardBtn.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
        // Make it circular and accent-styled
        addCardBtn.getStyleClass().add("accent");
        addCardBtn.setPrefSize(32, 32);
        addCardBtn.setMinSize(32, 32);
        addCardBtn.setMaxSize(32, 32);
        addCardBtn.setStyle("-fx-background-radius: 16; -fx-padding: 0;");
        addCardBtn.setOnAction(e -> handleAddCard(column));
        HBox addBtnBox = new HBox(addCardBtn);
        addBtnBox.setAlignment(Pos.CENTER);

        colBox.getChildren().addAll(topGrid, cardsBox, addBtnBox);
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

    // Toggle the blocked state using the 'type' field (BLOCKED/DEFAULT)
    private void toggleColumnBlocked(Column column) {
        boolean isBlocked = "BLOCKED".equalsIgnoreCase(column.getType());
        column.setType(isBlocked ? "DEFAULT" : "BLOCKED");
        columnDAO.update(column);
        loadColumns(currentBoardId);
    }

    // Show context menu (Rename / Delete) anchored to the gear icon
    private void showColumnMenu(Node anchor, Column column, MouseEvent e) {
        ContextMenu menu = new ContextMenu();
        MenuItem rename = new MenuItem("Rename");
        MenuItem delete = new MenuItem("Delete");
        rename.setOnAction(a -> {
            TextInputDialog dialog = new TextInputDialog(column.getName());
            dialog.setTitle("Rename Column");
            dialog.setContentText("New name:");
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/atlantafx/base/theme/primer-dark.css").toExternalForm()
            );
            Optional<String> res = dialog.showAndWait();
            res.ifPresent(newName -> {
                String trimmed = newName != null ? newName.trim() : "";
                if (!trimmed.isEmpty()) {
                    column.setName(trimmed);
                    columnDAO.update(column);
                    loadColumns(currentBoardId);
                }
            });
        });
        delete.setOnAction(a -> {
            columnDAO.delete(column.getId());
            loadColumns(currentBoardId);
        });
        menu.getItems().addAll(rename, delete);
        menu.show(anchor, e.getScreenX(), e.getScreenY());
    }

    // Prompt and create a new card in the given column
    private void handleAddCard(Column column) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Card");
        dialog.setContentText("Title for the new card:");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/atlantafx/base/theme/primer-dark.css").toExternalForm()
        );
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> {
            String trimmed = title != null ? title.trim() : "";
            if (trimmed.isEmpty()) {
                return; // ignore empty
            }
            // Determine next order in this column
            int nextOrder = 0;
            if (column.getCards() != null && !column.getCards().isEmpty()) {
                nextOrder = column.getCards().stream().mapToInt(Card::getOrder).max().orElse(-1) + 1;
            }
            // Create the card (id unused for insert)
            Card newCard = new Card(0L, trimmed, "", column.getId(), nextOrder, false);
            cardDAO.create(newCard);
            // Reload columns to reflect the new card
            loadColumns(currentBoardId);
        });
    }
}
