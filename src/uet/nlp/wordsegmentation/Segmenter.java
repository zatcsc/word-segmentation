package uet.nlp.wordsegmentation;

import java.util.ArrayList;
import java.util.List;

import uet.nlp.constant.Paths;
import uet.nlp.resource.Dictionary;
import uet.nlp.resource.Regex;
import uet.nlp.resource.RegexMatcher;
import uet.nlp.utils.StrUtils;

public class Segmenter {

	private RegexMatcher regexMatcher;
	private Dictionary vDict;

	public Segmenter() {
		regexMatcher = new RegexMatcher(Paths.REGEX_PATH);
		vDict = Dictionary.getDictionary(Paths.VIET_DICT_PATH);
	}

	public List<String> segment(String input) {
		List<String> segs = new ArrayList<String>();
		Regex matchingRes;
		int matchedEnd;
		String matchedPart;
		while (input.length() > 0) {
			matchingRes = regexMatcher.getLongestMatchedRegex(input);
			matchedEnd = matchingRes.getlgstAclen();
			matchedPart = input.substring(0, matchedEnd).trim();
			input = input.substring(matchedEnd).trim();
			if (matchingRes.isPhrase()) {
				List<String> subSegs = segmentPhrase(matchedPart);
				segs.addAll(subSegs);
			} else
				segs.add(matchedPart.replace(' ', '_'));
		}
		return segs;
	}

	private List<String> segmentPhrase(String phrase) {
		Graph wordGraph = buildGraph(phrase);

		List<String> segs = new ArrayList<String>();
		List<String> tokens = StrUtils.tokenizeString(phrase);
		List<Integer> bestSegs = wordGraph.getShortestPath();
		if (bestSegs != null)
			for (int i = 1; i < bestSegs.size(); i++) {
				int start = bestSegs.get(i) - 1;
				int end = bestSegs.get(i - 1) - 1;
				StringBuilder word = new StringBuilder(tokens.get(start));
				for (int j = start + 1; j < end; j++)
					word.append("_" + tokens.get(j));
				segs.add(0, word.toString());
			}
		else
			segs.addAll(tokens);

		return segs;
	}

	private Graph buildGraph(String phrase) {
		List<String> tokens = StrUtils.tokenizeString(phrase);
		int size = tokens.size();

		List<Edge> edges = new ArrayList<Edge>();
		for (int i = 0; i < size; i++) {
			StringBuilder candidate = new StringBuilder();
			for (int j = i; j < size; j++) {
				int wordLen = (j - i + 1);
				if (wordLen > 4)
					break;
				candidate.append(" " + tokens.get(j));
				if (vDict.contains(candidate.toString())) {
					Edge word = new Edge(i + 1, j + 2, 1.0 / wordLen);
					edges.add(word);
				}
			}
		}

		Graph graph = new Graph(size + 1, edges);
		return graph;
	}
}