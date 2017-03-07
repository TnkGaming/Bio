	public class Life {
		String DNA;
		double initSurvivability;
		double currSurvivability;
		double breedingPercentage;		
		int eggs;
		int startingGen;
		int currGen;
		
		public Life(String dna, double iS, double cS, double bP, int e, int sG, int cG){
			DNA=dna;
			initSurvivability=iS;
			currSurvivability=cS;
			breedingPercentage=bP;
			eggs=e;
			startingGen=sG;
			currGen=cG;
		}

	}