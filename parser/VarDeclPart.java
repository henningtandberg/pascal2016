package parser;

import main.*;
import types.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class VarDeclPart extends PascalSyntax {
    ArrayList<VarDecl> varDecl;
    int declBytes;

    public VarDeclPart(int lNum) {
        super(lNum);
        varDecl = new ArrayList<VarDecl>();
        declBytes = 0;
    }

    @Override
    public String identify() {
        return identify("vardeclpart");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrintLn("var");
        Main.log.prettyIndent();

        for(VarDecl vd : varDecl)
            vd.prettyPrint();

        Main.log.prettyOutdent();
    }

    /* Calculates amout of bytes after checking */
    @Override
    public void check(Block curScope, Library lib) {
        for( VarDecl vd : varDecl ) {
            vd.check(curScope, lib);
            vd.declOffset = (-32 - (declBytes += vd.type.size()) );
        }
    }

    @Override
    public void genCode(CodeFile f){}

    /* Returns amount of bytes declared */
    public int declearedBytes() {
        return declBytes;
    }

    public static VarDeclPart parse(Scanner s) {
        enterParser("vardeclpart");
        s.skip(varToken);
        s.test(nameToken);

        VarDeclPart vdp = new VarDeclPart(s.curLineNum());
        while( s.curToken.kind == nameToken )
            vdp.varDecl.add(VarDecl.parse(s));

        leaveParser("vardeclpart");
        return vdp;
    }
}
