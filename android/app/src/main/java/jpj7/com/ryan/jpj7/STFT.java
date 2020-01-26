package jpj7.com.ryan.jpj7;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;

import static java.lang.Math.floor;

public class STFT {
	Helper help=new Helper();
	double fs;
	double overlap=0.5;
	double windowLength=0.05;
	Complex[] signal;
	double[][] result;
	public STFT(Complex[] signal, Double fs, Double overlap,Double windowLength) {
		this.fs=fs;
		this.overlap=overlap;
		this.windowLength=windowLength;
		this.signal=signal;
		if(this.fs>22050){

			this.signal=this.resample(this.signal,this.fs);
			this.fs=22050;
		}

	}

	private Complex[] resample(Complex[] signal, double fs) {
		int ratio=(int)fs/22050;
		Log.d("MyApp", "resample: Resampling signal down from "+ fs+ "Hz to "+22050+"Hz with ratio of "+ratio);
		Complex[] newSig=new Complex[signal.length/ratio];
		for(int i=0; i<newSig.length; i++){
			Complex average=new Complex(0,0);
			for(int j=0; j<ratio; j++){
				average.add(signal[i*ratio+j]);
			}
			average=average.mult(new Complex(1/ratio,0));
			newSig[i]=average;
		}
		signal=newSig;
		return signal;
	}

	public STFT(Complex[] signal, Double fs) {
		this.fs=fs;
		this.signal=signal;
		if(this.fs>22050){

			this.signal=this.resample(this.signal,this.fs);
			this.fs=22050;
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public double[][] stft_short(Complex[] buffer) {

		System.out.println("Buffer Length: "+buffer.length);
		int fft_size=1024;
		int hop_size= 256;
		int total_segments=(int) Math.ceil(((float)buffer.length)/((float)hop_size));
		float tmax=((float)buffer.length)/((float)this.fs/2);
		result=new double[total_segments][fft_size/2];

		//Pad proc array to Double size
		Complex[] proc=new Complex[buffer.length+fft_size];
		System.out.println("Proc Length:"+proc.length);
		Arrays.fill(proc,new Complex(0,0));

		//PLEASE DON'T ARBITRARILY SCALE??
		//16 bit audio
		for(int i=0; i<buffer.length; i++) {
			proc[i]=buffer[i].mult(new Complex(32768,0));
//			if(27*hop_size>i && i>25*hop_size)System.out.print(proc[i]);
		}
		print("Total Samples:"+buffer.length);
		print("Total Segments:"+total_segments);
		print("FFT Size:"+fft_size);
		//Begin STFT

		for(int i=0; i<total_segments; i++) {
			Log.d("MyApp","Progress:"+((i+1)*100.0/total_segments)+"%");

			int current_hop=hop_size*i;

			Complex[] segment=new Complex[(int) (fft_size)];
			Arrays.fill(segment, (new Complex(0,0)));
			for(int j=0; j<segment.length;j++)
			{
				segment[j]=proc[current_hop+j];
			}

			Double[] windowed=hanning(segment,i);


			int inner_pad_size=windowed.length;
			if (Math.log(fft_size) % 2 != 0) {
				inner_pad_size=(int) Math.pow(2,floor(Math.log(windowed.length+fft_size)/Math.log(2)+1e-10)+1);
			}
			Complex[] padded=new Complex[inner_pad_size];


			Arrays.fill(padded, (new Complex(0,0)));
			for(int j=0; j<windowed.length;j++) {
				padded[j]= new Complex(windowed[j],0.0);

			}


			Complex[] spectrum= FFT2.fft(padded);



			for(int j=0; j<spectrum.length;j++)
				spectrum[j]=spectrum[j].scale(1.0/fft_size);




			double[] autopower=getAutopower(spectrum);



			result[i]=Arrays.copyOfRange(autopower, 0, (int) fft_size/2);

		}


//		System.out.println(result[0].length);
		for(int i=0; i<result.length;i++) {
			for(int j=0; j<result[i].length;j++) {
				result[i][j]=20*Math.log10(result[i][j]);
				if(result[i][j]<-40.0)
					result[i][j]=-40.0;
				if(result[i][j]>200.0)
					result[i][j]=200.0;
			}
		}
//		System.out.println(result.length);
//		for(int j=0; j<result.length;j++) {
//				System.out.println(result[0][j]+" ");
//			}
//			System.out.println();


		Log.d("MyApp","Transform complete");

		return result;

	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public double[][] stft(Complex[] buffer) {
		
		System.out.println("Buffer Length: "+buffer.length);
		int samples=(int) Math.pow(2, Math.log(this.fs)/Math.log(2.0)+1e-10);
		int fft_size=samples;
		int hop_size= (int) Math.floor(fft_size*this.windowLength*(1-this.overlap));
		int total_segments=(int) Math.ceil(((float)buffer.length)/((float)hop_size));
		float tmax=((float)buffer.length)/((float)this.fs/2);
		result=new double[total_segments][fft_size/2];
		
		//Pad proc array to Double size
		Complex[] proc=new Complex[buffer.length+fft_size];
		System.out.println("Proc Length:"+proc.length);
		Arrays.fill(proc,new Complex(0,0));
		
		//PLEASE DON'T ARBITRARILY SCALE??
		for(int i=0; i<buffer.length; i++) {
			proc[i]=buffer[i].mult(new Complex(32767.9438,0));
//			if(27*hop_size>i && i>25*hop_size)System.out.print(proc[i]);
		}
		print("Total Samples:"+buffer.length);
		print("Total Segments:"+total_segments);
		print("FFT Size:"+fft_size);
		//Begin STFT
		
		for(int i=0; i<total_segments; i++) {
			Log.d("MyApp","Progress:"+((i+1)*100.0/total_segments)+"%");

			int current_hop=hop_size*i;
			
			Complex[] segment=new Complex[(int) (fft_size*windowLength+1)];
			Arrays.fill(segment, (new Complex(0,0)));
//			print(i);
			for(int j=0; j<segment.length;j++)
			{
//				print(j);
				segment[j]=proc[current_hop+j];
			}
//			for(int j=0; j<segment.length; j++)
//				if(i==(int)(total_segments-1))
//					System.out.println(segment[j]+" ");
				
//			System.out.println();
			
			Double[] windowed=hanning(segment,i);
//			if(i==1) {
//				for(int j=0; j<segment.length;j++) {
//					System.out.println(windowed[j]+" ");
//				}
//				System.out.println();
//			}
			
			int inner_pad_size=windowed.length;
			if (Math.log(fft_size) % 2 != 0) {
	        	inner_pad_size=(int) Math.pow(2,floor(Math.log(windowed.length+fft_size)/Math.log(2)+1e-10)+1);
			}
	        Complex[] padded=new Complex[inner_pad_size];

			
			Arrays.fill(padded, (new Complex(0,0)));
			for(int j=0; j<windowed.length;j++) {
				padded[j]= new Complex(windowed[j],0.0);
				
			}
			
			
//			if(i==1) {
//				help.arrayToFile(padded, "resources/jav_padded.txt");
//				System.out.println("Padded Size:"+padded.length);
//				System.out.println("Window Length:"+windowed.length);
//				System.out.println("Inner Pad Size:"+(inner_pad_size-windowed.length));
//				padded=help.cArrayFromFile("resources/py_padded.txt");
//
////				System.out.println();
//			}
//			if(i==1) {
//				System.out.println("Windowed Size:"+windowed.length);
//				for(int j=0; j<windowed.length;j++) {
//					System.out.println(padded[j]+" ");
//				}
//				System.out.println();
//			}
			
//			System.out.println();
//			for(int j=0; j<padded.length; j++)
//				if(i==26)
//					System.out.println(padded[j]+" ");
//				
//			if(i==0) {
//				for(int j=0; j<10;j++) {
//					System.out.println(padded[j]+" ");
//				}
//				System.out.println();
//				System.out.println(padded.length);
//			}
//			print(padded.length);
//			if(i==1) {
//				System.out.println("Inner Pad Size:"+padded.length);
//				for(int j=0; j<windowed.length;j++) {
//					System.out.println(padded[j]+" ");
//				}
//				System.out.println();
//			}
			
			Complex[] spectrum= FFT2.fft(padded);
			
			
//			System.out.println();
//			for(int j=0; j<spectrum.length;j++) {
//				System.out.print(spectrum[j]+" ");
//			}
//			if(i==1) {
//				ArrayList<Complex> s=help.arrayFromFile(spectrum, "resources/py_fft.txt");
//				for(int j=0; j<spectrum.length; j++) {
//					spectrum[j]=s.get(j);
//				}
//			}
			for(int j=0; j<spectrum.length;j++)
				spectrum[j]=spectrum[j].scale(1.0/fft_size);
			
			
//			if(i==0) {
//				for(int j=0; j<10;j++) {
//					System.out.println(spectrum[j]+" ");
//				}
//				System.out.println();
//				System.out.println(spectrum.length);
//			}
			
			
			double[] autopower=getAutopower(spectrum);
			
			

			result[i]=Arrays.copyOfRange(autopower, 0, (int) fft_size/2);
			
//			if(i==3) {
//				for(int j=0; j<result.length;j++) {
//					System.out.println(result[i][j]+" ");
//				}
//				System.out.println();
//			}
		}
		
//		for(int j=0; j<result[0].length; j++) {
//			System.out.print(result[0][j]+" ");
//		}
//		System.out.println();
//		result=transpose(result);
//		for(int j=0; j<result[0].length; j++) {
//			System.out.print(result[0][j]+" ");
//		}
//		System.out.println();
//		System.out.println(result[0].length);
		for(int i=0; i<result.length;i++) {
			for(int j=0; j<result[i].length;j++) {
				result[i][j]=20*Math.log10(result[i][j]);
				if(result[i][j]<-40.0) 
					result[i][j]=-40.0;
				if(result[i][j]>200.0) 
					result[i][j]=200.0;
			}
		}
//		System.out.println(result.length);
//		for(int j=0; j<result.length;j++) {
//				System.out.println(result[0][j]+" ");
//			}
//			System.out.println();


		Log.d("MyApp","Transform complete");

		return result;
		
	}
	
	public Double[] hanning(Complex[] signal, int t) {
		
		Double[] result=new Double[signal.length];
		
//		print(size);
//		System.out.println(signal.length);
		for(int i=0; i<(signal.length) ;i++) {
//			if(t==1)
//				System.out.println((0.5 * (1.0 - Math.cos(2.0 * Math.PI * i / (signal.length-1))))*signal[i].re);
			result[i] =(0.5 * (1.0 - Math.cos(2.0 * Math.PI * i / (signal.length-1))))*signal[i].re;
//			System.out.println(result[i]);
			
		}
		return result;
	}
	
	public Complex[] multArray(Complex[] a, Complex[] b) {
    	if(a.length!=b.length) {
    		System.out.println("ERRROR, ARRAYS MUST BE SAME SIZE");
    		return null;
    	}
    	else {
        	Complex[] result=new Complex[a.length];
        	for(int i=0; i<result.length; i++) {
        		result[i]=a[i].mult(b[i]);
        	}
        	return result;
    	}
    }
	
	public double[] getAutopower(Complex[] a) {
		Complex[] b=conjArray(a);
 		if(a.length!=b.length) {
    		System.out.println("ERRROR, ARRAYS MUST BE SAME SIZE");
    		return null;
    	}
    	else {
        	double[] result=new double[a.length];
        	for(int i=0; i<result.length; i++) {
        		result[i]=a[i].mult(b[i]).re;
        	}
        	return result;
    	}
	}
	public Complex[] conjArray(Complex[] array) {
    	Complex[] result=new Complex[array.length];
    	for(int i=0; i<array.length; i++) {
    		result[i]=array[i].conj();
    	}
    	return result;
    		
    }
	
	private Double[][] transpose(Double[][] array) {
		Double[][] t=new Double[array[0].length][array.length];
		
		for(int i=0; i<array.length; i++ )	{
			for(int j=0; j<array[0].length;j++) {
				t[j][i]=array[i][j];
			}
		}
		return t;
	}
	
	public void print(Object x) {
		System.out.println(x.toString());
	}
}
