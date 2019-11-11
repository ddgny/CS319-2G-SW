package sample;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.awt.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class GamePage extends Scene {
    private int[] coin, shield, battle, vicPoint;
    private WonderBoard[] wb;

    public GamePage(StackPane sp, Scene mainmenu, Stage window, String name, ToggleGroup side) throws Exception {
        super(sp, Main.primaryScreenBounds.getWidth(), Main.primaryScreenBounds.getHeight());
        coin = new int[4];
        coin[0] = 3; coin[1] = 3; coin[2] = 3; coin[3] = 3;
        shield = new int[4];
        battle = new int[4];
        vicPoint = new int[4];
        wb = new WonderBoard[4];
        InputStream is = Files.newInputStream(Paths.get("images/gamepage.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(Main.primaryScreenBounds.getHeight());
        imgView.setFitWidth(Main.primaryScreenBounds.getWidth());
        sp.getChildren().add(imgView);
        distributeWonders( sp, side, name);
        sp.getChildren().addAll(wb);
    }
    private void distributeWonders( StackPane sp, ToggleGroup side, String name) throws Exception {
        Random rand = new Random();
        int[] randoms = new int[4];
        randoms[0] = rand.nextInt(7); randoms[0]++;
        randoms[1] = rand.nextInt(7); randoms[1]++;
        while(randoms[0] == randoms[1]) {randoms[1] = rand.nextInt(7); randoms[1]++;}
        randoms[2] = rand.nextInt(7); randoms[2]++;
        while(randoms[2] == randoms[1] || randoms[0] == randoms[2]) {randoms[2] = rand.nextInt(7); randoms[2]++;}
        randoms[3] = rand.nextInt(7); randoms[3]++;
        while(randoms[3] == randoms[0] || randoms[3] == randoms[1] || randoms[3] == randoms[2]) {randoms[3] = rand.nextInt(7); randoms[3]++;}
        wb[0] = new WonderBoard( sp,randoms[0], side.getSelectedToggle().getUserData().toString(), name, 0);
        wb[1] = new WonderBoard( sp,randoms[1], side.getSelectedToggle().getUserData().toString(), "bot1", 1);
        wb[2] = new WonderBoard( sp,randoms[2], side.getSelectedToggle().getUserData().toString(), "bot2", 2);
        wb[3] = new WonderBoard( sp,randoms[3], side.getSelectedToggle().getUserData().toString(), "bot3", 3);
        // wonder animasyonları
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setNode(wb[0]);
        translateTransition.setByY(120);
        translateTransition.play();
        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setNode(wb[1]);
        translateTransition.setByY(-100);
        translateTransition.setByX(450);
        translateTransition.play();
        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setNode(wb[2]);
        translateTransition.setByY(-100);
        translateTransition.setByX(-450);
        translateTransition.play();
        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setNode(wb[3]);
        translateTransition.setByY(-260);
        translateTransition.play();
    }
    private class WonderBoard extends Pane {
        Text coinText, shieldText, battleText, vicPointText;
        int pNum;
        Background bg;
        VBox coinVB, shieldVB, battleVB, vicPointVB;
        public WonderBoard( StackPane sp, int wNumber, String side, String playerName, int pN) throws Exception{
            setMaxSize(400, 250);
            pNum = pN;
            bg = new Background( new BackgroundFill(Color.rgb(0,0,0,0.6), CornerRadii.EMPTY, Insets.EMPTY));
            Rectangle board = new Rectangle(400,250);
            board.setArcHeight(15);
            board.setArcWidth(15);
            getChildren().add(board);
            Text sideText = new Text(playerName);
            sideText.setFill(Color.WHITESMOKE);
            sideText.setFont(Font.font("Kalam", FontWeight.BOLD,15));
            sideText.setTranslateX(10);
            sideText.setTranslateY(20);
            getChildren().add(sideText);

            // wNumber is 1=Rhodos, 2=Alexandria, 3=Ephesos, 4=Babylon, 5=Olympia, 6=Halikarnassos, 7=Gizah
            if( wNumber == 1) {
                InputStream is = Files.newInputStream(Paths.get("images/setname.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));

                Text sideText2 = new Text("Rhodos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(300);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
            }
            else if( wNumber == 2) {
                InputStream is = Files.newInputStream(Paths.get("images/alexandria.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));

                Text sideText2 = new Text("Alexandria - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(280);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
            }
            else if( wNumber == 3) {
                InputStream is = Files.newInputStream(Paths.get("images/gamepage.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));

                Text sideText2 = new Text("Ephesos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
            }
            else if( wNumber == 6) {
                InputStream is = Files.newInputStream(Paths.get("images/halikarnassos.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));

                Text sideText2 = new Text("Halikarnassos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(250);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
            }
            // coin part
            InputStream is = Files.newInputStream(Paths.get("images/coins.png"));
            Image img = new Image(is);
            is.close();
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(35);
            imgView.setFitWidth(35);
            coinText = new Text(coin[pNum] + "");
            coinText.setFill(Color.WHITESMOKE);
            coinText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            coinText.setTranslateX(5);
            coinVB = new VBox(imgView, coinText);
            coinVB.setBackground(bg);
            coinVB.setTranslateY(30);
            coinVB.setTranslateX(5);

            // shield part
            is = Files.newInputStream(Paths.get("images/shield.png"));
            img = new Image(is);
            is.close();
            imgView = new ImageView(img);
            imgView.setFitHeight(35);
            imgView.setFitWidth(35);
            shieldText = new Text(shield[pNum] + "");
            shieldText.setFill(Color.WHITESMOKE);
            shieldText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            shieldText.setTranslateX(5);
            shieldVB = new VBox(imgView, shieldText);
            shieldVB.setBackground(bg);
            shieldVB.setTranslateY(30);
            shieldVB.setTranslateX(45);

            // battle points part
            is = Files.newInputStream(Paths.get("images/helmet.png"));
            img = new Image(is);
            is.close();
            imgView = new ImageView(img);
            imgView.setFitHeight(35);
            imgView.setFitWidth(35);
            battleText = new Text(battle[pNum] + "");
            battleText.setFill(Color.WHITESMOKE);
            battleText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            battleText.setTranslateX(5);
            battleVB = new VBox(imgView, battleText);
            battleVB.setBackground(bg);
            battleVB.setTranslateY(30);
            battleVB.setTranslateX(85);

            // victory points part
            is = Files.newInputStream(Paths.get("images/award.png"));
            img = new Image(is);
            is.close();
            imgView = new ImageView(img);
            imgView.setFitHeight(35);
            imgView.setFitWidth(35);
            vicPointText = new Text(vicPoint[pNum] + "");
            vicPointText.setFill(Color.WHITESMOKE);
            vicPointText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            vicPointText.setTranslateX(5);
            vicPointVB = new VBox(imgView, vicPointText);
            vicPointVB.setBackground(bg);
            vicPointVB.setTranslateY(30);
            vicPointVB.setTranslateX(125);

            getChildren().addAll(shieldVB, coinVB, battleVB, vicPointVB);
        }
        public void makeChanges(){
            getChildren().remove(coinText);
            coinText = new Text(coin[pNum] + "");
            coinText.setFill(Color.WHITESMOKE);
            coinText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
            getChildren().addAll(coinText);
        }
    }
}
