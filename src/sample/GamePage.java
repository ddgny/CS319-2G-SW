package sample;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.io.*;

import javafx.scene.Group;

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
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.lang.Math;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Game page class implements game actions
 */
public class GamePage extends Scene {
    public static WonderBoard[] wb;
    public static Player[] players;
    private ImageView[] cardTicks;
    private Card[][] cards;
    private Card[] cardsAtStake;
    private Stage window;
    private StackPane sp;
    private static MediaPlayer mediaPlayer;
    //private StackPane sp,endgame;
    private String side;
    Text goldValue;
    Text goldValueRight;
    private int currentAge, currentTurn, noOfCardsAtStake;
    // mode = ally -> -1 , normal -> 0 , story -> 1,2,3,4,5...
    private int mode;
    
    /**
     * Resource class implements resource objects
     */
    private class Resource {
        String[] name;
        int[] quantity;
        public Resource( int optional) {
            name = new String[optional];
            quantity = new int[optional];
        }
        /**
         *This method is used for the constructor
         *Creates a new Resource with the given dummy
         *@param dummy is new Resource
         */
        public Resource( Resource dummy) {
            this.name = new String[dummy.name.length];
            this.quantity = new int[dummy.quantity.length];
            for( int i = 0; i < this.quantity.length; i++) {
                this.name[i] = dummy.name[i];
                this.quantity[i] = dummy.quantity[i];
            }
        }
    }
    /**
     * Represents a Properties of gane
     */
    public  class Property {
        int coin, shield, mechanic, literature, geometry, victoryPoint;
        String requiredBuilding;
        Resource resource;
        int specialCard;
        
        /**
         * Represents constructor of the Property class
         */
        public Property(){
            coin = shield = mechanic = literature = geometry = victoryPoint = specialCard = 0;
            requiredBuilding = "";
            resource = new Resource(0);
        }
    }
    /**
     * Represents a new Player participating in a game
     */
    public class Player {
        String name;
        int battlePoint, greenCards, redCards, yellowCards, greyCards, purpleCards, brownCards, blueCards, milestoneDone;
        String[] buildings;
        Resource[] resources;
        Resource leftTradedResources, rightTradedResources;
        int resourceCount, buildingsCount;
        boolean[] specialCards;
        Property stats;
        
        /**
         * Represents constructor for the Player class 
         * Takes String tmp to create new Player
         * @param tmp name of the new Player
         */
        public Player(String tmp) {
            stats = new Property();
            stats.coin = 3;
            name = tmp;
            battlePoint = greenCards = redCards = yellowCards = greyCards = purpleCards = brownCards = blueCards = milestoneDone = buildingsCount = resourceCount = 0;
            buildings = new String[22];
            resources = new Resource[22];
            rightTradedResources = new Resource(0);
            leftTradedResources = new Resource(0);
            specialCards = new boolean[23];
            for(int i = 0; i < 23; i++) specialCards[i] = false;
        }
        /**
         * Adds Resource objects to resources array
         * @param new Resource will be added
         */
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

    /**
     * Constructor for class GamePage
     * @param sp 
     * @param mainmenu main menu screen
     * @param window
     * @param name player name
     * @param side wanderboards side either A or B
     * @param sMode indicates game mode
     * @throws Exception 
     */
    public GamePage(StackPane sp, Scene mainmenu, Stage window, String name, String side, int sMode) throws Exception {
        super(sp, Main.primaryScreenBounds.getWidth(), Main.primaryScreenBounds.getHeight());
        mode = sMode;
        noOfCardsAtStake = 0;

        cardsAtStake = new Card[80];
        currentAge = currentTurn = 1;
        this.window = window;
        this.sp = sp;
        this.side = side;
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

        // save game
        if(mode > 1) {
            try (BufferedWriter bw = new BufferedWriter(new PrintWriter("save.txt"))) {
                bw.write(name);
                bw.newLine();
                bw.write(mode + "");
                giveError("Game Saved");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
            try {
                makeTrade( 0, 3, choice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        comboBoxTradeButton.setDisable(true);


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
            try {
                makeTrade(0, 1, choice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        comboBoxTradeButtonRight.setDisable(true);
        sp.getChildren().addAll(comboBoxTradeButtonRight,comboHboxRight);

        // Putting Pause Button into GamePage
        OptionsPage.PauseButton pb = new OptionsPage.PauseButton();
        pb.setOnMouseClicked( event -> {
            Rectangle r = new Rectangle(0,0,1700,1700);
            r.setFill(Color.rgb(0,0,0,0.6));
            sp.getChildren().add(r);
            VBox menu2 = new VBox(40);
            menu2.setTranslateX(0);
            menu2.setTranslateY(300);

            // Pause Menu opens up when Pause Button is clicked
            Main.MenuButton btnResume = new Main.MenuButton("Resume Game");
            Main.MenuButton btnMain = new Main.MenuButton("Main Menu");
            Main.MenuButton btnExit2 = new Main.MenuButton("Exit Game");
            // Resume Button
            btnResume.setOnMouseClicked( event2 -> {
                btnResume.setVisible(false);
                btnExit2.setVisible(false);
                btnMain.setVisible(false);
                menu2.getChildren().removeAll(btnResume, btnExit2, btnMain);
                sp.getChildren().remove(menu2);
                sp.getChildren().remove(r);
            });
            // Exit Button
            btnExit2.setOnMouseClicked( event2 -> {
                System.exit(0);
            });
            // Main Menu Button
            btnMain.setOnMouseClicked( event2 -> {
                btnResume.setVisible(false);
                btnExit2.setVisible(false);
                btnMain.setVisible(false);
                menu2.getChildren().removeAll(btnResume, btnExit2, btnMain);
                sp.getChildren().remove(menu2);
                sp.getChildren().remove(r);
                window.setScene( mainmenu);
            });
            menu2.getChildren().addAll(btnResume, btnMain, btnExit2);
            sp.getChildren().add(menu2);

        });
        // Mute and Unmute Buttons in GamePage
        Pane mute2 = new Pane();
        Pane unmute2 = new Pane();
        mute2.setMaxSize(50,50);
        unmute2.setMaxSize(50,50);
        try {
            InputStream is2 = Files.newInputStream(Paths.get("images/mute.png"));
            Image img2 = new Image(is2);
            is2.close();
            ImageView imgViewMute = new ImageView(img2);
            imgViewMute.setFitHeight(50);
            imgViewMute.setFitWidth(50);
            mute2.getChildren().add(imgViewMute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            InputStream is3 = Files.newInputStream(Paths.get("images/unmute.png"));
            Image img3 = new Image(is3);
            is3.close();
            ImageView imgViewUnmute = new ImageView(img3);
            imgViewUnmute.setFitHeight(50);
            imgViewUnmute.setFitWidth(50);
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
        mute2.setTranslateX(600);
        mute2.setTranslateY(-380);
        unmute2.setTranslateX(600);
        unmute2.setTranslateY(-380);
        pb.setTranslateX(660);
        pb.setTranslateY(-380);
        sp.getChildren().add(pb);

        players[0] = new Player(name);
//        players[0].stats.victoryPoint = 30;
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
        // Green tick symbol to track if a specific card can be built for free
        InputStream is2 = Files.newInputStream(Paths.get("images/tick.png"));
        Image imgTick = new Image(is2);
        is2.close();
        // Putting all ticks into an array
        cardTicks = new ImageView[7];
        for(int i = 0; i < 7; i++){
            cardTicks[i] = new ImageView(imgTick);
            cardTicks[i].setFitWidth(40);
            cardTicks[i].setFitHeight(40);
            cardTicks[i].setVisible(false);
        }
        // Setting coordinates relevant to card coordinates
        cardTicks[0].setTranslateX(-180); cardTicks[0].setTranslateY(10);
        cardTicks[1].setTranslateX(20); cardTicks[1].setTranslateY(10);
        cardTicks[2].setTranslateX(-380); cardTicks[2].setTranslateY(10);
        cardTicks[3].setTranslateX(-580); cardTicks[3].setTranslateY(10);
        cardTicks[4].setTranslateX(-80); cardTicks[4].setTranslateY(210);
        cardTicks[5].setTranslateX(-280); cardTicks[5].setTranslateY(210);
        cardTicks[6].setTranslateX(-480); cardTicks[6].setTranslateY(210);

        distributeWonders( sp, side, name);

        sp.getChildren().addAll(wb);
        sp.getChildren().addAll(cardTicks);

        // story mode features
        if(mode == 1){
            players[1].name = "General Seleucus";
            players[2].name = "General Cassander";
            players[3].name = "General Ptolemy";
            reDrawWonders();
            slidingText("Welcome to 7 wonders\n\nYou are one of the most powerful general of the kingdom of Macedon. Alexander the Great give tasks to his generals to rebuild one of the seven wonders.\nThe general who makes the best wonder will win.\n\nObejectives:\n- Collect the most victory point\n- Complete your wonder");
        }
        else if( mode == 2) {
            players[1].name = "Seleucid Empire";
            players[3].name = "Ptolemaic Dynasty";
            players[2].name = "The kingdom of Cassander";
            reDrawWonders();
            slidingText("Alexander the Great died at a young age and the kingdom of Macedon is divided to his generals according to his will. However people of yor region don't want you to rule. So you need to improve your region and earn their trust.\n\nObjectives:\n- Collect the most victory point\n- Collect at least 20 victory points from blue cards or your wonder");
        }
        else if( mode == 3) {
            players[1].name = "Roman Republic";
            players[2].name = "Roman Republic";
            players[3].name = "Roman Republic";
            players[1].stats.shield = 5;
            players[2].stats.shield = 5;
            players[3].stats.shield = 5;
            reDrawWonders();
            slidingText("Roman Republic started to invade your lands. You need to protect your people and defeat the large army of Romans.\n\n Objecives:\n- Collect the most victory points\n- Your battle point can not be negative at the end");
        }
        else if( mode == 4) {
            players[1].name = "Seleucid Empire";
            players[2].name = "Roman Republic";
            players[3].name = "Ptolemaic Dynasty";
            reDrawWonders();
            slidingText("After the invasion, your friend Aristotle wants you to improve the region's science buildings and make field to people to do research. You agreed the demands of Aristotle.\n\n- Collect the most victory points\n- Collect at least 25 victory points from green cards");
        }
    }
    /**
     *player 1 receives the resource
     * player 2 gains the gold
     * side is to check if player 2 is on the right or left of player 1
     */
    public void makeTrade(int p1, int p2, String choice) throws Exception {
        boolean isRight = ((p1 + 1) % 4 == p2);
        int worth = 2;
        Property tmp = new Property();
        // determine the worth of the trade
        if ((choice.equals("Ore") || choice.equals("Lumber") || choice.equals("Stone") || choice.equals("Clay")) && ((players[p1].specialCards[1] && isRight) || (players[p1].specialCards[2] && !isRight) || players[p1].specialCards[19]))
            worth = 1;
        if((choice.equals("Textile") || choice.equals("Glass") || choice.equals("Paper")) && players[p1].specialCards[3])
            worth = 1;
        if( players[p1].stats.coin < worth) {
            if( p1 == 0)
                giveError("Not enough coin");
            return;
        }

        // check the resources
        if( isRight) {
            tmp.resource = new Resource( players[p1].rightTradedResources.quantity.length + 1);
            for( int i = 0 ; i < players[p1].rightTradedResources.quantity.length; i++) {
                tmp.resource.name[i] = players[p1].rightTradedResources.name[i];
                tmp.resource.quantity[i] = players[p1].rightTradedResources.quantity[i];
            }
            tmp.resource.name[players[p1].rightTradedResources.quantity.length] = choice;
            tmp.resource.quantity[players[p1].rightTradedResources.quantity.length] = 1;
        }
        else {
            tmp.resource = new Resource( players[p1].leftTradedResources.quantity.length + 1);
            for( int i = 0 ; i < players[p1].leftTradedResources.quantity.length; i++) {
                tmp.resource.name[i] = players[p1].leftTradedResources.name[i];
                tmp.resource.quantity[i] = players[p1].leftTradedResources.quantity[i];
            }
            tmp.resource.name[players[p1].leftTradedResources.quantity.length] = choice;
            tmp.resource.quantity[players[p1].leftTradedResources.quantity.length] = 1;
        }
        if( checkResources( p2, false, tmp)) {
            if(isRight) players[p1].rightTradedResources = new Resource( tmp.resource);
            else players[p1].leftTradedResources = new Resource( tmp.resource);
            players[p1].stats.coin -= worth;
            players[p2].stats.coin += worth;
            reDrawWonders();
        }
        else
            giveError("Your neighbor doesn't have that resource");
    }
    // Method for making battles, it gets input age and it is called once per age.
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
            if( mode == -1) {
                if (players[0].stats.shield + players[2].stats.shield < players[1].stats.shield + players[3].stats.shield) {
                    players[1].battlePoint += 5;
                    players[3].battlePoint += 5;
                    players[2].battlePoint -= 1;
                    players[0].battlePoint -= 1;
                } else if (players[0].stats.shield + players[2].stats.shield > players[1].stats.shield + players[3].stats.shield) {
                    players[2].battlePoint += 5;
                    players[0].battlePoint += 5;
                    players[1].battlePoint -= 1;
                    players[3].battlePoint -= 1;
                }
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
    /**
     * Method that end the turn
     * @throws Exception
     */
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

        // end age
        if( currentTurn == 7) {
            currentTurn = 1;
            makeBattles(currentAge);
            currentAge++;
            if( currentAge == 4) {
                battleResultsPopup();
                return;
            }
            for( int i = 0; i < 28; i++) {
                if( !cards[currentAge - 2][i].isUsed) {
                    cardsAtStake[noOfCardsAtStake] = cards[currentAge - 2][i];
                    noOfCardsAtStake++;
                }
            }
            for( int i = 0; i < 4; i++) {
                if ( players[i].specialCards[22])
                    players[i].specialCards[18] = true;
            }
        }
        for(int i = ((lastTurn - 1) % 4) * 7; i <= ((lastTurn - 1) % 4) * 7 + 6; i++) sp.getChildren().remove(cards[lastAge - 1][i]);
        for(int i = ((currentTurn - 1) % 4) * 7; i <= ((currentTurn - 1) % 4) * 7 + 6; i++) sp.getChildren().add(cards[currentAge - 1][i]);
        for(int i = 0; i < 4; i++) { players[i].leftTradedResources = new Resource(0); players[i].rightTradedResources = new Resource( 0);}
        reDrawWonders();
        sp.getChildren().removeAll(cardTicks);
        reDrawTick();
        sp.getChildren().addAll(cardTicks);
        if( currentTurn == 1) battleResultsPopup();

    }
    /**
     * This method implements pop-up screen for end of the ages to show war situations
     * @throws Exception
     */
    public void battleResultsPopup() throws Exception {

        // popup screen at the end of the ages
        Media sound = new Media(new File("sounds/battle.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        int endAge=currentAge-1;
        String textToSend = "";
        if (players[0].stats.shield == players[1].stats.shield) {
            if (players[0].stats.shield == players[3].stats.shield)
                textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n"+"The battles was drawn!!" ;
            else {
                if (players[0].stats.shield > players[3].stats.shield)
                    textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n" + players[0].name + " won the battle against " + players[3].name + "\n But drawn the battle against " + players[1].name;
                else
                    textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n" + players[0].name + " lost the battle against " + players[3].name + "\n But drawn the battle against " + players[1].name;
            }

        }
        //if player0 has more soldier than player1
        if (players[0].stats.shield > players[1].stats.shield) {
            if (players[0].stats.shield > players[3].stats.shield)
                textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n" + players[0].name + " won all the battles!!";
            else if (players[0].stats.shield < players[3].stats.shield)
                textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n" + players[0].name + " won the battle against " + players[1].name + "\n But lost the battle against " + players[3].name;
            else
                textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n" + players[0].name + " won the battle against " + players[1].name + "\n But drawn the battle against " + players[3].name;

        }
        if (players[0].stats.shield < players[1].stats.shield) {
            if (players[0].stats.shield < players[3].stats.shield)
                textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n" + players[0].name + " lost all the battles!!";
            else if (players[0].stats.shield > players[3].stats.shield)
                textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n" + players[0].name + " won the battle against " + players[3].name + "\n But lost the battle against " + players[1].name;
            else
                textToSend = "Age "+ endAge +" Ended!! \n "+players[0].name+" did two battles. \n" + players[0].name + " lost the battle against " + players[1].name + "\n But drawn the battle against " + players[3].name;
        }
        if( mode == -1) {
            textToSend += "\n\nAlliances have been made and we ";
            if(players[0].stats.shield + players[2].stats.shield < players[1].stats.shield + players[3].stats.shield)
                textToSend += "lost";
            else if(players[0].stats.shield + players[2].stats.shield == players[1].stats.shield + players[3].stats.shield)
                textToSend += "drawn";
            else
                textToSend += "won";
            textToSend += " the battle.";
        }
        slidingText( textToSend);
    }
    /**
     * Putting green tick in the upper right corner of free cards.
     */
    public void reDrawTick(){
        for(int i = 0; i < 7; i++){
            cardTicks[i].setVisible(false);
        }
        for(int i = ((currentTurn - 1) % 4) * 7; i <= ((currentTurn - 1) % 4) * 7 + 6; i++){
            for (int j = 0; j < players[0].buildingsCount; j++) {
                if (players[0].buildings[j].contains(cards[currentAge - 1][i].cost.requiredBuilding) && !cards[currentAge - 1][i].cost.requiredBuilding.equals("") && !cards[currentAge -1][i].isUsed) {
                    cardTicks[i%7].setVisible(true);
                    //System.out.println("FREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE" + i%7);
                }
            }
        }

    }
    /**
     * Method for sliding text
     * @throws Exception
     */
    public void slidingText(String text) throws Exception{
        final String content = text;
        final Text textArea = new Text(10, 20, "");
        textArea.setWrappingWidth(550);
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

        Rectangle bg = new Rectangle(1250,680);
        Rectangle bg2 = new Rectangle(1350,680);
        bg2.setFill(Color.BLACK);
        bg2.setEffect( new GaussianBlur(3.5));
        bg2.setTranslateZ(100);
        bg2.setOpacity(0.9);
        InputStream ageWar = Files.newInputStream(Paths.get("images/AgeWar.png"));
        Image img1 = new Image(ageWar);
        bg.setOpacity(0.5);
        bg.setFill(new ImagePattern(img1));
        //bg.setFill(Color.BLACK);
        bg.setEffect( new GaussianBlur(3.5));
        bg.setTranslateZ(100);
        textArea.setTranslateZ(100);
        InputStream continueImg = Files.newInputStream(Paths.get("images/icons8.png"));
        Image img2 = new Image(continueImg);
//        Button btnCont =new Button("Continue");
//        btnCont.setTranslateX(0);
//        btnCont.setTranslateY(0);
        Rectangle contRec =new Rectangle(200,200);
        contRec.setFill(new ImagePattern(img2));
        contRec.setTranslateX(550);
        contRec.setTranslateY(200);
        contRec.setOnMouseClicked(event -> {
            try {
                sp.getChildren().removeAll(bg2,bg,textArea,contRec);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(currentAge > 3) {
                try {
                    endGameText();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        sp.getChildren().addAll(bg2,bg, textArea,contRec);

    }
    /**
     * Method that shows stats of the players at the end of the game
     * @throws Exception
     */
    public void endGameText()throws Exception{
        int[] total = {0,0,0,0};


        int[] endCoin ={0,0,0,0};


        int[] endScience = {0,0,0,0};


        int[] endSpecialPurple = {0,0,0,0};


        int[] endSpecialYellow = {0,0,0,0};

        int[ ] endSpecial = {0,0,0,0};
        Rectangle bg3 = new Rectangle(1380,680);
        bg3.setFill(Color.BLACK);
        bg3.setEffect( new GaussianBlur(3.5));
        bg3.setTranslateZ(100);
        bg3.setOpacity(0.9);
//        Rectangle contRec =new Rectangle(200,200);
//        InputStream contImg = Files.newInputStream(Paths.get("images/icons8.png"));
//        Image img3 = new Image(contImg);
//        contRec.setFill(new ImagePattern(img3));
//        contRec.setTranslateX(550);
//        contRec.setTranslateY(200);

        Rectangle bg4 = new Rectangle(1350,680);
        InputStream gameOver = Files.newInputStream(Paths.get("images/game-over4.jpg"));
        Image img4 = new Image(gameOver);
        bg4.setOpacity(0.9);

        bg4.setFill(new ImagePattern(img4));

        for (int i = 0; i < 4; i++)
            endCoin[i] = players[i].stats.coin / 3;

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
            total[i] = endCoin[i] + endScience[i] + endSpecialPurple[i] + endSpecialYellow[i] + players[i].stats.victoryPoint
                    + players[i].battlePoint;


        Text player0  = new Text(players[0].name);
        player0.setFill(Color.WHITESMOKE);
        player0.setFont(Font.font("Kalam",20));
        Text player1  = new Text(players[1].name);
        player1.setFill(Color.WHITESMOKE);
        player1.setFont(Font.font("Kalam",20));
        Text player2  = new Text(players[2].name);
        player2.setFill(Color.WHITESMOKE);
        player2.setFont(Font.font("Kalam",20));
        Text player3  = new Text(players[3].name);
        player3.setFill(Color.WHITESMOKE);
        player3.setFont(Font.font("Kalam",20));
        TextField player0name = new TextField(players[0].name);

        Text player0coin = new Text(players[0].stats.coin/3+"");
        //player0coin.setTranslateX(150);
        player0coin.setFill(Color.YELLOW);
        player0coin.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player1coin = new Text(players[1].stats.coin/3+"");
        //player1coin.setTranslateX(170);
        player1coin.setFill(Color.YELLOW);
        player1coin.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player2coin = new Text(players[2].stats.coin/3+"");
        //player2coin.setTranslateX(170);
        player2coin.setFill(Color.YELLOW);
        player2coin.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player3coin = new Text(players[3].stats.coin/3+"");
        //player3coin.setTranslateX(170);
        player3coin.setFill(Color.YELLOW);
        player3coin.setFont(Font.font("Kalam",FontWeight.BOLD,23));



        Text player0battlePoints = new Text(players[0].battlePoint+"");
       // player0battlePoints.setTranslateX(300);
        player0battlePoints.setFill(Color.RED);
        player0battlePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player1battlePoints = new Text(players[1].battlePoint+"");
        //player1battlePoints.setTranslateX(320);
        player1battlePoints.setFill(Color.RED);
        player1battlePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player2battlePoints = new Text(players[2].battlePoint+"");
        //player2battlePoints.setTranslateX(320);
        player2battlePoints.setFill(Color.RED);
        player2battlePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player3battlePoints = new Text(players[3].battlePoint+"");
        //player3battlePoints.setTranslateX(320);
        player3battlePoints.setFill(Color.RED);
        player3battlePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));


        Text player0sciencePoints = new Text(endScience[0]+"");
        //player0sciencePoints.setTranslateX(450);
        player0sciencePoints.setFill(Color.GREEN);
        player0sciencePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player1sciencePoints = new Text(endScience[1]+"");
        //player1sciencePoints.setTranslateX(470);
        player1sciencePoints.setFill(Color.GREEN);
        player1sciencePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player2sciencePoints = new Text(endScience[2]+"");
//        player2sciencePoints.setTranslateX(470);
        player2sciencePoints.setFill(Color.GREEN);
        player2sciencePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player3sciencePoints = new Text(endScience[3]+"");
//        player3sciencePoints.setTranslateX(470);
        player3sciencePoints.setFill(Color.GREEN);
        player3sciencePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));








        Text player0specialcards = new Text(endSpecial[0]+"");
//        player0specialcards.setTranslateX(650);
        player0specialcards.setFill(Color.PURPLE);
        player0specialcards.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player1specialcards = new Text(endSpecial[1]+"");
//        player1specialcards.setTranslateX(670);
        player1specialcards.setFill(Color.PURPLE);
        player1specialcards.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player2specialcards = new Text(endSpecial[2]+"");
//        player2specialcards.setTranslateX(670);
        player2specialcards.setFill(Color.PURPLE);
        player2specialcards.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player3specialcards = new Text(endSpecial[3]+"");
//        player3specialcards.setTranslateX(680);
        player3specialcards.setFill(Color.PURPLE);
        player3specialcards.setFont(Font.font("Kalam",FontWeight.BOLD,23));




        Text player0bluePoints = new Text(players[0].stats.victoryPoint+"");
//        player0bluePoints.setTranslateX(860);
        player0bluePoints.setFill(Color.BLUE);
        player0bluePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player1bluePoints = new Text(players[1].stats.victoryPoint+"");
//        player1bluePoints.setTranslateX(870);
        player1bluePoints.setFill(Color.BLUE);
        player1bluePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player2bluePoints = new Text(players[2].stats.victoryPoint+"");
//        player2bluePoints.setTranslateX(870);
        player2bluePoints.setFill(Color.BLUE);
        player2bluePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player3bluePoints = new Text(players[3].stats.victoryPoint+"");
//        player3bluePoints.setTranslateX(880);
        player3bluePoints.setFill(Color.BLUE);
        player3bluePoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));




        Text player0totalPoints = new Text(total[0]+"");
//        player0totalPoints.setTranslateX(1010);
        player0totalPoints.setFill(Color.WHITESMOKE);
        player0totalPoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player1totalPoints = new Text(total[1]+"");
//        player1totalPoints.setTranslateX(1020);
        player1totalPoints.setFill(Color.WHITESMOKE);
        player1totalPoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player2totalPoints = new Text(total[2]+"");
//        player2totalPoints.setTranslateX(1020);
        player2totalPoints.setFill(Color.WHITESMOKE);
        player2totalPoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text player3totalPoints = new Text(total[3]+"");
//        player3totalPoints.setTranslateX(1030);
        player3totalPoints.setFill(Color.WHITESMOKE);
        player3totalPoints.setFont(Font.font("Kalam",FontWeight.BOLD,23));

        Text nameText = new Text("Player Names");
        nameText.setFill(Color.WHITESMOKE);
        nameText.setFont(Font.font("Kalam",20));

        Text coinText =  new Text("Coin Points");
        coinText.setFill(Color.YELLOW);
        coinText.setFont(Font.font("Kalam",20));

        Text battlePointsText = new Text("Battle Points");
        battlePointsText.setFill(Color.RED);
        battlePointsText.setFont(Font.font("Kalam",20));

        Text sciencePointsText = new Text("Science Points");
        sciencePointsText.setFill(Color.GREEN);
        sciencePointsText.setFont(Font.font("Kalam",20));

        Text specialcardPointsText = new Text("Special Card Points");
        specialcardPointsText.setFill(Color.PURPLE);
        specialcardPointsText.setFont(Font.font("Kalam",20));

        Text bluePoints = new Text("Blue Card Points");
        bluePoints.setFill(Color.BLUE);
        bluePoints.setFont(Font.font("Kalam",20));

        Text totalPointsText = new Text("Total Points");
        totalPointsText.setFill(Color.WHITESMOKE);
        totalPointsText.setFont(Font.font("Kalam",20));

        HBox statsBox = new HBox(nameText,coinText,battlePointsText,sciencePointsText,specialcardPointsText,bluePoints,totalPointsText);
        statsBox.setTranslateX(120);
        statsBox.setTranslateY(330);
        statsBox.setSpacing(65);

        HBox player0box = new HBox(player0,player0coin,player0battlePoints,player0sciencePoints,player0specialcards,player0bluePoints,player0totalPoints);
        player0box.setSpacing(10);
        player0box.setTranslateY(380);
        player0box.setTranslateX(120);

        HBox player1box = new HBox(player1,player1coin,player1battlePoints,player1sciencePoints,player1specialcards,player1bluePoints,player1totalPoints);
        player1box.setTranslateY(430);
        player1box.setSpacing(10);
        player1box.setTranslateX(120);


        HBox player2box = new HBox(player2,player2coin,player2battlePoints,player2sciencePoints,player2specialcards,player2bluePoints,player2totalPoints);
        player2box.setTranslateY(480);
        player2box.setSpacing(10);
        player2box.setTranslateX(120);

        HBox player3box = new HBox(player3,player3coin,player3battlePoints,player3sciencePoints,player3specialcards,player3bluePoints,player3totalPoints);
        player3box.setTranslateY(530);
        player3box.setSpacing(10);
        player3box.setTranslateX(120);

        HBox menu6 = new HBox(140);
        menu6.setTranslateX(450);
        menu6.setTranslateY(250);


        Main.MenuButton btnExit2 = new Main.MenuButton("Return to menu");
        Main.MenuButton btnContinue = new Main.MenuButton("Continue");
        Main.MenuButton btnPlayAgain = new Main.MenuButton("PlayAgain");

        btnExit2.setOnMouseClicked( event2 -> {
            btnExit2.setVisible(false);
            btnContinue.setVisible(false);
            menu6.getChildren().removeAll(btnExit2, btnContinue);
//            sp.getChildren().remove(menu6);
            sp.getChildren().removeAll(bg3,bg4,statsBox,player0box,player1box,player2box,player3box,menu6);
            window.setScene(Main.mainMenu);
        });
        btnContinue.setOnMouseClicked( event2 -> {
            Scene scene;
            if (mode < 4) {
                try {
                    scene = new GamePage( new StackPane(), Main.mainMenu, window, players[0].name, side, mode + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    scene = new CreditsPage( new StackPane(), Main.mainMenu, window, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                scene = new GamePage( new StackPane(), Main.mainMenu, window, players[0].name, side, mode + 1);
                window.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        btnPlayAgain.setOnMouseClicked( mouseEvent -> {
            try {
                Scene scene = new GamePage( new StackPane(), Main.mainMenu, window, players[0].name, side, mode);
                window.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        boolean isContinue = false;
        if( mode == 1 && players[0].milestoneDone == 3 && total[0] > total[1] && total[0] > total[2] && total[0] > total[3]) isContinue = true;
        else if( mode == 2 && players[0].stats.victoryPoint > 19 && total[0] > total[1] && total[0] > total[2] && total[0] > total[3]) isContinue = true;
        else if( mode == 3  && total[0] > total[1] && total[0] > total[2] && total[0] > total[3] && players[0].battlePoint > -1) isContinue = true;
        if(isContinue)
            menu6.getChildren().addAll(btnExit2, btnContinue);
        else
            menu6.getChildren().addAll(btnExit2, btnPlayAgain);


//        contRec.setOnMouseClicked(event -> {
//            try {
//                sp.getChildren().removeAll(bg4,statsBox,player0box,player1box,player2box,player3box,contRec);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        });

        VBox namesbox = new VBox(nameText,player0,player1,player2,player3);
        namesbox.setTranslateY(330);
        namesbox.setSpacing(20);
        VBox coinsbox = new VBox(coinText,player0coin,player1coin,player2coin,player3coin);
        coinsbox.setTranslateY(330);
        coinsbox.setSpacing(20);
        VBox battlepointbox = new VBox(battlePointsText,player0battlePoints,player1battlePoints,player2battlePoints,player3battlePoints);
        battlepointbox.setTranslateY(330);
        battlepointbox.setSpacing(20);
        VBox sciencepointbox = new VBox(sciencePointsText,player0sciencePoints,player1sciencePoints,player2sciencePoints,player3sciencePoints);
        sciencepointbox.setSpacing(20);
        sciencepointbox.setTranslateY(330);
        VBox specialpointbox = new VBox(specialcardPointsText,player0specialcards,player1specialcards,player2specialcards,player3specialcards);
        specialpointbox.setSpacing(20);
        specialpointbox.setTranslateY(330);
        VBox bluepointbox = new VBox(bluePoints,player0bluePoints,player1bluePoints,player2bluePoints,player3bluePoints);
        bluepointbox.setSpacing(20);
        bluepointbox.setTranslateY(330);
        VBox totalpointbox = new VBox(totalPointsText,player0totalPoints,player1totalPoints,player2totalPoints,player3totalPoints);
        totalpointbox.setSpacing(20);
        totalpointbox.setTranslateY(330);

        HBox boxbox = new HBox(namesbox,coinsbox,battlepointbox,sciencepointbox,specialpointbox,bluepointbox,totalpointbox);
        boxbox.setSpacing(50);
        boxbox.setTranslateX(120);




        sp.getChildren().addAll(bg3,bg4,boxbox,menu6);

    }

    /**
     * Method that calculates science points coming from green cards
     * @param a variable for mechanic
     * @param b variable for literature
     * @param c variable for geometry
     * @return m total science point
     */
    public int sciencePointCalculator(int a, int b, int c){
        int m = 0;
        
        m += a * a + b * b + c* c;
        
        int l = min( a, min(b, c));
        m += 7 * l;
        return m;


    }
    /**
     * method for recursive check resources
     * @param playerResources holds player resources
     * @param pr
     * @param costsResource
     * @param cr
     */
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
    
    /**
     * method that checks the resources
     * @param playerNum player index
     * @param isWonderbuild checks whether wonder is built or not
     * @param cost
     * @return this
     */
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
        tmpPlayerResource = new Resource[60];
        Resource tmpCostResource = new Resource( cost.resource);
        System.out.println("length" + players[playerNum].resources.length);
        int k;
        for( k = 0; k < players[playerNum].resourceCount; k++)
            tmpPlayerResource[k] = new Resource( players[playerNum].resources[k]);
        for(int i = 0; i < players[playerNum].leftTradedResources.name.length; i++,k++) {
            tmpPlayerResource[k] = new Resource( 1);
            tmpPlayerResource[k].name[0] = players[playerNum].leftTradedResources.name[i];
            tmpPlayerResource[k].quantity[0] = players[playerNum].leftTradedResources.quantity[i];
        }
        for(int i = 0; i < players[playerNum].rightTradedResources.name.length; i++,k++) {
            tmpPlayerResource[k] = new Resource( 1);
            tmpPlayerResource[k].name[0] = players[playerNum].rightTradedResources.name[i];
            tmpPlayerResource[k].quantity[0] = players[playerNum].rightTradedResources.quantity[i];
        }
        return recursiveCheck( tmpPlayerResource, new boolean[players[playerNum].resourceCount + players[playerNum].rightTradedResources.quantity.length + players[playerNum].leftTradedResources.quantity.length], tmpCostResource, 0);
    }
    
    /**
     * method that gives benefit to player
     * @param playerNum player index
     * @param isWonderbuild checks whether wonder is built or not
     * @param benefit gained benefit
     * @param buildingName name of the building
     * @param buildingColor color of the building
     * @throws Exception
     */
    public void gainBenefit( int playerNum, boolean isWonderbuild, Property benefit, String buildingName, String buildingColor) throws Exception {
        // specialCard = # : 16= Babylon A, 17 = Babylon B, 18 = olympia A, 19 = olympia B1,    20 = olympiaB3, 21 = Halikarnassos, 22 = check of olympiaA
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
        if(benefit.specialCard == 18)  players[playerNum].specialCards[22] = true;
    }
    
    /**
     * Method for defining all cards in the game
     * @throws Exception
     */
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
        a = new Property();
        a.resource = new Resource(2);
        a.resource.name[0] = "Ore"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Glass"; a.resource.quantity[1] = 1;
        b = new Property();
        b.geometry=1;
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
        cards[1][24] = new Card("school","green",a,b);


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
        a.resource.name[3] = "Lumber"; a.resource.quantity[3] = 1;
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
    /**
     * Method that distributes the wonders to players
     * @param sp
     * @param side wonderboard side A or B
     * @param name player name
     * @throws Exception
     */
    private void distributeWonders( StackPane sp, String side, String name) throws Exception {
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
        wb[0] = new WonderBoard( sp,randoms[0], side, name, 0);
        wb[1] = new WonderBoard( sp,randoms[1], side, "bot1", 1);
        wb[2] = new WonderBoard( sp,randoms[2], side, "bot2", 2);
        wb[3] = new WonderBoard( sp,randoms[3], side, "bot3", 3);
        // wonder animations
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
    
    /**
     * Class that draws wanderboards
     */
    private class WonderBoard extends Pane {
        Text coinText, shieldText, battleText, vicPointText, literatureText, mechanicText, geometryText, greenText, redText, yellowText, greyText, brownText, blueText;
        int pNum, wonderNum;
        Background bg;
        Milestone[] milestones;
        VBox coinVB, shieldVB, battleVB, vicPointVB, literatureVB, mechanicVB, geometryVB;
        HBox greenHB, redHB, yellowHB, greyHB, brownHB, blueHB, resourcesHB;
        
        /**
         * Constructor for WonderBoard class
         * @param sp 
         * @param wNumber wonder number
         * @param side wonerboard side A or B
         * @param playerName player name
         * @param pN player index
         * @throws Exception
         */
        public WonderBoard( StackPane sp, int wNumber, String side, String playerName, int pN) throws Exception {
            Property a, b;
            String wName;
            wonderNum = wNumber;
            setMaxSize(400, 250);
            milestones = new Milestone[3];
            pNum = pN;
            bg = new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.6), CornerRadii.EMPTY, Insets.EMPTY));
            Rectangle board = new Rectangle(400, 250);
            board.setArcHeight(15);
            board.setArcWidth(15);
            getChildren().add(board);
            Text sideText = new Text(playerName);
            sideText.setFill(Color.WHITESMOKE);
            sideText.setFont(Font.font("Kalam", FontWeight.BOLD, 15));
            sideText.setTranslateX(10);
            sideText.setTranslateY(20);
            getChildren().add(sideText);


            // wNumber is 1=Rhodos, 2=Alexandria, 3=Ephesos, 4=Babylon, 5=Olympia, 6=Halikarnassos, 7=Gizah
            // specialCard = # : 16= Babylon A, 17 = Babylon B, 18 = olympia A, 19 = olympia B1,    20 = olympiaB3, 21 = Halikarnassos
            if (wNumber == 1) {
                InputStream is = Files.newInputStream(Paths.get("images/setname.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "rhodos";
                Text sideText2 = new Text("Rhodos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD, 15));
                sideText2.setTranslateX(300);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if (side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";
                    a.resource.quantity[0] = 3;
                    b.shield = 2;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";
                    a.resource.quantity[0] = 4;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                } else {
                    milestones = new Milestone[2];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 3;
                    b.victoryPoint = 3;
                    b.coin = 3;
                    b.shield = 1;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";
                    a.resource.quantity[0] = 4;
                    b.shield = 1;
                    b.victoryPoint = 4;
                    b.coin = 4;
                    milestones[1] = new Milestone(a, b);
                }
            } else if (wNumber == 2) {
                InputStream is = Files.newInputStream(Paths.get("images/alexandria.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "alexandria";
                Text sideText2 = new Text("Alexandria - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD, 15));
                sideText2.setTranslateX(280);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if (side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";
                    a.resource.quantity[0] = 2;
                    b.resource = new Resource(4);
                    b.resource.name[0] = "Lumber";
                    b.resource.quantity[0] = 1;
                    b.resource.name[1] = "Ore";
                    b.resource.quantity[1] = 1;
                    b.resource.name[2] = "Stone";
                    b.resource.quantity[2] = 1;
                    b.resource.name[3] = "Clay";
                    b.resource.quantity[3] = 1;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Glass";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                } else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";
                    a.resource.quantity[0] = 2;
                    b.resource = new Resource(4);
                    b.resource.name[0] = "Lumber";
                    b.resource.quantity[0] = 1;
                    b.resource.name[1] = "Ore";
                    b.resource.quantity[1] = 1;
                    b.resource.name[2] = "Stone";
                    b.resource.quantity[2] = 1;
                    b.resource.name[3] = "Clay";
                    b.resource.quantity[3] = 1;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 2;
                    b.resource = new Resource(3);
                    b.resource.name[0] = "Glass";
                    b.resource.quantity[0] = 1;
                    b.resource.name[1] = "Textile";
                    b.resource.quantity[1] = 1;
                    b.resource.name[2] = "Paper";
                    b.resource.quantity[2] = 1;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 3;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                }
            } else if (wNumber == 3) {
                InputStream is = Files.newInputStream(Paths.get("images/gamepage.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "ephesos";
                Text sideText2 = new Text("Ephesos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD, 15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if (side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 2;
                    b.resource = new Resource(4);
                    b.coin = 9;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Paper";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                } else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 2;
                    b.coin = 4;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    b.coin = 4;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(3);
                    a.resource.name[0] = "Paper";
                    a.resource.quantity[0] = 1;
                    a.resource.name[1] = "Glass";
                    a.resource.quantity[1] = 1;
                    a.resource.name[2] = "Textile";
                    a.resource.quantity[2] = 1;
                    b.victoryPoint = 5;
                    b.coin = 4;
                    milestones[2] = new Milestone(a, b);
                }
            } else if (wNumber == 4) {
                InputStream is = Files.newInputStream(Paths.get("images/babylon.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "babylon";
                Text sideText2 = new Text("Babylon - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD, 15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if (side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 3;
                    b.specialCard = 16;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";
                    a.resource.quantity[0] = 4;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                } else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Textile";
                    a.resource.quantity[0] = 1;
                    a.resource.name[1] = "Clay";
                    a.resource.quantity[1] = 1;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 2;
                    a.resource.name[1] = "Glass";
                    a.resource.quantity[1] = 1;
                    b.specialCard = 17;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Clay";
                    a.resource.quantity[0] = 3;
                    a.resource.name[1] = "Paper";
                    a.resource.quantity[1] = 1;
                    b.specialCard = 16;
                    milestones[2] = new Milestone(a, b);
                }
            } else if (wNumber == 5) {
                InputStream is = Files.newInputStream(Paths.get("images/olympia.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "olympia";
                Text sideText2 = new Text("Olympia - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD, 15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if (side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 2;
                    b.specialCard = 18;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                } else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 2;
                    b.specialCard = 19;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 5;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Ore";
                    a.resource.quantity[0] = 2;
                    a.resource.name[1] = "Textile";
                    a.resource.quantity[1] = 1;
                    b.specialCard = 20;
                    milestones[2] = new Milestone(a, b);
                }
            } else if (wNumber == 6) {
                InputStream is = Files.newInputStream(Paths.get("images/halikarnassos.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "halikarnassus";
                Text sideText2 = new Text("Halikarnassos - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD, 15));
                sideText2.setTranslateX(250);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if (side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";
                    a.resource.quantity[0] = 3;
                    b.specialCard = 21;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Textile";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                } else {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Ore";
                    a.resource.quantity[0] = 2;
                    b.specialCard = 21;
                    b.victoryPoint = 2;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";
                    a.resource.quantity[0] = 3;
                    b.specialCard = 21;
                    b.victoryPoint = 1;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(3);
                    a.resource.name[0] = "Glass";
                    a.resource.quantity[0] = 1;
                    a.resource.name[1] = "Textile";
                    a.resource.quantity[1] = 1;
                    a.resource.name[2] = "Paper";
                    a.resource.quantity[2] = 1;
                    b.specialCard = 21;
                    milestones[2] = new Milestone(a, b);
                }
            } else {
                InputStream is = Files.newInputStream(Paths.get("images/gizeh.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));
                wName = "gizah";
                Text sideText2 = new Text("Gizeh - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD, 15));
                sideText2.setTranslateX(300);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                if (side.equals("A")) {
                    milestones = new Milestone[3];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 3;
                    b.victoryPoint = 5;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 4;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                } else {
                    milestones = new Milestone[4];
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Lumber";
                    a.resource.quantity[0] = 2;
                    b.victoryPoint = 3;
                    milestones[0] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 3;
                    b.victoryPoint = 5;
                    milestones[1] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(1);
                    a.resource.name[0] = "Clay";
                    a.resource.quantity[0] = 3;
                    b.victoryPoint = 5;
                    milestones[2] = new Milestone(a, b);
                    a = new Property();
                    b = new Property();
                    a.resource = new Resource(2);
                    a.resource.name[0] = "Stone";
                    a.resource.quantity[0] = 4;
                    a.resource.name[1] = "Paper";
                    a.resource.quantity[1] = 1;
                    b.victoryPoint = 7;
                    milestones[2] = new Milestone(a, b);
                }
            }
            // milestones part
            HBox milestoneHB = new HBox(25);
            milestoneHB.setBackground(bg);
            for (int i = 1; i <= milestones.length; i++) {
                InputStream is = Files.newInputStream(Paths.get("images/Wonderpng/" + wName + side + i + ".png"));
                Image img = new Image(is);
                is.close();
                ImageView imgView = new ImageView(img);
                imgView.setFitHeight(40);
                if (milestones.length == 4)
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
            coinText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
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
            shieldText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
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
            battleText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
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
            vicPointText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
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
            literatureText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
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
            mechanicText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
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
            geometryText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
            geometryText.setTranslateX(5);
            geometryVB = new VBox(imgView, geometryText);
            geometryVB.setBackground(bg);
            geometryVB.setTranslateY(30);
            geometryVB.setTranslateX(245);

            getChildren().addAll(shieldVB, coinVB, battleVB, vicPointVB, literatureVB, mechanicVB, geometryVB);

            // Used Cards parts

            // Green Cards
            Rectangle card = new Rectangle(15, 20, Color.GREEN);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            greenText = new Text(players[pNum].greenCards + "");
            greenText.setFill(Color.WHITESMOKE);
            greenText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
            greenText.setTranslateX(10);
            greenHB = new HBox(card, greenText);
            greenHB.setBackground(bg);
            greenHB.setTranslateY(30);
            greenHB.setTranslateX(355);
            greenHB.setPrefSize(40, 20);

            // red Cards
            card = new Rectangle(15, 20, Color.RED);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            redText = new Text(players[pNum].redCards + "");
            redText.setFill(Color.WHITESMOKE);
            redText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
            redText.setTranslateX(10);
            redHB = new HBox(card, redText);
            redHB.setBackground(bg);
            redHB.setTranslateY(60);
            redHB.setTranslateX(355);
            redHB.setPrefSize(40, 20);

            // yellow Cards
            card = new Rectangle(15, 20, Color.YELLOW);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            yellowText = new Text(players[pNum].yellowCards + "");
            yellowText.setFill(Color.WHITESMOKE);
            yellowText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
            yellowText.setTranslateX(10);
            yellowHB = new HBox(card, yellowText);
            yellowHB.setBackground(bg);
            yellowHB.setTranslateY(90);
            yellowHB.setTranslateX(355);
            yellowHB.setPrefSize(40, 20);

            // grey Cards
            card = new Rectangle(15, 20, Color.GREY);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            greyText = new Text(players[pNum].greyCards + "");
            greyText.setFill(Color.WHITESMOKE);
            greyText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
            greyText.setTranslateX(10);
            greyHB = new HBox(card, greyText);
            greyHB.setBackground(bg);
            greyHB.setTranslateY(120);
            greyHB.setTranslateX(355);
            greyHB.setPrefSize(40, 20);

            // brown Cards
            card = new Rectangle(15, 20, Color.BROWN);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            brownText = new Text(players[pNum].brownCards + "");
            brownText.setFill(Color.WHITESMOKE);
            brownText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
            brownText.setTranslateX(10);
            brownHB = new HBox(card, brownText);
            brownHB.setBackground(bg);
            brownHB.setTranslateY(150);
            brownHB.setTranslateX(355);
            brownHB.setPrefSize(40, 20);

            // blue Cards
            card = new Rectangle(15, 20, Color.BLUE);
            card.setArcWidth(5);
            card.setArcHeight(5);
            card.setTranslateX(5);
            card.setTranslateY(5);
            blueText = new Text(players[pNum].blueCards + "");
            blueText.setFill(Color.WHITESMOKE);
            blueText.setFont(Font.font("Kalam", FontPosture.ITALIC, 20));
            blueText.setTranslateX(10);
            blueHB = new HBox(card, blueText);
            blueHB.setBackground(bg);
            blueHB.setTranslateY(180);
            blueHB.setTranslateX(355);
            blueHB.setPrefSize(40, 20);

            getChildren().addAll(greenHB, redHB, yellowHB, greyHB, brownHB, blueHB, milestoneHB);

            // Resource Part
            resourcesHB = new HBox(5);
            for (int i = 0; i < players[pNum].resourceCount; i++) {
                for (int k = 0; k < players[pNum].resources[i].quantity[0]; k++) {
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
            for(int i = 0; i < players[pNum].rightTradedResources.quantity.length; i++) {
                VBox vb = new VBox();
                vb.setBackground(bg);
                is = Files.newInputStream(Paths.get("images/" + players[pNum].rightTradedResources.name[i] + ".png"));
                img = new Image(is);
                is.close();
                imgView = new ImageView(img);
                imgView.setFitHeight(25);
                imgView.setFitWidth(25);
                vb.getChildren().addAll(imgView);
                resourcesHB.getChildren().addAll(vb);
            }
            for(int i = 0; i < players[pNum].leftTradedResources.quantity.length; i++) {
                VBox vb = new VBox();
                vb.setBackground(bg);
                is = Files.newInputStream(Paths.get("images/" + players[pNum].leftTradedResources.name[i] + ".png"));
                img = new Image(is);
                is.close();
                imgView = new ImageView(img);
                imgView.setFitHeight(25);
                imgView.setFitWidth(25);
                vb.getChildren().addAll(imgView);
                resourcesHB.getChildren().addAll(vb);
            }
            resourcesHB.setTranslateY(100);
            resourcesHB.setTranslateX(5);

            // special cards part
            HBox specialHB = new HBox(5);
            for (int i = 1; i < 16; i++) {
                if (players[pNum].specialCards[i] && i != 4 && i != 5) {
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
            getChildren().addAll(resourcesHB, specialHB);
            // Putting green ticks for milestones that are completed
            InputStream is5 = Files.newInputStream(Paths.get("images/tick.png"));
            Image img5 = new Image(is5);
            is5.close();

            HBox milestoneTick = new HBox();
            milestoneTick.setTranslateX(40);
            milestoneTick.setTranslateY(210);
            if(milestones.length < 4) {
                milestoneTick.setSpacing(100);
            }
            if(milestones.length == 4) {
                milestoneTick.setSpacing(63);
                milestoneTick.setTranslateX(32);
            }
            for (int i = 0; i < players[pNum].milestoneDone; i++) {
                ImageView imgTick = new ImageView(img5);
                imgTick.setFitHeight(37);
                imgTick.setFitWidth(37);
                milestoneTick.getChildren().add(imgTick);
            }
            getChildren().add(milestoneTick);
        }
    }
    /**
     * Milestone class that contains milestones
     */
    public class Milestone extends Pane {
        Property cost, benefit;
        
        /**
         * Constructor for Milestone class
         * @param cost milestone cost
         * @param benefit milestone benefit
         * @throws Exception
         */
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
        Button sellButton,buryButton,buildButton,olympiaButton;
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
        
        /**
         * method for mouse action 
         */
        public void  mouseEnteredHere(){
            if (this.isUsed)
                return;
            board.setOpacity(0.65);
            sellButton = new Button("SELL");
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

            buryButton = new Button("UPGRADE WONDER");
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

            if( players[0].specialCards[18]) {
                olympiaButton = new Button("BUILD FREE");
                olympiaButton.setTranslateX(20);
                olympiaButton.setTranslateY(150);
                getChildren().add(olympiaButton);
                olympiaButton.setOnMouseClicked(event -> {
                    try {
                        gainBenefit( 0, false, this.benefit, this.name, this.color);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        deleteCard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        endTurn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    players[0].specialCards[18] = false;
                });
            }

        /*
        sell = new Text("Sell");
        sell.setFill(Color.WHITESMOKE);
        sell.setFont(Font.font("Kalam", FontWeight.BOLD,15));
        sell.setTranslateX(110);
        sell.setTranslateY(150);
        getChildren().add(sell);

         */

        }
          
        /**
         * method for mouse action exit 
         */
        public void  mouseExitedHere(){
            if (this.isUsed)
                return;
            board.setOpacity(1);
            getChildren().remove(sellButton);
            getChildren().remove(buryButton);
            getChildren().remove(buildButton);
            getChildren().remove(olympiaButton);
        }
        /**
         * method that deletes card
         * @throws IOException when card cannot be deleted(IOException)
         */
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
            if(currentAge > 3) return  false;
            switch (action) {
                case SELL:
                    System.out.println(playerNum + "sell" + name);
                    Property tmp = new GamePage.Property();
                    tmp.coin = 3;
                    gainBenefit( playerNum, false, tmp, "", "");
                    cardsAtStake[noOfCardsAtStake] = this;
                    noOfCardsAtStake++;
                    break;

                case BURY:
                    System.out.println(playerNum + "bury" + name);
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
                    System.out.println(playerNum + "build" + name);
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
