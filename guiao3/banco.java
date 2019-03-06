import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;
import java.util.Random;


class InvalidAcc extends Exception {
	
}

class NotEnoughFunds extends Exception {
	

}

class Transferer extends Thread {
	
	private int id;
	private Banco b;

	public Transferer(int id, Banco b) {
		this.id = id;
		this.b = b;
	}

	public void run() {
		
		Random r;
		int i, j, k;
		r = new Random();
		k = 0;

		try {
			while(true) {
				if(k == 0) System.out.println("ID: " + id + " - Vivo!");
				i = r.nextInt(10);
				j = r.nextInt(10);
				this.b.transfer(i, j, 10);
				k = (k + 1) % 5000;
			}
		} catch(Exception c) { ; }

	}	
}




class Conta {
	
	private int saldo;
	final ReentrantLock lock;

	public Conta(int saldo) {
		this.saldo = saldo;
		this.lock = new ReentrantLock();
	}

	public int getSaldo() {
		return this.saldo;
	}

	public void deposit(int val) {
		this.saldo += val;
	}

	public void withdraw(int val) throws NotEnoughFunds {
		
		if(this.saldo - val < 0) {
			throw new NotEnoughFunds();
		}

		this.saldo -= val;

	}


}

/**
 * Implementação do banco proposto no guião usando a clasee
 * ReentrantLock para controlo de concorrência
 *
 */
class Banco {

	private int ultimaconta;
	private HashMap<Integer, Conta> contas;
	private final ReentrantLock lock;

	public Banco() {

		ultimaconta = 1;
		contas = new HashMap<Integer, Conta>(10, 0.8f); 
		lock = new ReentrantLock();
	
	}

	public int createAccount(int initialB) {

		int res;

		lock.lock();
		
		try { 
			res = ultimaconta;
			this.contas.put(res, new Conta(initialB));
			ultimaconta += 1;
			return res;
		} finally {
			lock.unlock();
		}


	}

	public int closeAccount(int id) throws InvalidAcc {
		
		Conta f;
		int res;
		
		lock.lock();
		try {
			f = this.contas.remove(id);
			if(f == null) {
				throw new InvalidAcc();
			}
			res = f.getSaldo();
		} finally {
			lock.unlock();
		}

		return res;

	}

	public void deposit(int id, int val) throws InvalidAcc {
		
		Conta f;

		this.lock.lock();
		try {
			f = this.contas.get(id);
			if( f == null ) {
				throw new InvalidAcc();
			}
			f.lock.lock();
		} finally {
				lock.unlock();
		}
		
		f.deposit(val);
		f.lock.unlock();	

	}
	
	public void withdraw(int id, int val) throws InvalidAcc, NotEnoughFunds {
		Conta aux;
			
		this.lock.lock();
		try  {
			aux = this.contas.get(id);
			aux.lock.lock();
		} finally {
			this.lock.unlock();
		}

		try {
			aux.withdraw(val);
		} finally {
			aux.lock.unlock();
		}

	}

	public void transfer(int from, int to, int val) throws InvalidAcc, NotEnoughFunds {
	
		Conta f,t;

		if(from == to) return;

		this.lock.lock();
		try {
			f = this.contas.get(from);
			t = this.contas.get(to);
			if(f == null || t == null) {
				throw new InvalidAcc();
			}
			if(from < to) {
				f.lock.lock();
				t.lock.lock();
			}
			else { 
				t.lock.lock();
				f.lock.lock();
			}
		} finally {
			this.lock.unlock();
		}
		
		try {
			f.withdraw(val);
			t.deposit(val);
		} finally {
			f.lock.unlock();
			t.lock.unlock();
		}

		try{
			f.withdraw(val);
		} finally {
			f.lock.unlock();
		}

	}

	public int totalBalance(int accounts[]) throws InvalidAcc {
	
		int[] aux;
		int res;
		int i;
		Conta[] ac; 
		
		ac = new Conta[accounts.length];
		aux = accounts.clone();
		Arrays.sort(aux);
		res = 0;

		this.lock.lock();
		try {
			for(i = 0; i < accounts.length; i++) {
				ac[i] = contas.get(accounts[i]);
				if(ac[i] == null) throw new InvalidAcc();
			}

			for(i = 0; i < accounts.length; i++) {
				ac[i].lock.lock();
			}

		} finally {
			this.lock.unlock();
		}
		
		for(i = 0; i < accounts.length; i++) {
			res += ac[i].getSaldo();
			ac[i].lock.unlock();
		}

		return res;

	}

}

class TestaBanco {

	public static void main(String[] args) {
	
	
	}
}
