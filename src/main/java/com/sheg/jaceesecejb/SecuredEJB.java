package com.sheg.jaceesecejb;

import javax.ejb.Remote;

public interface SecuredEJB {

    String getSecurityInformation();

    boolean administrativeMethod();

}
