import java.util.Set;
import java.util.HashSet;
import java.util.Random;

public class AlgoritmoGenetico {
    private static Random rand = new Random();
    public static int[][] populacao;
    public static int[][] intermediaria;
    public static int TAM_POPULACAO = 5;
    public static int TAM_ALUNOS;
    private static Preferencias p;
    
    public static void main(String[] args) throws Exception {
        p = new Preferencias("duplos4.txt");
        TAM_ALUNOS = p.getTamAlunos();
        print(p.toString());
        initPopulacao();
        for (int g=0; g<10000; g++)
        {
            print("Geração: " + (g+1));
            calculaAptidao();
            //if (g%500 == 0)
                //printPopulacao(); 
            int c = getMelhor();
            boolean ideal = checkIdeal(c);
            if (ideal)
                break;
            crossover();
            populacao = intermediaria.clone();
            if(rand.nextInt(5)==0) {
                mutacao();
            }	          
        }
        printPopulacao(); 
        int[] c = populacao[getMelhor()];
        print(Visual.getStringCromossomo(c));
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
        Set<Integer> disponivel;

        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            disponivel = getDisponiveis();
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
        int aptidao = 0;        
        for(int j = 0; j < TAM_ALUNOS; j++)
            aptidao += getPesoSala(j, populacao[cromossomo][j]+TAM_ALUNOS);
        return aptidao;
    }

    private static int getPesoSala(int alunoA, int alunoB)
    {
        int pesoA = p.getPreferencia(alunoA, alunoB);
        int pesoB = p.getPreferencia(alunoB, alunoA);
        return pesoA + pesoB;       
    }

    public static void printPopulacao()
    {
        int j;
        print("Populacao:");
        for(int i = 0; i < TAM_POPULACAO; i++)
        {
            System.out.print("C"+(i+1)+": ");
            for(j = 0; j < TAM_ALUNOS; j++)
            {
                System.out.print("[A"+(j+1)+",B"+(populacao[i][j]+1)+"] ");
            }
            print("F.A.: "+populacao[i][j]);
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
        
            print("Cromossomo " + (cromossomo+1) + " sofreu MUTAÇÃO nos quartos " + (quarto1+1) + " e " + (quarto2+1));
            int alunoB1 = populacao[cromossomo][quarto1];
            int alunoB2 = populacao[cromossomo][quarto2];
            int aux = alunoB1;
            populacao[cromossomo][quarto1] = alunoB2;
            populacao[cromossomo][quarto2] = aux;   
        }
    }    

    private static void print(String s)
    {
        System.out.println(s);
    } 
}