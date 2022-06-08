package Crypto;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Arrays;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class BilinearPairingCryptosystem {

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


    public static byte[][] Encrypt(Element P_pub, byte[] M)
    {
        Element u = Zr.newRandomElement().getImmutable();
        byte[] cipher = concat(M, u.toBytes()); 
        Element r = Zr.newElementFromHash(cipher,0,cipher.length);
        Element g = bp.pairing(P_pub, Q);
        byte[][] C = new byte[2][];

        C[0] = P_1.mulZn(r).toBytes();
        C[1] = BytesXor(concat(M, u.toBytes()), g.duplicate().powZn(r).toBytes());
        return C;
    }

    public static byte[] Decrypt(Element sk, byte[][] C)
    {
        Element tmp = bp.pairing(G1.newElementFromBytes(C[0]), sk);
        return BytesXor(C[1], tmp.toBytes());
    }

    public static byte[][] Sign(Element sk, byte[] M)
    {
        Element r = Zr.newRandomElement().getImmutable();
        Element f = bp.pairing(P_1.mulZn(r), sk.mulZn(Zr.newElementFromBytes(M)));
        byte[][] sig = new byte[3][];
        sig[0] = r.toBytes();
        sig[1] = M;
        sig[2] = f.toBytes();
        return sig;
    }
    public static boolean Verify(Element P_pub, byte[][] sig)
    {
        Element g = bp.pairing(P_pub, Q.mulZn(Zr.newElementFromBytes(sig[1])));
        if(Arrays.equals(sig[2], g.duplicate().powZn(Zr.newElementFromBytes(sig[0])).toBytes())){
            return true;
        }
        else{
            return false;
        }
    } 

    private static byte[] BytesXor(byte[] P, byte[] Q){

        if(P.length >= Q.length){
            byte[] c = P;
            for(int i = 0; i < Q.length; i++){
                c[i] = (byte)(P[i] ^ Q[i]);
            }
            return c;
            // boolean foundNonZero = false;
            // int i = c.length - 1;
            // while(i > 0 && !foundNonZero){
            //     foundNonZero |= (c[i]!=0);
            //     i--;
            // }
            // return Arrays.copyOfRange(c, 0, i);
        }
            
        else{
            byte[] c = Q;
            for(int i = 0; i < P.length; i++){
                c[i] = (byte)(P[i] ^ Q[i]);
            }
            return c;
            // boolean foundNonZero = false;
            // int i = c.length - 1;
            // while(i > 0 && !foundNonZero){
            //     foundNonZero |= (c[i]!=0);
            //     i--;
            // }
            // return Arrays.copyOfRange(c, 0, i);
        }
    }

    private static byte[] concat(byte[]...arrays)
    {
        // Determine the length of the result array
        int totalLength = 0;
        for (int i = 0; i < arrays.length; i++)
        {
            totalLength += arrays[i].length;
        }
    
        // create the result array
        byte[] result = new byte[totalLength];
    
        // copy the source arrays into the result array
        int currentIndex = 0;
        for (int i = 0; i < arrays.length; i++)
        {
            System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
            currentIndex += arrays[i].length;
        }
    
        return result;
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
