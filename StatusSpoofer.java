package hans.misc.markoff;
import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;


/**
 * @author Baird Workman
 *   This program analyses a text file of statements, and uses a first order 
 *   Markoff process to then generate its own statements in a similar style. 
 *   I am using this for my 5 minute piece to generate phony Facebook statuses.
 */
public class StatusSpoofer {
	
	public static final String fileName = "fbdata.txt";
	public File input;
	public LineData ld;
	public ArrayList<String> wordList;
	public ArrayList<WordEntry> entryList;
	public int[] wordCount;
	public int size;
	public Random r;
	
	public StatusSpoofer(File input) throws IOException {
		this.input = input;
		ld = getLineData();
		wordList = getWordList();
		size = wordList.size();
		entryList = getAssociations();
		wordCount = getWordCount(entryList.size());
		r = new Random(4624);
	}
	
	public static void main(String[] args) {
		File in = new File (fileName);
		StatusSpoofer spoof = null;
		try {
			spoof = new StatusSpoofer(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Average Line Length: " + spoof.ld.lengthAvg);
		System.out.println("Std Length Deviation: " + spoof.ld.lengthStdDev);
		
		//spoof.printAllWordCounts(ta);
		for (int i = 0; i < 1000; i++) {
			System.out.println("New Status: " + spoof.generateRandomStatus());	
		}
	    
	}
	
	public void printAllWordCounts(StatusSpoofer ta) {
		for (int i = 0; i < spoof.wordList.size(); i++) {
			spoof.printWordCount(spoof.wordList.get(i));
		}
	}
	
	public void printWordCount(String word) {
		int l = wordList.indexOf(word);
		int[] probabilities = entryList.get(l).count;
		System.out.println("\nWord Count for " + word);
		for (int i = 0; i < size; i++) {
			if (probabilities[i] > 0) {
				System.out.println(wordList.get(i) + ": " + probabilities[i]);	
			}
			
		}
	}
	
	public String generateRandomStatus() {
		String status = "";
		Random rand = new Random();
		//first get the length of the status
		int len = -1;
		while (len <= 0) {
			len = (int) ((rand.nextGaussian() * ld.lengthStdDev) + ld.lengthAvg);
		}
		//System.out.println("Len: " + len);
		
		String prevWord = getNextWord(wordList, wordCount);
		status += prevWord;
		//System.out.println("prev word: " + prevWord);
		//printWordCount(prevWord);
		
		/*
		int idx = wordList.indexOf(prevWord);
		prevWord = getNextWord(wordList, entryList.get(idx).count);
		status += " " + prevWord;
		*/
		for (int i = 1; i < len; i++) {
			//get index of previous word in wordList
			int idx = wordList.indexOf(prevWord);
			prevWord = getNextWord(wordList, entryList.get(idx).count);
			status += " " + prevWord;
		}
		
		return status;
	}
	
	private String getNextWord(ArrayList<String> list, int[] probabilities) {
		r = new Random();
		int count = 0;
		for (int i = 0; i < probabilities.length; i++) {
			count += probabilities[i];
		}
		
		//System.out.println("count: " + count);
		if (count == 0) {
			return getNextWord(wordList, wordCount);
		}
		int next = r.nextInt(count);
		int c = 0;
		int i = 0;
		//System.out.println("next: " + next);
		while (c <= next) {
			c += probabilities[i];
			i++;
			//System.out.println("i: " + i);
			//System.out.println("c: " + c);
		}
		//System.out.println("i: " + i);
		return list.get(i - 1);		
	}

	/**
	 * Counts the number of times each word appears in the text
	 * @param size- the number of different words in the text
	 * @return 
	 * @throws IOException
	 */
	public int[] getWordCount(int size) throws IOException {
		int[] wordCount = new int[size];
		Scanner s = new Scanner(input);
		int x;
		while (s.hasNext()) {
			x = wordList.indexOf(nextWord(s));
			wordCount[x]++;
		}
		
		return wordCount;
	}
	/**
	 * Iterates through each word in the word list, scans the entire file, 
	 * 	makes a count of how many times each word comes after that word, 
	 *  and returns it all as a neatly packaged entryList of WordEntrys 
	 * @return the ArrayList of WordEntrys 
	 * @throws IOException
	 */
	public ArrayList<WordEntry> getAssociations() throws IOException {
		Scanner s;
		// Create the WordEntry list and populate it
		ArrayList<WordEntry> entryList = new ArrayList<WordEntry>();
		for (int i = 0; i < wordList.size(); i++) {
			entryList.add(new WordEntry(wordList.get(i), wordList.size()));
		}
		
		// for each word in the list, go through the file and count the words
		//  that come directly after		
		
		for (int i = 0; i < wordList.size(); i++) {
			s = new Scanner(input);
			// go through the whole file 
			//System.out.println("entry #" + i);
			while (s.hasNext()) {
				// when you find the word
				
				if (nextWord(s).equals(wordList.get(i))) {
					//get the word after, then increment that word's count in the 
					// WordEntry list
					if (s.hasNext()) {
						String after = nextWord(s);
						int t = wordList.indexOf(after);
						entryList.get(i).count[t]++;
						//System.out.println("Entry updated: " + i);
						
					}
					
				}
			}
			s.close();
		}
		return entryList;
	}
	
	public String nextWord(Scanner s) {
		String n = s.next();
		n = n.toLowerCase();
		n = n.replaceAll("[^A-Za-z0-9]", "");
		return n;
	}
	
	/**
	 * Analyzes the file and builds a list of all the words that appear in it
	 * @returns the list
	 * @throws IOException
	 */
	public ArrayList<String> getWordList() throws IOException {
		Scanner fileScan = new Scanner(input);
		String next;
		ArrayList<String> list = new ArrayList<String>();
		while(fileScan.hasNext()) {
			next = fileScan.next();
			
			next = next.toLowerCase();
			next = next.replaceAll("[^A-Za-z0-9]", "");
			if (!list.contains(next)) {
				list.add(next);
			}
		}
		fileScan.close();
		return list;
	}
	
	public LineData getLineData() throws IOException {
		
		Scanner s = null, t = null;
		String line;
		int lineCount = 0;
		int[] lineLength;
		
		s = new Scanner(input);
		
		//first count the number of lines in the file
		while(s.hasNextLine()) {
			s.nextLine();
			lineCount++;
		}
		s.close();
		
		//next use the lineCount to create the array
		lineLength = new int[lineCount];
		
		//now count the number of words in each line
		s = new Scanner(input);
		
		for (int i = 0; i < lineCount; i++) {
			line = s.nextLine();
			t = new Scanner(line);
			while(t.hasNext()) {
				t.next();
				lineLength[i]++;
			}
		}
		s.close();
		t.close();
		
		return new LineData(lineLength);
	}
	private class LineData {
		public int lengthStdDev;
		public int lengthAvg;
		
		public LineData(int[] lengthList) {
			this.lengthAvg = calcAvg(lengthList);
			this.lengthStdDev = calcStdDev(lengthList);
		}
		
		public int calcStdDev(int[] lengthList) {
			int variance;
			int stdDev = 0;
			for (int i = 0; i < lengthList.length; i++) {
				variance = lengthList[i] - lengthAvg;
				stdDev += (variance * variance);
			}
			stdDev = (int) Math.sqrt(stdDev/ lengthList.length);
			return stdDev;
		}
		
		public int calcAvg(int[] lengthList) {
			int sum = 0;
			for (int i = 0; i < lengthList.length; i++) {
				sum +=lengthList[i];
			}
			return sum / lengthList.length;
		}
	}
	
	/**
	 * WordEntry class
	 * @author Baird Workman
	 *
	 * Not sure if this will work, but if list is null, we have
	 *  reached the final layer of the Markoff Process?
	 */
	private class WordEntry {
		public String word;
		public int[] count;
		
		public WordEntry(String word, int size) {
			this.word = word;
			this.count = new int[size];
		}
	}
}
