import java.util.HashMap;

class InvalidAcc extends Exception {
	
	int id;

	InvalidAcc(int id) {
		this.id = id;
	}
}

class NotEnoughFunds extends Exception {
	

}

class Conta {
	
	int saldo;

	public Conta(int saldo) {
		this.saldo = saldo;
	}

	int getSaldo() {
		return this.saldo;
	}

	void deposit(int val) {
		this.saldo += val;
	}

	void withdraw(int val) throws NotEnoughFunds {
		
		if(this.saldo - val < 0) {
			throw new NotEnoughFunds();
		}

		this.saldo -= val;

	}

}

class Banco {

	private static int ultimaconta = 1;

	private HashMap<Integer, Conta> contas;

	Banco() {
		contas = new HashMap<Integer, Conta>(10, 0.9f); 
	}

	static int getLast() {
		return ultimaconta;
	}

	static void incLast() {
		ultimaconta += 1;
	}

	private void testid(int id) throws InvalidAcc {

		if( ! this.contas.containsKey(id) ) {
			throw new InvalidAcc(id);
		}

	}

	int createAccount(int initialB) {

		int res = ultimaconta;
		
		this.contas.put(res, new Conta(initialB));
		incLast();

		return res;

	}

	int closeAccount(int id) throws InvalidAcc {
		
		Conta f;
		
		testid(id);
		f = this.contas.remove(id);

		return f.getSaldo();

	}

	void deposit(int id, int val) throws InvalidAcc {
	
	}
	
	void withdraw(int id, int val) throws InvalidAcc, NotEnoughFunds {
	
	}

	void transfer(int from, int to, int val) throws InvalidAcc, NotEnoughFunds {
	
	}

	int totalBalance(int accounts[]) throws InvalidAcc {
	
		int res = 0;


		return res; 

	}

}
