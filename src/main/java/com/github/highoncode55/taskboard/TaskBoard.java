package com.github.highoncode55.taskboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.exception.LiquibaseException;

import java.io.IOException;

public class TaskBoard extends Application {
    private void runLiquibase() throws LiquibaseException {
        // As credenciais do seu banco de dados
        String url = "jdbc:mysql://localhost:3306/taskboard_db";
        String user = "root";
        String password = "123456"; // <<< ATUALIZE AQUI
        String changelogFile = "db/changelog/db.changelog-master.xml";

        try {
            // A maneira moderna de executar comandos do Liquibase programaticamente
            CommandScope update = new CommandScope(UpdateCommandStep.COMMAND_NAME);

            update.addArgumentValue(DbUrlConnectionCommandStep.URL_ARG, url);
            update.addArgumentValue(DbUrlConnectionCommandStep.USERNAME_ARG, user);
            update.addArgumentValue(DbUrlConnectionCommandStep.PASSWORD_ARG, password);
            update.addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, changelogFile);

            // Define o ClassLoader para que o Liquibase encontre o changelog nos resources
            Scope.child(Scope.Attr.classLoader, this.getClass().getClassLoader(), () -> {
                update.execute();
            });

        } catch (Exception e) {
            // Lança a exceção para ser tratada no método start()
            throw new LiquibaseException("Falha ao executar a migração do Liquibase", e);
        }
    }


    @Override
    public void start(Stage stage) throws IOException, LiquibaseException {
        runLiquibase();
        FXMLLoader fxmlLoader = new FXMLLoader(TaskBoard.class.getResource("views/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Task Board");
        stage.setScene(scene);
        stage.show();
    }
}
