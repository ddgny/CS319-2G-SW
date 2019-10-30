package sample;

import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OptionsPage extends Scene {
    public OptionsPage( StackPane sp, Scene bp, Stage window)  throws Exception{
        super( sp, 1000, 650);
        InputStream is = Files.newInputStream(Paths.get("images/options.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(650);
        imgView.setFitWidth(1000);
        sp.getChildren().add(imgView);

        BackButton bb = new BackButton();
        bb.setOnMouseClicked( event -> {
            window.setScene( bp);
        });
        bb.setTranslateX(-350);
        bb.setTranslateY(-230);
        sp.getChildren().add(bb);

    }
    public static class BackButton extends VBox {
        public BackButton() throws Exception{
            setMaxSize(100,100);
            InputStream is = Files.newInputStream(Paths.get("images/back.png"));
            Image img = new Image(is);
            is.close();
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(100);
            imgView.setFitWidth(100);
            getChildren().add(imgView);
        }
    }
}
