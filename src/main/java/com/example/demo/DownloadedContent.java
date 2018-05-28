package com.example.demo;

import java.nio.file.Path;

import lombok.Value;


@Value
public  class DownloadedContent {

	Path tempFile;
	String uri;

	public DownloadedContent(Path tempFile, String createUri) {
		super();
		this.tempFile = tempFile;
		uri = createUri;
	}

}