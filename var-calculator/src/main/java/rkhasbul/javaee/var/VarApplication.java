package rkhasbul.javaee.var;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * VaR REST Application
 *
 * @author Ruslan Khasbulatov
 * @version 1.0
 */
@ApplicationPath("/rest")
public class VarApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(VarCalculator.class);
        return classes;
    }

}