public class Visual {
    
    public static String getStringCromossomo(int[] cromossomo)
    {
        StringBuilder sb = new StringBuilder("Melhor aptidao: "+cromossomo[cromossomo.length-1]+"\n");
        for(int i = 0; i < cromossomo.length-1; i++)
            sb.append("- Quarto ").append(i+1).append(": A").append(i+1).append(", B").append(cromossomo[i]+1).append("\n");            
        return sb.toString();
    }
}
