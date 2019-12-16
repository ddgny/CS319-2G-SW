package sample;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
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
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.shape.*;

import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.awt.*;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.min;

public class GamePage extends Scene {
    public static WonderBoard[] wb;
    public static Player[] players;
    private Card[][] cards;
    private static Stage window;
    // mode = ally -> -1 , normal -> 0 , story -> 1,2,3,4,5...
    private int mode;
    private class Resource {
        String[] name;
        int[] quantity;
        public Resource( int optional) {
                name = new String[optional];
                quantity = new int[optional];
        }
    }
    public static class Property {
        int coin, shield, mechanic, literature, geometry, victoryPoint;
        String requiredBuilding;
        Resource resource;
        int specialCard;
        public Property(){
            coin = shield = mechanic = literature = geometry = victoryPoint = specialCard = 0;
            requiredBuilding = "";
        }
    }
    private class Player {
        String name;
        int battlePoint, greenCards, redCards, yellowCards, greyCards, purpleCards, brownCards, blueCards, milestoneDone;
        String[] buildings;
        Resource[] resources;
        int resourceCount;
        boolean specialCards[];
        Property stats;
        public Player(String tmp) {
            stats = new Property();
            stats.coin = 3;
            name = tmp;
            battlePoint = greenCards = redCards = yellowCards = greyCards = purpleCards = brownCards = blueCards = milestoneDone = resourceCount = 0;
            buildings = new String[22];
            resources = new Resource[22];
            specialCards = new boolean[20];
            for(int i = 0; i < 20; i++) specialCards[i] = false;
        }
        void addResource( Resource add) {
            resources[resourceCount] = add;
            resourceCount++;
        }
    }


    public GamePage(StackPane sp, Scene mainmenu, Stage window, String name, ToggleGroup side, int sMode) throws Exception {
        super(sp, Main.primaryScreenBounds.getWidth(), Main.primaryScreenBounds.getHeight());
        mode = sMode;
        this.window = window;
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
        OptionsPage.BackButton bb = new OptionsPage.BackButton();
        bb.setOnMouseClicked( event -> {
            window.setScene( mainmenu);
        });
        bb.setTranslateX(-450);
        bb.setTranslateY(-330);
        sp.getChildren().add(bb);

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
            btnResume.setOnMouseClicked( event2 -> {
                        btnResume.setVisible(false);
                        btnExit2.setVisible(false);
                        menu2.getChildren().removeAll(btnResume, btnExit2);
                        sp.getChildren().remove(menu2);
                        sp.getChildren().remove(r);
            });
            menu2.getChildren().addAll(btnResume, btnExit2);
            sp.getChildren().add(menu2);


            //window.initStyle(StageStyle.TRANSPARENT);
            //mainmenu.setFill(Color.TRANSPARENT);
            //Color.rgb(0,0,0 ,0.5)
            //window.setOpacity(0.2);
            //mainmenu.setFill(Color.rgb(0,0,0 ,0.5));
        });
        pb.setTranslateX(530);
        pb.setTranslateY(270);
        sp.getChildren().add(pb);

        players[0] = new Player(name);
        players[1] = new Player("bot1");
        players[2] = new Player("bot2");
        players[3] = new Player("bot3");
        definingCards();
//        Collections.shuffle(Arrays.asList(cards[0]));
//        Collections.shuffle(Arrays.asList(cards[1]));
//        Collections.shuffle(Arrays.asList(cards[2]));
        sp.getChildren().addAll(cards[0][0],cards[0][1],cards[0][2],cards[0][3],cards[0][4],cards[0][5],cards[0][6]);

        distributeWonders( sp, side, name);
        sp.getChildren().addAll(wb);
    }
    public static void giveError( String errorMessage) {
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
    public static void endTurn() {

    }
    public static boolean checkResources(int playerNum, boolean isWonderbuild, Property cost) {
        if(isWonderbuild)
            cost = wb[playerNum].milestones[players[playerNum].milestoneDone].cost;
        return false;
    }
    public static void gainBenefit( int playerNum, boolean isWonderbuild, Property benefit) {
        if(isWonderbuild) {
            benefit = wb[playerNum].milestones[players[playerNum].milestoneDone].benefit;
            players[playerNum].milestoneDone++;
        }
    }
    private void definingCards() throws Exception {
        Property a = new Property();
        Property b = new Property();
        // bazı kartların benefiti özellik veriyor. bu özellikleri b.specialCard = # diyerek yapın
        // # -> east Trading post = 1,  west trading post = 2,  marketplace = 3,    vineyard = 4,   bazar = 5,  haven = 6,
        // lighthouse =7,  chamber of commerce = 8,    arena = 9,  workers guild = 10, craftsmens guild = 11,  traders guild = 12, philosophers guild = 13,
        // spies guild = 14,   magistrates guild = 15
        // arsenal örneği
        a.resource = new Resource(3);
        a.resource.name[0] = "Lumber"; a.resource.quantity[0] = 2;
        a.resource.name[1] = "Ore"; a.resource.quantity[1] = 1;
        a.resource.name[2] = "Textile"; a.resource.quantity[2] = 1;
        b.shield = 3;
        cards[0][0] = new Card("arsenal","red",a,b);

        // caravansery örneği
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
        cards[0][1] = new Card("caravansery","yellow",a,b);
        cards[0][2] = new Card("Arsenal","red",a,b);
        cards[0][3] = new Card("Arsenal","red",a,b);
        cards[0][4] = new Card("Arsenal","red",a,b);
        cards[0][5] = new Card("Arsenal","red",a,b);
        cards[0][6] = new Card("Arsenal","red",a,b);
        cards[0][0].setTranslateX(-250); cards[0][0].setTranslateY(90);
        cards[0][1].setTranslateX(-50);  cards[0][1].setTranslateY(90);
        cards[0][2].setTranslateX(-450); cards[0][2].setTranslateY(90);
        cards[0][3].setTranslateX(-650); cards[0][3].setTranslateY(90);
        cards[0][4].setTranslateX(-150); cards[0][4].setTranslateY(290);
        cards[0][5].setTranslateX(-350); cards[0][5].setTranslateY(290);
        cards[0][6].setTranslateX(-550); cards[0][6].setTranslateY(290);
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
        int pNum;
        Background bg;
        Milestone[] milestones;
        VBox coinVB, shieldVB, battleVB, vicPointVB, literatureVB, mechanicVB, geometryVB;
        HBox greenHB, redHB, yellowHB, greyHB, brownHB, blueHB, resourcesHB;
        public WonderBoard( StackPane sp, int wNumber, String side, String playerName, int pN) throws Exception{
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
                Resource tmp = new Resource( 1);
                tmp.name[0] = "Ore"; tmp.quantity[0] = 1;
                players[pNum].addResource( tmp);
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
                Resource tmp = new Resource( 1);
                tmp.name[0] = "Glass"; tmp.quantity[0] = 1;
                players[pNum].addResource( tmp);
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
                Resource tmp = new Resource( 1);
                tmp.name[0] = "Paper"; tmp.quantity[0] = 1;
                players[pNum].addResource( tmp);
            }
            else if( wNumber == 4) {
                InputStream is = Files.newInputStream(Paths.get("images/babylon.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));

                Text sideText2 = new Text("Babylon - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                Resource tmp = new Resource( 1);
                tmp.name[0] = "Clay"; tmp.quantity[0] = 1;
                players[pNum].addResource( tmp);
            }
            else if( wNumber == 5) {
                InputStream is = Files.newInputStream(Paths.get("images/olympia.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));

                Text sideText2 = new Text("Olympia - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(290);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                Resource tmp = new Resource( 1);
                tmp.name[0] = "Lumber"; tmp.quantity[0] = 1;
                players[pNum].addResource( tmp);
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
                Resource tmp = new Resource( 1);
                tmp.name[0] = "Textile"; tmp.quantity[0] = 1;
                players[pNum].addResource( tmp);
            }
            else if( wNumber == 7) {
                InputStream is = Files.newInputStream(Paths.get("images/gizeh.jpg"));
                Image img = new Image(is);
                is.close();
                board.setFill(new ImagePattern(img));

                Text sideText2 = new Text("Gizeh - (" + side + ")");
                sideText2.setFill(Color.WHITESMOKE);
                sideText2.setFont(Font.font("Kalam", FontWeight.BOLD,15));
                sideText2.setTranslateX(300);
                sideText2.setTranslateY(20);
                getChildren().addAll(sideText2);
                Resource tmp = new Resource( 1);
                tmp.name[0] = "Stone"; tmp.quantity[0] = 1;
                players[pNum].addResource( tmp);
            }
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

            getChildren().addAll(greenHB, redHB, yellowHB, greyHB, brownHB, blueHB);

//            Resource resource = new Resource(4);
//            resource.name[0] = "Ore"; resource.name[1] = "Lumber"; resource.name[2] = "Glass"; resource.name[3] = "Textile";
//            resource.quantity[0] = 1; resource.quantity[1] = 1; resource.quantity[2] = 1; resource.quantity[3] = 1;
//            players[pNum].addResource(resource);
            // Resource Part
            resourcesHB = new HBox( 5);
            for(int i = 0; i < players[pNum].resourceCount; i++) {
                VBox vb = new VBox();
                vb.setBackground(bg);
                for(int j = 0; j < players[pNum].resources[i].quantity.length; j++){
                    is = Files.newInputStream(Paths.get("images/" + players[pNum].resources[i].name[j] + ".png"));
                    img = new Image(is);
                    is.close();
                    imgView = new ImageView(img);
                    imgView.setFitHeight(min(25,50 / players[pNum].resources[i].quantity.length));
                    imgView.setFitWidth(25);
                    vb.getChildren().addAll(imgView);
                }
                resourcesHB.getChildren().addAll(vb);
            }
            resourcesHB.setTranslateY(100);
            resourcesHB.setTranslateX(5);
            getChildren().addAll(resourcesHB);
        }
        public void makeChanges(){
            getChildren().remove(coinText);
            coinText = new Text(players[pNum].stats.coin + "");
            coinText.setFill(Color.WHITESMOKE);
            coinText.setFont(Font.font("Kalam", FontPosture.ITALIC,15));
            getChildren().addAll(coinText);
        }
    }
    public class Milestone extends Pane {
        Property cost, benefit;
        public Milestone( Property cost, Property benefit ) throws Exception {
            this.cost = cost;
            this.benefit = benefit;
        }
    }
    public class Card extends Pane {
        String name, color;
        javafx.scene.control.Button sellButton,buryButton,buildButton;
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
            sellButton = new javafx.scene.control.Button("SELL");
            sellButton.setTranslateX(55);
            sellButton.setTranslateY(30);
            getChildren().add(sellButton);
            sellButton.setOnMouseClicked(event -> {
                GamePage.Property tmp;
                tmp = new GamePage.Property();
                tmp.coin = 3;
                GamePage.gainBenefit( 0, false, tmp);
            });

            buryButton = new javafx.scene.control.Button("UPGRADE WONDER");
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
}
