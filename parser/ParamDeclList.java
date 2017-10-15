package parser;

import main.*;
import types.*;
import scanner.*;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class ParamDeclList extends PascalSyntax {

    ArrayList<ParamDecl> pDeclList;

    public ParamDeclList(int lNum) {
        super(lNum);
        pDeclList = new ArrayList<>();
    }

    @Override
    public String identify() {
        return identify("paramdecllist");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint("( ");

        for( int i = 0; i < pDeclList.size(); i++ ) {
            pDeclList.get(i).prettyPrint();

            if( i < (pDeclList.size()-1) )
                Main.log.prettyPrint("; ");
            else
                Main.log.prettyPrint(" ");
        }

        Main.log.prettyPrint(" )");
    }

    /* Checks and calculates offset */
    @Override
    public void check(Block curScope, Library lib) {
        int offset = 4;
        for( ParamDecl pd : pDeclList ) {
            pd.check(curScope, lib);
            pd.declOffset = (offset += pd.type.size());
        }
    }

    @Override
    public void genCode(CodeFile f){}

    public static ParamDeclList parse(Scanner s) {
        enterParser("paramdecllist");
        s.skip(leftParToken);
        s.test(nameToken);

        ParamDeclList pdl = new ParamDeclList(s.curLineNum());
        while(s.curToken.kind == nameToken)
            pdl.pDeclList.add(ParamDecl.parse(s));

        s.skip(rightParToken);
        leaveParser("paramdecllist");
        return pdl;
    }

}
