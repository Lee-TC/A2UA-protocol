import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

public class SystemGen {
    public static void setup(String pairingParametersFileName, String ChainParamFileName){
        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);

        long startTime = System.currentTimeMillis();
        Element P_1 = bp.getG1().newRandomElement().getImmutable();
        Element P_2 = bp.getG2().newRandomElement().getImmutable();
        long endTime = System.currentTimeMillis();
        System.out.println("[+] Time cost: " + (endTime - startTime) + "ms\n");

        Properties ChainParam = new Properties();
        ChainParam.setProperty("P1", Base64.getEncoder().encodeToString(P_1.toBytes()));
        ChainParam.setProperty("P2", Base64.getEncoder().encodeToString(P_2.toBytes()));
        storePropToFile(ChainParam, ChainParamFileName);
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
        System.out.println("[+] Test Chain parameters generate : \n");
        setup("a.properties","data/ChainParam.properties");
        System.out.println("[+] Chain parameters generate finished!\n");
        System.out.println("[+] Chain parameters in data/ChainParam.properties\n");
    }
}
