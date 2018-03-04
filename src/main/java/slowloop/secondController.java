package slowloop;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class secondController {

    public TextField boxerName;
    public Button button;
    public TextFlow nextNews;
    public Text wins, losses, draws, nextFight, text, opponent, biography;
    public Hyperlink enemy;
    public GridPane pane;
    public ImageView flag;
    public String currentBoxer, currentOpponent;
    public ProgressIndicator progress;
    public Boolean connection;
    public HBox bioText;

    public void handle(KeyEvent ke) throws IOException{

        final KeyEvent event = ke;

        Task task = new Task() {
            @Override
            protected Void call() throws Exception {
                if (event.getCode() == KeyCode.ENTER) {
                    progress.setVisible(true);
                    connection = true;
                    Backend backend = new Backend();

                    String boxer = boxerName.getText();

                    try{
                        backend.connect(boxer);

                        if(backend.nationality.equals("") && backend.wins.equals("n/a") && backend.losses.equals("n/a") && backend.draws.equals("n/a")
                                && backend.opponent.equals("n/a")){

                            connection = false;
                        }

                    }catch(Exception e){
                        connection = false;
                    }

                    if(connection == false){
                        Platform.runLater(new Runnable() {
                            public void run() {

                                progress.setVisible(false);
                                new Interface().alertBox();
                            }
                        });
                    }
                    else{
                        loadInfo(boxer);
                        progress.setVisible(false);
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void buttonClicked(ActionEvent ae) throws IOException{

        Task task = new Task() {
            @Override
            protected Void call() throws Exception {
                progress.setVisible(true);
                connection = true;
                Backend backend = new Backend();

                String boxer = boxerName.getText();

                try{
                    backend.connect(boxer);

                    if(backend.nationality.equals("") && backend.wins.equals("n/a") && backend.losses.equals("n/a") && backend.draws.equals("n/a")
                            && backend.opponent.equals("n/a")){

                        connection = false;
                    }

                }catch(Exception e){
                    connection = false;
                }

                if(connection == false){
                    Platform.runLater(new Runnable() {
                        public void run() {

                            progress.setVisible(false);
                            new Interface().alertBox();
                        }
                    });
                }
                else{
                    loadInfo(boxer);
                    progress.setVisible(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    //If the name of the boxer's opponent is available then have the opponent's name return their info/stats when clicked on. An
    //alert box pops up when info on the opponent isn't available.
    public void opponentStats (MouseEvent me) throws IOException{

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (currentOpponent.contains("TBA") || currentOpponent.contains("n/a") || currentOpponent.contains("N/A")) {
                }
                else{
                    progress.setVisible(true);
                    connection = true;
                    Backend backend = new Backend();
                    backend.connect(currentBoxer);

                    try{
                        Backend back = new Backend();
                        back.connect(backend.opponent);

                        if(backend.nationality.equals("") && backend.wins.equals("n/a") && backend.losses.equals("n/a") && backend.draws.equals("n/a")
                                && backend.opponent.equals("n/a")){

                            connection = false;
                        }
                    }catch(Exception e){
                        connection = false;
                    }

                    if (connection == false) {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                progress.setVisible(false);
                                new Interface().alertBox();
                            }
                        });
                    }
                    else{
                        loadInfo(backend.opponent);
                        progress.setVisible(false);
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void deselect(MouseEvent me) {
        pane.requestFocus();
    }

    //Load the boxer info.
    public void loadInfo(String boxer) throws Exception{

        Backend backend = new Backend();
        currentBoxer = boxer;
        backend.connect(boxer);
        currentOpponent = backend.opponent;

        if(backend.bio.length() == 0){

           bioText.setPrefHeight(0);

        }else{
            bioText.setPrefHeight(USE_COMPUTED_SIZE);
        }

        new Interface().boxer(text, backend);
        new Interface().nation(backend.nationality, flag, backend);
        new Interface().record(wins, losses, draws, backend);
        new Interface().fight(nextFight, backend);
        new Interface().biography(biography, backend);
        new Interface().opponentStats(backend.opponent, backend.fight, enemy, opponent);

        new Interface().news(nextNews, backend.findArticles(backend.boxerNameCapitalized(currentBoxer)));

    }

    //Load's the info of the first searched boxer. The setText() method in the Interface class grabs the boxer name.
    public void initialize() throws Exception{

        progress.setVisible(false);
        boxerName.setText(new Interface().setText());
        loadInfo(boxerName.getText());
        boxerName.clear();
        new Interface().setAlignment();
    }

}
