package sample;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.io.File;
import javafx.scene.Group;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

import static java.lang.Math.min;
import static java.lang.Math.max;

public class EndGamePage extends Scene {

    int[] total = {0,0,0,0};


    int[] endCoin ={0,0,0,0};


    int[] endScience = {0,0,0,0};


    int[] endSpecialPurple = {0,0,0,0};


    int[] endSpecialYellow = {0,0,0,0};

    int[ ] endSpecial = {0,0,0,0};



    public EndGamePage(StackPane sp,  GamePage.Player[] players, Stage window) throws Exception
    {
        super( sp, Main.primaryScreenBounds.getWidth(), Main.primaryScreenBounds.getHeight());

        InputStream is = Files.newInputStream(Paths.get("images/options.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(Main.primaryScreenBounds.getHeight());
        imgView.setFitWidth(Main.primaryScreenBounds.getWidth());
        sp.getChildren().add(imgView);


        //point from coin
        for (int i = 0; i < 4; i++)
            endCoin[i] = players[i].stats.coin / 3;
        //point from science


        for (int i = 0; i < 4; i++) {
            if(players[i].specialCards[16]){
                int mec = 0; int lit = 0; int geo = 0;
                int mecSci = sciencePointCalculator(players[i].stats.mechanic + 1 , players[i].stats.literature, players[i].stats.geometry);
                int litSci = sciencePointCalculator(players[i].stats.mechanic, players[i].stats.literature + 1, players[i].stats.geometry);
                int geoSci = sciencePointCalculator(players[i].stats.mechanic , players[i].stats.literature, players[i].stats.geometry + 1);

                endScience[i] =  max( mecSci , max( litSci , geoSci));
            }
            else
                endScience[i] = sciencePointCalculator(players[i].stats.mechanic, players[i].stats.literature, players[i].stats.geometry);
        }

        //workers guild special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[10]) {
                endSpecialPurple[i] += players[(i + 1) % 4].brownCards + players[(i + 3) % 4].brownCards;
            }
        }
        //craftsmen guild special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[11]) {
                endSpecialPurple[i] += (players[(i + 1) % 4].greyCards) * 2 + (players[(i + 3) % 4].greyCards) * 2;
            }
        }
        //traders guild special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[12]) {
                endSpecialPurple[i] += players[(i + 1) % 4].yellowCards + players[(i + 3) % 4].yellowCards;
            }
        }
        //philosophers guil special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[13]) {
                endSpecialPurple[i] += players[(i + 1) % 4].greenCards + players[(i + 3) % 4].greenCards;
            }
        }
        //spies guild special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[14]) {
                endSpecialPurple[i] += players[(i + 1) % 4].redCards + players[(i + 3) % 4].redCards;
            }
        }
        //magistrates guild special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[15]) {
                endSpecialPurple[i] += players[(i + 1) % 4].blueCards + players[(i + 3) % 4].blueCards;
            }
        }
        //haven special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[6]) {
                endSpecialYellow[i] += players[i].brownCards;
            }
        }
        //lighthouse special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[7]) {
                endSpecialYellow[i] += players[i].yellowCards;
            }
        }
        //chamber of commerce special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[8]) {
                endSpecialYellow[i] += (players[i].greyCards) * 2;
            }
        }
        //arena special
        for (int i = 0; i < 4; i++) {
            if (players[i].specialCards[9]) {
                endSpecialYellow[i] += players[i].milestoneDone;
            }
        }
        for (int i = 0; i < 4; i++)
            endSpecial[i] += endSpecialPurple[i] + endSpecialYellow[i];
        //total point
        for (int i = 0; i < 4; i++)
            total[i] = endCoin[i] + endScience[i] + endSpecialPurple[i] + endSpecialYellow[i] + players[0].stats.victoryPoint
                    + players[i].battlePoint;


        /*
        TableView table = new TableView();
        TableColumn firstNameCol = new TableColumn("First Name");
        TableColumn lastNameCol = new TableColumn("Last Name");
        TableColumn emailCol = new TableColumn("Email");
        table.setEditable(true);
        table.getColumns().addAll(firstNameCol,lastNameCol,emailCol);
        table.getColumns().addAll()

        sp.getChildren().addAll(table);
    */


        Text player0  = new Text(players[0].name);
        player0.setFill(Color.BLACK);
        player0.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        Text player1  = new Text(players[1].name);
        player1.setFill(Color.BLACK);
        player1.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        Text player2  = new Text(players[2].name);
        player2.setFill(Color.BLACK);
        player2.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        Text player3  = new Text(players[3].name);
        player3.setFill(Color.BLACK);
        player3.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        TextField player0name = new TextField(players[0].name);


        Text player0coin = new Text(players[0].stats.coin/3+"");
        player0coin.setTranslateX(0);
        player0coin.setFill(Color.YELLOW);
        player0coin.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player1coin = new Text(players[1].stats.coin/3+"");
        player1coin.setTranslateX(0);
        player1coin.setFill(Color.YELLOW);
        player1coin.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player2coin = new Text(players[2].stats.coin/3+"");
        player2coin.setTranslateX(0);
        player2coin.setFill(Color.YELLOW);
        player2coin.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player3coin = new Text(players[3].stats.coin/3+"");
        player3coin.setTranslateX(0);
        player3coin.setFill(Color.YELLOW);
        player3coin.setFont(Font.font("Kalam", FontPosture.ITALIC,20));



        Text player0battlePoints = new Text(players[0].battlePoint+"");
        player0battlePoints.setTranslateX(0);
        player0battlePoints.setFill(Color.RED);
        player0battlePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player1battlePoints = new Text(players[1].battlePoint+"");
        player1battlePoints.setTranslateX(0);
        player1battlePoints.setFill(Color.RED);
        player1battlePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player2battlePoints = new Text(players[2].battlePoint+"");
        player2battlePoints.setTranslateX(0);
        player2battlePoints.setFill(Color.RED);
        player2battlePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player3battlePoints = new Text(players[3].battlePoint+"");
        player3battlePoints.setTranslateX(0);
        player3battlePoints.setFill(Color.RED);
        player3battlePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));


        Text player0sciencePoints = new Text(endScience[0]+"");
        player0sciencePoints.setTranslateX(0);
        player0sciencePoints.setFill(Color.GREEN);
        player0sciencePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player1sciencePoints = new Text(endScience[1]+"");
        player1sciencePoints.setTranslateX(0);
        player1sciencePoints.setFill(Color.GREEN);
        player1sciencePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player2sciencePoints = new Text(endScience[2]+"");
        player2sciencePoints.setTranslateX(0);
        player2sciencePoints.setFill(Color.GREEN);
        player2sciencePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player3sciencePoints = new Text(endScience[3]+"");
        player3sciencePoints.setTranslateX(0);
        player3sciencePoints.setFill(Color.GREEN);
        player3sciencePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));








        Text player0specialcards = new Text(endSpecial[0]+"");
        player0specialcards.setTranslateX(0);
        player0specialcards.setFill(Color.PURPLE);
        player0specialcards.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player1specialcards = new Text(endSpecial[1]+"");
        player1specialcards.setTranslateX(0);
        player1specialcards.setFill(Color.PURPLE);
        player1specialcards.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player2specialcards = new Text(endSpecial[2]+"");
        player2specialcards.setTranslateX(0);
        player2specialcards.setFill(Color.PURPLE);
        player2specialcards.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player3specialcards = new Text(endSpecial[3]+"");
        player3specialcards.setTranslateX(0);
        player3specialcards.setFill(Color.PURPLE);
        player3specialcards.setFont(Font.font("Kalam", FontPosture.ITALIC,20));




        Text player0bluePoints = new Text(players[0].stats.victoryPoint+"");
        player0bluePoints.setTranslateX(0);
        player0bluePoints.setFill(Color.BLUE);
        player0bluePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player1bluePoints = new Text(players[1].stats.victoryPoint+"");
        player1bluePoints.setTranslateX(0);
        player1bluePoints.setFill(Color.BLUE);
        player1bluePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player2bluePoints = new Text(players[2].stats.victoryPoint+"");
        player2bluePoints.setTranslateX(0);
        player2bluePoints.setFill(Color.BLUE);
        player2bluePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player3bluePoints = new Text(players[3].stats.victoryPoint+"");
        player3bluePoints.setTranslateX(0);
        player3bluePoints.setFill(Color.BLUE);
        player3bluePoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));




        Text player0totalPoints = new Text(total[0]+"");
        player0totalPoints.setTranslateX(00);
        player0totalPoints.setFill(Color.WHITESMOKE);
        player0totalPoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player1totalPoints = new Text(total[1]+"");
        player1totalPoints.setTranslateX(00);
        player1totalPoints.setFill(Color.WHITESMOKE);
        player1totalPoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player2totalPoints = new Text(total[2]+"");
        player2totalPoints.setTranslateX(00);
        player2totalPoints.setFill(Color.WHITESMOKE);
        player2totalPoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));

        Text player3totalPoints = new Text(total[3]+"");
        player3totalPoints.setTranslateX(00);
        player3totalPoints.setFill(Color.WHITESMOKE);
        player3totalPoints.setFont(Font.font("Kalam", FontPosture.ITALIC,20));






        Text nameText = new Text("Player Names");
        nameText.setFill(Color.BLACK);
        nameText.setFont(Font.font("Kalam", FontPosture.REGULAR,20));


        Text coinText =  new Text("Coin Points");
        coinText.setFill(Color.YELLOW);
        coinText.setFont(Font.font("Kalam", FontPosture.REGULAR,20));


        Text battlePointsText = new Text("Battle Points");
        battlePointsText.setFill(Color.RED);
        battlePointsText.setFont(Font.font("Kalam", FontPosture.REGULAR,20));


        Text sciencePointsText = new Text("Science Points");
        sciencePointsText.setFill(Color.GREEN);
        sciencePointsText.setFont(Font.font("Kalam", FontPosture.REGULAR,20));


        Text specialcardPointsText = new Text("Special Card Points");
        specialcardPointsText.setFill(Color.PURPLE);
        specialcardPointsText.setFont(Font.font("Kalam", FontPosture.REGULAR,20));


        Text bluePoints = new Text("Blue Card Points");
        bluePoints.setFill(Color.BLUE);
        bluePoints.setFont(Font.font("Kalam", FontPosture.REGULAR,20));



        Text totalPointsText = new Text("Total Points");
        totalPointsText.setFill(Color.WHITESMOKE);
        totalPointsText.setFont(Font.font("Kalam", FontPosture.REGULAR,20));





        VBox namesbox = new VBox(nameText,player0,player1,player2,player3);
        namesbox.setSpacing(30);
        VBox coinsbox = new VBox(coinText,player0coin,player1coin,player2coin,player3coin);
        coinsbox.setSpacing(30);
        VBox battlepointbox = new VBox(battlePointsText,player0battlePoints,player1battlePoints,player2battlePoints,player3battlePoints);
        battlepointbox.setSpacing(30);
        VBox sciencepointbox = new VBox(sciencePointsText,player0sciencePoints,player1sciencePoints,player2sciencePoints,player3sciencePoints);
        sciencepointbox.setSpacing(30);
        VBox specialpointbox = new VBox(specialcardPointsText,player0specialcards,player1specialcards,player2specialcards,player3specialcards);
        specialpointbox.setSpacing(30);
        VBox bluepointbox = new VBox(bluePoints,player0bluePoints,player1bluePoints,player2bluePoints,player3bluePoints);
        bluepointbox.setSpacing(30);
        VBox totalpointbox = new VBox(totalPointsText,player0totalPoints,player1totalPoints,player2totalPoints,player3totalPoints);
        totalpointbox.setSpacing(30);

        HBox boxbox = new HBox(namesbox,coinsbox,battlepointbox,sciencepointbox,specialpointbox,bluepointbox,totalpointbox);
        boxbox.setSpacing(100);


/*
        HBox statsBox = new HBox(nameText,coinText,battlePointsText,sciencePointsText,specialcardPointsText,bluePoints,totalPointsText);
        statsBox.setSpacing(30);

        HBox player0box = new HBox(player0,player0coin,player0battlePoints,player0sciencePoints,player0specialcards,player0bluePoints,player0totalPoints);
        player0box.setSpacing(20);
        player0box.setTranslateY(100);

        HBox player1box = new HBox(player1,player1coin,player1battlePoints,player1sciencePoints,player1specialcards,player1bluePoints,player1totalPoints);
        player1box.setTranslateY(200);
        player1box.setSpacing(30);


        HBox player2box = new HBox(player2,player2coin,player2battlePoints,player2sciencePoints,player2specialcards,player2bluePoints,player2totalPoints);
        player2box.setTranslateY(300);
        player2box.setSpacing(30);

        HBox player3box = new HBox(player3,player3coin,player3battlePoints,player3sciencePoints,player3specialcards,player3bluePoints,player3totalPoints);
        player3box.setTranslateY(400);
        player3box.setSpacing(20);



 */
        //sp.getChildren().addAll(statsBox,player0box,player1box,player2box,player3box);


       // sp.getChildren().addAll(namesbox,coinsbox,battlepointbox,sciencepointbox,specialpointbox,bluepointbox,totalpointbox);
        sp.getChildren().addAll(boxbox);

    }


    public int sciencePointCalculator(int a, int b, int c){
        int m = 0;
        for( int i = 0; i < 4; i++ ) {
            m += a * a + b * b + c* c;
        }
        int l = min( a, min(b, c));
        m += 7 * l;
        return m;


    }



}
