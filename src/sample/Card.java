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
    String name, color;
    Button sellButton,buryButton,buildButton;
    Rectangle board;
    GamePage.Property cost, benefit;
    public Card(String name, String color, GamePage.Property a, GamePage.Property b ) throws  Exception{
        this.name = name;
        this.color = color;
        cost = a;
        benefit = b;
        //bg = new Background( new BackgroundFill(Color.rgb(109,132,118,0.1), CornerRadii.EMPTY, Insets.EMPTY));
        board = new Rectangle(140,190,Color.rgb(109,132,118,1));
        board.setArcHeight(15);
        board.setArcWidth(15);
        getChildren().add(board);
        InputStream is = Files.newInputStream(Paths.get("images/arsenal.png"));
        Image img = new Image(is);
        is.close();
        board.setFill(new ImagePattern(img));
        this.setOnMouseEntered(event -> {
            mouseEnteredHere();
        });
        this.setOnMouseExited(event -> {
            mouseExitedHere();
        });
        setMaxSize(140, 190);
    }
    public void  mouseEnteredHere(){
        board.setOpacity(0.65);
        sellButton = new Button("SELL");
        sellButton.setTranslateX(55);
        sellButton.setTranslateY(30);
        getChildren().add(sellButton);
        sellButton.setOnMouseClicked(event -> {
            GamePage.Property tmp;
            tmp = new GamePage.Property();
            tmp.coin = 3;
            GamePage.gainBenefit( 0, false, tmp);
        });

        buryButton = new Button("UPGRADE WONDER");
        buryButton.setTranslateX(10);
        buryButton.setTranslateY(70);
        getChildren().add(buryButton);
        buryButton.setOnMouseClicked(event -> {
            if( GamePage.checkResources( 0 , true, cost)) {
                GamePage.gainBenefit(0, true, benefit);
                GamePage.endTurn();
            }
            else {
                GamePage.giveError("Not enough resources");
            }
        });

        buildButton = new Button("BUILD");
        buildButton.setTranslateX(50);
        buildButton.setTranslateY(110);
        getChildren().add(buildButton);
        buildButton.setOnMouseClicked(event -> {
            if( GamePage.checkResources( 0 , false, cost)) {
                GamePage.gainBenefit(0, false, benefit);
                GamePage.endTurn();
            }
            else {
                GamePage.giveError("Not enough resources");
            }
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
