package org.objectagon.core.utils;

import org.objectagon.core.exception.ErrorClass;
import org.objectagon.core.exception.ErrorKind;
import org.objectagon.core.msg.Message;
import org.objectagon.core.object.Method;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by christian on 2016-05-29.
 */
public class ObjectagonCompiler<T> {

    private long counter;
    private final JavaCompiler compiler;
    private final File compilePath;
    private final String classpath;
    private final String standardPackage;
    private final List<String> imports;
    private final DiagnosticCollector<JavaFileObject> diagnostics;
    private final StandardJavaFileManager fileManager;

    public ObjectagonCompiler(File compilePath, String classpath, String standardPackage, List<String> imports) {
        this.compilePath = compilePath;
        this.classpath = classpath;
        this.standardPackage = standardPackage;
        this.imports = imports;
        this.compiler = ToolProvider.getSystemJavaCompiler();
        diagnostics = new DiagnosticCollector<>();
        fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    }

    public Class<T> compile(String methodCode) throws IOException, URISyntaxException, ClassNotFoundException {
        String className = "ObjectagonMethod"+counter;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createClassFromMethodCode(new BufferedWriter(new OutputStreamWriter(out)), className, methodCode);
        out.flush();
        out.close();

        ArrayList<String> options = new ArrayList<>();
        if (classpath != null) {
            options.add("-classpath");
            options.add(classpath);
        }
        options.add("-sourcepath");
        options.add(compilePath.getPath());

        options.add("-d");
        options.add(compilePath.getPath());

        final String preparedCode = new String(out.toByteArray());

        List<SimpleJavaFileObject> sourceList = Arrays.asList(
                new SimpleJavaFileObject(new URI(className+".java"), JavaFileObject.Kind.SOURCE) {
                    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
                        return preparedCode;
                    }
                }
        );

        File compileAndPackagePath = new File(compilePath, getStandardPackageAsPath());
        if (!compileAndPackagePath.exists() && !compileAndPackagePath.mkdirs())
            throw new IOException("Failed to create dirs "+compileAndPackagePath.getAbsolutePath());

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, sourceList);

        if (!task.call()) {
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                System.err.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
            }
            throw new IOException("Could not compile project");
        }

        File classFile = new File(compileAndPackagePath, className+".class");
        if (!classFile.exists())
            throw new IOException("Compiled class not found! "+classFile.getAbsolutePath());
        return loadClass(standardPackage+"."+className);
    }

    private String getStandardPackageAsPath() {
        return "/"+standardPackage.replace(".","/");
    }

    private Class<T> loadClass(String className) throws IOException, ClassNotFoundException {
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{this.compilePath.toURI().toURL()},
                Thread.currentThread().getContextClassLoader());
        try {
            return (Class<T>) classLoader.loadClass(className);
        } finally {
            classLoader.close();
        }
    }

    private void createClassFromMethodCode(BufferedWriter writer, String className, String methodCode) throws IOException {
        writer.write("package "+standardPackage+";");
        imports.stream().forEach(s -> {
            try {
                writer.write("import " + s + ";");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.write("public class "+className+" implements Method.Invoke { ");
        writer.write("  public void invoke(Method.InvokeWorker invokeWorker) {");
        writer.write(methodCode);
        writer.write("  }");
        writer.write("}");
        writer.flush();
    }

    public static void main(String[] params) throws IOException, URISyntaxException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        File compilePath = new File("/tmp/objectagoncompiler/");
        if (!compilePath.exists() && !compilePath.mkdirs() )
            throw new IOException("Create dirs '"+compilePath.getAbsolutePath()+"' failed!");
        ObjectagonCompiler<Method.Invoke> objectagonCompiler = new ObjectagonCompiler<>(
                compilePath,
                "/projects/objectagon/objectagon-core/core/target/core-1.0-SNAPSHOT.jar",
                "org.objectagon.core.compiledmethod",
                Arrays.asList("org.objectagon.core.object.Method"));
        Class<Method.Invoke> newClass = objectagonCompiler.compile("invokeWorker.replyOk();");
        Method.Invoke invoke = newClass.newInstance();
        invoke.invoke(new Method.InvokeWorker() {
            @Override
            public List<Method.ParamName> getInvokeParams() {
                return null;
            }

            @Override
            public Message.Value getValue(String name) {
                return null;
            }

            @Override
            public Message.Value getValue(Method.ParamName paramName) {
                return null;
            }

            @Override
            public void replyOk() {
                System.out.println("ObjectagonCompiler.replyOk");
            }

            @Override
            public void replyOkWithParams(Message.Value... values) {

            }

            @Override
            public void failed(ErrorClass errorClass, ErrorKind errorKind, Message.Value... values) {

            }

            @Override
            public <T> Method.ValueCreator<T> setValue(String paramName) {
                return null;
            }

            @Override
            public <T> Method.ValueCreator<T> setValue(Method.ParamName paramName) {
                return null;
            }
        });
        //invoke.invoke(() -> System.out.println("ObjectagonCompiler.replyOk"));
    }
}
