package cz.ostastny.andycd.services;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class GitHubService {
	private final Logger log = Logger.getLogger(getClass());
	 
	/**
	 * Initialize new bare repository. 
	 * NOTE: This code is not optimized. For testing purposes only!
	 * 
	 * @param repoName Repository name. Will be validated and made safe.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void CreateRepo(String repoName) throws IOException, InterruptedException {
		Objects.requireNonNull(repoName);
		
		final String safeRepoName = CreateGitRepoSafeName(repoName);
		
		//init new bare repository with given name 
		ProcessBuilder builder = new ProcessBuilder("git", "init", safeRepoName, "--bare");
		builder.redirectErrorStream(true);
		Process p = builder.start();
		p.waitFor();
		
		final InputStream stream = p.getInputStream();
		final byte[] buffer = new byte[1000];
		while (stream.read(buffer) > 0)
		{
			log.info(buffer);
		}

		//create a commit hook to notify our API of any commits
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(
					new OutputStreamWriter(
							new FileOutputStream(safeRepoName + "/hooks/post-commit"), "UTF-8"));
			out.println("#!/bin/sh");
			out.println("git log --pretty=format:'%h' -n 1  | curl -X POST --header \"Content-Type: application/json\" --header \"Accept: application/json\" -d @- \"http://localhost:8080/andycd/api/ReleasePath/release\"");
			out.println("exit 0");
			
			log.info("Git repo '" + safeRepoName + "' successfully initialized.");
		}
		finally {
			out.close();
		}
	}
	
	/**
	 * Expose the repository over Git protocol on random port
	 * @param repoName
	 * @return Port on which the daemon listens
	 * @throws Exception 
	 */
	public Integer StartDaemon(String repoName) throws Exception {
		Objects.requireNonNull(repoName);
	
		ProcessBuilder builder = new ProcessBuilder("git", "daemon", "--reuseaddr", "--base-path=" + repoName, "--export-all", "--verbose");
		builder.redirectErrorStream(true);
		Process p = builder.start();
		
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader( p.getInputStream()));

		final String line = reader.readLine();
		log.info(line);
		
		//read the port
		final Pattern pattern = Pattern.compile("^\\[(\\d+)\\] Ready to rumble$");
		Matcher matcher = pattern.matcher(line);
		if(!matcher.matches())
			throw new Exception("Unable to start the Git daemon.");
		
		final Integer port = Integer.parseInt(matcher.group(1));
		
		log.info("Port for repo '" + repoName + "' is: " + port);
		
		return port;
	}
	
	/**
	 * Turn the input parameter into safe and compliant Git repo name.
	 * NOTE: Not implemented at the moment!
	 * 
	 * @param repoName Original repository name
	 * @return Safe repository name
	 */
	public String CreateGitRepoSafeName(String repoName)
	{
		Objects.requireNonNull(repoName);
		
		
		//remove invalid characters
		//add .git suffix
		return repoName;
	}
}
