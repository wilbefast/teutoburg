/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wjd.teutoburg.regiment;

import wjd.amb.view.Colour;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.V2;
import wjd.teutoburg.simulation.Palette;

/**
 *
 * @author william
 */
public class Cadaver implements IVisible
{
  /* CONSTANTS */
  // visibility
  private static final float ZOOM_THRESHOLD = 0.6f;
  private static final float BLOOD_AMOUNT_BASE = 10.0f;
  private static final float BLOOD_AMOUNT_VAR = 0.2f; // percentage of base
  
  
  /* ATTRIBUTES */
  private final V2 pos = new V2();
  private float blood_amount;
  private final Faction faction;
  
  /* METHODS */
  public Cadaver(V2 pos_, Faction faction_)
  {
    // save attributes
    this.faction = faction_;
    pos.reset(pos_);
    
    // generate man
  }
  
  /* IMPLEMENTS -- IVISIBLE */
  @Override
  public void render(ICanvas canvas)
  {
    canvas.setColour(Palette.BLOOD);
    canvas.circle(pos, blood_amount, true);
  }
  
}
