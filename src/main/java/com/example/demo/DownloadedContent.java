package com.example.demo;

import java.nio.file.Path;


public class DownloadedContent {

	private Path tempFile;
	private String uri;

	public DownloadedContent() {
		super();
	}

	public DownloadedContent(Path tempFile, String createUri) {
		super();
		this.tempFile = tempFile;
		uri = createUri;
	}

	public Path getTempFile() {
		return tempFile;
	}

	public void setTempFile(Path tempFile) {
		this.tempFile = tempFile;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}