// --- BEGIN COPYRIGHT BLOCK ---
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; version 2 of the License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
// (C) 2007 Red Hat, Inc.
// All rights reserved.
// --- END COPYRIGHT BLOCK ---
package com.netscape.kra;

import java.security.PublicKey;

import javax.crypto.spec.IvParameterSpec;

import org.mozilla.jss.crypto.CryptoToken;
import org.mozilla.jss.crypto.EncryptionAlgorithm;
import org.mozilla.jss.crypto.KeyGenAlgorithm;
import org.mozilla.jss.crypto.KeyWrapAlgorithm;
import org.mozilla.jss.crypto.PrivateKey;
import org.mozilla.jss.crypto.SymmetricKey;
import org.mozilla.jss.netscape.security.util.WrappingParams;

import com.netscape.certsrv.base.EBaseException;
import com.netscape.certsrv.security.IEncryptionUnit;
import com.netscape.cmsutil.crypto.CryptoUtil;

/**
 * A class represents the transport key pair. This key pair
 * is used to protected EE's private key in transit.
 *
 * @author thomask
 * @version $Revision$, $Date$
 */
public abstract class EncryptionUnit implements IEncryptionUnit {

    public static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EncryptionUnit.class);

    /* Establish one constant IV for base class, to be used for
       internal operations. Constant IV acceptable for symmetric keys.
    */
    public static final byte[] iv = { 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1 };
    public static final byte[] iv2 = { 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1 };
    public static final IvParameterSpec IV = new IvParameterSpec(iv);
    public static final IvParameterSpec IV2 = new IvParameterSpec(iv2);

    public EncryptionUnit() {
        logger.debug("EncryptionUnit.EncryptionUnit this: " + this);
    }

    public abstract CryptoToken getToken();

    public abstract CryptoToken getToken(org.mozilla.jss.crypto.X509Certificate cert);

    public abstract CryptoToken getInternalToken();

    public abstract PublicKey getPublicKey();

    public abstract PrivateKey getPrivateKey();

    public abstract PrivateKey getPrivateKey(org.mozilla.jss.crypto.X509Certificate cert);

    public WrappingParams getOldWrappingParams() {
        return new WrappingParams(
                SymmetricKey.DES3, KeyGenAlgorithm.DES3, 168,
                KeyWrapAlgorithm.RSA, EncryptionAlgorithm.DES3_CBC_PAD,
                KeyWrapAlgorithm.DES3_CBC_PAD, IV, IV);
    }

    public SymmetricKey unwrap_session_key(CryptoToken token, byte encSymmKey[], SymmetricKey.Usage usage,
            WrappingParams params) throws Exception {
        PrivateKey wrappingKey = getPrivateKey();
        String priKeyAlgo = wrappingKey.getAlgorithm();
        if (priKeyAlgo.equals("EC"))
            params.setSkWrapAlgorithm(KeyWrapAlgorithm.AES_ECB);

        return CryptoUtil.unwrap(
                token,
                params.getSkType(),
                params.getSkType().equals(SymmetricKey.DES3)? 0: params.getSkLength(),
                usage, wrappingKey,
                encSymmKey,
                params.getSkWrapAlgorithm());
    }

    /**
     * Verify the given key pair.
     */
    public void verify(PublicKey publicKey, PrivateKey privateKey) throws
            EBaseException {
    }

}
