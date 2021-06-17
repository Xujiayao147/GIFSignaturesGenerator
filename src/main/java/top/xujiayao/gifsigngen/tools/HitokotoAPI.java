package top.xujiayao.gifsigngen.tools;

import javafx.application.Platform;
import top.xujiayao.gifsigngen.Main;
import top.xujiayao.gifsigngen.ui.Dialogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Xujiayao
 */
public class HitokotoAPI implements Runnable {

	@Override
	public void run() {
		try {
			Variables.hitokotoData = ParseJSON.parseHitokotoJSON(getHitokoto());
		} catch (Exception e) {
			Platform.runLater(() -> Dialogs.showExceptionDialog(e));
		}

		if (Variables.hitokotoData.length != 0) {
			Variables.displayHitokotoData = Variables.hitokotoData[0] + "\n出自 " + Variables.hitokotoData[1];

			if (Variables.hitokotoData[0].length() > 21) {
				Platform.runLater(() -> Main.loginUI.text2.setY(438));
			}
		} else {
			Variables.displayHitokotoData = "没有个性，如何签名？\n出自 Xujiayao";
		}

		Platform.runLater(() -> Main.loginUI.text2.setText(Variables.displayHitokotoData));
	}

	private String getHitokoto() throws IOException {
		String data = null;

		URLConnection conn = new URL("https://v1.hitokoto.cn/").openConnection();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(conn).getInputStream(), StandardCharsets.UTF_8))) {
			data = reader.readLine();
		} catch (Exception e) {
			Platform.runLater(() -> Dialogs.showExceptionDialog(e));
		}

		return data;
	}
}