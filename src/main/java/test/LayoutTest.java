package test;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.nullfish.lib.tablelayout.HtmlTablePanel;

public class LayoutTest extends JFrame {
	private JLabel label = new JLabel("test");
	private JButton button = new JButton("button");
	
	public LayoutTest() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		HtmlTablePanel panel = new HtmlTablePanel("/test/layout.xml");
		panel.layoutByMemberName(this);
		getContentPane().add(panel);
	}
	
	public static void main(String[] args) {
		try {
			LayoutTest test = new LayoutTest();
			test.pack();
			test.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

