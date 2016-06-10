package nl.tudelft.kroket.event.events;

import java.util.EventObject;

/**
 * ButtonPressEvent, event to indicate a button was pressed.
 * 
 * @author Team Kroket
 *
 */
public class ButtonPressEvent extends EventObject {

  /**
   * 
   */
  private static final long serialVersionUID = 1899976237424414092L;
  String name;

  /**
   * Constructor for ButtonPressEvent.
   * 
   * @param source
   *          the source of the event
   * @param name
   *          the name of the button pressed
   */
  public ButtonPressEvent(Object source, String name) {
    super(source);
    this.name = name;
  }

  /**
   * Get the name of the button pressed that triggered this event.
   * 
   * @return the name of the button
   */
  public String getName() {
    return name;
  }
}
