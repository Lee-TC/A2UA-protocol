import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class testbc {
    /**
     * @see org.bouncycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi.ecParameters (line #173)
     * 192, 224, 239, 256, 384, 521
     * */
    private final static int KEY_SIZE = 256;//bit
    private final static String SIGNATURE = "SHA256withECDSA";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static void printProvider() {
        Provider provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
        for (Provider.Service service : provider.getServices()) {
            System.out.println(service.getType() + ": "
                    + service.getAlgorithm());
        }
    }

    public static void main(String[] args) {
        try {
            KeyPair keyPair = getKeyPair();
            ECPublicKey pubKey = (ECPublicKey) keyPair.getPublic();
            ECPrivateKey priKey = (ECPrivateKey) keyPair.getPrivate();
            //System.out.println("[pubKey]:\n" + getPublicKey(keyPair));
            //System.out.println("[priKey]:\n" + getPrivateKey(keyPair));

            //????

            String content = "82752009026766707091218501766704201542225043173436724509272980797285945667370028213306650957846329228511210069974834678182579694833173929348187427490040727945471186161285246071231912402777359399913369099590455074134585378234529492824456161502774209158658410389324517415817609465104748274465293498301111520224";
            String content_ = "5fd94a1bd0ce9ace5c8c53c215945d4c2a61215f4821d1d7d3f0b4b33a873f41,14f378a33b4b0f3d7d1d1284f51216a2c4d549512c35c8c5eca9ec0db1a49df5";


            //????
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            System.out.println("time:" + date);

            //??
            byte[] cipherTxt = encrypt(content.getBytes(), pubKey);
            //??
            byte[] clearTxt = decrypt(cipherTxt, priKey);
            //??
            System.out.println("content:" + content);
            System.out.println("cipherTxt["+cipherTxt.length+"]:" + new String(cipherTxt) + date);
            System.out.println("clearTxt:" + new String(clearTxt));

            //??
            byte[] sign1 = sign(content, priKey);
            byte[] sign2 = sign(content_, priKey);
            //??
            boolean ret = verify(content, sign1, pubKey);
            //??
            System.out.println("content:" + content);
            System.out.println("sign[" + sign1.length + sign2.length + "]:" + new String(sign1) + "//" + new String(sign2) + date);
            System.out.println("date length " + date.length() + "  enc length " + cipherTxt.length);
            System.out.println("verify:" + ret);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[main]-Exception:" + e.toString());
        }
    }

    //?????
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");//BouncyCastle
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    //????(Base64??)
    public static String getPublicKey(KeyPair keyPair) {
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    //????(Base64??)
    public static String getPrivateKey(KeyPair keyPair) {
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    //????
    public static byte[] encrypt(byte[] content, ECPublicKey pubKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
//        long stime1 = System.nanoTime();
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//        long etime1 = System.nanoTime();
//        System.out.println("encrypt执行时长 " + (etime1 - stime1)/1000000.0000 + " ms");

        return cipher.doFinal(content);
    }

    //????
    public static byte[] decrypt(byte[] content, ECPrivateKey priKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
//        long stime2 = System.nanoTime();
        cipher.init(Cipher.DECRYPT_MODE, priKey);
//        long etime2 = System.nanoTime();
//        System.out.println("decrypt执行时长 " + (etime2 - stime2)/1000000.0000 + " ms");

        return cipher.doFinal(content);
    }

    //????
    public static byte[] sign(String content, ECPrivateKey priKey) throws Exception {
        //?????????????????
        //Signature signature = Signature.getInstance(getSigAlgName(pubCert));
        Signature signature = Signature.getInstance(SIGNATURE);//"SHA256withECDSA"
        signature.initSign(priKey);
//        long stime3 = System.nanoTime();
        signature.update(content.getBytes());
//        long etime3 = System.nanoTime();
//        System.out.println("sign执行时长 " + (etime3 - stime3)/1000000.0000 + " ms");
        return signature.sign();
    }

    //????
    public static boolean verify(String content, byte[] sign, ECPublicKey pubKey) throws Exception {
        //?????????????????
        //Signature signature = Signature.getInstance(getSigAlgName(priCert));
        Signature signature = Signature.getInstance(SIGNATURE);//"SHA256withECDSA"
//        long stime4 = System.nanoTime();
        signature.initVerify(pubKey);
//        long etime4 = System.nanoTime();
//        System.out.println("verify执行时长 " + (etime4 - stime4)/1000000.0000 + " ms");
        signature.update(content.getBytes());
        return signature.verify(sign);
    }

    /**
     * ?????????????????????????????????????????
     * */
    private static String getSigAlgName(File certFile) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
        X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(new FileInputStream(certFile));
        return x509Certificate.getSigAlgName();
    }
}