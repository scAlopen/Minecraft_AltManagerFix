package PVP.AM.openauth.microsoft;

import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.concurrent.CompletableFuture;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;

public class LoginFrame extends JFrame {
    private CompletableFuture<String> future;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    public LoginFrame() {
        this.setTitle("Sign in to Minecraft");
        this.setSize(750, 750);
        this.setLocationRelativeTo(null);
        this.setContentPane(new JFXPanel());

        // Initialize the password field and checkbox for password visibility
        initPasswordField();
    }

    private void initPasswordField() {
        // Create a panel for the login form elements
        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Create password field
        passwordField = new JPasswordField();
        passwordField.setBounds(50, 50, 200, 30);
        panel.add(passwordField);

        // Create the checkbox to show/hide password
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBounds(50, 90, 150, 30);
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        panel.add(showPasswordCheckBox);

        // Add the panel to the frame
        this.setContentPane(panel);
    }

    // Toggles password visibility
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0); // Show password
        } else {
            passwordField.setEchoChar('*'); // Hide password
        }
    }

    public CompletableFuture<String> start(String url) {
        if (this.future != null) {
            return this.future;
        }

        this.future = new CompletableFuture<>();
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                future.completeExceptionally(new MicrosoftAuthenticationException("User closed the authentication window"));
            }
        });

        Platform.runLater(() -> this.init(url));
        return this.future;
    }

    protected void init(String url) {
        WebView webView = new WebView();
        JFXPanel content = (JFXPanel) this.getContentPane();
        content.setScene(new Scene(webView, this.getWidth(), this.getHeight()));

        // Listen for the OAuth token in the URL after login
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("access_token")) {
                this.setVisible(false);
                this.future.complete(newValue);
            }
        });
        webView.getEngine().load(url);

        this.setVisible(true);
    }

    // Override HTTP handler
    protected static void overrideFactory() {
        URL.setURLStreamHandlerFactory(protocol -> {
            if ("https".equals(protocol)) {
                return new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(URL url) throws IOException {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Directly use openConnection
                        
                        // Handle Microsoft login OAuth URLs specifically
                        if (isMicrosoftLoginUrl(url)) {
                            return new MicrosoftPatchedHttpURLConnection(url, connection); // Use your custom connection class
                        }

                        return connection;
                    }

                    private boolean isMicrosoftLoginUrl(URL url) {
                        String host = url.getHost();
                        String path = url.getPath();

                        return ("login.microsoftonline.com".equals(host) && path.endsWith("/oauth2/authorize"))
                                || ("login.live.com".equals(host) && "/oauth20_authorize.srf".equals(path))
                                || ("login.live.com".equals(host) && "/ppsecure/post.srf".equals(path))
                                || ("login.microsoftonline.com".equals(host) && "/login.srf".equals(path))
                                || ("login.microsoftonline.com".equals(host) && path.endsWith("/login"))
                                || ("login.microsoftonline.com".equals(host) && path.endsWith("/SAS/ProcessAuth"))
                                || ("login.microsoftonline.com".equals(host) && path.endsWith("/federation/oauth2"))
                                || ("login.microsoftonline.com".equals(host) && path.endsWith("/oauth2/v2.0/authorize"));
                    }
                };
            }
            return null;
        });
    }
}
