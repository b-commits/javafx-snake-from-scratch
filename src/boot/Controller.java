package boot;


import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.GameBoard;
import model.Snake;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class Controller {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ImageView arrowRight;
    @FXML
    private Button exitButton;
    @FXML
    private Button newgameButton;
    @FXML
    private Button configButton;
    @FXML
    private ImageView imgView;
    @FXML
    private Label difficultyLabel;
    @FXML
    private ImageView arrowLeft;
    @FXML
    private Button arrowLeftBtn;
    @FXML
    private Button arrowRightBtn;
    @FXML
    private AnchorPane configPane;
    @FXML
    private Button backButton;
    @FXML
    private AnchorPane highscoresPane;
    @FXML
    private ListView<String> listView;
    @FXML
    private Button highScoresBackButton;
    @FXML
    private Button highScoresBtn;

    private Clip clip;

    public void initialize() throws Exception {
        setControls();
        loadMenuImage();
        playMusic();
        animateLogo();
        animateNewGame();
        loadHighScores();
    }

    private void loadMenuImage() {
        imgView.setImage(new Image("resources\\menuImage.png"));
        arrowLeft.setImage(new Image("resources\\arrowleft.png"));
        arrowRight.setImage(new Image("resources\\arrowright.png"));
    }

    private void playMusic() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File ost = new File("src/resources/ducktales.wav");
        clip = AudioSystem.getClip();
        clip.open(AudioSystem.getAudioInputStream(ost));
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    private void animateLogo() {
        // TODO: 06.06.2020 zamienić na timer, bo się chrzani
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                imgView.setOpacity(imgView.getOpacity() - 0.1);
                if (imgView.getOpacity() <= 0) {
                    while (imgView.getOpacity() <= 1) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        imgView.setOpacity(imgView.getOpacity()+0.1);
                    }
                }
            }
        }).start();
    }

    private void animateNewGame() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                newgameButton.setOpacity(newgameButton.getOpacity() - 0.1);
                if (newgameButton.getOpacity() <= 0) {
                    while (newgameButton.getOpacity() <= 1) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        newgameButton.setOpacity(newgameButton.getOpacity()+0.1);
                    }
                }
            }
        }).start();
    }

    private void setControls() {
        difficultyLabel.setText("HARD");
        newgameButton.setOnAction(event -> {
                int cellHeight;
                int windowHeight = 800;
                int windowWidth = 800;
                if (difficultyLabel.getText().equals("HARD")) {
                    cellHeight = 40;
                    Snake.GAME_SPEED = 70;
                } else {
                    cellHeight = 20;
                    Snake.GAME_SPEED = 150;
                }
                GameBoard board = new GameBoard(cellHeight, clip);
                Scene scene = new Scene(board,
                        windowWidth + (windowWidth / cellHeight),
                        windowHeight + (windowWidth / cellHeight));
                Stage stage = (Stage)imgView.getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
                scene.setOnKeyPressed(this::setKeyListeners);
                stage.setTitle("Mega Snake 9 - GUI_PRO_3");
                stage.setOnCloseRequest(windowEvent -> Snake.isSlithering = false);
            });
        arrowLeftBtn.setOnAction(event -> {
            if (difficultyLabel.getText().equals("HARD"))
            difficultyLabel.setText("EASY");
        });
        arrowRightBtn.setOnAction(event -> {
            if (difficultyLabel.getText().equals("EASY"))
                difficultyLabel.setText("HARD");
        });
        configButton.setOnAction(event -> {
            mainPane.setVisible(false);
            configPane.setVisible(true);
        });
        backButton.setOnAction(event -> {
            mainPane.setVisible(true);
            configPane.setVisible(false);
        });
        exitButton.setOnAction(event -> {
            exitButton.getScene().getWindow().hide();
            clip.stop();
        });
        highScoresBtn.setOnAction(event -> {
            mainPane.setVisible(false);
            highscoresPane.setVisible(true);
        });
        highScoresBackButton.setOnAction(event -> {
            mainPane.setVisible(true);
            highscoresPane.setVisible(false);
        });

    }

    private void setKeyListeners(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.LEFT && !Snake.direction.equals("EAST")) {
            Snake.direction = "WEST";
        } else if (keyEvent.getCode() == KeyCode.RIGHT && !Snake.direction.equals("WEST")) {
            Snake.direction = "EAST";
        } else if (keyEvent.getCode() == KeyCode.UP && !Snake.direction.equals("SOUTH"))  {
            Snake.direction = "NORTH";
        } else if (keyEvent.getCode() == KeyCode.DOWN && !Snake.direction.equals("NORTH")) {
            Snake.direction = "SOUTH";
        }
    }

    private void loadHighScores() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("src/resources/highscores.txt"));
        String record = br.readLine();
        while (record != null) {
            listView.getItems().add(record);
            record = br.readLine();
        }
    }

}
