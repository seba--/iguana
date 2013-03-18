package org.jgll.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Input {

	private int[] input;

	private LineColumn[] lineColumns;

	public Input(String input) {
		this(fromString(input));
	}

	public Input(int[] input) {
		this.input = input;
		lineColumns = new LineColumn[input.length - 1];
		calculateLineLengths();
	}
	
	public int charAt(int index) {
		return input[index];
	}

	public static String readTextFromFile(String parentDir, String fileName) throws IOException {
		return readTextFromFile(new File(parentDir, fileName));
	}

	public static int[] fromString(String s) {
		int[] input = new int[s.length() + 1];
		for (int i = 0; i < s.length(); i++) {
			input[i] = s.charAt(i);
		}
		input[s.length()] = -1;
		return input;
	}
	
	public int get(int index) {
		return input[index];
	}
	
	public int size() {
		return input.length;
	}
	
	/**
	 * Returns the whole contents of a text file as a string.
	 * 
	 * @param path
	 *            the path to the text file
	 * @throws IOException
	 */
	public static String readTextFromFile(String path) throws IOException {
		return readTextFromFile(new File(path));
	}

	public static String readTextFromFile(File file) throws IOException {
		StringBuilder sb = new StringBuilder();

		InputStream in = new BufferedInputStream(new FileInputStream(file));
		int c = 0;
		while ((c = in.read()) != -1) {
			sb.append((char) c);
		}

		in.close();

		return sb.toString();
	}

	public static String read(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedInputStream in = new BufferedInputStream(is);
		int c = 0;
		while ((c = in.read()) != -1) {
			sb.append((char) c);
		}
		in.close();
		return sb.toString();
	}

	public int getLineNumber(int index) {
		return lineColumns[index].getLineNumber();
	}
	
	public int getColumnNumber(int index) {
		return lineColumns[index].getColumnNumber();
	}

	private void calculateLineLengths() {
		int lineNumber = 1;
		int columnNumber = 1;

		for (int i = 0; i < input.length - 1; i++) {
			lineColumns[i] = new LineColumn(lineNumber, columnNumber);
			if (input[i] == '\n') {
				lineNumber++;
				columnNumber = 1;
			} else if(input[i] == '\r' && i < input.length - 2 && input[i + 1] == '\n') {
				columnNumber = 1;
				lineNumber++;
				i++;
			} else if (input[i] == '\r') {
				lineNumber++;
				columnNumber = 1;
			} else {
				columnNumber++;
			}
		}
	}
	
	private static class LineColumn {

		private int lineNumber;
		private int columnNumber;
		
		public LineColumn(int lineNumber, int columnNumber) {
			this.lineNumber = lineNumber;
			this.columnNumber = columnNumber;
		}
		
		public int getLineNumber() {
			return lineNumber;
		}
		
		public int getColumnNumber() {
			return columnNumber;
		}
		
		@Override
		public String toString() {
			return "(" + lineNumber + ":" + columnNumber + ")";
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			
			if(!(obj instanceof LineColumn)) {
				return false;
			}
			
			LineColumn other = (LineColumn) obj;
			return lineNumber == other.lineNumber && columnNumber == other.columnNumber;
		}
	}
}
