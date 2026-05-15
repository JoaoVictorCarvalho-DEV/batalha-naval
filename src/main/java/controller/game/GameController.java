package controller.game;

import app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class GameController {

    public GridPane enemyBoard;


    @FXML
    private GridPane playerBoard;

    @FXML
    public void initialize() {
        buildBoard(playerBoard, false);
        buildBoard(enemyBoard, true);
    }

    public void viewMenu(){
        Main.changeScreen("menu.fxml");
    }


    private void buildBoard(GridPane board, boolean isEnemy) {
        int size = 10; // tabuleiro 10x10

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                Button cell = new Button();
                cell.setPrefSize(35, 35);

                int r = row;
                int c = col;

                if (isEnemy) {
                    cell.setOnAction(e -> shoot(r, c, cell));
                }

                board.add(cell, col, row);
            }
        }
    }


    private void shoot(int row, int col, Button cell) {
        cell.setStyle("-fx-background-color: red;");
        cell.setDisable(true);
    }
}
