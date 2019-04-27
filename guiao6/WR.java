import java.util.concurrent.locks.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Arrays;

class NomesProds {

 	public static final String[] nomes = {"Martelos", "Tabuas", "Pregos"}; 
}

class Produtor extends Thread {

	Warehouse w;
	int i;

	Produtor(Warehouse w, int i) {
		this.w = w;
		this.i = i;
	}

        public void run() {
		
		Random r = new Random();
		String nome;
		int qnt;

		while(true) {
			try {
				Thread.sleep(750);
				nome = NomesProds.nomes[r.nextInt(3)];
				qnt = r.nextInt(3) + 1;
				w.supply(nome, qnt);
				System.out.println("ID: " + i + " -- Produzidas " + qnt + " unidades de: " + nome );			
		
			} catch (Exception c) { System.out.println(i + " -- " + c); } 
		}	
	
        }
}

class Consumidor extends Thread {

	Warehouse w;
	String[] alvos;
	int id;

	Consumidor(Warehouse w, String[] alvos, int id) {
		this.w = w;
		this.alvos = alvos;
		this.id = id;
	}

	public void run() {
	
		while(true) {
			try {
				Thread.sleep(150);
				w.consume(alvos);
				System.out.println("ID: " + id + " -- Consumidas: " + Arrays.toString(alvos));
		
			} catch (Exception c) { ; }
		}		
	}

}

class Warehouse {

	ReentrantLock l;
	HashMap<String, Artigo> conteudo;

	Warehouse() {
		l = new ReentrantLock();
		conteudo = new HashMap<String, Artigo>();
	}

	private Artigo preenche(String item) {
		Artigo aux = new Artigo(l);
		Artigo res;

		res = conteudo.putIfAbsent(item, aux);
		if(res == null) {
			return aux;
		}
		return res;
	}

	void supply(String item, int quantity) throws InterruptedException { 
		
		Artigo aux;
	
		l.lock();
		aux = preenche(item);
		aux.fornecer(quantity);
		
		l.unlock();
	
	}

	private boolean temTodos(String[] items) throws InterruptedException {
		Artigo a;
		boolean res = true;
		
		for(int i = 0; i < items.length && res; i++) {
			a = preenche(items[i]);
			res = res && a.haStock(); 
		}

		return res;
	}


	// retorna quando e possivel vir embora com um de cada item que precisa
	void consume(String[] items) throws InterruptedException  {
	
		Artigo aux;
		l.lock();
		try {
			while( !temTodos(items) );
			for(String s : items) {
				aux = conteudo.get(s);
				aux.consumir();
			}
		}
		finally {
			l.unlock();
		}
	}

}

class Artigo {

	Condition naoha; 
	int qnt;

	Artigo(ReentrantLock l) {
		qnt = 0;
		naoha = l.newCondition();
	}

	void fornecer(int quantos) throws InterruptedException {
		qnt += quantos;
		naoha.signalAll();
	}

	void consumir() throws InterruptedException {
		while(this.qnt == 0) {
			naoha.await();
		}
		qnt -= 1;
	}

	boolean haStock() throws InterruptedException {

		if(this.qnt == 0) {
			naoha.await();
			return false;
		}

		return true;
	}

}

class Main {

	public static void main(String[] args) {
	
		Warehouse b = new Warehouse();

		String[] a1 = {NomesProds.nomes[0]};
		String[] a2 = {NomesProds.nomes[1], NomesProds.nomes[2]};
		String[] a3 = NomesProds.nomes;
		
		new Produtor(b, 1).start();
		new Produtor(b, 2).start();
		new Produtor(b, 3).start();
	
		new Consumidor(b, a1, 4).start();
		new Consumidor(b, a2, 5).start();
		new Consumidor(b, a3, 6).start();
	}

}
