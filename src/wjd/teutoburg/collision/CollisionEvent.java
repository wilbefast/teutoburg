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

import wjd.math.Rect;

/**
 *
 * @author wdyce
 * @since Dec 14, 2012
 */
public abstract class CollisionEvent 
{
  /* ATTRIBUTES */
  
  /* METHODS */

  // constructors

  // accessors

  // mutators
  
  /* IMPLEMENTATIONS */
  
  public static class BoundaryCollision extends CollisionEvent
  {
    /* FUNCTIONS */
    static boolean tryGenerate(Collider a, Rect boundary)
    {
      if(!a.getCircle().inside(boundary))
      {
        a.putEvent(new BoundaryCollision(boundary));
        return true;
      }
      return false;
    }

    /* ATTRIBUTES */
    
    public Rect boundary; 
    
    /* METHODS */
    
    // constructors
    private BoundaryCollision(Rect boundary_)
    {
      this.boundary = boundary_;
    }
    
    
  }
  
  public static class ObjectCollision extends CollisionEvent
  {
    static boolean tryGenerate(Collider a, Collider b)
    {
      if(!a.equals(b) && a.isColliding(b))
      {
        /*collision_point.inter(a.getCircle().centre, b.getCircle().centre, 0.5f);
        a.collision(b, collision_point);
        b.collision(a, collision_point);*/
        return true;
      }
      else
        return false;
    }
  }
  
}
