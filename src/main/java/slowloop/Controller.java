package slowloop;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

import static javafx.geometry.Pos.CENTER;

public class Controller {

    public TextField boxer;
    public Button search;
    public GridPane pane1, newPane;
    public HBox box;
    public Task<Void> task;
    public ProgressIndicator progress1;
    public String aBoxer;


    public void handle(KeyEvent ke) throws IOException{

        final KeyEvent event = ke;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                if (event.getCode() == KeyCode.ENTER) {

                    try{
                        aBoxer = boxer.getText();

                        Backend backend = new Backend();
                        backend.connect(aBoxer);

                        if(backend.nationality.equals("") && backend.wins.equals("n/a") && backend.losses.equals("n/a") && backend.draws.equals("n/a")
                                && backend.opponent.equals("n/a")){

                            new Interface().alertBox();

                        }
                        else{
                            newScene();
                        }

                    }catch(Exception e){
                        Platform.runLater(new Runnable() {
                            public void run() {
                                new Interface().alertBox();
                            }
                        });
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void searchBoxer(ActionEvent ae) throws IOException{

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try{
                    aBoxer = boxer.getText();

                    Backend backend = new Backend();
                    backend.connect(aBoxer);

                    if(backend.nationality.equals("") && backend.wins.equals("n/a") && backend.losses.equals("n/a") && backend.draws.equals("n/a")
                            && backend.opponent.equals("n/a")){

                        new Interface().alertBox();

                    }
                    else{
                        newScene();
                    }
                }catch(Exception e){
                    Platform.runLater(new Runnable() {
                        public void run() {
                            new Interface().alertBox();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void deselect(MouseEvent me) {
        pane1.requestFocus();
    }

    //Returns the name of the boxer entered into the Textfield.
    public String boxer(){
        return aBoxer;
    }

    public void initialize(){
        pane1.setAlignment(CENTER);
        progress1.setVisible(false);
    }

    //Loads a new FXML into a new GridPane object and sets the current GridPane with the new GridPane (containing the new FXML).
    public void newScene(){

        task = new Task<Void>(){
            protected Void call() throws Exception {
                progress1.setVisible(true);
                newPane = FXMLLoader.load(getClass().getResource("/boxing.fxml"));
                return null;
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                progress1.setVisible(false);
                pane1.getChildren().setAll(newPane);
            }
        });
        new Thread(task).start();
    }
}
