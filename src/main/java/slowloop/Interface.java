package slowloop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class Interface extends Application{

    public static Controller control;
    public HashMap<String, String> news;
    public String country, flagURL, boxerNation;
    public ImageView flag;


    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/intro.fxml"));
        Parent root = loader.load();
        //Create instance of Controller class in order to access its methods.
        control = loader.getController();

        Scene scene = new Scene(root, 300, 443);
        scene.setCursor(Cursor.DEFAULT);
        primaryStage.setTitle("Boxer Search");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()  {
            public void handle(WindowEvent t) {
                System.exit(0);
            }
        });
        primaryStage.show();
    }

    //Grab the boxer name that was entered in the opening/beginning Textfield.
    public String setText(){ return control.boxer(); }

    public void setAlignment(){
        control.pane1.setAlignment(Pos.TOP_CENTER);
    }

    //Set Text object with the boxer's name.
    public void boxer (Text text, Backend backend) throws IOException{

        text.setText(backend.boxer);
        text.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
    }

    //Grab an image url of the boxer's country's flag (from countries-ofthe-world.com or Wikipedia) and set the ImageView object using the grabbed image URL.
    public void nation(String nation, ImageView image, Backend name) throws IOException{

        country = name.nationality.replace(" ", "-").replaceAll("\\[[0-9]+\\]", "");
        flag = image;
        boxerNation = nation;

        HashMap<String, String> otherCountries = new HashMap<String, String>();

        otherCountries.put("Russian", "Russia");
        otherCountries.put("Kazakh", "Kazakhstan");
        otherCountries.put("Ukrainian", "Ukraine");
        otherCountries.put("Armenian", "Armenia");
        otherCountries.put("Kirghiz", "Kyrgyzstan");
        otherCountries.put("Uzbek", "Uzbekistan");
        otherCountries.put("Azerbaijan", "Azerbaijan");
        otherCountries.put("Moldavian", "Moldova");
        otherCountries.put("Estonian", "Estonia");
        otherCountries.put("Latvian", "Latvia");
        otherCountries.put("Lithuanian", "Lithuania");
        otherCountries.put("Tajik", "Tajikistan");
        otherCountries.put("Turkmen", "Turkmenistan");
        otherCountries.put("Turkestan", "Turkmenistan");
        otherCountries.put("Georgian", "Georgia");
        otherCountries.put("USA", "United-States-of-America");
        otherCountries.put("U.S.", "United-States-of-America");
        otherCountries.put("England", "United-Kingdom");
        otherCountries.put("Wales", "United-Kingdom");
        otherCountries.put("Northern-Ireland", "Ireland");
        otherCountries.put("Scotland", "United-Kingdom");
        otherCountries.put("Côte-D’Ivoire", "Cote-d-Ivoire");

        Document document = Jsoup.connect("https://www.countries-ofthe-world.com/flags-of-the-world.html").get();
        Elements countries = document.select("tr");

        if(!(countries.text().contains(country.replace("-"," "))) && otherCountries.containsKey(country) == false){

            Document document1 = Jsoup.connect("https://en.wikipedia.org/wiki/Gallery_of_flags_of_dependent_territories").get();
            Elements territories = document1.select("[src]");

            boolean found = false;

            for(Element territory : territories){
                String territoryName = territory.attr("alt").replace("Flag of ", "");

                if(territoryName.length() > 0){
                    if(name.nationality.contains(territoryName.substring(0,1).replace(" ", "")+ territoryName.substring(1))){
                        flagURL = territory.attr("abs:src").toString();
                        flag.setImage(new Image(flagURL));
                        found = true;
                    }
                }
            }
            //Boxer's flag is left blank if an image url can't be found.
            if(found == false){
                flag.setImage(new Image("http://"));
            }

        }else{
            if(otherCountries.containsKey(country)){
                flagURL = "https://www.countries-ofthe-world.com/flags-normal/flag-of-" + otherCountries.get(country) + ".png";
                flag.setImage(new Image(flagURL));
            }
            else{
                flagURL = "https://www.countries-ofthe-world.com/flags-normal/flag-of-" + country + ".png";
                flag.setImage(new Image(flagURL));
            }
        }

        //Set the Tooltip to show the correct name of the country.
        if(otherCountries.containsKey(country)){
            Tooltip t = new Tooltip(otherCountries.get(country).replace("-", " "));
            Tooltip.install(flag, t);
        }
        else{
            Tooltip t = new Tooltip(boxerNation);
            Tooltip.install(flag, t);
        }
    }

    //Sets the text for the wins, losses, and draws Text objects.
    public void record(Text wins, Text losses, Text draws, Backend back){

        try{
            wins.setText(back.wins);
            losses.setText(back.losses);
            draws.setText(back.draws);
        }
        catch(Exception e){
        }
    }

    //Set the opponent name. The name of the boxer's next opponent is set as a hyperlink (links
    //to the opponent's profile).
    public void opponentStats (String boxer, String fight, Hyperlink link, Text opposition) throws Exception{

        final Hyperlink hyperlink = link;
        final String opponent = boxer;
        final String fighting = fight;

        if (fighting.contains("Date")) {

            Platform.runLater(new Runnable() {
                public void run() {
                    hyperlink.setText(opponent);
                }
            });
            hyperlink.setVisible(true);
            hyperlink.setVisited(false);
            opposition.setText("Opponent:");
        }
        if (fighting.contains("scheduled")) {

            hyperlink.setVisible(false);
            opposition.setText("");

        } else if (opponent.contains("TBA") || opponent.contains("tba")) {

            Platform.runLater(new Runnable() {
                public void run() {
                    hyperlink.setText("TBA");
                }
            });
            hyperlink.setVisible(true);
            opposition.setText("Opponent:");

        } else if (opponent.contains("n/a") || opponent.contains("N/A")) {

            Platform.runLater(new Runnable() {
                public void run() {
                    hyperlink.setText("n/a");
                }
            });
            hyperlink.setVisible(true);
            opposition.setText("Opponent:");
        }
    }

    //Set the date for the boxer's next fight. If no fight is scheduled then the Text object is set to
    //"No scheduled fight."
    public void fight(Text fightText, Backend backend) throws IOException{

        fightText.setText(backend.fight);
    }

    //Set the text for the biography Text object.
    public void biography(Text bio, Backend backend) throws IOException{

        bio.setText(backend.bio);
    }

    //Fill a TextFlow object with the title of each article that was grabbed using the findArticles method in the Backend class.
    //Each article title is linked to their corresponding urls.
    public void news (TextFlow text, HashMap map) throws IOException {

        final TextFlow record = text;
        final HashMap<String,String> boxerNews = map;

        Platform.runLater(new Runnable() {
            public void run() {
                record.getChildren().clear();
                news = boxerNews;

                if (news.isEmpty()) {
                    record.getChildren().add(new Text("No articles found."));
                }
                else {
                    for (String article : news.keySet()) {

                        Hyperlink link = new Hyperlink(article);
                        link.setMaxWidth(270);
                        link.setTooltip(new Tooltip(article));

                        final String newsURL = news.get(article);

                        link.setOnAction(new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent event) {
                                getHostServices().showDocument(newsURL);
                            }
                        });
                        record.getChildren().add(link);
                    }
                }
            }
        });
    }

    public void alertBox() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText("Cannot find boxer.");
        alert.showAndWait();

    }
}
