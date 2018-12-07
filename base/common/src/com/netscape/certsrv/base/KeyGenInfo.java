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
package com.netscape.certsrv.base;

import java.io.IOException;

import com.netscape.cmsutil.util.Utils;

import org.mozilla.jss.netscape.security.util.DerInputStream;
import org.mozilla.jss.netscape.security.util.DerValue;
import org.mozilla.jss.netscape.security.x509.AlgorithmId;
import org.mozilla.jss.netscape.security.x509.X509Key;

/**
 *
 * The <code>KeyGenInfo</code> represents the information generated by
 * the KeyGen tag of the HTML forms. It provides the parsing and accessing
 * mechanisms.
 * <p>
 *
 * <pre>
 * SignedPublicKeyAndChallenge ::= SEQUENCE {
 *      publicKeyAndChallenge PublicKeyAndChallenge,
 *      signatureAlgorithm AlgorithmIdentifier,
 *      signature BIT STRING
 * }
 *
 * PublicKeyAndChallenge ::= SEQUENCE {
 *      spki SubjectPublicKeyInfo,
 *      challenge IA5STRING
 * }
 * </pre>
 *
 *
 * @version $Revision$, $Date$
 */

public class KeyGenInfo {

    /*==========================================================
     * variables
     *==========================================================*/
    private String mSPKACString;
    private byte mPKAC[];
    private byte mSPKAC[];
    private X509Key mSPKI;
    private DerValue mDerSPKI;
    private String mChallenge;
    private DerValue mDerChallenge;
    private byte mSignature[];
    private AlgorithmId mAlgId;

    /*==========================================================
     * constructors
     *==========================================================*/

    /**
     * Construct empty KeyGenInfo. Need to call decode function
     * later to initialize.
     */
    public KeyGenInfo() {

    }

    /**
     * Construct KeyGenInfo using the SignedPublicKeyAndChallenge
     * string representation.
     *
     * @param spkac SignedPublicKeyAndChallenge string representation
     */
    public KeyGenInfo(String spkac)
            throws IOException {
        decode(spkac);
    }

    /*==========================================================
     * public methods
     *==========================================================*/

    /**
     * Initialize using the SPKAC string
     *
     * @param spkac SPKAC string from the end user
     */
    public void decode(String spkac) throws IOException {
        mSPKACString = spkac;
        mSPKAC = base64Decode(spkac);
        derDecode(mSPKAC);
    }

    /**
     * Der encoded into buffer
     *
     * @return Der encoded buffer
     */
    public byte[] encode() {
        return mSPKAC;
    }

    /**
     * Get SPKI in DerValue form
     *
     * @return SPKI in DerValue form
     */
    public DerValue getDerSPKI() {
        return mDerSPKI;
    }

    /**
     * Get SPKI as X509Key
     *
     * @return SPKI in X509Key form
     */
    public X509Key getSPKI() {
        return mSPKI;
    }

    /**
     * Get Challenge phrase in DerValue form
     *
     * @return Challenge in DerValue form. null if none.
     */
    public DerValue getDerChallenge() {
        return mDerChallenge;
    }

    /**
     * Get Challenge phrase in string format
     *
     * @return challenge phrase. null if none.
     */
    public String getChallenge() {
        return mChallenge;
    }

    /**
     * Get Signature
     *
     * @return signature
     */
    public byte[] getSignature() {
        return mSignature;
    }

    /**
     * Get Algorithm ID
     *
     * @return the algorithm id
     */
    public AlgorithmId getAlgorithmId() {
        return mAlgId;
    }

    /**
     * Validate Signature and Challenge Phrase
     *
     * @param challenge phrase; null if none
     * @return true if validated; otherwise, false
     */
    public boolean validateChallenge(String challenge) {
        if (challenge != null) {
            if (!challenge.equals(mChallenge)) {
                return false;
            }
        }
        return true;
    }

    /**
     * String representation of KenGenInfo
     *
     * @return string representation of KeGenInfo
     */
    public String toString() {
        if (mSPKACString != null)
            return mSPKACString;
        return "";
    }

    /*==========================================================
     * private methods
     *==========================================================*/

    private byte[] base64Decode(String spkac)
            throws IOException {

        return Utils.base64decode(spkac);
    }

    private void derDecode(byte spkac[])
            throws IOException {
        DerInputStream derIn = new DerInputStream(spkac);

        /* get SPKAC Algorithm & Signature */
        DerValue derSPKACContent[] = derIn.getSequence(3);

        mAlgId = AlgorithmId.parse(derSPKACContent[1]);
        mSignature = derSPKACContent[2].getBitString();

        /* get PKAC SPKI & Challenge */
        mPKAC = derSPKACContent[0].toByteArray();
        derIn = new DerInputStream(mPKAC);
        DerValue derPKACContent[] = derIn.getSequence(2);

        mDerSPKI = derPKACContent[0];
        mSPKI = X509Key.parse(derPKACContent[0]);

        mDerChallenge = derPKACContent[1];
        if (mDerChallenge.length() != 0)
            mChallenge = derPKACContent[1].getIA5String();

    }

}
