package sample;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Card extends Pane {
    Text coinText;
    Button sellButton,buryButton,buildButton;
    Background bg;
    HBox greenHB, redHB, yellowHB, greyHB, brownHB, blueHB, resourcesHB ,geometryVB,coinHB;
    Rectangle board;
    public Card(String name, GamePage.Property a, GamePage.Property b ) throws  Exception{
        setMaxSize(250, 400);
        //bg = new Background( new BackgroundFill(Color.rgb(109,132,118,0.1), CornerRadii.EMPTY, Insets.EMPTY));
        board = new Rectangle(250,400,Color.rgb(109,132,118,1));
        board.setArcHeight(15);
        board.setArcWidth(15);
        getChildren().add(board);
        InputStream is = Files.newInputStream(Paths.get("images/arsenal.png"));
        Image img = new Image(is);
        is.close();
        board.setFill(new ImagePattern(img));




    }
    public void  mouseEnteredHere(){
        board.setOpacity(0.65);
        sellButton = new Button("SELL");
        sellButton.setTranslateX(110);
        sellButton.setTranslateY(150);
        getChildren().add(sellButton);
        sellButton.setOnMouseClicked(event -> {

            System.out.println("SELL");

        });




        buryButton = new Button("UPGRADE WONDER");
        buryButton.setTranslateX(65);
        buryButton.setTranslateY(220);
        getChildren().add(buryButton);
        buryButton.setOnMouseClicked(event -> {

            System.out.println("UPGRADE WONDER");

        });

        buildButton = new Button("BUILD");
        buildButton.setTranslateX(105);
        buildButton.setTranslateY(290);
        getChildren().add(buildButton);
        buildButton.setOnMouseClicked(event -> {

            System.out.println("BUILD");

        });

        /*
        sell = new Text("Sell");
        sell.setFill(Color.WHITESMOKE);
        sell.setFont(Font.font("Kalam", FontWeight.BOLD,15));
        sell.setTranslateX(110);
        sell.setTranslateY(150);
        getChildren().add(sell);

         */

    }
    public void  mouseExitedHere(){
        board.setOpacity(1);
        getChildren().remove(sellButton);
        getChildren().remove(buryButton);
        getChildren().remove(buildButton);
    }
}
