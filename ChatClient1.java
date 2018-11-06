package chatapllication3;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;

public class ChatClient1{

    static JFrame chatWindow = new JFrame("Chat Application");
    static JTextArea chatArea = new JTextArea(22, 40);
    static JTextField textField = new JTextField(40);
    static JLabel blankLabel = new JLabel("           ");
    static JButton sendButton = new JButton("Send");
    static JButton decryptButton = new JButton("Decryption");
    static BufferedReader in;
    static PrintWriter out;
    static JLabel nameLabel = new JLabel("         ");

    ChatClient1(){

        chatWindow.setLayout(new FlowLayout());
        chatWindow.add(nameLabel);
        chatWindow.add(new JScrollPane(chatArea));
        chatWindow.add(blankLabel);
        chatWindow.add(textField);
        chatWindow.add(sendButton);
        chatWindow.add(decryptButton);
        chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatWindow.setSize(475, 500);
        chatWindow.setVisible(true);

        textField.setEditable(false);
        chatArea.setEditable(false);

        sendButton.addActionListener(new Listener());
        textField.addActionListener(new Listener());
        decryptButton.addActionListener(new Listener());
    }

   

   

    void startChat() throws Exception{

        String ipAddress = JOptionPane.showInputDialog(

                chatWindow,

                "Enter IP Address:",

                "IP Address Required!!",

                JOptionPane.PLAIN_MESSAGE);    

 

        Socket soc = new Socket(ipAddress, 9806);
        in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        out = new PrintWriter(soc.getOutputStream(), true);

        while (true){

            String str = in.readLine();

            if (str.equals("NAMEREQUIRED")){

                String name = JOptionPane.showInputDialog(

                       chatWindow,

                       "Enter a unique name:",

                       "Name Required!!",

                       JOptionPane.PLAIN_MESSAGE);

                out.println(name);

            }

            else if(str.equals("NAMEALREADYEXISTS")){

                String name = JOptionPane.showInputDialog(

                    chatWindow,

                    "Enter another name:",

                    "Name Already Exits!!",

                    JOptionPane.WARNING_MESSAGE);
                
                out.println(name);
            }

            
            else if (str.startsWith("NAMEACCEPTED")){

               textField.setEditable(true);

               nameLabel.setText("You are logged in as: "+str.substring(12));
            }

            
            else{

               chatArea.append(str + "\n");
            }

        }

   }

   
    public static void main(String[] args) throws Exception {

        ChatClient1 client = new ChatClient1();
        client.startChat();

    }

}

class Listener implements ActionListener{

      @Override

    public void actionPerformed(ActionEvent e) {

          try {
              String plainText = ChatClient1.textField.getText();
              SecretKey secKey = getSecretEncryptionKey();
              byte[] cipherText = encryptText(plainText, secKey);
              //String decryptedText = decryptText(cipherText, secKey);
              ChatClient1.out.println(plainText);
              String ciphertext = bytesToHex(cipherText);
              ChatClient1.out.println(ciphertext);
              ChatClient1.textField.setText("");
          } catch (Exception ex) {
              Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
          }
    }
    
    public static SecretKey getSecretEncryptionKey() throws Exception{

	KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128); // The AES key size in number of bits
	SecretKey secKey = generator.generateKey();
        return secKey;

    }
    public static byte[] encryptText(String plainText,SecretKey secKey) throws Exception{

	Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
        return byteCipherText;

    }
    /*public static String decryptText(byte[] byteCipherText, SecretKey secKey) throws Exception {

	Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        return new String(bytePlainText);

    }*/
    private static String bytesToHex(byte[] hash) {

	String hex = DatatypeConverter.printHexBinary(hash);
        return hex;
    }
}
