package parser;

import main.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class ConstDeclPart extends PascalSyntax {
    ArrayList<ConstDecl> cdList;

    public ConstDeclPart(int lNum) {
        super(lNum);
        cdList = new ArrayList<ConstDecl>();
    }

    @Override
    public String identify() {
        return identify("constdeclpart");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrintLn("const");
        Main.log.prettyIndent();

        for(ConstDecl cd : cdList)
            cd.prettyPrint();

        Main.log.prettyOutdent();
    }

    @Override
    public void check(Block curScope, Library lib) {
        for( ConstDecl cd : cdList )
            cd.check(curScope, lib);
    }

    @Override
    public void genCode(CodeFile f){}

    public static ConstDeclPart parse(Scanner s) {
        enterParser("constdeclpart");
        s.skip(constToken);
        s.test(nameToken);

        ConstDeclPart cdp = new ConstDeclPart(s.curLineNum());
        while(s.curToken.kind == nameToken)
            cdp.cdList.add(ConstDecl.parse(s));

        leaveParser("constdeclpart");
        return cdp;
    }
}
