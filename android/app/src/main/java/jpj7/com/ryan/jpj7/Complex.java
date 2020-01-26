package jpj7.com.ryan.jpj7;

class Complex {
    public final double re;
    public final double im;
 
    public Complex() {
    	
        this(0.0, 0.0);
    }
 
    public Complex(double r, double i) {
        re = r;
        im = i;
    }
 
    public Complex add(Complex b) {
        return new Complex(this.re + b.re, this.im + b.im);
    }
 
    public Complex sub(Complex b) {
        return new Complex(this.re - b.re, this.im - b.im);
    }
 
    public Complex mult(Complex b) {
//    	System.out.println(this.toString());
//    	System.out.println(b.toString());
        return new Complex(this.re * b.re - this.im * b.im,
                this.re * b.im + this.im * b.re);
    }
    
    public Complex conj() {
    	return new Complex(this.re,-this.im);
    }
    
    public Complex clone() {
    	return new Complex(this.re, this.im);
    }
    
    public Complex scale(double s) {
    	return new Complex(this.re*s, this.im*s);
    }
    
    @Override
    public String toString() {
        return String.format("(%f,%f)", re, im);
    }
    
    
    
    
    
}