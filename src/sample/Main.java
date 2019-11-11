package sample;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.layout.VBox;
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
    private static StackPane root, options, htp, setName;
    private static Scene mainMenu;
    public static Rectangle2D primaryScreenBounds;
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
        public GameMenu() {
            VBox menu1 = new VBox(20);
            menu1.setTranslateX(200);
            menu1.setTranslateY(20);

            // start game button
            MenuButton btnStart = new MenuButton("Start Game");
            btnStart.setOnMouseClicked(event -> {
                Scene scene = null;
                try {
                    setName = new StackPane();
                    scene = new setNamePage(setName, mainMenu, window);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.setScene( scene);
            });

            // Options button
            MenuButton btnOptions = new MenuButton("Options");
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
            MenuButton btnHTP = new MenuButton("How to Play");
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

            // Exit Game button
            MenuButton btnExit = new MenuButton("Exit Game");
            btnExit.setOnMouseClicked(event -> {
                System.exit(0);
            });
            menu1.getChildren().addAll(btnStart, btnOptions, btnHTP, btnExit);
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
        launch(args);
    }
}
