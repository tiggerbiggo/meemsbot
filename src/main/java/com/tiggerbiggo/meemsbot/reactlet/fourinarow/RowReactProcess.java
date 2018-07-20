package com.tiggerbiggo.meemsbot.reactlet.fourinarow;

import com.tiggerbiggo.meemsbot.reactlet.ReactProcess;

public abstract class RowReactProcess extends ReactProcess {
  public final int number;
  public RowReactProcess(String reaction, int _number) {
    super(reaction);
    number = _number;
  }
}
