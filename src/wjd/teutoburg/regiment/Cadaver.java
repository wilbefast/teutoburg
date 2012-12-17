/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wjd.teutoburg.regiment;

import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.M;
import wjd.math.Rect;
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
  private static final float ZOOM_THRESHOLD = 0.2f;
  private static final float DETAIL_THRESHOLD = 0.9f;
  // blood
  private static final float BLOOD_AMOUNT_BASE = 12.0f;
  private static final float BLOOD_AMOUNT_VAR = 0.3f; // percentage of base
  // position
  private static final float POSITION_VAR = 30.0f;
  // body
  private static final V2 BODY_SIZE = new V2(18.0f, 5.0f);
  private static final float BODY_POSITION_VAR = 10.0f;
  // head
  private static final float HEAD_RADIUS = 2.0f;
  // shield
  private static final V2 SHIELD_SIZE = new V2(8.0f, 14.0f);
  private static final float SHIELD_POSITION_VAR = 5.0f;
  // weapon
  private static final float WEAPON_LENGTH = 18.0f;
  
  
  /* ATTRIBUTES */
  private final V2 pos = new V2();
  
  // view
  private float blood_amount;
  private final Faction faction;
  private final Rect body = new Rect(BODY_SIZE);
  private final V2 head_pos = new V2(), 
                  weapon_top = new V2(), 
                  weapon_bottom = new V2();
  private final Rect shield = new Rect(SHIELD_SIZE);
  
  /* METHODS */
  public Cadaver(V2 pos_, Faction faction_)
  {
    // save attributes
    this.faction = faction_;
    
    // randomise position
    pos.reset(pos_);
    pos.add((float)M.signedRand(POSITION_VAR, null), 
            (float)M.signedRand(POSITION_VAR, null));
    
    // randomise body
    body.centrePos(pos);
    
    // body left or right?
    if(Math.random() < 0.5)
      head_pos.xy(body.x + body.w + HEAD_RADIUS*0.5f, pos.y);
    else
      head_pos.xy(body.x - HEAD_RADIUS*0.5f, pos.y);
    
    // turn shield 90 degrees?
    if(Math.random() < 0.5)
      shield.turn90();
    
    // randomise weapon
    double weapon_angle = (Math.random() * Math.PI * 2);
    weapon_top.xy((float)Math.cos(weapon_angle), 
                  (float)Math.sin(weapon_angle)).scale(WEAPON_LENGTH*0.5f);
    weapon_bottom.reset(weapon_top).opp().add(pos);
    weapon_top.add(pos);
    
    // randomise shield
    shield.centrePos(body.getCentre()).shift(
            (float)M.signedRand(SHIELD_POSITION_VAR, null), 
            (float)M.signedRand(SHIELD_POSITION_VAR, null));
    
    // generate man
    blood_amount 
      = (float) (BLOOD_AMOUNT_BASE*(1 + M.signedRand(BLOOD_AMOUNT_VAR, null)));
  }
  
  /* IMPLEMENTS -- IVISIBLE */
  @Override
  public void render(ICanvas canvas)
  {
    if(canvas.getCamera().getZoom() < ZOOM_THRESHOLD)
      return;
        
    if(canvas.getCamera().getZoom() < DETAIL_THRESHOLD)
    {
      //  blood -- imposter
      canvas.setColour(faction.colour_imposter_dead);
      canvas.circle(pos, blood_amount, true);
      return;
    }
    
    // blood  -- detail
    canvas.setColour(Palette.BLOOD);
    canvas.circle(pos, blood_amount, true);
    
    // shadow
    body.y += 2.0f;
      canvas.setColour(Palette.GRASS_SHADOW);
      canvas.box(body, true);
    body.y -= 2.0f;
      
    // weapon
    canvas.setLineWidth(2.0f);
    canvas.setColour(faction.colour_weapon);
    canvas.line(weapon_top, weapon_bottom);
    
    // body
    canvas.setColour(faction.colour_tunic_dead);
    canvas.box(body, true);
    
    // head
    canvas.setColour(faction.colour_face_dead);
    canvas.circle(head_pos, HEAD_RADIUS, true);
    
    // shield
    canvas.setColour(faction.colour_shield_dead);
    canvas.box(shield, true);

  }
  
}
