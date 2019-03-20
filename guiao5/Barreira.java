import java.util.Random;

class Proc extends Thread {

	Barreira b;
	int i;

	Proc(Barreira b, int i) {
		this.b = b;
		this.i = i;
	}

	public void run() {
		Random r = new Random();
		try {
			while(true) {	
				Thread.sleep(i * r.nextInt(250));
				System.out.println("A fazer wait - " + i);
				this.b.await();
				System.out.println("Sai! - " + i);
			}
		} catch(Exception c) {
			System.out.println(c);
		}
	
	}
}

class Barreira {

	final int bloquearAte;
	int bloqueadas;
	long prox;

	Barreira(int N) {
		bloquearAte = N;
		bloqueadas = 0;
		prox = 0;
	}

	synchronized void await() throws InterruptedException {

		long aux = prox;

		bloqueadas++;
		if(bloqueadas == bloquearAte) {
			notifyAll();
			bloqueadas = 0;
			prox++;
		}
		else {
			while(prox == aux)
				wait();
		}
	}

}


class TestaBar {
	
	public static void main(String[] args) {
	
		Barreira b = new Barreira(10);

		for(int i = 0; i < 10; i++ ) {
			new Proc(b, i).start();
		}

	}
}
