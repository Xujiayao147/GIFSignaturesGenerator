package tools;

import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import javafx.application.Platform;
import ui.Dialogs;

/**
 * @author Xujiayao
 */
public class SystemTray {

	public static java.awt.SystemTray tray;
	public static TrayIcon trayIcon;

	public static void displayTray() {
		new Thread(new DisplayTrayThread()).start();
	}
}

class DisplayTrayThread implements Runnable {
	@Override
	public void run() {
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				Platform.runLater(() -> {
					Dialogs.showExceptionDialog(e);
				});
			}
			
			SystemTray.tray = java.awt.SystemTray.getSystemTray();
			
			JPopupMenu menu = new JPopupMenu();

			JMenuItem item1 = new JMenuItem("首选项");
			menu.add(item1);
			
			JMenuItem item2 = new JMenuItem("检查更新");
			menu.add(item2);
			
			JMenuItem item3 = new JMenuItem("关于");
			menu.add(item3);

			JMenuItem item4 = new JMenuItem("退出");
			menu.add(item4);
			
			if (Variables.language.equals("English")) {
				item1.setText("Preferences");
				item2.setText("Check for Updates");
				item3.setText("About");
				item4.setText("Quit");
				
				SystemTray.trayIcon = new TrayIcon(Variables.icon.get(Variables.icon.size() - 1), "PF Signatures Generator");
			} else {
				SystemTray.trayIcon = new TrayIcon(Variables.icon.get(Variables.icon.size() - 1), "PF签名图生成工具");
			}
			
			SystemTray.tray.add(SystemTray.trayIcon);

			SystemTray.trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 3) {
						menu.setLocation(e.getX(), e.getY() - 95);
						menu.setInvoker(menu);
						menu.setVisible(true);
					} else {
						menu.setVisible(false);
					}
				}
			});

			item1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Platform.runLater(() -> {
						Dialogs.showPreferencesDialog();
					});
				}
			});

			item2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Update.start(false);
				}
			});

			item3.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Platform.runLater(() -> {
						Dialogs.showAboutDialog();
					});
				}
			});

			item4.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			
			if (Variables.language.equals("English")) {
				SystemTray.trayIcon.displayMessage("PF Signatures Generator", "PF Signatures Generator is running", TrayIcon.MessageType.NONE);
			} else {
				SystemTray.trayIcon.displayMessage("PF签名图生成工具", "PF签名图生成工具正在运行", TrayIcon.MessageType.NONE);
			}
		} catch (Exception e) {
			Platform.runLater(() -> {
				Dialogs.showExceptionDialog(e);
			});
		}
	}
}