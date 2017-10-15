package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class NamedConst extends UnsignedConstant {

    String name;
    PascalDecl  ref;

    public NamedConst(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("namedconstant");
    }

    @Override
    public void prettyPrint() {
        Main.log.prettyPrint(name);
    }

    /**
    *   Saves pointer to refrenceDecl.
    *   Saves type of refrenceDecl.
    */
    @Override
    public void check(Block curScope, Library lib) {
        ref = curScope.findDecl(name, this);

        if( !(ref instanceof ParamDecl) ) {
            ConstDecl c = (ConstDecl)ref;
            img = c.con.uConst.img;
        }

        type = ref.type;
    }

    /* Lets refrence generate code */
    @Override
    public void genCode(CodeFile f){
        ref.genCode(f);
    }

    @Override
    public int getImgAsInt() {
        if( ref instanceof ConstDecl ) {
            ConstDecl c = (ConstDecl)ref;
            return c.con.uConst.getImgAsInt();
        }
        Main.panic("Panic! Cannot return value of !");
        return 0;
    }

    public static NamedConst parse(Scanner s) {
        enterParser("named constant");

        NamedConst nc = new NamedConst(s.curLineNum());
        nc.name = s.curToken.id;
        s.skip(nameToken);

        leaveParser("named constant");
        return nc;
    }

}
