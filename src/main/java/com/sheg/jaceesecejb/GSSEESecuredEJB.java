package com.sheg.jaceesecejb;

import org.jboss.ejb3.annotation.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import java.security.Principal;

@Stateless
@RolesAllowed({"Programmes"})
@SecurityDomain("GSEESecurityDomain")
@Remote(SecuredEJB.class)
public class GSSEESecuredEJB implements SecuredEJB {

    // Inject the Session Context
    @Resource
    private SessionContext ctx;

    /**
     * Secured EJB method using security annotations
     */
    public String getSecurityInformation() {
        // Session context injected using the resource annotation
        Principal principal = ctx.getCallerPrincipal();
        return principal.toString();
    }

    @RolesAllowed("Admins")
    public boolean administrativeMethod() {
        return true;
    }

}
