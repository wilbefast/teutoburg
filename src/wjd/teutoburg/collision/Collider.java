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

import wjd.math.Circle;
import wjd.math.M;
import wjd.math.Rect;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 6, 2012
 */
public abstract class Collider 
{
  /* ATTRIBUTES */
  protected final Circle c = new Circle();
  
  
  /* METHODS */
  
  // constructors
  public Collider(V2 position)
  {
    c.setCentre(position);
  }
  
  // mutators
  public void setRadius(float radius_)
  {
    c.radius = radius_;
  }
  
  // accessors
  public boolean isColliding(Collider other)
  {
    return (c.collides(other.c));
  }
  
  public Circle getCircle()
  {
    return c;
  }
  
  /* INTERFACE */

  public abstract void treatCollision(Collider b, V2 collision_point);

  public abstract void treatBoundaryCross(Rect boundary);
}
