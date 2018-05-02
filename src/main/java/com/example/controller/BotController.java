package com.example.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.dao.AuthorityRepository;
import com.example.dao.UserToUserRequestRepository;
import com.example.dao.LineProgressRepository;
import com.example.dao.RequestRepository;
import com.example.dao.UserInformationRepository;
import com.example.entities.Authority;
import com.example.entities.UserToUserRequest;
import com.example.entities.UserToUserRequestPK;
import com.example.entities.LineProgress;
import com.example.entities.Request;
import com.example.entities.UserInformation;
import com.example.service.AuthorityService;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.DatetimePickerAction;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
@LineMessageHandler
@RestController
public class BotController {
	Logger logger = LoggerFactory.getLogger(BotController.class);

	public static final String TOKEN = "OBna57cOodEGIIqhcSEjjpkjT0AUOl/AZNumYYcxT+H5T3ep6VRSXOOf5pyIRICy5QQ1ytWFUv1Ol5+1Pb2wOWk5+44idmC"
			+ "jlP6vancpqEmWHw9YZHZ0/2H4qn1jCl3AZ88XIo2WkFPylumplMuSlAdB04t89/1O/w1cDnyilFU=";

	@Autowired
	LineMessagingClient lineMessagingClient;
	@Autowired
	UserInformationRepository userInformationRepository;
	@Autowired
	RequestRepository requestRepository;
	@Autowired
	LineProgressRepository lineProgressRepository;
	@Autowired
	AuthorityRepository authorityRepository;
	@Autowired
	AuthorityService authorityService;
	@Autowired
	MessageSource messageSource;
	@Autowired
	UserToUserRequestRepository userToUserRequestRepository;

	LineMessagingClient client = LineMessagingClient.builder(TOKEN).build();
	TextMessage textMessage;
	PushMessage pushMessage;
	BotApiResponse botApiResponse;

	
	@EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        System.out.println("event: " + event);
        return new TextMessage(event.getMessage().getText());
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
	

	private static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	String titleRequest, detailRequest, name, status = "null";
	String idUser;
	long visibility, roleId, number;
	int n, numPage, num, t;
	UserInformation toUser, mainUser;
	Request request;
	UserToUserRequest userToUserRequest;
	LineProgress lineProgress;
	CarouselColumn carouselColumn;
	List<CarouselColumn> listCarouselColumns;
	List<UserInformation> users;
	Page<Authority> authority;
	List<Authority> authorityCont;
	Page<UserInformation> userpage;

	@ResponseBody
	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	private Map<String, Object> webhook(@RequestBody Map<String, Object> obj) throws JSONException, IOException {

		Map<String, Object> json = new HashMap<>();
		JSONObject jsonResult = new JSONObject(obj);
		JSONObject rsl = jsonResult.getJSONObject("originalRequest");
		JSONObject data = rsl.getJSONObject("data");
		JSONObject source = data.getJSONObject("source");
		JSONObject message = data.getJSONObject("message");
		idUser = source.getString("userId");
		String customerMessage = message.getString("text");
		String timestamp = jsonResult.getString("timestamp");
		JSONObject result = jsonResult.getJSONObject("result");
		String resolvedQuery = result.getString("resolvedQuery");
		JSONObject metadata = result.getJSONObject("metadata");
		String intentName = metadata.getString("intentName");
		JSONObject parameters = result.getJSONObject("parameters");

		LinkedHashMap<String, String> hm = new LinkedHashMap<>();

		mainUser = userInformationRepository.findOne(idUser);
		logger.info("in intente name ****** '{}'" + intentName);
		logger.info("in resolved Query ****** '{}'" + resolvedQuery);
		logger.info("JSONObject**************" + jsonResult);
		logger.info("staaaaatuuuuuuuus**************" + status);

		switch (intentName.toLowerCase()) {

		case "language":

			hm.put("English", "English");
			hm.put("日本語", "日本語");
			typeBRecursiveChoices(null, null, messageSource.getMessage("language.select", null,
					new Locale(mainUser.getSystemLanguage().toLowerCase())), hm, TOKEN, idUser);
			logger.info("Choose a Language :" + customerMessage);

			break;

		case "selected language":

			if (customerMessage.equals("English")) {
				mainUser.setSystemLanguage("English");
				userInformationRepository.save(mainUser);
			} else {
				mainUser.setSystemLanguage("Japanese");
				userInformationRepository.save(mainUser);
			}
			textMessage = new TextMessage(messageSource.getMessage("language.change", null,
					new Locale(mainUser.getSystemLanguage().toLowerCase())));
			pushMessage = new PushMessage(idUser, textMessage);
			try {
				botApiResponse = client.pushMessage(pushMessage).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			logger.info("Langauge ***********" + resolvedQuery);

			break;

		case "request":

			lineProgress = new LineProgress();
			n = 0;
			lineProgress.setUserLine(mainUser);
			status = lineProgress.getStatusLine();

			textMessage = new TextMessage(
					messageSource.getMessage("receiver", null, new Locale(mainUser.getSystemLanguage().toLowerCase())));
			pushMessage = new PushMessage(idUser, textMessage);
			try {
				botApiResponse = client.pushMessage(pushMessage).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			logger.info("Request ***********" + resolvedQuery);

			break;

		case "default fallback intent":

			customerMessage = customerMessage.toLowerCase();
			logger.info("customer Message in lower case : " + customerMessage);

			switch (status) {

			case "Default":

				List<UserInformation> user = userInformationRepository.findUserByName("%" + customerMessage + "%", null)
						.getContent();
				int a = user.size();

				if (a == 0) {

					n++;
					System.out.println("status*********" + status);
					logger.info("receiver has noooooooot been chosen" + customerMessage);
					if (n == 2) {
						sendAlertViaSlack(idUser, timestamp, "User can't find the receiver " + customerMessage);
						n = 0;
						lineProgressRepository.delete(lineProgress);
						textMessage = new TextMessage(messageSource.getMessage("receiver.not.found", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())));
						pushMessage = new PushMessage(idUser, textMessage);
						try {
							botApiResponse = client.pushMessage(pushMessage).get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					} else {
						textMessage = new TextMessage(messageSource.getMessage("receiver.again", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())));
						pushMessage = new PushMessage(idUser, textMessage);
						try {
							botApiResponse = client.pushMessage(pushMessage).get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					}

				} else {

					if (a < 4) {

						for (int i = 0; i < a; i++) {
							hm.put(user.get(i).getAccountName(), user.get(i).getAccountName());
						}
						hm.put(messageSource.getMessage("receiver.not.available", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())),
								messageSource.getMessage("receiver.not.available", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())));
						typeBRecursiveChoices(null, null, messageSource.getMessage("receiver.possibility", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())), hm, TOKEN, idUser);

						lineProgress.setStatusLine("receiverchosen");
						lineProgressRepository.save(lineProgress);
						status = lineProgress.getStatusLine();
						System.out.println("status*********" + status);

					} else {

						userpage = userInformationRepository.findUserByName("%" + customerMessage + "%",
								new PageRequest(numPage, 3));
						t = userpage.getTotalPages();
						name = customerMessage;
						users = userpage.getContent();

						for (int i = 0; i < 3; i++) {
							hm.put(users.get(i).getAccountName(), users.get(i).getAccountName());
						}
						hm.put(messageSource.getMessage("see.more", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())),
								messageSource.getMessage("see.more", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())));
						numPage++;

						typeBRecursiveChoices(null, null, messageSource.getMessage("receiver.possibility", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())), hm, TOKEN, idUser);

						lineProgress.setStatusLine("receiverchosen");
						lineProgressRepository.save(lineProgress);
						status = lineProgress.getStatusLine();
						System.out.println("status*********" + status);
					}
				}

				break;

			case "receiverchosen":

				if (customerMessage.equals("not available") || customerMessage.equals("該当なし")) {

					n++;
					lineProgress.setStatusLine("Default");
					lineProgressRepository.save(lineProgress);
					status = lineProgress.getStatusLine();
					System.out.println("status*********" + status);

					if (n == 2) {

						sendAlertViaSlack(idUser, timestamp, "User can't find the receiver " + customerMessage);
						n = 0;
						lineProgressRepository.delete(lineProgress);
						textMessage = new TextMessage(messageSource.getMessage("receiver.not.found", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())));
						pushMessage = new PushMessage(idUser, textMessage);
						try {
							botApiResponse = client.pushMessage(pushMessage).get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					} else {

						textMessage = new TextMessage(messageSource.getMessage("receiver.again", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())));
						pushMessage = new PushMessage(idUser, textMessage);
						try {
							botApiResponse = client.pushMessage(pushMessage).get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					}

				} else {

					if (customerMessage.equals("see more") || customerMessage.equals("もっと見る")) {

						userpage = userInformationRepository.findUserByName("%" + name + "%",
								new PageRequest(numPage, 3));
						users = userpage.getContent();

						if (numPage == (t - 1)) {

							for (int i = 0; i < userpage.getNumberOfElements(); i++) {
								hm.put(users.get(i).getAccountName(), users.get(i).getAccountName());
							}

							hm.put(messageSource.getMessage("receiver.not.available", null,
									new Locale(mainUser.getSystemLanguage().toLowerCase())),
									messageSource.getMessage("receiver.not.available", null,
											new Locale(mainUser.getSystemLanguage().toLowerCase())));
							numPage = 0;
						} else {

							for (int i = 0; i < userpage.getNumberOfElements(); i++) {
								hm.put(users.get(i).getAccountName(), users.get(i).getAccountName());
							}
							hm.put(messageSource.getMessage("see.more", null,
									new Locale(mainUser.getSystemLanguage().toLowerCase())),
									messageSource.getMessage("see.more", null,
											new Locale(mainUser.getSystemLanguage().toLowerCase())));
							numPage++;
						}
						typeBRecursiveChoices(null, null, messageSource.getMessage("receiver.possibility", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())), hm, TOKEN, idUser);

					} else {

						user = userInformationRepository.findUserByName("%" + customerMessage + "%", null).getContent();
						a = user.size();

						for (int i = 0; i < a; i++) {

							String x = user.get(i).getAccountName();
							logger.info("who is the receiver ****************" + x);

							if (customerMessage.equals(x)) {
								String receiverId = user.get(i).getIdUser();
								toUser = userInformationRepository.findOne(receiverId);
								logger.info("the receiver is ++++++++++++ ****************" + toUser);

								lineProgress.setStatusLine("Requesttitled");
								lineProgressRepository.save(lineProgress);
								status = lineProgress.getStatusLine();

								textMessage = new TextMessage(messageSource.getMessage("title", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())));
								pushMessage = new PushMessage(idUser, textMessage);
								try {
									botApiResponse = client.pushMessage(pushMessage).get();
								} catch (InterruptedException | ExecutionException e) {
									e.printStackTrace();
								}
								logger.info("receiver has been chosen" + customerMessage);

							}
						}
					}
				}

				break;

			case "Requesttitled":

				titleRequest = resolvedQuery;

				lineProgress.setStatusLine("RequestDetailed");
				lineProgressRepository.save(lineProgress);
				status = lineProgress.getStatusLine();
				System.out.println("status*********" + status);
				logger.info("Request Titled " + customerMessage);

				textMessage = new TextMessage(messageSource.getMessage("detail", null,
						new Locale(mainUser.getSystemLanguage().toLowerCase())));
				pushMessage = new PushMessage(idUser, textMessage);
				try {
					botApiResponse = client.pushMessage(pushMessage).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}

				break;

			case "RequestDetailed":

				detailRequest = resolvedQuery;

				lineProgress.setStatusLine("RequestAuthorited");
				lineProgressRepository.save(lineProgress);
				status = lineProgress.getStatusLine();
				System.out.println("status*********" + status);
				logger.info("Request detailed", customerMessage);

				authorityCont = authorityRepository.findAll();
				n = authorityCont.size();

				if (n < 5) {

					for (int i = 0; i < 4; i++) {
						hm.put(authorityCont.get(i).getAuthority(), authorityCont.get(i).getAuthority());
					}

					typeBRecursiveChoices(null, null, messageSource.getMessage("authority.select", null,
							new Locale(mainUser.getSystemLanguage().toLowerCase())), hm, TOKEN, idUser);
					logger.info("Choose request authority :" + customerMessage);

				} else {

					authority = authorityService.findAllAuthority(num, 3);
					n = authority.getTotalPages();
					authorityCont = authority.getContent();

					for (int i = 0; i < 3; i++) {
						hm.put(authorityCont.get(i).getAuthority(), authorityCont.get(i).getAuthority());
					}
					hm.put(messageSource.getMessage("see.more", null,
							new Locale(mainUser.getSystemLanguage().toLowerCase())),
							messageSource.getMessage("see.more", null,
									new Locale(mainUser.getSystemLanguage().toLowerCase())));
					num++;
					typeBRecursiveChoices(null, null, messageSource.getMessage("authority.select", null,
							new Locale(mainUser.getSystemLanguage().toLowerCase())), hm, TOKEN, idUser);
					logger.info("Choose request authority :" + customerMessage);
				}

				break;

			case "RequestAuthorited":

				if (customerMessage.equals(messageSource.getMessage("see.more".toLowerCase(), null,
						new Locale(mainUser.getSystemLanguage().toLowerCase())))) {

					authority = authorityService.findAllAuthority(num, 3);
					authorityCont = authority.getContent();

					if (num == (n - 1)) {
						for (int i = 0; i < authority.getNumberOfElements(); i++) {
							hm.put(authorityCont.get(i).getAuthority(), authorityCont.get(i).getAuthority());
						}
						num = 0;

					} else {
						for (int i = 0; i < 3; i++) {
							hm.put(authorityCont.get(i).getAuthority(), authorityCont.get(i).getAuthority());
						}
						hm.put(messageSource.getMessage("see.more", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())),
								messageSource.getMessage("see.more", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())));
						num++;
					}
					typeBRecursiveChoices(null, null, messageSource.getMessage("authority.select", null,
							new Locale(mainUser.getSystemLanguage().toLowerCase())), hm, TOKEN, idUser);
					logger.info("Choose request authority :" + customerMessage);

				} else {

					authorityCont = authorityRepository.findAll();
					n = authorityCont.size();

					for (int i = 0; i < n; i++) {
						if (customerMessage.equals(authorityCont.get(i).getAuthority().toLowerCase())) {
							logger.info("authoooooooooooooooooooorityyyyyyyyyy*************"
									+ authorityCont.get(i).getAuthority().toLowerCase());
							visibility = authorityCont.get(i).getRanking();
							roleId = authorityCont.get(i).getAuthorityId();
						}
					}

					lineProgress.setStatusLine("Finished");
					lineProgressRepository.save(lineProgress);
					status = lineProgress.getStatusLine();
					System.out.println("status*********" + status);
					logger.info("Request detailed", customerMessage);

					typeCQuestion(
							messageSource.getMessage("confirm", null,
									new Locale(mainUser.getSystemLanguage().toLowerCase()))
									+ "\n \n"
									+ messageSource.getMessage("receiver", null,
											new Locale(mainUser.getSystemLanguage().toLowerCase()))
									+ toUser.getAccountName() + "\n"
									+ messageSource.getMessage("title", null,
											new Locale(mainUser.getSystemLanguage().toLowerCase()))
									+ titleRequest + "\n"
									+ messageSource.getMessage("detail", null,
											new Locale(mainUser.getSystemLanguage().toLowerCase()))
									+ detailRequest + "\n"
									+ messageSource.getMessage("authority", null,
											new Locale(mainUser.getSystemLanguage().toLowerCase()))
									+ authorityRepository.findOne(roleId).getAuthority(),
							messageSource.getMessage("send", null,
									new Locale(mainUser.getSystemLanguage().toLowerCase())),
							messageSource.getMessage("send", null,
									new Locale(mainUser.getSystemLanguage().toLowerCase())),
							messageSource.getMessage("delete", null,
									new Locale(mainUser.getSystemLanguage().toLowerCase())),
							messageSource.getMessage("delete", null,
									new Locale(mainUser.getSystemLanguage().toLowerCase())),
							"Confirm", TOKEN, idUser);
				}

				break;

			default:

				lineProgressRepository.delete(lineProgress);
				textMessage = new TextMessage(messageSource.getMessage("default", null,
						new Locale(mainUser.getSystemLanguage().toLowerCase())));
				pushMessage = new PushMessage(idUser, textMessage);
				try {
					botApiResponse = client.pushMessage(pushMessage).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}

				break;
			}

			break;

		case "confirm":

			logger.info("request decesion**************************" + customerMessage);

			if (customerMessage.equals("Send") || customerMessage.equals("送信")) {

				request = new Request();
				request.setTitleRequest(titleRequest);
				request.setDetailRequest(detailRequest);
				request.setVisibility(visibility);
				request.setCreatedAt(convertToTimestamp(timestamp));
				request.setUpdatedAt(convertToTimestamp(timestamp));
				requestRepository.saveAndFlush(request);

				System.out.println("requeeeeest ID *********************" + request.getIdRequest());

				lineProgressRepository.delete(lineProgress);

				userToUserRequest = new UserToUserRequest();
				UserToUserRequestPK userPk = new UserToUserRequestPK(idUser, toUser.getIdUser(),
						request.getIdRequest());
				userToUserRequest.setRequest(request);
				userToUserRequest.setUserFrom(mainUser);
				userToUserRequest.setUserTo(toUser);
				userToUserRequest.setUserToUserRequestPK(userPk);
				userToUserRequestRepository.save(userToUserRequest);

				String imageUrl = "https://image.shutterstock.com/z/stock-vector-linear-check-mar"
						+ "k-icon-like-tick-and-cross-concept-of-approve-or-disapprove-round-button-and-659922649.jpg";

				hm.put(messageSource.getMessage("approve", null,
						new Locale(mainUser.getSystemLanguage().toLowerCase())),
						"Approve request " + request.getIdRequest());
				hm.put(messageSource.getMessage("disapprove", null,
						new Locale(mainUser.getSystemLanguage().toLowerCase())),
						"Disapprove request " + request.getIdRequest());
				hm.put(messageSource.getMessage("show.detail", null,
						new Locale(mainUser.getSystemLanguage().toLowerCase())),
						"Show detail " + request.getIdRequest());

				typeBRecursiveChoices(imageUrl,
						messageSource.getMessage("title", null, new Locale(mainUser.getSystemLanguage().toLowerCase()))
								+ titleRequest,
						messageSource.getMessage("sender", null, new Locale(mainUser.getSystemLanguage().toLowerCase()))
								+ mainUser.getAccountName(),
						hm, TOKEN, toUser.getIdUser());
				logger.info("request sent to:" + toUser.getAccountName());

				textMessage = new TextMessage(messageSource.getMessage("request.send", null,
						new Locale(mainUser.getSystemLanguage().toLowerCase())));
				pushMessage = new PushMessage(idUser, textMessage);
				try {
					botApiResponse = client.pushMessage(pushMessage).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}

			} else {

				lineProgressRepository.delete(lineProgress);

				textMessage = new TextMessage(messageSource.getMessage("request.delete", null,
						new Locale(mainUser.getSystemLanguage().toLowerCase())));
				pushMessage = new PushMessage(idUser, textMessage);
				try {
					botApiResponse = client.pushMessage(pushMessage).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			break;

		case "history":

			if (customerMessage.equals("Decision history")) {

				String imageUrl = "https://image.shutterstock.com/z/stock-vector-linear-check-mar"
						+ "k-icon-like-tick-and-cross-concept-of-approve-or-disapprove-round-button-and-659922649.jpg";

				List<UserToUserRequest> requests = userToUserRequestRepository.findPendingRequestByToUser(idUser);
				listCarouselColumns = new ArrayList<>();
				int a = requests.size();
				logger.info("size of requests is =" + requests.size());

				if (a == 0) {

					textMessage = new TextMessage(messageSource.getMessage("history.empty", null,
							new Locale(mainUser.getSystemLanguage().toLowerCase())));
					pushMessage = new PushMessage(idUser, textMessage);
					try {
						botApiResponse = client.pushMessage(pushMessage).get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				} else {
					if (a < 10) {
						for (int i = 0; i < a; i++) {

							listCarouselColumns
									.add(new CarouselColumn(imageUrl,
											messageSource.getMessage("title", null,
													new Locale(mainUser.getSystemLanguage().toLowerCase()))
													+ requests.get(i).getRequest().getTitleRequest(),
											messageSource.getMessage("sender", null,
													new Locale(mainUser.getSystemLanguage().toLowerCase()))
													+ requests.get(i).getUserFrom().getAccountName(),
											Arrays.asList(new MessageAction(messageSource.getMessage("approve", null,
													new Locale(mainUser.getSystemLanguage().toLowerCase())),
													"Approve request " + requests.get(i).getRequest().getIdRequest()),
													new MessageAction(
															messageSource.getMessage("disapprove", null,
																	new Locale(mainUser.getSystemLanguage()
																			.toLowerCase())),
															"Disapprove request "
																	+ requests.get(i).getRequest().getIdRequest()),
													new MessageAction(
															messageSource.getMessage("show.detail", null,
																	new Locale(mainUser.getSystemLanguage()
																			.toLowerCase())),
															"Show detail "
																	+ requests.get(i).getRequest().getIdRequest()))));
						}
					} else {
						for (int i = 0; i < 10; i++) {

							listCarouselColumns
									.add(new CarouselColumn(imageUrl,
											messageSource.getMessage("title", null,
													new Locale(mainUser.getSystemLanguage().toLowerCase()))
													+ requests.get(i).getRequest().getTitleRequest(),
											messageSource.getMessage("sender", null,
													new Locale(mainUser.getSystemLanguage().toLowerCase()))
													+ requests.get(i).getUserFrom().getAccountName(),
											Arrays.asList(new MessageAction(messageSource.getMessage("approve", null,
													new Locale(mainUser.getSystemLanguage().toLowerCase())),
													"Approve request " + requests.get(i).getRequest().getIdRequest()),
													new MessageAction(
															messageSource.getMessage("disapprove", null,
																	new Locale(mainUser.getSystemLanguage()
																			.toLowerCase())),
															"Disapprove request "
																	+ requests.get(i).getRequest().getIdRequest()),
													new MessageAction(
															messageSource.getMessage("show.detail", null,
																	new Locale(mainUser.getSystemLanguage()
																			.toLowerCase())),
															"Show detail "
																	+ requests.get(i).getRequest().getIdRequest()))));
						}
					}
				}

				CarouselTemplate carouselTemplate = new CarouselTemplate(listCarouselColumns);
				TemplateMessage templateMessage = new TemplateMessage("Carousel", carouselTemplate);
				PushMessage pushMessage1 = new PushMessage(idUser, templateMessage);
				LineMessagingServiceBuilder.create(TOKEN).build().pushMessage(pushMessage1).execute();
				logger.info("osaka :" + customerMessage);

			} else {

				String[] table = customerMessage.split(" ");
				String part1 = table[0];
				String part2 = table[1];
				String part3 = table[2];
				number = Long.parseLong(part3);
				Request r = requestRepository.findOne(number);
				UserToUserRequest u = userToUserRequestRepository.findRequest(number);

				if (r.getStatus().equals("pending") || r.getStatus().equals("passed")) {

					switch (part1) {
					case "Approve":

						typeCQuestion(
								messageSource.getMessage("confirm.approve",
										new Object[] { u.getUserFrom().getAccountName() },
										new Locale(mainUser.getSystemLanguage().toLowerCase())),
								messageSource.getMessage("yes.approve", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())),
								"Yes Approve " + r.getIdRequest(),
								messageSource.getMessage("no.cancel", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())),
								"No Cancel " + r.getIdRequest(), "Confirm", TOKEN, idUser);

						// r.setStatus("approved");
						// r.setUpdatedAt(convertToTimestamp(timestamp));
						// requestRepository.save(r);
						// logger.info("approooooooooooooooved");
						//
						// textMessage = new TextMessage(
						// userInformationRepository.findOne(idUser).getUserName().toUpperCase()
						// + " has APPROVED your request.\n \nTitle: " + r.getTitle().toUpperCase()
						// + "\nDetail: " + r.getDetail().toUpperCase());
						// pushMessage = new PushMessage(r.getFromUser(), textMessage);
						// try {
						// botApiResponse = client.pushMessage(pushMessage).get();
						// } catch (InterruptedException | ExecutionException e) {
						// e.printStackTrace();
						// }

						break;

					case "Disapprove":

						typeCQuestion(
								messageSource.getMessage("confirm.disapprove",
										new Object[] { u.getUserFrom().getAccountName() },
										new Locale(mainUser.getSystemLanguage().toLowerCase())),
								messageSource.getMessage("yes.disapprove", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())),
								"Yes Disapprove " + r.getIdRequest(),
								messageSource.getMessage("no.cancel", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())),
								"No Cancel " + r.getIdRequest(), "Confirm", TOKEN, idUser);

						// r.setStatus("disapproved");
						// r.setUpdatedAt(convertToTimestamp(timestamp));
						// requestRepository.save(r);
						// logger.info("diiiiiiiiisapproooooooooooooooved");
						//
						// textMessage = new TextMessage(
						// userInformationRepository.findOne(idUser).getUserName().toUpperCase()
						// + " has DISAPPROVED your request.\n \nTitle: " + r.getTitle().toUpperCase()
						// + "\nDetail: " + r.getDetail().toUpperCase());
						// pushMessage = new PushMessage(r.getFromUser(), textMessage);
						// try {
						// botApiResponse = client.pushMessage(pushMessage).get();
						// } catch (InterruptedException | ExecutionException e) {
						// e.printStackTrace();
						// }

						break;

					case "Show":

						logger.info("shoooooooooooooooooooooooooooooooooow");

						typeCQuestion(
								messageSource.getMessage("title", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase()))
										+ r.getTitleRequest().toUpperCase() + "\n"
										+ messageSource.getMessage("sender", null,
												new Locale(mainUser.getSystemLanguage().toLowerCase()))
										+ u.getUserFrom().getAccountName() + "\n"
										+ messageSource.getMessage("detail", null,
												new Locale(mainUser.getSystemLanguage().toLowerCase()))
										+ r.getDetailRequest() + "\n"
										+ messageSource.getMessage("authority", null,
												new Locale(mainUser.getSystemLanguage().toLowerCase()))
										+ authorityRepository.getOne(r.getVisibility()).getAuthority(),
								messageSource.getMessage("approve", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())),
								"Approve request " + r.getIdRequest(),
								messageSource.getMessage("disapprove", null,
										new Locale(mainUser.getSystemLanguage().toLowerCase())),
								"Disapprove request " + r.getIdRequest(), "Confirm", TOKEN, idUser);

						break;

					case "Yes":

						switch (part2) {
						case "Approve":

							r.setStatus("approved");
							r.setUpdatedAt(convertToTimestamp(timestamp));
							requestRepository.save(r);

							logger.info("approooooooooooooooved");

							textMessage = new TextMessage(messageSource.getMessage("approved",
									new Object[] { mainUser.getAccountName().toUpperCase() },
									new Locale(mainUser.getSystemLanguage().toLowerCase())) + "\n \nTitle: "
									+ r.getTitleRequest().toUpperCase() + "\nDetail: "
									+ r.getDetailRequest().toUpperCase());
							pushMessage = new PushMessage(u.getUserFrom().getIdUser(), textMessage);
							try {
								botApiResponse = client.pushMessage(pushMessage).get();
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}

							break;

						case "Disapprove":

							r.setStatus("disapproved");
							r.setUpdatedAt(convertToTimestamp(timestamp));
							requestRepository.save(r);
							logger.info("diiiiiiiiisapproooooooooooooooved");

							textMessage = new TextMessage(messageSource.getMessage("disapproved",
									new Object[] { mainUser.getAccountName().toUpperCase() },
									new Locale(mainUser.getSystemLanguage().toLowerCase())) + "\n \nTitle: "
									+ r.getTitleRequest().toUpperCase() + "\nDetail: "
									+ r.getDetailRequest().toUpperCase());
							pushMessage = new PushMessage(u.getUserFrom().getIdUser(), textMessage);
							try {
								botApiResponse = client.pushMessage(pushMessage).get();
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}

							break;
						}

						break;

					case "No":

						textMessage = new TextMessage(messageSource.getMessage("decision.not.taken", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())));
						pushMessage = new PushMessage(idUser, textMessage);
						try {
							botApiResponse = client.pushMessage(pushMessage).get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}

						break;
					}

				} else {

					textMessage = new TextMessage(
							messageSource.getMessage("decision.taken", new Object[] { r.getStatus().toUpperCase() },
									new Locale(mainUser.getSystemLanguage().toLowerCase())));
					pushMessage = new PushMessage(idUser, textMessage);
					try {
						botApiResponse = client.pushMessage(pushMessage).get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					logger.info("Decision already taken! The request is ****************" + r.getStatus());
				}

			}

			break;

		case "decision":

			String imageUrl = "https://image.shutterstock.com/z/stock-vector-linear-check-mar"
					+ "k-icon-like-tick-and-cross-concept-of-approve-or-disapprove-round-button-and-659922649.jpg";

			List<UserToUserRequest> requests = userToUserRequestRepository.findMyRequests(idUser);
			listCarouselColumns = new ArrayList<>();
			int a = requests.size();

			if (a == 0) {

				textMessage = new TextMessage(messageSource.getMessage(
						messageSource.getMessage("check.detail", null,
								new Locale(mainUser.getSystemLanguage().toLowerCase())),
						null, new Locale(mainUser.getSystemLanguage().toLowerCase())));
				pushMessage = new PushMessage(idUser, textMessage);
				try {
					botApiResponse = client.pushMessage(pushMessage).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			} else {
				if (a < 10) {
					for (int i = 0; i < a; i++) {

						listCarouselColumns.add(new CarouselColumn(imageUrl,
								"Request title: " + requests.get(i).getRequest().getTitleRequest(),
								"TO: " + requests.get(i).getUserFrom().getAccountName(),
								Arrays.asList(new PostbackAction("Request " + requests.get(i).getRequest().getStatus(),
										" "))));
					}
				} else {
					for (int i = 0; i < 10; i++) {

						listCarouselColumns.add(new CarouselColumn(imageUrl,
								"Request title: " + requests.get(i).getRequest().getTitleRequest(),
								"TO: " + requests.get(i).getUserFrom().getAccountName(),
								Arrays.asList(new PostbackAction("Request " + requests.get(i).getRequest().getStatus(),
										" "))));
					}
				}
			}

			CarouselTemplate carouselTemplate1 = new CarouselTemplate(listCarouselColumns);
			TemplateMessage templateMessage2 = new TemplateMessage("Carousel", carouselTemplate1);
			PushMessage pushMessage2 = new PushMessage(idUser, templateMessage2);
			LineMessagingServiceBuilder.create(TOKEN).build().pushMessage(pushMessage2).execute();
			logger.info("osaka :" + customerMessage);

			break;

		case "carousel":

			String imageUrl1 = createUri("/static/buttons/1040.jpg");
			CarouselTemplate carouselTemplate = new CarouselTemplate(Arrays.asList(
					new CarouselColumn(imageUrl1, "hoge", "fuga",
							Arrays.asList(new URIAction("Go to line.me", "https://line.me"),
									new URIAction("Go to line.me", "https://line.me"),
									new PostbackAction("Say hello1", "hello こんにちは"))),
					new CarouselColumn(imageUrl1, "hoge", "fuga",
							Arrays.asList(new PostbackAction("言 hello2", "hello こんにちは", "hello こんにちは"),
									new PostbackAction("言 hello2", "hello こんにちは", "hello こんにちは"),
									new MessageAction("Say message", "Rice=米"))),
					new CarouselColumn(imageUrl1, "Datetime Picker", "Please select a date, time or datetime",
							Arrays.asList(
									new DatetimePickerAction("Datetime", "action=sel", "datetime", "2017-06-18T06:15",
											"2100-12-31T23:59", "1900-01-01T00:00"),
									new DatetimePickerAction("Date", "action=sel&only=date", "date", "2017-06-18",
											"2100-12-31", "1900-01-01"),
									new DatetimePickerAction("Time", "action=sel&only=time", "time", "06:15", "23:59",
											"00:00")))));
			TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
			PushMessage pushMessage1 = new PushMessage(idUser, templateMessage);
			LineMessagingServiceBuilder.create(TOKEN).build().pushMessage(pushMessage1).execute();
			logger.info("osaka :" + customerMessage);

			break;

		case "menu":

			hm.put("Osaka", "osaka");
			hm.put("Tokyo", "tokyo");
			hm.put("London", "london");
			typeBRecursiveChoices(
					"https://lh3.googleusercontent.com/oKsgcsHtHu_nIkpNd-mNCAyzUD8xo68laRPOfvFuO0hqv6nDXVNNjEMmoiv9tIDgTj8=w170",
					" boldTitle", " normalTitle", hm, TOKEN, idUser);
			logger.info("paris :" + customerMessage);

			break;

		case "type b":

			hm.put("Osaka", "osaka");
			hm.put("Tokyo", "tokyo");
			typeBRecursiveChoices(
					"https://lh3.googleusercontent.com/oKsgcsHtHu_nIkpNd-mNCAyzUD8xo68laRPOfvFuO0hqv6nDXVNNjEMmoiv9tIDgTj8=w170",
					" boldTitle", " normalTitle", hm, TOKEN, idUser);
			logger.info("see more :", customerMessage);

			break;

		case "b2":

			hm = new LinkedHashMap<>();
			hm.put("Osaka", "osaka");
			hm.put("Tokyo", "tokyo");
			hm.put("London", "london");
			typeBChoices(
					"https://lh3.googleusercontent.com/oKsgcsHtHu_nIkpNd-mNCAyzUD8xo68laRPOfvFuO0hqv6nDXVNNjEMmoiv9tIDgTj8=w170",
					" boldTitle", " normalTitle", hm, "Next or see more", "Next or see more answer", TOKEN, idUser);
			logger.info("London :", customerMessage);

			break;

		case "date":

			typeDQuestion(
					"https://lh3.googleusercontent.com/oKsgcsHtHu_nIkpNd-mNCAyzUD8xo68laRPOfvFuO0hqv6nDXVNNjEMmoiv9tIDgTj8=w170",
					TOKEN, idUser);

			break;

		default:

			sendAlertViaSlack(idUser, timestamp, customerMessage);
			logger.info("slack :" + customerMessage);

			break;
		}

		return json;

	}

	private Timestamp convertToTimestamp(String timestampText) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		Date date = null;
		try {
			date = format.parse(timestampText);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Timestamp(date.getTime());
	}

	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
		String replyToken = event.getReplyToken();
		logger.info(replyToken + "Got postback data " + event.getPostbackContent().getData() + ", param "
				+ event.getPostbackContent().getParams().toString());
	}

	private void handleTextContent(String replyToken, Event event, TextMessageContent content) throws Exception {
		String text = content.getText();
		String TOKEN = "OBna57cOodEGIIqhcSEjjpkjT0AUOl/AZNumYYcxT+H5T3ep6VRSXOOf5pyIRICy5QQ1ytWFUv1Ol5+1Pb2wOWk5+44idmCjlP6vancpqEmWHw9YZHZ0/2H4qn1jCl3AZ88XIo2WkFPylumplMuSlAdB04t89/1O/w1cDnyilFU=";
		logger.info("Got text message from {}: {}" + replyToken + text);
		switch (text) {
		case "paris": {
			String idUser = event.getSource().getUserId();
			if (idUser != null) {

				typeDQuestion(
						"https://lh3.googleusercontent.com/oKsgcsHtHu_nIkpNd-mNCAyzUD8xo68laRPOfvFuO0hqv6nDXVNNjEMmoiv9tIDgTj8=w170",
						TOKEN, idUser);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info(content.toString());

			}
			break;
		}
		default:
			logger.info("Returns echo message {}: {}" + replyToken + text);
			logger.info(replyToken + text);
			break;
		}

	}

	/**
	 * Method for send carousel template message to use
	 * 
	 * @param idUser
	 * @param lChannelAccessToken
	 * @param nameSatff1
	 * @param nameSatff2
	 * @param poster1_url
	 * @param poster2_url
	 * @throws IOException
	 */
	private void carouselForUser(String idUser, String lChannelAccessToken, java.util.List<String> randomLinks)
			throws IOException {

		java.util.List<CarouselColumn> columns = new ArrayList<>();

		for (String link : randomLinks) {
			Document doc = Jsoup.connect(link).get();
			String title = doc.getElementsByClass("tit_articleName").get(0).text();
			String img = doc.getElementsByClass("max-width-260").get(0).attr("abs:src");

			CarouselColumn column = new CarouselColumn(img, title, "Click check for more details",
					Arrays.asList(new URIAction("check", link)));
			columns.add(column);
		}

		CarouselTemplate carouselTemplate = new CarouselTemplate(columns);

		TemplateMessage templateMessage = new TemplateMessage("Your search result", carouselTemplate);
		PushMessage pushMessage = new PushMessage(idUser, templateMessage);
		try {
			Response<BotApiResponse> response = LineMessagingServiceBuilder.create(lChannelAccessToken).build()
					.pushMessage(pushMessage).execute();
			logger.info(response.code() + " " + response.message());
		} catch (IOException e) {
			logger.info("Exception is raised ");
			e.printStackTrace();
		}
	}

	/**
	 * TypeC template
	 * 
	 * @param idUser
	 * @param TOKEN
	 * @param msgTemplate
	 * @param msgFirstAnswer
	 * @param msgSecondAnswer
	 * @param titleTemplate
	 * @throws IOException
	 */
	public void typeCQuestion(String msgTemplate, String msgFirstAnswer, String msgFirstAnswerToSend,
			String msgSecondAnswer, String msgSecondAnswerToSend, String titleTemplate, String TOKEN, String idUser)
			throws IOException {
		ConfirmTemplate confirmTemplate = new ConfirmTemplate(msgTemplate,
				new MessageAction(msgFirstAnswer, msgFirstAnswerToSend),
				new MessageAction(msgSecondAnswer, msgSecondAnswerToSend));
		TemplateMessage templateMessage = new TemplateMessage(titleTemplate, confirmTemplate);
		PushMessage pushMessage = new PushMessage(idUser, templateMessage);
		LineMessagingServiceBuilder.create(TOKEN).build().pushMessage(pushMessage).execute();

	}

	/**
	 * TypeB template
	 * 
	 * @param idUser
	 * @param TOKEN
	 * @param imageURL
	 * @param boldTitle
	 * @param normalTitle
	 * @param buttonHint
	 * @param messageToSend
	 * @throws IOException
	 */
	public void typeBQuestion(String imageURL, String boldTitle, String normalTitle, String buttonHint,
			String messageToSend, String TOKEN, String idUser) throws IOException {
		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(imageURL, boldTitle, normalTitle, Arrays.asList(
				new URIAction("Go to line.me", "https://line.me"), new MessageAction(buttonHint, messageToSend)));
		TemplateMessage templateMessage = new TemplateMessage("Button alt text", buttonsTemplate);
		PushMessage pushMessage = new PushMessage(idUser, templateMessage);
		LineMessagingServiceBuilder.create(TOKEN).build().pushMessage(pushMessage).execute();
	}

	/**
	 * TypeB template
	 * 
	 * @param idUser
	 * @param TOKEN
	 * @param imageURL
	 * @param boldTitle
	 * @param normalTitle
	 * @param buttonHint
	 * @param messageToSend
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public void typeBChoices(String imageURL, String boldTitle, String normalTitle, LinkedHashMap<String, String> hm,
			String nextOrSeeMore, String nextOrSeeMoreAnswer, String TOKEN, String idUser) throws IOException {
		List<Action> messageActions = new ArrayList<>();
		for (Map.Entry m : hm.entrySet()) {
			MessageAction ma = new MessageAction(m.getKey().toString(), m.getValue().toString());
			messageActions.add(ma);
		}
		messageActions.add(new MessageAction(nextOrSeeMore, nextOrSeeMoreAnswer));
		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(imageURL, boldTitle, normalTitle, messageActions);

		TemplateMessage templateMessage = new TemplateMessage("Button alt text", buttonsTemplate);
		PushMessage pushMessage = new PushMessage(idUser, templateMessage);
		LineMessagingServiceBuilder.create(TOKEN).build().pushMessage(pushMessage).execute();
	}

	/**
	 * TypeD template Date-Calendar
	 * 
	 * @param idUser
	 * @param TOKEN
	 * @param imageURL
	 * @param boldTitle
	 * @param normalTitle
	 * @param buttonHint
	 * @param messageToSend
	 * @throws IOException
	 */
	public void typeDQuestion(String imageUrl, String TOKEN, String idUser) throws IOException {
		logger.info("CALENDAAAAAAAAAAAARRRR");
		DatetimePickerAction date = new DatetimePickerAction("Date", "action=sel", "date");
		CarouselTemplate carouselTemplate = new CarouselTemplate(Arrays.asList(
				new CarouselColumn("https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png",
						"Datetime Picker", "Please select a date, time or datetime", Arrays.asList(date))));
		TemplateMessage templateMessage1 = new TemplateMessage("date time picker", carouselTemplate);
		ReplyMessage pushMessage1 = new ReplyMessage(idUser, templateMessage1);
		logger.info("push message is \n " + pushMessage1);
		logger.info(templateMessage1.getTemplate().toString());
		LineMessagingServiceBuilder.create(TOKEN).build().replyMessage(pushMessage1);
		logger.info("response is \n " + Arrays.toString(pushMessage1.getMessages().toArray()));

		// LineMessagingClient.builder(TOKEN).build().replyMessage(pushMessage1);
	}

	public void typeBRecursiveChoices(String imageURL, String boldTitle, String normalTitle, Map<String, String> hm,
			String TOKEN, String idUser) throws IOException {
		List<Action> messageActions = new ArrayList<>();
		hm.size();
		if (hm.size() <= 4) {
			for (Map.Entry m : hm.entrySet()) {
				MessageAction ma = new MessageAction(m.getKey().toString(), m.getValue().toString());
				messageActions.add(ma);
			}
			// messageActions.add(new MessageAction("N/A", "not available"));
			ButtonsTemplate buttonsTemplate = new ButtonsTemplate(imageURL, boldTitle, normalTitle, messageActions);

			TemplateMessage templateMessage = new TemplateMessage("New Request", buttonsTemplate);
			PushMessage pushMessage = new PushMessage(idUser, templateMessage);
			LineMessagingServiceBuilder.create(TOKEN).build().pushMessage(pushMessage).execute();

		}
	}

	/**
	 * @author Aymanov
	 * @param idUser
	 *            : user line ID
	 * @param timestamp:
	 *            message timestamp
	 * @param customerMessage:
	 *            text to send via slack
	 * 
	 *            method to send unknown messages to slack channel
	 * @throws JSONException
	 */
	private void sendAlertViaSlack(String idUser, String timestamp, String customerMessage) throws JSONException {
		String uri = "https://hooks.slack.com/services/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";
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		String input = "idUser:" + idUser + " \n time: " + timestamp + " \n text: " + customerMessage + "";
		JSONObject jsonSlack = new JSONObject();
		jsonSlack.put("text", input);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonSlack.toString(), headers);
		restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
	}

	private void checkLineFunction(@RequestBody Map<String, Object> obj) throws JSONException, IOException {
		String TOKEN = "OBna57cOodEGIIqhcSEjjpkjT0AUOl/AZNumYYcxT+H5T3ep6VRSXOOf5pyIRICy5QQ1ytWFUv1Ol5+1Pb2wOWk5+44idmCjlP6vancpqEmWHw9YZHZ0/2H4qn1jCl3AZ88XIo2WkFPylumplMuSlAdB04t89/1O/w1cDnyilFU=";

		JSONObject jsonResult = new JSONObject(obj);
		JSONObject rsl = jsonResult.getJSONObject("originalRequest");
		JSONObject data = rsl.getJSONObject("data");
		JSONObject source = data.getJSONObject("source");
		JSONObject message = data.getJSONObject("message");
		String idUser = source.getString("userId");
		String customerMessage = message.getString("text");
		String timestamp = jsonResult.getString("timestamp");
		JSONObject result = jsonResult.getJSONObject("result");
		JSONObject metadata = result.getJSONObject("metadata");
		String intentName = metadata.getString("intentName");
		JSONObject parameters = result.getJSONObject("parameters");
		JSONObject fulfillment = result.getJSONObject("fulfillment");
		String speech = fulfillment.getString("speech");

		List<EventHelper> eventHelpers = new ArrayList<>();
		eventHelpers.add(new EventHelper(1, "chooseLanguage", "initial", "C", null, "osaka"));
		eventHelpers.add(new EventHelper(2, "writeStation", "intermediate", "E", 1, "paris"));
		eventHelpers.add(new EventHelper(3, "keywordStation", "intermediate", "B", 2, "tokyo"));
		eventHelpers.add(new EventHelper(4, "whichTime", "intermediate", "B", 3, "london"));
		eventHelpers.add(new EventHelper(5, "whichJob", "intermediate", "f", 4, "help"));

		LinkedHashMap<String, String> hm = new LinkedHashMap<>();
		hm.put("Osaka", "osaka");
		hm.put("Tokyo", "tokyo");
		hm.put("London", "london");

		String msgTemplate = "Choose Language";
		String msgFirstAnswer = "English";
		String msgSecondAnswer = "Japanese";
		String titleTemplate = "Choose Language";
		String msgSecondAnswerToSend = "English";
		String msgFirstAnswerToSend = "Japanese";

		for (EventHelper eventHelper : eventHelpers) {
			if (intentName.equalsIgnoreCase(eventHelper.getIntentName()))

				switch (eventHelper.getTypeFunction().toLowerCase()) {
				/*
				 * case "a": typeBChoices(
				 * "https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png",
				 * "boldTitle ", " normalTitle", hm, "nextOrSeeMore", "nextOrSeeMoreAnswer",
				 * TOKEN, idUser); break;
				 */
				case "b":

					String imageURL = "https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png";
					String boldTitle = "boldTitle ";
					String normalTitle = " normalTitle";

					String nextOrSeeMore = "nextOrSeeMore";
					String nextOrSeeMoreAnswer = "nextOrSeeMore";

					int idEvent = 2;
					BQuestion bQuestion = new BQuestion(imageURL, boldTitle, normalTitle, hm, nextOrSeeMore,
							nextOrSeeMoreAnswer, idEvent);
					if (bQuestion.getIdEvent() == eventHelper.getIdEvent())
						typeBChoices(bQuestion.getImageURL(), bQuestion.getBoldTitle(), bQuestion.getNormalTitle(), hm,
								bQuestion.getNextOrSeeMore(), bQuestion.getNextOrSeeMoreAnswer(), TOKEN, idUser);
					break;
				case "c":
					CQuestion cQuestion = new CQuestion(msgTemplate, msgFirstAnswer, msgSecondAnswer, titleTemplate,
							msgSecondAnswerToSend, msgFirstAnswerToSend, 1);
					if (cQuestion.getIdEvent() == eventHelper.getIdEvent())

						typeCQuestion(cQuestion.getMsgTemplate(), cQuestion.getMsgFirstAnswer(),
								cQuestion.getMsgFirstAnswerToSend(), cQuestion.getMsgSecondAnswer(),
								cQuestion.getMsgSecondAnswerToSend(), cQuestion.getTitleTemplate(), TOKEN, idUser);
					break;

				case "e":

					break;
				case "d":
					typeDQuestion("https://cdn2.iconfinder.com/data/icons/employment-business/256/Job_Search-512.png",
							TOKEN, idUser);
					break;
				case "f":
					sendAlertViaSlack(idUser, timestamp, customerMessage);
					break;
				default:
					break;
				}

		}

	}

}
