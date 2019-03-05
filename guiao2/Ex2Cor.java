import java.util.Arrays;
import java.util.Random;

class InvalidAcc extends Exception {
	
	public final int id;

	InvalidAcc(int id) {
		this.id = id;
	}
}

class NotEnoughFunds extends Exception {


}

class NContas {
	public static final int c = 5;
	public static final int cred = 1000000;
}

class Ex2 {

	public static void main(String[] args) {
	
		Banco b = new Banco(NContas.c, NContas.cred);

		new Transferer(b,1).start();	
		new Transferer(b,2).start();	
		new Transferer(b,3).start();	
		new Transferer(b,4).start();	

		new Checker(b).start();

	}

}

class Transferer extends Thread {

	final Banco b;
	final int id;

	public Transferer(Banco b, int id) {
		this.b = b;
		this.id = id;
	}

	public void run() {
		
		Random r = new Random();
		int i, j;
		int k = 0;
		try {

			while(true) {
				if(k == 0) {
					System.out.println("ID: " + this.id + " - Vivo!");
				}
				i = r.nextInt(NContas.c);
				j = r.nextInt(NContas.c);
				this.b.transfer(i, j, 10);
				k = (k + 1) % 5000;
			}

		} catch(Exception c) { ; }

	}
}


class Checker extends Thread {

	final Banco b;

	public Checker(Banco b) {
		this.b = b;
	}

	public void run() {
		final int[] a = {4, 0, 1, 3, 2};
		int total;
		try {
			while(true) {
				total = b.totalBalance(a);
				if(total != NContas.c * NContas.cred) {
					System.out.println(total);
				}
			}
		} catch (Exception c) { System.out.println("Exception!"); }
	}
}


class Conta {
	
	int saldo = 0;

	public Conta(int capacidade) {
		this.saldo = capacidade;
	}

	synchronized int getBalance() {
		return saldo;
	}

	synchronized void deposit(int val) {
		saldo += val;
	}

	synchronized void withdraw(int val) throws NotEnoughFunds {
		
		if(saldo - val < 0) {
			throw new NotEnoughFunds();
		}

		saldo -= val;
	}
}

class Banco {

	private Conta[] contas;

	public Banco(int capacidade, int saldoinit) {
		
		this.contas = new Conta[capacidade];
		for(int i = 0; i < capacidade; i++) {
			contas[i] = new Conta(saldoinit);
		}
		
	}

	private void testid(int id) throws InvalidAcc {
		
		if(id < 0 || id >= contas.length) {
			throw new InvalidAcc(id);
		}

	}


	void deposit(int id, int val) throws InvalidAcc {
	
		testid(id);

		contas[id].deposit(val);
	}

	void withdraw(int id, int val) throws InvalidAcc, NotEnoughFunds {
		
		testid(id);
		contas[id].withdraw(val);

	}

	private int totalBalance(int[] ids, int indice) throws InvalidAcc {
	
		int id;

		if(indice == ids.length) {
			return 0;
		}

		id = ids[indice];
		testid(id);
		synchronized(contas[id]) {
			return contas[id].getBalance() + totalBalance(ids, indice + 1);
		}
	}

	int totalBalance(int[] ids) throws InvalidAcc {

		int[] aux = ids.clone();
		Arrays.sort(aux);
		return totalBalance(aux, 0);
	
	}

	int oldtotalBalance(int[] ids) throws InvalidAcc {
		
		int res = 0;

		for(int i = 0; i < ids.length; i++) {
			testid(ids[i]);
			res += contas[ids[i]].getBalance();
		}

		return res;
	}

	void transfer(int from, int to, int val) throws InvalidAcc, NotEnoughFunds {
		if(from == to)
			return ;
		
		Conta f, t, p, s;

		testid(from);
		testid(to);
		
		f = contas[from];
		t = contas[to];
		
		if(from > to) {
       			p = t;
			s = f;			
		}
		else {
			p = f;
			s = t;
		}
			
		synchronized(p) {
			synchronized(s) {
				f.withdraw(val);
				t.deposit(val);
			}
		}

	}

}
