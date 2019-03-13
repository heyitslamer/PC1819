import java.util.concurrent.Semaphore;
import java.util.Random;

class Produtor extends Thread {

	BoundedBuffer b; 

	Produtor(BoundedBuffer b) { this.b = b; };

	public void run() {
		int i = 0;
		try {
			while(true) {
				Thread.sleep(500);
				System.out.println("Produzido: " + i);
				this.b.put(i);
				i++;
			}
		} catch(Exception c) {
			System.out.println("Erro! - P " + c);
		}
	}
}

class Consumidor extends Thread {

	BoundedBuffer b;

	Consumidor(BoundedBuffer b) { this.b = b; };
	
	public void run() {

		try {
			while(true) {
				Thread.sleep(1000);
				System.out.println("Consumido: " + this.b.get());	
			}		
		} catch (Exception c) {
			System.out.println("Erro! - C " + c);
		} 

	}
}




class BoundedBuffer {

	// semaforos
	Semaphore items;
	Semaphore slots;
	Semaphore escrita;
	Semaphore leitura;
	// variaveis
	final int N;
	int[] container;
	int ultimaescrita;
	int ultimaleitura;

	BoundedBuffer(int n) {
		N = n;
		items = new Semaphore(0);
		slots = new Semaphore(n);
		escrita = new Semaphore(1);
		leitura = new Semaphore(1);
		
		container = new int[n];
		ultimaescrita = 0;
		ultimaleitura = 0;
	}

	int get() throws InterruptedException {
		int res;

		items.acquire();
		leitura.acquire();
		res = container[ultimaleitura];
		ultimaleitura = (ultimaleitura + 1) % N;
		leitura.release();
		slots.release();
		return res;

	}

	void put(int x) throws InterruptedException {
		
		slots.acquire();
		escrita.acquire();
		container[ultimaescrita] = x;
		ultimaescrita = (ultimaescrita + 1) % N;
		escrita.release();
		items.release();

	}
}


class testaBuffer {

	public static void main(String[] args) {
		
		int c, p;
		BoundedBuffer b = new BoundedBuffer(20);

		c = Integer.parseInt(args[0]);
		p = Integer.parseInt(args[1]);


		for(int i = 0; i < c; i++) {
			new Consumidor(b).start();
		}

		for(int i = 0; i < p; i++) {
			new Produtor(b).start();	
		}
	
	} 
}
