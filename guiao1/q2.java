class Ex2 {

	public static void main(String[] args) throws InterruptedException {
		final int N = Integer.parseInt(args[0]);
		final int I = Integer.parseInt(args[1]);	
	
		Thread[] a = new Incrementer[N];
		
		for(int i = 0; i < N; ++i) {
			a[i] = new Incrementer(i,I);
		}
		
		for(int i = 0; i < N; ++i) {
			a[i].start();
		}

		for(int i = 0; i < N; ++i) {
			a[i].join();
			System.out.println("O thread " + i + " terminou. o contador esta no valor: " + Counter.contador);
		}

		System.out.println("O numero e: " + Counter.contador);


	}

}

class Incrementer extends Thread {
	final long I;
	final int id;

	public Incrementer(int id, long l) {
		this.I = l;
		this.id = id;
	}

	public void run() {
		for(int i = 0; i < I; i++) {
			Counter.increment();
		}
	}

}

class Counter {

	public static long contador = 0;

	public static void increment() {
		contador += 1; 
	}

}
