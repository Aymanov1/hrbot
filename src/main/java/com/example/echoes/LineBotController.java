
package com.example.echoes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;

import okhttp3.ResponseBody;
import retrofit2.Response;

@RestController
@RequestMapping(value = "/linebot")
public class LineBotController {
	@Autowired
	@Qualifier("com.cloudinary.cloud_name")
	String cCloudName;

	@Autowired
	@Qualifier("com.cloudinary.api_key")
	String cApiKey;

	@Autowired
	@Qualifier("com.cloudinary.api_secret")
	String cApiSecret;

	@Autowired
	@Qualifier("com.linecorp.channel_secret")
	String lChannelSecret;

	@Autowired
	@Qualifier("com.linecorp.channel_access_token")
	String lChannelAccessToken;

	@RequestMapping(value = "/callback", method = RequestMethod.POST)
	public ResponseEntity<String> callback(@RequestHeader("X-Line-Signature") String aXLineSignature,
			@RequestBody String aPayload) throws JSONException {
		// compose body
		final String text = String.format("The Signature is: %s",
				(aXLineSignature != null && aXLineSignature.length() > 0) ? aXLineSignature : "N/A");

		System.out.println(text);

		final boolean valid = new LineSignatureValidator(lChannelSecret.getBytes())
				.validateSignature(aPayload.getBytes(), aXLineSignature);

		System.out.println("The signature is: " + (valid ? "valid" : "tidak valid"));

		// Get events from source
		if (aPayload != null && aPayload.length() > 0) {
			System.out.println("Payload: " + aPayload);
		}

		Gson gson = new Gson();
		Payload payload = gson.fromJson(aPayload, Payload.class);

		// Variable initialization
		String msgText = " ";
		String upload_url = " ";
		String mJSON = " ";
		String idTarget = " ";
		String eventType = payload.events[0].type;

		// Check event's type
		if (eventType.equals("join")) {
			if (payload.events[0].source.type.equals("group")) {
				replyToUser(payload.events[0].replyToken, "Hello Group");
			}
			if (payload.events[0].source.type.equals("room")) {
				replyToUser(payload.events[0].replyToken, "Hello Room");
			}
		} else if (eventType.equals("message")) { // Event's type is message
			if (payload.events[0].source.type.equals("group")) {
				idTarget = payload.events[0].source.groupId;
			} else if (payload.events[0].source.type.equals("room")) {
				idTarget = payload.events[0].source.roomId;
			} else if (payload.events[0].source.type.equals("user")) {
				idTarget = payload.events[0].source.userId;
			}

			// Check message's type
			if (!payload.events[0].message.type.equals("text")) {
				upload_url = getUserContent(payload.events[0].message.id, payload.events[0].source.userId);
				pushImage(idTarget, upload_url);
			} else {
				msgText = payload.events[0].message.text;
				msgText = msgText.toLowerCase();

				if (!msgText.contains("bot leave")) {
					replyToUser(payload.events[0].replyToken, "Unknown keyword");
				} else {
					if (payload.events[0].source.type.equals("group")) {
						leaveGR(payload.events[0].source.groupId, "group");
					} else if (payload.events[0].source.type.equals("room")) {
						leaveGR(payload.events[0].source.roomId, "room");
					}
				}
			}
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	// Method for reply to user's event
	private void replyToUser(String rToken, String messageToUser) {
		TextMessage textMessage = new TextMessage(messageToUser);
		ReplyMessage replyMessage = new ReplyMessage(rToken, textMessage);
		try {
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(lChannelAccessToken).build()
					.replyMessage(replyMessage).execute();
			System.out.println("Reply Message: " + response.code() + " " + response.message());
		} catch (IOException e) {
			System.out.println("Exception is raised ");
			e.printStackTrace();
		}
	}

	// Method for get user's image with LINE Messaging API
	private String getUserContent(String messageId, String source_id) throws JSONException {
		Cloudinary cloudinary = new Cloudinary("cloudinary://" + cApiKey + ":" + cApiSecret + "@" + cCloudName);
		String uploadURL = " ";
		try {
			// Get user's image with LINE Messaging API
			Response<ResponseBody> response = LineMessagingServiceBuilder.create(lChannelAccessToken).build()
					.getMessageContent(messageId).execute();
			if (response.isSuccessful()) {
				ResponseBody content = response.body();
				InputStream imageStream = content.byteStream();
				Path path = Files.createTempFile(messageId, ".jpg");
				try (FileOutputStream out = new FileOutputStream(path.toFile())) {
					byte[] buffer = new byte[1024];
					int len;
					while ((len = imageStream.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
				} catch (Exception e) {
					System.out.println("Exception is raised ");
				}
				// Upload user's image to cloudinary image storage
				Map uploadResult = cloudinary.uploader().upload(path.toFile(), ObjectUtils.emptyMap());
				System.out.println(uploadResult.toString());
				JSONObject jUpload = new JSONObject(uploadResult);
				// Get image's URL for echoing back to user
				uploadURL = jUpload.getString("secure_url");
			} else {
				System.out.println(response.code() + " " + response.message());
			}
		} catch (IOException e) {
			System.out.println("Exception is raised ");
			e.printStackTrace();
		}
		return uploadURL;
	}

	// Method for push image to user
	private void pushImage(String sourceId, String poster_url) {
		ImageMessage imageMessage = new ImageMessage(poster_url, poster_url);
		PushMessage pushMessage = new PushMessage(sourceId, imageMessage);
		try {
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(lChannelAccessToken).build()
					.pushMessage(pushMessage).execute();
			System.out.println(response.code() + " " + response.message());
		} catch (IOException e) {
			System.out.println("Exception is raised ");
			e.printStackTrace();
		}
	}

	// Method for leave group or room
	private void leaveGR(String id, String type) {
		try {
			if (type.equals("group")) {
				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(lChannelAccessToken).build()
						.leaveGroup(id).execute();
				System.out.println(response.code() + " " + response.message());
			} else if (type.equals("room")) {
				Response<BotApiResponse> response = LineMessagingServiceBuilder.create(lChannelAccessToken).build()
						.leaveRoom(id).execute();
				System.out.println(response.code() + " " + response.message());
			}
		} catch (IOException e) {
			System.out.println("Exception is raised ");
			e.printStackTrace();
		}
	}
}
