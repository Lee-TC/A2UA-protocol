package test;

import Crypto.AES;

public class aestest {
    public static void main(String[] args) throws Exception {
        String mykey = "1234567890123456";
        AES.setKey("1234567890123456");
        String plaintext = "Hello World";
        // benchmark 
        long startTime = System.currentTimeMillis();
        String ciphertext = AES.encrypt(plaintext, mykey);
        long endTime = System.currentTimeMillis();
        // print ciphertext
        System.out.println("[+] Ciphertext: " + ciphertext + "\n");
        System.out.println("[+] Time cost: " + (endTime - startTime) + "ms\n");
        // benchmark 
        startTime = System.currentTimeMillis();
        String plaintext2 = AES.decrypt(ciphertext, mykey);
        endTime = System.currentTimeMillis();
        // print plaintext
        System.out.println("[+] Plaintext: " + plaintext2 + "\n");
        System.out.println("[+] Time cost: " + (endTime - startTime) + "ms\n");
    }   
}
