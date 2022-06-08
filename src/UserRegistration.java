import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;
import java.util.Arrays;

import Crypto.AES;

public class UserRegistration {
    public static void UserKeyGen(String pairingParametersFileName, String TAParamFileName, String id)throws NoSuchAlgorithmException, InterruptedException {
        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);

        Properties ChainParam = loadPropFromFile(TAParamFileName);
        Element P = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(ChainParam.getProperty("P"))).getImmutable();  //Base64编码后对应的恢复元素的方法
        Element Q = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(ChainParam.getProperty("Q"))).getImmutable();  
        Element x = bp.getZr().newElementFromBytes(Base64.getDecoder().decode(ChainParam.getProperty("x"))).getImmutable();
        // benchmarking
        long start = System.currentTimeMillis();
        byte[] idHash = sha256(id);

        Element Pk_U = bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();
        Element a = bp.getZr().newRandomElement().getImmutable();
        Element Sk_U = Pk_U.mulZn(a);

        Element r_u1 = bp.getZr().newRandomElement().getImmutable();
        Element r_u2 = bp.getZr().newRandomElement().getImmutable();
        
        byte[] rid = sha256(id + r_u1.toString());

        byte[] mr_u1 = BytesXor(r_u1.toBytes(), Pk_U.toBytes());
        byte[] mr_u2 = BytesXor(r_u1.toBytes(), r_u2.toBytes());

        byte[][] MPu = new byte[2][];
        MPu[0] = concat(Pk_U.toBytes(), mr_u1);
        MPu[1] = concat(rid, mr_u2);
        
        String I_0 = AES.encrypt(id, mr_u2.toString());
        //I_0 为AES 加密后的id, key为mr_u2 
        Element r_tmp = bp.getZr().newRandomElement().getImmutable();
        
        byte[][] I_1 = new byte[2][];
        I_1[0] = BytesXor(MPu[0], Q.mulZn(r_tmp).toBytes());
        I_1[1] = BytesXor(MPu[1], Q.mulZn(r_tmp).toBytes());

        Element I_2 = P.mulZn(r_tmp);
        // 此处User应该发送{PK_U, I_0, I_1, I_2} 给 TA(semi-trusted authority) 
        long end = System.currentTimeMillis();
        System.out.println("[+] UserKeyGen: I_0, I_1, I_2, PK_U\n");
        Thread.sleep(1000);
        System.out.println("[+] Cost time: " + (end - start) + "ms\n");
        Thread.sleep(1000);
        System.out.println("[+] User will send {PK_U, I_0, I_1, I_2} to TA\n");

        Thread.sleep(2000);
        if(Arrays.equals(MPu[0], BytesXor(I_1[0], I_2.mulZn(x).toBytes())) && Arrays.equals(MPu[1], Arrays.copyOfRange(BytesXor(I_1[1], I_2.mulZn(x).toBytes()), 0, 52))){
            System.out.println("[+] TA Successfully recoverd rID, H_2(PK_U), mr_u1, mr_u2 !!!\n");
        }
        else {
            System.out.println("[-] Failed to recover rID, H_2(PK_U), mr_u1, mr_u2 !!!\n");
        }
        Thread.sleep(2000);
        System.out.println("[+] TA will send mapping <id, PK_U> to BlockChain\n");
        // TA 检查 H_2(PK_U), 然后解密出id, 将<id, PK_U> 上传至区块链
        // benchmarking
        start = System.currentTimeMillis();
        Element AF_U = P.mulZn(x).mulZn(bp.getZr().newElementFromBytes(rid));
        byte[] I_3 = BytesXor(AF_U.toBytes(), mr_u2);
        end = System.currentTimeMillis();
        System.out.println("[+] TA Calc: I_3 then send it to User\n");
        System.out.println("[+] Cost time: " + (end - start) + "ms\n");
        Thread.sleep(1000);
        // TA 计算认证因子 并且异或后将I_3发送给User

        // User 拥有mr_u2 ,异或回来就可以恢复 AF_U,benckmarking
        start = System.nanoTime();
        byte[] AF_UU = BytesXor(I_3, mr_u2);
        end = System.nanoTime();
        System.out.println("[+] User Recover AF_U From I_3 and mr_u2\n");
        Thread.sleep(1000);
        System.out.println("[+] Cost time: " + (end - start) + "ns\n");
        Thread.sleep(1000);
        if(Arrays.equals(AF_U.toBytes(), AF_UU)){
            System.out.println("[+] User Successfully recoverd AF_U !!!\n");
        }
        else {
            System.out.println("[-] Failed to recover AF_U !!!\n");
        }
        // User 本地储存 {AF_U, r_u1}

        Properties UserParam = new Properties();
        UserParam.setProperty("AF_U", Base64.getEncoder().encodeToString(AF_U.toBytes()));
        UserParam.setProperty("r_u1", Base64.getEncoder().encodeToString(r_u1.toBytes()));
        UserParam.setProperty("rid", Base64.getEncoder().encodeToString(rid));
        String UserParamFileName = "data/" + id + ".properties";
        storePropToFile(UserParam, UserParamFileName);
    }
    public static void AnonymousTransction (String pairingParametersFileName, String ChainParamFileName, String A, String B){
        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);
        
        Properties AParam = loadPropFromFile("data/"+ A +".properties");
        Properties BParam = loadPropFromFile("data/"+ B +".properties");

        Element rid_A = bp.getZr().newElementFromBytes(Base64.getDecoder().decode(AParam.getProperty("rid"))).getImmutable();
        Element rid_B = bp.getZr().newElementFromBytes(Base64.getDecoder().decode(BParam.getProperty("rid"))).getImmutable();

        Element k = bp.getZr().newRandomElement().getImmutable();
        Element Tk = bp.getZr().newRandomElement().getImmutable();


    }
    public static byte[] sha256(String content) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA-256");
        instance.update(content.getBytes());
        return instance.digest();
    }

    public static Properties loadPropFromFile(String fileName) {
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

    public static void storePropToFile(Properties prop, String fileName){
        try(FileOutputStream out = new FileOutputStream(fileName)){
            prop.store(out, null);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println(fileName + " save failed!");
            System.exit(-1);
        }
    }

    public static byte[] BytesXor(byte[] P, byte[] Q){

        if(P.length >= Q.length){
            byte[] c = P;
            for(int i = 0; i < Q.length; i++){
                c[i] = (byte)(P[i] ^ Q[i]);
            }
            return c;
        }
        else{
            byte[] c = Q;
            for(int i = 0; i < P.length; i++){
                c[i] = (byte)(P[i] ^ Q[i]);
            }
            return c;
        }
    }
    public static byte[] concat(byte[]...arrays)
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
    public static void main(String[] args) throws Exception {
        UserKeyGen("a.properties", "data/TAParam.properties", "Latt1ce@vidar.club");
        // UserKeyGen("a.properties", "data/ChainParam.properties", "Potat0@vidar.club");
        
    }
}
