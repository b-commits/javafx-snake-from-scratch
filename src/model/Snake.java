package model;

import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import static java.lang.Thread.sleep;

public class Snake extends Rectangle {

    /**
     * Config.
     */
    private static final Color TAIL_COLOR = Color.DARKRED;
    private static final Color HEAD_COLOR = Color.RED;
    public static int GAME_SPEED = 150;

    public static volatile boolean isSlithering;
    public static String direction;
    private Cell head;
    private final GameBoard board;
    private int center;
    private Cell food;
    private final ArrayList<Cell> tail = new ArrayList<>();
    private int length;

    public Snake(GameBoard board) {
        length = 1;
        isSlithering = true;
        this.board = board;
        this.center = ((board.cells.size()/2)-(GameBoard.WIDTH /board.cellHeight)+(GameBoard.WIDTH /board.cellHeight)/2)-1;
        this.head = board.cells.get(center);
        direction = "NORTH";
        Optional<Cell> start = board.cells.stream().filter(cell -> cell.equals(head)).findFirst();
        start.ifPresent(cell -> cell.setFill(Color.RED));
        spawnFood();
        slither();
    }

    private void slither()  {
        Thread snakeMovement = new Thread(() -> {
            try {
                while (isSlithering) {
                    try {
                        sleep(GAME_SPEED);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkWinCondition();
                    checkFailConditions();
                    head.setFill(Color.BLACK);
                    switch (direction) {
                        case "WEST":
                            head = board.cells.get(center - (GameBoard.WIDTH / board.cellHeight));
                            center -= (GameBoard.WIDTH / board.cellHeight);
                            getHead().ifPresent(cell -> cell.setFill(TAIL_COLOR));
                            break;
                        case "EAST":
                            head = board.cells.get(center + (GameBoard.WIDTH / board.cellHeight));
                            center += (GameBoard.WIDTH / board.cellHeight);
                            getHead().ifPresent(cell -> cell.setFill(TAIL_COLOR));
                            break;
                        case "NORTH":
                            head = board.cells.get(center - 1);
                            center--;
                            getHead().ifPresent(cell -> cell.setFill(TAIL_COLOR));
                            break;
                        case "SOUTH":
                            head = board.cells.get(center + 1);
                            center++;
                            getHead().ifPresent(cell -> cell.setFill(TAIL_COLOR));
                            break;
                    }
                    eatFood();
                    tail.add(head);
                    if (length > 0) {
                        tail.stream().filter(c -> !c.equals(food)).forEach(c -> c.setFill(Color.BLACK));
                        tail.stream().filter(c -> c.equals(food)).forEach(c -> c.setFill(Color.YELLOWGREEN));
                        for (int i = 0; i < length; i++) {
                            tail.get(tail.size() - i - 1).setFill(TAIL_COLOR);
                        }
                    }
                    checkFailConditions();
                    head.setFill(HEAD_COLOR);
                }
            } catch (IndexOutOfBoundsException ex) {
                fireDeathAnimation();
                bringUpMenu();
            }
        });
        snakeMovement.start();
    }

    private Optional<Cell> getHead() {
        return board.cells.stream().filter(cell -> cell.equals(head)).findFirst();
    }

    private Optional<Cell> getFood() {
        return board.cells.stream().filter(cell -> cell.equals(food)).findFirst();
    }

    private void eatFood() {
        if (head.getLayoutY() == food.getLayoutY() && head.getLayoutX() == food.getLayoutX() && head.getLayoutY() != 0) {
            food.setFill(Color.BLACK);
            length ++;
            spawnFood();
        }
    }

    private void spawnFood() {
        food = board.cells.get((int)(Math.random()*board.cells.size()));
        if (food.getLayoutY() == 0 && food.getLayoutX() != 0) {
            food.setFill(Color.BLACK);
            spawnFood();
        }
        getFood().ifPresent(cell -> cell.setFill(Color.YELLOWGREEN));
    }

    private void checkFailConditions() {
        if (length > 0) {
            if (tail.indexOf(head) != tail.size()-1 && tail.indexOf(head) >= tail.size()-length-1 && tail.indexOf(head) < tail.size()-1) {
                System.out.println("wtf");
                isSlithering = false;
                fireDeathAnimation();
                bringUpMenu();
            }
        }
    }

    private void fireDeathAnimation() {
        for (int i = tail.size()-1; i > tail.size()-length-1; i--) {
            FillTransition fr = new FillTransition(Duration.seconds(3), tail.get(i));
            FillTransition fr2 = new FillTransition(Duration.seconds(3), head);
            fr.setFromValue(Color.DARKRED);
            fr.setToValue(Color.BLACK);
            fr.play();
            fr2.play();
        }
    }

    private void bringUpMenu() {
        Platform.runLater(() -> {
            Stage s = (Stage)board.getScene().getWindow();
            board.getAudio().stop();
            Parent root = null;
            try {
                playGameOverEffect();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }

            TextInputDialog dialog = new TextInputDialog("Enter your name here...");
            dialog.setTitle("Your score: "+length);
            dialog.setHeaderText("GAME OVER");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(value -> saveHighScoreToFile(result.get()));

            try {
                root = FXMLLoader.load(getClass().getResource("/resources/sample.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            s.setScene(new Scene(root, 800, 800));
            s.setTitle("Mega Snake 9 - GUI_PRO_3");
            s.setResizable(false);
            s.show();
        });

    }

    private void checkWinCondition() {
        int snakeCoverage = 0;
        for (Cell cell: board.cells) {
            if (cell.getFill().equals(TAIL_COLOR) || cell.getFill().equals(HEAD_COLOR)) {
                snakeCoverage++;
            }
        }
        if (snakeCoverage == board.cells.size()) {
            bringUpMenu();
        }
    }

    private void saveHighScoreToFile(String s) {
        try {
            FileWriter fw = new FileWriter(new File("src/resources/highscores.txt"), true);
            if (GAME_SPEED == 70) {
                fw.write(s+"---"+(length*2)+"\n");
            } else {
                fw.write(s+"---"+length+"\n");
            }
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playGameOverEffect() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File ost = new File("src/resources/fail.wav.wav");
        Clip clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(ost));
        clip.start();
    }



}
