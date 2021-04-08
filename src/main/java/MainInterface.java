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
import java.util.Random;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;

public class MainInterface extends Application {
    private static Random rand = new Random();
    public static int[][] alunos; // Configuração inicial
    public static int[][] populacao;
    public static int[][] intermediaria;
    public static int TAM_POPULACAO = 5;
    public static int TAM_ALUNOS;
    private Desktop desktop = Desktop.getDesktop();
    private ObservableList list; 
    final MainController controller = new MainController();

    private boolean isCompleteVisualization = false;

    private int numberPopulation;

    private ScrollPane sp = new ScrollPane();

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        final FileChooser fileChooser = new FileChooser();

 
        final Button openButton = new Button("Abrir arquivo...");
        final Button startButton = new Button("Iniciar experimento");
        TextField numberOfPopulation = new TextField("Numero da população");

        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Visualização rapida");  
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
            });
            
            startButton.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(final ActionEvent e) {
                            //aqui vai iniciar a simulacao de fato
                            RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                            String selected = selectedRadioButton.getText();
                            if(selected == "Visualização rapida") {
                                isCompleteVisualization = false;
                            } else {
                                isCompleteVisualization =true;
                            }
                            controller.startExperiment();
                            showExperiment(controller.showFinalResult());
                            numberPopulation = Integer.parseInt(numberOfPopulation.getText()); 
                            System.out.println("Valor digitado" + numberPopulation);

                            System.out.println("Tipo de visualizacao" + isCompleteVisualization);
                        }
                    });
                    

      //Setting the space between the nodes of a VBox pane 
      root.setSpacing(5);   
      
      //Setting the margin to the nodes 
      root.setMargin(hbox, new Insets(20, 20, 20, 20));  

      list = root.getChildren(); 
      list.addAll(hbox, numberOfPopulation);
      list.add(sp);
        Scene scene = new Scene(root, 740, 580);
        stage.setScene(scene);
        stage.show();
    }
    private void openFile(File file) {
        try {
      
            Scanner s = new Scanner(file);
            TAM_ALUNOS = Integer.parseInt(s.nextLine());
            alunos = new int[TAM_ALUNOS*2][TAM_ALUNOS];
            for(int i = 0; i < TAM_ALUNOS*2; i++)
            {
                if (s.hasNextLine())
                {
                    String outraEscola = " B";
                    if (i >= TAM_ALUNOS)
                        outraEscola = " A";
                    String[] afinidades = s.nextLine().split(outraEscola);
                    for(int j = 1; j < afinidades.length; j++ )
                        alunos[i][j-1] = Integer.parseInt(afinidades[j].trim()) - 1;
                }
            }

            s.close();
            Label l = new Label("arquivo: "+file.getName()+" carregado com sucesso");
            list.add(l);
               
        } catch (IOException ex) {
            System.out.println("File not find");
        }
    }

    private void showExperiment(String result) { 
        Label l = new Label(result);
        //list.add(l);
        sp.setContent(l);
    }

    public static boolean checkIdeal(int cromossomo)
    {
        if (populacao[cromossomo][TAM_ALUNOS] == 0 || isAptidao100Porcento())
            return true;
        return false;
    }

    public static boolean isAptidao100Porcento()
    {
        for(int i = 1; i < TAM_POPULACAO; i++)
            if(populacao[i][TAM_ALUNOS] != populacao[i-1][TAM_ALUNOS])
                return false;
        return true;
    }

    public static void initPopulacao()
    {
        populacao = new int[TAM_POPULACAO][TAM_ALUNOS+1];
        intermediaria = new int[TAM_POPULACAO][TAM_ALUNOS+1];
        Set<Integer> disponivel;

        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            //Populacao = [i]
            disponivel = getDisponiveis();
            for(int j = 0; j < TAM_ALUNOS; j++)
            {
                //{ A = [j], B = [i][j] }
                boolean contains = false;
                while(!contains)
                {
                    int aluno2 = rand.nextInt(TAM_ALUNOS);
                    contains = disponivel.contains(aluno2);
                    if (contains)
                    {
                        populacao[i][j] = aluno2;
                        disponivel.remove(aluno2);
                    }
                }
            }
        }
    }

    private static Set<Integer> getDisponiveis()
    {
        Set<Integer> disponivel = new HashSet<>(TAM_ALUNOS);
        for(int i = 0; i < TAM_ALUNOS; i++)
            disponivel.add(i);
        return disponivel;
    }

    public static void calculaAptidao()
    {
        for (int i = 0; i < TAM_POPULACAO; i++)
            populacao[i][TAM_ALUNOS] = calculaAptidaoCromossomo(i);
    }

    public static int calculaAptidaoCromossomo(int cromossomo)
    {  
        //Populacao = [cromossomo]
        //{ A = [j], B = [i][j] }
        int aptidao = 0;        
        for(int j = 0; j < TAM_ALUNOS; j++)
        {
            int alunoA = j;
            int alunoB = populacao[cromossomo][j];
            aptidao += getPesoSala(alunoA, alunoB);
        }
        return aptidao;
    }

    private static int getPesoSala(int alunoA, int alunoB)
    {
        int pesoA = getPesoColegas(alunoA, alunoB+TAM_ALUNOS);
        int pesoB = getPesoColegas(alunoB+TAM_ALUNOS, alunoA);
        return pesoA + pesoB;       
    }

    private static int getPesoColegas(int aluno1, int aluno2)
    {
        int peso = 0;
        for(int j = 0; j < TAM_ALUNOS; j++)
            if (alunos[aluno1][j] == aluno2)
            {
                peso = j;
                break;
            }   
        return peso;
    }


    public static void printPopulacao()
    {
        int j;
        System.out.println("Populacao:");
        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            System.out.print("C"+(i+1)+": ");
            for(j = 0; j < TAM_ALUNOS; j++)
            {
                System.out.print("[A"+(j+1)+",B"+(populacao[i][j]+1)+"] ");
            }
            System.out.println("F.A.: "+populacao[i][j]);
        }
    }

    public static int getMelhor()
    {
        int melhorCromossomo = Integer.MAX_VALUE;
        int melhorAptidao = Integer.MAX_VALUE;
        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            int aptidao = populacao[i][TAM_ALUNOS];
            if (aptidao < melhorAptidao)
            {
                melhorAptidao = aptidao;
                melhorCromossomo = i;
            }   
        }
        System.out.println("Melhor cromossomo: ["+melhorAptidao+"] Aptidao: "+melhorAptidao);
        for(int i = 0; i < TAM_ALUNOS; i++)
            intermediaria[0][i] = populacao[melhorCromossomo][i];
        return melhorCromossomo;
    }

    public static int torneio(){
        int cromossomoA = rand.nextInt(TAM_POPULACAO);
        int cromossomoB = rand.nextInt(TAM_POPULACAO);        
        
        //populacao[Cromossomo][alunoA] = [alunoB]
        if(populacao[cromossomoA][TAM_ALUNOS] < populacao[cromossomoB][TAM_ALUNOS])
            return cromossomoA;
        else
            return cromossomoB;
    }

    public static void crossover()
    {         
        for (int j=1; j<TAM_POPULACAO; j=j+2)
        {
            int ind1 = torneio();
            int ind2 = torneio();
            for (int k=0; k<TAM_ALUNOS/2; k++)
            {
                intermediaria [j][k]= populacao [ind1][k];
                intermediaria [j+1][k]= populacao [ind2][k];
            }
            for (int k=TAM_ALUNOS/2; k<TAM_ALUNOS; k++)
            {
                intermediaria [j][k]= populacao [ind2][k];
                intermediaria [j+1][k]= populacao [ind1][k];
            }
        }
    }

    public static void mutacao(){
        int quant = rand.nextInt(3)+1;
        for(int i = 0; i<quant; i++){
            int cromossomo = rand.nextInt(TAM_POPULACAO);
            int quarto1 = rand.nextInt(TAM_ALUNOS);
            int quarto2 = rand.nextInt(TAM_ALUNOS);
        
            System.out.println("Cromossomo " + (cromossomo+1) + " sofreu MUTAÇÃO nos quartos " + (quarto1+1) + " e " + (quarto2+1));
            int alunoB1 = populacao[cromossomo][quarto1];
            int alunoB2 = populacao[cromossomo][quarto2];
            int aux = alunoB1;
            populacao[cromossomo][quarto1] = alunoB2;
            populacao[cromossomo][quarto2] = aux;   
        }
    }  

    public static void main(String[] args) {
        launch();
    }

}
