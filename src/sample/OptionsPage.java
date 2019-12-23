package sample;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

public class OptionsPage extends Scene {
    public OptionsPage( StackPane sp, Scene bp, Stage window)  throws Exception{
        super( sp, Main.primaryScreenBounds.getWidth(), Main.primaryScreenBounds.getHeight());
        InputStream is = Files.newInputStream(Paths.get("images/options.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(Main.primaryScreenBounds.getHeight());
        imgView.setFitWidth(Main.primaryScreenBounds.getWidth());
        sp.getChildren().add(imgView);

        BackButton bb = new BackButton();
        bb.setOnMouseClicked( event -> {
            window.setScene( bp);
        });
        bb.setTranslateX(-350);
        bb.setTranslateY(-230);
        sp.getChildren().add(bb);


        // mute option
        Pane mute = new Pane();
        Pane unmute = new Pane();
        mute.setMaxSize(200,200);
        unmute.setMaxSize(200,200);
        is = Files.newInputStream(Paths.get("images/mute.png"));
        img = new Image(is);
        is.close();
        ImageView imgViewMute = new ImageView(img);
        imgViewMute.setFitHeight(200);
        imgViewMute.setFitWidth(200);
        mute.getChildren().add(imgViewMute);
        is = Files.newInputStream(Paths.get("images/unmute.png"));
        img = new Image(is);
        is.close();
        ImageView imgViewUnmute = new ImageView(img);
        imgViewUnmute.setFitHeight(200);
        imgViewUnmute.setFitWidth(200);
        unmute.getChildren().add(imgViewUnmute);
        mute.setOnMouseClicked( event -> {
            Main.mediaPlayer.setMute(true);
            mute.setVisible(false);
            unmute.setVisible(true);
        });
        unmute.setOnMouseClicked( event -> {
            Main.mediaPlayer.setMute(false);
            mute.setVisible(true);
            unmute.setVisible(false);
        });
        if(Main.mediaPlayer.isMute())
            mute.setVisible(false);
        else
            unmute.setVisible(false);
        sp.getChildren().addAll(mute, unmute);
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
    // Pause Button for GamePage
    public static class PauseButton extends VBox {
        public PauseButton() throws Exception{
            setMaxSize( 50, 50);
            InputStream is = Files.newInputStream(Paths.get( "images/pause.png"));
            Image img = new Image(is);
            is.close();
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(50);
            imgView.setFitWidth(50);
            getChildren().add(imgView);
        }
    }
}
