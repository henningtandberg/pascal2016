package scanner;

import main.Main;
import static scanner.TokenKind.*;

import java.io.*;

/**
* Reads new lines from sourcefile and creates tokens.
* @author Dag Langmyr
* @author Henning P. Tandberg (henninpt)
* @version 20.09.16
*/
public class Scanner {
    public Token curToken = null, nextToken = null;

    private LineNumberReader sourceFile = null;
    private String sourceFileName, sourceLine = "";
    private int sourcePos = 0;

    /**
    * Creates a Scanner object
    * Reads opens a soruce file and inits the Scanner.
    *
    * @param fileName   Name/path to source file
    */
    public Scanner(String fileName) {
    	sourceFileName = fileName;
    	try {
    	    sourceFile = new LineNumberReader(new FileReader(fileName));
    	} catch (FileNotFoundException e) {
    	    Main.error("Cannot read " + fileName + "!");
    	}

    	readNextToken();  readNextToken();
    } /* END Constructor */

    /**
    * Identifys the Scanner object.
    *
    * @return   A string that identifys the Scanner.
    * @see      String
    */
    public String identify() {
    	return "Scanner reading " + sourceFileName;
    } /* END identify */

    /**
    * Line number of current token.
    *
    * @return   Returns the line number of curToken.
    */
    public int curLineNum() {
    	return curToken.lineNum;
    } /* END curLineNum */


    private void error(String message) {
    	Main.error("Scanner error on " +
    		   (curLineNum()<0 ? "last line" : "line "+curLineNum()) +
    		   ": " + message);
    } /* END error */

    /**
    * Creates tokens from source file
    * Splits each line from the source file into tokens.
    * Calls readNextLine() if needed and skips spaces and comments.
    */
    public void readNextToken() {
        curToken = nextToken;
        nextToken = null;

        if ( newLineIsNeeded() ) {
            readNextLine();
        }

        skipSpaces();
        while( isComment() ) {
            skipComment( endOfCommentOperator() );
            skipSpaces();
        }

        if( sourceFile == null ) {
            nextToken = new Token( eofToken, getFileLineNum() );
        } else if( isLetterAZ(sourceLine.charAt(sourcePos)) || isDigit(sourceLine.charAt(sourcePos)) ) {
            createNameOrNumberToken();
        } else if( sourceLine.charAt(sourcePos) == '\'' ) {
            createCharToken();
        } else {
            createOperatorToken();
        }

        Main.log.noteToken(nextToken);

    } /* END readNextToken */

    private void readNextLine() {
    	if (sourceFile != null) {
    	    try {
        		sourceLine = sourceFile.readLine();
        		if (sourceLine == null) {
        		    sourceFile.close();  sourceFile = null;
        		    sourceLine = "";
        		} else {
        		    sourceLine += " ";
        		}
        		sourcePos = 0;
    	    } catch (IOException e) {
    		    Main.error("Scanner error: unspecified I/O error!");
    	    }
    	}
    	if (sourceFile != null)
    	    Main.log.noteSourceLine(getFileLineNum(), sourceLine);
    } /* END readNextLine */

    private int getFileLineNum() {
    	return (sourceFile!=null ? sourceFile.getLineNumber() : 0);
    } /* END getFileLineNum */

    // Character test utilities:

    private boolean isLetterAZ(char c) {
    	return 'A'<=c && c<='Z' || 'a'<=c && c<='z';
    } /* END isLetterAZ */

    private boolean isDigit(char c) {
    	return '0'<=c && c<='9';
    } /* END isDigit */

    //Read token utilities

    /**
    * Checks if next character starts a comment
    * Returns true if next character i the start of a comment.
    * Comments are defined by "{" and "/*".
    *
    * @return   returns true if start of comment
    */
    private boolean isComment() {
        return ( (sourcePos < sourceLine.length() && sourceLine.charAt(sourcePos) == '{') ||
        ((sourcePos+1 < sourceLine.length()) && sourceLine.charAt(sourcePos) == '/' && sourceLine.charAt(sourcePos+1) == '*') );
    }

    /**
    * Checks if new line is needed.
    * Returns true if a new line should be read.
    *
    * @return   true if new line is needed
    */
    private boolean newLineIsNeeded() {
        return ( sourceLine.equals("") || sourcePos >= sourceLine.length() );
    }

    /**
    * Skips spaces of source line
    * Checks if current char of source line is a space.
    * Skips while space and reads a new line of end of line.
    * Spaces are defined by ' ' and '\t'.
    */
    private void skipSpaces() {
        while( sourcePos < sourceLine.length() &&
        (sourceLine.charAt(sourcePos) == ' ' || sourceLine.charAt(sourcePos) == '\t') ) {
            sourcePos++;
            if ( newLineIsNeeded() ) {
                readNextLine();
            }
        }
    }

    /**
    * Skips a comment
    * This method is called by readNextToken() if isComment() returns true.
    * While current line does not contain end of comment operator a new line will
    * be read. If no end of comment operator is found the rest of the file
    * will be counted as a comment.
    *
    * @param    endOfComment A string that defines the type of end comment operator.
    */
    private void skipComment(String endOfComment) {
        while( sourceFile != null && !sourceLine.contains(endOfComment) ) {
            readNextLine();
        }

        if( sourceFile != null ) {
            sourcePos = sourceLine.indexOf(endOfComment) + endOfComment.length();
        }

        if( newLineIsNeeded() ) {
            readNextLine();
        }
    }

    /**
    * Creates a name of number token
    * Appends character to string while next character is digit or letter.
    * Trys first to convert string to Integer. If it fails the string will be
    * used to create a string token else the Integer created will be used to create
    * a number token.
    */
    private void createNameOrNumberToken() {
        String str = "";
        while( sourcePos < sourceLine.length() &&
        isLetterAZ(sourceLine.charAt(sourcePos)) ||
        isDigit(sourceLine.charAt(sourcePos)) ) {
            str += sourceLine.charAt(sourcePos++);
        }

        try {
            nextToken = new Token( Integer.parseInt(str), getFileLineNum() );
        } catch( Exception e ) {
            nextToken = new Token( str.toLowerCase(), getFileLineNum() );
        }
    }

    /**
    * Creates a character token
    * If next character at source pos + 2 is a ' char.
    * The character at source pos + 1 will be used to create a character token.
    * If not, a scanner error will occur.
    */
    private void createCharToken() {
        if( sourceLine.charAt(sourcePos+2) == '\'' ) {
            nextToken = new Token( sourceLine.charAt(sourcePos+1), getFileLineNum() );
            sourcePos += 3;
        } else {
            error("Illigal char literal!");
        }
    }

    /**
    * Creates an operator token
    * Checks the next (2 - 1) character(s) of source line and creates the
    * correct token token kind. If the operator is not recognized a scanner error
    * will occur.
    */
    private void createOperatorToken() {
        TokenKind kind = null;

        if( sourceLine.charAt(sourcePos) == ':' && sourceLine.charAt(sourcePos+1) == '=' ) {
            kind = assignToken; sourcePos += 2;
        } else if( sourceLine.charAt(sourcePos) == '>' && sourceLine.charAt(sourcePos+1) == '=' ) {
            kind = greaterEqualToken; sourcePos += 2;
        } else if( sourceLine.charAt(sourcePos) == '<' && sourceLine.charAt(sourcePos+1) == '=' ) {
            kind = lessEqualToken; sourcePos += 2;
        } else if( sourceLine.charAt(sourcePos) == '<' && sourceLine.charAt(sourcePos+1) == '>' ) {
            kind = notEqualToken; sourcePos += 2;
        } else if( sourceLine.charAt(sourcePos) == '.' && sourceLine.charAt(sourcePos+1) == '.' ) {
            kind = rangeToken; sourcePos += 2;
        } else {
            if( sourceLine.charAt(sourcePos) == '+' ) {
                kind = addToken;
            } else if( sourceLine.charAt(sourcePos) == ':' ) {
                kind = colonToken;
            } else if( sourceLine.charAt(sourcePos) == ',' ) {
                kind = commaToken;
            } else if( sourceLine.charAt(sourcePos) == '.' ) {
                kind = dotToken;
            } else if( sourceLine.charAt(sourcePos) == '=' ) {
                kind = equalToken;
            } else if( sourceLine.charAt(sourcePos) == '>' ) {
                kind = greaterToken;
            } else if( sourceLine.charAt(sourcePos) == '[' ) {
                kind = leftBracketToken;
            } else if( sourceLine.charAt(sourcePos) == '(' ) {
                kind = leftParToken;
            } else if( sourceLine.charAt(sourcePos) == '<' ) {
                kind = lessToken;
            } else if( sourceLine.charAt(sourcePos) == '*' ) {
                kind = multiplyToken;
            } else if( sourceLine.charAt(sourcePos) == ']' ) {
                kind = rightBracketToken;
            } else if( sourceLine.charAt(sourcePos) == ')' ) {
                kind = rightParToken;
            } else if( sourceLine.charAt(sourcePos) == ';' ) {
                kind = semicolonToken;
            } else if( sourceLine.charAt(sourcePos) == '-' ) {
                kind = subtractToken;
            } else {
                error( "Unknown operator '" + sourceLine.charAt(sourcePos) + "'!" );
            } sourcePos++;
        }

        nextToken = new Token( kind, getFileLineNum() );
    }

    /**
    * Returns the correct end of comment operator
    * By checking the start of comment operator the correct end of comment
    * operator will be created and returned. If unrecognized start of comment operator
    * the method will return null.
    *
    * @return   correct end of comment operator
    * @see      String
    */
    private String endOfCommentOperator() {
        if( sourceLine.charAt(sourcePos) == '{' )
            return "}";
        else if( sourceLine.charAt(sourcePos) == '/' && sourceLine.charAt(sourcePos+1) == '*' )
            return "*/";
        else
            return null;
    }

    // Parser tests:

    public void test(TokenKind t) {
    	if (curToken.kind != t)
    	    testError(t.toString());
    } /* END test */

    public void testError(String message) {
    	Main.error(curLineNum(),
    		   "Expected a " + message +
    		   " but found a " + curToken.kind + "!");
    } /* END testError */

    public void skip(TokenKind t) {
    	test(t);
    	readNextToken();
    } /* END skip */

}
