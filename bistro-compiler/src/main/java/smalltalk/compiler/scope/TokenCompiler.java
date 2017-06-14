package smalltalk.compiler.scope;

import java.io.*;
import org.antlr.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.AutoIndentWriter;
import smalltalk.compiler.Bistro;
import smalltalk.compiler.BistroLex;
import smalltalk.compiler.Emission;
import smalltalk.compiler.element.Scope;

/**
 * Compiles a Bistro file from a token stream with a parser.
 * @author Copyright 1999,2017 Nikolas S. Boyd. All rights reserved.
 */
public class TokenCompiler {

    File tokenFile;
    Bistro parser;
    TokenStream tokenStream;

    public TokenCompiler(File aFile) {
        tokenFile = aFile;
    }

    public TokenStream tokenStream() {
        return tokenStream;
    }

    public boolean wasParsed() {
        return parser != null;
    }

    public boolean notParsed() {
        return parser == null;
    }

    /**
     * Parses the associated tokens and generates code from the resulting AST.
     * @return indicates success or failure
     */
    public boolean compile() {
        java.io.File sourceFile = tokenFile.sourceFile();
        if (sourceFile == null || !sourceFile.exists()) {
            reportMissingSource();
            return false;
        }

        parseTokens();
        if (wasParsed()) {
            emitCode();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Parses the associated tokens if needed.
     */
    public void parseTokens() {
        if (notParsed()) {
            try {
                parser = new Bistro(createTokenStream());
                Scope.current = tokenFile;
                parser.compilationUnit();
            }
            catch (Exception ex) {
                // unlikely given safeguards
            }
        }
   }

    private LegacyCommonTokenStream createTokenStream() throws Exception {
        LegacyCommonTokenStream result = new LegacyCommonTokenStream(createLexer());
        result.discardTokenType(Bistro.WhiteSpaces);
        tokenStream = result;
        return result;
    }

    private TokenSource createLexer() throws Exception {
        return new BistroLex(createInputStream());
    }

    private CharStream createInputStream() throws Exception {
        return new ANTLRFileStream(tokenFile.sourceFile().getAbsolutePath());
    }

    private void emitCode() {
        tokenFile.clean();

        java.io.File targetFolder = tokenFile.facePackage().createTarget();
        if (targetFolder == null) return; // failure already reported

        java.io.File targetFile = new java.io.File(targetFolder, tokenFile.targetFilename());
        try (PrintWriter oStream = new PrintWriter(new FileWriter(targetFile))) {
            Emission scope = tokenFile.emitScope();
            scope.write(new AutoIndentWriter(oStream));
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    private void reportMissingSource() {
        String message = "Can't find source file for " + tokenFile.faceName();
        System.out.println(message);
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}
