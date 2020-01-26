package jpj7.com.ryan.jpj7;

public class CombState {
	
	int NH=10;
	Double[][] state=new Double[NH][2];
	double DF=1;
	double DA=0.1;
	Double SCORE=-1.0;
	double SIG_A=100;
	double SIG_f=0.01;
	double EPSILON=0.1;
	

	
	public CombState(int nh) {
		this.NH=nh;
		state=new Double[this.NH][2];
		
	}
	
	public Double[] harmonicAt(int i) {
		
		if(i<0 || i>9) {
			System.out.println("out of range!");
		}
		return state[i];
	}
	
	public Double frequencyAt(int i) {
		
		if(i<0 || i>this.NH) {
			System.out.println("out of range!");
		}
		return state[i][0];
	}
	
	public double amplitudeAt(int i) {
		
		if(i<0 || i>this.NH) {
			System.out.println("out of range!");
		}
		return state[i][1];
	}
	
	public Double[][] getState(){
		return this.state;
	}
	
	public boolean equals(CombState c) {
		for(int i=0; i<this.NH; i++) {
			if(state[i][0]<c.getState()[i][0]*Math.pow(2, -(1/12)) || state[i][0]>c.getState()[i][0]*Math.pow(2, (1/12))) {
				return false;
			}
		}
		return true;
	}

	public void print() {
		for(int i=0; i<this.NH; i++) {
			System.out.println(i+": "+state[i][0]+"Hz\t"+20*Math.log10(state[i][1])+" dB");
		}
		
	}
	
	public double sumAmplitudes() {
		double s=0;
		for(int i=0; i<this.NH; i++) {
			s+=state[i][1];
		}
		return s;
	}

	public boolean contains(Double f) {
		for(int i=0; i<this.NH; i++) {
			if(state[i][0].equals(f))
				return true;
		}
		return false;
	}



	public CombState predict(double[][] delta) {
		CombState prediction=new CombState(this.NH);
		for(int i=0; i<this.NH; i++) {
			if(this.amplitudeAt(i)+DF*delta[i][1]<0.01) {
				prediction.state[i][0]=this.frequencyAt(i)+DF*delta[i][0];
				prediction.state[i][1]=0.01;
			}
			else {
				prediction.state[i][0]=this.frequencyAt(i)+DF*delta[i][0];
				prediction.state[i][1]=this.amplitudeAt(i)+DA*delta[i][1];
			}
		}
		return prediction;
	}
	
	public Double[] getPartial(int i) {
		return state[i];
	}

	public double maxAmplitude() {
		double amplitude=-1000;
        for(int i=0; i<this.NH; i++) {
            if(this.amplitudeAt(i)>amplitude) {
                amplitude=this.amplitudeAt(i);
            }
        }
        return amplitude;
	}
	
	public Double getScore() {
		return this.SCORE;
	}

	public void addScore(Double sn) {
		this.SCORE=sn;		
	}

	public double sumL1(CombState predicted_comb) {
		double sum=0;
		for(int i=0; i<this.NH; i++) {
//			System.out.println("L("+this.frequencyAt(i)+" Hz "+this.amplitudeAt(i)+" Amps "+this.frequencyAt(i)+"Hz "+this.SIG_f+" sig)");
			Double l1=this.L1(predicted_comb.frequencyAt(i),predicted_comb.amplitudeAt(i),predicted_comb.frequencyAt(i));
//			System.out.println(predicted_comb.frequencyAt(i)+" Hz "+predicted_comb.amplitudeAt(i)+" Amps   Score="+l1);
			sum+=l1;
		}
//		System.out.println("SUM"+sum+"\n");
		return sum;
	}

	public double L1(Double f, Double a, Double fp) {
		return a*Math.exp(-(1/this.SIG_f)*Math.abs(Math.log(f/fp)));
	}
		
	public double L2(Double f, Double a, Double fp, Double ap ) {
		return a*Math.exp(-(1/this.SIG_f)*Math.abs(Math.log(f/fp))-(1/this.SIG_A)*Math.abs(Math.log(a/ap)));
	}

	public int index(Double[] h) {
		int i;
		for(i=0; i<this.NH; i++) {
			if(this.frequencyAt(i).equals(h[0])) {
				return i;
			}
		}
		System.out.println("Leader Harmonic not in predicted comb!\nReturning -1...");
		return -1;
	}

	public void printRaw() {
		for(int i=0; i<this.NH; i++) {
			System.out.println(i+": "+state[i][0]+"Hz\t"+(state[i][1])+" Amps");
		}
		
	}

}
