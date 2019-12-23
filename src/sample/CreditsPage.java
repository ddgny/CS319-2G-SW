package sample;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.util.Duration;


import java.awt.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * CreditsPage class that creates credits page
 */
public class CreditsPage extends Scene {
    
    /**
     * Constructor for CreditsPage class
     * @param sp
     * @param bp
     * @param window
     * @param isStoryFinished checks whether story is finished or not
     * @throws Exception
     */
    public CreditsPage(StackPane sp, Scene bp, Stage window, boolean isStoryFinished) throws Exception{
        super(sp, Main.primaryScreenBounds.getWidth(), Main.primaryScreenBounds.getHeight());
        InputStream is = Files.newInputStream(Paths.get("images/mainmenu.jpg"));
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
        sp.getChildren().add(bb);
        Text congrats = new Text("Congratulations!! You have finished the story mode");
        congrats.setFill(Color.GREEN);
        congrats.setFont(Font.font("Verdana", FontWeight.BOLD,35));
        congrats.setTranslateY(-300);
        //sliding credits page
        final String content = "7 Wonders\n\n" + "Ahmet Berk Eren\n"+"Safa Alperen Oruç\n"+"Göktuğ Öztürkcan\n"+"Ömer Faruk Oflaz\n"+"Deniz Doğanay";
        final Text textArea = new Text(10, 20, "");
        textArea.setWrappingWidth(350);
        textArea.maxHeight(500);
        textArea.setFill(Color.WHITESMOKE);
        textArea.setFont(Font.font("Verdana", FontWeight.THIN, 30));
        textArea.setTextAlignment(TextAlignment.CENTER);
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(8000));
            }

            protected void interpolate(double frac) {
                final int length = content.length();
                final int n = Math.round(length * (float) frac);
                textArea.setText(content.substring(0, n));
            }

        };

        animation.play();


        Circle bg = new Circle(200);

        bg.setOpacity(0.6);
        bg.setFill(Color.BLACK);
        bg.setEffect( new GaussianBlur(3.5));
        sp.getChildren().addAll(bg, textArea);
        if(isStoryFinished) sp.getChildren().add(congrats);
    }
}
