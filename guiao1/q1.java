class Ex1 {
	public static void main(String[] args) {
		final int N = Integer.parseInt(args[0]);
		final int I = Integer.parseInt(args[1]);	
	
		Thread[] a = new Printer[N];
		
		for(int i = 0; i < N; ++i) {
			a[i] = new Printer(i,I);
		}
		
		for(int i = 0; i < N; ++i) {
			a[i].start();
		}
	}

}

class Printer extends Thread {
	final long I;
	final int id;

	public Printer(int id, long l) {
		this.I = l;
		this.id = id;
	}

	public void run() {
		for(int i = 0; i < I; i++) {
			System.out.println("id: " + this.id + " -- nmr: " + i);
		}
	}

}
