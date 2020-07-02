package minolovec;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class MineSweeper extends JPanel implements AWTEventListener, ActionListener {

	private static final long serialVersionUID = 1L;


	private int ROWS = 9;
	private int COLUMNS = 9;
	private static final int MAX_BOMB_COUNT = 10;
	private JPanel pnlMain = new JPanel(new GridLayout(ROWS, COLUMNS));
	private JLabel lblBombCount = new JLabel(MAX_BOMB_COUNT + "");
	private JButton btnReset = new JButton("Reset");
	JSpinner max_num_of_col = new JSpinner(new SpinnerNumberModel(9, 1, 30, 1));

	JSpinner num_of_mines = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
	JLabel label1 = new JLabel("Število vrstic");
	JLabel label2 = new JLabel("	Število min");



	private GameState state = GameState.NotStarted;

	public MineSweeper() {
		
		setLayout(new BorderLayout());
		add(pnlMain, BorderLayout.CENTER);
		createButtons(9,9,10);
		addControlPanel();

	}

	private void restartGame() {
		
		int max_col =(int) max_num_of_col.getValue();
		// System.out.println(max_col);
		// System.out.println((int) num_of_mines.getValue());
		max_col = max_col * max_col;
		
		if(max_col >= (int) num_of_mines.getValue()) {
			state = GameState.NotStarted; 
			pnlMain.removeAll();
			pnlMain.setLayout(new GridLayout((int) max_num_of_col.getValue(), (int) max_num_of_col.getValue()));
			createButtons((int) max_num_of_col.getValue(),(int) max_num_of_col.getValue(),(int) num_of_mines.getValue());
			pnlMain.updateUI();
			lblBombCount.setText("" + (int) num_of_mines.getValue());
			lblBombCount.updateUI();
		}
		else {
			JOptionPane.showMessageDialog(null, "NE MORE BIT VEC MIN KAKOR JE POLJ LOL");
		}
	}

	private void addControlPanel() {
		
		JPanel pnlN = new JPanel(new GridLayout(2,3));

		pnlN.add(lblBombCount);
		pnlN.add(label1);
		pnlN.add(max_num_of_col); 
		pnlN.add(btnReset);
		pnlN.add(label2);
		pnlN.add(num_of_mines);
		
		add(pnlN, BorderLayout.NORTH);
		btnReset.addActionListener(this);
	}

	private void createButtons(int ROWS,int COLUMNS,int MAX_BOMB_COUNT) {
		List<Point> lstBombsLocation = new ArrayList<Point>();
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				// System.out.println(lstBombsLocation);
				JButton btn = getButton(lstBombsLocation, ROWS * COLUMNS, new Point(row, col) {
					private static final long serialVersionUID = 1L;
					
					@Override
					public String toString() {
						return (int) getX() + ", " + (int) getY();
					}

					@Override
					public boolean equals(Object obj) {
						return ((Point) obj).getX() == getX() && ((Point) obj).getY() == getY();
					}
				},MAX_BOMB_COUNT);
				pnlMain.add(btn);
			}
		}
		while (lstBombsLocation.size() < MAX_BOMB_COUNT) {


			updateBomds(lstBombsLocation, pnlMain.getComponents(),COLUMNS,ROWS*COLUMNS);
		}
		for (Component c : pnlMain.getComponents()) {
			updateBombCount((GameButton) c, pnlMain.getComponents());
		}
		// System.out.println("Total Bomb Count: " + lstBombsLocation.size());
	}

	private void updateBomds(List<Point> lstBombsLocation, Component[] components,int COLUMNS,int TOTAL) {
		// Add the bombs to bomb list.
		
		// int currentPosition = new Double(((location.x) * COLUMNS ) +
		// location.getY()).intValue();
		Random r = new Random();
		
		for (Component c : components) {
			
			Point location = ((GameButton) c).getPosition();
			int currentPosition = new Double(((location.x) * COLUMNS) + location.getY()).intValue();
			int bombLocation = r.nextInt(TOTAL);
			
			if (bombLocation == currentPosition) {
				((GameButton) c).setBomb(true);
				lstBombsLocation.add(((GameButton) c).getPosition());
				return;
			}
		}
	}

	private GameButton getButton(List<Point> lstBombsLocation, int totalLocations, Point location, int MAX_BOMB_COUNT ) {

		GameButton btn = new GameButton(location);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setFocusable(false);
		
		if (lstBombsLocation.size() < MAX_BOMB_COUNT) {
			
			if (isBomb()) {
				btn.setBomb(true);
				lstBombsLocation.add(location);
			}
		}
		
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (((GameButton) mouseEvent.getSource()).isEnabled() == false) {
					return;
				}
				if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
					if (((GameButton) mouseEvent.getSource()).getState() == State.Marked) {
						((GameButton) mouseEvent.getSource()).setState(State.Initial);
						lblBombCount.setText((Long.parseLong(lblBombCount.getText()) + 1) + "");
						((GameButton) mouseEvent.getSource()).updateUI();
						return;
					}
					((GameButton) mouseEvent.getSource()).setState(State.Clicked);
					if (((GameButton) mouseEvent.getSource()).isBomb()) {
						blastBombs();
						return;
					} else {
						if (((GameButton) mouseEvent.getSource()).getBombCount() == 0) {
							updateSurroundingZeros(((GameButton) mouseEvent.getSource()).getPosition());
						}
					}
					if (!checkGameState()) {
						((GameButton) mouseEvent.getSource()).setEnabled(false);
					}
				} else if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
					if (((GameButton) mouseEvent.getSource()).getState() == State.Marked) {
						((GameButton) mouseEvent.getSource()).setState(State.Initial);
						lblBombCount.setText((Long.parseLong(lblBombCount.getText()) + 1) + "");
					} else {
						((GameButton) mouseEvent.getSource()).setState(State.Marked);
						lblBombCount.setText((Long.parseLong(lblBombCount.getText()) - 1) + "");
					}
				}
				((GameButton) mouseEvent.getSource()).updateUI();
			}
		});
		return btn;
	}

	private boolean checkGameState() {
		boolean isWin = false;
		for (Component c : pnlMain.getComponents()) {
			GameButton b = (GameButton) c;
			if (b.getState() != State.Clicked) {
				if (b.isBomb()) {
					isWin = true;
				} else {
					return false;
				}
			}
		}
		if (isWin) {
			state = GameState.Finished;
			for (Component c : pnlMain.getComponents()) {
				GameButton b = (GameButton) c;
				if (b.isBomb()) {
					b.setState(State.Marked);
				}
				b.setEnabled(false);

			}
			JOptionPane.showMessageDialog(this, "You win the game.", "Congrats", JOptionPane.INFORMATION_MESSAGE, null);
		}
		return isWin;
	}

	private void updateSurroundingZeros(Point currentPoint) {
		Point[] points = getSurroundings(currentPoint);

		for (Point p : points) {
			GameButton b = getButtonAt(pnlMain.getComponents(), p);
			if (b != null && b.getBombCount() == 0 && b.getState() != State.Clicked && b.getState() != State.Marked && b.isBomb() == false) {
				b.setState(State.Clicked);
				updateSurroundingZeros(b.getPosition());
				b.updateUI();
			}
			if (b != null && b.getBombCount() > 0 && b.getState() != State.Clicked && b.getState() != State.Marked && b.isBomb() == false) {
				b.setEnabled(false);
				b.setState(State.Clicked);
				b.updateUI();
			}
		}
	}

	private void blastBombs() {
		int blastCount = 0;
		for (Component c : pnlMain.getComponents()) {
			((GameButton) c).setEnabled(false);
			((GameButton) c).transferFocus();
			if (((GameButton) c).isBomb() && ((GameButton) c).getState() != State.Marked) {
				((GameButton) c).setState(State.Clicked);
				((GameButton) c).updateUI();
				blastCount++;
			}
			if (((GameButton) c).isBomb() == false && ((GameButton) c).getState() == State.Marked) {
				((GameButton) c).setState(State.WrongMarked);
			}
		}
		lblBombCount.setText("" + blastCount);
		lblBombCount.updateUI();
		state = GameState.Finished;
		JOptionPane.showMessageDialog(this, "You loose the game.", "Game Over", JOptionPane.ERROR_MESSAGE, null);
		for (Component c : pnlMain.getComponents()) {
			GameButton b = (GameButton) c;
			b.setEnabled(false);
		}
	}

	private boolean isBomb() {
		Random r = new Random();
		return r.nextInt(ROWS) == 1;
	}


	private Point[] getSurroundings(Point cPoint) {
		
		int cX = (int) cPoint.getX();
		int cY = (int) cPoint.getY();
		
		Point[] points = { 
				new Point(cX - 1, cY - 1), new Point(cX - 1, cY), new Point(cX - 1, cY + 1), 
				new Point(cX, cY - 1), new Point(cX, cY + 1), new Point(cX + 1, cY - 1), 
				new Point(cX + 1, cY), new Point(cX + 1, cY + 1) };
		return points;
	}

	private void updateBombCount(GameButton btn, Component[] components) {
		Point[] points = getSurroundings(btn.getPosition());

		for (Point p : points) {
			GameButton b = getButtonAt(components, p);
			if (b != null && b.isBomb()) {
				btn.setBombCount(btn.getBombCount() + 1);
			}
		}
		btn.setText(btn.getBombCount() + "");
	}

	private GameButton getButtonAt(Component[] components, Point position) {
		
		for (Component btn : components) {
			if ((((GameButton) btn).getPosition().equals(position))) {
				return (GameButton) btn;
			}
		}
		return null;
	}

	public void eventDispatched(AWTEvent event) {
		
		if (KeyEvent.class.isInstance(event) && ((KeyEvent) (event)).getID() == KeyEvent.KEY_RELEASED) {
			if (((KeyEvent) (event)).getKeyCode() == KeyEvent.VK_F2) {
				restartGame();
			}
			if (((KeyEvent) (event)).getKeyCode() == KeyEvent.VK_F12) {
				for (Component c : pnlMain.getComponents()) {
					GameButton b = (GameButton) c;
					if (b.isBomb() == false) {
						b.setState(State.Clicked);
					} else {
						b.setState(State.Marked);
					}
					b.setEnabled(false);
				}
				checkGameState();
			}
		}
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == btnReset) {
			restartGame();
		}
	}
}
