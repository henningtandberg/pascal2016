package parser;

import main.*;
import types.*;
import scanner.*;
import static scanner.TokenKind.*;

public class NameType extends Type {

    String      name;

    public NameType(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("type name");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(name);
    }

    @Override // Saves reference to decl saves type
    public void check(Block curScope, Library lib) {
        ref = curScope.findDecl(name, this);

        if( ref instanceof Program || ref instanceof ProcDecl )
            Main.error(ref.identify() + " should not be assigned as type!");
        else
            type = ref.type;
    }

    @Override
    public void genCode(CodeFile f){}

    public static NameType parse(Scanner s) {
        enterParser("type name");

        NameType nt = new NameType( s.curLineNum() );
        nt.name = s.curToken.id;
        s.skip(nameToken);

        leaveParser("type name");
        return nt;
    }
}
