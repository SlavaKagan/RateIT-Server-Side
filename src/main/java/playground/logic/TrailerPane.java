package playground.logic;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.services.customsearch.model.Search;
import com.google.api.services.samples.youtube.cmdline.data.YTSearch;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

public class TrailerPane extends BorderPane {

	final public static String URL = "https://www.youtube.com/embed/";
	final public static String CX = "002030300570137734147:nsqqao_o4aq";
	final public static String GOOGLE_API_KEY = "AIzaSyBWw_z28frnKg0NNVjemfUqTBcNr92fCUE";

	public TrailerPane() {
	}

	public TrailerPane(String searchQuery) throws GeneralSecurityException, IOException {
		super();
		String[] parts = getVideo(searchQuery);
		Search result = getResult(searchQuery);
		this.setLeft(this.getYouTubeVideo(parts[0], result));
		this.setCenter(this.getPoster(result));

	}

	public String[] getVideo(String term) {
		return new YTSearch().getSearchResultIDAndNamesList(term).get(0).split(",");
	}

	// Search Method (Keep in any case)
	public Search getResult(String term) throws IOException, GeneralSecurityException {
		Customsearch cs = new Customsearch.Builder(GoogleNetHttpTransport.newTrustedTransport(),
				JacksonFactory.getDefaultInstance(), null).setApplicationName("MyApplication")
						.setGoogleClientRequestInitializer(new CustomsearchRequestInitializer(GOOGLE_API_KEY)).build();

		Customsearch.Cse.List list = cs.cse().list(term).setCx(CX);
		list.setSearchType("image");
		list.setImgSize("small");
		list.setImgType("photo");

		return list.execute();
	}

	public WebView getYouTubeVideo(String url, Search result) {
		WebView webview = new WebView();
		webview.getEngine().load(URL + url);
		webview.setPrefSize(640, 390);
		return webview;
	}

	public Pane getPoster(Search result) {
		Pane imagePane = new Pane();
		if (!result.getItems().isEmpty()) {
			ImageView iv = new ImageView(new Image(result.getItems().get(0).getLink(), 400, 390, false, true));
			imagePane.getChildren().add(iv);
		} else {
			Text t = new Text("No results");
			t.autosize();
			imagePane.getChildren().add(t);
		}

		imagePane.setPrefSize(400, 390);
		return imagePane;
	}
}
