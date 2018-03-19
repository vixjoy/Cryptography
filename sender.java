
import java.io.*;
import java.net.*;
import java.util.*;

import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;


/**
* A simple client based off the code found at
* http://cs.lmu.edu/~ray/notes/javanetexamples/
*/
public class sender {

  public static void main(String[] args) throws Exception {
    int port = 9069;
    String host = "localhost";
    String m = "test.txt";
    if (args.length > 0) {
      try {
        port = Integer.parseInt(args[1]);
        host = args[0];
        m = args[2];
      } catch (NumberFormatException e) {
        System.err.println("Argument" + args[0] + " must be an host.\nArgument" + args [1]+ " must be a port number");
        System.exit(1);
      }
    }

    FileInputStream fstream = new FileInputStream(m);
    DataInputStream dataIn = new DataInputStream(fstream);
    BufferedReader buffReader = new BufferedReader(new InputStreamReader(dataIn));

    String input,message;
    byte seqNo,ID,end;
    int checksum= 5;
    String[] split;

    end = -1;

    int totalSent = 0;

    Socket s  = new Socket(host, port);
    PrintWriter out = new PrintWriter(s.getOutputStream(),true);
    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));


    input = buffReader.readLine();
    split = input.split(" ");
    message = "";
    seqNo = 1;
    ID = -1;
    String expectedString = "ACK1";

    while (true) {// while loop should execute once per word

      //set up for next word
      if(seqNo == 0){
        seqNo = 1;
      }else{
        seqNo = 0;
      }
      if(expectedString=="ACK0"){
        expectedString = "ACK1";
      }else{
        expectedString = "ACK0";
      }
      ID++;
      //checksum = generateChecksum(split[ID]);

      message = seqNo + " " + ID + " " + generateChecksum(split[ID])  + " " + split[ID] + " " + "\0";
      System.out.print("Waiting "+ expectedString +", "+ totalSent+ ", send Packet"+seqNo+"\n");
      totalSent++;
      out.println(message);//send inital message

      input = in.readLine();  //wait for response

      if(split[ID].contains(".")){//waiting for final ACK
        while(!input.equals(expectedString)){
          System.out.print("Waiting "+ expectedString +", "+totalSent +", no more packets to send\n");
          totalSent++;
          out.println(message);
          input = in.readLine();  //wait for response
        }
        out.println("-1");
        s.close();
        out.close();
        in.close();
        System.exit(0);
      }else{//waiting for traditional ACK
        while(!input.equals(expectedString)){
          System.out.print("Waiting "+ expectedString +", "+ totalSent+ ", resend Packet"+seqNo+"\n");
          totalSent++;
          out.println(message);
          input = in.readLine();  //wait for response
        }
      }//end else


    }//end while loop

  }

  static Integer generateChecksum(String s) {
       int asciiVal;
       int checksum = 0;
       for (int i = 0; i < s.length(); i++) {
         asciiVal = (int) s.charAt(i);
         checksum += asciiVal;
       }
       return checksum;
        }

}
