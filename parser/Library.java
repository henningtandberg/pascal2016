package parser;

import main.*;
import types.*;

public class Library extends Block {

    /*
    *   Type    boolean
    *   Type    integer
    *   Type    character
    *   Const   eol
    *   Const   true
    *   Const   false
    *   Proc    write
    */

    types.Type  booleanType,
                integerType,
                characterType;

    /*
    *   Creates all predeclerations.
    */
    public Library() {
        super(0);

        booleanType = new types.BoolType();
        integerType = new types.IntType();
        characterType = new types.CharType();

        addDecl( "boolean", new TypeDecl("boolean", -1) );
        addDecl( "integer", new TypeDecl("integer", -1) );
        addDecl( "char", new TypeDecl("char", -1) );
        addDecl( "write", new ProcDecl("write", -1) );
        addDecl( "false", createConstDecl("false") );
        addDecl( "true", createConstDecl("true") );
        addDecl( "eol", createConstDecl("eol") );

        decls.get( "boolean" ).type = booleanType;
        decls.get( "integer" ).type = integerType;
        decls.get( "char" ).type = characterType;
        decls.get( "eol" ).type = characterType;
        decls.get( "true" ).type = booleanType;
        decls.get( "false" ).type = booleanType;
    }

    /* Blocklevel 0 ... */
    @Override
    public int blockLevel() {
        return 0;
    }

    /* Initis the constants eol, true and false with values */
    private ConstDecl createConstDecl(String s) {
        ConstDecl ret = new ConstDecl(s, -1);
        ret.con = new Constant(-1);

        if( s.equals("eol") )
            (ret.con.uConst = new NumberLiteral(-1)).img = "10";
        else if( s.equals("true") )
            (ret.con.uConst = new NumberLiteral(-1)).img = "1";
        else if( s.equals("false") )
            (ret.con.uConst = new NumberLiteral(-1)).img = "0";

        return ret;
    }

}
