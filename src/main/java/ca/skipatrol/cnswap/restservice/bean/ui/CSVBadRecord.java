package ca.skipatrol.cnswap.restservice.bean.ui;

public class CSVBadRecord {
	int lineNumber ;
	String possibleError;
	String line;
	
	@Override
	public String toString() {
		return "CSVBadRecord [lineNumber=" + lineNumber + ", possibleError=" + possibleError + ", line=" + line + "]";
	}

	
	public CSVBadRecord(int lineNumber,String line) {
		this.lineNumber = lineNumber;
		this.line = line;
		this.possibleError = new String();
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getPossibleError() {
		return possibleError;
	}

	public void setPossibleError(String possibleError) {
		this.possibleError = possibleError;
	}

	
}
