/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package screenanal;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 *
 * @author aden
 */
public class Getscreen {

    static BufferedImage grabScreen(int height, int width, int TYPE_INT_RGB) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private Rectangle Rectangle;
    public static BufferedImage grabScreen() throws AWTException { 
        try {
            return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())) ;
        } catch (SecurityException | AWTException e) {
        }
        return null;
    }
    
}
