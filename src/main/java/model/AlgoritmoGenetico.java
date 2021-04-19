
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.io.File;  
import java.io.FileNotFoundException;
import java.util.Scanner; 
import java.io.IOException;

public class AlgoritmoGenetico {
    private static Random rand = new Random();
    public static int[][] alunos; // Configuração inicial
    public static int[][] populacao;
    public static int[][] intermediaria;
    public static int TAM_POPULACAO;
    public static int TAM_ALUNOS;
    private static StringBuilder sbDetails = new StringBuilder("Visualizão detalhada:\n");


    public String loadFileAndInitAlunos(File file) {
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
        return file.getName();
    } catch (IOException ex) {
        System.out.println("File not find");
        return "";
    }
    }

    public boolean isTaxPercent(int percent) {
        float lucky = 1 - rand.nextFloat();
        int rightPercent = percent % 100;
        return lucky >= 1 - rightPercent;
    }

    public void iniciaExperimento(int sizeOfpopulation, int taxMutation, int taxCrossover) {
      initPopulacao(sizeOfpopulation);
      boolean isTaxMutation = isTaxPercent(taxMutation);
      boolean isTaxCrossover = isTaxPercent(taxCrossover);

      for (int g=0; g<10000; g++)
      {
          System.out.println("Geração: " + (g+1));
          sbDetails.append("Geracao: ").append((g + 1)).append("\n");
          calculaAptidao();
          int c = getMelhor();
          int piorCromosso = getPior();
          sbDetails.append("  Melhor  Cromossomo: ").append((c + 1)).append("\n");
          sbDetails.append("  Pior  Cromossomo: ").append((piorCromosso + 1)).append("\n");
          printPopulacao();
          boolean ideal = checkIdeal(c);
          if (ideal) {
            sbDetails.append("  Condição de parada atendida").append("\n");
            break;
          }
          if(isTaxCrossover) {
            crossoverPBX();
          }
          populacao = intermediaria.clone();
          if(isTaxMutation) {
            mutacao();
          }      
      }
    }

    public String getBestCromossomo() {
      int[] cromossomo = populacao[getMelhor()];
      StringBuilder sb = new StringBuilder("Melhor Combinação de quartos encontrada:\n");
      for(int i = 0; i < cromossomo.length-1; i++)
          sb.append("- Quarto ").append(i+1).append(": A").append(i+1).append(", B").append(cromossomo[i]+1).append("\n");
      sb.append("Aptidao: ").append(cromossomo[cromossomo.length-1]);
          
      return sb.toString();
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

    public static void initAlunos(String filename)
    {
        try {
            File config = new File(filename);
            Scanner s = new Scanner(config);
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
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void printAlunos()
    {
        System.out.println("\nConfiguração inicial: ");
        // Escola A
        for(int i = 0; i < TAM_ALUNOS; i++)
        {
            System.out.print("A"+(i+1)+": ");
            for(int j = 0; j < TAM_ALUNOS; j++)
            {
                System.out.print("B"+(alunos[i][j]+1)+" ");
            }
            System.out.println();
        }
        // Escola B
        for(int i = TAM_ALUNOS; i < TAM_ALUNOS*2; i++)
        {
            System.out.print("B"+(i-TAM_ALUNOS+1)+": ");
            for(int j = 0; j < TAM_ALUNOS; j++)
            {
                System.out.print("A"+(alunos[i][j]+1)+" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void initPopulacao(int sizeOfpopulation)
    {
        TAM_POPULACAO = sizeOfpopulation;
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
        sbDetails.append("  Aptidao:").append("\n");
        for (int i = 0; i < TAM_POPULACAO; i++) {
            populacao[i][TAM_ALUNOS] = calculaAptidaoCromossomo(i);
            sbDetails.append("      Cromossomo ").append(i + 1).append(": ").append(populacao[i][TAM_ALUNOS]).append("\n");
        }
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
        sbDetails.append("      População:").append("\n");
        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            System.out.print("C"+(i+1)+": ");
            sbDetails.append("       C").append(i + 1).append(": ");
            for(j = 0; j < TAM_ALUNOS; j++)
            {
                System.out.print("[A"+(j+1)+",B"+(populacao[i][j]+1)+"] ");
                sbDetails.append("[A").append(j + 1).append(",B").append((populacao[i][j]+1)).append("] ");
            }
            System.out.println("F.A.: "+populacao[i][j]);
            sbDetails.append("F.A.: ").append(populacao[i][j]).append("\n");
            
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

    public static int getPior()
    {
        int piorCromossomo = 0;
        int piorAptidao = 0;
        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            int aptidao = populacao[i][TAM_ALUNOS];
            if (aptidao > piorAptidao)
            {
                piorAptidao = aptidao;
                piorCromossomo = i;
            }   
        }
        System.out.println("Pior cromossomo: ["+piorAptidao+"] Aptidao: "+piorCromossomo);
        return piorCromossomo;
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

    public static void crossoverPBX() {
        int ind1 = torneio();
        int ind2 = torneio();
        //posicoes selecionadas
        ArrayList<Integer> positionSelecteds = new ArrayList();
       
        for (int k=0; k<TAM_ALUNOS; k++)
        {
            // porcentagem de change de pegar a posicao
            if(rand.nextInt(2)==0) {
                positionSelecteds.add(k);
            }
        }

        for (int i=0; i<positionSelecteds.size(); i++)
        {
            int alunoB1 = populacao[ind1][positionSelecteds.get(i)];
            int alunoB2 =populacao[ind2][positionSelecteds.get(i)];
            int aux = alunoB1;
            populacao[ind1][positionSelecteds.get(i)] = alunoB2;
            populacao[ind2][positionSelecteds.get(i)] = aux;   
           
        }

        for (int j=1; j<TAM_POPULACAO; j++) {
            for (int k=0; k<TAM_ALUNOS; k++)
            {
               intermediaria[j][k] = populacao[ind1][k];
               if(j < TAM_POPULACAO - 1) {
                intermediaria[j + 1][k] = populacao[ind2][k];
               }
            }
        }

    }

    public static void mutacao(){
        int cromossomo = rand.nextInt(TAM_POPULACAO);
        int quarto1 = rand.nextInt(TAM_ALUNOS);
        int quarto2 = rand.nextInt(TAM_ALUNOS);
        
        System.out.println("Cromossomo " + (cromossomo+1) + " sofreu MUTAÇÃO nos quartos " + (quarto1+1) + " e " + (quarto2+1));
        sbDetails.append("  Cromossomo ").append(cromossomo+1).append(" sofreu MUTAÇÃO nos quartos ").append((quarto1+1)).append(" e ").append( (quarto2+1)).append("\n");
        int alunoB1 = populacao[cromossomo][quarto1];
        int alunoB2 = populacao[cromossomo][quarto2];
        int aux = alunoB1;
        populacao[cromossomo][quarto1] = alunoB2;
        populacao[cromossomo][quarto2] = aux;         
    } 
    
    public String showVisualizationComplete() {
        return sbDetails.toString();
    }
}
