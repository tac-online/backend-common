package de.johanneswirth.tac.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private static String publicKey;

    /**
     * Constructor
     */
    private Utils() {

    }

    public static void init(String publicKey) {
        Utils.publicKey = publicKey;
    }

    public static RSAPublicKey loadPublic(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(publicKey));
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
    }

    public static Optional<String> validateJWT(String ticket) {
        LOGGER.info("Trying to authorize with ticket " + ticket);
        RSAPublicKey publicKey = null;
        try {
            publicKey = Utils.loadPublic(Utils.publicKey);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            LOGGER.error("Uncaught Exception", e);
        }
        try {

            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("tac-server")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(ticket);
            return Optional.of(jwt.getSubject());
        } catch (JWTVerificationException exception){
            return Optional.empty();
        }
    }
}
