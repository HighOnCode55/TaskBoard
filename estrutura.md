```
TaskBoardFX/
|
|-- .gitignore
|-- pom.xml                // Arquivo de configuração do Maven (dependências do JavaFX, MySQL)
|-- README.md
|
`-- src/
    |
    `-- main/
|
|-- java/
|   `-- com/
|       `-- seunome/
|           `-- taskboard/
|               |
|               |-- MainApp.java             // Classe principal que inicia a aplicação JavaFX
|               |
|               |-- controller/
|               |   |-- MainViewController.java  // Controla a tela principal (seleção de board)
|               |   |-- BoardViewController.java // Controla a visualização de um board (colunas e cards)
|               |   |-- CardDetailsController.java// Controla a janela de criação/edição de card
|               |   `-- DialogController.java    // Controla diálogos (ex: bloquear card, criar board)
|               |
|               |-- model/
|               |   |-- Board.java             // Representa um board (ID, nome)
|               |   |-- Column.java            // Representa uma coluna (ID, nome, ordem, tipo)
|               |   `-- Card.java              // Representa um card (ID, título, descrição, etc.)
|               |
|               |-- dao/ (Data Access Object)
|               |   |-- DatabaseConnection.java// Gerencia a conexão com o banco MySQL
|               |   |-- BoardDAO.java          // Métodos para interagir com a tabela 'boards' (salvar, listar, excluir)
|               |   |-- ColumnDAO.java         // Métodos para interagir com a tabela 'colunas'
|               |   `-- CardDAO.java           // Métodos para interagir com a tabela 'cards' e seus históricos
|               |
|               `-- util/
|                   `-- AlertFactory.java      // Classe utilitária para criar alertas e diálogos padronizados
|
`-- resources/
    `-- com/
        `-- seunome/
            `-- taskboard/
                |
                |-- views/
                |   |-- MainView.fxml          // Layout da tela principal
                |   |-- BoardView.fxml         // Layout da visualização do board
                |   |-- CardDetails.fxml       // Layout da janela de detalhes do card
                |   `-- NamePromptDialog.fxml  // Layout para um diálogo que pede um nome/motivo
                |
                 `-- css/
                     `-- style.css              // Folha de estilos para a aplicação
```