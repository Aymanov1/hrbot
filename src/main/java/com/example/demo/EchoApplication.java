package com.example.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.Value;
import retrofit2.Response;

@SpringBootApplication
@LineMessageHandler
public class EchoApplication {

	String token = "U0wNzlrKrSkVmZTmcYqa9/5nFFw97D3At7i4fvX55iMlXyo8oDH6y0qsJtszWcpD7C9IdvW9BFNSHA08g24qqqkKB8uj/5KSxcZ9AUUkn25Tz1sly0u76AHHmPJ9v1q/4DC3vQG+097JrmniWS06ywdB04t89/1O/w1cDnyilFU=";
	Logger log = LoggerFactory.getLogger(EchoApplication.class);
	static Path downloadedContentDir;
	@Autowired
	private LineMessagingClient lineMessagingClient;

	public static void main(String[] args) {
		Path downloadedContentDir;
		SpringApplication.run(EchoApplication.class, args);
	}

	@EventMapping
	public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
		System.out.println("event: " + event);
		return new TextMessage(event.getMessage().getText());
	}

	@EventMapping
	public void handleDefaultMessageEvent(Event event) {
		System.out.println("event: " + event);
	}

	@EventMapping
	public ImageMessageContent handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
		// You need to install ImageMagick

		handleHeavyContent(event.getReplyToken(), event.getMessage().getId(), responseBody -> {
			DownloadedContent jpg = saveContent("jpg", responseBody);
			DownloadedContent previewImg = createTempFile("jpg");
			log.info(previewImg.tempFile.getFileName().toString());

		});

		return new ImageMessageContent(event.getMessage().getId());
	}

	private static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
		Logger log = LoggerFactory.getLogger(DownloadedContent.class);
		// log.info("Got content-type: {}", responseBody);

		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.tempFile)) {
			ByteStreams.copy(responseBody.getStream(), outputStream);

			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void handleHeavyContent(String replyToken, String messageId,
			Consumer<MessageContentResponse> messageConsumer) {
		final MessageContentResponse response;
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			log.info(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		messageConsumer.accept(response);
	}

	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
		Path tempFile = EchoApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));

	}

	private static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	@Value
	public static class DownloadedContent {

		Path tempFile;
		String uri;

		public DownloadedContent(Path tempFile, String createUri) {
			super();
			this.tempFile = tempFile;
			uri = createUri;
		}

	}
}