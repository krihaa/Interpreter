package scanner;

import java.io.*;
import java.util.*;

import main.*;
import static scanner.TokenKind.*;
import static scanner.TokenIndex.*;

public class Scanner {
    private LineNumberReader sourceFile = null;
    private String curFileName;
    private ArrayList<Token> curLineTokens = new ArrayList<>();
    private Stack<Integer> indents = new Stack<>();
    private final int TABDIST = 4;


	private String original_line; // Hele linjen vi starter med
	private String rem_line; // Linjen vi jobber med 


    public Scanner(String fileName) {
		curFileName = fileName;
		indents.push(0);

		try {
			sourceFile = new LineNumberReader(
					new InputStreamReader(
					new FileInputStream(fileName),
					"UTF-8"));
		} catch (IOException e) {
			scannerError("Cannot read " + fileName + "!");
		}
    }


    private void scannerError(String message) {
		String m = "Asp scanner error";
		if (curLineNum() > 0)
			m += " on line " + curLineNum();
		m += ": " + message;

		Main.error(m);
    }


    public Token curToken() {
		while (curLineTokens.isEmpty()) {
			readNextLine();
		}
		return curLineTokens.get(0);
    }


    public void readNextToken() {
		if (! curLineTokens.isEmpty())
			curLineTokens.remove(0);
	}
	
	/**
	 * Skriver ut en feilmelding og avslutter programmet
	 * @param msg Feilmeldingen som skal skrives ut
	 */
	public void printScannerError(String msg) {
		System.out.println("Error on line " + curLineNum() +".");
		System.out.println(original_line);
		int l = original_line.length() - rem_line.length() -1 > 0 ? original_line.length() - rem_line.length() -1 : 1;
		String spaces = String.format("%" + (l) + "s","");
		System.out.println(spaces + "^");
		System.out.println(spaces + "|");
		System.out.println(spaces + msg);
		System.exit(0);
	}

	public void printParserError() {
		System.out.println("Error on line " + curLineNum() +".");
		System.out.println(original_line +" <- "+"Illegal: \'"+curToken().kind.toString()+"\' token");
		System.exit(0);
	}

	/**
	 * Tar ut en token fra remainingLine og putter den i token variablen
	 * Fjerner også ledende/trailing mellomrom hvis det finner
	 * @param index : indexen på linjen hvor token slutter
	 */
	private String removeTokenFromLine(int index) {
		String token = rem_line.substring(0, index);
		rem_line = rem_line.substring(index);
		token = token.trim();
		return token;
	}

	/**
	 * Finner indenteringen til en linje
	 */
	private void checkIndent(int n) {
		if(!indents.empty()) {
			if(n > indents.peek()) {
				indents.add(n);
				curLineTokens.add(new Token(indentToken,curLineNum()));
			}else {
				while(n < indents.peek()) {
					indents.pop();
					curLineTokens.add(new Token(dedentToken,curLineNum()));
				}
			}
			if(n != indents.peek()) {
				printScannerError("Illegal Indentering");
			}
		}
	}

	/**
	 * Sjekker om vi har en string i koden og om den er gyldig
	 * @return true hvis vi har string, false hvis vi ikke har det
	 */
	private boolean checkStringToken() {
		int j = -1;
		boolean text = false;
		if(rem_line.startsWith("\"")) { 
			j = rem_line.indexOf("\"", 1); 
			text = true;
		}else if(rem_line.startsWith("\'"))
		{
			j = rem_line.indexOf("\'", 1);
			text = true;
		}
		if(j < 0 && text) { // Hvis vi kun fant start på string men ikke slutten
			printScannerError("Illegal string: No string terminator found");
		}
		else if(text) {
				String token = removeTokenFromLine(j+1);
				Token t = new Token(stringToken, curLineNum());
				t.stringLit = token.substring(1,token.length()-1); // Fjerne "" tegn fra teksten
				curLineTokens.add(t);
				return true;
		}
		return false;
	}
	/**
	 * Finner neste ting som kan seprarere variable navn. f.eks mellomrom eller [ tegn
	 * @return indexen til separatoren i teksten
	 */
	private int findNextSeparator() {
		int i = -1;
		for(int x = TokenIndex.symbol_start; x <= TokenIndex.length; x++) {
			int j = rem_line.indexOf(TokenKind.values()[x].toString());
			if(j > -1 && (i >= j || i < 0)) {
				i = j;
			}
			if(j == 0 && x >= TokenIndex.double_symbol_start) { // Spesial tilfelle for dobble tegn '==','!=' etc..
				return 2;
			}
		}
		int j = rem_line.indexOf(' ');
			if(j > -1 && (i > j || i < 0)) {
				i = j;
		}
		if(i < 0)
			return rem_line.length();
		if(i == 0)
			return i+1;
		return i;
	}

	/**
	 * Sjekker om token er en keyword eller kode-symbol
	 * @param token teksten som skal sjekkes om er et keyword
	 * @return True hvis det er et keyword, false hvis ikke
	 */
	private boolean checkKeywordToken(String token) {
		if(token.length() > 0) {
			for(int x = TokenIndex.length; x >= TokenIndex.keyword_start; x--) {
				if(token.equals(TokenKind.values()[x].toString())) {
					curLineTokens.add(new Token(TokenKind.values()[x], curLineNum()));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sjekker om token er et integrer eller flyt-tall
	 * @param token teksten som skal sjekkes om er et flyt-tall / integrer
	 * @return True hvis det er et tall, false hvis ikke
	 */
	private boolean checkNumberToken(String token) {
		int foundDot = -1;
		for(int x = 0; x < token.length(); x++) {
			if(!isDigit(token.charAt(x))) {
				if(token.charAt(x) == '.') {
					if(foundDot == -1) { // Kun lovlig med en '.' i float
						foundDot = x;
					}else
						return false;
				}else
					return false;
			}
		}
		if(foundDot < token.length() -1 && foundDot > 0) {
			Token t = new Token(floatToken, curLineNum());
			t.floatLit = Double.parseDouble(token);
			curLineTokens.add(t);
			return true;
		}
		if(foundDot == -1) {
			Token t = new Token(integerToken, curLineNum());
			t.integerLit = Integer.parseInt(token);
			curLineTokens.add(t);
			return true;
		}
		return false;
	}

	/**
	 * Sjekker om token er et lovlig variable navn
	 * @param token Teksten som skal sjekkes
	 */
	private void checkVariableToken(String token) {
		for(int x = 0; x < token.length(); x++) {
			if(!isLetterAZ(token.charAt(x))) {
				if(!isDigit(token.charAt(x))) {
					printScannerError("Illegal: "+ token.charAt(x)+" Symbol in variable name");
				}
			}
		}
		Token t = new Token(nameToken, curLineNum());
		t.name = token;
		curLineTokens.add(t);
	}

	/**
	 * Går gjennom linjen bit for bit og finner tokens
	 */
	private void scanNextToken() {
		checkStringToken();

		String token = removeTokenFromLine(findNextSeparator());
		if(token.isEmpty()) { //Tom/blank tegn, ikke interesant
			if(!rem_line.isEmpty())
				scanNextToken();
			return;
		}
		if(token.startsWith("#")) { // Kommentarer, ikke interesant
			return;
		}
		if(!checkKeywordToken(token)){ //Sjekke om det er keyword
			if(!checkNumberToken(token)) { // Hvis ikke sjekk om det er tall
				checkVariableToken(token); // Hvis ikke så må det være variabel (Eller feil kode)
			}
		}
		if(rem_line.length() > 0) { // Hvis det er mer igjen av linjen fortsett
			scanNextToken();
		}
	}


    private void readNextLine() {
		curLineTokens.clear();

		// Read the next line:
		String line = null;
		try {
			line = sourceFile.readLine();
			if (line == null) {
			sourceFile.close();
			sourceFile = null;
			} else {
			Main.log.noteSourceLine(curLineNum(), line);
			}
		} catch (IOException e) {
			sourceFile = null;
			scannerError("Unspecified I/O error!");
		}

		if(line == null) { // Hvis det ikke er mer å lese legg til en end-of-file Token
			for(int i : indents) {
				if(i > 0) {
					curLineTokens.add(new Token(dedentToken,curLineNum()));
				}
			}
			curLineTokens.add(new Token(eofToken));
		}else{
			original_line = line;
			line = expandLeadingTabs(line); // Gjør om tabs til blanke
			int indent = findIndent(line); // Teller blanke
			line = line.substring(indent); // Fjerner blanke fra linja

			// Ikke interesert i blank eller kommentert ut linje
			if(line.startsWith("#") || line.length() <= 0) {
				return;
			}
			rem_line = line;
			checkIndent(indent);
			scanNextToken();
			// Terminate line:
			curLineTokens.add(new Token(newLineToken,curLineNum()));
		}

		for (Token t: curLineTokens) 
			Main.log.noteToken(t);
	}

	public int curLineNum() {
		return sourceFile!=null ? sourceFile.getLineNumber() : 0;
		}

		private int findIndent(String s) {
		int indent = 0;

		while (indent<s.length() && s.charAt(indent)==' ') indent++;
		return indent;
    }

    private String expandLeadingTabs(String s) {
		String newS = "";
		for (int i = 0;  i < s.length();  i++) {
			char c = s.charAt(i);
			if (c == '\t') {
			do {
				newS += " ";
			} while (newS.length()%TABDIST > 0);
			} else if (c == ' ') {
			newS += " ";
			} else {
			newS += s.substring(i);
			break;
			}
		}
		return newS;
    }


    private boolean isLetterAZ(char c) {
		return ('A'<=c && c<='Z') || ('a'<=c && c<='z') || (c=='_');
    }


    private boolean isDigit(char c) {
		return '0'<=c && c<='9';
    }


    public boolean isCompOpr() {
		TokenKind k = curToken().kind;
		switch(k) {
			case lessToken:
			case lessEqualToken:
			case greaterToken:
			case greaterEqualToken:
			case doubleEqualToken:
			case notEqualToken:
				return true;
			default:
				return false;
		}
    }


    public boolean isFactorPrefix() {
		return isTermOpr();
    }


    public boolean isFactorOpr() {
		TokenKind k = curToken().kind;
		switch(k) {
			case astToken:
			case slashToken:
			case percentToken:
			case doubleSlashToken:
				return true;
			default:
				return false;
		}
    }
	

    public boolean isTermOpr() {
		TokenKind k = curToken().kind;
		switch(k) {
			case plusToken:
			case minusToken:
				return true;
			default:
				return false;
		}
    }


    public boolean anyEqualToken() {
		for (Token t: curLineTokens) {
			if (t.kind == equalToken) return true;
			if (t.kind == semicolonToken) return false;
		}
		return false;
    }
}
