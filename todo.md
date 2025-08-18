Ótima iniciativa! Ter um plano claro é a melhor forma de construir o software. Vamos dividir o trabalho em etapas lógicas, começando pela base (os dados) e subindo até a interface do usuário.

Aqui está um "To-Do List" do que implementar em cada parte do seu projeto, sem códigos prontos.

---

### Fase 1: A Base de Tudo (Modelos e Conexão)

O objetivo aqui é criar as "plantas" dos seus dados e garantir a conexão com o banco.

#### **Para as classes do pacote `model` (`Board.java`, `Column.java`, `Card.java`)**

* [x] **Definir os Atributos:** Em cada classe, declare os campos privados que correspondem exatamente às colunas da sua tabela no banco de dados.
    * **`Board`**: `id`, `nome`.
    * **`Column`**: `id`, `nome`, `ordem`, `tipo`, `board_id`.
    * **`Card`**: `id`, `titulo`, `descricao`, `data_criacao`, `esta_bloqueado`, `coluna_id`.
* [x] **Usar o Lombok:** Adicione a anotação `@Data` (ou `@Getter`, `@Setter`, etc.) em cima da declaração de cada classe para gerar automaticamente os métodos básicos.

#### **Para a classe `dao/DatabaseConnection.java`**

* [ ] **Implementar o Padrão Singleton:** Garanta que só exista uma instância dessa classe.
    * [ ] Crie uma instância estática privada dela mesma.
    * [ ] Crie um construtor `private` para impedir que outros a criem.
    * [ ] Crie um método `public static` `getInstance()` ou `getConnection()` que retorna a única instância.
* [ ] **Gerenciar a Conexão:** O método `getConnection()` deve verificar se a conexão com o banco já existe. Se não, ele deve criá-la usando `DriverManager.getConnection()` com a URL, usuário e senha.
* [ ] **Criar um Método para Fechar:** Implemente um método para fechar a conexão, que será chamado quando a aplicação for encerrada.

---

### Fase 2: A Camada de Persistência (DAOs - Data Access Objects)

Aqui você vai escrever o código que "conversa" com o banco de dados. Cada DAO será responsável por uma tabela.

#### **Para a classe `dao/BoardDAO.java`**

* [ ] **`salvar(Board board)`:** Recebe um objeto `Board`, executa um `INSERT` na tabela `boards` e, idealmente, retorna o objeto `Board` com o `id` gerado pelo banco.
* [ ] **`atualizar(Board board)`:** Recebe um objeto `Board`, executa um `UPDATE` na tabela `boards` usando o `id` do objeto.
* [ ] **`excluir(long boardId)`:** Recebe um `id`, executa um `DELETE` na tabela `boards` para aquele `id`.
* [ ] **`buscarPorId(long boardId)`:** Recebe um `id`, executa um `SELECT` e retorna o objeto `Board` correspondente.
* [ ] **`buscarTodos()`:** Executa um `SELECT` para buscar todos os registros da tabela `boards` e retorna uma `List<Board>`.

#### **Para as classes `dao/ColumnDAO.java` e `dao/CardDAO.java`**

* [ ] **Implementar os métodos CRUD:** Crie os mesmos métodos básicos do `BoardDAO` (`salvar`, `atualizar`, `excluir`, `buscarPorId`).
* [ ] **Criar Métodos de Busca Específicos:** Esta é a parte mais importante.
    * **`ColumnDAO`**: Crie um método `buscarPorBoardId(long boardId)`. Ele receberá o ID de um board e retornará uma `List<Column>` com todas as colunas que pertencem àquele board, ordenadas pelo campo `ordem`.
    * **`CardDAO`**: Crie um método `buscarPorColunaId(long colunaId)`. Ele receberá o ID de uma coluna e retornará uma `List<Card>` com todos os cards daquela coluna.

---

### Fase 3: A Lógica da Interface (Controllers)

Agora você vai conectar a lógica de negócio com os botões e componentes visuais.

#### **Para a classe `controller/MainViewController.java` (Tela de seleção de boards)**

* [ ] **Mapear Componentes FXML:** Use a anotação `@FXML` para linkar os componentes da sua tela (ex: um `ListView<String>` para os nomes dos boards, `Button` para criar, selecionar, excluir).
* [ ] **`initialize()`:** Implemente o método `initialize`. Ele é chamado quando a tela é carregada. A tarefa dele é:
    * [ ] Chamar o `boardDAO.buscarTodos()`.
    * [ ] Popular a `ListView` com os nomes dos boards retornados.
* [ ] **Implementar as Ações dos Botões:** Crie métodos para cada botão (ex: `handleSelecionarButton`).
    * **Botão "Selecionar"**: Deve pegar o board selecionado na lista e abrir a tela principal do board, passando o objeto `Board` selecionado para o próximo controller.
    * **Botão "Criar"**: Deve abrir uma pequena janela de diálogo para pedir o nome do novo board, chamar `boardDAO.salvar()` e depois atualizar a lista.
    * **Botão "Excluir"**: Deve pegar o board selecionado, pedir confirmação, chamar `boardDAO.excluir()` e atualizar a lista.

#### **Para a classe `controller/BoardViewController.java` (Tela principal do board)**

* [ ] **Receber o Board:** Crie um método público (ex: `carregarDados(Board board)`) que será chamado pelo `MainViewController` para passar o board que o usuário selecionou.
* [ ] **Renderizar o Board:** Dentro do método `carregarDados`:
    * [ ] Use o ID do board recebido para chamar `columnDAO.buscarPorBoardId(board.getId())`.
    * [ ] Faça um loop pela lista de colunas retornada. Para cada coluna:
        * [ ] Crie dinamicamente a interface da coluna (um `VBox` dentro de um `Pane` estilizado, por exemplo).
        * [ ] Chame `cardDAO.buscarPorColunaId(coluna.getId())`.
        * [ ] Faça um loop pela lista de cards retornada. Para cada card:
            * [ ] Crie dinamicamente a interface do card (um `Pane` com um `Label`, por exemplo).
            * [ ] Adicione a interface do card na interface da coluna correspondente.
* [ ] **Implementar Ações do Board:** Crie métodos para as ações (criar card, mover card, bloquear/desbloquear).
    * Esses métodos chamarão os DAOs apropriados (`cardDAO.salvar`, `cardDAO.atualizar`) e, após a confirmação do banco, deverão **atualizar a interface gráfica** para refletir a mudança.

---

### Fase 4: Juntando Tudo (Classe Principal)

#### **Para a classe `TaskBoardApplication.java`**

* [ ] **Verificar a Ordem no `start()`:** Garanta que a sequência de inicialização seja:
    1.  Executar o método `runLiquibase()`.
    2.  Se o Liquibase rodar com sucesso, carregar o FXML da tela principal (`MainView.fxml`).
    3.  Configurar a `Scene` e o `Stage`.
    4.  Exibir a janela com `stage.show()`.

Seguindo esta lista, você construirá sua aplicação de forma estruturada, garantindo que cada peça seja testada e funcione antes de passar para a próxima. Boa codificação!