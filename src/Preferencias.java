import java.io.File;  
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Preferencias {

    private Integer TAM_ALUNOS = 0;
    private Integer[][] preferencias;

    public Preferencias(String filename) throws FileNotFoundException, Exception
    {
        File f = new File(filename);
        Scanner s = new Scanner(f);
        TAM_ALUNOS = Integer.parseInt(s.nextLine().trim());
        preferencias = new Integer[TAM_ALUNOS*2][TAM_ALUNOS];
        for(int i = 0; i < TAM_ALUNOS*2; i++)
        {
            if (s.hasNextLine())
            {
                String outraEscola = " B";
                if (i >= TAM_ALUNOS)
                    outraEscola = " A";
                String[] afinidades = s.nextLine().split(outraEscola);
                for(int j = 1; j < afinidades.length; j++ )
                    preferencias[i][j-1] = Integer.parseInt(afinidades[j].trim()) - 1;
            }
            else
            {
                s.close();
                throw new Exception("Arquivo Invalido: Faltam dados de preferencias.");
            }
        }
        s.close();
    }

    public int getTamAlunos()
    {
        return TAM_ALUNOS;
    }

    /**
     * Calcula a preferencia do aluno 1 por aluno 2
     * @param aluno1 aluno 1
     * @param aluno2 aluno 2
     * @return preferencia
     */
    public int getPreferencia(int aluno1, int aluno2)
    {
        int peso = Integer.MAX_VALUE;
        for(int j = 0; j < TAM_ALUNOS; j++)
        {
            if (preferencias[aluno1][j] == aluno2)
            {
                peso = j;
                break;
            }   
        }
        return peso;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("\nPreferências dos alunos:\n")
                            .append("(+)  ");
        for(int i = 0; i < TAM_ALUNOS; i++)
            sb.append(i + "  ");
        sb.append("(-)\n");
        for(int i = 0; i < TAM_ALUNOS*2; i++)
        {
            int aluno1;
            String escola2;
            if (i < TAM_ALUNOS)
            {
                sb.append("A");
                aluno1 = i+1;
                escola2 = "B";
            }
            else
            {
                sb.append("B");
                aluno1 = i-TAM_ALUNOS+1;
                escola2 = "A";
            }
            sb.append(aluno1).append(": ");
            for(int j = 0; j < TAM_ALUNOS; j++)
            {
                int aluno2 = preferencias[i][j]+1;
                sb.append(escola2).append(aluno2).append(" ");
            }                
            sb.append("\n");
        }
        return sb.toString();
    }
}
