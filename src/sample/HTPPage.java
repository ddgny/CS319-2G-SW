package sample;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HTPPage extends Scene {
    public HTPPage(StackPane sp) throws Exception{
        super(sp, 1000, 650);
        InputStream is = Files.newInputStream(Paths.get("images/mainmenu.jpg"));
        Image img = new Image(is);
        is.close();
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(650);
        imgView.setFitWidth(1000);
        sp.getChildren().add(imgView);

    }
}
