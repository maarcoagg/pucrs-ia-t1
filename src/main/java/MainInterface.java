import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner; 
import javafx.scene.layout.VBox;
import javafx.geometry.Insets; 
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;


public class MainInterface extends Application {
    private Desktop desktop = Desktop.getDesktop();
    private ObservableList list; 

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        final FileChooser fileChooser = new FileChooser();
 
        final Button openButton = new Button("Abrir arquivo...");
        final Button restartButton = new Button("Reiniciar");
        final Button startButton = new Button("Iniciar experimento");
        HBox hbox = new HBox(openButton, restartButton, startButton);
        hbox.setSpacing(20);

        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        openFile(file);
                    }
                }
            });

            restartButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        //aqui vai remover todos os dados dos experimentos
                      list.remove(1, list.size());
                    }
                });

            startButton.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(final ActionEvent e) {
                            //aqui vai iniciar a simulacao de fato
                            System.out.println("Estou aqui");
                         ;
                        }
                    });

              //Setting the space between the nodes of a VBox pane 
        root.setSpacing(10);   
      
      //Setting the margin to the nodes 
      root.setMargin(hbox, new Insets(20, 20, 20, 20));  
      root.setMargin(l, new Insets(20, 20, 20, 20)); 
      list = root.getChildren(); 
      list.addAll(hbox, l);
        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }
    private void openFile(File file) {
        try {
      
                Scanner s = new Scanner(file);
               int TAM_ALUNOS = Integer.parseInt(s.nextLine());
                for(int i = 0; i < TAM_ALUNOS*2; i++)
                {
                    if (s.hasNextLine())
                    {
                        String outraEscola = " B";
                        if (i >= TAM_ALUNOS)
                            outraEscola = " A";
                        String[] afinidades = s.nextLine().split(outraEscola);
                        System.out.println(s.nextLine());
                    }
                }
    
                s.close();
                Label l = new Label("arquivo"+file.getName()+"lido");
                list.add(l);
               
        } catch (IOException ex) {
            System.out.println("File not find");
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
