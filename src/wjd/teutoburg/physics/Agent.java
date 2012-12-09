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
package wjd.teutoburg.physics;

import java.util.HashMap;
import java.util.Map;
import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IDynamic;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.M;
import wjd.math.Rect;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 4, 2012
 */
public class Agent implements IVisible, IDynamic, IPhysical
{
  /* CONSTANTS */
  private static final double INV_2PI = 1/(2*Math.PI);
  
  /* ATTRIBUTES */
  // model
  protected final V2 direction = new V2(1.0f, 0.0f);
  protected float radius;
  protected final V2 position;
  protected final V2 front_position;
  // view
  protected Rect visibility_box;
  protected boolean visible = true;
  // brain
  protected Map<String, String> belief = new HashMap<String, String>();

  /* METHODS */
  
  // constructors
  public Agent(V2 start_position, float start_radius)
  {
    position = start_position;
      front_position = direction.clone().scale(radius).add(position);
    setRadius(start_radius);
    visibility_box = new Rect(0, 0, 2.5f*radius, 2.5f*radius).centrePos(position);
  }

  // accessors
  
  public float getRadius()
  {
    return radius;
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
    direction.reset(target).sub(position).normalise();
    directionChange();
  }
  
  protected void directionChange()
  {
    front_position.reset(direction).scale(radius).add(position);
  }
  
  //----------------------------------------------------------------------------
  // POSITION
  //----------------------------------------------------------------------------
  public void advance(float distance)
  {
    // move various spatial components
    direction.scale(distance);
      position.add(direction);
      front_position.add(direction);
    direction.scale(1/distance);
    visibility_box.centrePos(position);
    
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
  public final void setRadius(float new_radius)
  {
    radius = new_radius;
    visibility_box = new Rect(0, 0, 2.5f*radius, 2.5f*radius).centrePos(position);
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
    //advance(0.1f*t_delta);
    //turn(0.005f*t_delta);
    
    
    // override if needed
    return EUpdateResult.CONTINUE;
  }
  
  /* IMPLEMENTS -- IPHYSICAL */

  @Override
  public boolean isColliding(IPhysical other)
  {
    if(other instanceof Agent)
    {
      Agent a = (Agent)other;
      return (a.position.distance2(position) < M.sqr(a.radius+radius));
    }
    else
      return false;
  }
 
}
