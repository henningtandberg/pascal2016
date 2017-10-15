package parser;

import main.*;
import scanner.*;
import static scanner.TokenKind.*;

class AssignStatm extends Statement {

    Variable    var;
    Expression  expr;

    public AssignStatm(int lNum) {
        super(lNum);
    }

    @Override
    public String identify() {
        return identify("assign-statment ");
    }

    @Override
    public void prettyPrint() {
        var.prettyPrint();
        Main.log.prettyPrint(" := ");
        expr.prettyPrint();
    }

    /*
    *   Checks that expr and var are of the same type.
    *   Uses correctType() in case of arrayType.
    */
    @Override
    public void check(Block curScope, Library lib) {
        var.check(curScope, lib);
        var.vRef.checkWhetherAssignable(this);
        expr.check(curScope, lib);

        types.Type vTest, eTest;
        vTest = correctType(var.type);
        eTest = correctType(expr.type);

        vTest.checkType(eTest, ":=", this,
        "Assigning a " + eTest.identify() + " to a " +
        vTest.identify() + " is not allowed!");
    }

    /* Generates expression code first then checks the type
    * of the variable to assign to. And generates the correct code
    */
    @Override
    public void genCode(CodeFile f) {
        expr.genCode(f);

        if ( var.vRef instanceof FuncDecl ) {
            f.genInstr("", "movl", (-4*(var.vRef.declLevel))+"(%ebp),%edx", "");
            f.genInstr("", "movl", "%eax,-32(%edx)", var.name + " :=");
        } else if ( var.expr != null ) {
            f.genInstr("", "pushl","%eax", "");
            var.expr.genCode(f);

            types.ArrayType tmp = (types.ArrayType)var.vRef.type;
            if( tmp.loLim > 0 )
            { f.genInstr("", "subl", "$"+tmp.loLim+",%eax", ""); }
            f.genInstr("", "movl", (-4*var.vRef.declLevel)+"(%ebp),%edx", "");
            f.genInstr("", "leal", var.vRef.declOffset+"(%edx),%edx", "");
            f.genInstr("", "popl", "%ecx", "");
            f.genInstr("", "movl", "%ecx,(%edx,%eax,4)", "");
        } else {
            f.genInstr("", "movl", (-4*var.vRef.declLevel)+"(%ebp),%edx", "");
            f.genInstr("", "movl", "%eax,"+var.vRef.declOffset+"(%edx)", var.name + " :=");
        }
    }

    public static AssignStatm parse(Scanner s) {
        enterParser("assign-statment");

        AssignStatm as = new AssignStatm( s.curLineNum() );
        as.var  = Variable.parse(s);
        /* leftBracketToken gets handled in Variable */
        s.skip(assignToken);
        as.expr = Expression.parse(s);

        leaveParser("assign-statment");
        return as;
    }

}
