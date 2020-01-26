package jpj7.com.ryan.jpj7;

import java.util.Arrays;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.sin;

public class FFT1 {
 
//    public static int bitReverse(int n, int bits) {
//        int reversedN = n;
//        int count = bits - 1;
// 
//        n >>= 1;
//        while (n > 0) {
//            reversedN = (reversedN << 1) | (n & 1);
//            count--;
//            n >>= 1;
//        }
// 
//        return ((reversedN << count) & ((1 << bits) - 1));
//    }
// 
//    static Complex[] fft(Complex[] buffer) {
//    	
//    	if (Math.log(buffer.length) % 2 != 0) {
//        	int size=(int) Math.pow(2,floor(Math.log(buffer.length)/Math.log(2)+1e-10)+1);
//        	
////        	System.out.println(size);
//        	Complex[] x_=new Complex[size];
//        	Arrays.fill(x_, new Complex(0,0));
////        	System.out.println(buffer.length);
//        	for(int i=0; i<buffer.length; i++)
//        	{
//        		x_[i]=buffer[i];
//        	}
//        	buffer=x_;
//            
//        }
////    	System.out.println("Size:"+buffer.length);
//
//        int bits = (int) (log(buffer.length) / log(2));
//        for (int j = 1; j < buffer.length / 2; j++) {
// 
//            int swapPos = bitReverse(j, bits);
//            Complex temp = buffer[j];
//            buffer[j] = buffer[swapPos];
//            buffer[swapPos] = temp;
//        }
// 
//        for (int N = 2; N <= buffer.length; N <<= 1) {
//            for (int i = 0; i < buffer.length; i += N) {
//                for (int k = 0; k < N / 2; k++) {
// 
//                    int evenIndex = i + k;
//                    int oddIndex = i + k + (N / 2);
//                    Complex even = buffer[evenIndex];
//                    Complex odd = buffer[oddIndex];
// 
//                    double term = (-2 * PI * k) / (double) N;
//                    Complex exp = (new Complex(cos(term), sin(term)).mult(odd));
// 
//                    buffer[evenIndex] = even.add(exp);
//                    buffer[oddIndex] = even.sub(exp);
//                }
//            }
//        }
//        Complex[] result=new Complex[buffer.length];
//        
//        for(int i=0; i<buffer.length;i++) {
//        	result[i]=buffer[i].clone();
//        }
//        
////        System.out.println("Results:");
////		
////			for (Complex c : result) {
////		          System.out.print(c);
////		      }
////			System.out.println();
//		
//        return result;
//    }
    
    public static int bitReverse(int n, int bits) {
        int reversedN = n;
        int count = bits - 1;
 
        n >>= 1;
        while (n > 0) {
            reversedN = (reversedN << 1) | (n & 1);
            count--;
            n >>= 1;
        }
 
        return ((reversedN << count) & ((1 << bits) - 1));
    }
 
    static Complex[] fft(Complex[] buffer) {
    	
    	if ((Math.log(buffer.length)/Math.log(2))%1 != 0) {
        	int size=(int) Math.pow(2,floor(Math.log(buffer.length)/Math.log(2)+1e-10)+1);
        	
        	System.out.println(size);
        	Complex[] x_=new Complex[size];
        	Arrays.fill(x_, new Complex(0,0));
//        	System.out.println(buffer.length);
        	for(int i=0; i<buffer.length; i++)
        	{
        		x_[i]=buffer[i];
        	}
        	buffer=x_;
            
        }
        int bits = (int) (log(buffer.length) / log(2)+1e-10);
        for (int j = 1; j < buffer.length / 2; j++) {
 
            int swapPos = bitReverse(j, bits);
            Complex temp = buffer[j];
            buffer[j] = buffer[swapPos];
            buffer[swapPos] = temp;
        }
 
        for (int N = 2; N <= buffer.length; N <<= 1) {
            for (int i = 0; i < buffer.length; i += N) {
                for (int k = 0; k < N / 2; k++) {
 
                    int evenIndex = i + k;
                    int oddIndex = i + k + (N / 2);
                    Complex even = buffer[evenIndex];
                    Complex odd = buffer[oddIndex];
 
                    double term = (-2 * PI * k) / (double) N;
                    Complex exp = (new Complex(cos(term), sin(term)).mult(odd));
 
                    buffer[evenIndex] = even.add(exp);
                    buffer[oddIndex] = even.sub(exp);
                }
            }
        }
        Complex[] result=Arrays.copyOf(buffer, buffer.length);
        return result;
    }
    
    
    
//    public static void main(String[] args) {
//        double[] input = {0.3, 0.24, 0.4, 0.01, 0.3, 0.23, 0.01, 0.02};
 
//        Complex[] cinput = new Complex[input.length];
//        for (int i = 0; i < input.length; i++)
//            cinput[i] = new Complex(input[i], 0.0);
// 
//        fft(cinput);
// 
//        System.out.println("Results:");
//        for (Complex c : cinput) {
//            System.out.println(c);
//        }
//    }
// 

}