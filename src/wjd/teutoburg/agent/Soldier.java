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
package wjd.teutoburg.agent;

import wjd.amb.view.Colour;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.Rect;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 4, 2012
 */
public abstract class Soldier implements IVisible
{
  /* CONSTANTS */
  private static final float SHADOW_RADIUS =  6.0f;
  private static final Colour C_SHADOW = Colour.BLACK;
  private static final V2 SHIELD_SIZE = new V2(8.0f, 10.0f);
  private static final V2 SHIELD_OFFSET = new V2(SHIELD_SIZE.x * 0.5f, 
                                                -SHIELD_SIZE.y * 0.5f);
  private static final V2 BODY_SIZE = new V2(6.0f, 12.0f);
  private static final float HEAD_RADIUS = 2.0f;
  private static final float HEAD_OFFSET = -BODY_SIZE.y -(HEAD_RADIUS * 0.5f);
  
  /* ATTRIBUTES */
  private V2 position = new V2(), direction = new V2(), head = new V2();
  private Rect body = new Rect(V2.ORIGIN, BODY_SIZE),
               shield = new Rect(V2.ORIGIN, SHIELD_SIZE);
  private final Colour c_shield, c_body, c_head;
  
  /* METHODS */
  // constructors
  protected Soldier(V2 _position, V2 _direction, Colour _c_shield, Colour _c_body, Colour _c_head)
  {
    c_shield = _c_shield;
    c_body = _c_body;
    c_head = _c_head;
    reposition(_position, _direction);
  }
  
  public final void reposition(V2 _position, V2 _direction)
  {
    // save position and direction
    position.reset(_position);
    direction.reset(_direction);

    // position body parts
    head.xy(position.x, position.y + HEAD_OFFSET);
    body.xy(position.x - (BODY_SIZE.x * 0.5f), position.y - BODY_SIZE.y);
    shield.w = SHIELD_SIZE.x * Math.abs(direction.y);
    shield.centrePos(position).shift(direction.y * SHIELD_OFFSET.x, SHIELD_OFFSET.y);
  }
  
  /* NESTING */
  public static class Roman extends Soldier
  {
    /* CONSTANTS */
    private static final Colour C_BODY = Colour.RED;
    private static final Colour C_HEAD = Colour.YELLOW;
    private static final Colour C_SHIELD = Colour.VIOLET;

    /* METHODS */
    
    // constructor
    public Roman(V2 _position, V2 _direction)
    {
      super(_position, _direction, C_SHIELD, C_BODY, C_HEAD);
    }
  }
  
  /* IMPLEMENTS -- IVISIBLE */
  @Override
  public void render(ICanvas canvas)
  {
    // shadow
    canvas.setColour(Colour.BLACK);
    canvas.circle(position, SHADOW_RADIUS, true);
    
    // up => shield is further than body
    if(direction.y < 0)
    {
      renderShield(canvas);
      renderHead(canvas);
      renderBody(canvas);
    }
    // down => body is further than shield
    else
    {
      renderBody(canvas);
      renderHead(canvas);
      renderShield(canvas);
    }
  }
  
  /* SUBROUTINES */
  public void renderHead(ICanvas canvas)
  {
    canvas.setColour(c_head);
    canvas.circle(head, HEAD_RADIUS, true);
  }
  
  public void renderShield(ICanvas canvas)
  {
    canvas.setColour(c_shield);
    canvas.box(shield, true);
  }
  
  public void renderBody(ICanvas canvas)
  {
    canvas.setColour(c_body);
    canvas.box(body, true);
  }
}
