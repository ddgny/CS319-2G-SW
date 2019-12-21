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

public class GamePage extends Scene {
    public static WonderBoard[] wb;
    public static Player[] players;
    private Card[][] cards;
    private Card[] cardsAtStake;
    private Stage window;
    private StackPane sp;
    private String side;
    Text goldValue;
    Text goldValueRight;
    private int currentAge, currentTurn, noOfCardsAtStake;
    // mode = ally -> -1 , normal -> 0 , story -> 1,2,3,4,5...
    private int mode;
    private class Resource {
        String[] name;
        int[] quantity;
        public Resource( int optional) {
            name = new String[optional];
            quantity = new int[optional];
        }
        public Resource( Resource dummy) {
            this.name = new String[dummy.name.length];
            this.quantity = new int[dummy.quantity.length];
            for( int i = 0; i < this.quantity.length; i++) {
                this.name[i] = dummy.name[i];
                this.quantity[i] = dummy.quantity[i];
            }
        }
    }
    public  class Property {
        int coin, shield, mechanic, literature, geometry, victoryPoint;
        String requiredBuilding;
        Resource resource;
        int specialCard;
        public Property(){
            coin = shield = mechanic = literature = geometry = victoryPoint = specialCard = 0;
            requiredBuilding = "";
            resource = new Resource(0);
        }
    }
    private class Player {
        String name;
        int battlePoint, greenCards, redCards, yellowCards, greyCards, purpleCards, brownCards, blueCards, milestoneDone;
        String[] buildings;
        Resource[] resources;
        int resourceCount, buildingsCount;
        boolean[] specialCards;
        Property stats;

        public Player(String tmp) {
            stats = new Property();
            stats.coin = 3;
            name = tmp;
            battlePoint = greenCards = redCards = yellowCards = greyCards = purpleCards = brownCards = blueCards = milestoneDone = buildingsCount = resourceCount = 0;
            buildings = new String[22];
            resources = new Resource[22];
            specialCards = new boolean[22];
            for(int i = 0; i < 22; i++) specialCards[i] = false;
        }
        void addResource( Resource add) {
            resources[resourceCount] = add;
            resourceCount++;
        }

        /**
         * Play for a player randomly
         * @param playerNum player index
         */
        void randomPlay(int playerNum) throws Exception { //TODO you should not use playerNum here!!!
            int baseCardNum = ((currentTurn + playerNum - 1) % 4) * 7;
            boolean tmpResult = false;

            //create deck
            ArrayList<Integer> deck = new ArrayList<>();
            for (int i = 0; i < 7; i++) deck.add(baseCardNum + i);
            Collections.shuffle(deck);

            //first, try bury for all cards, randomly
            for (int i = 0; i < deck.size() && !(tmpResult = cards[currentAge-1][deck.get(i)].playCard(playerNum, CardAction.BURY)); i++);

            //if no success
            if (!tmpResult) {
                if (players[playerNum].stats.coin < 5) // and coin<5, sell random card
                    for (int i = 0; i < deck.size() && !(tmpResult = cards[currentAge-1][deck.get(i)].playCard(playerNum, CardAction.SELL)); i++);
                else // and coin>5, try build all cards, randomly
                    for (int i = 0; i < deck.size() && !(tmpResult = cards[currentAge-1][deck.get(i)].playCard(playerNum, CardAction.BUILD)); i++);
            }

            //if no success and no success, sell random card
            if (!tmpResult)
                for (int i = 0; i < deck.size() && !(tmpResult = cards[currentAge-1][deck.get(i)].playCard(playerNum, CardAction.SELL)); i++);

            System.out.println(String.format("Player %d, made %sa move", playerNum, tmpResult ? "" : " not"));
        }

    }


    public GamePage(StackPane sp, Scene mainmenu, Stage window, String name, ToggleGroup side, int sMode) throws Exception {
        super(sp, Main.primaryScreenBounds.getWidth(), Main.primaryScreenBounds.getHeight());
        mode = sMode;
        noOfCardsAtStake = 0;
        cardsAtStake = new Card[40];
        currentAge = currentTurn = 1;
        this.window = window;
        this.sp = sp;
        this.side = side.getSelectedToggle().getUserData().toString();
        wb = new WonderBoard[4];
        players = new Player[4];
        cards = new Card[3][28];
        InputStream is = Files.newInputStream(Paths.get("images/gamepage.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(Main.primaryScreenBounds.getHeight());
        imgView.setFitWidth(Main.primaryScreenBounds.getWidth());
        sp.getChildren().add(imgView);


        //TRADE COMBO BOX

        //images for hboxes
        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboGold = new ImageView(img);
        imgComboGold.setFitWidth(20);
        imgComboGold.setFitHeight(20);
        imgComboGold.setTranslateX(25);

        is = Files.newInputStream(Paths.get("images/Lumber.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboLumber = new ImageView(img);
        imgComboLumber.setFitHeight(20);
        imgComboLumber.setFitWidth(20);
        imgComboLumber.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/ore.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboOre = new ImageView(img);
        imgComboOre.setFitHeight(20);
        imgComboOre.setFitWidth(20);
        imgComboOre.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Clay.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboClay = new ImageView(img);
        imgComboClay.setFitHeight(20);
        imgComboClay.setFitWidth(20);
        imgComboClay.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Paper.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboPaper = new ImageView(img);
        imgComboPaper.setFitHeight(20);
        imgComboPaper.setFitWidth(20);
        imgComboPaper.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Glass.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboGlass = new ImageView(img);
        imgComboGlass.setFitHeight(20);
        imgComboGlass.setFitWidth(20);
        imgComboGlass.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Textile.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboTextile = new ImageView(img);
        imgComboTextile.setFitHeight(20);
        imgComboTextile.setFitWidth(20);
        imgComboTextile.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Stone.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboStone  = new ImageView(img);
        imgComboStone.setFitHeight(20);
        imgComboStone.setFitWidth(20);
        imgComboStone.setTranslateX(10);


        HBox lumberhbox,orebox,stonebox,claybox,glassbox,textilebox,paperbox;

        //texts and hboxes
        Text lumberText = new Text("Lumber");
        lumberText.setTranslateX(5);
        lumberText.setFill(Color.BROWN);
        lumberText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        lumberhbox = new HBox(lumberText,imgComboLumber);
        lumberhbox.setPrefSize(10,10);

        Text oreText = new Text("Ore");
        oreText.setTranslateX(5);
        oreText.setFill(Color.BLACK);
        oreText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        orebox = new HBox(oreText,imgComboOre);
        orebox.setPrefSize(10,10);

        Text stoneText = new Text("Stone");
        stoneText.setTranslateX(5);
        stoneText.setFill(Color.GREY);
        stoneText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        stonebox = new HBox(stoneText,imgComboStone);
        stonebox.setPrefSize(10,10);

        Text clayText = new Text("Clay");
        clayText.setTranslateX(5);
        clayText.setFill(Color.RED);
        clayText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        claybox = new HBox(clayText,imgComboClay);
        claybox.setPrefSize(10,10);

        Text glassText = new Text("Glass");
        glassText.setTranslateX(5);
        glassText.setFill(Color.BLUE);
        glassText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        glassbox = new HBox(glassText,imgComboGlass);
        glassbox.setPrefSize(10,10);


        Text paperText = new Text("Paper");
        paperText.setTranslateX(5);
        paperText.setFill(Color.rgb(172,118,75,1));
        paperText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        paperbox = new HBox(paperText,imgComboPaper);
        paperbox.setPrefSize(10,10);

        Text textileText = new Text("Textile");
        textileText.setTranslateX(5);
        textileText.setFill(Color.PURPLE);
        textileText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        textilebox = new HBox(textileText,imgComboTextile);
        textilebox.setPrefSize(10,10);


        ComboBox<HBox> comboHbox = new ComboBox<HBox>();
        comboHbox.setTranslateX(-500);
        comboHbox.setTranslateY(-300);
        comboHbox.getItems().addAll(lumberhbox,orebox,stonebox,claybox,glassbox,textilebox,paperbox);
        comboHbox.getSelectionModel().selectFirst();



        //making trade button
        Button comboBoxTradeButton = new Button("Trade");
        comboBoxTradeButton.setTranslateX(-600);
        comboBoxTradeButton.setTranslateY(-300);





        comboHbox.setOnMouseClicked(event -> {
            comboBoxTradeButton.setDisable(false);//enable button
            String temporarytwo = ((Text)comboHbox.getValue().getChildren().get(0)).getText();
            if (temporarytwo.equals("Lumber"))
            {
                comboHbox.getItems().remove(orebox);
                comboHbox.getItems().add(orebox);
                comboHbox.getItems().remove(stonebox);
                comboHbox.getItems().add(stonebox);
                comboHbox.getItems().remove(claybox);
                comboHbox.getItems().add(claybox);
                //comboHbox.getItems().remove(lumberhbox);
                //comboHbox.getItems().add(lumberhbox);
                comboHbox.getItems().remove(textilebox);
                comboHbox.getItems().add(textilebox);
                comboHbox.getItems().remove(glassbox);
                comboHbox.getItems().add(glassbox);
                comboHbox.getItems().remove(paperbox);
                comboHbox.getItems().add(paperbox);
            }
            if (temporarytwo.equals("Ore"))
            {
                //comboHbox.getItems().remove(orebox);
                //comboHbox.getItems().add(orebox);
                comboHbox.getItems().remove(stonebox);
                comboHbox.getItems().add(stonebox);
                comboHbox.getItems().remove(claybox);
                comboHbox.getItems().add(claybox);
                comboHbox.getItems().remove(lumberhbox);
                comboHbox.getItems().add(lumberhbox);
                comboHbox.getItems().remove(textilebox);
                comboHbox.getItems().add(textilebox);
                comboHbox.getItems().remove(glassbox);
                comboHbox.getItems().add(glassbox);
                comboHbox.getItems().remove(paperbox);
                comboHbox.getItems().add(paperbox);
            }

            if (temporarytwo.equals("Clay"))
            {
                comboHbox.getItems().remove(orebox);
                comboHbox.getItems().add(orebox);
                comboHbox.getItems().remove(stonebox);
                comboHbox.getItems().add(stonebox);
                //comboHbox.getItems().remove(claybox);
                //comboHbox.getItems().add(claybox);
                comboHbox.getItems().remove(lumberhbox);
                comboHbox.getItems().add(lumberhbox);
                comboHbox.getItems().remove(textilebox);
                comboHbox.getItems().add(textilebox);
                comboHbox.getItems().remove(glassbox);
                comboHbox.getItems().add(glassbox);
                comboHbox.getItems().remove(paperbox);
                comboHbox.getItems().add(paperbox);
            }

            if (temporarytwo.equals("Stone"))
            {
                comboHbox.getItems().remove(orebox);
                comboHbox.getItems().add(orebox);
               //comboHbox.getItems().remove(stonebox);
                //comboHbox.getItems().add(stonebox);
                comboHbox.getItems().remove(claybox);
                comboHbox.getItems().add(claybox);
                comboHbox.getItems().remove(lumberhbox);
                comboHbox.getItems().add(lumberhbox);
                comboHbox.getItems().remove(textilebox);
                comboHbox.getItems().add(textilebox);
                comboHbox.getItems().remove(glassbox);
                comboHbox.getItems().add(glassbox);
                comboHbox.getItems().remove(paperbox);
                comboHbox.getItems().add(paperbox);
            }
            if (temporarytwo.equals("Glass"))
            {
                comboHbox.getItems().remove(orebox);
                comboHbox.getItems().add(orebox);
                comboHbox.getItems().remove(stonebox);
                comboHbox.getItems().add(stonebox);
                comboHbox.getItems().remove(claybox);
                comboHbox.getItems().add(claybox);
                comboHbox.getItems().remove(lumberhbox);
                comboHbox.getItems().add(lumberhbox);
                comboHbox.getItems().remove(textilebox);
                comboHbox.getItems().add(textilebox);
                //comboHbox.getItems().remove(glassbox);
                //comboHbox.getItems().add(glassbox);
                comboHbox.getItems().remove(paperbox);
                comboHbox.getItems().add(paperbox);
            }
            if (temporarytwo.equals("Paper"))
            {
                comboHbox.getItems().remove(orebox);
                comboHbox.getItems().add(orebox);
                comboHbox.getItems().remove(stonebox);
                comboHbox.getItems().add(stonebox);
                comboHbox.getItems().remove(claybox);
                comboHbox.getItems().add(claybox);
                comboHbox.getItems().remove(lumberhbox);
                comboHbox.getItems().add(lumberhbox);
                comboHbox.getItems().remove(textilebox);
                comboHbox.getItems().add(textilebox);
                comboHbox.getItems().remove(glassbox);
                comboHbox.getItems().add(glassbox);
                //comboHbox.getItems().remove(paperbox);
                //comboHbox.getItems().add(paperbox);
            }

        });


        comboHbox.getSelectionModel().selectedItemProperty().addListener((v,oldvalue,newValue) -> {
            String checkType= ((Text)newValue.getChildren().get(0)).getText();

            System.out.println(checkType);
            if (checkType.equals("Ore"))
            {
                if (players[0].specialCards[2]==true)
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 1");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                    //checkForTrade("a");
                }
                else
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 2");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
            }
            else if (checkType.equals("Lumber"))
            {
                if (players[0].specialCards[2]==true)
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 1");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
                else
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 2");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
            }
            else if (checkType.equals("Clay"))
            {
                if (players[0].specialCards[2]==true)
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 1");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
                else
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 2");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
            }
            else if (checkType.equals("Stone"))
            {
                if (players[0].specialCards[2]==true)
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 1");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
                else
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 2");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
            }
            else if (checkType.equals("Glass"))
            {
                if (players[0].specialCards[3]==true)
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 1");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
                else
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 2");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
            }
            else if (checkType.equals("Paper"))
            {
                if (players[0].specialCards[3]==true)
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 1");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
                else
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 2");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
            }
            else if (checkType.equals("Textile"))
            {
                if (players[0].specialCards[3]==true)
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 1");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
                else
                {
                    sp.getChildren().remove(goldValue);
                    goldValue = new Text("for 2");
                    goldValue.setFill(Color.WHITESMOKE);
                    goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValue.setTranslateX(-410);
                    goldValue.setTranslateY(-300);
                    sp.getChildren().addAll(goldValue);
                }
            }




        });


        comboBoxTradeButton.setOnMouseClicked(event -> {
            String choice = ((Text)comboHbox.getValue().getChildren().get(0)).getText();

            if (choice.equals("Ore"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Clay"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Lumber"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Stone"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Glass"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Paper"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Textile"))
            {
                System.out.println(choice);
                //trade choice
            }
        });
        comboBoxTradeButton.setDisable(true);

        int worth=2;
        //adding trade gold image
        goldValue = new Text( "for 2");
        goldValue.setFill(Color.WHITESMOKE);
        goldValue.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        goldValue.setTranslateX(-410);
        goldValue.setTranslateY(-300);
        sp.getChildren().addAll(goldValue);
        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        ImageView imgGold = new ImageView(img);
        imgGold.setFitHeight(25);
        imgGold.setFitWidth(25);
        imgGold.setTranslateX(-380);
        imgGold.setTranslateY(-300);
        sp.getChildren().addAll(imgGold);





        sp.getChildren().addAll(comboBoxTradeButton,comboHbox);


        //Trade with right


        goldValueRight = new Text( "for 2");
        goldValueRight.setFill(Color.WHITESMOKE);
        goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        goldValueRight.setTranslateX(580);
        goldValueRight.setTranslateY(-300);
        sp.getChildren().addAll(goldValueRight);
        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        ImageView imgGoldRight = new ImageView(img);
        imgGoldRight.setFitHeight(25);
        imgGoldRight.setFitWidth(25);
        imgGoldRight.setTranslateX(610);
        imgGoldRight.setTranslateY(-300);
        sp.getChildren().addAll(imgGoldRight);

        HBox lumberhboxright,oreboxright,stoneboxright,clayboxright,glassboxright,textileboxright,paperboxright;



        //images for hboxes
        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboGoldright = new ImageView(img);
        imgComboGoldright.setFitWidth(20);
        imgComboGoldright.setFitHeight(20);
        imgComboGoldright.setTranslateX(25);

        is = Files.newInputStream(Paths.get("images/Lumber.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboLumberright = new ImageView(img);
        imgComboLumberright.setFitHeight(20);
        imgComboLumberright.setFitWidth(20);
        imgComboLumberright.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/ore.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboOreright = new ImageView(img);
        imgComboOreright.setFitHeight(20);
        imgComboOreright.setFitWidth(20);
        imgComboOreright.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Clay.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboClayright = new ImageView(img);
        imgComboClayright.setFitHeight(20);
        imgComboClayright.setFitWidth(20);
        imgComboClayright.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Paper.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboPaperright = new ImageView(img);
        imgComboPaperright.setFitHeight(20);
        imgComboPaperright.setFitWidth(20);
        imgComboPaperright.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Glass.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboGlassright = new ImageView(img);
        imgComboGlassright.setFitHeight(20);
        imgComboGlassright.setFitWidth(20);
        imgComboGlassright.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Textile.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboTextileright = new ImageView(img);
        imgComboTextileright.setFitHeight(20);
        imgComboTextileright.setFitWidth(20);
        imgComboTextileright.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Stone.png"));
        img = new Image(is);
        is.close();
        ImageView imgComboStoneright  = new ImageView(img);
        imgComboStoneright.setFitHeight(20);
        imgComboStoneright.setFitWidth(20);
        imgComboStoneright.setTranslateX(10);


        //texts
        Text lumberTextRight = new Text("Lumber");
        lumberTextRight.setTranslateX(5);
        lumberTextRight.setFill(Color.BROWN);
        lumberTextRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        lumberhboxright = new HBox(lumberTextRight,imgComboLumberright);
        lumberhboxright.setPrefSize(10,10);

        Text oreTextRight = new Text("Ore");
        oreTextRight.setTranslateX(5);
        oreTextRight.setFill(Color.BLACK);
        oreTextRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        oreboxright = new HBox(oreTextRight,imgComboOreright);
        oreboxright.setPrefSize(10,10);

        Text stoneTextRight = new Text("Stone");
        stoneTextRight.setTranslateX(5);
        stoneTextRight.setFill(Color.GREY);
        stoneTextRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        stoneboxright = new HBox(stoneTextRight,imgComboStoneright);
        stoneboxright.setPrefSize(10,10);

        Text clayTextRight = new Text("Clay");
        clayTextRight.setTranslateX(5);
        clayTextRight.setFill(Color.RED);
        clayTextRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        clayboxright = new HBox(clayTextRight,imgComboClayright);
        clayboxright.setPrefSize(10,10);

        Text glassTextRight = new Text("Glass");
        glassTextRight.setTranslateX(5);
        glassTextRight.setFill(Color.BLUE);
        glassTextRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        glassboxright = new HBox(glassTextRight,imgComboGlassright);
        glassboxright.setPrefSize(10,10);

        Text paperTextRight = new Text("Paper");
        paperTextRight.setTranslateX(5);
        paperTextRight.setFill(Color.rgb(172,118,75,1));
        paperTextRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        paperboxright = new HBox(paperTextRight,imgComboPaperright);
        paperboxright.setPrefSize(10,10);


        Text textileTextRight = new Text("Textile");
        textileTextRight.setTranslateX(5);
        textileTextRight.setFill(Color.PURPLE);
        textileTextRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
        textileboxright = new HBox(textileTextRight,imgComboTextileright);
        textileboxright.setPrefSize(10,10);


        ComboBox<HBox> comboHboxRight = new ComboBox<HBox>();
        comboHboxRight.setTranslateX(500);
        comboHboxRight.setTranslateY(-300);
        comboHboxRight.getItems().addAll(lumberhboxright,oreboxright,stoneboxright,clayboxright,glassboxright,textileboxright,paperboxright);
        comboHboxRight.getSelectionModel().selectFirst();



        //making trade button
        Button comboBoxTradeButtonRight = new Button("Trade");
        comboBoxTradeButtonRight.setTranslateX(410);
        comboBoxTradeButtonRight.setTranslateY(-300);





        comboHboxRight.setOnMouseClicked(event -> {
            comboBoxTradeButtonRight.setDisable(false);//enable button
            String topresource = ((Text)comboHboxRight.getValue().getChildren().get(0)).getText();
            if (topresource.equals("Lumber"))
            {
                comboHboxRight.getItems().remove(oreboxright);
                comboHboxRight.getItems().add(oreboxright);
                comboHboxRight.getItems().remove(stoneboxright);
                comboHboxRight.getItems().add(stoneboxright);
                comboHboxRight.getItems().remove(clayboxright);
                comboHboxRight.getItems().add(clayboxright);
                //comboHboxRight.getItems().remove(lumberhboxright);
                //comboHboxRight.getItems().add(lumberhboxright);
                comboHboxRight.getItems().remove(textileboxright);
                comboHboxRight.getItems().add(textileboxright);
                comboHboxRight.getItems().remove(glassboxright);
                comboHboxRight.getItems().add(glassboxright);
                comboHboxRight.getItems().remove(paperboxright);
                comboHboxRight.getItems().add(paperboxright);
            }
            if (topresource.equals("Ore"))
            {
                //comboHboxRight.getItems().remove(oreboxright);
                //comboHboxRight.getItems().add(oreboxright);
                comboHboxRight.getItems().remove(stoneboxright);
                comboHboxRight.getItems().add(stoneboxright);
                comboHboxRight.getItems().remove(clayboxright);
                comboHboxRight.getItems().add(clayboxright);
                comboHboxRight.getItems().remove(lumberhboxright);
                comboHboxRight.getItems().add(lumberhboxright);
                comboHboxRight.getItems().remove(textileboxright);
                comboHboxRight.getItems().add(textileboxright);
                comboHboxRight.getItems().remove(glassboxright);
                comboHboxRight.getItems().add(glassboxright);
                comboHboxRight.getItems().remove(paperboxright);
                comboHboxRight.getItems().add(paperboxright);
            }

            if (topresource.equals("Clay"))
            {
                comboHboxRight.getItems().remove(oreboxright);
                comboHboxRight.getItems().add(oreboxright);
                comboHboxRight.getItems().remove(stoneboxright);
                comboHboxRight.getItems().add(stoneboxright);
                //comboHboxRight.getItems().remove(clayboxright);
                //comboHboxRight.getItems().add(clayboxright);
                comboHboxRight.getItems().remove(lumberhboxright);
                comboHboxRight.getItems().add(lumberhboxright);
                comboHboxRight.getItems().remove(textileboxright);
                comboHboxRight.getItems().add(textileboxright);
                comboHboxRight.getItems().remove(glassboxright);
                comboHboxRight.getItems().add(glassboxright);
                comboHboxRight.getItems().remove(paperboxright);
                comboHboxRight.getItems().add(paperboxright);
            }

            if (topresource.equals("Stone"))
            {
                comboHboxRight.getItems().remove(oreboxright);
                comboHboxRight.getItems().add(oreboxright);
                //comboHboxRight.getItems().remove(stoneboxright);
                //comboHboxRight.getItems().add(stoneboxright);
                comboHboxRight.getItems().remove(clayboxright);
                comboHboxRight.getItems().add(clayboxright);
                comboHboxRight.getItems().remove(lumberhboxright);
                comboHboxRight.getItems().add(lumberhboxright);
                comboHboxRight.getItems().remove(textileboxright);
                comboHboxRight.getItems().add(textileboxright);
                comboHboxRight.getItems().remove(glassboxright);
                comboHboxRight.getItems().add(glassboxright);
                comboHboxRight.getItems().remove(paperboxright);
                comboHboxRight.getItems().add(paperboxright);
            }
            if (topresource.equals("Glass"))
            {
                comboHboxRight.getItems().remove(oreboxright);
                comboHboxRight.getItems().add(oreboxright);
                comboHboxRight.getItems().remove(stoneboxright);
                comboHboxRight.getItems().add(stoneboxright);
                comboHboxRight.getItems().remove(clayboxright);
                comboHboxRight.getItems().add(clayboxright);
                comboHboxRight.getItems().remove(lumberhboxright);
                comboHboxRight.getItems().add(lumberhboxright);
                comboHboxRight.getItems().remove(textileboxright);
                comboHboxRight.getItems().add(textileboxright);
                //comboHboxRight.getItems().remove(glassboxright);
                //comboHboxRight.getItems().add(glassboxright);
                comboHboxRight.getItems().remove(paperboxright);
                comboHboxRight.getItems().add(paperboxright);
            }
            if (topresource.equals("Paper"))
            {
                comboHboxRight.getItems().remove(oreboxright);
                comboHboxRight.getItems().add(oreboxright);
                comboHboxRight.getItems().remove(stoneboxright);
                comboHboxRight.getItems().add(stoneboxright);
                comboHboxRight.getItems().remove(clayboxright);
                comboHboxRight.getItems().add(clayboxright);
                comboHboxRight.getItems().remove(lumberhboxright);
                comboHboxRight.getItems().add(lumberhboxright);
                comboHboxRight.getItems().remove(textileboxright);
                comboHboxRight.getItems().add(textileboxright);
                comboHboxRight.getItems().remove(glassboxright);
                comboHboxRight.getItems().add(glassboxright);
               // comboHboxRight.getItems().remove(paperboxright);
                //comboHboxRight.getItems().add(paperboxright);
            }
            if (topresource.equals("Textile"))
            {
                comboHboxRight.getItems().remove(oreboxright);
                comboHboxRight.getItems().add(oreboxright);
                comboHboxRight.getItems().remove(stoneboxright);
                comboHboxRight.getItems().add(stoneboxright);
                comboHboxRight.getItems().remove(clayboxright);
                comboHboxRight.getItems().add(clayboxright);
                comboHboxRight.getItems().remove(lumberhboxright);
                comboHboxRight.getItems().add(lumberhboxright);
                //comboHboxRight.getItems().remove(textileboxright);
                //comboHboxRight.getItems().add(textileboxright);
                comboHboxRight.getItems().remove(glassboxright);
                comboHboxRight.getItems().add(glassboxright);
                comboHboxRight.getItems().remove(paperboxright);
                comboHboxRight.getItems().add(paperboxright);
            }


        });


        comboHboxRight.getSelectionModel().selectedItemProperty().addListener((v,oldvalue,newValue) -> {
            String checkType= ((Text)newValue.getChildren().get(0)).getText();

            System.out.println(checkType);
            if (checkType.equals("Ore"))
            {
                if (players[0].specialCards[1]==true)
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 1");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                    //checkForTrade("a");
                }
                else
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 2");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                }
            }
            else if (checkType.equals("Lumber"))
            {
                if (players[0].specialCards[1]==true)
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 1");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                    //checkForTrade("a");
                }
                else
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 2");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                }
            }
            else if (checkType.equals("Clay"))
            {
                if (players[0].specialCards[1]==true)
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 1");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                    //checkForTrade("a");
                }
                else
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 2");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                }
            }
            else if (checkType.equals("Stone"))
            {
                if (players[0].specialCards[1]==true)
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 1");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                    //checkForTrade("a");
                }
                else
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 2");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                }
            }
            else if (checkType.equals("Glass"))
            {
                if (players[0].specialCards[3]==true)
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 1");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                    //checkForTrade("a");
                }
                else
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 2");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                }
            }
            else if (checkType.equals("Paper"))
            {
                if (players[0].specialCards[3]==true)
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 1");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                    //checkForTrade("a");
                }
                else
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 2");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                }
            }
            else if (checkType.equals("Textile"))
            {
                if (players[0].specialCards[3]==true)
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 1");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                    //checkForTrade("a");
                }
                else
                {
                    sp.getChildren().remove(goldValueRight);
                    goldValueRight = new Text("for 2");
                    goldValueRight.setFill(Color.WHITESMOKE);
                    goldValueRight.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
                    goldValueRight.setTranslateX(580);
                    goldValueRight.setTranslateY(-300);
                    sp.getChildren().addAll(goldValueRight);
                }
            }




        });


        comboBoxTradeButtonRight.setOnMouseClicked(event -> {
            String choice = ((Text)comboHboxRight.getValue().getChildren().get(0)).getText();

            if (choice.equals("Ore"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Clay"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Lumber"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Stone"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Glass"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Paper"))
            {
                System.out.println(choice);
                //trade choice
            }
            if (choice.equals("Textile"))
            {
                System.out.println(choice);
                //trade choice
            }
        });
        comboBoxTradeButtonRight.setDisable(true);
        sp.getChildren().addAll(comboBoxTradeButtonRight,comboHboxRight);




        // TESTING FOR TRADE
        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        ImageView imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);

        is = Files.newInputStream(Paths.get("images/Lumber.png"));
        img = new Image(is);
        is.close();
        ImageView imgLumber = new ImageView(img);
        imgLumber.setFitHeight(35);
        imgLumber.setFitWidth(35);
        imgLumber.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/ore.png"));
        img = new Image(is);
        is.close();
        ImageView imgOre = new ImageView(img);
        imgOre.setFitHeight(35);
        imgOre.setFitWidth(35);
        imgOre.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Clay.png"));
        img = new Image(is);
        is.close();
        ImageView imgClay = new ImageView(img);
        imgClay.setFitHeight(35);
        imgClay.setFitWidth(35);
        imgClay.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Paper.png"));
        img = new Image(is);
        is.close();
        ImageView imgPaper = new ImageView(img);
        imgPaper.setFitHeight(35);
        imgPaper.setFitWidth(35);
        imgPaper.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Glass.png"));
        img = new Image(is);
        is.close();
        ImageView imgGlass = new ImageView(img);
        imgGlass.setFitHeight(35);
        imgGlass.setFitWidth(35);
        imgGlass.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Textile.png"));
        img = new Image(is);
        is.close();
        ImageView imgTextile = new ImageView(img);
        imgTextile.setFitHeight(35);
        imgTextile.setFitWidth(35);
        imgTextile.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Stone.png"));
        img = new Image(is);
        is.close();
        ImageView imgStone  = new ImageView(img);
        imgStone.setFitHeight(35);
        imgStone.setFitWidth(35);
        imgStone.setTranslateX(10);

        Text ForText;
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);



        Button tradeLumberLeft = new Button("Trade");
        tradeLumberLeft.setTranslateX(5);
        tradeLumberLeft.setOnMouseClicked(event -> {
            System.out.println("tradeLumberLeft");
            players[0].stats.coin = players[0].stats.coin - 2;
            players[3].stats.coin = players[3].stats.coin + 2;
            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button tradeClayLeft = new Button("Trade");
        tradeClayLeft.setTranslateX(5);
        tradeClayLeft.setOnMouseClicked(event -> {
            System.out.println("tradeClayLeft");
            players[0].stats.coin = players[0].stats.coin - 2;
            players[3].stats.coin = players[3].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Button tradeOreLeft = new Button("Trade");
        tradeOreLeft.setTranslateX(5);
        tradeOreLeft.setOnMouseClicked(event -> {
            System.out.println("tradeOreLeft");
            players[0].stats.coin = players[0].stats.coin - 2;
            players[3].stats.coin = players[3].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Button tradeStoneLeft = new Button("Trade");
        tradeStoneLeft.setTranslateX(5);
        tradeStoneLeft.setOnMouseClicked(event -> {
            System.out.println("tradeStoneLeft");
            players[0].stats.coin = players[0].stats.coin - 2;
            players[3].stats.coin = players[3].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button tradeGlassLeft = new Button("Trade");
        tradeGlassLeft.setTranslateX(5);
        tradeGlassLeft.setOnMouseClicked(event -> {
            System.out.println("tradeGlassLeft");
            players[0].stats.coin = players[0].stats.coin - 2;
            players[3].stats.coin = players[3].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Button tradePaperLeft = new Button("Trade");
        tradePaperLeft.setTranslateX(5);
        tradePaperLeft.setOnMouseClicked(event -> {
            System.out.println("tradePaperLeft");
            players[0].stats.coin = players[0].stats.coin - 2;
            players[3].stats.coin = players[3].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Button tradeTextileLeft = new Button("Trade");
        tradeTextileLeft.setTranslateX(5);
        tradeTextileLeft.setOnMouseClicked(event -> {
            System.out.println("tradeTextileLeft");
            players[0].stats.coin = players[0].stats.coin - 2;
            players[3].stats.coin = players[3].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        HBox tradeLumberHBLeft,tradeClayHBLeft,tradeStoneHBLeft,tradeOreHBLeft,tradeGlassHBLeft,tradePaperHBLeft,tradeTextileHBLeft;

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeLumberHBLeft = new HBox(tradeLumberLeft,imgLumber,ForText,imgCoin);
        tradeLumberHBLeft.setTranslateY(270);
        tradeLumberHBLeft.setTranslateX(60);
        tradeLumberHBLeft.setPrefSize(60,60);


        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeClayHBLeft = new HBox(tradeClayLeft,imgClay,ForText,imgCoin);
        tradeClayHBLeft.setTranslateY(300);
        tradeClayHBLeft.setTranslateX(60);
        tradeClayHBLeft.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeOreHBLeft = new HBox(tradeOreLeft,imgOre,ForText,imgCoin);
        tradeOreHBLeft.setTranslateY(330);
        tradeOreHBLeft.setTranslateX(60);
        tradeOreHBLeft.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeStoneHBLeft = new HBox(tradeStoneLeft,imgStone,ForText,imgCoin);
        tradeStoneHBLeft.setTranslateY(360);
        tradeStoneHBLeft.setTranslateX(60);
        tradeStoneHBLeft.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeGlassHBLeft = new HBox(tradeGlassLeft,imgGlass,ForText,imgCoin);
        tradeGlassHBLeft.setTranslateY(390);
        tradeGlassHBLeft.setTranslateX(60);
        tradeGlassHBLeft.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradePaperHBLeft = new HBox(tradePaperLeft,imgPaper,ForText,imgCoin);
        tradePaperHBLeft.setTranslateY(420);
        tradePaperHBLeft.setTranslateX(60);
        tradePaperHBLeft.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeTextileHBLeft = new HBox(tradeTextileLeft,imgTextile,ForText,imgCoin);
        tradeTextileHBLeft.setTranslateY(450);
        tradeTextileHBLeft.setTranslateX(60);
        tradeTextileHBLeft.setPrefSize(60,60);



        sp.getChildren().addAll(tradeLumberHBLeft,tradeClayHBLeft,tradeOreHBLeft,tradeStoneHBLeft,tradeGlassHBLeft,tradePaperHBLeft,tradeTextileHBLeft);
        // TESTING FOR TRADE

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);

        is = Files.newInputStream(Paths.get("images/Lumber.png"));
        img = new Image(is);
        is.close();
        imgLumber = new ImageView(img);
        imgLumber.setFitHeight(35);
        imgLumber.setFitWidth(35);
        imgLumber.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/ore.png"));
        img = new Image(is);
        is.close();
        imgOre = new ImageView(img);
        imgOre.setFitHeight(35);
        imgOre.setFitWidth(35);
        imgOre.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Clay.png"));
        img = new Image(is);
        is.close();
        imgClay = new ImageView(img);
        imgClay.setFitHeight(35);
        imgClay.setFitWidth(35);
        imgClay.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Paper.png"));
        img = new Image(is);
        is.close();
        imgPaper = new ImageView(img);
        imgPaper.setFitHeight(35);
        imgPaper.setFitWidth(35);
        imgPaper.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Glass.png"));
        img = new Image(is);
        is.close();
        imgGlass = new ImageView(img);
        imgGlass.setFitHeight(35);
        imgGlass.setFitWidth(35);
        imgGlass.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Textile.png"));
        img = new Image(is);
        is.close();
        imgTextile = new ImageView(img);
        imgTextile.setFitHeight(35);
        imgTextile.setFitWidth(35);
        imgTextile.setTranslateX(10);

        is = Files.newInputStream(Paths.get("images/Stone.png"));
        img = new Image(is);
        is.close();
        imgStone  = new ImageView(img);
        imgStone.setFitHeight(35);
        imgStone.setFitWidth(35);
        imgStone.setTranslateX(10);

        Button tradeLumberRight = new Button("Trade");
        tradeLumberRight.setTranslateX(5);
        tradeLumberRight.setOnMouseClicked(event -> {
            players[0].stats.coin = players[0].stats.coin - 2;
            players[1].stats.coin = players[1].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("tradeLumberRight");
        });

        Button tradeClayRight = new Button("Trade");
        tradeClayRight.setTranslateX(5);
        tradeClayRight.setOnMouseClicked(event -> {
            players[0].stats.coin = players[0].stats.coin - 2;
            players[1].stats.coin = players[1].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("tradeClayRight");
        });
        Button tradeOreRight = new Button("Trade");
        tradeOreRight.setTranslateX(5);
        tradeOreRight.setOnMouseClicked(event -> {
            players[0].stats.coin = players[0].stats.coin - 2;
            players[1].stats.coin = players[1].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("tradeOreRight");
        });
        Button tradeStoneRight = new Button("Trade");
        tradeStoneRight.setTranslateX(5);
        tradeStoneRight.setOnMouseClicked(event -> {
            players[0].stats.coin = players[0].stats.coin - 2;
            players[1].stats.coin = players[1].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("tradeStoneRight");
        });

        Button tradeGlassRight = new Button("Trade");
        tradeGlassRight.setTranslateX(5);
        tradeGlassRight.setOnMouseClicked(event -> {
            players[0].stats.coin = players[0].stats.coin - 2;
            players[1].stats.coin = players[1].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("tradeGlassRight");
        });
        Button tradePaperRight = new Button("Trade");
        tradePaperRight.setTranslateX(5);
        tradePaperRight.setOnMouseClicked(event -> {
            players[0].stats.coin = players[0].stats.coin - 2;
            players[1].stats.coin = players[1].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("tradePaperRight");
        });
        Button tradeTextileRight = new Button("Trade");
        tradeTextileRight.setTranslateX(5);
        tradeTextileRight.setOnMouseClicked(event -> {
            players[0].stats.coin = players[0].stats.coin - 2;
            players[1].stats.coin = players[1].stats.coin + 2;

            try {
                reDrawWonders();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("tradeTextileRight");
        });
        HBox tradeLumberHBRight,tradeClayHBRight,tradeStoneHBRight,tradeOreHBRight,tradeGlassHBRight,tradePaperHBRight,tradeTextileHBRight;

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeLumberHBRight = new HBox(tradeLumberRight,imgLumber,ForText,imgCoin);
        tradeLumberHBRight.setTranslateY(270);
        tradeLumberHBRight.setTranslateX(1650);
        tradeLumberHBRight.setPrefSize(60,60);


        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeClayHBRight = new HBox(tradeClayRight,imgClay,ForText,imgCoin);
        tradeClayHBRight.setTranslateY(300);
        tradeClayHBRight.setTranslateX(1650);
        tradeClayHBRight.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeOreHBRight = new HBox(tradeOreRight,imgOre,ForText,imgCoin);
        tradeOreHBRight.setTranslateY(360);
        tradeOreHBRight.setTranslateX(1650);
        tradeOreHBRight.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.WHITESMOKE);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeStoneHBRight = new HBox(tradeStoneRight,imgStone,ForText,imgCoin);
        tradeStoneHBRight.setTranslateY(330);
        tradeStoneHBRight.setTranslateX(1650);
        tradeStoneHBRight.setPrefSize(40,40);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.BLACK);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeGlassHBRight = new HBox(tradeGlassRight,imgGlass,ForText,imgCoin);
        tradeGlassHBRight.setTranslateY(390);
        tradeGlassHBRight.setTranslateX(1650);
        tradeGlassHBRight.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.BLACK);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradePaperHBRight = new HBox(tradePaperRight,imgPaper,ForText,imgCoin);
        tradePaperHBRight.setTranslateY(420);
        tradePaperHBRight.setTranslateX(1650);
        tradePaperHBRight.setPrefSize(60,60);

        is = Files.newInputStream(Paths.get("images/coins.png"));
        img = new Image(is);
        is.close();
        imgCoin = new ImageView(img);
        imgCoin.setFitHeight(35);
        imgCoin.setFitWidth(35);
        imgCoin.setTranslateX(20);
        ForText = new Text("For 2 ");
        ForText.setFill(Color.BLACK);
        ForText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
        ForText.setTranslateX(15);
        tradeTextileHBRight = new HBox(tradeTextileRight,imgTextile,ForText,imgCoin);
        tradeTextileHBRight.setTranslateY(450);
        tradeTextileHBRight.setTranslateX(1650);
        tradeTextileHBRight.setPrefSize(60,60);

        sp.getChildren().addAll(tradeLumberHBRight,tradeClayHBRight,tradeStoneHBRight,tradeOreHBRight,tradeGlassHBRight,tradePaperHBRight,tradeTextileHBRight);



        OptionsPage.PauseButton pb = new OptionsPage.PauseButton();
        pb.setOnMouseClicked( event -> {
            Rectangle r = new Rectangle(0,0,1700,1700);
            r.setFill(Color.rgb(0,0,0,0.6));
            sp.getChildren().add(r);
            VBox menu2 = new VBox(40);
            menu2.setTranslateX(0);
            menu2.setTranslateY(300);

            Main.MenuButton btnResume = new Main.MenuButton("Resume Game");
            Main.MenuButton btnExit2 = new Main.MenuButton("Exit Game");
            Main.MenuButton btnMain = new Main.MenuButton("Main Menu");
            btnResume.setOnMouseClicked( event2 -> {
                btnResume.setVisible(false);
                btnExit2.setVisible(false);
                btnMain.setVisible(false);
                menu2.getChildren().removeAll(btnResume, btnExit2, btnMain);
                sp.getChildren().remove(menu2);
                sp.getChildren().remove(r);
            });
            btnExit2.setOnMouseClicked( event2 -> {
                System.exit(0);
            });
            btnMain.setOnMouseClicked( event2 -> {
                btnResume.setVisible(false);
                btnExit2.setVisible(false);
                btnMain.setVisible(false);
                menu2.getChildren().removeAll(btnResume, btnExit2, btnMain);
                sp.getChildren().remove(menu2);
                sp.getChildren().remove(r);
                window.setScene( mainmenu);
            });
            menu2.getChildren().addAll(btnResume, btnExit2, btnMain);
            sp.getChildren().add(menu2);

        });
        Pane mute2 = new Pane();
        Pane unmute2 = new Pane();
        mute2.setMaxSize(100,100);
        unmute2.setMaxSize(100,100);
        try {
            InputStream is2 = Files.newInputStream(Paths.get("images/mute.png"));
            Image img2 = new Image(is2);
            is2.close();
            ImageView imgViewMute = new ImageView(img2);
            imgViewMute.setFitHeight(100);
            imgViewMute.setFitWidth(100);
            mute2.getChildren().add(imgViewMute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            InputStream is3 = Files.newInputStream(Paths.get("images/unmute.png"));
            Image img3 = new Image(is3);
            is3.close();
            ImageView imgViewUnmute = new ImageView(img3);
            imgViewUnmute.setFitHeight(100);
            imgViewUnmute.setFitWidth(100);
            unmute2.getChildren().add(imgViewUnmute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mute2.setOnMouseClicked( event2 -> {
            Main.mediaPlayer.setMute(true);
            mute2.setVisible(false);
            unmute2.setVisible(true);
        });
        unmute2.setOnMouseClicked( event2 -> {
            Main.mediaPlayer.setMute(false);
            mute2.setVisible(true);
            unmute2.setVisible(false);
        });
        if(Main.mediaPlayer.isMute())
            mute2.setVisible(false);
        else
            unmute2.setVisible(false);
        sp.getChildren().addAll(mute2, unmute2);
        mute2.setTranslateX(650);
        mute2.setTranslateY(-380);
        unmute2.setTranslateX(650);
        unmute2.setTranslateY(-380);
        pb.setTranslateX(750);
        pb.setTranslateY(-380);
        sp.getChildren().add(pb);

        players[0] = new Player(name);
        players[1] = new Player("bot1");
        players[2] = new Player("bot2");
        players[3] = new Player("bot3");
        definingCards();
        Collections.shuffle(Arrays.asList(cards[0]));
        Collections.shuffle(Arrays.asList(cards[1]));
        Collections.shuffle(Arrays.asList(cards[2]));
        sp.getChildren().addAll(cards[0][0],cards[0][1],cards[0][2],cards[0][3],cards[0][4],cards[0][5],cards[0][6]);
        for( int i = 0; i < 3; i++) {
            for( int j = 0; j < 4; j++) {
                cards[i][j*7].setTranslateX(-250); cards[i][j*7].setTranslateY(90);
                cards[i][j*7+1].setTranslateX(-50);  cards[i][j*7+1].setTranslateY(90);
                cards[i][j*7+2].setTranslateX(-450); cards[i][j*7+2].setTranslateY(90);
                cards[i][j*7+3].setTranslateX(-650); cards[i][j*7+3].setTranslateY(90);
                cards[i][j*7+4].setTranslateX(-150); cards[i][j*7+4].setTranslateY(290);
                cards[i][j*7+5].setTranslateX(-350); cards[i][j*7+5].setTranslateY(290);
                cards[i][j*7+6].setTranslateX(-550); cards[i][j*7+6].setTranslateY(290);
            }
        }

        distributeWonders( sp, side, name);

        sp.getChildren().addAll(wb);



    }
    /*
    *player 1 receives the resource
    * player 2 gains the gold
    * side is to check if player 2 is on the right or left of player 1
     */
    public void makeTrade(Player p1, Player p2, String resource, String side )
    {

    }

    public void checkForTrade(String resource) {
        int worth=2;

        if ( resource.equals("Ore"))
        {
            if(players[0].specialCards[2]==true)
            {
            }
        }
        if ( resource.equals("Stone"))
        {
            if(players[0].specialCards[2]==true)
            {
                worth = 1;
            }
        }
        if ( resource.equals("Lumber"))
        {
            if(players[0].specialCards[2]==true)
            {
                worth = 1;
            }
        }
        if ( resource.equals("Clay"))
        {
            if(players[0].specialCards[2]==true)
            {
                worth = 1;
            }
        }


        Text cost;
        cost = new Text( "for" + worth+"gold");
        cost.setTranslateX(-600);
        cost.setTranslateY(-300);
        sp.getChildren().addAll(cost);


    }
    private void makeBattles(int age){
        if(age == 1) {
            if (players[0].stats.shield > players[1].stats.shield) {
                players[0].battlePoint += 1;
                players[1].battlePoint -= 1;
            }
            if (players[0].stats.shield < players[1].stats.shield) {
                players[0].battlePoint -= 1;
                players[1].battlePoint += 1;
            }
            if (players[0].stats.shield > players[3].stats.shield) {
                players[0].battlePoint += 1;
                players[3].battlePoint -= 1;
            }
            if (players[0].stats.shield < players[3].stats.shield) {
                players[0].battlePoint -= 1;
                players[3].battlePoint += 1;
            }
            if (players[1].stats.shield > players[2].stats.shield) {
                players[1].battlePoint += 1;
                players[2].battlePoint -= 1;
            }
            if (players[1].stats.shield < players[2].stats.shield) {
                players[1].battlePoint -= 1;
                players[2].battlePoint += 1;
            }
            if (players[2].stats.shield > players[3].stats.shield) {
                players[2].battlePoint += 1;
                players[3].battlePoint -= 1;
            }
            if (players[2].stats.shield < players[3].stats.shield) {
                players[2].battlePoint -= 1;
                players[3].battlePoint += 1;
            }
        }
        else if(age == 2){
            if (players[0].stats.shield > players[1].stats.shield) {
                players[0].battlePoint += 3;
                players[1].battlePoint -= 1;
            }
            if (players[0].stats.shield < players[1].stats.shield) {
                players[0].battlePoint -= 1;
                players[1].battlePoint += 3;
            }
            if (players[0].stats.shield > players[3].stats.shield) {
                players[0].battlePoint += 3;
                players[3].battlePoint -= 1;
            }
            if (players[0].stats.shield < players[3].stats.shield) {
                players[0].battlePoint -= 1;
                players[3].battlePoint += 3;
            }
            if (players[1].stats.shield > players[2].stats.shield) {
                players[1].battlePoint += 3;
                players[2].battlePoint -= 1;
            }
            if (players[1].stats.shield < players[2].stats.shield) {
                players[1].battlePoint -= 1;
                players[2].battlePoint += 3;
            }
            if (players[2].stats.shield > players[3].stats.shield) {
                players[2].battlePoint += 3;
                players[3].battlePoint -= 1;
            }
            if (players[2].stats.shield < players[3].stats.shield) {
                players[2].battlePoint -= 1;
                players[3].battlePoint += 3;
            }
        }
        else if(age == 3){
            if (players[0].stats.shield > players[1].stats.shield) {
                players[0].battlePoint += 5;
                players[1].battlePoint -= 1;
            }
            if (players[0].stats.shield < players[1].stats.shield) {
                players[0].battlePoint -= 1;
                players[1].battlePoint += 5;
            }
            if (players[0].stats.shield > players[3].stats.shield) {
                players[0].battlePoint += 5;
                players[3].battlePoint -= 1;
            }
            if (players[0].stats.shield < players[3].stats.shield) {
                players[0].battlePoint -= 1;
                players[3].battlePoint += 5;
            }
            if (players[1].stats.shield > players[2].stats.shield) {
                players[1].battlePoint += 5;
                players[2].battlePoint -= 1;
            }
            if (players[1].stats.shield < players[2].stats.shield) {
                players[1].battlePoint -= 1;
                players[2].battlePoint += 5;
            }
            if (players[2].stats.shield > players[3].stats.shield) {
                players[2].battlePoint += 5;
                players[3].battlePoint -= 1;
            }
            if (players[2].stats.shield < players[3].stats.shield) {
                players[2].battlePoint -= 1;
                players[3].battlePoint += 5;
            }
        }

    }
    public void giveError( String errorMessage) {
        Popup popup = new Popup();
        Text text = new Text(errorMessage);
        text.setFill(Color.RED);
        text.setFont(Font.font("Kalam", FontWeight.BOLD,40));
        text.setTranslateY(200);
        popup.getContent().add(text);
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(e -> popup.hide());

        popup.show(window);
        delay.play();
    }
    public void reDrawWonders() throws Exception{
        sp.getChildren().removeAll(wb);
        wb[0] = new WonderBoard( sp,wb[0].wonderNum, side, players[0].name, 0);
        wb[1] = new WonderBoard( sp,wb[1].wonderNum, side, players[1].name, 1);
        wb[2] = new WonderBoard( sp,wb[2].wonderNum, side, players[2].name, 2);
        wb[3] = new WonderBoard( sp,wb[3].wonderNum, side, players[3].name, 3);
        wb[0].setTranslateY(150);
        wb[0].setTranslateX(350);
        wb[1].setTranslateY(-150);
        wb[1].setTranslateX(450);
        wb[3].setTranslateY(-150);
        wb[3].setTranslateX(-450);
        wb[2].setTranslateY(-260);
        sp.getChildren().addAll(wb);
    }
    public boolean isBot(int playerNum) {
        return playerNum != 0;
    }

    /**
     * Makes all the bots play
     */
    public void playBots() {
        // bot turns (player 1-3)
        for (int i = 1; i < 4; i++) {
            try {
                players[i].randomPlay(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void endTurn() throws Exception {

        playBots();

        // vineyard and bazar check
        for( int i = 0; i < 4 ; i++){
            if(players[i].specialCards[4]) {
                players[i].stats.coin += (players[(i + 1) % 4].brownCards + players[(i + 3) % 4].brownCards + players[i].brownCards);
                players[i].specialCards[4] = false;
            }
            if(players[i].specialCards[5]) {
                players[i].stats.coin += (players[(i + 1) % 4].greyCards + players[(i + 3) % 4].greyCards + players[i].greyCards) * 2;
                players[i].specialCards[5] = false;
            }
        }



        // cards part
        int lastAge = currentAge;
        int lastTurn = currentTurn;
        currentTurn++;

        if( currentTurn == 7) {
            currentTurn = 1;
            makeBattles(currentAge);
            currentAge++;
            if( currentAge == 4) {
                endGame();
                return;
            }
        }
        for(int i = ((lastTurn - 1) % 4) * 7; i <= ((lastTurn - 1) % 4) * 7 + 6; i++) sp.getChildren().remove(cards[lastAge - 1][i]);
        for(int i = ((currentTurn - 1) % 4) * 7; i <= ((currentTurn - 1) % 4) * 7 + 6; i++) sp.getChildren().add(cards[currentAge - 1][i]);
        reDrawWonders();
    }
    public void endGame() {
    }
    public boolean recursiveCheck( Resource[] playersResource, boolean[] pr, Resource costsResource, int cr) {
        System.out.println("pr.length: " + pr.length);
        System.out.println("cost length: " + costsResource.quantity.length);
        System.out.println("player length: " + playersResource.length);
        if( cr >= costsResource.quantity.length)
            return true;
//        System.out.println("cost: " + costsResource.name[cr]);
        boolean returning = false;
        for( int i = 0; i < pr.length; i++) {
            if(!pr[i]) {
                for (int j = 0; j < playersResource[i].quantity.length; j++) {
                    if(playersResource[i].name[j].equals(costsResource.name[cr])) {
                        System.out.println(playersResource[i].name[j] + " -> " + costsResource.name[cr]);
                        int tmpCr = cr;
                        boolean[] tmpPr = pr;
                        Resource[] tmpPlayerResource = new Resource[playersResource.length];
                        Resource tmpCostResource = new Resource( costsResource);
                        for( int k = 0; k < pr.length; k++)
                            tmpPlayerResource[k] = new Resource( playersResource[k]);
                        tmpCostResource.quantity[cr]--;
                        if(tmpCostResource.quantity[cr] == 0)
                            tmpCr++;
                        tmpPlayerResource[i].quantity[j]--;
                        if( tmpPlayerResource[i].quantity[j] == 0)
                            tmpPr[i] = true;

                        if( recursiveCheck(tmpPlayerResource, tmpPr, tmpCostResource, tmpCr))
                            return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean checkResources(int playerNum, boolean isWonderbuild, Property cost) {
        if(isWonderbuild)
            cost = wb[playerNum].milestones[players[playerNum].milestoneDone].cost;
        if(cost.requiredBuilding != "") {
            for (int i = 0; i < players[playerNum].buildingsCount; i++) {
//            System.out.println(players[playerNum].buildings[i]);
                if (players[playerNum].buildings[i].contains(cost.requiredBuilding)) {
                    System.out.println(players[playerNum].buildings[i]);
                    return true;
                }
            }
        }
        if( players[playerNum].stats.coin < cost.coin)
            return false;
        Resource[] tmpPlayerResource;
        tmpPlayerResource = new Resource[players[playerNum].resources.length];
        Resource tmpCostResource = new Resource( cost.resource);
        System.out.println("length" + players[playerNum].resources.length);
        for( int k = 0; k < players[playerNum].resourceCount; k++)
            tmpPlayerResource[k] = new Resource( players[playerNum].resources[k]);

        return recursiveCheck( tmpPlayerResource, new boolean[players[playerNum].resourceCount], tmpCostResource, 0);
    }
    public void gainBenefit( int playerNum, boolean isWonderbuild, Property benefit, String buildingName, String buildingColor) throws Exception {
        // specialCard = # : 16= Babylon A, 17 = Babylon B, 18 = olympia A, 19 = olympia B1,    20 = olympiaB3, 21 = Halikarnassos
        if(isWonderbuild) {
            benefit = wb[playerNum].milestones[players[playerNum].milestoneDone].benefit;
            players[playerNum].milestoneDone++;
        }
        // halikarnassos feature
        if(benefit.specialCard == 21 ) {
            if(noOfCardsAtStake == 0) return;
            if( playerNum != 0) {
                Random rand = new Random();
                int random = rand.nextInt(noOfCardsAtStake);
                gainBenefit( playerNum, false, cardsAtStake[random].benefit, cardsAtStake[random].name, cardsAtStake[random].color);
                cardsAtStake[random].deleteCard();
                return;
            }
            Stage stage = new Stage();
            stage.setTitle("Reward: Pick a card to build");
            stage.show();
            Group  group = new Group();
            Scene scene = new Scene( group);
            HBox hBox = new HBox();
            Rectangle[] board = new Rectangle[noOfCardsAtStake];
            for( int i = 0; i < noOfCardsAtStake; i++) {
                board[i] = new Rectangle(140,190,Color.rgb(109,132,118,1));
                board[i].setArcHeight(15);
                board[i].setArcWidth(15);
                InputStream is = Files.newInputStream(Paths.get("images/card images/" + cardsAtStake[i].name + ".png"));
                Image img = new Image(is);
                is.close();
                board[i].setFill(new ImagePattern(img));
                int finalI = i;
                board[i].setOnMouseClicked(mouseEvent -> {
                    try {
                        gainBenefit(playerNum, false, cardsAtStake[finalI].benefit, cardsAtStake[finalI].name, cardsAtStake[finalI].color);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stage.close();
                    try {
                        reDrawWonders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        cardsAtStake[finalI].deleteCard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                hBox.getChildren().add(board[i]);
            }
            group.getChildren().add(hBox);
            stage.setScene( scene);
            return;
        }
        // olympia b3 feature
        if( benefit.specialCard == 20) {
            int leftNeighbor, rightNeighbor, i = 0;
            rightNeighbor = ((playerNum + 1) % 4);
            leftNeighbor = ((playerNum + 3) % 4);
            Card[] guildDeck = new Card[players[leftNeighbor].purpleCards + players[rightNeighbor].purpleCards];
            if( players[leftNeighbor].specialCards[10] || players[rightNeighbor].specialCards[10]) {
                Property tmp = new Property();
                tmp.specialCard = 10;
                guildDeck[i] = new Card("workersguild","purple", new Property(), tmp);
                i++;
            }
            if( players[leftNeighbor].specialCards[11] || players[rightNeighbor].specialCards[11]) {
                Property tmp = new Property();
                tmp.specialCard = 11;
                guildDeck[i] = new Card("craftsmensguild","purple", new Property(), tmp);
                i++;
            }
            if( players[leftNeighbor].specialCards[12] || players[rightNeighbor].specialCards[12]) {
                Property tmp = new Property();
                tmp.specialCard = 12;
                guildDeck[i] = new Card("tradersguild","purple", new Property(), tmp);
                i++;
            }
            if( players[leftNeighbor].specialCards[13] || players[rightNeighbor].specialCards[13]) {
                Property tmp = new Property();
                tmp.specialCard = 13;
                guildDeck[i] = new Card("philosophersguild","purple", new Property(), tmp);
                i++;
            }
            if( players[leftNeighbor].specialCards[14] || players[rightNeighbor].specialCards[14]) {
                Property tmp = new Property();
                tmp.specialCard = 14;
                guildDeck[i] = new Card("spiesguild","purple", new Property(), tmp);
                i++;
            }
            if( players[leftNeighbor].specialCards[15] || players[rightNeighbor].specialCards[15]) {
                Property tmp = new Property();
                tmp.specialCard = 15;
                guildDeck[i] = new Card("magistratesguild","purple", new Property(), tmp);
                i++;
            }
            if(i == 0) return;
            if( playerNum != 0) {
                Random rand = new Random();
                int random = rand.nextInt(i);
                gainBenefit( playerNum, false, guildDeck[random].benefit, guildDeck[random].name, guildDeck[random].color);
                return;
            }
            Stage stage = new Stage();
            stage.setTitle("Reward: Pick a guild to copy");
            stage.show();
            Group  group = new Group();
            Scene scene = new Scene( group);
            HBox hBox = new HBox();
            Rectangle[] board = new Rectangle[i];
            for( int j = 0; j < i; j++) {
                board[j] = new Rectangle(140,190,Color.rgb(109,132,118,1));
                board[j].setArcHeight(15);
                board[j].setArcWidth(15);
                InputStream is = Files.newInputStream(Paths.get("images/card images/" + guildDeck[j].name + ".png"));
                Image img = new Image(is);
                is.close();
                board[j].setFill(new ImagePattern(img));
                int finalI = j;
                board[j].setOnMouseClicked(mouseEvent -> {
                    try {
                        gainBenefit(playerNum, false, guildDeck[finalI].benefit, guildDeck[finalI].name, guildDeck[finalI].color);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stage.close();
                    try {
                        reDrawWonders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                hBox.getChildren().add(board[j]);
            }
            group.getChildren().add(hBox);
            stage.setScene( scene);
            return;
        }
        if(!buildingName.equals("")) {
            players[playerNum].buildings[players[playerNum].buildingsCount] = buildingName;
            players[playerNum].buildingsCount++;
            if(buildingColor.equals("red")) players[playerNum].redCards++;
            else if(buildingColor.equals("grey")) players[playerNum].greyCards++;
            else if(buildingColor.equals("green")) players[playerNum].greenCards++;
            else if(buildingColor.equals("blue")) players[playerNum].blueCards++;
            else if(buildingColor.equals("yellow")) players[playerNum].yellowCards++;
            else if(buildingColor.equals("brown")) players[playerNum].brownCards++;
            else if(buildingColor.equals("purple")) players[playerNum].purpleCards++;
        }
        if(benefit.resource.quantity.length != 0)
            players[playerNum].addResource(benefit.resource);
        players[playerNum].stats.coin += benefit.coin;
        players[playerNum].stats.shield += benefit.shield;
        players[playerNum].stats.mechanic += benefit.mechanic;
        players[playerNum].stats.literature += benefit.literature;
        players[playerNum].stats.geometry += benefit.geometry;
        players[playerNum].stats.victoryPoint += benefit.victoryPoint;
        if(benefit.specialCard == 6)    players[playerNum].stats.coin += players[playerNum].brownCards;
        else if (benefit.specialCard == 7)  players[playerNum].stats.coin += players[playerNum].yellowCards;
        else if (benefit.specialCard == 8)  players[playerNum].stats.coin += players[playerNum].greyCards * 2;
        else if (benefit.specialCard == 9)  players[playerNum].stats.coin += players[playerNum].milestoneDone * 3;
        players[playerNum].specialCards[benefit.specialCard] = true;
    }
    private void definingCards() throws Exception {
        Property a = new Property();
        Property b = new Property();

        // bazı kartların benefiti özellik veriyor. bu özellikleri b.specialCard = # diyerek yapın
        // # -> east Trading post = 1,  west trading post = 2,  marketplace = 3,    vineyard = 4,   bazar = 5,  haven = 6,
        // lighthouse =7,  chamber of commerce = 8,    arena = 9,  workers guild = 10, craftsmens guild = 11,  traders guild = 12, philosophers guild = 13,
        // spies guild = 14,   magistrates guild = 15

        //1st age cards
        //scriptorium1
        a.resource = new Resource(1);
        a.resource.name[0] = "Paper"; a.resource.quantity[0] = 1;
        b.literature = 1;
        cards[0][0] = new Card("scriptorium","green",a,b);

        //scriptorium2
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Paper"; a.resource.quantity[0] = 1;
        b = new Property();
        b.literature = 1;
        cards[0][1] = new Card("scriptorium","green",a,b);

        //workshop
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Glass"; a.resource.quantity[0] = 1;
        b = new Property();
        b.mechanic = 1;
        cards[0][2] = new Card("workshop","green",a,b);

        //apothecary
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Textile"; a.resource.quantity[0] = 1;
        b = new Property();
        b.geometry = 1;
        cards[0][3] = new Card("apothecary","green",a,b);

        //stockade
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        b = new Property();
        b.shield = 1;
        cards[0][4] = new Card("stockade","red",a,b);

        //barracks
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 1;
        b = new Property();
        b.shield = 1;
        cards[0][5] = new Card("barracks","red",a,b);

        //guard tower1
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 1;
        b = new Property();
        b.shield = 1;
        cards[0][6] = new Card("guardtower","red",a,b);

        //guard tower2
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 1;
        b = new Property();
        b.shield = 1;
        cards[0][7] = new Card("guardtower","red",a,b);

        //pawnshop
        a = new Property();
        b = new Property();
        b.victoryPoint = 3;
        cards[0][8] = new Card("pawnshop","blue",a,b);

        //altar
        a = new Property();
        b = new Property();
        b.victoryPoint = 2;
        cards[0][9] = new Card("altar","blue",a,b);

        //theater
        a = new Property();
        b = new Property();
        b.victoryPoint = 2;
        cards[0][10] = new Card("theater","blue",a,b);

        //baths
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Stone"; a.resource.quantity[0] = 1;
        b = new Property();
        b.victoryPoint = 3;
        cards[0][11] = new Card("baths","blue",a,b);

        //lumberyard1
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Lumber"; b.resource.quantity[0] = 1;
        cards[0][12] = new Card("lumberyard","brown",a,b);

        //lumberyard2
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Lumber"; b.resource.quantity[0] = 1;
        cards[0][13] = new Card("lumberyard","brown",a,b);

        //stone pit
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Stone"; b.resource.quantity[0] = 1;
        cards[0][14] = new Card("stonepit","brown",a,b);

        //clay pool
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Clay"; b.resource.quantity[0] = 1;
        cards[0][15] = new Card("claypool","brown",a,b);

        //ore vein1
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Ore"; b.resource.quantity[0] = 1;
        cards[0][16] = new Card("orevein","brown",a,b);

        //loom
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Textile"; b.resource.quantity[0] = 1;
        cards[0][17] = new Card("loom","grey",a,b);

        //glassworks
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Glass"; b.resource.quantity[0] = 1;
        cards[0][18] = new Card("glassworks","grey",a,b);

        //press
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Paper"; b.resource.quantity[0] = 1;
        cards[0][19] = new Card("press","grey",a,b);

        //excavation
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(2);
        b.resource.name[0] = "Stone"; b.resource.quantity[0] = 1;
        b.resource.name[1] = "Clay"; b.resource.quantity[1] = 1;
        cards[0][20] = new Card("excavation","brown",a,b);

        //clay pit
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(2);
        b.resource.name[0] = "Clay"; b.resource.quantity[0] = 1;
        b.resource.name[1] = "Ore"; b.resource.quantity[1] = 1;
        cards[0][21] = new Card("claypit","brown",a,b);

        //timber yard
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(2);
        b.resource.name[0] = "Stone"; b.resource.quantity[0] = 1;
        b.resource.name[1] = "Lumber"; b.resource.quantity[1] = 1;
        cards[0][22] = new Card("timberyard","brown",a,b);

        //ore vein2
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Ore"; b.resource.quantity[0] = 1;
        cards[0][23] = new Card("orevein","brown",a,b);

        //tavern
        a = new Property();
        b = new Property();
        b.coin = 5;
        cards[0][24] = new Card("tavern","yellow",a,b);

        //east trading post
        a = new Property();
        b = new Property();
        b.specialCard = 1;
        cards[0][25] = new Card("easttradingpost","yellow",a,b);

        //west trading post
        a = new Property();
        b = new Property();
        b.specialCard = 2;
        cards[0][26] = new Card("westtradingpost","yellow",a,b);

        //marketplace
        a = new Property();
        b = new Property();
        b.specialCard = 3;
        cards[0][27] = new Card("marketplace","yellow",a,b);


        //2nd age cards

//sawmill
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Lumber"; b.resource.quantity[0] = 2;
        cards[1][0] = new Card("sawmill","brown",a,b);
//sawmill2
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Lumber"; b.resource.quantity[0] = 2;
        cards[1][1] = new Card("sawmill","brown",a,b);

//quarry
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Stone"; b.resource.quantity[0] = 2;
        cards[1][2] = new Card("quarry","brown",a,b);
//quarry2
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Stone"; b.resource.quantity[0] = 2;
        cards[1][3] = new Card("quarry","brown",a,b);

//brickyard
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Clay"; b.resource.quantity[0] = 2;
        cards[1][4] = new Card("brickyard","brown",a,b);
//brickyard2
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Clay"; b.resource.quantity[0] = 2;
        cards[1][5] = new Card("brickyard","brown",a,b);


//foundry
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Ore"; b.resource.quantity[0] = 2;
        cards[1][6] = new Card("foundry","brown",a,b);
//foundry2
        a = new Property();
        a.coin = 1;
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Ore"; b.resource.quantity[0] = 2;
        cards[1][7] = new Card("foundry","brown",a,b);


//aqueduct
        a = new Property();
        a.requiredBuilding = "Baths";
        a.resource = new Resource(1);
        a.resource.name[0] = "Stone"; a.resource.quantity[0] = 3;
        b = new Property();
        b.victoryPoint = 5;
        cards[1][8] = new Card("aqueduct","blue",a,b);

//temple
        a = new Property();
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Clay"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Glass"; a.resource.quantity[2] = 1;
        a.requiredBuilding = "altar";
        b = new Property();
        b.victoryPoint = 3;
        cards[1][9] = new Card("temple","blue",a,b);

//statue
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 2;
        a.requiredBuilding = "altar";
        b = new Property();
        b.victoryPoint = 4;
        cards[1][10] = new Card("statue","blue",a,b);

//forum
        a = new Property();
        a.requiredBuilding = "tradingpost";
        a.resource = new Resource(1);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 2;
        b = new Property();
        b.resource = new Resource(3);
        b.resource.name[0] = "Glass"; b.resource.quantity[0] = 1;
        b.resource.name[1] = "Textile"; b.resource.quantity[1] = 1;
        b.resource.name[2] = "Paper"; b.resource.quantity[2] = 1;
        cards[1][11] = new Card("forum","yellow",a,b);


// caravansery
        a = new Property();
        a.requiredBuilding = "marketplace";
        a.resource = new Resource(1);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 2;
        b = new Property();
        b.resource = new Resource(4);
        b.resource.name[0] = "Lumber"; b.resource.quantity[0] = 1;
        b.resource.name[1] = "Stone"; b.resource.quantity[1] = 1;
        b.resource.name[2] = "Ore"; b.resource.quantity[2] = 1;
        b.resource.name[3] = "Clay"; b.resource.quantity[3] = 1;
        cards[1][12] = new Card("caravansery","yellow",a,b);

//vineyard
        a = new Property();
        b = new Property();
        b.specialCard =4;
        cards[1][13] = new Card("vineyard","yellow",a,b);

//bazar
        a = new Property();
        b = new Property();
        b.specialCard =5;
        cards[1][14] = new Card("bazar","yellow",a,b);

//walls
        a = new Property();
        a.resource = new Resource(1);
        a.resource.name[0] = "Stone"; a.resource.quantity[0] = 3;
        b = new Property();
        b.shield = 2;
        cards[1][15] = new Card("walls","red",a,b);

//trainingground
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Lumber"; a.resource.quantity[1] = 1;
        b = new Property();
        b.shield = 2;
        cards[1][16] = new Card("trainingground","red",a,b);

//stables
        a = new Property();
        a.resource = new Resource(3);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Clay"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Lumber"; a.resource.quantity[2] = 1;
        b = new Property();
        b.shield =2;
        cards[1][17] = new Card("stables","red",a,b);

//dispensary
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Glass"; a.resource.quantity[1] = 1;
        b = new Property();
        b.geometry=1;
        cards[1][18] = new Card("dispensary","green",a,b);
//dispensary2
        cards[1][19] = new Card("dispensary","green",a,b);


//archeryrange
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        b = new Property();
        b.shield =2;
        cards[1][20] = new Card("archeryrange","red",a,b);

//laboratory
        a = new Property();
        a.requiredBuilding = "workshop";
        a.resource = new Resource(2);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Paper"; a.resource.quantity[1] = 1;
        b = new Property();
        b.mechanic =1;
        cards[1][21] = new Card("laboratory","green",a,b);

//courthouse
        a = new Property();
        a.requiredBuilding = "scriptorium";
        a.resource = new Resource(2);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Textile"; a.resource.quantity[1] = 1;
        b = new Property();
        b.victoryPoint =4;
        cards[1][22] = new Card("courthouse","blue",a,b);

//library
        a = new Property();
        a.requiredBuilding = "scriptorium";
        a.resource = new Resource(2);
        a.resource.name[0] = "Stone"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Textile"; a.resource.quantity[1] = 1;
        b = new Property();
        b.literature =1;
        cards[1][23] = new Card("library","green",a,b);

//school
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Paper"; a.resource.quantity[1] = 1;
        b = new Property();
        b.literature =1;
        cards[1][24] = new Card("dispensary","green",a,b);


//loom
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Textile"; b.resource.quantity[0] = 1;
        cards[1][25] = new Card("loom","grey",a,b);

//glawssworks
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Glass"; b.resource.quantity[0] = 1;
        cards[1][26] = new Card("glassworks","grey",a,b);

//press
        a = new Property();
        b = new Property();
        b.resource = new Resource(1);
        b.resource.name[0] = "Paper"; b.resource.quantity[0] = 1;
        cards[1][27] = new Card("press","grey",a,b);



        //3rd age cards
        //red cards
        // arsenal1
        a = new Property();
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b = new Property();
        b.shield = 3;
        cards[2][0] = new Card("arsenal","red",a,b);

        // arsenal2
        a = new Property();
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b = new Property();
        b.shield = 3;
        cards[2][1] = new Card("arsenal","red",a,b);


        //Circus
        a = new Property();
        a.requiredBuilding = "trainingground";
        a.resource = new Resource(2);
        a.resource.name[0] = "Stone"; a.resource.quantity[0] = 3;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        b = new Property();
        b.shield = 3;
        cards[2][2] = new Card("circus","red",a,b);

        //fortifications
        a = new Property();
        a.requiredBuilding = "walls";
        a.resource = new Resource(2);
        a.resource.name[0] = "Stone"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 3;
        b = new Property();
        b.shield = 3;
        cards[2][3] = new Card("fortifications","red",a,b);

        //siegeworkshop
        a = new Property();
        a.requiredBuilding = "laboratory";
        a.resource = new Resource(2);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Clay"; a.resource.quantity[1] = 3;
        b = new Property();
        b.shield = 3;
        cards[2][4] = new Card("siegeworkshop","red",a,b);

        //blue cards
        //Pantheon
        a = new Property();
        a.requiredBuilding = "temple";
        a.resource = new Resource(5);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Paper"; a.resource.quantity[2] = 1;
        a.resource.name[3] = "Textile"; a.resource.quantity[3] = 1;
        a.resource.name[4] = "Glass"; a.resource.quantity[4] = 1;
        b = new Property();
        b.victoryPoint=7;
        cards[2][5] = new Card("pantheon","blue",a,b);

        //Gardens1
        a = new Property();
        a.requiredBuilding = "statue";
        a.resource = new Resource(2);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Clay"; a.resource.quantity[1] = 2;
        b = new Property();
        b.victoryPoint = 5;
        cards[2][6] = new Card("gardens","blue",a,b);

        //Gardens2
        a = new Property();
        a.requiredBuilding = "statue";
        a.resource = new Resource(2);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Clay"; a.resource.quantity[1] = 2;
        b = new Property();
        b.victoryPoint = 5;
        cards[2][7] = new Card("gardens","blue",a,b);

        //Townhall
        a = new Property();
        a.resource = new Resource(3);
        a.resource.name[0] = "Glass"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Stone"; a.resource.quantity[2] = 2;
        b = new Property();
        b.victoryPoint = 6;
        cards[2][8] = new Card("townhall","blue",a,b);

        //Palace
        a = new Property();
        a.resource = new Resource(7);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Paper"; a.resource.quantity[2] = 1;
        a.resource.name[3] = "Textile"; a.resource.quantity[3] = 1;
        a.resource.name[4] = "Glass"; a.resource.quantity[4] = 1;
        a.resource.name[5] = "Lumber"; a.resource.quantity[5] = 1;
        a.resource.name[6] = "Stone"; a.resource.quantity[6] = 1;
        b = new Property();
        b.victoryPoint=8;
        cards[2][9] = new Card("palace","blue",a,b);

        //Senate
        a = new Property();
        a.requiredBuilding = "library";
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Stone"; a.resource.quantity[2] = 1;
        b = new Property();
        b.victoryPoint=8;
        cards[2][10] = new Card("senate","blue",a,b);

        //Green Cards
        //Lodge
        a = new Property();
        a.requiredBuilding = "dispensary";
        a.resource = new Resource(3);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Paper"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b = new Property();
        b.geometry = 1;
        cards[2][11] = new Card("lodge","green",a,b);

        //Observatory
        a = new Property();
        a.requiredBuilding = "laboratory";
        a.resource = new Resource(3);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Glass"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b = new Property();
        b.mechanic = 1;
        cards[2][12] = new Card("observatory","green",a,b);


        //University1
        a = new Property();
        a.requiredBuilding = "library";
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Paper"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Glass"; a.resource.quantity[2] = 1;
        b = new Property();
        b.literature = 1;
        cards[2][13] = new Card("university","green",a,b);

        //University2
        a = new Property();
        a.requiredBuilding = "library";
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Paper"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Glass"; a.resource.quantity[2] = 1;
        b = new Property();
        b.literature = 1;
        cards[2][14] = new Card("university","green",a,b);

        //Study
        a = new Property();
        a.requiredBuilding = "school";
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Paper"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b = new Property();
        b.mechanic = 1;
        cards[2][15] = new Card("study","green",a,b);

        //academy
        a = new Property();
        a.requiredBuilding = "school";
        a.resource = new Resource(2);
        a.resource.name[0] = "Stone"; a.resource.quantity[0] = 3;
        a.resource.name[1] = "Glass"; a.resource.quantity[1] = 1;
        b = new Property();
        b.geometry = 1;
        cards[2][16] = new Card("academy","green",a,b);


        //Yellow Cards
        //Haven1
        a = new Property();
        a.requiredBuilding = "forum";
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b = new Property();
        b.specialCard=6;
        cards[2][17] = new Card("haven","yellow",a,b);

        //Haven2
        a = new Property();
        a.requiredBuilding = "forum";
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b = new Property();
        b.specialCard=6;
        cards[2][18] = new Card("haven","yellow",a,b);

        //Lighthouse
        a = new Property();
        a.requiredBuilding = "caravansery";
        a.resource = new Resource(2);
        a.resource.name[0] = "Glass"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Stone"; a.resource.quantity[1] = 1;
        b = new Property();
        b.specialCard=7;
        cards[2][19] = new Card("lighthouse","yellow",a,b);

        //Chamber of commerce
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Paper"; a.resource.quantity[1] = 1;
        b = new Property();
        b.specialCard=8;
        cards[2][20] = new Card("chamberofcommerce","yellow",a,b);

        //arena
        a = new Property();
        a.requiredBuilding = "dispensary";
        a.resource = new Resource(2);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Stone"; a.resource.quantity[1] = 2;
        b = new Property();
        b.specialCard=9;
        cards[2][21] = new Card("arena","yellow",a,b);

        //Purple Cards
        //Workers Guild
        a = new Property();
        a.resource = new Resource(4);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Stone"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Clay"; a.resource.quantity[2] = 1;
        a.resource.name[3] = "Lumber"; a.resource.quantity[3] = 2;
        b = new Property();
        b.specialCard=10;
        cards[2][22] = new Card("workersguild","purple",a,b);

        //Craftmens guild
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Stone"; a.resource.quantity[1] = 2;
        b = new Property();
        b.specialCard=11;
        cards[2][23] = new Card("craftsmensguild","purple",a,b);

        //Traders Guild
        a = new Property();
        a.resource = new Resource(3);
        a.resource.name[0] = "Paper"; a.resource.quantity[0] = 1;
        a.resource.name[1] = "Textile"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Glass"; a.resource.quantity[2] = 1;
        b = new Property();
        b.specialCard=12;
        cards[2][24] = new Card("tradersguild","purple",a,b);

        //Philosophers Guild
        a = new Property();
        a.resource = new Resource(3);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 3;
        a.resource.name[1] = "Textile"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Paper"; a.resource.quantity[2] = 1;
        b = new Property();
        b.specialCard=13;
        cards[2][25] = new Card("philosophersguild","purple",a,b);

        //Spies Guild
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Clay"; a.resource.quantity[0] = 3;
        a.resource.name[1] = "Glass"; a.resource.quantity[1] = 1;
        b = new Property();
        b.specialCard=14;
        cards[2][26] = new Card("spiesguild","purple",a,b);

        //Magistrates Guild
        a = new Property();
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 3;
        a.resource.name[1] = "Stone"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b = new Property();
        b.specialCard=15;
        cards[2][27] = new Card("magistratesguild","purple",a,b);

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
        for( int i = 0; i < 4; i++) {
            Resource tmp = new Resource( 1);
            if(randoms[i] == 1) {tmp.name[0] = "Ore"; tmp.quantity[0] = 1; }
            if(randoms[i] == 2) {tmp.name[0] = "Glass"; tmp.quantity[0] = 1; }
            if(randoms[i] == 3) {tmp.name[0] = "Paper"; tmp.quantity[0] = 1; }
            if(randoms[i] == 4) {tmp.name[0] = "Clay"; tmp.quantity[0] = 1; }
            if(randoms[i] == 5) {tmp.name[0] = "Lumber"; tmp.quantity[0] = 1; }
            if(randoms[i] == 6) {tmp.name[0] = "Textile"; tmp.quantity[0] = 1; }
            if(randoms[i] == 7) {tmp.name[0] = "Stone"; tmp.quantity[0] = 1; }
            players[i].addResource( tmp);
        }
        wb[0] = new WonderBoard( sp,randoms[0], side.getSelectedToggle().getUserData().toString(), name, 0);
        wb[1] = new WonderBoard( sp,randoms[1], side.getSelectedToggle().getUserData().toString(), "bot1", 1);
        wb[2] = new WonderBoard( sp,randoms[2], side.getSelectedToggle().getUserData().toString(), "bot2", 2);
        wb[3] = new WonderBoard( sp,randoms[3], side.getSelectedToggle().getUserData().toString(), "bot3", 3);
        // wonder animasyonları
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setNode(wb[0]);
        translateTransition.setByY(150);
        translateTransition.setByX(350);
        translateTransition.play();
        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setNode(wb[1]);
        translateTransition.setByY(-150);
        translateTransition.setByX(450);
        translateTransition.play();
        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setNode(wb[3]);
        translateTransition.setByY(-150);
        translateTransition.setByX(-450);
        translateTransition.play();
        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(1000));
        translateTransition.setNode(wb[2]);
        translateTransition.setByY(-260);
        translateTransition.play();
    }
    private class WonderBoard extends Pane {
        Text coinText, shieldText, battleText, vicPointText, literatureText, mechanicText, geometryText, greenText, redText, yellowText, greyText, brownText, blueText;
        int pNum, wonderNum;
        Background bg;
        Milestone[] milestones;
        VBox coinVB, shieldVB, battleVB, vicPointVB, literatureVB, mechanicVB, geometryVB;
        HBox greenHB, redHB, yellowHB, greyHB, brownHB, blueHB, resourcesHB;
        public WonderBoard( StackPane sp, int wNumber, String side, String playerName, int pN) throws Exception{
            Property a,b;
            String wName;
            wonderNum = wNumber;
            setMaxSize(400, 250);
            milestones = new Milestone[3];
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
            // specialCard = # : 16= Babylon A, 17 = Babylon B, 18 = olympia A, 19 = olympia B1,    20 = olympiaB3, 21 = Halikarnassos
            if( wNumber == 1) {
                InputStream is = Files.newInputStream(Paths.get("images/setname.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "rhodos";
                Text sideText2 = new Text("Rhodos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(300);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if(side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";    a.resource.quantity[0] = 3;
                    b.shield = 2;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";  a.resource.quantity[0] = 4;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
                else {
                    milestones = new Milestone[2];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";  a.resource.quantity[0] = 3;
                    b.victoryPoint = 3; b.coin = 3; b.shield = 1;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";    a.resource.quantity[0] = 4;
                    b.shield = 1;   b.victoryPoint = 4; b.coin = 4;
                    milestones[1] = new Milestone(a,b);
                }
            }
            else if( wNumber == 2) {
                InputStream is = Files.newInputStream(Paths.get("images/alexandria.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "alexandria";
                Text sideText2 = new Text("Alexandria - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(280);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if(side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";    a.resource.quantity[0] = 2;
                    b.resource = new Resource(4);
                    b.resource.name[0] = "Lumber";  b.resource.quantity[0] = 1;
                    b.resource.name[1] = "Ore";  b.resource.quantity[1] = 1;
                    b.resource.name[2] = "Stone";  b.resource.quantity[2] = 1;
                    b.resource.name[3] = "Clay";  b.resource.quantity[3] = 1;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Glass";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
                else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";  a.resource.quantity[0] = 2;
                    b.resource = new Resource(4);
                    b.resource.name[0] = "Lumber";  b.resource.quantity[0] = 1;
                    b.resource.name[1] = "Ore";  b.resource.quantity[1] = 1;
                    b.resource.name[2] = "Stone";  b.resource.quantity[2] = 1;
                    b.resource.name[3] = "Clay";  b.resource.quantity[3] = 1;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";    a.resource.quantity[0] = 2;
                    b.resource = new Resource(3);
                    b.resource.name[0] = "Glass";  b.resource.quantity[0] = 1;
                    b.resource.name[1] = "Textile";  b.resource.quantity[1] = 1;
                    b.resource.name[2] = "Paper";  b.resource.quantity[2] = 1;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";  a.resource.quantity[0] = 3;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
            }
            else if( wNumber == 3) {
                InputStream is = Files.newInputStream(Paths.get("images/gamepage.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "ephesos";
                Text sideText2 = new Text("Ephesos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if(side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";    a.resource.quantity[0] = 2;
                    b.resource = new Resource(4);
                    b.coin = 9;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Paper";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
                else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 2; b.coin = 4;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3; b.coin= 4;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(3);
                    a.resource.name[0] = "Paper";  a.resource.quantity[0] = 1;
                    a.resource.name[1] = "Glass";  a.resource.quantity[1] = 1;
                    a.resource.name[2] = "Textile";  a.resource.quantity[2] = 1;
                    b.victoryPoint = 5; b.coin = 4;
                    milestones[2] = new Milestone(a,b);
                }
            }
            else if( wNumber == 4) {
                InputStream is = Files.newInputStream(Paths.get("images/babylon.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "babylon";
                Text sideText2 = new Text("Babylon - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if(side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";    a.resource.quantity[0] = 3;
                    b.specialCard = 16;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";  a.resource.quantity[0] = 4;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
                else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Textile";  a.resource.quantity[0] = 1;
                    a.resource.name[1] = "Clay";  a.resource.quantity[1] = 1;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Lumber";    a.resource.quantity[0] = 2;
                    a.resource.name[1] = "Glass";    a.resource.quantity[1] = 1;
                    b.specialCard = 17;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Clay";  a.resource.quantity[0] = 3;
                    a.resource.name[1] = "Paper";  a.resource.quantity[1] = 1;
                    b.specialCard = 16;
                    milestones[2] = new Milestone(a,b);
                }
            }
            else if( wNumber == 5) {
                InputStream is = Files.newInputStream(Paths.get("images/olympia.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "olympia";
                Text sideText2 = new Text("Olympia - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if(side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";    a.resource.quantity[0] = 2;
                    b.specialCard = 18;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
                else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";  a.resource.quantity[0] = 2;
                    b.specialCard = 19;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";    a.resource.quantity[0] = 2;
                    b.victoryPoint = 5;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Ore";  a.resource.quantity[0] = 2;
                    a.resource.name[1] = "Textile";  a.resource.quantity[1] = 1;
                    b.specialCard = 20;
                    milestones[2] = new Milestone(a,b);
                }
            }
            else if( wNumber == 6) {
                InputStream is = Files.newInputStream(Paths.get("images/halikarnassos.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "halikarnassus";
                Text sideText2 = new Text("Halikarnassos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(250);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if(side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";    a.resource.quantity[0] = 3;
                    b.specialCard = 21;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Textile";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
                else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";  a.resource.quantity[0] = 2;
                    b.specialCard = 21; b.victoryPoint = 2;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";    a.resource.quantity[0] = 3;
                    b.specialCard = 21; b.victoryPoint = 1;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(3);
                    a.resource.name[0] = "Glass";  a.resource.quantity[0] = 1;
                    a.resource.name[1] = "Textile";  a.resource.quantity[1] = 1;
                    a.resource.name[2] = "Paper";  a.resource.quantity[2] = 1;
                    b.specialCard = 21;
                    milestones[2] = new Milestone(a,b);
                }
            }
            else {
                InputStream is = Files.newInputStream(Paths.get("images/gizeh.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "gizah";
                Text sideText2 = new Text("Gizeh - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(300);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if(side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";    a.resource.quantity[0] = 3;
                    b.victoryPoint = 5;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";  a.resource.quantity[0] = 4;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
                else {
                    milestones = new Milestone[4];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";  a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";    a.resource.quantity[0] = 3;
                    b.victoryPoint = 5;
                    milestones[1] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";  a.resource.quantity[0] = 3;
                    b.victoryPoint = 5;
                    milestones[2] = new Milestone(a,b);
                    a = new Property(); b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Stone";  a.resource.quantity[0] = 4;
                    a.resource.name[1] = "Paper";  a.resource.quantity[1] = 1;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a,b);
                }
            }
            // milestones part
            HBox milestoneHB = new HBox(25);
            milestoneHB.setBackground(bg);
            for( int i = 1; i <= milestones.length; i++) {
                InputStream is = Files.newInputStream(Paths.get("images/Wonderpng/" + wName + side + i + ".png"));
                Image img = new Image(is);
                is.close();
                ImageView imgView = new ImageView(img);
                imgView.setFitHeight(40);
                if( milestones.length == 4)
                    imgView.setFitWidth(75);
                else
                    imgView.setFitWidth(110);
                milestoneHB.getChildren().add(imgView);
            }
            milestoneHB.setTranslateY(208);
            milestoneHB.setTranslateX(5);

            // coin part
            InputStream is = Files.newInputStream(Paths.get("images/coins.png"));
            Image img = new Image(is);
            is.close();
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(35);
            imgView.setFitWidth(35);
            coinText = new Text(players[pNum].stats.coin + "");
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
            shieldText = new Text(players[pNum].stats.shield + "");
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
            battleText = new Text(players[pNum].battlePoint + "");
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
            vicPointText = new Text(players[pNum].stats.victoryPoint + "");
            vicPointText.setFill(Color.WHITESMOKE);
            vicPointText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            vicPointText.setTranslateX(5);
            vicPointVB = new VBox(imgView, vicPointText);
            vicPointVB.setBackground(bg);
            vicPointVB.setTranslateY(30);
            vicPointVB.setTranslateX(125);

            // literature part
            is = Files.newInputStream(Paths.get("images/ancient-scroll.png"));
            img = new Image(is);
            is.close();
            imgView = new ImageView(img);
            imgView.setFitHeight(35);
            imgView.setFitWidth(35);
            literatureText = new Text(players[pNum].stats.literature + "");
            literatureText.setFill(Color.WHITESMOKE);
            literatureText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            literatureText.setTranslateX(5);
            literatureVB = new VBox(imgView, literatureText);
            literatureVB.setBackground(bg);
            literatureVB.setTranslateY(30);
            literatureVB.setTranslateX(165);

            // mechanic part
            is = Files.newInputStream(Paths.get("images/mechanic.png"));
            img = new Image(is);
            is.close();
            imgView = new ImageView(img);
            imgView.setFitHeight(35);
            imgView.setFitWidth(35);
            mechanicText = new Text(players[pNum].stats.mechanic + "");
            mechanicText.setFill(Color.WHITESMOKE);
            mechanicText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            mechanicText.setTranslateX(5);
            mechanicVB = new VBox(imgView, mechanicText);
            mechanicVB.setBackground(bg);
            mechanicVB.setTranslateY(30);
            mechanicVB.setTranslateX(205);

            // geometry part
            is = Files.newInputStream(Paths.get("images/geometry.png"));
            img = new Image(is);
            is.close();
            imgView = new ImageView(img);
            imgView.setFitHeight(35);
            imgView.setFitWidth(35);
            geometryText = new Text(players[pNum].stats.geometry + "");
            geometryText.setFill(Color.WHITESMOKE);
            geometryText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            geometryText.setTranslateX(5);
            geometryVB = new VBox(imgView, geometryText);
            geometryVB.setBackground(bg);
            geometryVB.setTranslateY(30);
            geometryVB.setTranslateX(245);

            getChildren().addAll(shieldVB, coinVB, battleVB, vicPointVB, literatureVB, mechanicVB, geometryVB);

            // Used Cards parts

            // Green Cards
            Rectangle card = new Rectangle( 15,20, Color.GREEN);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            greenText = new Text(players[pNum].greenCards + "");
            greenText.setFill(Color.WHITESMOKE);
            greenText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            greenText.setTranslateX(10);
            greenHB = new HBox(card, greenText);
            greenHB.setBackground(bg);
            greenHB.setTranslateY(30);
            greenHB.setTranslateX(355);
            greenHB.setPrefSize(40,20);

            // red Cards
            card = new Rectangle( 15,20, Color.RED);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            redText = new Text(players[pNum].redCards + "");
            redText.setFill(Color.WHITESMOKE);
            redText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            redText.setTranslateX(10);
            redHB = new HBox(card, redText);
            redHB.setBackground(bg);
            redHB.setTranslateY(60);
            redHB.setTranslateX(355);
            redHB.setPrefSize(40,20);

            // yellow Cards
            card = new Rectangle( 15,20, Color.YELLOW);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            yellowText = new Text(players[pNum].yellowCards + "");
            yellowText.setFill(Color.WHITESMOKE);
            yellowText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            yellowText.setTranslateX(10);
            yellowHB = new HBox(card, yellowText);
            yellowHB.setBackground(bg);
            yellowHB.setTranslateY(90);
            yellowHB.setTranslateX(355);
            yellowHB.setPrefSize(40,20);

            // grey Cards
            card = new Rectangle( 15,20, Color.GREY);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            greyText = new Text(players[pNum].greyCards + "");
            greyText.setFill(Color.WHITESMOKE);
            greyText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            greyText.setTranslateX(10);
            greyHB = new HBox(card, greyText);
            greyHB.setBackground(bg);
            greyHB.setTranslateY(120);
            greyHB.setTranslateX(355);
            greyHB.setPrefSize(40,20);

            // brown Cards
            card = new Rectangle( 15,20, Color.BROWN);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            brownText = new Text(players[pNum].brownCards + "");
            brownText.setFill(Color.WHITESMOKE);
            brownText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            brownText.setTranslateX(10);
            brownHB = new HBox(card, brownText);
            brownHB.setBackground(bg);
            brownHB.setTranslateY(150);
            brownHB.setTranslateX(355);
            brownHB.setPrefSize(40,20);

            // blue Cards
            card = new Rectangle( 15,20, Color.BLUE);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            blueText = new Text(players[pNum].blueCards + "");
            blueText.setFill(Color.WHITESMOKE);
            blueText.setFont(Font.font("Kalam", FontPosture.ITALIC,20));
            blueText.setTranslateX(10);
            blueHB = new HBox(card, blueText);
            blueHB.setBackground(bg);
            blueHB.setTranslateY(180);
            blueHB.setTranslateX(355);
            blueHB.setPrefSize(40,20);

            getChildren().addAll(greenHB, redHB, yellowHB, greyHB, brownHB, blueHB, milestoneHB);

            // Resource Part
            resourcesHB = new HBox( 5);
            for(int i = 0; i < players[pNum].resourceCount; i++) {
                for(int k = 0; k < players[pNum].resources[i].quantity[0]; k++) {
                    VBox vb = new VBox();
                    vb.setBackground(bg);
                    for (int j = 0; j < players[pNum].resources[i].quantity.length; j++) {
                        is = Files.newInputStream(Paths.get("images/" + players[pNum].resources[i].name[j] + ".png"));
                        img = new Image(is);
                        is.close();
                        imgView = new ImageView(img);
                        imgView.setFitHeight(min(25, 50 / players[pNum].resources[i].quantity.length));
                        imgView.setFitWidth(25);
                        vb.getChildren().addAll(imgView);
                    }
                    resourcesHB.getChildren().addAll(vb);
                }
            }
            resourcesHB.setTranslateY(100);
            resourcesHB.setTranslateX(5);

            // special cards part
            HBox specialHB = new HBox( 5);
            for(int i = 1; i < 16; i++) {
                if(players[pNum].specialCards[i] && i != 4 && i != 5){
                    VBox vb = new VBox();
                    vb.setBackground(bg);
                    is = Files.newInputStream(Paths.get("images/cardskill/" + i + ".png"));
                    img = new Image(is);
                    is.close();
                    imgView = new ImageView(img);
                    imgView.setFitHeight(30);
                    imgView.setFitWidth(60);
                    vb.getChildren().addAll(imgView);
                    specialHB.getChildren().add(vb);
                }
            }
            specialHB.setTranslateY(160);
            specialHB.setTranslateX(5);
            getChildren().addAll(resourcesHB,specialHB);
        }
    }
    public class Milestone extends Pane {
        Property cost, benefit;
        public Milestone( Property cost, Property benefit ) throws Exception {
            this.cost = cost;
            this.benefit = benefit;
        }
    }

    /**
     * Enum for possible card actions
     */
    private enum CardAction { SELL, BURY, BUILD; }
    public class Card extends Pane {
        String name, color;
        javafx.scene.control.Button sellButton,buryButton,buildButton;
        Rectangle board;
        GamePage.Property cost, benefit;
        boolean isUsed;

        public Card(String name, String color, GamePage.Property a, GamePage.Property b ) throws  Exception{
            this.name = name;
            this.color = color;
            this.isUsed = false;
            cost = a;
            benefit = b;
            //bg = new Background( new BackgroundFill(Color.rgb(109,132,118,0.1), CornerRadii.EMPTY, Insets.EMPTY));
            board = new Rectangle(140,190,Color.rgb(109,132,118,1));
            board.setArcHeight(15);
            board.setArcWidth(15);
            getChildren().add(board);
            InputStream is = Files.newInputStream(Paths.get("images/card images/" + name + ".png"));
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
            if (this.isUsed)
                return;
            board.setOpacity(0.65);
            sellButton = new javafx.scene.control.Button("SELL");
            sellButton.setTranslateX(55);
            sellButton.setTranslateY(30);
            getChildren().add(sellButton);
            sellButton.setOnMouseClicked(event -> {
                try {
                    playCard(0, CardAction.SELL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            buryButton = new javafx.scene.control.Button("UPGRADE WONDER");
            buryButton.setTranslateX(10);
            buryButton.setTranslateY(70);
            getChildren().add(buryButton);
            buryButton.setOnMouseClicked(event -> {
                try {
                    playCard(0, CardAction.BURY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            buildButton = new Button("BUILD");
            buildButton.setTranslateX(50);
            buildButton.setTranslateY(110);
            getChildren().add(buildButton);
            buildButton.setOnMouseClicked(event -> {
                try {
                    playCard(0, CardAction.BUILD);
                } catch (Exception e) {
                    e.printStackTrace();
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
            if (this.isUsed)
                return;
            board.setOpacity(1);
            getChildren().remove(sellButton);
            getChildren().remove(buryButton);
            getChildren().remove(buildButton);
        }
        public void deleteCard() throws IOException {
            board.setOpacity(1);
            getChildren().remove(sellButton);
            getChildren().remove(buryButton);
            getChildren().remove(buildButton);
            InputStream is = Files.newInputStream(Paths.get("images/card images/age" + currentAge + ".png"));
            Image img = new Image(is);
            is.close();
            board.setFill(new ImagePattern(img));
            this.setOnMouseEntered(event -> {
            });
            this.setOnMouseExited(event -> {
            });
            this.isUsed = true;
        }

        /**
         * Plays card for a player randomly
         * @param playerNum player index
         * @param action what to do with this card
         * @return if the card played successfully
         * @throws Exception when turn cannot be ended(Exception)
         * @throws IOException when card cannot be deleted(IOException)
         */
        public boolean playCard(int playerNum, CardAction action) throws IOException, Exception {
            if (isUsed) // check if it is used or not
                return false;

            switch (action) {
                case SELL:
                    Property tmp = new GamePage.Property();
                    tmp.coin = 3;
                    gainBenefit( playerNum, false, tmp, "", "");
                    cardsAtStake[noOfCardsAtStake] = this;
                    noOfCardsAtStake++;
                    break;

                case BURY:
                    if (players[playerNum].milestoneDone == wb[playerNum].milestones.length) {
                        if (!isBot(playerNum))
                            giveError("All wonders have already built");
                        return false;
                    }

                    if (!checkResources(playerNum, true, cost)) {
                        if (!isBot(playerNum))
                            giveError("Not enough resources");
                        return false;
                    }

                    gainBenefit(playerNum, true, benefit, "", "");
                    break;

                case BUILD:
                    if (!checkResources(playerNum, false, cost)) {
                        if (!isBot(playerNum))
                            giveError("Not enough resources");
                        return false;
                    }

                    if(cost.coin != 0)
                        benefit.coin--;
                    gainBenefit(playerNum, false, benefit, name, color);

                    break;
            }

            deleteCard();
            if (!isBot(playerNum))
                endTurn();
            return true;
        }
    }
}
