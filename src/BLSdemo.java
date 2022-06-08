import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class BLSdemo {
    public static void main(String[] args){
        // Init
        Pairing bp = PairingFactory.getPairing("a.properties");

        Field G1 = bp.getG1();
        Field Zr = bp.getZr();

        Element g = G1.newRandomElement();
        Element x = Zr.newRandomElement();
        Element g_x = g.duplicate().powZn(x);

        // sig
        String msg = "Jayson Tatum";
        byte[] msg_hash = Integer.toString(msg.hashCode()).getBytes();
        Element h = G1.newElementFromHash(msg_hash, 0, msg_hash.length);
        Element sig = h.duplicate().powZn(x);

        // verify
        Element pl = bp.pairing(g, sig);
        Element pr = bp.pairing(h, g_x);
        if (pl.isEqual(pr)){
            System.out.println("YES!!!");
        }
        else {
            System.out.println("NO!!!");
        }
    }
}
