import java.io.File;

public class MainController {

  private AlgoritmoGenetico ag;

  public MainController() {
    ag = new AlgoritmoGenetico();
  }

  public String loadFile(File file) {
    return ag.loadFileAndInitAlunos(file);
  }

  public String showCompleteResult() {
    return ag.showVisualizationComplete();
  }

  public String showFinalResult() {
    return ag.getBestCromossomo();
  }

  public void startExperiment(int sizeOfpopulation, int taxMutation, int taxCrossover) {
    ag.iniciaExperimento(sizeOfpopulation, taxMutation, taxCrossover);
  }
    
}
