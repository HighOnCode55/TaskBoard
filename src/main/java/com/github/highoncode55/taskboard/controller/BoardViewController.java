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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BoardViewController {
    private static final DataFormat DF_COLUMN = new DataFormat("taskboard/column");
    private static final DataFormat DF_CARD = new DataFormat("taskboard/card");
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

    public void loadBoardButton(long boardId){
        this.currentBoardId = boardId;
        loadColumns(boardId);
    }

    @FXML
    private void handleBoardNewColumnButton(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Name the new Column:");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/github/highoncode55/taskboard/css/primer-dark.css").toExternalForm()
        );
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            int nextOrder = 0;
            Board board = boardDAO.getFullBoard(currentBoardId);
            if (board != null && board.getColumns() != null && !board.getColumns().isEmpty()) {
                nextOrder = board.getColumns().stream().mapToInt(Column::getOrder).max().orElse(-1) + 1;
            }
            Column newCol = new Column();
            newCol.setName(name);
            newCol.setBoardId(currentBoardId);
            newCol.setBlocked(false); // Default to unblocked
            newCol.setOrder(nextOrder);
            columnDAO.create(newCol);
            loadColumns(currentBoardId);
        });
    }

    @FXML
    private void handleBoardMenuButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/highoncode55/taskboard/views/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/github/highoncode55/taskboard/css/primer-dark.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadColumns(long boardId){
        boardHBox.getChildren().clear();
        Board board = boardDAO.getFullBoard(boardId);
        if (board == null) {
            return;
        }
        board.getColumns().sort(Comparator.comparingInt(Column::getOrder));
        for (Column column : board.getColumns()) {
            Node columnNode = createColumnNode(column);
            boardHBox.getChildren().add(columnNode);
        }
    }

    private Node createColumnNode(Column column) {
        VBox colBox = new VBox();
        colBox.setSpacing(8);
        colBox.setStyle("-fx-background-color: #161b22; -fx-padding: 15; -fx-border-color: -color-border-muted; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        colBox.setPrefWidth(220);
        colBox.setUserData(column.getId());

        // Drag and Drop for Columns
        colBox.setOnDragDetected(event -> {
            if (!column.isBlocked()) { // Only non-blocked columns can be dragged
                Dragboard db = colBox.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(colBox.snapshot(null, null));
                ClipboardContent content = new ClipboardContent();
                content.put(DF_COLUMN, column.getId());
                db.setContent(content);
                event.consume();
            }
        });

        colBox.setOnDragOver(event -> {
            if (event.getGestureSource() != colBox && event.getDragboard().hasContent(DF_COLUMN)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        colBox.setOnDragEntered(event -> {
            if (event.getGestureSource() != colBox && event.getDragboard().hasContent(DF_COLUMN)) {
                colBox.setStyle("-fx-background-color: #2d333b; -fx-padding: 15; -fx-border-color: -color-accent-emphasis; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
            }
        });

        colBox.setOnDragExited(event -> {
            colBox.setStyle("-fx-background-color: #161b22; -fx-padding: 15; -fx-border-color: -color-border-muted; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        });

        colBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasContent(DF_COLUMN)) {
                long draggedColumnId = (Long) db.getContent(DF_COLUMN);
                if (draggedColumnId != column.getId()) {
                    reorderColumns(draggedColumnId, column.getOrder());
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        colBox.setOnDragDone(DragEvent::consume);

        GridPane topGrid = new GridPane();
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(20);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(60);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(20);
        topGrid.getColumnConstraints().addAll(c1, c2, c3);

        org.kordamp.ikonli.javafx.FontIcon lockIcon = new org.kordamp.ikonli.javafx.FontIcon(column.isBlocked() ? "codicon-lock" : "codicon-unlock");
        lockIcon.setIconSize(14);
        org.kordamp.ikonli.javafx.FontIcon gearIcon = new org.kordamp.ikonli.javafx.FontIcon("codicon-gear");
        gearIcon.setIconSize(16);

        Label header = new Label(column.getName());
        header.setStyle("-fx-font-weight: bold;");

        GridPane.setHalignment(lockIcon, HPos.LEFT);
        GridPane.setHalignment(header, HPos.CENTER);
        GridPane.setHalignment(gearIcon, HPos.RIGHT);
        topGrid.add(lockIcon, 0, 0);
        topGrid.add(header, 1, 0);
        topGrid.add(gearIcon, 2, 0);

        lockIcon.setOnMouseClicked(e -> toggleColumnBlocked(column));
        gearIcon.setOnMouseClicked(e -> showColumnMenu(gearIcon, column, e));

        VBox cardsBox = new VBox();
        cardsBox.setSpacing(8);
        VBox.setVgrow(cardsBox, Priority.ALWAYS);
        cardsBox.setUserData(column.getId());

        loadCards(cardsBox, column);

        Button addCardBtn = new Button();
        org.kordamp.ikonli.javafx.FontIcon addIcon = new org.kordamp.ikonli.javafx.FontIcon("codicon-add");
        addIcon.setIconSize(16);
        addCardBtn.setGraphic(addIcon);
        addCardBtn.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
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

        // Drag and drop for dropping cards into a column
        cardsBox.setOnDragOver(event -> {
            if (event.getGestureSource() != cardsBox && event.getDragboard().hasContent(DF_CARD)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cardsBox.setOnDragEntered(event -> {
            if (event.getGestureSource() != cardsBox && event.getDragboard().hasContent(DF_CARD)) {
                cardsBox.setStyle("-fx-background-color: #2d333b;");
            }
        });

        cardsBox.setOnDragExited(event -> {
            cardsBox.setStyle("");
        });

        cardsBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasContent(DF_CARD)) {
                long draggedCardId = (Long) db.getContent(DF_CARD);
                double dropY = event.getY();
                int newIndex = 0;
                for (Node node : cardsBox.getChildren()) {
                    if (node instanceof Button && node.getUserData() != null) {
                        if (dropY > node.getBoundsInParent().getMinY() + node.getBoundsInParent().getHeight() / 2) {
                            newIndex++;
                        } else {
                            break;
                        }
                    }
                }
                moveCard(draggedCardId, (long) cardsBox.getUserData(), newIndex);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        column.getCards().sort(Comparator.comparingInt(Card::getOrder));

        for (Card card : column.getCards()){
            Button cardNode = new Button(card.getTitle());
            cardNode.setMaxWidth(Double.MAX_VALUE);
            cardNode.setWrapText(true);
            cardNode.getStyleClass().add("card-item");
            cardNode.setUserData(card.getId());

            cardNode.setOnAction(event -> showCardDetailsDialog(card));

            // Drag and Drop for individual cards
            cardNode.setOnDragDetected(event -> {
                Dragboard db = cardNode.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(cardNode.snapshot(null, null));
                ClipboardContent content = new ClipboardContent();
                content.put(DF_CARD, card.getId());
                db.setContent(content);
                event.consume();
            });

            cardNode.setOnDragDone(DragEvent::consume);
            cardsBox.getChildren().add(cardNode);
        }
    }

    private void toggleColumnBlocked(Column column) {
        column.setBlocked(!column.isBlocked());
        columnDAO.update(column);
        loadColumns(currentBoardId);
    }

    private void showColumnMenu(Node anchor, Column column, MouseEvent e) {
        ContextMenu menu = new ContextMenu();
        MenuItem rename = new MenuItem("Rename");
        MenuItem delete = new MenuItem("Delete");
        rename.setOnAction(a -> {
            TextInputDialog dialog = new TextInputDialog(column.getName());
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("New name:");
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/com/github/highoncode55/taskboard/css/primer-dark.css").toExternalForm()
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

        // A new scene is created for the menu, so we need to apply the stylesheet to it
        menu.setOnShown(event -> {
            String stylesheet = getClass().getResource("/com/github/highoncode55/taskboard/css/primer-dark.css").toExternalForm();
            if (menu.getScene() != null) {
                menu.getScene().getStylesheets().add(stylesheet);
            }
        });

        menu.show(anchor, e.getScreenX(), e.getScreenY());
    }

    private void handleAddCard(Column column) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("New Card");
        dialog.setHeaderText("Create a new card");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/github/highoncode55/taskboard/css/primer-dark.css").toExternalForm()
        );

        // Set the button types.
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the title and description labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.getStyleClass().add("description-area");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable create button depending on whether a title was entered.
        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue.trim().isEmpty());
        });

        // Convert the result to a title-description pair when the create button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Pair<>(titleField.getText(), descriptionArea.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(titleDescription -> {
            String title = titleDescription.getKey();
            String description = titleDescription.getValue();
            String trimmedTitle = title != null ? title.trim() : "";
            if (trimmedTitle.isEmpty()) {
                return;
            }
            int nextOrder = 0;
            if (column.getCards() != null && !column.getCards().isEmpty()) {
                nextOrder = column.getCards().stream().mapToInt(Card::getOrder).max().orElse(-1) + 1;
            }
            Card newCard = new Card(0L, trimmedTitle, description, column.getId(), nextOrder);
            cardDAO.create(newCard);
            loadColumns(currentBoardId);
        });
    }

    private void showCardDetailsDialog(Card card) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Card Details");
        alert.setHeaderText(card.getTitle());

        TextArea descriptionArea = new TextArea(card.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.getStyleClass().add("description-area");
        alert.getDialogPane().setContent(descriptionArea);

        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/github/highoncode55/taskboard/css/primer-dark.css").toExternalForm()
        );

        ButtonType editButtonType = new ButtonType("Edit");
        alert.getButtonTypes().setAll(editButtonType, ButtonType.CLOSE);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == editButtonType) {
            showEditCardDialog(card);
        }
    }

    private void showEditCardDialog(Card card) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Card");
        dialog.setHeaderText("Edit card title and description");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/github/highoncode55/taskboard/css/primer-dark.css").toExternalForm()
        );

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(card.getTitle());
        titleField.setPromptText("Title");
        TextArea descriptionArea = new TextArea(card.getDescription());
        descriptionArea.setPromptText("Description");
        descriptionArea.getStyleClass().add("description-area");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(titleField.getText().trim().isEmpty());

        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Pair<>(titleField.getText(), descriptionArea.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(titleDescription -> {
            String title = titleDescription.getKey();
            String description = titleDescription.getValue();
            String trimmedTitle = title != null ? title.trim() : "";
            if (trimmedTitle.isEmpty()) {
                return;
            }
            card.setTitle(trimmedTitle);
            card.setDescription(description);
            cardDAO.update(card);
            loadColumns(currentBoardId);
        });
    }

    private void reorderColumns(long draggedColumnId, int targetOrder) {
        Board board = boardDAO.getFullBoard(currentBoardId);
        if (board == null) return;

        Column draggedColumn = board.getColumns().stream()
                .filter(c -> c.getId() == draggedColumnId)
                .findFirst().orElse(null);

        if (draggedColumn == null) return;

        int originalOrder = draggedColumn.getOrder();

        if (originalOrder < targetOrder) {
            board.getColumns().stream()
                    .filter(c -> c.getId() != draggedColumnId && c.getOrder() > originalOrder && c.getOrder() <= targetOrder)
                    .forEach(c -> {
                        c.setOrder(c.getOrder() - 1);
                        columnDAO.update(c);
                    });
        } else {
            board.getColumns().stream()
                    .filter(c -> c.getId() != draggedColumnId && c.getOrder() >= targetOrder && c.getOrder() < originalOrder)
                    .forEach(c -> {
                        c.setOrder(c.getOrder() + 1);
                        columnDAO.update(c);
                    });
        }

        draggedColumn.setOrder(targetOrder);
        columnDAO.update(draggedColumn);

        loadColumns(currentBoardId);
    }

    private void moveCard(long cardId, long newColumnId, int newPosition) {
        Card cardToMove = cardDAO.getById(cardId);
        if (cardToMove == null) return;

        long oldColumnId = cardToMove.getColumnId();
        int oldPosition = cardToMove.getOrder();

        if (oldColumnId == newColumnId && oldPosition == newPosition) {
            return;
        }

        Board board = boardDAO.getFullBoard(currentBoardId);
        if (board == null) return;

        Column oldColumn = board.getColumns().stream().filter(c -> c.getId() == oldColumnId).findFirst().orElse(null);
        Column newColumn = board.getColumns().stream().filter(c -> c.getId() == newColumnId).findFirst().orElse(null);

        if (oldColumn == null || newColumn == null) return;

        if (oldColumnId != newColumnId) {
            oldColumn.getCards().stream()
                .filter(c -> c.getOrder() > oldPosition)
                .forEach(c -> cardDAO.updateOrderAndColumn(c.getId(), oldColumnId, c.getOrder() - 1));

            newColumn.getCards().stream()
                .filter(c -> c.getOrder() >= newPosition)
                .forEach(c -> cardDAO.updateOrderAndColumn(c.getId(), newColumnId, c.getOrder() + 1));

            cardDAO.updateOrderAndColumn(cardId, newColumnId, newPosition);

        } else { // Moving within the same column
            List<Card> cards = oldColumn.getCards();
            if (newPosition > oldPosition) { // Moved down
                cards.stream()
                    .filter(c -> c.getId() != cardId && c.getOrder() > oldPosition && c.getOrder() <= newPosition)
                    .forEach(c -> cardDAO.updateOrderAndColumn(c.getId(), oldColumnId, c.getOrder() - 1));
            } else { // Moved up
                cards.stream()
                    .filter(c -> c.getId() != cardId && c.getOrder() >= newPosition && c.getOrder() < oldPosition)
                    .forEach(c -> cardDAO.updateOrderAndColumn(c.getId(), oldColumnId, c.getOrder() + 1));
            }
            cardDAO.updateOrderAndColumn(cardId, oldColumnId, newPosition);
        }

        loadColumns(currentBoardId);
    }
}
