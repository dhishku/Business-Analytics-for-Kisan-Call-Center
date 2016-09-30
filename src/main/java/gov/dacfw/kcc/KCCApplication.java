package gov.dacfw.kcc;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("")
public class KCCApplication extends Application {
 @Override
 public Set<Class<?>> getClasses() {
  Set<Class<?>> classes = new HashSet<Class<?>>();
  classes.add(gov.dacfw.kcc.resources.KCCResource.class);
  return classes;
 }
}