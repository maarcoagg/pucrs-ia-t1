import java.util.Set;
import java.util.HashSet;
import java.util.Random;

public class AlgoritmoGenetico {
    private static Random rand = new Random();
    private static Preferencias p;
    public static int[][] populacao;
    public static int[][] intermediaria;
    public static int TAM_POPULACAO = 3;
    public static int TAM_ALUNOS;
    
    public static void main(String[] args) throws Exception {
        p = new Preferencias("config/duplos4.txt");
        System.out.print(p.toString());
        TAM_ALUNOS = p.getTamAlunos();
        initPopulacao();
        for (int g=0; g<10000; g++)
        {
            System.out.println("# Geração: " + (g+1));
            calculaAptidao();
            printPopulacao();
            if (checkIdeal(getMelhor()))
                break;
            crossover();
            populacao = intermediaria.clone();
            double taxaMutacao = 1.0 - rand.nextFloat(); 
            if(taxaMutacao >= 0.95)
                mutacao();
        }
        printPopulacao(); 
        int[] c = populacao[getMelhor()];
        System.out.println(Visual.getStringCromossomo(c));
        if(args.length == 0) {
            //escolha default visualizacao rapida
            //exibiQuartosFinal(); 
        } 
        else
        {
            switch(args[0].toLowerCase())
            {
                case "s":
                //caso visualizacao completa
                // chamar mais uma funcao
                //exibiQuartosFinal();
                break;
                default:
                case "n":
                //caso visualizacao rapida
                //exibiQuartosFinal();
            }
        }
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
        Set<Integer> setAlunos = new HashSet<>(TAM_ALUNOS);
        for(int i = 0; i < TAM_ALUNOS; i++)
            setAlunos.add(i);

        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            Set<Integer> disponivel = new HashSet<>(setAlunos);
            for(int j = 0; j < TAM_ALUNOS; j++)
            {
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

    public static void calculaAptidao()
    {
        for (int i = 0; i < TAM_POPULACAO; i++)
            populacao[i][TAM_ALUNOS] = calculaAptidaoCromossomo(i);
    }

    public static int calculaAptidaoCromossomo(int cromossomo)
    {  
        int aptidao = 0;        
        for(int j = 0; j < TAM_ALUNOS; j++)
        {
            int alunoA = j;
            int alunoB = populacao[cromossomo][j]+TAM_ALUNOS;
            aptidao += calculaAptidaoQuarto(alunoA, alunoB);
        }
        return aptidao;
    }

    private static int calculaAptidaoQuarto(int alunoA, int alunoB)
    {
        int prefA = p.getPreferencia(alunoA, alunoB-TAM_ALUNOS);
        int prefB = p.getPreferencia(alunoB, alunoA);
        return prefA + prefB;       
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
        //print("Melhor cromossomo: ["+melhorAptidao+"] Aptidao: "+melhorAptidao);
        for(int i = 0; i < TAM_ALUNOS; i++)
            intermediaria[0][i] = populacao[melhorCromossomo][i];
        return melhorCromossomo;
    }

    public static int torneio(){
        int cromossomoA = rand.nextInt(TAM_POPULACAO);
        int cromossomoB = rand.nextInt(TAM_POPULACAO);        
        
        if(populacao[cromossomoA][TAM_ALUNOS] < populacao[cromossomoB][TAM_ALUNOS])
            return cromossomoA;
        else
            return cromossomoB;
    }

    public static void crossover()
    {         
        // P = 1 - Nunca faz crossover, apenas elitismo
        // P = 2 - Crossover começa no ultimo indice [1], estoura exception
        // P = 3 - Crossover ok
        // p = 4 - Exception
        // P = 5 - Crossover ok
        if (TAM_POPULACAO >= 2)
        {
            for (int j=1; j<TAM_POPULACAO; j=j+2)
            {
                int ind1 = torneio();
                int ind2 = torneio();
                if (j-TAM_POPULACAO > 1)
                {
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
        }
    }

    public static void mutacao(){
        int quant = rand.nextInt(3)+1;
        for(int i = 0; i<quant; i++){
            int cromossomo = rand.nextInt(TAM_POPULACAO);
            int quarto1 = rand.nextInt(TAM_ALUNOS);
            int quarto2 = rand.nextInt(TAM_ALUNOS);
        
            System.out.print("Cromossomo " + (cromossomo+1) + " sofreu MUTAÇÃO nos quartos " + (quarto1+1) + " e " + (quarto2+1));
            int alunoB1 = populacao[cromossomo][quarto1];
            int alunoB2 = populacao[cromossomo][quarto2];
            int aux = alunoB1;
            populacao[cromossomo][quarto1] = alunoB2;
            populacao[cromossomo][quarto2] = aux;   
        }
    }         
}