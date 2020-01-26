package jpj7.com.ryan.jpj7;


public class FFT2 {
	
	public static Complex[] fft(Complex[] data) {
		int N=data.length;
//		System.out.println("Size of data: "+data.length);
		if(N<=1) return data;
		Complex[] even=fft(even(data));
		Complex[] odd=fft(odd(data));
		Complex[] t=new Complex[N/2];
		Complex[] result=new Complex[N];
//		System.out.println("N="+N);
//		System.out.println("N/2="+N/2);
//		System.out.println("Size of odd:"+odd.length);
//		System.out.println("Size of even:"+even.length);
//		System.out.println();
		for(int k=0; k<N/2; k++) {
			t[k]=odd[k].mult(new Complex(Math.cos(-2*Math.PI*k/N),Math.sin(-2*Math.PI*k/N)));
		}
		for(int k=0; k<N; k++) {
			if(k<N/2)
				result[k]=(even[k].add(t[k]));
			else
				result[k]=(even[k-N/2].sub(t[k-N/2]));
		}
		return result;
	}

	private static Complex[] odd(Complex[] data) {
		int i=0;
		int length=data.length;
		Complex[] o;
		if(length%2==1){
			o=new Complex[length/2+1];
		}
		else {
			o = new Complex[length / 2];
		}
		int j=0;
		while(i<data.length) {
			o[j]=(data[i]);
			i+=2;
			j+=1;
		}
		return o;
	}

	private static Complex[] even(Complex[] data) {
		int i=1;
		int length=data.length;
		Complex[] e=new Complex[length/2];


		int j=0;
		while(i<data.length) {
			e[j]=(data[i]);
			i+=2;
			j+=1;
		}
		return e;
	}

}
