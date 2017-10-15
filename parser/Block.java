package parser;

import main.*;
import scanner.*;
import java.util.HashMap;
import java.util.ArrayList;
import static scanner.TokenKind.*;

public class Block extends PascalSyntax {
    HashMap<String, PascalDecl> decls;
    ArrayList<PascalDecl>       dList;
    ConstDeclPart               cdPart;
    VarDeclPart                 vdPart;
    StatmList                   sList;
    Program                     context;
    Block                       outerScope;
    int                         offset;

    public Block(int lNum) {
        super(lNum);
        decls = new HashMap<>();
        dList = new ArrayList<>();
        offset = 0;
    }

    @Override
    public String identify() {
        return identify("block");
    }

    @Override
    public void prettyPrint() {

        if(cdPart != null)
            cdPart.prettyPrint();

        if(vdPart != null)
            vdPart.prettyPrint();

        for(int i = 0; i < dList.size(); i++) {
            if( dList.get(i) == null ) {}
            else { dList.get(i).prettyPrint(); }
        }

        Main.log.prettyPrintLn("");
        Main.log.prettyPrintLn("begin");
        Main.log.prettyIndent();

        if(sList != null)
            sList.prettyPrint();

        Main.log.prettyOutdent();
        Main.log.prettyPrint("end");
    }

    @Override
    public void check(Block curScope, Library lib) {
        outerScope = curScope; // save pointer to outerscope.
        //offset = outerScope.offset;

        if( cdPart != null ) cdPart.check(this, lib);
        if( vdPart != null ) vdPart.check(this, lib);

        for( int i = 0; i < dList.size(); i++ )
            dList.get(i).check(this, lib);

        //System.out.println( identify() + " offset: " + offset);
        if( sList != null ) sList.check(this, lib);
    }

    /* Generates code for sList if not null */
    @Override
    public void genCode(CodeFile f){
        if( sList != null ) sList.genCode(f);
    }

    /* Separate function for generating func and proc code */
    public void genFuncProcCode(CodeFile f) {
        for( int i = 0; i < dList.size(); i++ )
            dList.get(i).genCode(f);
    }

    /* Returnes the amount of bytes declared in block */
    public int declearedBytes() {
        return (vdPart != null)?
        vdPart.declearedBytes() : 0;
    }

    /* Returns the blockLevel */
    public int blockLevel() {
        return outerScope.blockLevel() + 1;
    }

    /**
    *   Adds a new decleration to decls.
    *   Throws error if decl allready declerated
    *   in same block.
    */
    public void addDecl(String id, PascalDecl d) {
        if( decls.containsKey(id) )
            d.error(id + " declared twice in same block!");
        decls.put(id, d);
    }

    /**
    *   Searches all blocks to find decl.
    *   If decl is not to be found an error
    *   is thrown.
    */
    public PascalDecl findDecl(String id, PascalSyntax where) {
        PascalDecl d = decls.get(id);
        if( d != null ) {
            Main.log.noteBinding(id, where, d);
            return d;
        }

        if( outerScope != null )
            return outerScope.findDecl(id, where);

        where.error("Name " + id + " is unknown!");
        return null;
    }

    public static Block parse(Scanner s){
        enterParser("block");

        Block b = new Block(s.curLineNum());
        while( isConstVarDeclPart(s) ) {
            switch(s.curToken.kind) {
                case constToken:
                    if( b.cdPart == null )
                        b.cdPart =  ConstDeclPart.parse(s);
                    else
                        Main.error("Error at line " + s.curLineNum()
                        + ": constants have allready been decleared!" );
                    break;
                case varToken:
                    if( b.vdPart == null )
                        b.vdPart =  VarDeclPart.parse(s);
                    else
                        Main.error("Error at line " + s.curLineNum()
                        + ": variables have allready been decleared!" );
                    break;
                default:
                    break;
            }
        }

        while( isFuncProcDecl(s) )
            b.dList.add( PascalDecl.parse(s) );

        s.skip(beginToken);
        b.sList = StatmList.parse(s);
        s.skip(endToken);

        leaveParser("block");
        return b;
    }

    /**
        Returns true if next part of Block is
        either vardecl- or constdeclpart.
    */
    private static boolean isConstVarDeclPart(Scanner s) {
        return (s.curToken.kind == varToken
        || s.curToken.kind == constToken);
    }

    /**
        Returns true if next part of Block is
        either funcdecl- or procdeclpart.
    */
    private static boolean isFuncProcDecl(Scanner s) {
        return (s.curToken.kind == functionToken
        || s.curToken.kind == procedureToken );
    }

}
