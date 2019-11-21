package sample;

import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;


import java.awt.*;
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

        Text textArea = new Text();

        textArea.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Duis at consectetur lorem donec massa sapien. Lacus vel facilisis volutpat est velit egestas dui id. Vel risus commodo viverra maecenas. Consequat interdum varius sit amet mattis vulputate enim. Mauris rhoncus aenean vel elit. Eu lobortis elementum nibh tellus molestie. Sem fringilla ut morbi tincidunt augue interdum velit euismod in. Leo vel orci porta non. Et molestie ac feugiat sed lectus vestibulum mattis. Turpis massa sed elementum tempus. Platea dictumst quisque sagittis purus sit amet volutpat. Etiam dignissim diam quis enim lobortis scelerisque fermentum dui faucibus. Leo duis ut diam quam nulla porttitor massa id neque. Phasellus faucibus scelerisque eleifend donec pretium vulputate.\n");
        textArea.setWrappingWidth(450);
        textArea.maxHeight(500);
        textArea.setFill(Color.WHITESMOKE);
        textArea.setFont(Font.font("Verdana", FontWeight.BOLD,15));
        Rectangle bg = new Rectangle(500,500);
        bg.setOpacity(0.6);
        bg.setFill(Color.BLACK);
        bg.setEffect( new GaussianBlur(3.5));
        sp.getChildren().addAll(bg, textArea);
    }
}
