class Ex1 {

	public static void main(String[] args) throws InterruptedException {
		final int N = Integer.parseInt(args[0]);
		final int I = Integer.parseInt(args[1]);	
	
		Thread[] a = new Incrementer[N];
		Counter c = new Counter();

		
		for(int i = 0; i < N; ++i) {
			a[i] = new Incrementer(I,c);
		}
		
		for(int i = 0; i < N; ++i) {
			a[i].start();
		}

		for(int i = 0; i < N; ++i) {
			a[i].join();
		}

		System.out.println("O numero e: " + c.get());


	}

}

class Incrementer extends Thread {
	final long I;
	final Counter c;

	public Incrementer(int I, Counter c) {
		this.I = I;
		this.c = c;
	}

	public void run() {
		for(int i = 0; i < I; i++) {
			this.c.increment();
		}
	}

}

class Counter {

	private long contador;

	public synchronized void increment() {
		contador += 1; 
	}

	public synchronized long get() {
		return this.contador;
	}

}
