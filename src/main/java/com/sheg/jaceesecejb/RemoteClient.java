/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sheg.jaceesecejb;

import org.wildfly.security.auth.client.AuthenticationConfiguration;
import org.wildfly.security.auth.client.AuthenticationContext;
import org.wildfly.security.auth.client.MatchRule;
import org.wildfly.security.sasl.SaslMechanismSelector;

import javax.ejb.EJBAccessException;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;
import java.util.concurrent.Callable;

/**
 * The remote client responsible for making calls to the secured EJB.
 *
 * @author <a href="mailto:sguilhen@redhat.com">Stefan Guilhen</a>
 */
public class RemoteClient {
    public static void main(String[] args) throws Exception {
        AuthenticationConfiguration common = AuthenticationConfiguration.empty().useRealm("GSEERealm")
                .setSaslMechanismSelector(SaslMechanismSelector.NONE.addMechanism("BASIC"));
        AuthenticationConfiguration ejbConfig = common.useName("e.zhi").usePassword("5077de6c68bf5178379c9b4c2a426991");
        AuthenticationContext context = AuthenticationContext.empty().with(MatchRule.ALL, ejbConfig);
        System.out.println(context.runCallable(callable));
    }
    /**
     * A {@code Callable} that looks up the remote EJB and invokes its methods.
     */
    static final Callable<String> callable = () -> {
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        properties.put(Context.PROVIDER_URL, "remote+http://localhost:8080");

        final Context context = new InitialContext(properties);

        SecuredEJB reference = (SecuredEJB) context.lookup("ejb:/jac-ee-sec-ejb-1.0-SNAPSHOT/GSSEESecuredEJB!com.sheg.jaceesecejb.SecuredEJB");

        StringBuilder builder = new StringBuilder();
        builder.append("\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n\n");
        builder.append("Called secured bean, caller principal " + reference.getSecurityInformation());
        boolean hasAdminPermission = false;
        try {
            hasAdminPermission = reference.administrativeMethod();
        } catch (EJBAccessException e){

        }
        builder.append("\n\nPrincipal has admin permission: " + hasAdminPermission);
        builder.append("\n\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\n\n");
        return builder.toString();
    };



}
