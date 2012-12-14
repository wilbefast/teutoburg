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

import java.util.LinkedList;
import java.util.List;
import wjd.math.Circle;
import wjd.math.Rect;
import wjd.math.V2;

/**
 *
 * @author wdyce
 * @since Dec 11, 2012
 */
public class ListCollisionManager implements ICollisionManager 
{
  /* ATTRIBUTES */
  private final List<Collider> objects = new LinkedList<Collider>(),
                              query_result = new LinkedList<Collider>();
  private final V2 collision_point = new V2();
  private final Rect boundary, bounding_rect = new Rect();
  
  /* METHODS */

  // constructors
  
  public ListCollisionManager(Rect boundary_)
  {
    this.boundary = boundary_;
  }

  // accessors

  // mutators
  
  /* IMPLEMENTS -- IPHYSICSMANAGER */
  
  @Override
  public Iterable<Collider> getInRect(Rect area)
  {
    query_result.clear();
    for(Collider c : objects)
    {
      if(c.getCircle().collides(area))
        query_result.add(c);
    }
    return query_result;
  }

  @Override
  public Iterable<Collider> getInCircle(Circle circle_query)
  {
    query_result.clear();
    for(Collider c : objects)
      if(circle_query.collides(c.getCircle()))
        query_result.add(c);
    return query_result;
  }

  @Override
  public void register(Collider c)
  {
    objects.add(c);
  }

  @Override
  public void generateCollisions()
  {
    for(Collider a : objects)
    {
      // check collisions with boundary
      CollisionEvent.BoundaryCollision.tryGenerate(a, boundary);
      
      // check collisions between pairs of objects
      for(Collider b : objects)
        CollisionEvent.ObjectCollision.tryGenerate(a, b);
    }
      
  }
}
