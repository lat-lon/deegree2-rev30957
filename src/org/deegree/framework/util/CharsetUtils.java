//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/framework/util/CharsetUtils.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.framework.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.LinkedList;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * 
 * 
 * @version $Revision: 18195 $
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version 1.0. $Revision: 18195 $, $Date: 2009-06-18 17:55:39 +0200 (Do, 18. Jun 2009) $
 * 
 * @since 2.0
 */
public final class CharsetUtils {

    private static ILogger LOG = LoggerFactory.getLogger( CharsetUtils.class );

    private static final String DEFAULT_CHARSET = "UTF-8";

    private CharsetUtils() {
        // nottin
    }

    /**
     * returns the name of the charset that is passed to the JVM as system property -DCHARSET=... If no charset has been
     * defined UTF-8 will be returned as default.
     * 
     * @return the name of the charset that is passed to the JVM as system property -DCHARSET=... If no charset has been
     *         defined UTF-8 will be returned as default.
     */
    public static String getSystemCharset() {
        String charset = null;
        try {
            charset = System.getProperty( "CHARSET" );
        } catch ( Exception exc ) {
            LOG.logError( "Error retrieving system property CHARSET", exc );
        }
        if ( charset == null ) {
            charset = DEFAULT_CHARSET;
        }
        LOG.logDebug( "Using system charset: " + charset );
        return charset;
    }

    /**
     * A method to convert an input string from a given charset to UTF-8 by using
     * {@link java.nio.charset.CharsetEncoder}
     * 
     * @param input
     * @param inCharset
     * @return the charset encoded String
     */
    public static String convertToUnicode( String input, String inCharset ) {
        // Create the encoder and decoder for inCharset
        Charset charset = Charset.forName( inCharset );
        CharsetEncoder encoder = charset.newEncoder();

        ByteBuffer bbuf = null;
        try {
            // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
            // The new ByteBuffer is ready to be read.
            bbuf = encoder.encode( CharBuffer.wrap( input ) );

        } catch ( CharacterCodingException e ) {
            LOG.logError( e.getMessage(), e );
        }
        return bbuf.toString();
    }

    /**
     * A method to convert an input string in UTF-8 to a given charset by using {@link java.nio.charset.CharsetDecoder}
     * 
     * @param input
     * @param targetCharset
     * @return the decoded string
     */
    public static String convertFromUnicode( String input, String targetCharset ) {
        // Create the encoder and decoder for inCharset
        Charset charset = Charset.forName( targetCharset );
        CharsetDecoder decoder = charset.newDecoder();

        CharBuffer cbuf = null;
        try {
            // Convert ISO-LATIN-1 bytes in a ByteBuffer to a character ByteBuffer and then to a
            // string.
            // The new ByteBuffer is ready to be read.
            cbuf = decoder.decode( ByteBuffer.wrap( input.getBytes() ) );
        } catch ( CharacterCodingException e ) {
            LOG.logError( e.getMessage(), e );
        }
        return cbuf.toString();
    }

    public static Reader convertToReaderWithDefaultCharset( InputStream stream )
                            throws IOException {
        String capAsString = convertInputStreamToString( stream );
        byte[] capInBytes = capAsString.getBytes( Charset.defaultCharset().displayName() );
        return new InputStreamReader( new ByteArrayInputStream( capInBytes ) );
    }

    private static String convertInputStreamToString( InputStream is )
                            throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream( is, 1024 );
        String encoding = readEncoding( pushbackInputStream );

        LOG.logDebug( "Read stream with encoding " + encoding );

        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader( new InputStreamReader( pushbackInputStream, encoding ) );
            while ( ( line = br.readLine() ) != null ) {
                sb.append( line );
            }
        } finally {
            if ( br != null ) {
                try {
                    br.close();
                } catch ( IOException e ) {
                }
            }
        }
        return sb.toString();
    }

    /**
     * reads the encoding of a XML document from its header. If no header available
     * <code>CharsetUtils.getSystemCharset()</code> will be returned
     * 
     * @param pbis
     * @return encoding of a XML document
     * @throws IOException
     */
    public static String readEncoding( PushbackInputStream pbis )
                            throws IOException {
        byte[] b = new byte[80];
        String s = "";
        int rd = 0;

        LinkedList<byte[]> bs = new LinkedList<byte[]>();
        LinkedList<Integer> rds = new LinkedList<Integer>();
        while ( rd < 80 ) {
            rds.addFirst( pbis.read( b ) );
            if ( rds.peek() == -1 ) {
                rds.poll();
                break;
            }
            rd += rds.peek();
            s += new String( b, 0, rds.peek() ).toLowerCase();
            bs.addFirst( b );
            b = new byte[80];
        }

        String encoding = CharsetUtils.getSystemCharset();
        if ( s.indexOf( "?>" ) > -1 ) {
            int p = s.indexOf( "encoding=" );
            if ( p > -1 ) {
                StringBuffer sb = new StringBuffer();
                int k = p + 1 + "encoding=".length();
                while ( s.charAt( k ) != '"' && s.charAt( k ) != '\'' ) {
                    sb.append( s.charAt( k++ ) );
                }
                encoding = sb.toString();
            }
        }
        while ( !bs.isEmpty() ) {
            pbis.unread( bs.poll(), 0, rds.poll() );
        }

        return encoding;
    }

}
