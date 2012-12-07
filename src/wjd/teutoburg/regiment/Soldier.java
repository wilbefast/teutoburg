/*
 Copyright (C) 2012 William James Dyce

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wjd.teutoburg.regiment;

import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.Rect;
import wjd.math.V2;
import wjd.teutoburg.simulation.Palette;

/**
 *
 * @author wdyce
 * @since Dec 4, 2012
 */
public abstract class Soldier implements IVisible
{
  /* CONSTANTS */
  // shadow
  private static final float SHADOW_RADIUS =  6.0f;
  // shield
  private static final V2 SHIELD_SIZE = new V2(8.0f, 10.0f);
  private static final V2 SHIELD_OFFSET = new V2(SHIELD_SIZE.x * 0.5f, 
                                                -SHIELD_SIZE.y * 0.5f);
  // weapon
  private static final V2 WEAPON_SIZE = new V2(2.0f, 18.0f);
  private static final V2 WEAPON_OFFSET = new V2(5.0f, 1.0f);
  // body
  private static final V2 BODY_SIZE = new V2(6.0f, 12.0f);
  // head
  private static final float HEAD_RADIUS = 2.0f;
  private static final float HEAD_OFFSET = -BODY_SIZE.y -(HEAD_RADIUS * 0.5f);
  // visibility
  private static final float ZOOM_IMPOSTER_THRESHOLD = 0.6f;
  
  /* ATTRIBUTES */
  private V2 position = new V2(), direction = new V2(), head = new V2(), 
            weapon_bottom = new V2(), weapon_top = new V2();
  private Rect body = new Rect(V2.ORIGIN, BODY_SIZE),
               shield = new Rect(V2.ORIGIN, SHIELD_SIZE);
  private boolean nearby = true, nearby_previous = true;
  private Faction faction;
  
  /* METHODS */
  // constructors
  protected Soldier(V2 _position, V2 _direction, Faction faction)
  {
    this.faction = faction;
    reposition(_position, _direction);
  }
  
  public final void reposition(V2 _position, V2 _direction)
  {
    // always save the position and move the body 
    position.reset(_position);
    body.xy(position.x - (BODY_SIZE.x * 0.5f), position.y - BODY_SIZE.y);
      
    // don't bother with the rest if we're drawing the simplified version
    if(!nearby)
      return;
    
    // reset direction
    direction.reset(_direction);

    // position auxilliary body parts
    head.xy(position.x, position.y + HEAD_OFFSET);
    //shield.w = SHIELD_SIZE.x * Math.abs(direction.y);
    shield.centrePos(position).shift(direction.y * SHIELD_OFFSET.x, 
                                     SHIELD_OFFSET.y);
    
    weapon_bottom.reset(position).add(-direction.y * WEAPON_OFFSET.x, 
                                      direction.y * WEAPON_OFFSET.y);
    weapon_top.xy(weapon_bottom.x, weapon_bottom.y - WEAPON_SIZE.y);
  }
  
  /* IMPLEMENTS -- IVISIBLE */
  @Override
  public void render(ICanvas canvas)
  {
    // always draw the shadow
    canvas.setColour(Palette.GRASS_SHADOW);
    canvas.circle(position, SHADOW_RADIUS, true);
    
    // draw a simplified body if far away
    nearby_previous = nearby;
    nearby = (canvas.getCamera().getZoom() >= ZOOM_IMPOSTER_THRESHOLD);
    if(nearby && nearby_previous)
      renderNearby(canvas);
    else
      renderDistant(canvas);
  }
  
  /* SUBROUTINES */
  
  private void renderNearby(ICanvas canvas)
  { 
    // North
    if(direction.y < 0)
    {
      // North-East
      if(direction.x > 0)
      {
        renderShield(canvas);
        renderHead(canvas);
        renderTunic(canvas);
        renderWeapon(canvas);
      }
      // North-West
      else
      {
        renderWeapon(canvas);
        renderHead(canvas);
        renderTunic(canvas);
        renderShield(canvas);
      }
      
    }
    // South
    else
    {
      // South-East
      if(direction.x > 0)
      {
        renderShield(canvas);
        renderTunic(canvas);
        renderHead(canvas);
        renderWeapon(canvas);
      }
      // South-West
      else
      {
        renderWeapon(canvas);
        renderTunic(canvas);
        renderHead(canvas);
        renderShield(canvas);
      }
    }
  }
  
  private void renderDistant(ICanvas canvas)
  {
    canvas.setColour(faction.colour_imposter);
    canvas.box(body, true);
  }
  
  private void renderHead(ICanvas canvas)
  {
    canvas.setColour(faction.colour_face);
    canvas.circle(head, HEAD_RADIUS, true);
  }
  
  private void renderShield(ICanvas canvas)
  {
    canvas.setColour(faction.colour_shield);
    canvas.box(shield, true);
  }
  
  private void renderTunic(ICanvas canvas)
  {
    canvas.setColour(faction.colour_tunic);
    canvas.box(body, true);
  }

  private void renderWeapon(ICanvas canvas)
  {
    canvas.setLineWidth(WEAPON_SIZE.x);
    canvas.setColour(faction.colour_weapon);
    canvas.line(weapon_bottom, weapon_top);
  }
}
