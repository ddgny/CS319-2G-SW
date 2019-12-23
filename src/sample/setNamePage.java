package sample;

import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.CheckBox;

import java.awt.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * setNamePage class
 */
public class setNamePage extends Scene {
    /**
     * Constructor for setNamePage class
     * @param sp
     * @param bp
     * @param window
     * @param mode game mode
     * @throws Exception
     */
    public setNamePage(StackPane sp, Scene bp, Stage window, String mode) throws Exception {
        super( sp, Main.primaryScreenBounds.getWidth(), Main.primaryScreenBounds.getHeight());
        InputStream is = Files.newInputStream(Paths.get("images/setnameQuick.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(Main.primaryScreenBounds.getHeight());
        imgView.setFitWidth(Main.primaryScreenBounds.getWidth());
        sp.getChildren().add(imgView);

        OptionsPage.BackButton bb = new OptionsPage.BackButton();
        bb.setOnMouseClicked( event -> {
            window.setScene( bp);
        });
        bb.setTranslateX(-350);
        bb.setTranslateY(-230);

        // isim ve yüz seçerkenki karanlık alan
        Rectangle nameArea = new Rectangle(300,250);
        nameArea.setOpacity(0.6);
        nameArea.setFill(Color.BLACK);
        nameArea.setEffect( new GaussianBlur(3.5));
        // name
        if( mode == "Quick") {
            TextField tf = new TextField("Player");
            tf.setMaxWidth(150);
            tf.setTranslateY(-30);
            Text nameText = new Text("Player Name: ");
            nameText.setFill(Color.WHITESMOKE);
            nameText.setFont(Font.font("Verdana", FontWeight.BOLD,15));
            nameText.setTranslateY(-70);

            Text sideText = new Text("Which side you want to play? ");
            sideText.setFill(Color.WHITESMOKE);
            sideText.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
            sideText.setTranslateY(10);
            // side
            RadioButton aSide = new RadioButton("A");
            aSide.setUserData("A");
            RadioButton bSide = new RadioButton("B");
            bSide.setUserData("B");
            aSide.setTextFill(Color.WHITESMOKE);
            bSide.setTextFill(Color.WHITESMOKE);
            ToggleGroup sidetg = new ToggleGroup();
            aSide.setToggleGroup(sidetg);
            bSide.setToggleGroup(sidetg);
            aSide.setSelected(true);
            HBox hbox = new HBox(aSide, bSide);
            bSide.setTranslateX(40);
            hbox.setTranslateY(447);
            hbox.setTranslateX(710);
            // ally mode
            CheckBox ally = new CheckBox("Ally Mode");
            ally.setTranslateY(90);
            ally.setTextFill(Color.WHITESMOKE);
            // Start Button
            Main.MenuButton sb = new Main.MenuButton("Start");
            sb.setMaxSize(250, 30);
            sb.setTranslateY(170);

            sb.setOnMouseClicked(event -> {
                String side = sidetg.getSelectedToggle().getUserData().toString();
                Scene scene = null;
                StackPane gameScreen = new StackPane();
                int intMode;
                if (mode == "Story") intMode = 1;
                else if (ally.isSelected()) intMode = -1;
                else intMode = 0;
                try {
                    scene = new GamePage(gameScreen, bp, window, tf.getText(), side, intMode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.setScene(scene);
            });

            sp.getChildren().addAll(bb, nameArea, nameText, tf, sideText, hbox, ally, sb);
        }
        else{
            sp.getChildren().removeAll(imgView);
            InputStream is1 = Files.newInputStream(Paths.get("images/setnameStory.jpg"));
            Image img8 = new Image(is1);
            is.close();
            ImageView imgView1 = new ImageView(img8);
            imgView1.setFitHeight(Main.primaryScreenBounds.getHeight());
            imgView1.setFitWidth(Main.primaryScreenBounds.getWidth());
            sp.getChildren().add(imgView1);
            TextField tf = new TextField("Player");
            tf.setMaxWidth(150);
            tf.setTranslateY(20);
            Text nameText = new Text("Player Name: ");
            nameText.setFill(Color.WHITESMOKE);
            nameText.setFont(Font.font("Verdana", FontWeight.BOLD,15));
            nameText.setTranslateY(-20);

            RadioButton aSide = new RadioButton("A");
            aSide.setUserData("A");
            RadioButton bSide = new RadioButton("B");
            bSide.setUserData("B");
            aSide.setTextFill(Color.WHITESMOKE);
            bSide.setTextFill(Color.WHITESMOKE);
            ToggleGroup sidetg = new ToggleGroup();
            aSide.setToggleGroup(sidetg);
            bSide.setToggleGroup(sidetg);
            aSide.setSelected(true);
            Main.MenuButton sb = new Main.MenuButton("Start");
            sb.setMaxSize(250, 30);
            sb.setTranslateY(170);

            sb.setOnMouseClicked(event -> {
                String side = sidetg.getSelectedToggle().getUserData().toString();
                Scene scene = null;
                StackPane gameScreen = new StackPane();
                int intMode;
                if (mode == "Story") intMode = 1;

                else intMode = 0;
                try {
                    scene = new GamePage(gameScreen, bp, window, tf.getText(), side, intMode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                window.setScene(scene);
            });

            sp.getChildren().addAll(bb, nameArea, nameText, tf, sb);
        }
    }
}
