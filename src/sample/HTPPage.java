package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.HBox;

import javafx.scene.control.ScrollBar;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HTPPage extends Scene {
    public HTPPage(StackPane sp, Scene bp, Stage window) throws Exception{
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
        VBox Vtext =new VBox();
        HBox scrollHbox= new HBox();
        Text textArea = new Text();
        Text textHeader = new Text();
        textArea.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Duis at consectetur lorem donec massa sapien. Lacus vel facilisis volutpat est velit egestas dui id. Vel risus commodo viverra maecenas. Consequat interdum varius sit amet mattis vulputate enim. Mauris rhoncus aenean vel elit. Eu lobortis elementum nibh tellus molestie. Sem fringilla ut morbi tincidunt augue interdum velit euismod in. Leo vel orci porta non. Et molestie ac feugiat sed lectus vestibulum mattis. Turpis massa sed elementum tempus. Platea dictumst quisque sagittis purus sit amet volutpat. Etiam dignissim diam quis enim lobortis scelerisque fermentum dui faucibus. Leo duis ut diam quam nulla porttitor massa id neque. Phasellus faucibus scelerisque eleifend donec pretium vulputate.\n");
        textArea.setWrappingWidth(450);
        textArea.maxHeight(500);
        textArea.setFill(Color.WHITESMOKE);
        textArea.setFont(Font.font("Verdana", FontWeight.BOLD,15));
        textHeader.setText("Seven Wonders");
        textHeader.setFill(Color.WHITESMOKE);
        textHeader.setFont(Font.font("Verdana", FontWeight.BOLD,25));

        Vtext.getChildren().add(textHeader);
        Vtext.getChildren().add(textArea);
        Vtext.setMargin(textHeader,new Insets(25,60,20,120));
        Vtext.maxHeight(500);
        ScrollBar s = new ScrollBar();
        s.setOrientation(Orientation.VERTICAL);
        s.setMax(100);


        scrollHbox.getChildren().add(Vtext);
        scrollHbox.getChildren().add(s);
        scrollHbox.setAlignment(Pos.CENTER);
        scrollHbox.setMaxHeight(100);
        scrollHbox.setPrefHeight(100);
       // scrollHbox.setFillHeight(false);
        Rectangle bg = new Rectangle(500,500);
        bg.setOpacity(0.6);
        bg.setFill(Color.BLACK);
        bg.setEffect( new GaussianBlur(3.5));
        ScrollPane scpane = new ScrollPane();
        sp.getChildren().addAll(bg,scrollHbox);
    }
}
