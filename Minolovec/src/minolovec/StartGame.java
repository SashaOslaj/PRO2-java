package minolovec;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class StartGame extends JFrame {
	
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame fr = new JFrame("Minolovec");
		fr.setLayout(new BorderLayout());
		fr.add(new MineSweeper());

		fr.setSize(800, 800);
		fr.setResizable(true);
		fr.setLocationRelativeTo(null);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);

	}

}
