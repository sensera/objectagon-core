package org.objectagon.core.developer;

import com.google.common.reflect.ClassPath;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Uploads new version
 */
@Mojo(name = "ObjectagonDeveloper",
        defaultPhase = LifecyclePhase.INSTALL

)
public class ObjectagonDeveloperMojo extends AbstractMojo {

    @Parameter(property = "url", defaultValue = "http://localhost:9900")
    private String url;

    @Override public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( "Begin scan packages");
        try {
            final File rootDir = new File("target/classes");
            if (!rootDir.exists()) { throw new IllegalStateException("Does not exist."); }
            getLog().info( "rootDir: "+rootDir.getAbsolutePath());
            final URL targetClasses = new URL("file", null, rootDir.getAbsolutePath()+"/");
            final URLClassLoader classLoader = new URLClassLoader(new URL[]{targetClasses});


                    ClassPath.from(classLoader)
                    .getAllClasses().asList().stream()
                    .filter(classInfo -> classInfo.getName().contains("objectagon"))
                    //.filter(classInfo -> classInfo.getName().contains("examples"))
                    .peek(classInfo -> getLog().info("Examining class "+classInfo.getName() ))
                    .filter(classInfo -> {
                        try {
                            getLog().info("Try load class "+classInfo.getName());
                            final Class<?> aClass = classLoader.loadClass(classInfo.getName());
                            getLog().info("class loaded"+classInfo.getName());
                            //final Class<?> aClass = classInfo.load();
                            return aClass.isAnnotationPresent(ObjectagonMeta.class)
                                    || aClass.isAnnotationPresent(ObjectagonClass.class);
                        } catch (NullPointerException e) {
                            System.out.println("ObjectagonDeveloperMojo.execute Nullpointer for class "+classInfo.getName());
                            e.printStackTrace();
                            return false;
                        } catch (IllegalStateException e) {
                            System.out.println("ObjectagonDeveloperMojo.execute Illegal state "+e.getMessage());
                            e.printStackTrace();
                            return false;
                        } catch (ClassNotFoundException e) {
                            System.out.println("ObjectagonDeveloperMojo.execute class not found "+e.getMessage());
                            e.printStackTrace();
                            return false;
                        }
                    })
                    .peek(classInfo -> getLog().info("Found class "+classInfo.getName() ))
                    .forEach(classInfo -> {

                    });


        } catch (IOException e) {
            throw new MojoExecutionException("Failed to scan packages!", e);
        }
        getLog().info( "Execute Objectagon at url "+url);
    }
}
