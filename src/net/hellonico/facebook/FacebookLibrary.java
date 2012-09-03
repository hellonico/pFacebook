/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package net.hellonico.facebook;

import static java.lang.System.out;

import java.awt.Desktop;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import processing.core.PApplet;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;

public class FacebookLibrary {

	PApplet myParent;

	public final static String VERSION = "##library.prettyVersion##";

	private static final Token EMPTY_TOKEN = null;

	private FacebookClient facebookClient;

	public FacebookLibrary(PApplet theParent) throws Exception {
		myParent = theParent;
		String YOUR_API_KEY = "233100973389749";
		String YOUR_API_SECRET = "0772afb21ddf0d0fab8200c1ff707319";
		/*
		 * https://github.com/fernandezpablo85/scribe-java/blob/master/src/test/java
		 * /org/scribe/examples/FacebookExample.java
		 */
		OAuthService service = new ServiceBuilder()
				.provider(FacebookApi.class)
				.apiKey(YOUR_API_KEY).apiSecret(YOUR_API_SECRET)
				.callback("http://localhost:8080/oauth_callback/")
				.build();

		String url = service.getAuthorizationUrl(null);
		Desktop.getDesktop().browse(new URL(url).toURI());
//		JOptionPane.showMessageDialog(null,
//				"Press ok to continue once you have authenticated.");

		Scanner in = new Scanner(System.in);
		Verifier verifier = new Verifier(in.nextLine());
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);

		String token = accessToken.getToken();
		String secret = accessToken.getSecret();

		out.println(token + "\n" + secret);
		facebookClient = new DefaultFacebookClient(token);

		User user = facebookClient.fetchObject("me", User.class);
		Page page = facebookClient.fetchObject("cocacola", Page.class);

		out.println("User name: " + user.getName());
		out.println("Page likes: " + page.getLikes());
	}

	public FacebookLibrary(String token) {
		facebookClient = new DefaultFacebookClient(token);
	}

	public void myFriends() throws Exception {
		Connection<User> myFriends = facebookClient.fetchConnection(
				"me/friends", User.class);
		out.println("Count of my friends: " + myFriends.getData().size());

		for (User friend : myFriends.getData())
			out.println("User: " + friend.getName());
	}

	public void myFeeds() throws Exception {
		Connection<Post> myFeed = facebookClient.fetchConnection("me/feed",
				Post.class);
		out.println("First item in my feed: " + myFeed.getData().get(0));
	}

	public void post(String message) throws Exception {
		FacebookType publishMessageResponse = facebookClient.publish("me/feed",
				FacebookType.class, Parameter.with("message", message));

		out.println("Published message ID: " + publishMessageResponse.getId());
	}

	public static String version() {
		return VERSION;
	}

	public static void main(String[] args) throws Exception {
		String myToken = "AAADUAQy3N7UBALiQbk53RqzIZAq68xfZAppFQ67WXMsEzIcgwZCc2tDKLRFjucGgIDdRAtiaGQbjrqkfU6yWLjzMNA2AbNt1776JOd7AAZDZD";
		FacebookLibrary api = new FacebookLibrary(myToken);
		api.myFriends();
		//api.post("月曜日のオフィスは。。。");
	}
}
