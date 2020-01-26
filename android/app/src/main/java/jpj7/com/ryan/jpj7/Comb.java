package jpj7.com.ryan.jpj7;

import java.util.ArrayList;

public class Comb {
	
	ArrayList<CombState> comb=new ArrayList<CombState>();
	boolean active=false;
	double sig_j=Math.sqrt(2);
	double df=1;
	double lmda=0.5;
	int NH=10;
	
	public Comb(CombState cs, int nh) {
		this.NH=nh;
		comb.add(cs);
		active=true;
	}
	
	
	public void terminate() {
		comb=new ArrayList<CombState>();
		active=false;
	}
	
	public CombState getComb(int i) {
		return comb.get(comb.size()-1-i);
	}
	
	public double[] getContour(int i) {
		double[] contour=new double[i];
		
		for(int j=0; j<i; j++) {
			if(j>comb.size()) {
				contour[j]=0;
			}
			else {
				contour[j]=comb.get(j).frequencyAt(0);
			}
		}
	
	return contour;	
	}
	
	public double[] getContour() {
		double[] contour=new double[20];
		
		for(int j=0; j<20; j++) {
			if(j>comb.size()) {
				contour[j]=0;
			}
			else {
				contour[j]=comb.get(j).frequencyAt(0);
			}
		}
	
	return contour;	
	}
	
	public void addScore(Double sn) {
		getCurrentCombState().addScore(sn);
	}
	
	public void updateScore(Double sn) {
		getCurrentCombState().addScore(sn);
	
	}
	
	public double getScore() {
		return getCurrentCombState().getScore();
	}
	
	public double getPreviousScore() {
		
		return getPreviousCombState().getScore();
	}
	
	public CombState getCurrentCombState() {
//		System.out.println(comb.size());
		return comb.get(comb.size()-1);
	}
	
	public CombState getPreviousCombState() {
		if(comb.size()==1) {
			return comb.get(comb.size()-1);
		}
		else {
			return comb.get(comb.size()-2);
		}
	}
	
	public int length() {
		return comb.size();
	}
	
	public void update(CombState cs) {
		comb.add(cs);
	}
	
	public boolean equals(Comb other) {
		if(comb.size()<2 || other.length()<2) {
//			System.out.println("Not enough history.");
			return false;
		}
		else {
//			Check for partials within semitone.
			if(getCurrentCombState().equals(other.getCurrentCombState())) {
				if(getPreviousCombState().equals(other.getPreviousCombState())) {
					return true;
				}
			}
			return false;
		}
	}
	
	public void printComb() {
		for(int i=0; i<comb.size(); i++) {
			System.out.print("Comb "+i);
			comb.get(i).print();
		}
	}
	
	public double getStability() {
		double[] contour;
		if(comb.size()<20) {
			contour=getContour(comb.size());
		}
		else {
			contour=getContour();
		}
		
		double sig_c=getStDev();
		
		if(contour.length==0)	
			return 0;
		if(sig_c<sig_j)	
			return (sig_c*sig_c)/(sig_j*sig_j);
		else	
			return 1;
	}

	private double getStDev() {
		double[] contour;
		if(comb.size()<20) {
			contour=getContour(comb.size());
		}
		else {
			contour=getContour();
		}
		
		double avg=0;
		for(int i=0; i<contour.length;i++) {
			avg+=contour[i];
		}
		avg=avg/contour.length;
		
		double std=0;
		for(int i=0; i<contour.length;i++) {
			std+=(contour[i]-avg)*(contour[i]-avg)/contour.length;
		}
		return Math.sqrt(std);
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isTracking(Double f) {
		return getCurrentCombState().contains(f);
	}
	
	public double[][] delta(){
		CombState cstate=getCurrentCombState();
		CombState pstate=getPreviousCombState();
		
		double [][] d=new double[this.NH][2];
		
		for(int i=0; i<this.NH; i++) {
			d[i][0]=cstate.frequencyAt(i)-pstate.frequencyAt(i);
			d[i][1]=cstate.amplitudeAt(i)-pstate.amplitudeAt(i);
		}
		return d;
	}
	
	public CombState predictionUpdate() {
		CombState ccomb=getCurrentCombState();
		CombState prediction=ccomb.predict(delta());
		
		return prediction;
	}
	
	public ArrayList<Double[]> getLeaderHarmonics() {
		ArrayList<Double[]> H=new ArrayList<Double[]>();
		CombState prediction=predictionUpdate();
		
		for(int i=0; i<this.NH; i++) {
			Double[] harmonic=prediction.getPartial(i);
			
			ArrayList<Double> ph1=new ArrayList<Double>();
			for(int t=1; t<5; t++) {
				if(t<comb.size()) {
					ph1.add(getComb(t).amplitudeAt(1));
				}
				else {
					break;
				}
			}
			boolean flag=true;
			
			for(int t=0; t<ph1.size(); t++) {
				
				if(harmonic[1]<lmda*ph1.get(t)) {
					flag=false;
					break;
				}
			}
			
			if(flag==true) {
				H.add(harmonic);
			}
		}
		return H;
	}
	
	public double estimateF0() {
		return getCurrentCombState().frequencyAt(0);
	}
	
	public ArrayList<Double> getScores() {
		ArrayList<Double> scores=new ArrayList<Double>();
		for(int i=0; i<comb.size();i++) {
			scores.add(comb.get(i).getScore());
		}
		return scores;
	}
	
	public double maxAmplitude() {
		return getCurrentCombState().maxAmplitude();
	}
	
	public double sumL1(CombState predicted_comb) {
		return getCurrentCombState().sumL1(predicted_comb);	
	}


	public double sum_amplitudes() {
		return getCurrentCombState().sumAmplitudes();
	}


	public void printCurrentState() {
		
		getCurrentCombState().print();
		
	}


	public void score() {
		getCurrentCombState().addScore(getCurrentCombState().sumL1(getCurrentCombState()));
		
	}



}
