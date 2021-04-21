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
    public static int[][] populacao = null;
    public static int[][] intermediaria = null;
    public static int[][] populacaoVazia = null;
    public static int TAM_POPULACAO;
    public static int TAM_ALUNOS;
    private static StringBuilder sbDetails = null;

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
            System.err.println("File not find");
            return "";
        }
    }

    private static boolean checkProbability(int percent) {
        int randomPercentage = Math.round(rand.nextFloat()*100);
        //debug("Received percentage = "+percent+"%\trandomPercentage = "+randomPercentage+"%\tapproved? "+ ((randomPercentage<=percent) ? true : false));
        return (randomPercentage<=percent) ? true : false;
    }

    public void iniciaExperimento(int generationSize, int populationSize, int mutationRate, int crossoverRate) {
        sbDetails = new StringBuilder("Visualizão detalhada:\n");
        sbDetails.append("População inicial \n");
        initPopulacao(populationSize);
        calculaAptidao();
        printPopulacao();
        if (!verificaCondicoesDeParada())
        {
            for (int g=0; g < generationSize; g++)
            {
                sbDetails.append("Geracao: ").append((g + 1)).append("\n");
                iniciaTabelaIntermediaria();
                sbDetails.append("Fazendo crossover (chance de "+crossoverRate+"%)\n");
                crossoverPBX(crossoverRate);
                populacao = intermediaria.clone();
                calculaAptidao(); // Calcula aptidão da geração atual
                printPopulacao(); // Mostra resultado do cálculo
                mutacao(mutationRate);
                if(verificaCondicoesDeParada()) // Verifica se chegou na condição ideal
                    break;
            }
        }
    }

    public boolean verificaCondicoesDeParada()
    {
        int melhorCromossomo = getMelhorCromossomo();
        int piorCromossomo = getPiorCromossomo();
        sbDetails.append("\t* Melhor  Cromossomo: ").append((melhorCromossomo + 1)).append("\n");
        sbDetails.append("\t* Pior  Cromossomo: ").append((piorCromossomo + 1)).append("\n");

        if (isIdeal(melhorCromossomo))
        {
            debug("Condição de parada atendida!");
            sbDetails.append("\tCondição de parada atendida!").append("\n");
            return true;
        }
        return false;
    }

    public String getBestCromossomo() {
      int[] cromossomo = populacao[getMelhorCromossomo()];
      StringBuilder sb = new StringBuilder("Melhor combinação de quartos encontrada:\n");
      for(int i = 0; i < cromossomo.length-1; i++)
          sb.append("- Quarto ").append(i+1).append(": A").append(i+1).append(", B").append(cromossomo[i]+1).append("\n");
      sb.append("Aptidao: ").append(cromossomo[cromossomo.length-1]);
          
      return sb.toString();
    }
    
    public static boolean isIdeal(int cromossomo)
    {
        if (populacao[cromossomo][TAM_ALUNOS] == 0 || isAptidao100PorcentoIgual())
            return true;
        return false;
    }

    public static boolean isAptidao100PorcentoIgual()
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
            System.err.println("An error occurred.");
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

    private static void initPopulacao(int populationSize)
    {
        TAM_POPULACAO = populationSize;
        populacao = new int[TAM_POPULACAO][TAM_ALUNOS+1];
        populacaoVazia = new int[TAM_POPULACAO][TAM_ALUNOS+1];
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
                        populacaoVazia[i][j] = -100;
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
        //sbDetails.append("  Aptidao:").append("\n");
        for (int i = 0; i < TAM_POPULACAO; i++) {
            populacao[i][TAM_ALUNOS] = calculaAptidaoCromossomo(i);
            //sbDetails.append("      Cromossomo ").append(i + 1).append(": ").append(populacao[i][TAM_ALUNOS]).append("\n");
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
        int pesoA = getPesoColegas(alunoA, alunoB);
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
        //System.out.println("Populacao:");
        sbDetails.append("\tPopulação:").append("\n");
        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            //System.out.print("C"+(i+1)+": ");
            sbDetails.append("\tC").append(i + 1).append(": ");
            for(j = 0; j < TAM_ALUNOS; j++)
            {
                //System.out.print("[A"+(j+1)+",B"+(populacao[i][j]+1)+"] ");
                sbDetails.append("[A").append(j + 1).append(",B").append((populacao[i][j]+1)).append("] ");
            }
            //System.out.println("FA: "+populacao[i][j]);
            sbDetails.append("FA: ").append(populacao[i][j]).append("\n");
            
        }
    }

    public static int getMelhorCromossomo()
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
        return melhorCromossomo;
    }

    public static int getPiorCromossomo()
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
        //System.out.println("Pior cromossomo: ["+piorAptidao+"] Aptidao: "+piorCromossomo);
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

    private static ArrayList<Integer> selecionaQuartosAleatoriamente(int randomRate)
    {
        // Escolhe quartos para realizar o swap.
        ArrayList<Integer> quartosSelecionados = new ArrayList<>();
        //debug("Selecionando quartos (chance de "+randomRate+"%)...");
        for (int k=0; k<TAM_ALUNOS; k++)
        {
            if (checkProbability(randomRate))
            {
                //debug("\tQuarto selecionado: "+k+".");
                quartosSelecionados.add(k);
            }
        }
        debug("Total de quartos selecionados: "+quartosSelecionados.size()+"/"+TAM_ALUNOS);
        return quartosSelecionados;
    }

    // funcao para pegar uma dupla disponivel que ainda nao foi utilizada
    public static int getAvaliablePartner(int cromossomo, ArrayList<Integer> alunosUsados) {
        for(int alunoA = 0; alunoA < TAM_ALUNOS; alunoA++)
        {
            int alunoB = populacao[cromossomo][alunoA];
            if(!alunosUsados.contains(alunoB))
            {
                return alunoB;
            }
        }

        return -1;
    }

    public static void iniciaTabelaIntermediaria()
    {
        intermediaria = populacaoVazia.clone();
        int melhorCromossomo = getMelhorCromossomo();

        // Repassa melhor cromossomo para posição 0 da população intermediária
        debug("Repassando melhor cromossomo para posição 0 da população intermediária...");
        for(int i = 0; i < TAM_ALUNOS; i++)
            intermediaria[0][i] = populacao[melhorCromossomo][i];
    }

    public static void crossoverPBX(int crossoverRate) {
        // Populando tabela intermediária
        debug("Iniciando crossover PBX...");
        int p = 1;
        while (p < TAM_POPULACAO)
        {
            if(checkProbability(crossoverRate))
            {
                // Escolhe cromossomos por torneio...
                int c1 = torneio();
                int c2 = torneio();
                sbDetails.append("Cromossomos selecionados por torneio: C"+c1+" e C"+c2+"...");
                debug("Cromossomos selecionados por torneio: C"+c1+" e C"+c2);
                debug("\tC"+c1+" antes do crossover: "+printCromossomo(c1, populacao));
                debug("\tC"+c2+" antes do crossover: "+printCromossomo(c2, populacao));

                // Escolhe quartos para realizar o swap.
                ArrayList<Integer> quartosSelecionados = selecionaQuartosAleatoriamente(50);

                // arrays com os quartos ja utilizados
                ArrayList<Integer> alunosUsadosC1 = new ArrayList<>();
                ArrayList<Integer> alunosUsadosC2 = new ArrayList<>();

                // Faz o swap nos quartos selecionados
                for (int i=0; i<quartosSelecionados.size(); i++)
                {
                    int alunoA = quartosSelecionados.get(i);
                    debug("Fazendo swapping dos cromossomos "+c1+" e "+c2+" no quarto de A"+alunoA+"...");
                    int aluno1 = populacao[c1][alunoA];
                    int aluno2 = populacao[c2][alunoA];

                    debug("\tAntes: C["+c1+"] = (A"+alunoA+",B"+aluno1+") e C["+c2+"] = (A"+alunoA+",B"+aluno2+")");

                    intermediaria[p][alunoA] = aluno2;
                    alunosUsadosC1.add(aluno2);
                    debug("Aluno  B"+aluno2+" usado em C1...");
                    if (p+1 < TAM_POPULACAO) {
                        //int[] c = intermediaria[p+1]; //testa o indice do cromossomo
                        intermediaria[p+1][alunoA] = aluno1;
                        alunosUsadosC2.add(aluno1);
                        debug("Aluno B"+aluno1+" usado em C2...");
                    }

                    String depois = "\tDepois: C["+p+"] = (A"+alunoA+",B"+intermediaria[p][alunoA]+") e C["+(p+1)+"] ";
                    if (p+1 < TAM_POPULACAO)
                        depois += "= (A"+alunoA+",B"+intermediaria[p+1][alunoA]+")";
                    else
                        depois += "ignorado pois ultrapassa tamanho limite da população (máx: "+(TAM_POPULACAO-1)+")";
                    debug(depois);
                }
                
                // Preenche quartos não-selecionados
                debug("Preenchendo os "+(TAM_ALUNOS-quartosSelecionados.size())+" quartos restantes de C"+p+" e C"+(p+1)+"...");
                for(int i = 0; i < TAM_ALUNOS; i++)
                {
                    if (!quartosSelecionados.contains(i))
                    {
                        //valido a funcao para cada um dos cromossomos
                        int alunoDisponivelC1 = getAvaliablePartner(c1, alunosUsadosC1);
                        int alunoDisponivelC2 = getAvaliablePartner(c2, alunosUsadosC2);

                        if(alunoDisponivelC1 != -1) {
                            intermediaria[p][i] = alunoDisponivelC1;
                            alunosUsadosC1.add(alunoDisponivelC1);
                        }

                        //verifica se nao é a ultima posicao disponivel
                        if (p+1 < TAM_POPULACAO) {
                            if(alunoDisponivelC2 != -1) {
                                intermediaria[p+1][i] = alunoDisponivelC2;
                                alunosUsadosC2.add(alunoDisponivelC2);
                            }
                        }
                    }
                }
                sbDetails.append("\n\tDepois do crossover:\t").append(printCromossomo(p, intermediaria));
                if (p+1 < TAM_POPULACAO)
                    sbDetails.append("\tDepois do crossover:\t").append(printCromossomo(p+1, intermediaria));
                p += 2;
            }
            else
            {
                // Copia direto para intermediária
                for (int k=0; k<TAM_ALUNOS; k++)
                {
                   intermediaria[p][k] = populacao[p][k];
                }
                p++;
            }
        }
    }

    public static void mutacao(int mutationRate){
        if (TAM_POPULACAO < 2)
            return;
        int c = 0;
        while (c == 0)
            c = rand.nextInt(TAM_POPULACAO);
        int quarto1 = rand.nextInt(TAM_ALUNOS);
        int quarto2 = rand.nextInt(TAM_ALUNOS);
        
        //System.out.println("Cromossomo " + (cromossomo+1) + " sofreu MUTAÇÃO nos quartos " + (quarto1+1) + " e " + (quarto2+1));
        sbDetails.append("\tCromossomo ").append(c+1).append(" sofreu MUTAÇÃO nos quartos ").append((quarto1+1)).append(" e ").append( (quarto2+1)).append("\n");
        int alunoB1 = populacao[c][quarto1];
        int alunoB2 = populacao[c][quarto2];
        int aux = alunoB1;
        populacao[c][quarto1] = alunoB2;
        populacao[c][quarto2] = aux;         
        calculaAptidaoCromossomo(c);
        sbDetails.append("\t\t").append(printCromossomo(c, populacao));
    } 
    
    public String showVisualizationComplete() {
        return sbDetails.toString();
    }

    private static String printCromossomo(int c, int[][] pop)
    {
        StringBuilder sb = new StringBuilder("C"+(c+1)+": ");
        for(int alunoA = 0; alunoA < TAM_ALUNOS; alunoA++)
        {
            sb.append("[A"+alunoA+",B"+pop[c][alunoA]+"] ");
        }
        sb.append("FA: ").append(pop[c][TAM_ALUNOS]).append("\n");
        return sb.toString();
    }

    public static void debug(String s)
    {
        if (s == null)
        {
            String debugText = "## [info]:\t "+s;
            System.out.println(debugText);
        }
    }
}
