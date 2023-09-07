package com.udtrucks.adaptor;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarVerifier {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("JarVerifier");
    public static boolean verifyJar(String jarFilePath) throws IOException {
        File jarFile;
        JarFile jar = null;
        try {
            jarFile = new File(jarFilePath);
            jar = new JarFile(jarFile);
            Manifest manifest = jar.getManifest();

            // Check the manifest file to make sure that it contains the following attributes:
            // * Manifest-Version
            // * Created-By
            // * Main-Class
            if (manifest.getMainAttributes().getValue("Implementation-Version") != "1.0") {
                throw new RuntimeException("Manifest file is missing the Implementation-Version attribute");
            }
            if (manifest.getMainAttributes().getValue("Specification-Vendor") != "LumenData") {
                throw new RuntimeException("Manifest file is missing the Specification-Vendor attribute");
            }
            if (manifest.getMainAttributes().getValue("Implementation-Title") != "udtrucks") {
                throw new RuntimeException("Manifest file is missing the Specification-Vendor attribute");
            }

            // Verify the signature of the JAR file
            String command = "jarsigner -verify " + jarFile.getAbsolutePath();
            Process process = Runtime.getRuntime().exec(command);
            try {
                if (process.waitFor() != 0) {
                    throw new RuntimeException("JAR file is not signed or has been tampered with");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            LOGGER.info("JAR file is verified");
            return true;
        } catch (IOException e) {
            throw new RuntimeException("JAR file is not Found");
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                    LOGGER.error("Unable to close bufReader because:\n" + e.getMessage());
                }
            }
        }
    }

}
