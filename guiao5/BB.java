class Produtor extends Thread {

	BoundedBuffer bb;

	Produtor( BoundedBuffer b ) { this.bb = b; }

	public void run() {
		int i = 1;
		try {
			while(true) {
				Thread.sleep(500);
				System.out.println("Prod: " + i);
				this.bb.put(i);
				i++;
			}
		} catch(Exception c) {
			System.out.println(c);
		}
	}
}	



class Consumidor extends Thread {


	BoundedBuffer bb;
	
	Consumidor( BoundedBuffer b ) { this.bb = b; }

	public void run() {
		
		try {
			while(true) {
				Thread.sleep(2000);
				System.out.println("Consumido: " + this.bb.get());
			
			}
		} catch(Exception c) {
			System.out.println(c);
		}
	}
}


class BoundedBuffer {

	final int N;
	int[] container;
	int ultimaescrita;
	int ultimaleitura;
	int slotsVazios;
	
	BoundedBuffer(int n) {
	
		N = n;
		container = new int[n];
		ultimaescrita = 0;
		ultimaleitura = 0;
		slotsVazios = N;

	}

	private synchronized boolean taVazio() {
		return slotsVazios == N;
	}

	private synchronized boolean taCheio() {
		return slotsVazios == 0;
	}

	synchronized int get() throws InterruptedException {
	
		int res;
		
		while(taVazio())
			wait();

		try {	
			res = container[ultimaleitura];
			ultimaleitura = (ultimaleitura + 1) % N;
			slotsVazios++;
			
			return res;
		} finally {
			notifyAll();
		}
	}

	synchronized void put(int p) throws InterruptedException {
	
		while(taCheio())
		       wait();
		
		container[ultimaescrita] = p;
		ultimaescrita= (ultimaescrita + 1) % N;
		slotsVazios--;
		notifyAll();

	}

}

class TestaBuffer {

	public static void main(String[] args) {

		BoundedBuffer bb = new BoundedBuffer(15);

		new Consumidor(bb).start();	
		new Consumidor(bb).start();	
		new Consumidor(bb).start();	
	
		new Produtor(bb).start();
		new Produtor(bb).start();
		new Produtor(bb).start();

	}
}
