RDT 3.0 Simulation

Compilation:
javac network.java javac receiver.java javac sender.java
-or-
javac *.java

Execution: 
My code has default values for all 3 java files. If passed no arguments the programs will default to port 9069 on localhost. Sender.java will read from test.txt.
java network java receiver java sender
-or-
java network 1234
java receiver localhost 1234
java sender localhost 1234 myFile.txt

Code structure:
Sender.java is a while loop that executes once per word. Inside there are more loops that will consistently send the same message until the expected response is received. Once the proper ACK is received sender.java will construct the new message to send. When a period is detected the code will enter termination mode. Once the proper ACK is received sender gracefully terminates.
Receiver.java implements a similar structure. Receiver.java will continue onto the next ACK once it receives a message with the proper sequence number and checksum. The proper message is concatenated to the final output message. Upon receiving a -1 termination byte the message is printed and the program gracefully terminates.
Network.java has two classes. Receiver_ and sender_ are the two threads that my network will accept. Receiver_ must always be connected first. When a packet comes in the network uses the Math.random() function to determine if the packet should be PASS, DROP, or CORRUPT. DROP will send the message
 
“ACK2”, CORRUPT adds one to the checksum and PASS gives the message forward unchanged. When the -1 byte is received from the sender -1 is passed to the receiver then the program gracefully terminates.
Examples:
I demonstrate the default values explained above in this screenshot. The receiver’s ACKs are alternating and the total number of packets sent counting logically. The message received is then printed. The sender is sending and resending packets as expected and the network is behaving as the go-between.
This screenshot demonstrates the non-default values.

Limitations:
My program will exit if there is a period anywhere in the final word. Periods should not be in the middle of words unless they are the final word in the message.
I was having an issue compiling my code but I think it resolved. I had my classes sender and receiver named the same as my java codes. I changed my class names to sender_ and receiver_ respectively. This fixed the compilation bug. If you run into the bug during testing execute the code this way:
javac network.java
  
java network 1234
javac receiver.java
java receiver localhost 1234
javac sender.java
java sender localhost 1234 myFile.txt
