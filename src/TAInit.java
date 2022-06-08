import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

public class TAInit {
    public static void setup(String pairingParametersFileName, String TAParamFileName){
        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);

        long startTime = System.currentTimeMillis();
        Element P = bp.getG1().newRandomElement().getImmutable();
        Element x = bp.getZr().newRandomElement().getImmutable();
        Element Q = P.mulZn(x);
        long endTime = System.currentTimeMillis();
        System.out.println("[+] Time cost: " + (endTime - startTime) + "ms\n");
        Properties TAParam = new Properties();
        TAParam.setProperty("P", Base64.getEncoder().encodeToString(P.toBytes()));
        TAParam.setProperty("x", Base64.getEncoder().encodeToString(x.toBytes()));
        TAParam.setProperty("Q", Base64.getEncoder().encodeToString(Q.toBytes()));
        storePropToFile(TAParam, TAParamFileName);
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

    public static void main(String[] args){
        System.out.println("[+] Test TA parameters generate : \n");
        setup("a.properties","data/TAParam.properties");
        System.out.println("[+] TA parameters generate finished!\n");
        System.out.println("[+] TA parameters in data/TAParam.properties\n");
    }
}
