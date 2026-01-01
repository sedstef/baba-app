package at.fhj.softsec.baba.domain.audit;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static java.lang.String.format;

public class AuditInvocationHandler implements InvocationHandler {

    private final Object target;
    private final Path auditlog;

    public AuditInvocationHandler(Object target, Path auditlog) {
        this.target = target;
        this.auditlog = auditlog;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Object result = method.invoke(target, args);
            writeAudit(method, args);
            return result;
        } catch (Throwable t) {
            throw t.getCause();
        }
    }

    private void writeAudit(Method method, Object[] args) {
        try {
            Files.writeString(auditlog, format(
                    "method=%s args=%s%n",
                    method.getName(),
                    Arrays.toString(args)
            ), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


}
