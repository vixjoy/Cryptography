
import java.io.*;
import java.net.*;
import java.util.*;

/**
* A simple client based off the code found at
* http://cs.lmu.edu/~ray/notes/javanetexamples/
*/
public class receiver {

  public static void main(String[] args) throws Exception {
    int port = 9069;
    String host = "localhost";
    if (args.length > 0) {
      try {
        port = Integer.parseInt(args[1]);
        host = args[0];
      } catch (NumberFormatException e) {
        System.err.println("Argument" + args[0] + " must be an host.\nArgument" + args [1]+ " must be a port number");
        System.exit(1);
      }
    }

    Socket s  = new Socket(host, port);
    String ACK = "ACK1";
    String input = "";
    String[] split;
    String expectedSeqNo = "0";
    byte expectedID;
    String complete = "";
    int checksum = 5;
    String temp;


    PrintWriter out = new PrintWriter(s.getOutputStream(),true);
    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    int totalRecived = 0;


    while(true){//execute once per word

      input = in.readLine(); //wait for packet
      totalRecived+=1;




      if(input.equals("-1")){ //terminates
        System.out.println("Message: "+complete);//print out data
        out.close();
        in.close();
        s.close();
        System.exit(1);
      }


      split = input.split(" ");


      while(input.equals("CORRUPT") ||//checksum
      input.equals("ACK2") ||
      !split[0].equals(expectedSeqNo) ||
      generateChecksum(split[3]) != Integer.parseInt(split[2]))  //check dropped packet//check sequence number

      {//hold here until correct message recived

        System.out.print("Waitng "+ expectedSeqNo+", "+totalRecived+", " + input+", "+ACK+"\n");
        out.println(ACK);//resend ACK for most recently recived package
        input = in.readLine(); //wait for packet
        totalRecived+=1;


        if(input.equals("-1")){ //terminates
          System.out.println("Message: "+complete);//print out data
          out.close();
          in.close();
          s.close();
          System.exit(1);
        }
        split = input.split(" ");
      }



      //proper ACK recevied update message
      complete += split[3]+ " ";
      //System.out.print("COMPLETE: "+complete+"\n");
      System.out.print("Waitng "+ expectedSeqNo+", "+totalRecived+", " + input+", "+ACK+"\n");
      //expectedID ++;
      if(ACK == "ACK0"){
        ACK = "ACK1";
      }else{
        ACK = "ACK0";
      }

      if(expectedSeqNo == "0"){
        expectedSeqNo = "1";
      }else{
        expectedSeqNo = "0";
      }

      out.println(ACK);//send next ACK for most recently recived package



    }


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
