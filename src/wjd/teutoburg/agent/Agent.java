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

import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IDynamic;
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
public class Agent implements IVisible, IDynamic
{
  /* CONSTANTS */
  private static final double INV_2PI = 1/(2*Math.PI);
  
  /* ATTRIBUTES */
  private V2 direction = new V2(1.0f, 0.0f);
  private V2 left = new V2(0.0f, 1.0f);
  private float radius;
  private Rect bounding_box;
  private V2 position;
  private V2 front_position;

  /* METHODS */
  
  // constructors
  public Agent(V2 start_position, float start_radius)
  {
    position = start_position;
      front_position = direction.clone().scale(radius).add(position);
    radius = start_radius;
    bounding_box = new Rect(0, 0, 2.5f*radius, 2.5f*radius).centrePos(position);
  }
  
  // accessors

  public void writePositionTo(V2 receiver)
  {
    receiver.reset(position);
  }
  
  public void writeLeftTo(V2 receiver)
  {
    receiver.reset(left);
  }
  
  public void writeDirection(V2 receiver)
  {
    receiver.reset(direction);
  }
  
  // mutators
  
  public void turn(float angle)
  {
    direction.addAngle((float)(INV_2PI*angle));
    left.reset(direction).left();
    front_position.reset(direction).scale(radius).add(position);
  }
  
  public void advance(float distance)
  {
    direction.scale(distance);
      position.add(direction);
      front_position.add(direction);
    direction.scale(1/distance);
    bounding_box.centrePos(position);
  }
  
  
  /* IMPLEMENTS -- IVISIBLE */

  @Override
  public void render(ICanvas canvas)
  {
    canvas.setColour(Colour.BLACK);
    canvas.box(bounding_box, false);
    canvas.circle(position, radius, false); // no fill
    canvas.line(position, front_position);
  }

  /* IMPLEMENTS -- IDYNAMIC */
  
  @Override
  public EUpdateResult update(int t_delta)
  {
    //advance(0.1f*t_delta);
    turn(0.001f*t_delta);
    
    
    // override if needed
    return EUpdateResult.CONTINUE;
  }
}
