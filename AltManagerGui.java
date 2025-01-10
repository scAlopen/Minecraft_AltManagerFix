package PVP.AM;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import PVP.AM.microsoft.GuiLoginMicrosoft;
import PVP.MM.MM;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class AltManagerGui extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {

        this.buttonList.add(new GuiButton(1, width / 2 + 4 + 50, height - 48, 100, 20, "Cancel"));
        this.buttonList.add(new GuiButton(2, width / 2 - 50, height - 48, 100, 20, "Use Cracked"));
        this.buttonList.add(new GuiButton(3, width / 2 - 150 - 4, height - 48, 100, 20, "Use Microsoft"));
        this.buttonList.add(new GuiButton(5, width / 2 - 95 - 4, height - 24, 190, 20, "Subscribe to ALOPEN!"));
        super.initGui();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            mc.displayGuiScreen(new MM());
        }
        if(button.id == 2){
            mc.displayGuiScreen(new GuiLogin());
        }
        if(button.id == 3){
            mc.displayGuiScreen(new GuiLoginMicrosoft());
        }
         
        if (button.id == 5) {
            openUrl("https://www.youtube.com/@aolpen");  // Replace with the actual URL you want to open
        }
    }
        public void openUrl(String urlString) {
            try {
                // Create a URI object from the URL string
                URI uri = new URI(urlString);
                
                // Check if Desktop is supported and then open the URL
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.browse(uri);  // Open the URL in the default web browser
                }
            } catch (IOException | java.net.URISyntaxException e) {
                e.printStackTrace();  // Handle any errors that might occur while opening the URL
            }
        }

       
       
     
    }

