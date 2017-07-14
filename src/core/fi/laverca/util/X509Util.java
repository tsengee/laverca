/* ==========================================
 * Laverca Project
 * https://sourceforge.net/projects/laverca/
 * ==========================================
 * Copyright 2015 Laverca Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fi.laverca.util;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A collection of helper methods for commonplace X509 tasks.
 */
public class X509Util {
    private static final Log log = LogFactory.getLog(X509Util.class);

    /**
     * Convert a DER certificate to X509Certificate
     * @param der Certificate to convert
     * @return Converted certificate as X509Certificate or null if the conversion failed
     */
    public static X509Certificate DERtoX509Certificate(final byte[] der) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(der);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate)cf.generateCertificate(bis);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Convert an X509 certificate to byte[] DER
     * @param cert Certificate to convert
     * @return Converted certificate as byte[] DER or null if the conversion failed
     */
    public static byte[] X509CertificateToDER(final X509Certificate cert) {
        try {
            return cert.getEncoded();
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    
    /** 
     * Calculates a SHA-1 hash of the given byte[] certificate
     * @param cert Certificate as byte[]
     * @return SHA-1 hash of the cert  or null if the calculation failed
     */
    public static byte[] certHash(final byte[] cert) {
        if(cert == null) {
            return null;
        }
        
        byte[] hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            hash = md.digest(cert);
        } catch (Throwable t) {
            // never happens
        }
        
        return hash;
    }
    
    /**
     * Parse the CN part from the given certificate's Subject
     * @param cert Certificate
     * @return CN as String
     */
    public static String parseSubjectCn(final X509Certificate cert) {
        return parseSubjectName(cert, "CN");
    }

    /**
     * Parse the given RND type from the given certificate's subject
     * @param cert Certificate
     * @param rdnType RND type
     * @return parsed value as String
     */
    public static String parseSubjectName(final X509Certificate cert, final String rdnType) {
        String dn = cert.getSubjectX500Principal().getName();
    
        String name = null;
        try {
            LdapName ldapDn = new LdapName(dn);
            List<Rdn> rdns = ldapDn.getRdns();
            for(Rdn r : rdns) {
                if(rdnType.equals(r.getType())) {
                    name = r.getValue().toString();
                }
            }
        } catch(InvalidNameException e) {
            log.error(e);
        }
        
        return name;
    }
    
    /**
     * Convert the X509Certificate boolean[] to List<String>
     * 
     * The following list shows the String vs the corresponding boolean[] index:
     * <pre>
     *     digitalSignature        (0),
     *     nonRepudiation          (1),
     *     keyEncipherment         (2),
     *     dataEncipherment        (3),
     *     keyAgreement            (4),
     *     keyCertSign             (5),
     *     cRLSign                 (6),
     *     encipherOnly            (7),
     *     decipherOnly            (8)
     * </pre>
     * @param keyUsage KeyUsage boolean[]
     * @return KeyUsage List<String>
     */
   public static List<String> keyUsageToString(final boolean[] keyUsage) {
       
       List<String> str = new ArrayList<>();
       
       if (keyUsage != null && keyUsage.length >= 9) {
           if (keyUsage[0]) str.add("digitalSignature");
           if (keyUsage[1]) str.add("nonRepudiation");
           if (keyUsage[2]) str.add("keyEncipherment");
           if (keyUsage[3]) str.add("dataEncipherment");
           if (keyUsage[4]) str.add("keyAgreement");
           if (keyUsage[5]) str.add("keyCertSign");
           if (keyUsage[6]) str.add("cRLSign");
           if (keyUsage[7]) str.add("encipherOnly");
           if (keyUsage[8]) str.add("decipherOnly");
       }
       
       return str;
   }

}
