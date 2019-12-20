package sample;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
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



        Button btn1 = new Button("1");
        Button btn2 = new Button("2");
        Button btn3 = new Button("3");
        Button btn4 = new Button("4");
        Button btn5 = new Button("5");
        Button btn6 = new Button("6");
        Button btn7 = new Button("7");
        Button btn8 = new Button("8");
        Button btn9 = new Button("9");
        Button btn10 = new Button("10");
        Button btn11 = new Button("11");
        HBox btnHbox= new HBox();
        btnHbox.getChildren().add(btn1);
        btnHbox.getChildren().add(btn2);
        btnHbox.getChildren().add(btn3);
        btnHbox.getChildren().add(btn4);
        btnHbox.getChildren().add(btn5);
        btnHbox.getChildren().add(btn6);
        btnHbox.getChildren().add(btn7);
        btnHbox.getChildren().add(btn8);
        btnHbox.getChildren().add(btn9);
        btnHbox.getChildren().add(btn10);
        btnHbox.getChildren().add(btn11);
        btnHbox.setTranslateX(720);
        btnHbox.setTranslateY(620);
        Rectangle htpRec= new Rectangle(500,400);
        InputStream htpImage = Files.newInputStream(Paths.get("images/htp1.jpg"));
        Image img1 = new Image(htpImage);
        htpRec.setFill(new ImagePattern(img1));

        btn1.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp1.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btn2.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp2.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btn3.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp3.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btn4.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp4.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btn5.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp5.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btn6.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp6.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        btn7.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp7.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        btn8.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp8.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        btn9.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp9.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        btn10.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp10.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        btn11.setOnMouseClicked(event -> {
            try {
                htpRec.setFill(new ImagePattern(new Image(Files.newInputStream(Paths.get("images/htp11.jpg")))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        VBox Vtext =new VBox();
        VBox htpVBox= new VBox();
//        Text textArea = new Text();
        Text textHeader = new Text();
//        textArea.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Duis at consectetur lorem donec massa sapien. Lacus vel facilisis volutpat est velit egestas dui id. Vel risus commodo viverra maecenas. Consequat interdum varius sit amet mattis vulputate enim. Mauris rhoncus aenean vel elit. Eu lobortis elementum nibh tellus molestie. Sem fringilla ut morbi tincidunt augue interdum velit euismod in. Leo vel orci porta non. Et molestie ac feugiat sed lectus vestibulum mattis. Turpis massa sed elementum tempus. Platea dictumst quisque sagittis purus sit amet volutpat. Etiam dignissim diam quis enim lobortis scelerisque fermentum dui faucibus. Leo duis ut diam quam nulla porttitor massa id neque. Phasellus faucibus scelerisque eleifend donec pretium vulputate.\n");
//        textArea.setWrappingWidth(450);
//        textArea.maxHeight(500);
//        textArea.setFill(Color.WHITESMOKE);
//        textArea.setFont(Font.font("Verdana", FontWeight.BOLD,15));
        textHeader.setText("Seven Wonders");
        textHeader.setFill(Color.WHITESMOKE);
        textHeader.setFont(Font.font("Verdana", FontWeight.BOLD,25));

        Vtext.getChildren().add(textHeader);
        //       Vtext.getChildren().add(htpImage);
        Vtext.setMargin(textHeader,new Insets(25,60,10,120));

//        ScrollBar s = new ScrollBar();
//        s.setOrientation(Orientation.VERTICAL);
//        s.setMax(100);


        htpVBox.getChildren().add(textHeader);
        htpVBox.getChildren().add(htpRec);
        htpVBox.setAlignment(Pos.CENTER);
        htpVBox.setMaxHeight(0);
        htpVBox.setPrefHeight(0);
        htpVBox.setTranslateY(-36);
        // htpVBox.setFillHeight(false);
        Rectangle bg = new Rectangle(500,500);
        bg.setOpacity(0.6);
        bg.setFill(Color.BLACK);
        bg.setEffect( new GaussianBlur(3.5));
        ScrollPane scpane = new ScrollPane();
        sp.getChildren().addAll(bg,htpVBox,btnHbox);
        OptionsPage.BackButton bb = new OptionsPage.BackButton();
        bb.setOnMouseClicked( event -> {
            window.setScene( bp);
        });
        bb.setTranslateX(-350);
        bb.setTranslateY(-230);
        sp.getChildren().add(bb);
    }
}
