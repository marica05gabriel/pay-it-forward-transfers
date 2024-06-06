package ro.payitforward.pay_it_forward_transfers.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

@Service
public class SignatureService {

    @Value("${ALCHEMY_PROVIDER_HTTP_URL}")
    private String provider_url;

    public String verifyTransaction() {
        System.out.println("Provider url: " + provider_url);
        Web3j web3 = Web3j.build(new HttpService(provider_url));
        try {
            BigInteger blockNumber = web3.ethBlockNumber().send().getBlockNumber();
            System.out.println("Latest Ethereum block number: " + blockNumber);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String sign(String message) throws SignatureException {
//        String privateAccountKey = "12c01464380d2dcf34adf62f63fa5534ca30ed86ef870f7336f267d8ec073ef2";
//        String publicAddress = "0x257922e046f34ceF26059fFfD3eD777D848b3857";

        String privateAccountKey = "a67cd263f6c6e02273ee15d1e7bd678f3787880dac1b742f52ec5e1a22e03cce";
        String publicAddress = "0xeF20E03C103cF9Cd4E0716e0486a6349A2daA350";
        Credentials credentials = Credentials.create(privateAccountKey);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        Sign.SignatureData signature = Sign.signPrefixedMessage(messageBytes, credentials.getEcKeyPair());
        System.out.println(signature);
        System.out.println(new String(signature.getR()));
        System.out.println(new String(signature.getS()));
        System.out.println(new String(signature.getV()));


        byte[] retval = new byte[65];
        System.arraycopy(signature.getR(), 0, retval, 0, 32);
        System.arraycopy(signature.getS(), 0, retval, 32, 32);
        System.arraycopy(signature.getV(), 0, retval, 64, 1);
        System.out.println(Numeric.toHexString(retval));

        String result = getAddressUsedToSignHashedMessage(Numeric.toHexString(retval), message);
        System.out.println(result);
        String result1 = "0x" + result;
        System.out.println(result1);
        String result2 = result1.toLowerCase();
        System.out.println(result2);
        System.out.println(publicAddress);
        System.out.println(publicAddress.toLowerCase().equals(result2));

//        String address = getAddressUsedToSignHashedMessage(signature, Numeric.toHexString(retval), messageBytes);
//        System.out.println(publicAddress.equals(address));
//        System.out.println(publicAddress);
//        System.out.println(address);

        return "test";
    }

    /**
     * This method is the reverse of the signing process.
     *
     * @author djma
     * @see <a href="https://gist.github.com/djma/386c2dcf91fefc004b14e5044facd3a9">Github url</a>
     *
     * @param signedMessageInHex
     *                           The signature in hex format. It is 65 bytes long,
     *                           32 bytes for r, 32 bytes for s, and 1 byte for v.
     *                           May or may not be pre-pended with "0x".
     * @param originalMessage
     *                           The original message that was signed. Not hashed.
     * @return
     *         The address that was used to sign the message.
     * @throws SignatureException
     */
    public static String getAddressUsedToSignHashedMessage(String signedMessageInHex, String originalMessage)
            throws SignatureException {
        if (signedMessageInHex.startsWith("0x")) {
            signedMessageInHex = signedMessageInHex.substring(2);
        }

        // No need to prepend these strings with 0x because
        // Numeric.hexStringToByteArray() accepts both formats
        String r = signedMessageInHex.substring(0, 64);
        String s = signedMessageInHex.substring(64, 128);
        String v = signedMessageInHex.substring(128, 130);

        // Using Sign.signedPrefixedMessageToKey for EIP-712 compliant signatures.
        String pubkey = Sign.signedPrefixedMessageToKey(originalMessage.getBytes(),
                        new Sign.SignatureData(
                                Numeric.hexStringToByteArray(v)[0],
                                Numeric.hexStringToByteArray(r),
                                Numeric.hexStringToByteArray(s)))
                .toString(16);

        return Keys.getAddress(pubkey);
    }
}
