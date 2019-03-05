import java.util.Random;
import java.util.Arrays;

class InvalidAccount extends Exception {
	
	public final int id;

	InvalidAccount(int id) {
		this.id = id;
	}

}

class NotEnoughFunds extends Exception {
	

}




class Transferer extends Thread {

	final Banco b;
		
	public Transferer(Banco b) {
		this.b = b;
	}

	public void run() {
		try {
		Random r = new Random();
		int k = 0;
		while(true) {
			int i = r.nextInt(5);
			int j = r.nextInt(5);
			b.transfer(i, j, 10);
			if(k % 100 == 0) 
				System.out.println("Tou vivo!");

			k++;
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
		try {	
			int[] a = {4, 0, 1, 3, 2};
			while(true) {
				int total = b.totalBalance(a);
				if(total != 5000000) {
					System.out.println(total);
				}
			}
		}
		catch (Exception c) { System.out.println("Exception"); }	
	}
}


class Banco {

	private static class Account {
		
		int balance = 0;
		
		synchronized int getBalance() {
			return balance;
		}

		synchronized void deposit(int val) {
			balance += val;
		}

		synchronized void withdraw(int val) throws NotEnoughFunds {
			
			if(balance - val < 0) {
				throw new NotEnoughFunds();
			}

			balance -= val;
		} 
	}
	
	private Account[] contas;

	public Banco(int capacidade) {
		this.contas = new Account[capacidade];
		for(int i = 0; i < capacidade; ++i) {
			contas[i] = new Account();
		}
	}

	void deposit(int id, int val) throws InvalidAccount {
		if(id >= contas.length || id < 0) {
			throw new InvalidAccount(id);
		}
		contas[id].deposit(val);
	}

	void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {

		if(id >= contas.length || id < 0) {
			throw new InvalidAccount(id);
		}

		contas[id].withdraw(val);
	}

	void transfer(int from, int to, int qnt) throws InvalidAccount, NotEnoughFunds {
		
		if(from == to) return;
		// testid(from)
		// testid(to)

		Account f = contas[from];
		Account t = contas[to];
		Account c1, c2;

		if(from < to) {
			c1 = f;
			c2 = t;
		}
		else {
			c1 = t;
			c2 = f;
		}

		synchronized(f) {
			synchronized(t) {
				withdraw(from, qnt);
				deposit(to, qnt);
			}
		}
	}

	private int totalBalance(int[] ids, int indice) throws InvalidAccount, NotEnoughFunds {
 		
		int id;

		if(indice == ids.length) {
			return 0;
		}

		id = ids[indice];
		//testid(id);
		synchronized(contas[id]) {
			return contas[id].getBalance() + totalBalance(ids, indice+1);
		}

	}

	int totalBalance(int[] ids) throws InvalidAccount, NotEnoughFunds {
		ids = ids.clone();
		Arrays.sort(ids);
		return totalBalance(ids, 0);
	}	
}

class testaBanco {

	public static void main(String[] args) throws Exception {
	
		int N = 10;

		Banco b = new Banco(10);
		Thread d;

		for(int i = 0; i < N; i++) {
			b.deposit(i, 1000000);
		}

		new Transferer(b).start();
		new Transferer(b).start();
		new Transferer(b).start();
		new Transferer(b).start();

		new Checker(b).start();	
	}


}
