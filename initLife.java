import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class initLife {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter bf=new BufferedWriter(new FileWriter("log.txt"));
		BufferedWriter bf1=new BufferedWriter(new FileWriter("stats"));
		BufferedWriter bf2=new BufferedWriter(new FileWriter("treeLog.txt"));
		bf.write(0+"\n");
		for(int i=0;i<26;i++){
			char DNA=(char) ('a'+i);
			double iS=Math.random()*0.4+0.2;
			double cS=iS;
			double bP=Math.random()*0.4+0.2;
			int eggs=(int)(Math.random()*5)+1;
			int sG=0;
			int cG=0;
			bf1.write(DNA+" "+iS+" "+cS+" "+bP+" "+eggs+" "+sG+" "+cG+"\n");
			bf2.write(DNA + " 0 5\n");
			for(int j=0;j<5;j++)
			bf.write(DNA+" "+iS+" "+cS+" "+bP+" "+eggs+" "+sG+" "+cG+"\n");
			}
		bf.close();
		bf1.close();
		bf2.close();
	}

}
