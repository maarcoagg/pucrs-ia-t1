public class Visual {
    
  public static String getStringCromossomo(int[] cromossomo)
  {
      StringBuilder sb = new StringBuilder("Melhor cromossomo:\n");
      for(int i = 0; i < cromossomo.length-1; i++)
          sb.append("- Quarto ").append(i+1).append(": A").append(i+1).append(", B").append(cromossomo[i]+1).append("\n");
      sb.append("Aptidao: ").append(cromossomo[cromossomo.length-1]);
          
      return sb.toString();
  }

  @Override
  public String toString() {
      // TODO Auto-generated method stub
      return super.toString();
  }
}
