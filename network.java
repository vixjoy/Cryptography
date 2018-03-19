import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * My name is Victoria. I created this server for CNT 4007C in 2018
 * I based this code off the code found at the url
 * http://cs.lmu.edu/~ray/notes/javanetexamples/
 */
public class network {
  public static void main(String[] args) throws Exception {

    int port = 9069;//defalut port
    int clientNumber = 0;
    if (args.length > 0) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.err.println("Argument" + args [0]+ " must be a port number");
        System.exit(1);
      }
    }
      //ServerSocket ss = new ServerSocket(port);
      //Socket s = ss.accept();


    try
      {
        connection c = new connection(port);


        Thread r = new Thread(new receiver_(c));
        r.start();
        while(!c.ready){
          System.out.print("");
        };
        System.out.println("Ready to connect sender");
        Thread se = new Thread(new sender_(c));
        se.start();

    } finally {
      //s.close();
    }

  }
}

  /**
  * A private thread to handle requests on a particular
  * socket.  The client terminates the dialogue by sending a single line
  * containing only a period.
  */
  class connection extends Thread {
    //serverSocket;
    ServerSocket s1;
    boolean ready = false;

    public connection(int portNumber) throws IOException{
      s1 = new ServerSocket(portNumber);
    }

  }

  class sender_ implements Runnable {
      connection con;
      static PrintWriter out;
      static BufferedReader in;
      sender_(connection con){
        this.con = con;
      }
        public void run() {
          try{
            Socket s = con.s1.accept();
            System.out.println("Hello from a sender!");

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(),true);
            double x;
            String input;
            String[] split;
            int temp;




            while(true){

            input = in.readLine();

            if(input.equals("-1")){
              receiver_.out.println(input);
              in.close();
              out.close();
              s.close();
              receiver_.in.close();
              receiver_.out.close();
              System.exit(0);
            }
            x = Math.random();

            split = input.split(" ");

            if(x < .5){
              //send packet to receiver
              System.out.print("Received: Packet"+split[0]+", "+split[1]+"," +" PASS\n");
              receiver_.out.println(input);
            }else if(x<.75){
              //send CORRUPT to receiver
              System.out.print("Received: Packet"+split[0]+", "+split[1]+"," +" CORRUPT\n");
              split = input.split(" ");
              temp = Integer.parseInt(split[2]) + 1;
              input = split[0]+" "+split[1]+" "+temp+" "+split[3];
              receiver_.out.println(input);
            }else{
              //send DROP to reciver
              System.out.print("Received: Packet"+split[0]+", "+split[1]+"," +" DROP\n");
              receiver_.out.println("ACK2");
            }
          }

          } catch(IOException ex){
            System.err.println("something is wrong with the sender");
            System.exit(1);
          }
        }
    }

   class receiver_ implements Runnable {
      connection con;
      static BufferedReader in;
      static PrintWriter out;
      receiver_(connection con_){
        con = con_;
      }
        public void run() {
          try{
            Socket s = con.s1.accept();
            System.out.println("Hello from a receiver!");//call s.start();
            con.ready = true;

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(),true);
            double x;
            String input;
            String[] split;
            int temp;

            while(true){
            input = in.readLine();



          try{
            if(input.equals("-1")){
              System.exit(0);
            }
          }catch(NullPointerException e){
            System.exit(0);
          }
          x = Math.random();
            if(x < .5){
              //send ACK to sender
              System.out.print("Received: "+ input + " PASS\n");
              sender_.out.println(input);
            }else if(x< .75){
              //send CORRUPT to sender
              System.out.print("Received: "+ input + " CORRUPT\n");
              sender_.out.println("ACK2");
            }else{
              //send DROP to sender
              System.out.print("Received: "+ input + " DROP\n");
              sender_.out.println("ACK2");
            }
          }
        }
        catch(IOException ex){
          System.err.println("something is wrong with the receiver");
          System.exit(1);
        }

      }
    }
