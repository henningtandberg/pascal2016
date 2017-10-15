package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

public class ArrayType extends Type {

    Type        elmType;
    Type        indType;
    Constant    const1, const2;
    types.Type  arrIndType;
    types.Type  arrElmType;

    public ArrayType(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("array-type");
    }

    @Override
    public void prettyPrint() {
        String c1 = "", c2 = "";
        NamedConst n = null;

        if( const1.uConst instanceof NamedConst ) {
            n = (NamedConst)const1.uConst;
            c1 = n.name;
        } else {
            c1 = const1.uConst.img;
        }
        if( const2.uConst instanceof NamedConst ) {
            n = (NamedConst)const2.uConst;
            c2 = n.name;
        } else {
            c2 = const2.uConst.img;
        }

        Main.log.prettyPrint("array[ ");
        Main.log.prettyPrint(c1 + ".." + c2 + " ] of ");
        elmType.prettyPrint();
    }

    /**
    *   Saves elemet and index type.
    *   Checks that constants are of correct type.
    *   Creates an arrayType
    */
    @Override
    public void check(Block curScope, Library lib) {
        elmType.check(curScope, lib);
        arrElmType = elmType.type;

        const1.check(curScope, lib);
        const2.check(curScope, lib);
        checkConstType(const1.type, const2.type, "range-check");
        arrIndType = const1.type;

        type = new types.ArrayType(arrElmType, arrIndType,
        const1.uConst.getImgAsInt(), const2.uConst.getImgAsInt());
    }

    @Override
    public void genCode(CodeFile f){}

    public static ArrayType parse(Scanner s) {
        enterParser("array-type");

        ArrayType at = new ArrayType( s.curLineNum() );
        s.skip(arrayToken); s.skip(leftBracketToken);
        at.const1 = Constant.parse(s);
        s.skip(rangeToken);
        at.const2 = Constant.parse(s);
        //s.skip(colonToken);
        //at.indType = Type.parse(s);
        s.skip(rightBracketToken); s.skip(ofToken);
        at.elmType = Type.parse(s);

        leaveParser("array-type");
        return at;
    }

    /*
    *   Private
    */

    private void checkConstType(types.Type ct, types.Type it, String test) {
        String err = "Array range constants are of different types!";
        ct.checkType(it, test, this, err);
    }

}
