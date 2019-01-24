<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<html>
    <head>
       
    </head>
    <body>
        <html:form action="UserAction" >
            <table>
                <tr>
                    <td>
                        <bean:write name="UserForm" property="message" />
                    </td>
                </tr>
                <tr>
                    <td>
                        <html:submit property="method"><bean:message key="UserForm.add"/></html:submit>
                    </td>
                </tr>
                <tr>
                    <td>
                        <html:submit property="method"><bean:message key="UserForm.update"/></html:submit>
                    </td>
                </tr>
                <tr>
                    <td>
                        <html:submit property="method"><bean:message key="UserForm.delete"/></html:submit>
                    </td>
                </tr>
                 <tr>
                    <td>
                        <html:submit property="method"><bean:message key="UserForm.incorrect"/></html:submit>
                    </td>
                </tr>
            </table>
        </html:form>
    </body>
</html>