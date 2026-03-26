package com.startup.TFC;

import com.startup.TFC.services.ServiceStudent;
import com.startup.TFC.views.MainMenuFrame;
import com.startup.TFC.views.StudentFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.SwingUtilities;

@SpringBootApplication
public class TfcApplication {

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");

		SpringApplication app = new SpringApplication(TfcApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ApplicationContext context = app.run(args);

		SwingUtilities.invokeLater(() -> {
			MainMenuFrame menu = new MainMenuFrame(context);
			menu.setVisible(true);
		});
	}
}