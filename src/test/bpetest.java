package test;

import Crypto.BilinearPairingCryptosystem;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.lang.Thread;
public class bpetest {
    static Pairing bp = PairingFactory.getPairing("a.properties");
    
    static Properties ChainParam = loadPropFromFile("data/ChainParam.properties");
    static Properties TAParam = loadPropFromFile("data/TAParam.properties");
    static Field G1 = bp.getG1();
    static Field G2 = bp.getG2();
    static Field Zr = bp.getZr();

    static Element P_1 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(ChainParam.getProperty("P1"))).getImmutable();  //Base64编码后对应的恢复元素的方法
    static Element P_2 = bp.getG2().newElementFromBytes(Base64.getDecoder().decode(ChainParam.getProperty("P2"))).getImmutable();  
    static Element P = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(TAParam.getProperty("P"))).getImmutable();  
    static Element Q = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(TAParam.getProperty("Q"))).getImmutable();  
    
    static Element s = Zr.newRandomElement().getImmutable();
    static Element P_pub = P_1.mulZn(s);
    static Element Q_pub = P_2.mulZn(s);
    static Element sk = Q.mulZn(s);
    public static void main(String[] args) throws InterruptedException
    {
        String s = "latt1ce@hdu.cn";
        // 测试加密
        System.out.println("[+] Test Bilinear Pairing Encrypt : \n");
        System.out.println("[+] My message :latt1ce@hdu.cn \n");

        long startTime1 = System.nanoTime();
        byte[][] Enc = BilinearPairingCryptosystem.Encrypt(P_pub, s.getBytes());
        long endTime1 = System.nanoTime();

        System.out.println("[+] Successfully Encrypt \n");
        System.out.println("[+] Encrypt Run Time : " + (endTime1 - startTime1)/1000000.0 + "ms\n"); 
        System.out.println("[+] Wait 3 seconds... \n");
        Thread.sleep(3000);
        // 测试解密
        System.out.println("[+] Test Bilinear Pairing Decrypt : \n");
        long startTime2 = System.nanoTime();
        byte[] dec = BilinearPairingCryptosystem.Decrypt(sk, Enc);
        long endTime2 = System.nanoTime();
        System.out.println("[+] Decrypt Run Time : " + (endTime2 - startTime2)/1000000.0 + "ms\n"); 
        System.out.println("[+] Decrypted message (M) : "+ new String(dec).substring(0, s.length()) + "\n");
        // 测试签名
        System.out.println("[+] Wait 3 seconds... \n");
        Thread.sleep(3000);
        System.out.println("[+] Test Bilinear Pairing Sign : \n");
        long startTime3 = System.nanoTime();
        byte[][] sig = BilinearPairingCryptosystem.Sign(sk, s.getBytes());
        long endTime3 = System.nanoTime();
        System.out.println("[+] Successfully Sign \n");

        System.out.println("[+] Sign message Run Time : " + (endTime3 - startTime3)/1000000.0 + "ms\n"); 

        // 测试验证签名
        System.out.println("[+] Wait 3 seconds... \n");
        Thread.sleep(3000);
        System.out.println("[+] Test Bilinear Pairing Verify : \n");
        long startTime4 = System.nanoTime();
        boolean flag = BilinearPairingCryptosystem.Verify(P_pub, sig);
        long endTime4 = System.nanoTime();
        if(flag == true)
        {
            System.out.println("[+] Correct Signature \n");
        }
        else
        {
            System.out.println("[+] Incorrect Signature \n");
        }
        System.out.println("[+] Verify Signature Run Time : " + (endTime4 - startTime4)/1000000.0 + "ms\n"); 


    }
    
    private static Properties loadPropFromFile(String fileName) {
        Properties prop = new Properties();
        try (FileInputStream in = new FileInputStream(fileName)){
            prop.load(in);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println(fileName + " load failed!");
            System.exit(-1);
        }
        return prop;
    }

}
