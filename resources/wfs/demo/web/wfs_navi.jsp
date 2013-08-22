<%-- $HeadURL: https://scm.wald.intevation.org/svn/deegree/base/trunk/resources/wfs/demo/web/wfs_navi.jsp $ --%>
<%-- $Id: wfs_navi.jsp 21330 2009-12-09 08:18:21Z sgoerke $ --%>
<%-- 
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> 
<%@ page import="org.deegree.framework.version.*" %>
<%
    String docPath = "http://download.deegree.org/deegree" + Version.getVersionNumber() + "/docs/wfs";
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>deegree featureService</title>
        <link rel="stylesheet" type="text/css" href="css/deegree.css" />
    </head>
    <body>
        <table width="100%" border="1" cellspacing="0" cellpadding="3">
            <tr>
                <th>deegree Web&nbsp;Feature&nbsp;Service</th>
            </tr>
            <tr>
                <td class="menu">
                    <a href="wfs_ftList.jsp" target="main">List of WFS FeatureTypes</a><br />
                    <a href="wfs_basic.html" target="main">Basic WFS Requests</a><br />
                    <a href="wfs_getfeature.html" target="main">WFS GetFeature Requests</a><br />
                    <a href="wfs_transaction.html" target="main">WFS-T Requests</a><br />
                    <a href="client/client.html" target="main">Generic Client</a>
                </td>
            </tr>
        </table>
        <br />
        <table width="100%" border="1" cellspacing="0" cellpadding="3">
            <tr>
                <th>Documentation online</th>
            </tr>
            <tr>
                <td class="menu">
                    <strong>WFS</strong><br />
                    <a href="<%=docPath %>/deegree_wfs_documentation_en.pdf" target="_blank">deegree WFS</a> [PDF]<br />
                    <a href="<%=docPath %>/README.txt" target="main">readme</a> [TXT]<br />
                </td>
            </tr>
        </table>
        <br/>
        <table width="100%" border="1" cellspacing="0" cellpadding="3">
            <tr>
                <th>deegree online</th>
            </tr>
            <tr>
                <td class="menu">
                    <a href="http://deegree.org/" target="_blank">Home&nbsp;Page</a><br />
                    <a href="http://wiki.deegree.org" target="_blank">Wiki</a><br />
                    <a href="http://demo.deegree.org" target="_blank">Demo Installation</a><br />
                    <a href="http://wald.intevation.org/tracker/?group_id=27" target="_blank">Issue Tracker</a><br />
                    <a href="https://lists.sourceforge.net/lists/listinfo/deegree-users" target="_blank">Users&nbsp;Mailing&nbsp;List</a><br />
                    <a href="https://lists.sourceforge.net/lists/listinfo/deegree-devel" target="_blank">Developers&nbsp;Mailing&nbsp;List</a>
                </td>
            </tr>
        </table>
        <br/>
        <table width="100%" border="1" cellspacing="0" cellpadding="3">
            <tr>
                <th>HOME</th>
            </tr>
            <tr>
                <td>
                    <a href="wfs.jsp" target="_top">WFS home page</a><br />
                    <a href="/${webapp.deploy.context}" target="_top">Start Page</a>
                </td>
            </tr>
        </table>
    </body>
</html>