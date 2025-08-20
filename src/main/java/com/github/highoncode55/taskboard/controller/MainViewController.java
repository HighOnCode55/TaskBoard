package com.github.highoncode55.taskboard.controller;

import com.github.highoncode55.taskboard.dao.BoardDAO;
import com.github.highoncode55.taskboard.model.Board;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainViewController {
    private BoardDAO boardDAO;
    
    @FXML
    private TilePane boardsPane;

    @FXML
    private Button newBoardButton;

    private void loadBoards(){
        boardsPane.getChildren().clear();
        List<Board> allBoards = boardDAO.getAll();
        for (Board board : allBoards){
            Node tile = populateTile(board);
            boardsPane.getChildren().add(tile);
        }
    }
@FXML
    public void handleNewBoardButton(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Board");
        dialog.setContentText("Name the new Board:");
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/atlantafx/base/theme/primer-dark.css").toExternalForm()
    );
        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(name -> {
            Board board = new Board(name);
            boardDAO.create(board);
            loadBoards();
        });
    }

    public void handleLoadBoardButton(ActionEvent event, long boardId) {
        try {

            // 1. Carrega o arquivo FXML da nova cena (BoardView.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/highoncode55/taskboard/views/BoardView.fxml"));
            Parent boardViewRoot = loader.load();

            // 2. (MUITO IMPORTANTE) Pega a instância do controller da nova cena
            BoardViewController boardController = loader.getController();

            // 3. Passa os dados do board selecionado para o novo controller
            //    Você precisará criar este método 'initData' no seu BoardController.
            boardController.loadBoardButton(boardId);

            // 4. Pega o Stage (a janela) atual a partir do botão que foi clicado
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 5. Cria uma nova cena com a view do board
            Scene scene = new Scene(boardViewRoot);

            // 6. (Opcional) Aplica o tema à nova cena também
            //    Lembre-se de ajustar o caminho para o seu CSS
            scene.getStylesheets().add(
                    getClass().getResource("/atlantafx/base/theme/primer-dark.css").toExternalForm()
            );

            // 7. Define a nova cena na janela
            stage.setScene(scene);
            stage.show(); // Mostra a nova cena

        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar um alerta de erro para o usuário aqui
        }
    }

    private Node populateTile(Board board) {
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
@FXML
    private void initialize(){
        this.boardDAO = new BoardDAO();
        loadBoards();
    }
}
