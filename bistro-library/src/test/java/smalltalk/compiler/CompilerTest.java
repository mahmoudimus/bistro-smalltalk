package smalltalk.compiler;

import org.junit.Test;

/**
 * Compiles the Bistro class library.
 * @author nik
 */
public class CompilerTest {

    private static final String SourceFolder = "src/main/bistro";
    private static final String TargetFolder = "../libs-smalltalk/src/main/java";
    private static final String ClassFolder  = "../libs-smalltalk/target/classes";

    @Test
    public void compileCode() throws Exception {
        String[] runtimeArgs = {
            SourceFolder,
            TargetFolder,
            ClassFolder,

            "smalltalk.*",
        };

        BistroCompiler.main(runtimeArgs);

        String[] smalltalkArgs = {
            SourceFolder,
            TargetFolder,
            ClassFolder,

            "org.ansi.smalltalk.*",
        };

        BistroCompiler.main(smalltalkArgs);

        String[] bistroArgs = {
            SourceFolder,
            TargetFolder,
            ClassFolder,

            "smalltalk.behavior.*",
            "smalltalk.magnitude.*",
            "smalltalk.collection.*",
            "smalltalk.stream.*",
            "smalltalk.geometry.*",
            "smalltalk.example.*",
        };

        BistroCompiler.main(bistroArgs);
    }

}
