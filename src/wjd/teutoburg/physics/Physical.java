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

import wjd.math.M;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 6, 2012
 */
public class Physical 
{
  /* ATTRIBUTES */
  protected final V2 position;
  protected float radius = 0;
  
  
  /* METHODS */
  
  // constructors
  public Physical(V2 position_)
  {
    this.position = position_;
  }
  
  // mutators
  public void setRadius(float radius_)
  {
    this.radius = radius_;
  }
  
  // accessors
  public boolean isColliding(Physical p)
  {
    return (p.position.distance2(position) <= M.sqr(p.radius+radius));
  }
  
  public float getRadius()
  {
    return radius;
  }
  
  public V2 getPosition()
  {
    return position;
  }
}
