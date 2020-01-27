package jpj7.com.ryan.jpj7;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;

import static java.lang.Math.floor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.FloatFFT;

public class STFT {
	Helper help=new Helper();
	float fs;
	float overlap=0.5f;
	float windowLength=0.05f;
	Complex[] signal;
	float[][] result;
	FloatFFT transform;

	public STFT(Complex[] signal, Float fs, Float overlap,Float windowLength) {
		this.fs=fs;
		this.overlap=overlap;
		this.windowLength=windowLength;
		this.signal=signal;


	}



	public STFT(Complex[] signal, Float fs) {
		this.fs=fs;
		this.signal=signal;

	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public float[][] stft_short(Complex[] buffer) {

		System.out.println("Buffer Length: "+buffer.length);
		int fft_size=1024;
		int hop_size= 256;
		this.transform=new FloatFFT(1024);
		int total_segments=(int) Math.ceil(((float)buffer.length)/((float)hop_size));
		float tmax=((float)buffer.length)/((float)this.fs/2);
		result=new float[total_segments][fft_size/2];

		//Pad proc array to Float size
		Complex[] proc=new Complex[buffer.length+fft_size];
		System.out.println("Proc Length:"+proc.length);
		Arrays.fill(proc,new Complex(0,0));

		//PLEASE DON'T ARBITRARILY SCALE??
		//16 bit audio
		for(int i=0; i<buffer.length; i++) {
			proc[i]=buffer[i].mult(new Complex(32767,0));
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

			Float[] windowed=hanning(segment,i);


			int inner_pad_size=windowed.length;
			if (Math.log(fft_size) % 2 != 0) {
				inner_pad_size=(int) Math.pow(2,floor(Math.log(windowed.length+fft_size)/Math.log(2)+1e-10)+1);
			}

			//For my FFT
//			Complex[] padded=new Complex[inner_pad_size];
//
//
//			Arrays.fill(padded, (new Complex(0,0)));
//			for(int j=0; j<windowed.length;j++) {
//				padded[j]= new Complex(windowed[j],0);
//
//			}
// 			Complex[] spectrum= FFT2.fft(padded);
//			for(int j=0; j<spectrum.length;j++)
//				spectrum[j]=spectrum[j].scale((float) (1.0/fft_size));
//			float[] autopower=getAutopower(spectrum);

			//For Tarsos FFT
			float[] padded=new float[inner_pad_size];



			for(int j=0; j<windowed.length;j++) {
				padded[j]= windowed[j];

			}

//			transform.forwardTransform(padded);
//			float[] autopower=new float[fft_size];
//			transform.modulus(padded,autopower);









//			result[i]=Arrays.copyOfRange(autopower, 0, (int) fft_size/2);

		}


//		System.out.println(result[0].length);
		for(int i=0; i<result.length;i++) {
			for(int j=0; j<result[i].length;j++) {
				result[i][j]= (float) (20*Math.log10(result[i][j]));
				if(result[i][j]<-40.0f)
					result[i][j]=-40.f;
				if(result[i][j]>200.0f)
					result[i][j]=200.0f;
			}
		}
		System.out.println(result.length);
		System.out.println(result[0].length);
		System.out.println(result[0][220]+" ");
		System.out.println(result[0][221]+" ");
		System.out.println(result[0][530]+" ");
		System.out.println(result[0][531]+" ");
		System.out.println();


		Log.d("MyApp","Transform complete");

		return result;

	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public float[][] stft(Complex[] buffer) {
		
		System.out.println("Buffer Length: "+buffer.length);
		int samples=(int) Math.pow(2, Math.log(this.fs)/Math.log(2.0)+1e-10);
		int fft_size=samples;
		int hop_size= (int) Math.floor(fft_size*this.windowLength*(1-this.overlap));
		int total_segments=(int) Math.ceil(((float)buffer.length)/((float)hop_size));
		float tmax=((float)buffer.length)/((float)this.fs/2);
		result=new float[total_segments][fft_size/2];

		//Pad proc array to Float size
		Complex[] proc=new Complex[buffer.length+fft_size];
		System.out.println("Proc Length:"+proc.length);
		Arrays.fill(proc,new Complex(0,0));
		
		//PLEASE DON'T ARBITRARILY SCALE??
		for(int i=0; i<buffer.length; i++) {
			proc[i]=buffer[i].mult(new Complex(32767.0f,0));
		}
		print("Total Samples:"+buffer.length);
		print("Total Segments:"+total_segments);
		print("FFT Size:"+fft_size);
		//Begin STFT
		
		for(int i=0; i<total_segments; i++) {
			Log.d("MyApp","Progress:"+((i+1)*100.0/total_segments)+"%");

			int current_hop=hop_size*i;
			
			Complex[] segment=new Complex[(int) (fft_size*windowLength+1)];
//			Log.d("SEG","Segment Length:"+segment.length);
			Arrays.fill(segment, (new Complex(0,0)));
//			print(i);
			for(int j=0; j<segment.length;j++)
			{
//				print(j);
				segment[j]=proc[current_hop+j];
                if(i==1){

                    if(j<3||j>=segment.length-4)
                        Log.d("SEG:",""+segment[j]);
                }
			}
//			for(int j=0; j<segment.length; j++)
//				if(i==(int)(total_segments-1))
//					System.out.println(segment[j]+" ");
				
//			System.out.println();
			
			Float[] windowed=hanning(segment,i);
//			if(i==1) {
//				for(int j=0; j<segment.length;j++) {
//					System.out.println(windowed[j]+" ");
//				}
//				System.out.println();
//			}



			int inner_pad_size=windowed.length+fft_size;
			if (i == 1) {
				for(int j=0; j<windowed.length; j++)
				{
					if(j<3||j>=windowed.length-4) {
						Log.d("WIN", ":" + windowed[j].toString());
					}
				}

			}

			//For my FFT
			float[] padded=new float[inner_pad_size];



			for(int j=0; j<windowed.length;j+=2) {
				padded[j]= windowed[j];
				padded[j+1]=0;

			}

			transform=new FloatFFT(inner_pad_size/2);
			transform.complexForward(padded);
			float[] spectrum=padded;
			Complex[] spect=floatToComplex(spectrum);
			if (i == 1) {
				for(int j=0; j<spect.length; j++)
				{

					if(j<3||j>spect.length-4) {
						Log.d("FFT", j+":" + spect[j].toString());
					}
				}

			}
			for(int j=0; j<spect.length;j++) {
				spect[j] = spect[j].scale ((float) 2.0 / fft_size);

			}
			if (i == 1) {
				for(int j=0; j<spect.length; j++)
				{
					if(j<3||j>=spect.length-4) {
						Log.d("sFFT", j+":" + spect[j].toString());

					}
				}

			}

			float[] autopower=getAutopower(spect);
			if (i == 1) {
				for(int j=0; j<autopower.length; j++)
				{
					if(j<3||j>=autopower.length-4) {
						Log.d("AP", j+":" + autopower[j]);
					}
				}

			}

			//For Tarsos FFT
//
//			float[] padded=new float[inner_pad_size];
//			this.transform=new FloatFFT(inner_pad_size);
//			for(int j=0; j<windowed.length;j++) {
//				padded[j]= windowed[j];
//
//			}
//

//			transform.complexForwardTransform(padded);
//			Log.d("Spect length:",""+padded.length);
//			for(int j=0; j<padded.length;j++) {
//				padded[j]= (float) (padded[j]*(1.0/fft_size));
//			}
//			float[] autopower=new float[inner_pad_size];


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


//			for(int j=0; j<spectrum.length; j++)
//            {
//
//            }
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

			
//			if(i==0) {
//				for(int j=0; j<10;j++) {
//					System.out.println(spectrum[j]+" ");
//				}
//				System.out.println();
//				System.out.println(spectrum.length);
//			}
			
			

			
			

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
				result[i][j]= (float) (20*Math.log10(result[i][j]));
				if(result[i][j]<-40.0f)
					result[i][j]=-40.0f;
				if(result[i][j]>200.0f)
					result[i][j]=200.0f;
			}
		}
//		System.out.println(result.length);
//		for(int j=0; j<result.length;j++) {
//				System.out.println(result[0][j]+" ");
//			}
//			System.out.println();


		System.out.println(result.length);
		System.out.println(result[0].length);
		System.out.println(result[0][220]+" ");
		System.out.println(result[0][221]+" ");
		System.out.println(result[0][530]+" ");
		System.out.println(result[0][531]+" ");
		System.out.println();
		Log.d("MyApp","Transform complete");

		return result;
		
	}

	private Complex[] floatToComplex(float[] spectrum) {
		if(spectrum.length%2==0){
			Complex[] c=new Complex[spectrum.length/2];
			for (int i=0; i<spectrum.length; i+=2){
				c[i/2]=new Complex(spectrum[i],spectrum[i+1]);
			}
			return c;
		}
		else{
			Complex[] c=new Complex[spectrum.length/2];
			for (int i=0; i<c.length*2; i+=2){

				c[i/2]=new Complex(spectrum[i],spectrum[i+1]);
			}
			return c;
		}

	}

	public Float[] hanning(Complex[] signal, int t) {
		
		Float[] result=new Float[signal.length];
		
//		print(size);
//		System.out.println(signal.length);
		for(int i=0; i<(signal.length) ;i++) {
//			if(t==1)
//				System.out.println((0.5 * (1.0 - Math.cos(2.0 * Math.PI * i / (signal.length-1))))*signal[i].re);
			result[i] =(float)(0.5 * (1.0 - Math.cos(2.0 * Math.PI * i / (signal.length-1))))*signal[i].re;
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
	
	public float[] getAutopower(Complex[] a) {
		Complex[] b=conjArray(a);
		if(a.length!=b.length) {
			System.out.println("ERRROR, ARRAYS MUST BE SAME SIZE");
			return null;
		}
		else {
			float[] result=new float[a.length];
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
	
	private Float[][] transpose(Float[][] array) {
		Float[][] t=new Float[array[0].length][array.length];
		
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
