package org.valkyrja2.component.idempotent;

import org.springframework.core.NamedThreadLocal;
import org.valkyrja2.component.idempotent.exception.IdempotentException;

import java.util.concurrent.TimeUnit;

/**
 * Request Token Idempotent Object
 *
 * @author Tequila
 * @create 2022/08/05 22:46
 **/
public class RtiO {

    private static NamedThreadLocal<Idempotent> sessionIdempotent = new NamedThreadLocal<>("RequestTokenIdempotent");


}
