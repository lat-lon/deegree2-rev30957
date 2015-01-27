//$HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/src/org/deegree/enterprise/control/ajax/JSONEvent.java $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.enterprise.control.ajax;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.FileUtils;
import org.stringtree.json.JSONReader;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: jmays $
 * 
 * @version. $Revision: 23820 $, $Date: 2010-04-26 08:17:25 +0200 (Mo, 26. Apr 2010) $
 */
public class JSONEvent extends WebEvent {

    private static final String PARAMETER_DECODE = "parametersToDecodeAsUri";

    private static final long serialVersionUID = 6459849162427895987L;

    private static final ILogger LOG = LoggerFactory.getLogger( JSONEvent.class );

    private Map<String, Object> json;

    /**
     * 
     * @param servletContext
     * @param request
     * @throws ServletException
     */
    @SuppressWarnings("unchecked")
    JSONEvent( ServletContext servletContext, HttpServletRequest request ) throws ServletException {
        super( servletContext, request, null );
        JSONReader reader = new JSONReader();
        String string = null;
        String characterEncoding = detectEncoding( request );
        try {
            InputStreamReader isr = new InputStreamReader( request.getInputStream(), characterEncoding );

            string = FileUtils.readTextFile( isr ).toString();
        } catch ( IOException e ) {
            LOG.logError( e.getMessage(), e );
            throw new ServletException( "can not parse json: " + json, e );
        }
        json = (Map<String, Object>) reader.read( string );
        decodeParameters( characterEncoding );
        LOG.logDebug( "request parameter: " + json );
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getParameter() {
        return json;
    }

    /**
     * 
     * @param bean
     */
    void setBean( String bean ) {
        this.bean = bean;
    }

    @Override
    public Object getAsBean()
                            throws Exception {
        Class<?> clzz = Class.forName( bean );
        Object bean = clzz.newInstance();
        Method[] methods = clzz.getMethods();
        for ( Method method : methods ) {
            if ( method.getName().startsWith( "set" ) ) {
                String var = method.getName().substring( 4, method.getName().length() );
                var = method.getName().substring( 3, 4 ).toLowerCase() + var;
                Object val = json.get( var );
                Type type = method.getGenericParameterTypes()[0];
                if ( val != null ) {
                    method.invoke( bean, ( (Class<?>) type ).cast( val ) );
                }
            }
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private void decodeParameters( String characterEncoding ) {
        if ( isInMap( PARAMETER_DECODE ) ) {
            for ( String paramToDecode : (List<String>) json.get( PARAMETER_DECODE ) ) {
                if ( isInMap( paramToDecode ) ) {
                    try {
                        String valueToDecode = (String) json.get( paramToDecode );
                        String decodedValue = URLDecoder.decode( valueToDecode, characterEncoding );
                        json.put( paramToDecode, decodedValue );
                    } catch ( UnsupportedEncodingException e ) {
                        LOG.logWarning( "Decoding failed: " + e.getMessage() );
                    }
                }
            }
        }
    }

    private boolean isInMap( String key ) {
        return json.containsKey( key ) && json.get( key ) != null;
    }

    private String detectEncoding( HttpServletRequest request ) {
        String characterEncoding = request.getCharacterEncoding();
        LOG.logDebug( "request character encoding: " + characterEncoding );
        if ( characterEncoding != null )
            return characterEncoding;
        // always use UTF-8 because XMLHttpRequest normally uses this encoding
        return "UTF-8";
    }

}
