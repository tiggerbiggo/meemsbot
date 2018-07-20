package com.tiggerbiggo.meemsbot.reactlet.fourinarow;

import com.tiggerbiggo.meemsbot.reactlet.ReactProcess;
import com.tiggerbiggo.meemsbot.reactlet.ReactableMessage;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import old.core.MessageTools;

public class FourInARow extends ReactableMessage {

  public static final String[] NUMBEREMOTES =
      {"1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "🔟"}; //"0⃣",

  public static final String BLANKEMOTE = "\uD83D\uDD32";

  public final int width, height;
  private CellState[][] board;
  private String P1Emote, P2Emote;
  private User P1, P2, currentPlayer;

  public FourInARow(MessageChannel c, int _width, int _height, String _P1Emote, String _P2Emote, User _P1, User _P2) {
    super(c, true, true);

    if (_width <= 0 || _height <= 0) {
      throw new IllegalArgumentException(
          "Width and height must both be >= 1 and <= 10. Width: " + _width + ", Height: "
              + _height);
    }

    width = _width;
    height = _height;

    P1Emote = _P1Emote;
    P2Emote = _P2Emote;

    P1 = _P1;
    P2 = _P2;

    currentPlayer = P1;

    board = new CellState[width][height];
    clearBoard();

    control = MessageTools.sendMessage("Please wait...", c);
    setupNumberReacts();

    control.editMessage(renderMessage()).queue();
  }

  private String renderMessage() {
    StringBuilder renderer = new StringBuilder();
    for (int i = 0; i < width; i++) {
      renderer.append(NUMBEREMOTES[i]);
    }
    renderer.append("\n");
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        renderer.append(getEmoteFromState(board[j][i]));
      }
      renderer.append("\n");
    }
    renderer.append("\nCurrent player: ")
        .append(getEmoteFromState(getStateFromPlayer(currentPlayer)));
    return renderer.toString();
  }

  private void setupNumberReacts() {
    for (int i = 0; i < width; i++) {
      addReactProcess(new RowReactProcess(NUMBEREMOTES[i], i) {
        @Override
        protected void onReact(GenericMessageReactionEvent e) {
          if (e.getUser().equals(currentPlayer)) {
            if (dropPiece(number, getStateFromPlayer(e.getUser()))) {
              swapPlayers(e.getUser());
              CellState win = detectWinner();
              if(win != null){
                c.sendMessage(renderMessage()).queue();
                if(win.equals(CellState.BLANK))
                  c.sendMessage("Stalemate... Try again.").queue();
                else
                  c.sendMessage(getEmoteFromState(win) + " won!").queue();
                destroy();
              }
            }
            control.editMessage(renderMessage()).queue();
          }
        }
      });
    }
    addReactProcess(new ReactProcess("❌") {
      @Override
      protected void onReact(GenericMessageReactionEvent e) {
        if(e.getUser().equals(P1) || e.getUser().equals(P2))
          destroy();
        else
          e.getUser().openPrivateChannel().complete().sendMessage("You can't do that! you're not a player!").queue();
      }
    });

    doReactions();
  }

  private void swapPlayers(User u) {
    if (P1.equals(u)) {
      currentPlayer = P2;
    } else {
      currentPlayer = P1;
    }
  }

  public CellState detectWinner() {
    boolean blankFound = false;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (!board[i][j].equals(CellState.BLANK)) {
          CellState found = board[i][j];
          for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
              if (dx == 0 && dy == 0) {
                continue;
              }
              if (detect(found, i, j, dx, dy, 4))
                return found;
            }
          }
        }
        else
          blankFound = true;
      }
    }
    if(blankFound)
      return null;
    else
      return CellState.BLANK;
  }

  public boolean detect(CellState toCheck, int x, int y, int dx, int dy, int depth) {
    if (!isInRange(x, y)) {
      return false;
    }
    if (board[x][y].equals(toCheck)) {
      if (depth >= 1) {
        return detect(toCheck, x + dx, y + dy, dx, dy, depth - 1);
      }
      return true;
    }
    return false;
  }

  public boolean dropPiece(int position, CellState state) {
    if (state.equals(CellState.BLANK)) {
      return false;
    }
    if (position >= width) {
      return false;
    }

    //go down from the top of the grid
    for (int i = 0; i < height; i++) {
      //if current cell is not blank
      if (!board[position][i].equals(CellState.BLANK)) {
        //if we are at the top of the board, we cannot place
        if (i - 1 < 0) {
          return false;
        }
        else {
          //if we can place the piece, do that and return true
          board[position][i - 1] = state;
          return true;
        }
      }
    }
    //if no pieces are found, piece has hit the bottom
    board[position][height - 1] = state;
    return true;
  }

  public void clearBoard() {
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        board[i][j] = CellState.BLANK;
      }
    }
  }

  public CellState getStateFromPlayer(User u) {
    if (u.equals(P1)) {
      return CellState.PLAYER1;
    }
    if (u.equals(P2)) {
      return CellState.PLAYER2;
    }
    return CellState.BLANK;
  }

  public String getEmoteFromState(CellState state) {
    switch (state) {
      case PLAYER1:
        return P1Emote;
      case PLAYER2:
        return P2Emote;
      case BLANK:
        return BLANKEMOTE;
    }
    return "ERROR";
  }

  public boolean isInRange(int x, int y) {
    return (x >= 0 && y >= 0) && (x < width && y < height);
  }
}

enum CellState {
  PLAYER1,
  PLAYER2,
  BLANK;
}