package minolovec;


import java.awt.Color;
import java.awt.Point;

import javax.swing.JButton;

public class GameButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	
    private boolean isBomb = false;
    private Point position = null;
    private int bombCount = 0;
    private State state = State.Initial;

    public void setState(State state) {
      this.state = state;
      if (getBombCount() == 0 && !isBomb) {
        setEnabled(false);
      }
    }

    public State getState() {
      return state;
    }

    public int getBombCount() {
      return bombCount;
    }

    public void setBombCount(int bombCount) {
      this.bombCount = bombCount;
    }

    public GameButton(Point position) {
      setPosition(position);
      setText(position.toString());
    }

    public Point getPosition() {
      return position;
    }

    public void setPosition(Point position) {
      this.position = position;
    }

    public boolean isBomb() {
      return isBomb;
    }

    public void setBomb(boolean isBomb) {
      this.isBomb = isBomb;
    }

    @Override
    public String getText() {
      if (state == State.Initial) {
        return "";
      }
      if (state == State.Marked) {
        return "<html><font size='5'><b>\uD83D\uDCA3</b></font></html>";
      }
      if (state == State.Clicked) {
        if (isBomb) {
          return "<html><font size='5'><b>\uD83D\uDCA3</b></font></html>";
        } else {
          if (getBombCount() > 0)
            return getBombCount() + "";
          else
            return "";
        }
        
      }
      if (state == State.WrongMarked) {
        return "";
      }
      return super.getText();
    }

    @Override
    public Color getBackground() {
      if (state == State.Clicked) {
        if (isBomb) {
          return Color.BLACK;
        }
        if (getBombCount() > 0) {
          return Color.WHITE;
        }
      }
      if (isEnabled()) {
        return Color.GRAY;
      } else {
        return super.getBackground();
      }
    }
  }