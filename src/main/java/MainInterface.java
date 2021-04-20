import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner; 
import javafx.scene.layout.VBox;
import javafx.geometry.Insets; 
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import java.util.Set;
import java.util.HashSet;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.*;

public class MainInterface extends Application {

    final MainController controller = new MainController();
    private Desktop desktop = Desktop.getDesktop();
    private ScrollPane sp = new ScrollPane();
    private ObservableList list; 

    private boolean isCompleteVisualization = false;
    private int population;
    private int mutationRate;
    private int crossoverRate;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        final FileChooser fileChooser = new FileChooser();
        final Button openButton = new Button("Abrir arquivo...");
        final Button startButton = new Button("Iniciar experimento");

        Text populationLabel = new Text();
        populationLabel.setText("Número da população:");
        Text mutationLabel = new Text();
        mutationLabel.setText("Taxa de mutação de 0 a 100:");
        Text crossoverLabel = new Text();
        crossoverLabel.setText("Taxa de crossover de 0 a 100:");

        // por default crossover tera 100% e a mutacao 5%
        TextField populationText = new TextField("20");
        TextField mutationRateText = new TextField("5");
        TextField crossoverRateText = new TextField("100");

        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Visualização rápida");  
        rb1.setToggleGroup(group);
        rb1.setSelected(true);

        RadioButton rb2 = new RadioButton("Visualização completa");
        rb2.setToggleGroup(group);
 
        HBox hbox = new HBox(openButton, startButton, rb1,rb2);
        hbox.setSpacing(20); 

        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        String result = controller.loadFile(file);
                        showExperiment(result);
                    }
                }
            }
        );
            
        startButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    //aqui vai iniciar a simulacao de fato
                    RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                    String selected = selectedRadioButton.getText();

                    if(selected == "Visualização rápida")
                        isCompleteVisualization = false;
                    else
                        isCompleteVisualization = true;
                    
                    population = getRateText(populationText.getText(), 20);
                    mutationRate = getRateText(mutationRateText.getText(), 5);
                    crossoverRate = getRateText(crossoverRateText.getText(), 100);
    
                    //System.out.println("Valor digitado: " + population);
                    //System.out.println("Tipo de visualizacao: " + isCompleteVisualization);

                    controller.startExperiment(population, mutationRate, crossoverRate);
                    if(isCompleteVisualization) {
                        String finalT = controller.showCompleteResult() +"\n"+ controller.showFinalResult();
                        showExperiment(finalT);
                    } else {
                        showExperiment(controller.showFinalResult());
                    }
                }
            });
                    
        root.setSpacing(5);   
      
        root.setMargin(hbox, new Insets(20, 20, 20, 20));  

        list = root.getChildren(); 
        list.addAll(hbox, populationLabel, populationText, mutationLabel, mutationRateText, crossoverLabel, crossoverRateText);
        list.add(sp);
        Scene scene = new Scene(root, 740, 580);
        stage.setScene(scene);
        stage.show();
    }

    private int getRateText(String text, int defaultRate)
    {
        if(text == null || text.isEmpty())
            return defaultRate;
        return Integer.parseInt(text); 
    }

    private void showExperiment(String result) { 
        Label l = new Label(result);
        sp.setContent(l);
    }

    public static void main(String[] args) {
        launch();
    }
}
