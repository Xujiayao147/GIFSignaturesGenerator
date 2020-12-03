package tools;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.Dialogs;

/**
 * @author Xujiayao
 */
public class Update {
	
	public static double downloadProgress = 0;
	
	public static String[] datas;
	
	public static void start(boolean auto) {
		String data = Update.downloadJSON("https://cdn.jsdelivr.net/gh/Xujiayao147/PFSignaturesGenerator/update/version.json");
		
		try {
			if (data == null) {
				data = Update.downloadJSON("https://raw.githubusercontent.com/Xujiayao147/PFSignaturesGenerator/master/update/version.json");
			}
		} catch (Exception e) {
			data = Update.downloadJSON("https://cdn.jsdelivr.net/gh/Xujiayao147/PFSignaturesGenerator/update/version.json");
		}
		
		datas = parseData(data);
		
		Platform.runLater(() -> {
			if (datas[0].equals(Variables.version)) {
				if (!auto) {
					if (Variables.language.equals("English")) {
						Dialogs.showMessageDialog("You are using the latest version of PF Signatures Generator.", "Check for Updates", true);
					} else {
						Dialogs.showMessageDialog("您正在使用最新版本的PF签名图生成工具。", "检查更新", false);
					}
				}
			} else {
				boolean response;
				
				datas[1] = datas[1].replaceAll("\\\\n", "\n");
				datas[2] = datas[2].replaceAll("\\\\n", "\n");
				
				if (Variables.language.equals("English")) {
					response = Dialogs.showConfirmDialog("PF Signatures Generator " + datas[0] + " is available (You are v2.0). Do you want to update now?\n\nRelease notes: \n\n" + datas[2], "Check for Updates", "An update is available!", true);
				} else {
					response = Dialogs.showConfirmDialog("PF签名图生成工具 " + datas[0] + " 现在可用（您是 v2.0）。您想要现在更新吗？\n\n更新说明：\n\n" + datas[1], "检查更新", "有新版本可用！", false);
				}
				
				if (response) {
					Dialogs.showDownloadingDialog();
					
					new Thread(new DownloadThread(datas[4], datas[5])).start();
				}
			}
		});
	}
	
	public static File saveFile(byte[] data) {
		try {
			FileChooser fileChooser = new FileChooser();
			
			try {
				fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			} catch (Exception e) {
				Dialogs.showExceptionDialog(e);
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
			}
			
			fileChooser.setInitialFileName("Update_PFSignaturesGenerator.exe");
			
			if (Variables.language.equals("中文")) {
				fileChooser.setTitle("保存新版本");
	            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("应用程序", "*.exe"));
			} else if (Variables.language.equals("English")) {
				fileChooser.setTitle("Save new version");
	            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Application", "*.exe"));
			}
			
			File file = fileChooser.showSaveDialog(new Stage());
			
			if(file != null){
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data);
				fos.close();
				
				return file;
			}
		} catch (Exception e) {
			Platform.runLater(() -> {
				Dialogs.showExceptionDialog(e);
			});
		}
		
		return null;
	}
	
	static byte[] downloadUpdate(String link) {
		downloadProgress = 0;
		
		byte[] data = null;
		
		try {
			URLConnection conn = new URL(link).openConnection();
			
			conn.setUseCaches(false);
			
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36");
			
			InputStream inputStream = conn.getInputStream();
			
			data = readInputStream(inputStream, (double) conn.getContentLength());
			
			Platform.runLater(() -> {
				Dialogs.bar.setProgress(1);
				
				if (Variables.language.equals("English")) {
					Dialogs.text.setText("Finished (100%)");
				} else {
					Dialogs.text.setText("完成 (100%)");
				}
			});
		} catch (Exception e) {
			Platform.runLater(() -> {
				Dialogs.showExceptionDialog(e);
			});
		}
		
		return data;
	}
	
	static byte[] readInputStream(InputStream inputStream, Double length) {
		try {
			byte[] buffer = new byte[1];
			int len = 0;
			
			double temp1 = 0;
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			while ((len = inputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
				
				downloadProgress = downloadProgress + (100 / Double.parseDouble(datas[3]));
				
				double temp2 = Double.parseDouble(String.format("%.1f", downloadProgress));
				
				if (temp1 != temp2) {
					Platform.runLater(() -> {
						Dialogs.bar.setProgress(downloadProgress / 100);
						
						if (Variables.language.equals("English")) {
							Dialogs.text.setText("Downloading (" + temp2 + "%)");
						} else {
							Dialogs.text.setText("下载中 (" + temp2 + "%)");
						}
					});
				}
				
				temp1 = temp2;
			}
			
			bos.close();
			return bos.toByteArray();
		} catch (Exception e) {
			Platform.runLater(() -> {
				Dialogs.showExceptionDialog(e);
			});
		}
		
		return null;
	}
	
	static String[] parseData(String data) {
		String[] datas = new String[6];
		
		try {
			data = data.substring(12);
			datas[0] = data.substring(0, data.indexOf("\""));
			
			data = data.substring(data.indexOf("\"")).substring(12);
			datas[1] = data.substring(0, data.indexOf("\""));
			
			data = data.substring(data.indexOf("\"")).substring(12);
			datas[2] = data.substring(0, data.indexOf("\""));
			
			data = data.substring(data.indexOf("\"")).substring(14);
			datas[3] = data.substring(0, data.indexOf("\""));
			
			data = data.substring(data.indexOf("\"")).substring(11);
			datas[4] = data.substring(0, data.indexOf("\""));
			
			data = data.substring(data.indexOf("\"")).substring(11);
			datas[5] = data.substring(0, data.indexOf("\""));
		} catch (Exception e) {
			Platform.runLater(() -> {
				Dialogs.showExceptionDialog(e);
			});
		}
		
		return datas;
	}
	
	static String downloadJSON(String link) {
		String data = null;
		
		try {
			URLConnection conn = new URL(link).openConnection();
			
			conn.setUseCaches(false);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
			
			data = reader.readLine();
			
			reader.close();
		} catch (Exception e) {
			Platform.runLater(() -> {
				Dialogs.showExceptionDialog(e);
			});
		}
		
		return data;
	}
}

class DownloadThread implements Runnable {
	String link;
	String altLink;
	
	public DownloadThread(String link, String altLink) {
		this.link = link;
		this.altLink = altLink;
	}
	
	@Override
	public void run() {
		try {
			byte[] datas = Update.downloadUpdate(link);
			
			byte[] data[] = {datas};
			
			try {
				if (data[0] == null) {
					data[0] = Update.downloadUpdate(altLink);
				}
			} catch (Exception e) {
				data[0] = Update.downloadUpdate(altLink);
			}
			
			Platform.runLater(() -> {
				File file = Update.saveFile(data[0]);
				
				if (file != null) {
					try {
						Desktop.getDesktop().open(file);
						
						System.exit(0);
					} catch (Exception e) {
						Dialogs.showExceptionDialog(e);
					}
				} else {
					Dialogs.dialog.getDialogPane().getButtonTypes().add(new ButtonType("", ButtonBar.ButtonData.OK_DONE));
					Dialogs.dialog.close();
				}
			});
		} catch (Exception e) {
			Platform.runLater(() -> {
				Dialogs.showExceptionDialog(e);
			});
		}
	}
}