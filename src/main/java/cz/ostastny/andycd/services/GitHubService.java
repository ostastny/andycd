package cz.ostastny.andycd.services;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public class GitHubService {
	private final Logger log = Logger.getLogger(getClass());
	 
	public void CreateRepo(String repoName) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec("git init " + repoName);
		p.waitFor();

		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuilder sb = new StringBuilder();	
		String line = null;		
		while ((line = reader.readLine())!= null) {
			sb.append(line + "\n");
		}

		PrintWriter out = new PrintWriter(
				new OutputStreamWriter(
						new FileOutputStream(repoName + "/.git/hooks/post-commit"), "UTF-8"));

		out.println("#!/bin/sh");
		out.println("git log --pretty=format:'%h' -n 1  | curl -X POST --header \"Content-Type: application/json\" --header \"Accept: application/json\" -d @- \"http://localhost:8080/andycd/api/ReleasePath/release\"");
		out.println("exit 0");
		out.close();

		log.info(sb.toString());
		 
	}
}
