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
package wjd.teutoburg.collision;

import java.util.HashMap;
import java.util.Map;
import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IDynamic;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.Rect;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 4, 2012
 */
public abstract class Agent extends Collider implements IVisible, IDynamic
{
  /* CONSTANTS */
  private static final double INV_2PI = 1/(2*Math.PI);
  
  /* ATTRIBUTES */
  // model
  protected final V2 direction = new V2(1.0f, 0.0f);
  protected final V2 front_position;
  // view
  protected Rect visibility_box;
  protected boolean visible = true;
  // brain
  protected Map<String, String> belief = new HashMap<String, String>();

  /* METHODS */
  
  // constructors
  public Agent(V2 start_position)
  {
    super(start_position);
    
    front_position = direction.clone().scale(c.radius).add(c.centre);
    visibility_box = new Rect(0, 0, 2.5f*c.radius, 2.5f*c.radius).centrePos(c.centre);
  }

  // mutators
  
  //----------------------------------------------------------------------------
  // DIRECTION
  //----------------------------------------------------------------------------
  public void turn(float degrees)
  {
    direction.addAngle((float)(INV_2PI*degrees));
    directionChange();
  }
  
  public void faceRandom()
  {
    direction.addAngle((float)Math.random()*360);
    directionChange();
  }
  
  public void faceTowards(V2 target)
  {
    direction.reset(target).sub(c.centre).normalise();
    directionChange();
  }
  
  protected void directionChange()
  {
    front_position.reset(direction).scale(c.radius).add(c.centre);
    
    // override if need be
  }
  
  //----------------------------------------------------------------------------
  // POSITION
  //----------------------------------------------------------------------------
  public void advance(float distance)
  {
    // move various spatial components
    direction.scale(distance);
      c.centre.add(direction);
      front_position.add(direction);
    direction.scale(1/distance);
    visibility_box.centrePos(c.centre);
    
    // inform subclasses of the move
    positionChange();
  }
  
  protected void positionChange()
  {
    // override if need be
  }
  
  //----------------------------------------------------------------------------
  // SIZE
  //----------------------------------------------------------------------------
  @Override
  public final void setRadius(float new_radius)
  {
    c.radius = new_radius;
    visibility_box = new Rect(0, 0, 2.5f*c.radius, 2.5f*c.radius).centrePos(c.centre);
  }
  

  
  /* IMPLEMENTS -- IVISIBLE */

  @Override
  public void render(ICanvas canvas)
  {
  }

  /* IMPLEMENTS -- IDYNAMIC */
  
  @Override
  public EUpdateResult update(int t_delta)
  {
    // override if needed
    return EUpdateResult.CONTINUE;
  }
  
  /* IMPLEMENTS -- COLLIDERS */
  
  @Override
  public void boundaryEvent(Rect boundary)
  {
    // do nothing
  }

  @Override
  public void collisionEvent(Collider other)
  {
    if(other.getClass().equals(this.getClass()))
    {
      V2 push = other.getCircle().centre.clone().sub(c.centre).scale(0.03f);
      c.centre.sub(push);
      positionChange();
    }
  }
}
