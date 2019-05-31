import java.util.HashMap;
import java.util.concurrent.locks.*;
import java.util.Calendar;

class Evento {
  private int nrocur;
  private long mss;
  public final Condition cond;

  public Evento(Condition l) {
    nrocur = 0;
    mss = Calendar.getInstance().getTimeInMillis();
    cond = l;
  }

  synchronized public void incr() {
    nrocur += 1;
    mss = Calendar.getInstance().getTimeInMillis();
  }

  synchronized public boolean fevent(int nrocur) {
    return this.nrocur != nrocur;
  }

  synchronized public boolean sevent(int nrocur) {
    return this.nrocur - nrocur < 2;
  }

  synchronized public long tempo() {
    return this.mss;
  }
  
  synchronized public int ocur() {
    return this.nrocur;
  }
  
}

class GereEventos {

  final HashMap<String, Evento> eventos;
  final Lock lk;

  public GereEventos() {
    eventos = new HashMap<>();
    lk = new ReentrantLock();
  }

  public void evento(String evento) {
    Evento aux;
    lk.lock();
    this.eventos.putIfAbsent(evento, new Evento(lk.newCondition()));
    aux = this.eventos.get(evento);
    aux.incr();
    aux.cond.signalAll();
    lk.unlock();
  }

  public void waitDouble(String evento) throws Exception {
    Evento aux;
    lk.lock();
    this.eventos.putIfAbsent(evento, new Evento(lk.newCondition()));
    aux = this.eventos.get(evento);
    long ms;
    int nr = aux.ocur();
    
    
    while(aux.fevent(nr)) 
      aux.cond.await();

    ms = aux.tempo();

    while(Calendar.getInstance().getTimeInMillis() - ms <= 100 || aux.sevent(nr)) {
       aux.cond.await();
    }
    lk.unlock();
  }

}

class Exec extends Thread {

  final GereEventos ge;
  String ev;

  Exec(GereEventos ge, String ev) {
    this.ge = ge;
    this.ev = ev;
  }

  public void run() {
    try {
      while(true) {
        ge.waitDouble(ev);
        System.out.println("Sai do event: " + ev); 
      }
    } catch (Exception e) { System.out.println(e); }
  
  }
}

class Proc extends Thread {

  int id;
  final GereEventos ge;
  String ev;

  Proc(int id, GereEventos ge, String ev) {
    this.id = id;
    this.ge = ge;
    this.ev = ev;
  }

  public void run() {
    try {
      while(true) {
      Thread.sleep(500);
      ge.evento(ev);
      System.out.println("Feito o event: " + ev + " ::: " + id); 
      }
    } catch (Exception e) { System.out.println(e); }
  }
}

class Tester {
  public static void main(String[] args) throws Exception {
    GereEventos ge = new GereEventos();
    new Exec(ge, "x").start();
    new Exec(ge, "x").start();
    new Exec(ge, "y").start();

    new Proc(1, ge, "x").start();
    new Proc(3, ge, "x").start();
    new Proc(2, ge, "y").start();

  }
}
