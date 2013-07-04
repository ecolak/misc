package com.abe.olasihaber.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abe.olasihaber.model.Argument;
import com.abe.olasihaber.model.Arguments;
import com.abe.olasihaber.model.Article;
import com.abe.olasihaber.model.Vote;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockData {

	private static final ObjectMapper om = new ObjectMapper();
	
	private static Map<Long, Article> articles = new HashMap<Long, Article>();
	private static Map<Long, Arguments> articleToArgsMap = new HashMap<Long, Arguments>();
	static {
		try {
			Article[] artArr = om.readValue(new File("src/main/webapp/data/fake_articles.json"), Article[].class); 
			for (Article a : artArr) {
				articles.put(a.getId(), a);
			}
		} catch (Exception e) {
			System.err.println("Error parsing fake article data: " + e.getMessage());
		}
		
		try {
			Arguments[] argsArr = om.readValue(new File("src/main/webapp/data/fake_arguments.json"), Arguments[].class);
			for (Arguments args : argsArr) {
				List<Argument> argsFor = args.getArguments_for();
				List<Argument> argsAgainst = args.getArguments_against();
				for (Argument arg : argsFor) {
					Arguments existing = articleToArgsMap.get(arg.getArticle_id());
					if (existing == null) {
						existing = new Arguments();
						articleToArgsMap.put(arg.getArticle_id(), existing);
					}
					List<Argument> existingArgsFor = existing.getArguments_for();
					if (existingArgsFor == null) {
						existingArgsFor = new ArrayList<Argument>();
						existing.setArguments_for(existingArgsFor);
					}
					existingArgsFor.add(arg);
				}
				for (Argument arg : argsAgainst) {
					Arguments existing = articleToArgsMap.get(arg.getArticle_id());
					if (existing == null) {
						existing = new Arguments();
						articleToArgsMap.put(arg.getArticle_id(), existing);
					}
					List<Argument> existingArgsAgainst = existing.getArguments_against();
					if (existingArgsAgainst == null) {
						existingArgsAgainst = new ArrayList<Argument>();
						existing.setArguments_against(existingArgsAgainst);
					}
					existingArgsAgainst.add(arg);
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing fake arguments data: " + e.getMessage());
		}
	}
	
	private static Map<String, Map<Long, Vote>> votes = new HashMap<String, Map<Long, Vote>>();

	public static List<Article> getAllArticles() {
		return new ArrayList<Article>(articles.values());
	}

	public static Article getArticleById(long id) {
		return articles.get(id);
	}
	
	public static Arguments getArgumentsForArticle(long articleId) {
		return articleToArgsMap.get(articleId);
	}
	
	public static void saveVote(Vote vote) {
		Map<Long, Vote> userVotes = votes.get(vote.getIp());
		if (userVotes == null) {
			userVotes = new HashMap<Long, Vote>();
			votes.put(vote.getIp(), userVotes);
		}
		userVotes.put(vote.getArticle_id(), vote);
	}
	
	public static Vote getVoteByIpAndArticle(String ip, long articleId) {
		Map<Long, Vote> articleMap = votes.get(ip);
		if (articleMap != null) {
			return articleMap.get(articleId);
		}
		return null;
	}
	
	public static void deleteVoteByIpAndArticle(String ip, long articleId) {
		Map<Long, Vote> articleMap = votes.get(ip);
		if (articleMap != null) {
			articleMap.remove(articleId);
		}
	}
}
