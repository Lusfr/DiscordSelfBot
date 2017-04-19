package me.alexander.discordbot.SelfBot;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import me.alexander.discordbot.Main;

public class ICommandSelfBot {

	static Random rand = new Random();

	public static List<String> extractUrls(String text) {
		List<String> containedUrls = new ArrayList<String>();
		String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = pattern.matcher(text);
		while (urlMatcher.find()) {
			containedUrls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
		}
		return containedUrls;
	}

	public static User getUser(String tag) {
		for (Server c : Main.bot.getAPI().getServers()) {
			for (User u : c.getMembers()) {
				if (u.getMentionTag().equals(tag) || u.getId().equals(tag)) {
					return u;
				}
			}
		}
		return null;
	}

	public static void userInfo(Message m) {
		m.delete();
		EmbedBuilder emb = new EmbedBuilder();
		emb.setColor(Color.cyan);
		User user = getUser(m.getContent().replace("/user ", ""));
		if (user == null) {
			user = Main.bot.getAPI().getYourself();
		}
		String output = "Username\n" + user.getName();
		output += "\n\nUser ID\n" + user.getId();
		output += "\n\nMention Tag\n" + user.getMentionTag();
		output += "\n\nAvatar\n" + user.getAvatarUrl();
		output += "\n\nStatus\n" + user.getStatus().name() + "\n";

		emb.setTitle("Profile of " + user.getName() + ":");
		emb.setDescription(output);
		emb.setThumbnail(user.getAvatarUrl().toString());
		emb.setFooter(
				"Deft's Bot | Message sent "
						+ new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()),
				"https://avatars1.githubusercontent.com/u/6422482?v=3&s=400");
		m.reply("", emb);
	}

	public static void embed(Message message) {
		if (message.getContent().startsWith("/user")) {
			userInfo(message);
			return;
		}
		if (!message.getContent().startsWith("/embed")) {
			return;
		}
		String msg = message.getContent().replace("/embed ", "");
		message.delete();

		try {
			EmbedBuilder emb = new EmbedBuilder();
			emb.setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()).brighter());

			emb.setFooter(
					"Deft's Bot | Message sent "
							+ new SimpleDateFormat("MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()),
					"https://avatars1.githubusercontent.com/u/6422482?v=3&s=400");

			for (String url : extractUrls(msg)) {
				if (url.endsWith(".png") || url.endsWith(".jpeg") || url.endsWith(".jpg")) {
					emb.setImage(url);
					msg = msg.replace(url, "");
					break;
				}
			}

			if (msg.contains("**")) {
				String title = msg.split("\\*\\*")[1].split("\\*\\*")[0];
				msg = msg.replace("**" + title + "**", "");
				emb.setTitle(title);
			} else {
				emb.setAuthor("Deftware says:");
			}

			if (msg.contains("--")) {
				String footer = msg.split("\\-\\-")[1].split("\\-\\-")[0];
				msg = msg.replace("--" + footer + "--", "");
				emb.setFooter(footer);
			}

			emb.setDescription(msg);
			message.reply("", emb);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void doCmd(String in, final Message message, final SelfBot bot, final User u) {
		if (u != bot.getAPI().getYourself()) {
			return;
		}
		String cmd = in;
		if (cmd.contains(" ")) {
			cmd = cmd.split(" ")[0];
		}
		if (!cmd.startsWith("/")) {
			return;
		}
		switch (cmd.toLowerCase()) {
		case "/shutdown":
			message.delete();
			bot.disconnect();
			break;
		default:
			break;
		}
	}

}