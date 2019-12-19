package sample;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage.*;
import javafx.scene.Node.*;

import java.awt.*;

public class Main extends Application {
    private GameMenu gameMenu;
    private static Stage window;
    private static StackPane root, options, htp,credits, setName;
    private static Scene mainMenu;
    public static Rectangle2D primaryScreenBounds;
    public static MediaPlayer mediaPlayer;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setMaximized(true);
        root = new StackPane();
        window = primaryStage;
        primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        // Background image
        InputStream is = Files.newInputStream(Paths.get("images/mainmenu.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(primaryScreenBounds.getHeight());
        imgView.setFitWidth(primaryScreenBounds.getWidth());

        gameMenu = new GameMenu();
        root.getChildren().addAll(imgView, gameMenu);

        mainMenu = new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        primaryStage.setTitle("Seven Wonders");
        primaryStage.setScene(mainMenu);
        window.show();
    }
    private static class GameMenu extends Parent {
        MenuButton btnStart, btnLoadGame, btnOptions, btnHTP, btnCredits, btnExit;
        private void modeSelectionMenu() {
            VBox menu2 = new VBox(20);
            menu2.setTranslateX(200);
            menu2.setTranslateY(20);

            // story mode
            MenuButton btnStory = new MenuButton("Story Mode");
            btnStory.setOnMouseClicked(event -> {
                Scene scene = null;
                try {
                    setName = new StackPane();
                    scene = new setNamePage(setName, mainMenu, window, "Story");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.setScene( scene);
            });

            // quickmatch
            MenuButton btnQuickmatch = new MenuButton("Quick Match");
            btnQuickmatch.setOnMouseClicked(event -> {
                Scene scene = null;
                try {
                    setName = new StackPane();
                    scene = new setNamePage(setName, mainMenu, window, "Quick");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.setScene( scene);
            });

            // cancel
            MenuButton btnCancel = new MenuButton("Cancel");
            btnCancel.setOnMouseClicked( event -> {
                btnCancel.setVisible(false);
                btnQuickmatch.setVisible(false);
                btnStory.setVisible(false);
                menu2.setVisible(false);
                btnStart.setVisible(true);
                btnLoadGame.setVisible(true);
                btnOptions.setVisible(true);
                btnHTP.setVisible(true);
                btnCredits.setVisible(true);
                btnExit.setVisible(true);
            });
            menu2.getChildren().addAll(btnStory, btnQuickmatch, btnCancel);
            getChildren().addAll(menu2);
        }
        public GameMenu() {
            VBox menu1 = new VBox(20);
            menu1.setTranslateX(200);
            menu1.setTranslateY(20);


            // start game button
            btnStart = new MenuButton("Start a New Game");
            btnLoadGame = new MenuButton("Load Game");
            btnOptions = new MenuButton("Options");
            btnHTP = new MenuButton("How to Play");
            btnCredits = new MenuButton("Credits");
            btnExit = new MenuButton("Exit Game");
            btnStart.setOnMouseClicked(event -> {
                btnStart.setVisible(false);
                btnLoadGame.setVisible(false);
                btnOptions.setVisible(false);
                btnHTP.setVisible(false);
                btnCredits.setVisible(false);
                btnExit.setVisible(false);
                modeSelectionMenu();
//                Scene scene = null;
//                try {
//                    setName = new StackPane();
//                    scene = new setNamePage(setName, mainMenu, window);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                window.setScene( scene);
            });

            // load game button
            btnLoadGame.setOnMouseClicked(event -> {

            });

            // Options button
            btnOptions.setOnMouseClicked(event -> {
                Scene scene = null;
                try {
                    options = new StackPane();
                    scene = new OptionsPage(options, mainMenu, window);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.setScene( scene);
            });

            // How to Play button
            btnHTP.setOnMouseClicked(event -> {
                Scene scene = null;
                try {
                    htp = new StackPane();
                    scene = new HTPPage(htp, mainMenu, window);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.setScene( scene);
            });
            //Credits Game button
            btnCredits.setOnMouseClicked(event -> {
                Scene scene = null;
                try {
                    credits = new StackPane();
                    scene = new CreditsPage(credits, mainMenu, window);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.setScene( scene);
            });

            // Exit Game button
            btnExit.setOnMouseClicked(event -> {
                System.exit(0);
            });
            menu1.getChildren().addAll(btnStart, btnLoadGame, btnOptions, btnHTP, btnCredits, btnExit);
            getChildren().add(menu1);
        }
    }
    public static class MenuButton extends StackPane{
        private Text text;
        public MenuButton(String name) {
            // creating the text of button
            text = new Text(name);
            text.setFont(text.getFont().font(20));
            text.setFill(Color.WHITESMOKE);

            // creating the button shape
            Rectangle bg = new Rectangle(250,30);
            bg.setOpacity(0.6);
            bg.setFill(Color.BLACK);
            bg.setEffect( new GaussianBlur(3.5));

            setAlignment(Pos.CENTER);
            getChildren().addAll(bg, text);

            // when mouse is on the button
            setOnMouseEntered(event -> {
                bg.setFill(Color.WHITESMOKE);
                text.setFill(Color.BLACK);
            });

            // when mouse exits the button
            setOnMouseExited(event-> {
                bg.setFill(Color.BLACK);
                text.setFill(Color.WHITESMOKE);
            });

            DropShadow click = new DropShadow(50, Color.WHITE);
            click.setInput(new Glow());
            setOnMousePressed(event -> setEffect(click));
            setOnMouseReleased(event -> setEffect(null));

        }
    }

    public static void main(String[] args) {
        Media sound = new Media(new File("sounds/gamemusic.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        launch(args);
    }
}
