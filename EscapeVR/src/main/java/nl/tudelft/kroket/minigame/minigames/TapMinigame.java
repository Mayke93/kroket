package nl.tudelft.kroket.minigame.minigames;

import nl.tudelft.kroket.event.events.ButtonPressEvent;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.minigame.Minigame;
import nl.tudelft.kroket.net.protocol.Protocol;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * This class contains the logic of the second minigame (B) of the VR player.
 * 
 * @author Team Kroket
 */
public class TapMinigame extends Minigame {

  /** The name of this minigame. */
  private static final String GAME_NAME = "B";

  /** Current class, used as tag for logger. */
  private final String className = this.getClass().getSimpleName();

  /** Singleton logger instance. */
  private Logger log = Logger.getInstance();

  /** Singleton instance. */
  private static Minigame instance = new TapMinigame();

  /** Private constructor. */
  private TapMinigame() {
  }

  /** The correct sequence of buttons. */
  private static List<String> sequenceList = new ArrayList<String>();

  /** the correct sub sequences of buttons. */
  private static List<String> firstSequence = new ArrayList<String>();
  private static List<String> secondSequence = new ArrayList<String>();
  private static List<String> thirdSequence = new ArrayList<String>();
  private static List<String> fourthSequence = new ArrayList<String>();

  /** The enum used to see how many sequences the client has finished. */
  public static sequenceState seqState = sequenceState.sequenceOne;

  /** The list of buttons pressed. */
  private static List<String> buttonList = new ArrayList<String>();

  /** Get the singleton instance. */
  public static Minigame getInstance() {
    return instance;
  }

  /**
   * The sequenceState is used to check how many sequences the vr client has completed.
   */
  public enum sequenceState {
    sequenceOne, sequenceTwo, sequenceThree, sequenceFour, completed;

    public sequenceState getNext() {
      switch (this) {
        case sequenceOne:
          return sequenceTwo;
        case sequenceTwo:
          return sequenceThree;
        case sequenceThree:
          return sequenceFour;
        case sequenceFour:
          return completed;
        default:
          return this;
      }

    }

    /**
     * Returns the correct sequence according to the current minigamestate.
     * 
     * @return the correct sequence
     */
    public List<String> returnSequence() {
      switch (this) {
        case sequenceOne:
          return firstSequence;
        case sequenceTwo:
          return secondSequence;
        case sequenceThree:
          return thirdSequence;
        case sequenceFour:
          return fourthSequence;
        default:
          return sequenceList;
      }
    }

    /**
     * Matches the enums to actual strings.
     * 
     * @return the string that belongs to the current enum
     */
    public String returnCompleteMessage() {
      switch (this) {
        case sequenceOne:
          return "First sequence complete!";
        case sequenceTwo:
          return "Well done! Two more to go!";
        case sequenceThree:
          return "One more to go!";
        case sequenceFour:
          return "You're done!";
        default:
          return "Good job!";
        }
    }

  }

  /**
   * Start the minigame.
   */
  @Override
  public void start() {
    log.info(className, "Minigame B started.");

    screenManager.getScreen("controller").show();
    hud.setCenterText(
        "Hack the computer by entering the colorsequences\nyou will receive from your fellow CIA agents.\nUse the colored buttons on your controller!",
        20);
    seqState = seqState.sequenceOne;
    buttonList.clear();
    sequenceList.clear();

  }

  /**
   * Stop the minigame.
   */
  @Override
  public void stop() {
    log.info(className, "Minigame B completed.");
    hud.setCenterText("Great job!\nWait... I think I saw something appear on that wall!", 10);
    sceneManager.extendEscapeScene("C");
    screenManager.getScreen("controller").hide();
  }

  /**
   * The update method. Checks whether the sequence added is the correct one. and updates seqState
   * if the sequence was correct.
   */
  @Override
  public void update(float tpf) {
	 
    if (buttonList.equals(seqState.returnSequence())) {
      hud.setCenterText(seqState.returnCompleteMessage(), 2);
      seqState = seqState.getNext();
      buttonList.clear();
      
      if(seqState == sequenceState.completed) {
          clientThread.sendMessage(String.format("%s[%s]", Protocol.COMMAND_DONE, getName()));
      }
    }

  }

  /**
   * Handles the event of clicking a button. Keeps the buttonlist of the same or smaller than 4.
   */
  @Override
  public void handleEvent(EventObject event) {

    if (event instanceof ButtonPressEvent) {

      String buttonName = ((ButtonPressEvent) event).getName();

      buttonList.add(buttonName);

      // Keep the lists the same size, by removing the first element
      if (buttonList.size() > 4) {
        buttonList = buttonList.subList(1, buttonList.size());
      }

    }

  }

  /**
   * Parse the colors received from the server by matching them to the button that should be pressed
   * and add this button to the sequenceList.
   * 
   * @param params
   *          the params from the command that contain the colors.
   */
  public void parseButtons(List<String> params) {
    for (String buttonString : params) {
      switch (buttonString) {
        case "RED":
          sequenceList.add("Button B");
          break;
        case "GREEN":
          sequenceList.add("Button A");
          break;
        case "BLUE":
          sequenceList.add("Button X");
          break;
        case "YELLOW":
          sequenceList.add("Button Y");
          break;
        default:
      }

    }

    firstSequence = sequenceList.subList(0, 4);
    secondSequence = sequenceList.subList(4, 8);
    thirdSequence = sequenceList.subList(8, 12);
    fourthSequence = sequenceList.subList(12, 16);
    
  }

  /**
   * Return the game of the game.
   */
  public String getName() {
    return GAME_NAME;
  }

  /**
   * Getter for the first sequence.
   * 
   * @return the first sequence
   */
  public static List<String> getFirstSequence() {
    return firstSequence;
  }

  /**
   * Getter for the second sequence.
   * 
   * @return the second sequence
   */
  public static List<String> getSecondSequence() {
    return secondSequence;
  }

  /**
   * Getter for the third sequence.
   * 
   * @return the third sequence
   */
  public static List<String> getThirdSequence() {
    return thirdSequence;
  }

  /**
   * Getter for the fourth sequence.
   * 
   * @return the fourth sequence
   */
  public static List<String> getFourthSequence() {
    return fourthSequence;
  }

  /**
   * Gets the buttonlist.
   * @return the button list
   */
  public static List<String> getButtonList() {
    return buttonList;
  }
  
  

}
