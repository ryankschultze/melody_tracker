package jpj7.com.ryan.jpj7;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Tracker {

	Double SPECTRUM[][];
	int NC=3;
	int NH=10;
	ArrayList<Comb> COMBS=new ArrayList<Comb>();
	ArrayList<Double[]> SORTED_PARTIAL_SPACE=new ArrayList<Double[]>();
	ArrayList<Double[]> LEADER_HARMONICS=new ArrayList<Double[]>();
	double SIG_A=100;
	double SIG_f=0.01;
	double EPSILON=0.1;
	int FRAMES;
	double TIME;
	Converter converter=new Converter(440);
	int BAND_L=0;
	int BAND_H=1100;
	public Tracker(float[][] data) {
		this.SPECTRUM=new Double[data.length][data[0].length];
		for (int i=0; i<data.length; i++){
			for(int j=0; j<data[i].length; j++){
				this.SPECTRUM[i][j]= Double.valueOf(data[i][j]);
			}
		}
		this.FRAMES=data.length;
		System.out.println("Frames: "+FRAMES);
	
	}
	public Tracker(float[][] data, int band) {
		this.SPECTRUM=new Double[data.length][data[0].length];
		for (int i=0; i<data.length; i++){
			for(int j=0; j<data[i].length; j++){
				this.SPECTRUM[i][j]= Double.valueOf(data[i][j]);
			}
		}
		this.FRAMES=data.length;
		System.out.println("Frames: "+FRAMES);
		switch(band){
			case 0:
				System.out.println("No band-limit");
				BAND_L=85;
				BAND_H=1100;
				break;
			case 1:
				System.out.println("Tracking for Bass");
				BAND_L=85;
				BAND_H=261;
				break;
			case 2:
				System.out.println("Tracking for Tenor");
				BAND_L=131;
				BAND_H=391;
				break;
			case 3:
				System.out.println("Tracking for Alto");
				BAND_L=196;
				BAND_H=587;
				break;
			case 4:
				System.out.println("Tracking for Soprano");
				BAND_L=262;
				BAND_H=698;
				break;
		}


	}
	
	public Tracker(Double[][] data,double time) {
		this.SPECTRUM=data;
		this.FRAMES=data.length;
		this.TIME=time;
	}
	
	public Tracker(Double[][] data, int nc) {
		this.SPECTRUM=data;
		this.FRAMES=data.length;
		this.NC=nc;
	}
	
	//Private helper functions
	private int maxIndex(Double[] array) {
		Double max=array[0];
		int index=0;
		for(int i=0; i<array.length; i++) {
				if(array[i]>max) {
					max=array[i];
					index=i;					
				}
		}
		return index;
	}
	
	
	public void output(ArrayList<Double> f_contour, String filename) {
		filename+=".txt";
		File file=new File(filename);
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(file));
			for(int i=0; i<this.FRAMES; i++) {
				String line=Math.round(i/this.FRAMES*(this.TIME))+"\t"+f_contour.get(i)+"\n";
				bw.write(line);
			}
			bw.close();
			
		} catch (IOException e) {
			System.out.println("File Not Found!");
			e.printStackTrace();
		}
		
	}
	
	public Double[] kRange(double freq) {
		int min=(int)Math.round(freq*Math.pow(2.0, (-2.0/12.0)));
		int max=(int)Math.round(freq*Math.pow(2.0, (2.0/12.0)));
		if(max>=this.SPECTRUM[0].length/2)
		    max=this.SPECTRUM[0].length-1;
		if(min<=85) {
			min = 85;
		}
		if(max-min+1<=0){
			return new Double[]{85.0};
		}
		Double[] k_range=new Double[max-min+1];
		for(int i=0;i<=max-min; i++) {

			k_range[i]=(double) (min+i);
		}
		return k_range;	
	}
	
	public double L1(Double f, Double a, Double fp) {
		return a*Math.exp(-(1/this.SIG_f)*Math.abs(Math.log(f/fp)));
	}
		
	public double L2(Double f, Double a, Double fp, Double ap ) {
		return a*Math.exp(-(1/this.SIG_f)*Math.abs(Math.log(f/fp))-(1/this.SIG_A)*Math.abs(Math.log(a/ap)));
	}
	
	public int getWeakestComState() {
		Double[] amplitudes=new Double[this.COMBS.size()];
		
		for(int i=0; i<this.COMBS.size();i++) {
			amplitudes[i]=this.COMBS.get(i).maxAmplitude();
		}
		return maxIndex(amplitudes);		
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void extractPartialSpace(int t) {
		Double[] frequencies=this.SPECTRUM[t];
		Double[] v_range=Arrays.copyOfRange(frequencies, 85, 1100);
		ArrayList<Integer> partial_space=getExtrema(v_range);
		ArrayList<Double> amps=new ArrayList<Double>();
		
		for(int i=0; i<partial_space.size(); i++) {
			amps.add(v_range[partial_space.get(i)]);
		}
		ArrayList<Double[]> space=new ArrayList<Double[]>();
		for(int i=0; i<partial_space.size(); i++) {
			Double[]p= {Double.parseDouble((partial_space.get(i)+85)+""),Math.pow(10, amps.get(i)/20)};
			space.add(p);			
		}
		int j;
		boolean fl=true;
		Double[] temp;
		while(fl) {
			fl=false;
			for(j=0; j<space.size()-1; j++) {
				if(space.get(j)[1]<space.get(j+1)[1]) {
					temp=space.get(j);
					space.set(j, space.get(j+1));
					space.set(j+1, temp);
					fl=true;
				}
			}
		}
		this.SORTED_PARTIAL_SPACE=space;
		
	}

	private ArrayList<Integer> getExtrema(Double[] v_range) {
		ArrayList<Integer> extrema=new ArrayList<Integer>();
//		System.out.println("V range length: "+v_range.length);
		for(int i=1; i<v_range.length-2; i++) {

			if(v_range[i]!=null) {
				if(v_range[i]>v_range[i-1]&&v_range[i]>v_range[i+1]) {

					extrema.add(i);
				}
			}
			
		}
		return extrema;
	}
	
	CombState createL1Comb(Double[] partial, int t) {
		CombState l1_comb=new CombState(this.NH);
		
		Double[][] state=new Double[this.NH][2];
		state[0]=partial;
		
		Double f0=partial[0];
		int h=2;
		
		while(h<=this.NH){
			if(h*f0>5000) {
				state[h-1][0]=h*f0;
				state[h-1][1]=0.01;
			}
			else {
				state[h-1][0]=h*f0;
				state[h-1][1]=Math.pow(10, this.SPECTRUM[t][(int) Math.round(h*f0)]/20);
			}
			h++;
		}
		l1_comb.state=state;
		return l1_comb;
	}
	
	CombState createL2Comb(Double[] partial, int t,Double delta_a, CombState pcomb) {
		CombState l1_comb=new CombState(this.NH);
		
		Double[][] state=new Double[this.NH][2];
		state[0]=partial;
		Double f0=partial[0];
		int h=2;
		
		while(h<=this.NH){
			Double[] k_range=this.kRange(h*f0);
			Double[] dynamic_likelihood=new Double[k_range.length];
			
			for(int i=0; i<k_range.length; i++) {
				dynamic_likelihood[i]=this.L2(k_range[i], Math.pow(10, this.SPECTRUM[t][(int) Math.round(k_range[i])]/20), h*f0, delta_a*pcomb.amplitudeAt(h-1));
			}
			
			int k_star=maxIndex(dynamic_likelihood);
			
			state[h-1][0]=(double) Math.round(k_star);
			state[h-1][1]=Math.pow(10, this.SPECTRUM[t][k_star]/20);
			h++;
		}
		l1_comb.state=state;
		return l1_comb;
	}
	
	public ArrayList<Double> track() {
		ArrayList<Double> contour=new ArrayList<Double>();
		for(int t=0; t<this.FRAMES; t++) {

			Log.d("MyApp","Progress:"+((t+1)*100.0/this.FRAMES)+"%");
//			System.out.println("Progress:"+((t+1)*100.0/this.FRAMES)+"%");
			
			this.extractPartialSpace(t);
			
//			System.out.println("\nExtracted Partial Space:");
//			System.out.println("["+this.SORTED_PARTIAL_SPACE.get(0)[0]+"Hz "+this.SORTED_PARTIAL_SPACE.get(0)[1]+"dB]");
//			System.out.println("["+this.SORTED_PARTIAL_SPACE.get(1)[0]+"Hz "+this.SORTED_PARTIAL_SPACE.get(1)[1]+"dB]");
//			System.out.println("["+this.SORTED_PARTIAL_SPACE.get(2)[0]+"Hz "+this.SORTED_PARTIAL_SPACE.get(2)[1]+"dB]\n");
			
//			System.out.println("\nBefore prediction update:");
//			this.printCurrentCombs();
			predictionUpdate(t);
			
//			System.out.println("\nAfter prediction update/Before Comb Initialization:");
//			this.printCurrentCombs();
			initializeCombs(t);
			
//			System.out.println("\nAfter Comb Initialization/Before Comb Termination:");
//			this.printCurrentCombs();
			terminateCombs();
			
//			System.out.println("\nAfter Comb Termination:");
			
//			this.printCurrentCombs();
			
			//Vocal Selection
			double f0=vocalSelection();
			if(f0!=0){
				if (f0<BAND_L){
					f0=f0*2;
				}
				else if(f0>BAND_H){
					f0=f0/2;
				}
			}
			contour.add(f0);
		}
		
		return contour;
	}

	private void printCurrentCombs() {
		if(this.COMBS.size()==0) {
//			System.out.println("No active combs...");
		}
		for(int i=0; i<this.COMBS.size(); i++) {
//			System.out.println("C"+i+":");
			this.COMBS.get(i).printCurrentState();
			System.out.println();
		}
		
	}

	private Double vocalSelection() {
		ArrayList<Double> scores=new ArrayList<Double>();
		ArrayList<Double> previous_scores=new ArrayList<Double>();
		for(int c=0; c<this.COMBS.size(); c++) {
			this.COMBS.get(c).score();
			scores.add(this.COMBS.get(c).getScore());
			previous_scores.add(this.COMBS.get(c).getPreviousScore());
		}
		
//		System.out.println("sn:"+scores.toString());
//		System.out.println("sn:"+previous_scores.toString());
		Double[] final_scores=new Double[this.COMBS.size()];
//		System.out.println("Final Scores:");
		for(int c=0; c<this.COMBS.size(); c++) {
			double s_score=this.smoothing(scores.get(c),previous_scores.get(c));
			double j_score=s_score*this.COMBS.get(c).getStability();
			this.COMBS.get(c).updateScore(j_score);
			final_scores[c]=j_score;
//			System.out.print("["+final_scores[c]+"]");
		}
//		System.out.println();
		
		Double f_winner=0.0;
		if(final_scores.length==0) {
			return f_winner;
		}
		else {
			int c_winner=this.maxIndex(final_scores);
			f_winner=this.COMBS.get(c_winner).estimateF0();
			return f_winner;
		}
	}

	private double smoothing(Double score, Double p_score) {
		return score+0.7*p_score;		
	}



	private void terminateCombs() {
		for(int c=0; c<this.COMBS.size(); c++) {
			Comb comb=this.COMBS.get(c);
			
			if(this.SORTED_PARTIAL_SPACE.size()==0) {
//				System.out.println("No partials available");
				comb.terminate();
			}
			
			else if(comb.sum_amplitudes()<this.EPSILON*this.SORTED_PARTIAL_SPACE.get(0)[1]) {
//				System.out.println("Below harmonic threshold! Terminating comb...");
				comb.terminate();
			}
			else if(20*Math.log10(comb.getCurrentCombState().amplitudeAt(0))<0) {
//				System.out.println("FAKE MATH... Terminating");
				comb.terminate();
			}
			else if(100.0>comb.estimateF0()||comb.estimateF0()>1100) {
//				System.out.println("Out of range... Terminating");
				comb.terminate();
			}
		}
		for(int c=0; c<this.COMBS.size();c++) {
			Comb comb=this.COMBS.get(c);
			if(!comb.isActive())
				this.COMBS.remove(comb);
		}
		
		//Redundancy
		for(int c=0; c<this.COMBS.size(); c++) {
			Comb comb_c=this.COMBS.get(c);
			if(this.SORTED_PARTIAL_SPACE.size()==0) {
//				System.out.println("No partials...");
				comb_c.terminate();
			}
			else {
				
				for(int i=0; i<this.COMBS.size(); i++) {
					if(i!=c) {
						Comb comb_i=this.COMBS.get(i);
						
						if(comb_c.equals(comb_i)) {
//							System.out.println("Redundancy detected! @"+c+"|"+i);
							if(comb_c.length()<comb_i.length()) {
//								System.out.println("Terminating comb..."+c);
								comb_c.terminate();
								this.COMBS.remove(comb_c);
							}
							else if(comb_c.length()==comb_i.length()) {
								
								if(comb_c.getScore()>comb_i.getScore()) {
									comb_i.terminate();
									this.COMBS.remove(comb_i);
//									System.out.println("Terminating comb..."+i);
								}
								else {
									comb_c.terminate();
									this.COMBS.remove(comb_c);
//									System.out.println("Terminating comb..."+c);
								}
							}
							else {
								comb_i.terminate();
								this.COMBS.remove(comb_i);
//								System.out.println("Terminating comb..."+i);
							}
						}
					}
				}					
			}
		}
		for(int c=0; c<this.COMBS.size();c++) {
			Comb comb=this.COMBS.get(c);
			if(!comb.isActive())
				this.COMBS.remove(comb);
		}
		
//		System.out.println("Number of Combs Active: "+this.COMBS.size());
		
	}

	private void initializeCombs(int t) {
		ArrayList<Double[]> peak_partials= new ArrayList<Double[]>();
		if(this.SORTED_PARTIAL_SPACE.isEmpty()) {
//			System.out.println("No partials to track...");
		}
		else if(this.SORTED_PARTIAL_SPACE.size()<3) {
			for(int i=0; i<this.SORTED_PARTIAL_SPACE.size(); i++) {
				peak_partials.add(this.SORTED_PARTIAL_SPACE.get(i));
			}
		}
		else {
			for(int i=0; i<3; i++) {
				peak_partials.add(this.SORTED_PARTIAL_SPACE.get(i));
			}
		}
		
		
		for(int i=0; i<peak_partials.size();i++) {

			Double[] partial=peak_partials.get(i);
			
			if(this.COMBS.size()==0) {
				this.COMBS.add(new Comb((this.createL1Comb(partial, t)),this.NH));
			}
			else if(this.COMBS.size()<this.NC) {
				if(!this.isTracking(partial[0])) {
					this.COMBS.add(new Comb((this.createL1Comb(partial, t)),this.NH));
				}
				else {
//					System.out.println(partial[0]+"Hz is already being tracked...");
				}
			}
		}
	}

	private boolean isTracking(Double f) {
		for(int i=0; i<this.COMBS.size(); i++) {
			if(this.COMBS.get(i).isTracking(f)) {
				return true;
			}
		}
		return false;
	}

	private void predictionUpdate(int t) {
		for(int c=0; c<this.COMBS.size(); c++) {
			
			if(c>=this.COMBS.size())
				break;
			
			if(this.COMBS.get(c).isActive()) {
				CombState predicted_comb=this.COMBS.get(c).predictionUpdate();
				
				this.LEADER_HARMONICS=this.COMBS.get(c).getLeaderHarmonics();
				if(this.LEADER_HARMONICS.isEmpty()) {
//					System.out.println("No Leader Harmonics");
					this.LEADER_HARMONICS.add(predicted_comb.getPartial(0));
				}
				
				ArrayList<CombState> potential_states=new ArrayList<CombState>();
				
				for(int i=0; i< this.LEADER_HARMONICS.size(); i++) {
					Double[] h=this.LEADER_HARMONICS.get(i);
					
					Double[] k_range=this.kRange(h[0]);

					Double[] dynamic_likelihood=new Double[k_range.length];
					
					for(int k=0; k<k_range.length; k++) {
						int fk=(int) Math.round(k_range[k]);
//                        Log.d("MyApp", "predictionUpdate: fk value="+fk);
						Double da_star=Math.pow(10, this.SPECTRUM[t][fk])/predicted_comb.amplitudeAt(i);

						dynamic_likelihood[k]=this.L2((double)fk, Math.pow(10, this.SPECTRUM[t][fk]), (i+1)*h[0], da_star*h[1]);
						
					}
					
					
					Double k_star=k_range[this.maxIndex(dynamic_likelihood)];
//					System.out.println("K_star:"+k_star);
					Double[] h_star=new Double[2];
					h_star[0]=k_star;
					h_star[1]=Math.pow(10, this.SPECTRUM[t][(int)Math.round(k_star)]);
					
					Double f0h_star=(double) Math.round(h_star[0]/(predicted_comb.index(h)+1));
					Double[] potential_partial=new Double[2];
					potential_partial[0]=f0h_star;
					potential_partial[1]=Math.pow(10, this.SPECTRUM[t][(int)Math.round(f0h_star)]/20);
					CombState potential_state=this.createL1Comb(potential_partial, t);
//					potential_state.print();
					potential_states.add(potential_state);
					
					}
				Double[] potential_L1_scores=new Double[potential_states.size()];
//				System.out.println("Potential Combs and their L1 scores:");
				for(int i=0; i<potential_L1_scores.length; i++) {
//					System.out.println("PC"+i);
					potential_L1_scores[i]=this.COMBS.get(c).sumL1(potential_states.get(i));
					}
				
				if(potential_L1_scores.length==0) {
					this.COMBS.get(c).terminate();
					this.COMBS.remove(this.COMBS.get(c));
				}
				else {
					int winner_index=this.maxIndex(potential_L1_scores);
					CombState winner_comb=potential_states.get(winner_index);
//					System.out.println("Winner Comb:");
//					winner_comb.printRaw();
					this.COMBS.get(c).update(winner_comb);
				}
			}
			
		}
	}

	public void writeContourToFile(ArrayList<Double> contour) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(new File("/Contour.txt")));
			for(int i=0; i<contour.size(); i++) {
				bw.write(contour.get(i)+"\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
