package uet.nlp.test;

import java.util.List;

import uet.nlp.wordsegmentation.Segmenter;

public class Test {
	
	public static void main(String[] args) {
		Segmenter segmenter = new Segmenter();
		List<String> tokens = segmenter.segment("thuộc địa phận hai phường Yên Hoà và Trung Hoà");
		for (String token : tokens)
			System.out.println(token);
	}
}
