package org.jgll.util.hashing;

public class StringExternalHasher implements ExternalHasher<String> {
	
	private static final long serialVersionUID = 1L;
	
	private static ExternalHasher<String> instance;
	
	public static ExternalHasher<String> getInstance() {
		if(instance == null) {
			instance = new StringExternalHasher();
		}
		return instance;
	}
	
	@Override
	public int hash(String s, HashFunction f) {
		int[] array = new int[s.length() / 2 + 1];
		
		for (int i = 1; i < s.length(); i += 2) {
			array[i - 1] = s.charAt(i - 1) | (s.charAt(i) << 16);
		}

		return f.hash(array);
	}

	@Override
	public boolean equals(String s1, String s2) {
		return s1.equals(s2);
	}
 
}
