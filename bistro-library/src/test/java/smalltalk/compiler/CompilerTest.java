package smalltalk.compiler;

import org.junit.Test;

/**
 *
 * @author nik
 */
public class CompilerTest {
    
    private static final String SourceFolder = "src/main/bistro";
    private static final String TargetFolder = "../libs-smalltalk/src/main/java";
    private static final String ClassFolder  = "../libs-smalltalk/target/classes";
    
    @Test
    public void compileCode() throws Exception {
        String[] packageNames = { 
            "smalltalk.*",
            "org.ansi.smalltalk.*", 
            "smalltalk.behavior.*", 
            "smalltalk.magnitude.*", 
            "smalltalk.collection.*", 
            "smalltalk.stream.*", 
            "smalltalk.geometry.*", 
            "smalltalk.example.*", 
        };
        
        for (String packageName : packageNames) {
            compilePackage(packageName);
        }
    }
    
    private void compilePackage(String packageName) throws Exception {
        String[] args = { SourceFolder, TargetFolder, ClassFolder, packageName};
        BistroCompiler.main(args);
    }

}
