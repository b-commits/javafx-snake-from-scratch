package model;


import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import javax.sound.sampled.Clip;
import java.util.ArrayList;
import java.util.List;

public class GameBoard extends GridPane {

    private static final int HEIGHT = 800;
    public static final int WIDTH = 800;
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Color FILL_COLOR = Color.BLACK;

    private final Clip clip;
    public int cellHeight;
    public List<Cell> cells;

    public GameBoard(int cellHeight, Clip clip) {
        this.clip = clip;
        setPrefSize(WIDTH, HEIGHT);
        this.cellHeight = cellHeight;
        this.cells = new ArrayList<>();
        fillWithCells();
        new Snake(this);
    }

    private void fillWithCells() {
        int cellCount = 800/cellHeight;
        for (int i = 0; i < cellCount; i++) {
            for (int j = 0; j < cellCount; j++) {
                Cell cell = new Cell(cellHeight, cellHeight);
                cell.setStroke(BORDER_COLOR);
                cell.setFill(FILL_COLOR);
                cells.add(cell);
                this.addRow(j, cell);
            }
        }
    }

    public Clip getAudio() {
        return clip;
    }
}

